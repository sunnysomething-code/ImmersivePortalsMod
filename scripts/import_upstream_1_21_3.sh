#!/usr/bin/env bash
set -euo pipefail

git remote remove upstream 2>/dev/null || true
git remote add upstream https://github.com/iPortalTeam/ImmersivePortalsMod.git
git fetch --depth=1 upstream 1.21.3

# Import the source/resource changes made by the original author for Minecraft 1.21.3.
# These contain a major renderer/shader migration that is a better base for the 26.1 port.
git checkout FETCH_HEAD -- \
  src/main/java/qouteall/imm_ptl/core/IPMcHelper.java \
  src/main/java/qouteall/imm_ptl/core/chunk_loading/ImmPtlChunkTickets.java \
  src/main/java/qouteall/imm_ptl/core/compat/iris_compatibility/ExperimentalIrisPortalRenderer.java \
  src/main/java/qouteall/imm_ptl/core/compat/iris_compatibility/IrisCompatibilityPortalRenderer.java \
  src/main/java/qouteall/imm_ptl/core/compat/iris_compatibility/IrisPortalRenderer.java \
  src/main/java/qouteall/imm_ptl/core/compat/mixin/iris/MixinIrisTransformPatcher.java \
  src/main/java/qouteall/imm_ptl/core/compat/mixin/sodium/MixinSodiumShaderLoader.java \
  src/main/java/qouteall/imm_ptl/core/ducks/IEShader.java \
  src/main/java/qouteall/imm_ptl/core/ducks/IEWorldRenderer.java \
  src/main/java/qouteall/imm_ptl/core/mixin/client/accessor/CoreShadersAccessor.java \
  src/main/java/qouteall/imm_ptl/core/mixin/client/render/MixinGameRenderer.java \
  src/main/java/qouteall/imm_ptl/core/mixin/client/render/MixinLevelRenderer.java \
  src/main/java/qouteall/imm_ptl/core/mixin/client/render/MixinMinecraft_Render.java \
  src/main/java/qouteall/imm_ptl/core/mixin/client/render/MixinRenderSystem_Clipping.java \
  src/main/java/qouteall/imm_ptl/core/mixin/client/render/MixinShaderInstanceForIris.java \
  src/main/java/qouteall/imm_ptl/core/mixin/client/render/framebuffer/MixinRenderTarget.java \
  src/main/java/qouteall/imm_ptl/core/mixin/client/render/isometric/MixinGameRenderer_Isometric.java \
  src/main/java/qouteall/imm_ptl/core/mixin/client/render/optimization/MixinLevelRenderer_Clouds.java \
  src/main/java/qouteall/imm_ptl/core/mixin/client/render/shader/MixinCompiledShader.java \
  src/main/java/qouteall/imm_ptl/core/mixin/client/render/shader/MixinGameRenderer_Shaders.java \
  src/main/java/qouteall/imm_ptl/core/mixin/client/render/shader/MixinShaderInstance.java \
  src/main/java/qouteall/imm_ptl/core/portal/PortalPlaceholderBlock.java \
  src/main/java/qouteall/imm_ptl/core/render/FrontClipping.java \
  src/main/java/qouteall/imm_ptl/core/render/MyRenderHelper.java \
  src/main/java/qouteall/imm_ptl/core/render/ShaderCodeTransformation.java \
  src/main/java/qouteall/imm_ptl/core/render/renderer/PortalRenderer.java \
  src/main/java/qouteall/imm_ptl/core/render/renderer/RendererUsingFrameBuffer.java \
  src/main/java/qouteall/imm_ptl/core/render/renderer/RendererUsingStencil.java \
  src/main/resources/imm_ptl.accesswidener \
  src/main/resources/imm_ptl.mixins.json

# Files intentionally removed by upstream 1.21.3.
git rm -f --ignore-unmatch \
  src/main/java/qouteall/imm_ptl/core/mixin/client/render/shader/MixinProgram.java \
  src/main/resources/assets/immersive_portals/shaders/core/clear_iris_gbuffer.fsh \
  src/main/resources/assets/immersive_portals/shaders/core/clear_iris_gbuffer.json \
  src/main/resources/assets/immersive_portals/shaders/core/clear_iris_gbuffer.vsh

python3 scripts/port_26_1_mechanical.py
