package sypztep.peony.common.datagen;

import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MobStatsDataProvider implements DataProvider {
    private final PackOutput output;
    private final String modid;

    public MobStatsDataProvider(PackOutput output, String modid) {
        this.output = output;
        this.modid = modid;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        Map<ResourceLocation, JsonObject> map = new HashMap<>();

        generateMobStats(map);

        // Write each JSON file
        return CompletableFuture.allOf(
                map.entrySet().stream()
                        .map(entry -> {
                            ResourceLocation id = entry.getKey();
                            JsonObject json = entry.getValue();
                            return DataProvider.saveStable(cache, json,
                                    output.getOutputFolder().resolve("data/" + id.getNamespace() + "/basemobstats/" + id.getPath() + ".json"));
                        })
                        .toArray(CompletableFuture[]::new)
        );
    }

    private void generateMobStats(Map<ResourceLocation, JsonObject> map) {
        addMobStats(map, EntityType.ZOMBIE, 15, 8, 12, 20, 5, 1, 2, 75);
        addMobStats(map, EntityType.SKELETON, 12, 15, 25, 10, 8, 3, 4, 125);
        addMobStats(map, EntityType.SPIDER, 18, 22, 20, 12, 6, 8, 5, 150);
        addMobStats(map, EntityType.CREEPER, 10, 12, 15, 25, 12, 5, 6, 200);

        // Stronger mobs
        addMobStats(map, EntityType.ENDERMAN, 25, 30, 28, 22, 35, 15, 15, 850);
        addMobStats(map, EntityType.BLAZE, 20, 18, 22, 18, 45, 12, 12, 650);
        addMobStats(map, EntityType.WITCH, 8, 15, 18, 15, 55, 25, 18, 1200);

        // Boss-tier mobs
        addMobStats(map, EntityType.WITHER, 80, 60, 70, 90, 85, 40, 50, 15000);
        addMobStats(map, EntityType.ENDER_DRAGON, 120, 80, 90, 150, 100, 60, 75, 50000);

        // Passive mobs (low stats, small exp)
        addMobStats(map, EntityType.COW, 12, 5, 8, 18, 3, 8, 1, 25);
        addMobStats(map, EntityType.PIG, 10, 8, 6, 15, 3, 12, 1, 20);
        addMobStats(map, EntityType.CHICKEN, 5, 18, 12, 8, 3, 15, 1, 15);
        addMobStats(map, EntityType.SHEEP, 8, 6, 5, 20, 3, 10, 1, 18);

        // Ocean mobs
        addMobStats(map, EntityType.GUARDIAN, 22, 25, 30, 35, 20, 8, 20, 1500);
        addMobStats(map, EntityType.ELDER_GUARDIAN, 45, 35, 40, 60, 35, 15, 35, 8000);

        // Nether mobs
        addMobStats(map, EntityType.ZOMBIFIED_PIGLIN, 25, 15, 18, 28, 8, 6, 8, 350);
        addMobStats(map, EntityType.GHAST, 15, 20, 25, 20, 40, 18, 14, 750);
        addMobStats(map, EntityType.MAGMA_CUBE, 20, 12, 10, 35, 15, 5, 10, 400);

        // Illagers (like Ragnarok's job classes)
        addMobStats(map, EntityType.PILLAGER, 18, 20, 35, 15, 12, 10, 12, 600); // Archer-type
        addMobStats(map, EntityType.VINDICATOR, 35, 15, 20, 25, 8, 8, 14, 750); // Warrior-type
        addMobStats(map, EntityType.EVOKER, 10, 18, 25, 18, 50, 20, 22, 1800);  // Mage-type
    }

    private void addMobStats(Map<ResourceLocation, JsonObject> map, EntityType<?> entityType,
                             int str, int agi, int dex, int vit, int intel, int luk, int lvl, int exp) {
        ResourceLocation id = EntityType.getKey(entityType);

        JsonObject root = new JsonObject();
        JsonObject basestat = new JsonObject();

        basestat.addProperty("str", str);
        basestat.addProperty("agi", agi);
        basestat.addProperty("dex", dex);
        basestat.addProperty("vit", vit);
        basestat.addProperty("int", intel);
        basestat.addProperty("luk", luk);
        basestat.addProperty("lvl", lvl);
        basestat.addProperty("exp", exp);

        root.add("basestat", basestat);

        map.put(id, root);
    }

    @Override
    public String getName() {
        return "Mob Stats Data for " + modid;
    }
}