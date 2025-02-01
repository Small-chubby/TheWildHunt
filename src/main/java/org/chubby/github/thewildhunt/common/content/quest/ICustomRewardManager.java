package org.chubby.github.thewildhunt.common.content.quest;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface ICustomRewardManager
{
    void addRewards(Player player, Level level);
}
