package qouteall.imm_ptl.core.chunk_loading;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongPredicate;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkResult;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.Ticket;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.TicketStorage;
import net.minecraft.world.level.chunk.LevelChunk;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import qouteall.dimlib.api.DimensionAPI;
import qouteall.imm_ptl.core.IPGlobal;
import qouteall.imm_ptl.core.ducks.IEChunkMap;
import qouteall.imm_ptl.core.ducks.IEServerChunkCache;
import qouteall.imm_ptl.core.ducks.IEWorld;
import qouteall.imm_ptl.core.platform_specific.IPConfig;
import qouteall.q_misc_util.Helper;
import qouteall.q_misc_util.my_util.RateStat;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

/**
 * Manages Immersive Portals chunk tickets for one dimension.
 *
 * <p>Minecraft 26.1 moved region-ticket ownership out of DistanceManager and
 * into TicketStorage. The mod keeps its existing small throttling queue, but
 * all ticket creation/removal now goes through that storage.</p>
 */
public class ImmPtlChunkTickets {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * A non-expiring loading ticket. In 26.1 TicketType is no longer generic
     * and custom types are represented directly by their timeout/flag record.
     */
    public static final TicketType TICKET_TYPE =
        new TicketType(TicketType.NO_TIMEOUT, TicketType.FLAG_LOADING);

    @SuppressWarnings("FieldMayBeFinal")
    private static boolean enableDebugRateStat = false;
    private static final RateStat debugRateStat = new RateStat("imm_ptl_chunk_ticket");

    public static final WeakHashMap<ServerLevel, ImmPtlChunkTickets> BY_DIMENSION = new WeakHashMap<>();

    public static void init() {
        DimensionAPI.SERVER_PRE_REMOVE_DIMENSION_EVENT.register(
            ImmPtlChunkTickets::onDimensionRemove
        );
        IPGlobal.SERVER_CLEANUP_EVENT.register(ImmPtlChunkTickets::cleanup);
    }

    public static class ChunkTicketInfo {
        public int lastUpdateGeneration;
        public int distanceToSource;

        public ChunkTicketInfo(int lastUpdateGeneration, int distanceToSource) {
            this.lastUpdateGeneration = lastUpdateGeneration;
            this.distanceToSource = distanceToSource;
        }
    }

    private final Long2ObjectOpenHashMap<ChunkTicketInfo> chunkPosToTicketInfo = new Long2ObjectOpenHashMap<>();
    private final ArrayList<LongLinkedOpenHashSet> chunksToAddTicketByDistance = new ArrayList<>();
    private final LongOpenHashSet waitingForLoading = new LongOpenHashSet();
    private boolean isValid = true;

    public final int throttlingLimit = 4;

    private ImmPtlChunkTickets() {
    }

    public static ImmPtlChunkTickets get(ServerLevel world) {
        return BY_DIMENSION.computeIfAbsent(world, k -> new ImmPtlChunkTickets());
    }

    public void markForLoading(long chunkPos, int distanceToSource, int generation) {
        Validate.isTrue(distanceToSource >= 0);

        ChunkTicketInfo info = chunkPosToTicketInfo.get(chunkPos);
        if (info == null) {
            info = new ChunkTicketInfo(generation, distanceToSource);
            chunkPosToTicketInfo.put(chunkPos, info);
            getQueueByDistance(distanceToSource).add(chunkPos);
            return;
        }

        if (generation != info.lastUpdateGeneration) {
            info.lastUpdateGeneration = generation;
            int oldDistanceToSource = info.distanceToSource;
            info.distanceToSource = distanceToSource;
            if (getQueueByDistance(oldDistanceToSource).remove(chunkPos)) {
                getQueueByDistance(distanceToSource).add(chunkPos);
            }
        }
        else if (distanceToSource < info.distanceToSource) {
            int oldDistanceToSource = info.distanceToSource;
            info.distanceToSource = distanceToSource;
            if (getQueueByDistance(oldDistanceToSource).remove(chunkPos)) {
                getQueueByDistance(distanceToSource).add(chunkPos);
            }
        }
    }

    private LongLinkedOpenHashSet getQueueByDistance(int distanceToSource) {
        return Helper.arrayListComputeIfAbsent(
            chunksToAddTicketByDistance,
            distanceToSource,
            LongLinkedOpenHashSet::new
        );
    }

    public void tick(ServerLevel world) {
        flushThrottling(world);
    }

