package sypztep.peony.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import sypztep.peony.client.util.DerivedStatsCalculator;
import sypztep.peony.common.attachment.LivingEntityStatsAttachment;
import sypztep.peony.common.init.ModAttachments;
import sypztep.peony.module.level.LivingStats;
import sypztep.peony.module.level.StatTypes;

/**
 * Ragnarok Online-style status screen displaying player statistics.
 */
public class StatusScreen extends Screen {
    
    // Window dimensions
    private static final int WINDOW_WIDTH = 280;
    private static final int WINDOW_HEIGHT = 320;
    
    // Colors (RO-style)
    private static final int BACKGROUND_COLOR = 0xE0C0A080;  // Light brown/tan background
    private static final int BORDER_COLOR = 0xFF8B4513;      // Saddle brown border
    private static final int BORDER_DARK = 0xFF654321;       // Darker border for depth
    private static final int HEADER_COLOR = 0xFF2F1B14;      // Dark brown header
    private static final int TEXT_COLOR = 0xFF000000;        // Black text
    private static final int VALUE_COLOR = 0xFF8B0000;       // Dark red for values
    private static final int ARROW_COLOR = 0xFF006400;       // Dark green for arrows
    private static final int STAT_POINTS_COLOR = 0xFF0000FF; // Blue for stat points
    
    // Layout constants
    private static final int PADDING = 8;
    private static final int LINE_HEIGHT = 14;
    private static final int SECTION_SPACING = 4;
    private static final int HEADER_HEIGHT = 20;
    
    private int windowX;
    private int windowY;
    
