package sypztep.peony.client.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class TextParticleProvider {
    private static final Map<Integer, TextParticleProvider> REGISTRY = new HashMap<>();
    private static int nextFlag = 0;

    private final int flag;
    private final Component text;
    private final Color color;
    private final float maxSize;
    private final float yPos;
    private final Supplier<Boolean> configSupplier;

    public static class Builder {
        private Component text;
        private Color color = Color.WHITE;
        private float maxSize = -0.045f;
        private float yPos = 0f;
        private Supplier<Boolean> configSupplier = () -> true;

        public Builder(Component text) {
            this.text = text;
        }

        public Builder color(Color color) {
            this.color = color;
            return this;
        }

        public Builder maxSize(float maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public Builder yPos(float yPos) {
            this.yPos = yPos;
            return this;
        }

        public Builder config(Supplier<Boolean> configSupplier) {
            this.configSupplier = configSupplier != null ? configSupplier : () -> true;
            return this;
        }

        public TextParticleProvider build() {
            return new TextParticleProvider(text, color, maxSize, yPos, configSupplier);
        }
    }

    private TextParticleProvider(Component text, Color color, float maxSize, float yPos, Supplier<Boolean> configSupplier) {
        this.flag = nextFlag++;
        this.text = text;
        this.color = color;
        this.maxSize = maxSize;
        this.yPos = yPos;
        this.configSupplier = configSupplier;
        REGISTRY.put(this.flag, this);
    }

    private static Builder builder(Component text) {
        return new Builder(text);
    }

    public static TextParticleProvider register(Component text) {
        return builder(text).build();
    }

    public static TextParticleProvider register(Component text, float maxSize) {
        return builder(text)
                .maxSize(maxSize)
                .build();
    }

    public static TextParticleProvider register(Component text, Color color, float maxSize) {
        return builder(text)
                .color(color)
                .maxSize(maxSize)
                .build();
    }

    public static TextParticleProvider register(Component text, Color color, float maxSize, float yPos) {
        return builder(text)
                .color(color)
                .maxSize(maxSize)
                .yPos(yPos)
                .build();
    }

    public static TextParticleProvider register(Component text, Color color, float maxSize, float yPos, Supplier<Boolean> configSupplier) {
        return builder(text)
                .color(color)
                .maxSize(maxSize)
                .yPos(yPos)
                .config(configSupplier)
                .build();
    }

    public int getFlag() {
        return flag;
    }

    public static void handleParticle(Entity entity, int flag) {
        TextParticleProvider particle = REGISTRY.get(flag);
        if (particle != null && particle.configSupplier.get()) {
            ParticleUtil.spawnTextParticle(
                    entity,
                    particle.text,
                    particle.color,
                    particle.maxSize,
                    particle.yPos
            );
        }
    }
}
