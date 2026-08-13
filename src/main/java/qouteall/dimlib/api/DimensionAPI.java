package qouteall.dimlib.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.Set;

/**
 * Minimal 26.1.x compatibility surface for the parts of DimLib that the
 * Immersive Portals core still consumes directly.
 *
 * <p>The upstream DimLib project has not been ported beyond the 1.21.x era.
 * Keeping these lifecycle events local lets the portal engine be ported first
 * without pretending that dynamic-dimension creation/removal is implemented.
 * The full DimLib integration is restored with the peripheral module.</p>
 */
public final class DimensionAPI {
    private DimensionAPI() {
    }

    @FunctionalInterface
    public interface PreRemoveDimensionCallback {
        void accept(ServerLevel world);
    }

    @FunctionalInterface
    public interface ServerDynamicUpdateListener {
        void run(MinecraftServer server, Set<ResourceKey<Level>> dimensions);
    }

    @FunctionalInterface
    public interface ClientDynamicUpdateListener {
        void run(Set<ResourceKey<Level>> dimensions);
    }

    public static final Event<PreRemoveDimensionCallback> SERVER_PRE_REMOVE_DIMENSION_EVENT =
        EventFactory.createArrayBacked(
            PreRemoveDimensionCallback.class,
            listeners -> world -> {
                for (PreRemoveDimensionCallback listener : listeners) {
                    listener.accept(world);
                }
            }
        );

    public static final Event<ServerDynamicUpdateListener> SERVER_DIMENSION_DYNAMIC_UPDATE_EVENT =
        EventFactory.createArrayBacked(
            ServerDynamicUpdateListener.class,
            listeners -> (server, dimensions) -> {
                for (ServerDynamicUpdateListener listener : listeners) {
                    listener.run(server, dimensions);
                }
            }
        );

    @Environment(EnvType.CLIENT)
    public static final Event<ClientDynamicUpdateListener> CLIENT_DIMENSION_UPDATE_EVENT =
        EventFactory.createArrayBacked(
            ClientDynamicUpdateListener.class,
            listeners -> dimensions -> {
                for (ClientDynamicUpdateListener listener : listeners) {
                    listener.run(dimensions);
                }
            }
        );
}