    public StatusScreen() {
        super(Component.translatable("gui.peony.status.title"));
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Center the window
        this.windowX = (this.width - WINDOW_WIDTH) / 2;
        this.windowY = (this.height - WINDOW_HEIGHT) / 2;
        
        // Add close button
        this.addRenderableWidget(
            Button.builder(Component.translatable("gui.done"), 
                button -> this.onClose())
                .bounds(windowX + WINDOW_WIDTH - 60, windowY + WINDOW_HEIGHT - 25, 50, 20)
                .build()
        );
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Semi-transparent background
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        
        // Get player stats
        LivingEntityStatsAttachment statsAttachment = minecraft.player.getData(ModAttachments.LIVINGSTATS.get());
        if (statsAttachment == null) {
            this.onClose();
            return;
        }
        
        LivingStats stats = statsAttachment.getLivingStats();
        DerivedStatsCalculator.DerivedStats derivedStats = new DerivedStatsCalculator.DerivedStats(stats);
        
        // Draw main window
        drawWindow(guiGraphics);
        
        // Draw header
        drawHeader(guiGraphics);
        
        // Draw primary stats section
        int yOffset = drawPrimaryStats(guiGraphics, stats);
        
        // Draw separator
        yOffset += SECTION_SPACING;
        drawSeparator(guiGraphics, yOffset);
        yOffset += SECTION_SPACING;
        
        // Draw derived stats section
        yOffset = drawDerivedStats(guiGraphics, derivedStats, yOffset);
        
        // Draw separator
        yOffset += SECTION_SPACING;
        drawSeparator(guiGraphics, yOffset);
        yOffset += SECTION_SPACING;
        
        // Draw status points in right column
        int rightColumnX = windowX + WINDOW_WIDTH / 2 + PADDING;
        drawStatusPoints(guiGraphics, statsAttachment.getStatPoints(), rightColumnX, yOffset);
        
        // Render widgets (close button)
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
    
    private void drawWindow(GuiGraphics guiGraphics) {
        // Main background
        guiGraphics.fill(windowX, windowY, windowX + WINDOW_WIDTH, windowY + WINDOW_HEIGHT, BACKGROUND_COLOR);
        
        // Border
        // Top border
        guiGraphics.fill(windowX, windowY, windowX + WINDOW_WIDTH, windowY + 2, BORDER_COLOR);
        // Bottom border
        guiGraphics.fill(windowX, windowY + WINDOW_HEIGHT - 2, windowX + WINDOW_WIDTH, windowY + WINDOW_HEIGHT, BORDER_DARK);
        // Left border
        guiGraphics.fill(windowX, windowY, windowX + 2, windowY + WINDOW_HEIGHT, BORDER_COLOR);
        // Right border
        guiGraphics.fill(windowX + WINDOW_WIDTH - 2, windowY, windowX + WINDOW_WIDTH, windowY + WINDOW_HEIGHT, BORDER_DARK);
    }
    
    private void drawHeader(GuiGraphics guiGraphics) {
        // Header background
        guiGraphics.fill(windowX + 4, windowY + 4, windowX + WINDOW_WIDTH - 4, windowY + 4 + HEADER_HEIGHT, HEADER_COLOR);
        
        // Header text
        Component title = Component.literal("STATUS");
        int titleWidth = this.font.width(title);
        int titleX = windowX + (WINDOW_WIDTH - titleWidth) / 2;
        int titleY = windowY + 4 + (HEADER_HEIGHT - this.font.lineHeight) / 2;
        guiGraphics.drawString(this.font, title, titleX, titleY, 0xFFFFFFFF);
    }
    
    private int drawPrimaryStats(GuiGraphics guiGraphics, LivingStats stats) {
        int startY = windowY + 4 + HEADER_HEIGHT + PADDING;
        int currentY = startY;
        
        for (StatTypes statType : StatTypes.values()) {
            int currentValue = stats.getStat(statType).getValue();
            int statPointCost = stats.getLevelSystem().getGainStatPointsForLevel(currentValue);
            
            // Stat name (using aka field - Str, Agi, etc.)
            String statName = statType.getAka().toUpperCase();
            guiGraphics.drawString(this.font, statName, windowX + PADDING, currentY, TEXT_COLOR);
            
            // Current value
            String currentStr = String.valueOf(currentValue);
            int currentX = windowX + 60;
            guiGraphics.drawString(this.font, currentStr, currentX, currentY, VALUE_COLOR);
            
            // Triangle arrow
            String arrow = "▷";
            int arrowX = windowX + 100;
            guiGraphics.drawString(this.font, arrow, arrowX, currentY, ARROW_COLOR);
            
            // Stat point cost
            String costStr = String.valueOf(statPointCost);
            int costX = windowX + 120;
            guiGraphics.drawString(this.font, costStr, costX, currentY, VALUE_COLOR);
            
            currentY += LINE_HEIGHT;
        }
        
        return currentY;
    }
    
    private void drawSeparator(GuiGraphics guiGraphics, int y) {
        guiGraphics.fill(windowX + PADDING, y, windowX + WINDOW_WIDTH - PADDING, y + 1, BORDER_DARK);
    }
    
    private int drawDerivedStats(GuiGraphics guiGraphics, DerivedStatsCalculator.DerivedStats derivedStats, int startY) {
        int currentY = startY;
        int leftColumnX = windowX + PADDING;
        int rightColumnX = windowX + WINDOW_WIDTH / 2 + PADDING;
        
        // Row 1: ATK and DEF
        drawDerivedStat(guiGraphics, "Atk", derivedStats.baseATK, derivedStats.bonusATK, leftColumnX, currentY);
        drawDerivedStat(guiGraphics, "Def", derivedStats.baseDEF, derivedStats.bonusDEF, rightColumnX, currentY);
        currentY += LINE_HEIGHT;
        
        // Row 2: MATK and MDEF
        drawDerivedStat(guiGraphics, "Matk", derivedStats.baseMATK, derivedStats.bonusMATK, leftColumnX, currentY);
        drawDerivedStat(guiGraphics, "Mdef", derivedStats.baseMDEF, derivedStats.bonusMDEF, rightColumnX, currentY);
        currentY += LINE_HEIGHT;
        
        // Row 3: Accuracy and Evasion
        drawSingleStat(guiGraphics, "Accuracy", derivedStats.accuracy, leftColumnX, currentY);
        drawDerivedStat(guiGraphics, "Evasion", derivedStats.baseEvasion, derivedStats.bonusEvasion, rightColumnX, currentY);
        currentY += LINE_HEIGHT;
        
        // Row 4: Critical and Aspd
        drawSingleStat(guiGraphics, "Critical", derivedStats.crit, leftColumnX, currentY);
        drawSingleStat(guiGraphics, "Aspd", derivedStats.aspd, rightColumnX, currentY);
        currentY += LINE_HEIGHT;
        
        return currentY;
    }
    
    private void drawDerivedStat(GuiGraphics guiGraphics, String name, int baseValue, int bonusValue, int x, int y) {
        // Stat name
        guiGraphics.drawString(this.font, name, x, y, TEXT_COLOR);
        
        // Value in "base + bonus" format
        String valueStr = baseValue + " + " + bonusValue;
        int valueX = x + 50;
        guiGraphics.drawString(this.font, valueStr, valueX, y, VALUE_COLOR);
    }
    
    private void drawSingleStat(GuiGraphics guiGraphics, String name, int value, int x, int y) {
        // Stat name
        guiGraphics.drawString(this.font, name, x, y, TEXT_COLOR);
        
        // Value
        String valueStr = String.valueOf(value);
        int valueX = x + 50;
        guiGraphics.drawString(this.font, valueStr, valueX, y, VALUE_COLOR);
    }
    
    private void drawStatusPoints(GuiGraphics guiGraphics, int statPoints, int x, int y) {
        String text = "Status Point       " + statPoints;
        guiGraphics.drawString(this.font, text, x, y, STAT_POINTS_COLOR);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false; // Don't pause the game when this screen is open
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Close on ESC or the status screen key
        if (keyCode == 256) { // ESC key
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}