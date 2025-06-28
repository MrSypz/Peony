package sypztep.peony.common.init;

import net.minecraft.network.chat.Component;
import sypztep.peony.Peony;
import sypztep.peony.client.particle.TextParticleProvider;

import java.awt.*;

public final class ModParticle {
    public static final TextParticleProvider CRITICAL;
    public static final TextParticleProvider MISSING;
    public static final TextParticleProvider MISSING_MONSTERS;
    public static final TextParticleProvider BACK_ATTACK;

    static {
        CRITICAL = TextParticleProvider.builder(Component.translatable(Peony.MODID + ".text.critical"))
                .color(new Color(0xFF4F00))
                .maxSize(-0.055F)
                .yPos(-0.045F)
                .build();

        MISSING = TextParticleProvider.builder(Component.translatable(Peony.MODID + ".text.missing"))
                .color(new Color(255, 255, 255))
                .maxSize(-0.045F)
                .yPos(-0.085F)
                .build();

        MISSING_MONSTERS = TextParticleProvider.builder(Component.translatable(Peony.MODID + ".text.missing"))
                .color(new Color(255, 28, 28))
                .maxSize(-0.045F)
                .yPos(-0.085F)
                .build();

        BACK_ATTACK = TextParticleProvider.builder(Component.translatable(Peony.MODID + ".text.backattack"))
                .color(new Color(0xFFFFFF))
                .maxSize(-0.055F)
                .yPos(-0.085F)
                .build();
    }
}