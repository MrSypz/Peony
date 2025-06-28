package sypztep.peony.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.awt.*;


public final class TextParticle extends Particle {
    private static final int FLICK_DURATION = 12;
    private static final int FADE_DURATION = 10;
    private static final float VELOCITY_DAMPEN = 0.9f;
    private static final float FADE_AMOUNT = 0.1f;

    private String text;
    private float scale;
    private float maxSize;
    private float targetRed;
    private float targetGreen;
    private float targetBlue;
    private float targetAlpha;

    public TextParticle(ClientLevel world, double x, double y, double z) {
        super(world, x, y, z);
        this.lifetime = 25;
        this.scale = 0.0F;
        this.maxSize = -0.045F;
        this.gravity = -0.125f;
        this.targetRed = this.rCol;
        this.targetGreen = this.gCol;
        this.targetBlue = this.bCol;
        this.targetAlpha = this.alpha;
    }

    public void setMaxSize(float maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public void setColor(float red, float green, float blue) {
        super.setColor(clamp(red, 0.0f, 1.0f), clamp(green, 0.0f, 1.0f), clamp(blue, 0.0f, 1.0f));
    }

    public void setColor(int red, int green, int blue) {
        this.targetRed = clamp(red / 255.0f, 0.0f, 1.0f);
        this.targetGreen = clamp(green / 255.0f, 0.0f, 1.0f);
        this.targetBlue = clamp(blue / 255.0f, 0.0f, 1.0f);
        super.setColor(1.0f, 1.0f, 1.0f); // Start white for flick effect
    }

    public void setAlpha(float alpha) {
        this.targetAlpha = clamp(alpha, 0.0f, 1.0f);
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }


    @Override
    public void tick() {
        if (this.age++ <= FLICK_DURATION) {
            float progress = age / (float) FLICK_DURATION;
            setColor(
                    Mth.lerp(progress, 1.0f, targetRed),
                    Mth.lerp(progress, 1.0f, targetGreen),
                    Mth.lerp(progress, 1.0f, targetBlue)
            );
            this.scale = Mth.lerp(elasticOut(progress, 0.0F, 1.0F, 1.0F), 0.0F, this.maxSize);
            this.alpha = Mth.lerp(progress, 1.0f, targetAlpha);
        } else if (this.age <= this.lifetime) {
            float progress = (age - FLICK_DURATION) / (float) FADE_DURATION;
            setColor(
                    targetRed * (1f - progress * FADE_AMOUNT),
                    targetGreen * (1f - progress * FADE_AMOUNT),
                    targetBlue * (1f - progress * FADE_AMOUNT)
            );
            this.scale = Mth.lerp(progress, this.maxSize, 0.0f);
            this.alpha = Mth.lerp(progress, targetAlpha, 0.0f);
        } else {
            this.remove();
        }
        this.yd *= VELOCITY_DAMPEN;
        super.tick();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @Override
    public void render(@NotNull VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Vec3 cameraPos = camera.getPosition();
        float particleX = (float) (xo + (x - xo) * tickDelta - cameraPos.x);
        float particleY = (float) (yo + (y - yo) * tickDelta - cameraPos.y);
        float particleZ = (float) (zo + (z - zo) * tickDelta - cameraPos.z);

        Minecraft client = Minecraft.getInstance();
        Font textRenderer = client.font;
        MultiBufferSource.BufferSource vertexConsumers = client.renderBuffers().bufferSource();
        float textX = textRenderer.width(text) / -2.0F;

        Matrix4f matrix = new Matrix4f()
                .translation(particleX, particleY, particleZ)
                .rotate(camera.rotation())
                .rotate((float) Math.PI, 0.0F, 1.0F, 0.0F)
                .scale(scale, scale, scale);

        Vector3f offset = new Vector3f(0.0f, 0.0f, 0.03f);
        int textColor = new Color(clamp(rCol, 0.0f, 1.0f), clamp(gCol, 0.0f, 1.0f), clamp(bCol, 0.0f, 1.0f), clamp(alpha, 0.1f, 1.0f)).getRGB(); // Ensure alpha does not go below 0.1
        int textBorderColor = new Color(clamp(rCol, 0.0f, 0.0f), clamp(gCol, 0.0f, 0.0f), clamp(bCol, 0.0f, 0.0f), clamp(alpha, 0.1f, 1.0f)).getRGB(); // Ensure alpha does not go below 0.1

        for (int[] pos : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
            matrix.translate(offset);
            textRenderer.drawInBatch(text, textX + pos[0], pos[1], textBorderColor, false, matrix,
                    vertexConsumers, Font.DisplayMode.NORMAL, 0, 0xF000F0);
        }

        matrix.translate(offset);
        textRenderer.drawInBatch(text, textX, 0, textColor, false, matrix,
                vertexConsumers, Font.DisplayMode.NORMAL, 0, 0xF000F0);

        vertexConsumers.endBatch();
    }

    public void setText(@NotNull String text) {
        this.text = text;
    }

    /**
     * <h1>Easing Fun</h1>
     **/
    public static float elasticOut(float t, float b, float c, float d) {
        return elasticOut(t, b, c, d, -1f, 0f);
    }

    public static float elasticOut(float t, float b, float c, float d, float amplitude, float period) {
        if (t == 0) return b;
        if ((t /= d) == 1) return b + c;

        float a = amplitude;
        float p = period;

        if (p == 0) p = d * 0.3f;

        float s;
        if (a < Math.abs(c)) {
            a = c;
            s = p / 4;
        } else {
            s = p / (float) (2 * Math.PI) * (float) Math.asin(c / a);
        }

        return a * (float) Math.pow(2, -10 * t) * (float) Math.sin((t * d - s) * (2 * Math.PI) / p) + c + b;
    }

}