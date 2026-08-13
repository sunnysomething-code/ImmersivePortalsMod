package qouteall.imm_ptl.core.render;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import qouteall.imm_ptl.core.portal.LoadingIndicatorEntity;

/**
 * Loading indicators intentionally have no world geometry. Minecraft 26.1
 * requires entity renderers to provide an extraction render-state type.
 */
public class LoadingIndicatorRenderer extends EntityRenderer<LoadingIndicatorEntity, EntityRenderState> {
    public LoadingIndicatorRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
}
