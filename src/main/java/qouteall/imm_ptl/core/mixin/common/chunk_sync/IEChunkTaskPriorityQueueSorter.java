package qouteall.imm_ptl.core.mixin.common.chunk_sync;

import net.minecraft.server.level.ThrottlingChunkTaskDispatcher;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Compatibility placeholder for the old ChunkTaskPriorityQueueSorter mixin.
 * Minecraft 26.1 replaced that class with ThrottlingChunkTaskDispatcher.
 * The old mailbox accessor is no longer used by Immersive Portals' custom
 * chunk-ticket throttling, so this mixin intentionally has no accessors.
 */
@Mixin(ThrottlingChunkTaskDispatcher.class)
public interface IEChunkTaskPriorityQueueSorter {
}
