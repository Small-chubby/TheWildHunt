package org.chubby.github.thewildhunt.datagen.custom;

import com.google.common.collect.Maps;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.chubby.github.thewildhunt.common.data.QuestData;
import org.chubby.github.thewildhunt.common.data.RequirementData;
import org.chubby.github.thewildhunt.common.data.RewardData;
import org.chubby.github.thewildhunt.util.CommonClass;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class QuestGenerator extends QuestProvider {
    public QuestGenerator(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void generate(HolderLookup.Provider registries, Consumer<QuestData> writer) {
        writer.accept(
                new QuestData.Builder(CommonClass.questLoc("mine_diamonds"))
                        .setName("Mine Diamonds Quest")
                        .setDescription("Mine the required diamonds.")
                        .setRequirementData(
                                new RequirementData.Builder()
                                        .setType(RequirementData.RequirementType.ITEM_COLLECTION)
                                        .setConditions(Map.of())
                                        .setCustomRequirement(player -> player.getInventory().contains(new ItemStack(Items.DIAMOND,5)))
                                        .build()
                        )
                        .setRewardData(
                                new RewardData.Builder()
                                        .setExperienceReward(500)
                                        .setItemRewards(List.of(new ItemStack(Items.DIAMOND,5)))
                                        .setCustomReward(null)
                                        .setAdditionalRewards(Map.of())
                                        .build()
                        )
                        .setTimedQuest(false)
                        .setMaxQuestCompletionTime(0)
                        .setStatus(QuestData.QuestStatus.NOT_STARTED)
                        .build()
        );
    }
}
