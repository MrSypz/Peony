package sypztep.peony.common.reloadlistener;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import sypztep.peony.Peony;
import sypztep.peony.common.data.BaseMobStatsEntry;

import java.util.Map;

public class MobStatsReloadListenr extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();

    public MobStatsReloadListenr() {
        super(GSON, "basemobstats");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        BaseMobStatsEntry.BASEMOBSTATS_MAP.clear();

        for (Map.Entry<ResourceLocation, JsonElement> entry : object.entrySet()) {
            ResourceLocation identifier = entry.getKey();

            try {
                JsonObject jsonObject = entry.getValue().getAsJsonObject();

                String filePath = identifier.getPath();
                String entityIdStr = filePath.replace("/", ":");
                ResourceLocation entityId = ResourceLocation.parse(entityIdStr);

                EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(entityId);

                // Check if entity type exists (similar to your Fabric validation)
                if (entityType == BuiltInRegistries.ENTITY_TYPE.get(BuiltInRegistries.ENTITY_TYPE.getDefaultKey())
                        && !entityId.equals(BuiltInRegistries.ENTITY_TYPE.getDefaultKey())) {
                    continue;
                }

                // Parse the basestat object (matching your JSON structure)
                JsonObject basestat = jsonObject.getAsJsonObject("basestat");

                int str = basestat.get("str").getAsInt();
                int agi = basestat.get("agi").getAsInt();
                int dex = basestat.get("dex").getAsInt();
                int vit = basestat.get("vit").getAsInt();
                int anInt = basestat.get("int").getAsInt();
                int luk = basestat.get("luk").getAsInt();
                int lvl = basestat.get("lvl").getAsInt();
                int exp = basestat.get("exp").getAsInt();

                BaseMobStatsEntry.BASEMOBSTATS_MAP.put(entityType, new BaseMobStatsEntry(str, agi, dex, vit, anInt, luk, lvl, exp));

            } catch (Exception e) {
                Peony.LOGGER.error("Failed to load base mob stats from '{}': {}", identifier, e.getMessage());
                Peony.LOGGER.error("Exception details: ", e);
            }
        }

        Peony.LOGGER.info("Loaded {} mob stat entries", BaseMobStatsEntry.BASEMOBSTATS_MAP.size());
    }
}