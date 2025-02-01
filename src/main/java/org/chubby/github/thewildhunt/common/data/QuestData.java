package org.chubby.github.thewildhunt.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

import java.nio.file.Path;

public class QuestData implements INBTSerializable<CompoundTag> {
    private final ResourceLocation id;
    private final String name;
    private final String description;
    private final RequirementData requirementData;
    private final RewardData rewardData;
    private final boolean isTimedQuest;
    private final int maxQuestCompletionTime;
    private QuestStatus status;

    public static final Codec<QuestData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("id").forGetter(QuestData::getId),
                    Codec.STRING.fieldOf("name").forGetter(QuestData::getName),
                    Codec.STRING.fieldOf("description").forGetter(QuestData::getDescription),
                    RequirementData.CODEC.fieldOf("requirements").forGetter(QuestData::getRequirementData),
                    RewardData.CODEC.fieldOf("rewards").forGetter(QuestData::getRewardData),
                    Codec.BOOL.fieldOf("isTimedQuest").forGetter(QuestData::isTimedQuest),
                    Codec.INT.fieldOf("maxQuestCompletionTime").forGetter(QuestData::getMaxQuestCompletionTime),
                    createQuestStatusCodec().fieldOf("status").forGetter(QuestData::getStatus)
            ).apply(instance, (id, name, description, requirements, rewards, isTimed, maxTime, status) ->
                    new QuestData.Builder(id)
                            .setName(name)
                            .setDescription(description)
                            .setRequirementData(requirements)
                            .setRewardData(rewards)
                            .setTimedQuest(isTimed)
                            .setMaxQuestCompletionTime(maxTime)
                            .setStatus(status)
                            .build()
            )
    );

    private static Codec<QuestStatus> createQuestStatusCodec() {
        return Codec.INT.xmap(
                QuestStatus::fromId,
                QuestStatus::getId
        );
    }

    private QuestData(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.requirementData = builder.requirementData;
        this.rewardData = builder.rewardData;
        this.isTimedQuest = builder.isTimedQuest;
        this.maxQuestCompletionTime = builder.maxQuestCompletionTime;
        this.status = builder.status;
    }

    public ResourceLocation getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public RequirementData getRequirementData() {
        return requirementData;
    }

    public RewardData getRewardData() {
        return rewardData;
    }

    public boolean isTimedQuest() {
        return isTimedQuest;
    }

    public int getMaxQuestCompletionTime() {
        return maxQuestCompletionTime;
    }

    public QuestStatus getStatus() {
        return status;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", id.toString());
        tag.putString("name", name);
        tag.putString("description", description);
        tag.putBoolean("isTimedQuest", isTimedQuest);
        tag.putInt("maxQuestCompletionTime", maxQuestCompletionTime);
        tag.putInt("status", status.getId());

        if (requirementData != null) {
            tag.put("requirementData", requirementData.serializeNBT(provider));
        }
        if (rewardData != null) {
            tag.put("rewardData", rewardData.serializeNBT(provider));
        }

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        if (tag.contains("status")) {
            this.status = QuestStatus.fromId(tag.getInt("status"));
        }

        if (tag.contains("requirementData") && requirementData != null) {
            requirementData.deserializeNBT(provider, tag.getCompound("requirementData"));
        }
        if (tag.contains("rewardData") && rewardData != null) {
            rewardData.deserializeNBT(provider, tag.getCompound("rewardData"));
        }
    }

    // Builder class for constructing QuestData instances
    public static class Builder {
        private final ResourceLocation id;
        private String name = "";
        private String description = "";
        private RequirementData requirementData = null;
        private RewardData rewardData = null;
        private boolean isTimedQuest = false;
        private int maxQuestCompletionTime = 0;
        private QuestStatus status = QuestStatus.NOT_STARTED;

        public Builder(ResourceLocation id) {
            this.id = id;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setRequirementData(RequirementData requirementData) {
            this.requirementData = requirementData;
            return this;
        }

        public Builder setRewardData(RewardData rewardData) {
            this.rewardData = rewardData;
            return this;
        }

        public Builder setTimedQuest(boolean isTimedQuest) {
            this.isTimedQuest = isTimedQuest;
            return this;
        }

        public Builder setMaxQuestCompletionTime(int maxQuestCompletionTime) {
            this.maxQuestCompletionTime = maxQuestCompletionTime;
            return this;
        }

        public Builder setStatus(QuestStatus status) {
            this.status = status;
            return this;
        }

        public QuestData build() {
            return new QuestData(this);
        }
    }

    public enum QuestStatus {
        NOT_STARTED(-1),
        STARTED(0),
        IS_PROGRESSING(1),
        COMPLETED(2),
        FAILED(-2);

        private final int id;

        QuestStatus(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }

        public static QuestStatus fromId(int id) {
            for (QuestStatus status : QuestStatus.values()) {
                if (status.getId() == id) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid quest status ID: "+id);
        }
    }
}
