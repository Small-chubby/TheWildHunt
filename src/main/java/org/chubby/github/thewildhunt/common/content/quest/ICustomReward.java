package org.chubby.github.thewildhunt.common.content.quest;

public interface ICustomReward<T>
{
    T getReward();

    void setReward(T pVal);

    boolean isValidReward(T pVal);
}
