package qouteall.imm_ptl.core.mixin.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import qouteall.imm_ptl.core.ducks.IEParticleManager;

/**
 * 26.1 moved particle rendering into extract/render-state processing and
 * removed ParticleEngine.render/tickParticle. Keep the multi-world level
 * switch hook here; portal-specific particle culling will be reintroduced
 * against the new extraction pipeline after core rendering is compiling.
 */
@Mixin(ParticleEngine.class)
public class MixinParticleEngine implements IEParticleManager {
    @Shadow
    protected ClientLevel level;

    @Override
    public void ip_setWorld(ClientLevel world) {
        level = world;
    }
}
