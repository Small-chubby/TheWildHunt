package org.chubby.github.thewildhunt.util;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class CommonClass
{
    public static ResourceLocation customLoc (String pPath)
    {
        return ResourceLocation.fromNamespaceAndPath(Constants.MODID, pPath);
    }
}
