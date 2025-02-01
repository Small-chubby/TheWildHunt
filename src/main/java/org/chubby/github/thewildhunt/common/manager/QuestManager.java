package org.chubby.github.thewildhunt.common.manager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.chubby.github.thewildhunt.common.data.QuestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QuestManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();
    private static final Logger LOGGER = LoggerFactory.getLogger(QuestManager.class);
    private static final String DIRECTORY = "quests/quest";
    private final Map<ResourceLocation, QuestData> quests = new ConcurrentHashMap<>();

    public QuestManager() {
        super(GSON, DIRECTORY);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonElements, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Map<ResourceLocation, QuestData> tempQuests = new HashMap<>();

        for (Map.Entry<ResourceLocation, JsonElement> entry : jsonElements.entrySet()) {
            try {
                JsonObject jsonObject = entry.getValue().getAsJsonObject();
                QuestData questData = GSON.fromJson(jsonObject, QuestData.class);
                tempQuests.put(entry.getKey(), questData);
            } catch (JsonParseException | IllegalStateException e) {
                LOGGER.error("Failed to load quest {}: {}", entry.getKey(), e.getMessage());
            }
        }

        quests.clear();
        quests.putAll(tempQuests);
        LOGGER.info("Loaded {} quests", quests.size());
    }

    public QuestData getQuest(ResourceLocation id) {
        return quests.get(id);
    }

    public Map<ResourceLocation, QuestData> getQuests() {
        return Collections.unmodifiableMap(quests);
    }
}
