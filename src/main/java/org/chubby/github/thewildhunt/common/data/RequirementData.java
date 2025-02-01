package org.chubby.github.thewildhunt.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Represents the requirements needed to complete a quest objective.
 * This class supports serialization and deserialization using NBT.
 */
public class RequirementData implements INBTSerializable<CompoundTag> {
    private final RequirementType type;
    private final Map<String, Object> conditions;
    private final int requiredProgress;
    private final Predicate<Player> customRequirement;

    private RequirementData(Builder builder) {
        this.type = builder.type;
        this.conditions = builder.conditions;
        this.requiredProgress = builder.requiredProgress;
        this.customRequirement = builder.customRequirement;
    }

    public RequirementType getType() {
        return type;
    }

    public Map<String, Object> getConditions() {
        return conditions;
    }

    public int getRequiredProgress() {
        return requiredProgress;
    }

    public Predicate<Player> getCustomRequirement() {
        return customRequirement;
    }

    /**
     * Checks whether the given player meets the requirement.
     *
     * @param player The player to check.
     * @return true if the requirement is met, false otherwise.
     */
    public boolean checkRequirement(Player player) {
        return customRequirement == null || customRequirement.test(player);
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putString("type", type.name());
        tag.putInt("requiredProgress", requiredProgress);

        CompoundTag conditionsTag = new CompoundTag();
        conditions.forEach((key, value) -> {
            if (value instanceof Integer) {
                conditionsTag.putInt(key, (Integer) value);
            } else if (value instanceof String) {
                conditionsTag.putString(key, (String) value);
            }
        });
        tag.put("conditions", conditionsTag);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        if (tag.contains("type", Tag.TAG_STRING)) {
            RequirementType.valueOf(tag.getString("type"));
        }
        if (tag.contains("requiredProgress", Tag.TAG_INT)) {
            tag.getInt("requiredProgress");
        }

        Map<String, Object> conditions = new HashMap<>();
        if (tag.contains("conditions", Tag.TAG_COMPOUND)) {
            CompoundTag conditionsTag = tag.getCompound("conditions");
            for (String key : conditionsTag.getAllKeys()) {
                if (conditionsTag.contains(key, Tag.TAG_INT)) {
                    conditions.put(key, conditionsTag.getInt(key));
                } else if (conditionsTag.contains(key, Tag.TAG_STRING)) {
                    conditions.put(key, conditionsTag.getString(key));
                }
            }
        }
    }

    /**
     * Enum representing different types of requirements.
     */
    public enum RequirementType {
        ITEM_COLLECTION,
        MONSTER_DEFEAT,
        LOCATION_REACH,
        PLAYER_LEVEL,
        CUSTOM
    }

    /**
     * Builder class for creating instances of {@link RequirementData}.
     */
    public static class Builder {
        private RequirementType type;
        private Map<String, Object> conditions = new HashMap<>();
        private int requiredProgress;
        private Predicate<Player> customRequirement;

        public Builder setType(RequirementType type) {
            this.type = type;
            return this;
        }

        public Builder setConditions(Map<String, Object> conditions) {
            this.conditions = conditions;
            return this;
        }

        public Builder setRequiredProgress(int requiredProgress) {
            this.requiredProgress = requiredProgress;
            return this;
        }

        public Builder setCustomRequirement(Predicate<Player> customRequirement) {
            this.customRequirement = customRequirement;
            return this;
        }

        public RequirementData build() {
            return new RequirementData(this);
        }
    }

    public static final Codec<RequirementType> REQUIREMENT_TYPE_CODEC = Codec.STRING.xmap(
            RequirementType::valueOf,
            Enum::name
    );

    @SuppressWarnings("unchecked")
    public static final Codec<Map<String, Object>> CONDITIONS_CODEC = Codec.unboundedMap(
            Codec.STRING,
            Codec.STRING.xmap(
                    str -> (Object)str,
                    obj -> obj.toString()
            )
    );

    public static final Codec<RequirementData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    REQUIREMENT_TYPE_CODEC.fieldOf("type").forGetter(RequirementData::getType),
                    CONDITIONS_CODEC.fieldOf("conditions").forGetter(RequirementData::getConditions),
                    Codec.INT.fieldOf("requiredProgress").forGetter(RequirementData::getRequiredProgress)
            ).apply(instance, (type, conditions, progress) ->
                    new RequirementData.Builder()
                            .setType(type)
                            .setConditions(conditions)
                            .setRequiredProgress(progress)
                            .build()
            )
    );
}
