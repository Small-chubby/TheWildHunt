package org.chubby.github.thewildhunt.common.content.data;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.chubby.github.thewildhunt.common.content.quest.ICustomReward;
import org.chubby.github.thewildhunt.common.content.quest.ICustomRewardManager;
import org.chubby.github.thewildhunt.common.content.quest.RewardManager;

import java.util.List;
import java.util.Map;

public class RewardData {
    private final List<ItemStack> itemRewards;
    private final int experienceReward;
    private final ICustomReward<?> customReward;
    private final Map<String, Object> additionalRewards;

    public RewardData(List<ItemStack> itemRewards, int experienceReward,
                      ICustomReward<?> customReward, Map<String, Object> additionalRewards) {
        this.itemRewards = itemRewards;
        this.experienceReward = experienceReward;
        this.customReward = customReward;
        this.additionalRewards = additionalRewards;
    }

    public void grantRewards(Player player) {
        if (itemRewards != null && !itemRewards.isEmpty()) {
            for (ItemStack item : itemRewards) {
                if(player.getInventory().isEmpty()){
                    player.getInventory().add(item);
                }
            }
        }

        if (experienceReward > 0) {
            player.giveExperiencePoints(experienceReward);
        }

        if (customReward != null) {
            ((ICustomRewardManager) customReward).addRewards(player,player.level());
        }

        if (additionalRewards != null && !additionalRewards.isEmpty())
        {
            RewardManager.addAdditionalPlayerReward(player);
        }
    }

}