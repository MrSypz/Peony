package sypztep.peony.client.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import sypztep.peony.PeonyConfig;
import sypztep.peony.common.attachment.LivingEntityStatsAttachment;
import sypztep.peony.common.init.ModAttachments;

public class XpHudOverlay implements LayeredDraw.Layer {

    // Animation state
    private static float animatedXpProgress = 0.0f;
    private static int lastXp = 0;
    private static float xpGainGlowTimer = 0.0f;
    private static final float XP_GLOW_DURATION = 2.0f; // 2 seconds duration
    
    // Slide animation state
    private static float slideOffset = 0.0f; // 0 = fully visible, 1 = fully hidden
    private static float hideTimer = 0.0f;
    private static boolean shouldBeVisible = true;
    private static final float SLIDE_DURATION = 0.5f; // Animation duration in seconds

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

        // Get configuration values
        boolean isDynamicMode = PeonyConfig.XP_HUD_DYNAMIC_MODE.get();
        double autoHideDelay = PeonyConfig.XP_HUD_AUTO_HIDE_DELAY.get();

        // Detect XP gain and start timer
        if (currentXp > lastXp) {
            xpGainGlowTimer = XP_GLOW_DURATION;
            // In dynamic mode, show HUD when XP is gained
            if (isDynamicMode) {
                shouldBeVisible = true;
                hideTimer = (float) autoHideDelay;
            }
        }

        // Update animations
        float deltaTime = deltaTracker.getGameTimeDeltaTicks() / 20.0f;
        updateAnimations(currentXp, xpToNext, isMaxLevel, deltaTime, isDynamicMode, autoHideDelay);

        // Calculate slide position offset
        int currentHudX = calculateHudX(isDynamicMode);

        // Only render if HUD is at least partially visible
        if (!isDynamicMode || slideOffset < 1.0f) {
            // Render the HUD with player name
            renderXpHud(guiGraphics, player.getName().getString(), level, currentXp, xpToNext, animatedXpProgress, isMaxLevel, currentHudX);
        }

        lastXp = currentXp;
    }

    private void updateAnimations(int currentXp, int xpToNext, boolean isMaxLevel, float deltaTime, 
                                boolean isDynamicMode, double autoHideDelay) {
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

        // Handle dynamic mode animations
        if (isDynamicMode) {
            // Update hide timer
            if (hideTimer > 0) {
                hideTimer -= deltaTime;
                if (hideTimer <= 0) {
                    shouldBeVisible = false;
                }
            }

            // Calculate target slide offset
            float targetSlideOffset = shouldBeVisible ? 0.0f : 1.0f;
            
            // Smooth slide animation
            float slideSpeed = 1.0f / SLIDE_DURATION; // Complete animation in SLIDE_DURATION seconds
            if (slideOffset != targetSlideOffset) {
                float direction = targetSlideOffset > slideOffset ? 1.0f : -1.0f;
                slideOffset += direction * slideSpeed * deltaTime;
                
                // Clamp to target
                if (direction > 0 && slideOffset > targetSlideOffset) {
                    slideOffset = targetSlideOffset;
                } else if (direction < 0 && slideOffset < targetSlideOffset) {
                    slideOffset = targetSlideOffset;
                }
            }
        } else {
            // In static mode, always visible
            slideOffset = 0.0f;
            shouldBeVisible = true;
        }
    }

    private void renderXpHud(GuiGraphics guiGraphics, String playerName, int level, int currentXp, int xpToNext,
                             float xpProgress, boolean isMaxLevel, int hudX) {

        int hudWidth = BAR_WIDTH + 10;
        int hudHeight = 40; // Increased height for player name

        Minecraft minecraft = Minecraft.getInstance();

        // Background panel
        guiGraphics.fill(hudX - 3, HUD_Y - 3, hudX + hudWidth, HUD_Y + hudHeight, BACKGROUND_COLOR);
        drawBorder(guiGraphics, hudX - 3, HUD_Y - 3, hudWidth + 3, hudHeight + 3);

        // Player name and level text
        String playerLevelText = playerName + " | " + (isMaxLevel ? "MAX" : "Lv." + level);
        int levelColor = isMaxLevel ? 0xFFFF6600 : LEVEL_COLOR;
        guiGraphics.drawString(minecraft.font, playerLevelText, hudX, HUD_Y, levelColor, true);

        // XP Bar
        int barY = HUD_Y + 22; // Moved down to accommodate player name
        guiGraphics.fill(hudX, barY, hudX + BAR_WIDTH, barY + BAR_HEIGHT, XP_BAR_BG_COLOR);

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
                guiGraphics.fill(hudX - 2, barY - 2, hudX + progressWidth + 2, barY + BAR_HEIGHT + 2, xpGlowColor);
                guiGraphics.fill(hudX - 1, barY - 1, hudX + progressWidth + 1, barY + BAR_HEIGHT + 1, xpGlowColor);
            }

            // Normal XP bar
            guiGraphics.fill(hudX, barY, hudX + progressWidth, barY + BAR_HEIGHT, XP_BAR_COLOR);
        }

        drawBorder(guiGraphics, hudX, barY, BAR_WIDTH, BAR_HEIGHT);

        // XP Text
        String xpText;
        if (isMaxLevel) {
            xpText = "MAX LEVEL";
        } else {
            xpText = LivingEntityStatsAttachment.formatNumber(currentXp) + "/" +
                    LivingEntityStatsAttachment.formatNumber(xpToNext);
        }

        int textY = barY + BAR_HEIGHT + 2;
        guiGraphics.drawString(minecraft.font, xpText, hudX, textY, TEXT_COLOR, true);

        // XP Percentage
        if (!isMaxLevel) {
            String percentText = String.format("%.1f%%", xpProgress * 100);
            int percentX = hudX + BAR_WIDTH - minecraft.font.width(percentText);
            guiGraphics.drawString(minecraft.font, percentText, percentX, textY, 0xFFAAAAAA, true);
        }
    }

    private void drawBorder(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        guiGraphics.fill(x, y, x + width, y + 1, BORDER_COLOR);
        guiGraphics.fill(x, y + height - 1, x + width, y + height, BORDER_COLOR);
        guiGraphics.fill(x, y, x + 1, y + height, BORDER_COLOR);
        guiGraphics.fill(x + width - 1, y, x + width, y + height, BORDER_COLOR);
    }

    /**
     * Calculate the X position of the HUD based on animation state
     */
    private int calculateHudX(boolean isDynamicMode) {
        if (!isDynamicMode) {
            return HUD_X;
        }

        // Calculate slide offset - HUD slides to the left (negative X)
        int hudWidth = BAR_WIDTH + 10 + 6; // Include border padding
        int hiddenX = -hudWidth; // Completely off-screen to the left
        
        // Apply cubic easing to the slide offset for smooth animation
        float easedOffset = cubicEaseInOut(slideOffset);
        
        return (int) Mth.lerp(easedOffset, HUD_X, hiddenX);
    }

    /**
     * Cubic ease-in-out function for smooth animations
     */
    private float cubicEaseInOut(float t) {
        if (t < 0.5f) {
            return 4.0f * t * t * t;
        } else {
            float p = 2.0f * t - 2.0f;
            return 1.0f + p * p * p / 2.0f;
        }
    }
}