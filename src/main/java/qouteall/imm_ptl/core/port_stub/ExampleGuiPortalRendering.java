package qouteall.imm_ptl.core.api.example;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

/**
 * Temporary 26.1.2 core-port placeholder for the optional GUI-portal example.
 * The old example depends on the pre-26.1 GUI/framebuffer renderer and is
 * intentionally disabled until the core portal renderer is fully migrated.
 */
public final class ExampleGuiPortalRendering {
    private ExampleGuiPortalRendering() {
    }

    public static void onCommandExecuted(ServerPlayer player, ServerLevel world, Vec3 pos) {
        // Optional debug/example feature; no core portal behavior depends on it.
    }
}
