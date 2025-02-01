package org.chubby.github.thewildhunt.common.content.data;

import net.minecraft.resources.ResourceLocation;

public class QuestData
{
    private final ResourceLocation id;
    private final String name;
    private final String description;
    private final RequirementData requirementData;
    private final RewardData rewardData;
    private final boolean isTimedQuest;
    private final int maxQuestCompletionTime;
    private final QuestStatus status;

    public QuestData(ResourceLocation id, String name, String description, RequirementData requirementData, RewardData rewardData, boolean isTimedQuest, int maxQuestCompletionTime, QuestStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.requirementData = requirementData;
        this.rewardData = rewardData;
        this.isTimedQuest = isTimedQuest;
        this.maxQuestCompletionTime = maxQuestCompletionTime;
        this.status = status;
    }

    //Helper Enums
    public enum QuestStatus {
        NOT_STARTED(-1),
        STARTED(0),
        IS_PROGRESSING(1),
        COMPLETED(2),
        FAILED(-2);

        final int id;
        QuestStatus(int id) { this.id = id; }
        public int getId() { return this.id; }
    }
}
