package sypztep.peony.common;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import sypztep.peony.Peony;
import sypztep.peony.common.datagen.MobStatsDataProvider;

@EventBusSubscriber(modid = Peony.MODID, bus= EventBusSubscriber.Bus.MOD)
public class PeonyDatagen {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();

        generator.addProvider(event.includeServer(), new MobStatsDataProvider(output, Peony.MODID));
    }
}
