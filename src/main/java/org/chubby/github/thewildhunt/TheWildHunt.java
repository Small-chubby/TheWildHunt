package org.chubby.github.thewildhunt;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import org.chubby.github.thewildhunt.common.manager.QuestManager;
import org.chubby.github.thewildhunt.util.Constants;

@Mod(Constants.MODID)
public class TheWildHunt {


    public TheWildHunt(IEventBus modEventBus, ModContainer modContainer)
    {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::reloadResource);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    private void reloadResource(AddReloadListenerEvent event)
    {
        event.addListener(new QuestManager());
    }
}
