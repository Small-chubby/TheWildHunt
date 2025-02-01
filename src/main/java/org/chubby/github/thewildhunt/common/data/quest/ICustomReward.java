package org.chubby.github.thewildhunt.common.data.quest;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public interface ICustomReward<T> extends INBTSerializable<CompoundTag>
{
    T getReward();

    void setReward(T pVal);

    boolean isValidReward(T pVal);
}
