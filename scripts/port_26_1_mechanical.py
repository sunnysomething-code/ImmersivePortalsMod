#!/usr/bin/env python3
import re
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
    "qouteall/imm_ptl/core/portal/Portal.java": [
        ("import net.minecraft.core.Direction;\n", "import net.minecraft.core.Direction;\nimport net.minecraft.core.registries.Registries;\n"),
        ("import net.minecraft.world.level.Level;\n", "import net.minecraft.world.level.Level;\nimport net.minecraft.world.level.storage.ValueInput;\nimport net.minecraft.world.level.storage.ValueOutput;\n"),
        ("public static final EntityType<Portal> ENTITY_TYPE = createPortalEntityType(Portal::new);",
         "public static final EntityType<Portal> ENTITY_TYPE = createPortalEntityType(\"portal\", Portal::new);"),
        ("public static <T extends Portal> EntityType<T> createPortalEntityType(\n        EntityType.EntityFactory<T> constructor\n    ) {",
         "public static <T extends Portal> EntityType<T> createPortalEntityType(\n        String id, EntityType.EntityFactory<T> constructor\n    ) {"),
        ("            .forceTrackedVelocityUpdates(true)\n            .build();",
         "            .forceTrackedVelocityUpdates(true)\n            .build(ResourceKey.create(\n                Registries.ENTITY_TYPE,\n                McHelper.newIdentifier(\"immersive_portals\", id)\n            ));"),
        ("protected void readAdditionalSaveData(CompoundTag compoundTag) {",
         "protected void readAdditionalSaveData(ValueInput valueInput) {\n        CompoundTag compoundTag = PortalDataCompat.read(valueInput);"),
        ("protected void addAdditionalSaveData(CompoundTag compoundTag) {",
         "protected void addAdditionalSaveData(ValueOutput valueOutput) {\n        CompoundTag compoundTag = new CompoundTag();"),
        ("        WRITE_PORTAL_DATA_SIGNAL.invoker().accept(this, compoundTag);\n        \n    }",
         "        WRITE_PORTAL_DATA_SIGNAL.invoker().accept(this, compoundTag);\n        PortalDataCompat.write(valueOutput, compoundTag);\n        \n    }"),
    ],
    "qouteall/imm_ptl/core/portal/nether_portal/NetherPortalEntity.java": [
        ("createPortalEntityType(NetherPortalEntity::new)", "createPortalEntityType(\"nether_portal_new\", NetherPortalEntity::new)"),
    ],
    "qouteall/imm_ptl/core/portal/EndPortalEntity.java": [
        ("createPortalEntityType(EndPortalEntity::new)", "createPortalEntityType(\"end_portal\", EndPortalEntity::new)"),
    ],
    "qouteall/imm_ptl/core/portal/Mirror.java": [
        ("createPortalEntityType(Mirror::new)", "createPortalEntityType(\"mirror\", Mirror::new)"),
    ],
    "qouteall/imm_ptl/core/portal/BreakableMirror.java": [
        ("createPortalEntityType(BreakableMirror::new)", "createPortalEntityType(\"breakable_mirror\", BreakableMirror::new)"),
    ],
    "qouteall/imm_ptl/core/portal/global_portals/GlobalTrackedPortal.java": [
        ("createPortalEntityType(GlobalTrackedPortal::new)", "createPortalEntityType(\"global_tracked_portal\", GlobalTrackedPortal::new)"),
    ],
    "qouteall/imm_ptl/core/portal/global_portals/WorldWrappingPortal.java": [
        ("createPortalEntityType(WorldWrappingPortal::new)", "createPortalEntityType(\"border_portal\", WorldWrappingPortal::new)"),
    ],
    "qouteall/imm_ptl/core/portal/global_portals/VerticalConnectingPortal.java": [
        ("createPortalEntityType(VerticalConnectingPortal::new)", "createPortalEntityType(\"end_floor_portal\", VerticalConnectingPortal::new)"),
    ],
    "qouteall/imm_ptl/core/portal/nether_portal/GeneralBreakablePortal.java": [
        ("createPortalEntityType(GeneralBreakablePortal::new)", "createPortalEntityType(\"general_breakable_portal\", GeneralBreakablePortal::new)"),
    ],
    "qouteall/imm_ptl/core/portal/LoadingIndicatorEntity.java": [
        ("import net.minecraft.core.BlockPos;\n", "import net.minecraft.core.BlockPos;\nimport net.minecraft.core.registries.Registries;\nimport net.minecraft.resources.ResourceKey;\n"),
        ("        ).fireImmune().trackable(96, 20).build();",
         "        ).fireImmune().trackable(96, 20).build(\n            ResourceKey.create(Registries.ENTITY_TYPE, qouteall.imm_ptl.core.McHelper.newIdentifier(\"immersive_portals\", \"loading_indicator\"))\n        );"),
    ],
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
    "qouteall/imm_ptl/core/IPMcHelper.java": [
        ("import com.mojang.blaze3d.platform.GlUtil;", "import com.mojang.blaze3d.systems.RenderSystem;"),
        ("GlUtil.getVendor()", "RenderSystem.getBackendDescription()"),
    ],
}

# 26.1's NBT convenience getters became Optional-returning. Existing 1.21
# call sites expect the old defaulting behavior, so migrate literal-key calls
# to the explicit *Or methods. Restricting this to literal keys avoids changing
# new code that intentionally consumes Optional values.
def migrate_nbt_getters(text: str) -> str:
    text = re.sub(r'\.getDouble\("([^"]+)"\)', r'.getDoubleOr("\1", 0.0)', text)
    text = re.sub(r'\.getFloat\("([^"]+)"\)', r'.getFloatOr("\1", 0.0F)', text)
    text = re.sub(r'\.getLong\("([^"]+)"\)', r'.getLongOr("\1", 0L)', text)
    text = re.sub(r'\.getInt\("([^"]+)"\)', r'.getIntOr("\1", 0)', text)
    text = re.sub(r'\.getBoolean\("([^"]+)"\)', r'.getBooleanOr("\1", false)', text)
    text = re.sub(r'\.getString\("([^"]+)"\)', r'.getStringOr("\1", "")', text)
    text = re.sub(r'\.getCompound\("([^"]+)"\)', r'.getCompoundOrEmpty("\1")', text)
    text = re.sub(r'\.getList\("([^"]+)",\s*\d+\)', r'.getListOrEmpty("\1")', text)
    text = text.replace('.getAsString()', '.asString().orElse("")')
    return text

changed = 0
for path in ROOT.rglob("*.java"):
    old = path.read_text(encoding="utf-8")
    new = old
    for before, after in REPLACEMENTS.items():
        new = new.replace(before, after)

    rel = path.relative_to(ROOT).as_posix()
    for before, after in PATH_REPLACEMENTS.get(rel, []):
        new = new.replace(before, after)

    new = migrate_nbt_getters(new)

    if new != old:
        path.write_text(new, encoding="utf-8")
        changed += 1
        print(path.relative_to(ROOT.parent.parent.parent))

print(f"Updated {changed} Java files")
