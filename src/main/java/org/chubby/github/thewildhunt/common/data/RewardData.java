package org.chubby.github.thewildhunt.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.chubby.github.thewildhunt.common.data.quest.ICustomReward;
import org.chubby.github.thewildhunt.common.data.quest.ICustomRewardManager;
import org.chubby.github.thewildhunt.common.data.quest.RewardManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles the reward data for quests, including items, experience, and custom rewards.
 */
public class RewardData implements INBTSerializable<CompoundTag> {
    private final List<ItemStack> itemRewards;
    private int experienceReward;
    private final ICustomReward<?> customReward;
    private final Map<String, Object> additionalRewards;

    private RewardData(Builder builder) {
        this.itemRewards = builder.itemRewards;
        this.experienceReward = builder.experienceReward;
        this.customReward = builder.customReward;
        this.additionalRewards = builder.additionalRewards;
    }

    public List<ItemStack> getItemRewards() {
        return itemRewards;
    }

    public int getExperienceReward() {
        return experienceReward;
    }

    public ICustomReward<?> getCustomReward() {
        return customReward;
    }

    public Map<String, Object> getAdditionalRewards() {
        return additionalRewards;
    }

    /**
     * Grants rewards to the player, including items, experience, and custom rewards.
     *
     * @param player The player receiving the rewards.
     */
    public void grantRewards(Player player) {
        itemRewards.forEach(player.getInventory()::add);

        if (experienceReward > 0) {
            player.giveExperiencePoints(experienceReward);
        }

        if (customReward instanceof ICustomRewardManager rewardManager) {
            rewardManager.addRewards(player, player.level());
        }

        if (!additionalRewards.isEmpty()) {
            RewardManager.addAdditionalPlayerReward(player);
        }
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        ListTag itemList = new ListTag();

        for (ItemStack item : itemRewards) {
            itemList.add(item.save(provider));
        }
        tag.put("itemRewards", itemList);
        tag.putInt("experienceReward", experienceReward);

        if (customReward instanceof INBTSerializable<?> serializableReward) {
            tag.put("customReward", serializableReward.serializeNBT(provider));
        }
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        itemRewards.clear();
        if (tag.contains("itemRewards", Tag.TAG_LIST)) {
            ListTag itemList = tag.getList("itemRewards", Tag.TAG_COMPOUND);
            for (Tag itemTag : itemList) {
                itemRewards.add(ItemStack.parseOptional(provider, (CompoundTag) itemTag));
            }
        }

        experienceReward = tag.getInt("experienceReward");

        if (tag.contains("customReward") && customReward instanceof INBTSerializable<CompoundTag> serializableReward) {
            serializableReward.deserializeNBT(provider, tag.getCompound("customReward"));
        }
    }

    /**
     * Builder for {@link RewardData}.
     */
    public static class Builder {
        private List<ItemStack> itemRewards = new ArrayList<>();
        private int experienceReward;
        private ICustomReward<?> customReward;
        private Map<String, Object> additionalRewards = new HashMap<>();

        public Builder setItemRewards(List<ItemStack> itemRewards) {
            this.itemRewards = itemRewards;
            return this;
        }

        public Builder setExperienceReward(int experienceReward) {
            this.experienceReward = experienceReward;
            return this;
        }

        public Builder setCustomReward(ICustomReward<?> customReward) {
            this.customReward = customReward;
            return this;
        }

        public Builder setAdditionalRewards(Map<String, Object> additionalRewards) {
            this.additionalRewards = additionalRewards;
            return this;
        }

        public RewardData build() {
            return new RewardData(this);
        }
    }

    @SuppressWarnings("unchecked")
    public static final Codec<Map<String, Object>> ADDITIONAL_REWARDS_CODEC = Codec.unboundedMap(
            Codec.STRING,
            Codec.STRING.xmap(
                    str -> (Object)str,
                    Object::toString
            )
    );

    public static final Codec<RewardData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ItemStack.CODEC.listOf().fieldOf("itemRewards").forGetter(RewardData::getItemRewards),
                    Codec.INT.fieldOf("experienceReward").forGetter(RewardData::getExperienceReward),
                    ADDITIONAL_REWARDS_CODEC.optionalFieldOf("additionalRewards", new HashMap<>())
                            .forGetter(RewardData::getAdditionalRewards)
            ).apply(instance, (items, exp, additional) ->
                    new RewardData.Builder()
                            .setItemRewards(items)
                            .setExperienceReward(exp)
                            .setAdditionalRewards(additional)
                            .build()
            )
    );
}
