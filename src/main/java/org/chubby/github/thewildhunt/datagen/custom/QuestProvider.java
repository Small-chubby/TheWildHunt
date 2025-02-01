package org.chubby.github.thewildhunt.datagen.custom;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.chubby.github.thewildhunt.common.data.QuestData;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class QuestProvider implements DataProvider
{
    private final PackOutput.PathProvider packOutput;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;

    public QuestProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        this.packOutput = packOutput.createPathProvider(PackOutput.Target.DATA_PACK,"quest");
        this.lookupProvider = lookupProvider;
    }

    protected abstract void generate(HolderLookup.Provider registries, Consumer<QuestData> writer);

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        return this.lookupProvider.thenCompose(provider -> {
            Set<ResourceLocation> set = Sets.newHashSet();
            List<CompletableFuture<?>> list = Lists.newArrayList();
            Consumer<QuestData> consumer = questData -> {
              if(!set.add(questData.getId())){
                  throw new IllegalStateException("Duplicate quest data " + questData.getId());
              }
              else{
                  Path path = this.packOutput.json(questData.getId());
                  list.add(DataProvider.saveStable(cachedOutput,provider,QuestData.CODEC,questData,path));
              }
            };
           this.generate(provider,consumer);
           return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public String getName() {
        return "Quests Data";
    }
}
