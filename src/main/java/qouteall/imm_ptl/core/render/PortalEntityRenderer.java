package qouteall.imm_ptl.core.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import qouteall.imm_ptl.core.IPCGlobal;
import qouteall.imm_ptl.core.portal.Portal;

@Environment(EnvType.CLIENT)
public class PortalEntityRenderer extends EntityRenderer<Portal, PortalEntityRenderer.PortalRenderState> {
    public static class PortalRenderState extends EntityRenderState {
        public Portal portal;
    }

    public PortalEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public PortalRenderState createRenderState() {
        return new PortalRenderState();
    }

    @Override
    public void extractRenderState(Portal portal, PortalRenderState state, float partialTick) {
        super.extractRenderState(portal, state, partialTick);
        state.portal = portal;
    }

    @Override
    public void submit(
        PortalRenderState state,
        PoseStack poseStack,
        SubmitNodeCollector submitNodeCollector,
        CameraRenderState cameraRenderState
    ) {
        if (state.portal != null) {
            IPCGlobal.renderer.renderPortalInEntityRenderer(state.portal);
        }
        super.submit(state, poseStack, submitNodeCollector, cameraRenderState);
    }
}
