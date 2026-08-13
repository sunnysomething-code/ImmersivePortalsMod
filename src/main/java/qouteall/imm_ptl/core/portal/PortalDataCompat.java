package qouteall.imm_ptl.core.portal;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Bridges Immersive Portals' existing CompoundTag serialization to the
 * ValueInput/ValueOutput entity persistence API introduced in Minecraft 26.1.
 */
final class PortalDataCompat {
    private static final String DATA_KEY = "imm_ptl_portal_data";

    private PortalDataCompat() {
    }

    static CompoundTag read(ValueInput input) {
        var current = input.read(DATA_KEY, CompoundTag.CODEC);
        if (current.isPresent()) {
            return current.get();
        }

        // Compatibility with worlds saved by pre-26.1 Immersive Portals,
        // where portal fields lived directly in the entity's root tag.
        CompoundTag tag = new CompoundTag();

        copyDoubles(input, tag,
            "width", "height", "thickness",
            "axisWX", "axisWY", "axisWZ",
            "axisHX", "axisHY", "axisHZ",
            "destinationX", "destinationY", "destinationZ",
            "rotationA", "rotationB", "rotationC", "rotationD",
            "scale"
        );
        copyLongs(input, tag,
            "specificPlayerMost", "specificPlayerLeast",
            "pauseTime", "timeOffset"
        );
        copyBooleans(input, tag,
            "shapeNormalized", "teleportable", "interactable",
            "teleportChangesScale", "teleportChangesGravity",
            "fuseView", "renderingMergable", "hasCrossPortalCollision",
            "doRenderPlayer", "isVisible"
        );
        copyStrings(input, tag, "dimensionTo", "portalTag");

        copyCompound(input, tag, "portalShape");
        copyCompound(input, tag, "animation");
        copyCompound(input, tag, "defaultAnimation");
        copyCompound(input, tag, "thisSideReferenceState");
        copyCompound(input, tag, "otherSideReferenceState");
        copyCompound(input, tag, "pausedThisSideState");
        copyCompound(input, tag, "pausedOtherSideState");

        copyCompoundList(input, tag, "thisSideAnimations");
        copyCompoundList(input, tag, "otherSideAnimations");
        copyStringList(input, tag, "commandsOnTeleported");
        copyDoubleList(input, tag, "specialShape");

        return tag;
    }

    static void write(ValueOutput output, CompoundTag tag) {
        output.store(DATA_KEY, CompoundTag.CODEC, tag);
    }

    private static void copyDoubles(ValueInput input, CompoundTag tag, String... keys) {
        for (String key : keys) {
            input.read(key, Codec.DOUBLE).ifPresent(value -> tag.putDouble(key, value));
        }
    }

    private static void copyLongs(ValueInput input, CompoundTag tag, String... keys) {
        for (String key : keys) {
            input.read(key, Codec.LONG).ifPresent(value -> tag.putLong(key, value));
        }
    }

    private static void copyBooleans(ValueInput input, CompoundTag tag, String... keys) {
        for (String key : keys) {
            input.read(key, Codec.BOOL).ifPresent(value -> tag.putBoolean(key, value));
        }
    }

    private static void copyStrings(ValueInput input, CompoundTag tag, String... keys) {
        for (String key : keys) {
            input.read(key, Codec.STRING).ifPresent(value -> tag.putString(key, value));
        }
    }

    private static void copyCompound(ValueInput input, CompoundTag tag, String key) {
        input.read(key, CompoundTag.CODEC).ifPresent(value -> tag.put(key, value));
    }

    private static void copyCompoundList(ValueInput input, CompoundTag tag, String key) {
        var values = input.listOrEmpty(key, CompoundTag.CODEC);
        if (values.isEmpty()) {
            return;
        }
        ListTag list = new ListTag();
        values.forEach(value -> list.add(value));
        tag.put(key, list);
    }

    private static void copyStringList(ValueInput input, CompoundTag tag, String key) {
        var values = input.listOrEmpty(key, Codec.STRING);
        if (values.isEmpty()) {
            return;
        }
        ListTag list = new ListTag();
        values.forEach(value -> list.add(StringTag.valueOf(value)));
        tag.put(key, list);
    }

    private static void copyDoubleList(ValueInput input, CompoundTag tag, String key) {
        var values = input.listOrEmpty(key, Codec.DOUBLE);
        if (values.isEmpty()) {
            return;
        }
        ListTag list = new ListTag();
        values.forEach(value -> list.add(DoubleTag.valueOf(value)));
        tag.put(key, list);
    }
}
