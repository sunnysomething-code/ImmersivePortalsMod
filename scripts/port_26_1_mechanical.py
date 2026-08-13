#!/usr/bin/env python3
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1] / "src" / "main" / "java"

REPLACEMENTS = {
    "net.minecraft.resources.ResourceLocation": "net.minecraft.resources.Identifier",
    "ResourceLocation": "Identifier",
    "import net.minecraft.Util;": "import net.minecraft.util.Util;",
    "net.minecraft.world.entity.vehicle.AbstractMinecart": "net.minecraft.world.entity.vehicle.minecart.AbstractMinecart",
    "net.minecraft.client.renderer.FogRenderer": "net.minecraft.client.renderer.fog.FogRenderer",
    "net.minecraft.client.renderer.RenderType": "net.minecraft.client.renderer.rendertype.RenderType",
    "net.minecraft.world.entity.projectile.AbstractArrow": "net.minecraft.world.entity.projectile.arrow.AbstractArrow",
    "net.minecraft.world.entity.projectile.Arrow": "net.minecraft.world.entity.projectile.arrow.Arrow",
    "net.minecraft.world.entity.projectile.ThrownEnderpearl": "net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl",
    "net.minecraft.world.level.dimension.end.EndDragonFight": "net.minecraft.world.level.dimension.end.EnderDragonFight",
    "com.mojang.blaze3d.platform.GlDebug": "com.mojang.blaze3d.opengl.GlDebug",
    "com.mojang.blaze3d.platform.GlStateManager": "com.mojang.blaze3d.opengl.GlStateManager",
    "com.mojang.blaze3d.shaders.Uniform": "com.mojang.blaze3d.opengl.Uniform",
    "net.minecraft.world.level.storage.DimensionDataStorage": "net.minecraft.world.level.storage.SavedDataStorage",
    "DimensionDataStorage": "SavedDataStorage",
    "net.minecraft.world.entity.RelativeMovement": "net.minecraft.world.entity.Relative",
    "RelativeMovement": "Relative",
    "net.minecraft.world.level.portal.DimensionTransition": "net.minecraft.world.level.portal.TeleportTransition",
    "DimensionTransition": "TeleportTransition",
    "net.minecraft.util.random.WeightedRandomList": "net.minecraft.util.random.WeightedList",
    "WeightedRandomList": "WeightedList",
    "net.fabricmc.fabric.api.client.command.v2.ClientCommandManager": "net.fabricmc.fabric.api.client.command.v2.ClientCommands",
    "ClientCommandManager": "ClientCommands",
    "net.minecraft.world.InteractionResultHolder": "net.minecraft.world.InteractionResult",
    "InteractionResultHolder<ItemStack>": "InteractionResult",
}

PATH_REPLACEMENTS = {
    "qouteall/imm_ptl/core/render/renderer/PortalRenderer.java": [
        ("import net.minecraft.client.GraphicsStatus;\n", ""),
        ("import qouteall.imm_ptl.core.compat.iris_compatibility.ExperimentalIrisPortalRenderer;\n", ""),
        ("import qouteall.imm_ptl.core.compat.iris_compatibility.IrisCompatibilityPortalRenderer;\n", ""),
        ("import qouteall.imm_ptl.core.compat.iris_compatibility.IrisPortalRenderer;\n", ""),
        ('''        if (Minecraft.getInstance().options.graphicsMode().get() == GraphicsStatus.FABULOUS) {
            if (!fabulousWarned) {
                fabulousWarned = true;
                CHelper.printChat(Component.translatable("imm_ptl.fabulous_warning"));
            }
        }
        
''', ""),
        ('''        if (IrisInterface.invoker.isIrisPresent()) {
            if (IrisInterface.invoker.isShaders()) {
                if (IPCGlobal.experimentalIrisPortalRenderer) {
                    switchRenderer(ExperimentalIrisPortalRenderer.instance);
                    return;
                }
                
                switch (IPGlobal.renderMode) {
                    case normal -> switchRenderer(IrisPortalRenderer.instance);
                    case compatibility -> switchRenderer(IrisCompatibilityPortalRenderer.instance);
                    case debug -> switchRenderer(IrisCompatibilityPortalRenderer.debugModeInstance);
                    case none -> switchRenderer(IPCGlobal.rendererDummy);
                }
                return;
            }
        }
        
''', ""),
    ],
    "qouteall/imm_ptl/core/platform_specific/IPModEntryClient.java": [
        ("import qouteall.imm_ptl.core.compat.iris_compatibility.ExperimentalIrisPortalRenderer;\n", ""),
        ("            SodiumInterface.invoker = new SodiumInterface.OnSodiumPresent();\n", "            Helper.log(\"Sodium integration is temporarily disabled on the 26.1.2 port\");\n"),
        ('''        if (FabricLoader.getInstance().isModLoaded("iris")) {
            Helper.log("Iris is present");
            IrisInterface.invoker = new IrisInterface.OnIrisPresent();
            ExperimentalIrisPortalRenderer.init();
            
            IPGlobal.CLIENT_TASK_LIST.addTask(MyTaskList.oneShotTask(() -> {
                if (IPConfig.getConfig().shouldDisplayWarning("iris")) {
                    CHelper.printChat(
                        Component.translatable("imm_ptl.iris_warning")
                            .append(IPMcHelper.getDisableWarningText("iris"))
                    );
                }
            }));
        }
        else {
            Helper.log("Iris is not present");
        }
''', '''        if (FabricLoader.getInstance().isModLoaded("iris")) {
            Helper.log("Iris integration is temporarily disabled on the 26.1.2 port");
        }
        else {
            Helper.log("Iris is not present");
        }
'''),
    ],
}

changed = 0
for path in ROOT.rglob("*.java"):
    old = path.read_text(encoding="utf-8")
    new = old
    for before, after in REPLACEMENTS.items():
        new = new.replace(before, after)

    rel = path.relative_to(ROOT).as_posix()
    for before, after in PATH_REPLACEMENTS.get(rel, []):
        new = new.replace(before, after)

    if new != old:
        path.write_text(new, encoding="utf-8")
        changed += 1
        print(path.relative_to(ROOT.parent.parent.parent))

print(f"Updated {changed} Java files")
