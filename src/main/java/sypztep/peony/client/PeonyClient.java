package sypztep.peony.client;

import net.fabricmc.api.ClientModInitializer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import sypztep.peony.Peony;

@Mod(value = Peony.MODID, dist = Dist.CLIENT)
public class PeonyClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
    }
}