    public void flushThrottling(ServerLevel world) {
        if (Thread.currentThread() != ((IEWorld) world).portal_getThread()) {
            LOGGER.error("Called in a non-server-main (or server-world) thread.", new Throwable());
            return;
        }

        if (enableDebugRateStat) {
            debugRateStat.update();
        }

        if (!isValid) {
            LOGGER.error("flushing when invalid {}", world);
            return;
        }

        if (!world.getServer().isRunning()) {
            return;
        }

        DistanceManager distanceManager = getDistanceManager(world);

        waitingForLoading.removeIf((long chunkPos) -> {
            ChunkHolder chunkHolder = getChunkHolder(world, chunkPos);
            if (chunkHolder == null) {
                return true;
            }

            ChunkResult<LevelChunk> resultNow = chunkHolder.getEntityTickingChunkFuture().getNow(null);
            if (resultNow == null) {
                return false;
            }

            if (!resultNow.isSuccess()) {
                LOGGER.error("Chunk loading failure {} {}", world, new ChunkPos(chunkPos));
            }
            return true;
        });

        for (LongLinkedOpenHashSet queue : chunksToAddTicketByDistance) {
            if (queue == null) {
                continue;
            }

            while (!queue.isEmpty()) {
                if (waitingForLoading.size() >= throttlingLimit) {
                    return;
                }

                long chunkPos = queue.removeFirstLong();
                if (chunkPosToTicketInfo.containsKey(chunkPos)) {
                    addTicket(distanceManager, chunkPos);
                    waitingForLoading.add(chunkPos);
                }
                else {
                    LOGGER.warn("Chunk {} is not in the queue", new ChunkPos(chunkPos));
                }
            }
        }
    }

    private static TicketStorage getTicketStorage(DistanceManager distanceManager) {
        return ((qouteall.imm_ptl.core.mixin.common.chunk_sync.IEDistanceManager) distanceManager)
            .ip_getTicketStorage();
    }

    private static void addTicket(DistanceManager distanceManager, long chunkPos) {
        if (!IPConfig.getConfig().enableImmPtlChunkLoading) {
            return;
        }

        getTicketStorage(distanceManager).addTicketWithRadius(
            TICKET_TYPE,
            new ChunkPos(chunkPos),
            getLoadingRadius()
        );

        if (enableDebugRateStat) {
            debugRateStat.hit();
        }
    }

    private static void removeImmPtlTicketsAt(DistanceManager distanceManager, long chunkPos) {
        TicketStorage ticketStorage = getTicketStorage(distanceManager);
        List<Ticket> toRemove = ticketStorage.getTickets(chunkPos).stream()
            .filter(ticket -> ticket.getType() == TICKET_TYPE)
            .toList();

        if (toRemove.isEmpty()) {
            return;
        }

        ChunkPos chunkPosObj = new ChunkPos(chunkPos);
        for (Ticket ticket : toRemove) {
            ticketStorage.removeTicket(ticket, chunkPosObj);
        }
    }

    public void purge(ServerLevel world, LongPredicate shouldKeepLoadingFunc) {
        DistanceManager distanceManager = getDistanceManager(world);

        chunkPosToTicketInfo.long2ObjectEntrySet().removeIf(e -> {
            long chunkPos = e.getLongKey();
            ChunkTicketInfo ticketInfo = e.getValue();
            boolean keepLoading = shouldKeepLoadingFunc.test(chunkPos);

            if (keepLoading) {
                return false;
            }

            waitingForLoading.remove(chunkPos);
            boolean pendingTicketAdding = getQueueByDistance(ticketInfo.distanceToSource).remove(chunkPos);
            if (!pendingTicketAdding) {
                removeImmPtlTicketsAt(distanceManager, chunkPos);
            }
            return true;
        });
    }

    public int getLoadedChunkNum() {
        return chunkPosToTicketInfo.size();
    }

    public static void onDimensionRemove(ServerLevel world) {
        ImmPtlChunkTickets dimTicketManager = BY_DIMENSION.remove(world);
        if (dimTicketManager == null) {
            return;
        }

        removeAllTicketsInWorld(world, dimTicketManager);
    }

    private static void removeAllTicketsInWorld(ServerLevel world, ImmPtlChunkTickets dimTicketManager) {
        DistanceManager distanceManager = getDistanceManager(world);
        dimTicketManager.chunkPosToTicketInfo.keySet().forEach(
            (long pos) -> removeImmPtlTicketsAt(distanceManager, pos)
        );
        dimTicketManager.isValid = false;
    }

    public static int getLoadingRadius() {
        return IPGlobal.activeLoading ? 2 : 1;
    }

    public static ChunkHolder getChunkHolder(ServerLevel world, long chunkPos) {
        return ((IEChunkMap) world.getChunkSource().chunkMap).ip_getChunkHolder(chunkPos);
    }

    public static DistanceManager getDistanceManager(ServerLevel world) {
        return ((IEServerChunkCache) world.getChunkSource()).ip_getDistanceManager();
    }

    private static void cleanup(MinecraftServer server) {
        for (ImmPtlChunkTickets immPtlChunkTickets : BY_DIMENSION.values()) {
            immPtlChunkTickets.isValid = false;
        }
        BY_DIMENSION.clear();
    }
}
