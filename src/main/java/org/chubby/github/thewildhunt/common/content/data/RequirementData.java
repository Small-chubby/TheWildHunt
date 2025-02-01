package org.chubby.github.thewildhunt.common.content.data;

import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.function.Predicate;

public record RequirementData(org.chubby.github.thewildhunt.common.content.data.RequirementData.RequirementType type,
                              Map<String, Object> conditions, int requiredProgress,
                              Predicate<Player> customRequirement) {

    public boolean checkRequirement(Player player) {
        if (customRequirement != null) {
            return customRequirement.test(player);
        }
        return false;
    }

    public enum RequirementType {
        ITEM_COLLECTION,
        MONSTER_DEFEAT,
        LOCATION_REACH,
        PLAYER_LEVEL,
        CUSTOM
    }
}