package qouteall.imm_ptl.core.compat.sodium_compatibility;

import org.jetbrains.annotations.Nullable;
import qouteall.imm_ptl.core.render.FrustumCuller;

/**
 * Temporary no-Sodium bridge for the Minecraft 26.1.2 core port.
 * The real Sodium integration is intentionally excluded until its 26.1 API is ported.
 */
public class SodiumInterface {
    @Nullable
    public static FrustumCuller frustumCuller = null;

    public static class Invoker {
        public boolean isSodiumPresent() {
            return false;
        }

        public Object createNewContext(int renderDistance) {
            return null;
        }

        public void switchContextWithCurrentWorldRenderer(Object context) {
        }

        public void markSpriteActive(Object sprite) {
        }

        public void onClientChunkLoaded(Object world, int chunkX, int chunkZ) {
        }

        public void onClientChunkUnloaded(Object world, int chunkX, int chunkZ) {
        }
    }

    public static Invoker invoker = new Invoker();
}
