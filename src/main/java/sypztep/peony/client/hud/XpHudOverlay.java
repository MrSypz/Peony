package sypztep.peony.client.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import sypztep.peony.common.attachment.LivingEntityStatsAttachment;
import sypztep.peony.common.init.ModAttachments;

public class XpHudOverlay implements LayeredDraw.Layer {

    // Animation state
    private static float animatedXpProgress = 0.0f;
    private static int lastXp = 0;
    private static float xpGainGlowTimer = 0.0f;
    private static final float XP_GLOW_DURATION = 2.0f; // 2 seconds duration

    // HUD positioning
    private static final int HUD_X = 10;
    private static final int HUD_Y = 10;
    private static final int BAR_WIDTH = 80;
    private static final int BAR_HEIGHT = 6;

    // Colors
    private static final int BACKGROUND_COLOR = 0xA0000000;
    private static final int BORDER_COLOR = 0xFF444444;
    private static final int XP_BAR_COLOR = 0xFF00CC00;
    private static final int XP_BAR_BG_COLOR = 0xFF222222;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int LEVEL_COLOR = 0xFFFFD700;
    private static final int XP_GAIN_GLOW_COLOR = 0xFFFFFFFF;

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player == null || minecraft.options.hideGui) {
            return;
        }

        LivingEntityStatsAttachment stats = player.getData(ModAttachments.LIVINGSTATS.get());
        if (stats == null) return;

        int level = stats.getLevel();
        int currentXp = stats.getXp();
        int xpToNext = stats.getXpToNextLevel();
        boolean isMaxLevel = stats.getLevelSystem().isAtMaxLevel();

        // Detect XP gain and start timer
        if (currentXp > lastXp) {
            xpGainGlowTimer = XP_GLOW_DURATION;
        }

        // Update animations
        float deltaTime = deltaTracker.getGameTimeDeltaTicks() / 20.0f;
        updateAnimations(currentXp, xpToNext, isMaxLevel, deltaTime);

        // Render the HUD
        renderXpHud(guiGraphics, level, currentXp, xpToNext, animatedXpProgress, isMaxLevel);

        lastXp = currentXp;
    }

    private void updateAnimations(int currentXp, int xpToNext, boolean isMaxLevel, float deltaTime) {
        // XP bar smooth animation
        float targetProgress = isMaxLevel ? 1.0f : (float) currentXp / (float) xpToNext;
        float lerpSpeed = 0.08f;
        animatedXpProgress = Mth.lerp(lerpSpeed, animatedXpProgress, targetProgress);

        // XP gain glow timer countdown
        if (xpGainGlowTimer > 0) {
            xpGainGlowTimer -= deltaTime;
            if (xpGainGlowTimer < 0) {
                xpGainGlowTimer = 0;
            }
        }
    }

    private void renderXpHud(GuiGraphics guiGraphics, int level, int currentXp, int xpToNext,
                             float xpProgress, boolean isMaxLevel) {

        int hudWidth = BAR_WIDTH + 10;
        int hudHeight = 30;

        Minecraft minecraft = Minecraft.getInstance();

        // Background panel
        guiGraphics.fill(HUD_X - 3, HUD_Y - 3, HUD_X + hudWidth, HUD_Y + hudHeight, BACKGROUND_COLOR);
        drawBorder(guiGraphics, HUD_X - 3, HUD_Y - 3, hudWidth + 3, hudHeight + 3);

        // Level text
        String levelText = isMaxLevel ? "MAX" : "Lv." + level;
        int levelColor = isMaxLevel ? 0xFFFF6600 : LEVEL_COLOR;
        guiGraphics.drawString(minecraft.font, levelText, HUD_X, HUD_Y, levelColor, true);

        // XP Bar
        int barY = HUD_Y + 12;
        guiGraphics.fill(HUD_X, barY, HUD_X + BAR_WIDTH, barY + BAR_HEIGHT, XP_BAR_BG_COLOR);

        // XP Bar progress with gentle glow effect
        int progressWidth = (int) (BAR_WIDTH * xpProgress);
        if (progressWidth > 0) {
            // XP gain glow effect - gentle and non-intrusive
            if (xpGainGlowTimer > 0) {
                float glowStrength = xpGainGlowTimer / XP_GLOW_DURATION;

                float time = (XP_GLOW_DURATION - xpGainGlowTimer) * 3.0f; // Speed up pulse
                float pulse = (float) (0.5f + 0.5f * Math.sin(time * Math.PI));

                // Combine strength and pulse
                float finalGlow = glowStrength * (0.7f + 0.3f * pulse); // Min 70%, max 100% intensity

                int glowAlpha = (int) (finalGlow * 130); // Lower alpha for subtlety
                int xpGlowColor = (glowAlpha << 24) | (XP_GAIN_GLOW_COLOR & 0x00FFFFFF);

                // Single subtle glow layer
                guiGraphics.fill(HUD_X - 2, barY - 2, HUD_X + progressWidth + 2, barY + BAR_HEIGHT + 2, xpGlowColor);
                guiGraphics.fill(HUD_X - 1, barY - 1, HUD_X + progressWidth + 1, barY + BAR_HEIGHT + 1, xpGlowColor);
            }

            // Normal XP bar
            guiGraphics.fill(HUD_X, barY, HUD_X + progressWidth, barY + BAR_HEIGHT, XP_BAR_COLOR);
        }

        drawBorder(guiGraphics, HUD_X, barY, BAR_WIDTH, BAR_HEIGHT);

        // XP Text
        String xpText;
        if (isMaxLevel) {
            xpText = "MAX LEVEL";
        } else {
            xpText = LivingEntityStatsAttachment.formatNumber(currentXp) + "/" +
                    LivingEntityStatsAttachment.formatNumber(xpToNext);
        }

        int textY = barY + BAR_HEIGHT + 2;
        guiGraphics.drawString(minecraft.font, xpText, HUD_X, textY, TEXT_COLOR, true);

        // XP Percentage
        if (!isMaxLevel) {
            String percentText = String.format("%.1f%%", xpProgress * 100);
            int percentX = HUD_X + BAR_WIDTH - minecraft.font.width(percentText);
            guiGraphics.drawString(minecraft.font, percentText, percentX, textY, 0xFFAAAAAA, true);
        }
    }

    private void drawBorder(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        guiGraphics.fill(x, y, x + width, y + 1, BORDER_COLOR);
        guiGraphics.fill(x, y + height - 1, x + width, y + height, BORDER_COLOR);
        guiGraphics.fill(x, y, x + 1, y + height, BORDER_COLOR);
        guiGraphics.fill(x + width - 1, y, x + width, y + height, BORDER_COLOR);
    }
}