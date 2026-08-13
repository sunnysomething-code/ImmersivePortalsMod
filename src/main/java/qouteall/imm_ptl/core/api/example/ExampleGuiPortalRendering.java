package qouteall.imm_ptl.core.api.example;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

/**
 * The old GUI-portal demonstration uses Minecraft's pre-26.1 immediate-mode
 * GUI/render-target APIs. Keep the debug command source-compatible while the
 * example is rewritten for the 26.1 extraction renderer.
 */
public final class ExampleGuiPortalRendering {
    private ExampleGuiPortalRendering() {
    }

    public static void onCommandExecuted(ServerPlayer player, ServerLevel world, Vec3 pos) {
        player.sendSystemMessage(
            net.minecraft.network.chat.Component.literal(
                "GUI portal rendering is temporarily unavailable in the 26.1.2 port"
            )
        );
    }
}
