package sypztep.peony.common.init;

import net.minecraft.network.chat.Component;
import sypztep.peony.Peony;
import sypztep.peony.PeonyConfig;
import sypztep.peony.client.util.TextParticleProvider;

import java.awt.*;

public final class ModParticle {
    public static final TextParticleProvider CRITICAL;
    public static final TextParticleProvider BACK_ATTACK;
    public static final TextParticleProvider MISSING;
    public static final TextParticleProvider MISSING_MONSTERS;

    static {
        CRITICAL = TextParticleProvider.register(Component.translatable(Peony.MODID + ".text.critical"),
                new Color(0xFF4F00), -0.055f, -0.045F,
                PeonyConfig.SHOW_ATTACK_NOTIFY_PARTICLES);

        BACK_ATTACK = TextParticleProvider.register(Component.translatable(Peony.MODID + ".text.backattack"),
                new Color(0xFFFFFF), -0.035F, 0.3F,
                PeonyConfig.SHOW_ATTACK_NOTIFY_PARTICLES);

        MISSING = TextParticleProvider.register(Component.translatable(Peony.MODID + ".text.missing"),
                new Color(255, 255, 255),-0.045F,-0.085F,
                PeonyConfig.SHOW_MISS_PARTICLES);

        MISSING_MONSTERS = TextParticleProvider.register(Component.translatable(Peony.MODID + ".text.missing"),
                new Color(255,  28, 28),-0.045F,-0.085F,
                PeonyConfig.SHOW_MISS_PARTICLES);
    }
}