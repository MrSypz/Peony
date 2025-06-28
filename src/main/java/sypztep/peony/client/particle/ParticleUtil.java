package sypztep.peony.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.awt.*;

public final class ParticleUtil {
    private static void spawnParticle(Entity target, String text, Color color, float maxSize, float yPos) {
        Minecraft client = Minecraft.getInstance();
        ClientLevel world = client.level;
        if (world == null || !world.isClientSide()) return;

        Vec3 particlePos = target.position().add(0.0, target.getBbHeight() + 0.95 + yPos, 0.0);
        TextParticle particle = new TextParticle(world, particlePos.x, particlePos.y, particlePos.z);
        particle.setText(text);
        particle.setColor(color.getRed(), color.getGreen(), color.getBlue());
        particle.setMaxSize(maxSize);
        client.particleEngine.add(particle);
    }

    public static void spawnTextParticle(Entity target, Component text, Color color, float maxSize, float yPos) {
        if (target.level().isClientSide())
            spawnParticle(target, text.getString(), color, maxSize, yPos);
    }

    public static void spawnTextParticle(Entity target, Component text, Color color, float maxSize) {
        if (target.level().isClientSide())
            spawnParticle(target, text.getString(), color, maxSize, 0);
    }

    public static void spawnTextParticle(Entity target, Component text, float maxSize) {
        if (target.level().isClientSide())
            spawnParticle(target, text.getString(), new Color(255, 255, 255), maxSize, 0);
    }

    public static void spawnTextParticle(Entity target, Component text) {
        if (target.level().isClientSide())
            spawnParticle(target, text.getString(), new Color(255, 255, 255), -0.045F, 0);
    }
}
