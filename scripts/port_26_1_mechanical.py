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
}

changed = 0
for path in ROOT.rglob("*.java"):
    old = path.read_text(encoding="utf-8")
    new = old
    for before, after in REPLACEMENTS.items():
        new = new.replace(before, after)
    if new != old:
        path.write_text(new, encoding="utf-8")
        changed += 1
        print(path.relative_to(ROOT.parent.parent.parent))

print(f"Updated {changed} Java files")
