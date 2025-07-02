package sypztep.peony.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import sypztep.peony.common.attachment.LivingEntityStatsAttachment;
import sypztep.peony.common.init.ModAttachments;
import sypztep.peony.module.level.StatTypes;
import sypztep.peony.module.level.Stat;
import sypztep.tyrannus.client.screen.BaseScreen;
import sypztep.tyrannus.client.screen.panel.Button;
import sypztep.tyrannus.client.screen.panel.UIPanel;

import java.util.ArrayList;
import java.util.List;

public class StatsUpdateScreen extends BaseScreen {
    private static final int PANEL_WIDTH = 300;
    private static final int PANEL_HEIGHT = 350;
    private static final int STAT_ROW_HEIGHT = 25;
    private static final int BUTTON_WIDTH = 20;
    private static final int BUTTON_HEIGHT = 15;
    
    // Colors matching XpHudOverlay style
    private static final int BACKGROUND_COLOR = 0xA0000000;
    private static final int BORDER_COLOR = 0xFF444444;
    private static final int HEADER_COLOR = 0xFF555555;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int STAT_POINTS_COLOR = 0xFFFFD700;
    private static final int STAT_VALUE_COLOR = 0xFF00CC00;
    
    private LivingEntityStatsAttachment statsAttachment;
    private List<Button> upgradeButtons = new ArrayList<>();
    private int hoveredStatIndex = -1;
    private UIPanel mainPanel;

    public StatsUpdateScreen() {
        super(Component.literal("Character Stats"));
    }

    @Override
    protected void init() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            onClose();
            return;
        }

        this.statsAttachment = player.getData(ModAttachments.LIVINGSTATS.get());
        if (statsAttachment == null) {
            onClose();
            return;
        }

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;
        
        // Create main UI panel
        this.mainPanel = new UIPanel(panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT);
        this.addPanel(mainPanel);
        
        // Create upgrade buttons for each stat
        upgradeButtons.clear();
        StatTypes[] statTypes = StatTypes.values();
        
        for (int i = 0; i < statTypes.length; i++) {
            int statIndex = i;
            StatTypes statType = statTypes[i];
            int buttonX = panelX + PANEL_WIDTH - 30;
            int buttonY = panelY + 60 + (i * STAT_ROW_HEIGHT) + 5;
            
            Button upgradeButton = new Button(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT, 
                    Component.literal("+"), button -> upgradeStatPoint(statType));
            
            upgradeButtons.add(upgradeButton);
            this.mainPanel.addChild(upgradeButton);
        }
        
        // Close button
        Button closeButton = new Button(panelX + PANEL_WIDTH - 60, panelY + PANEL_HEIGHT - 30, 50, 20,
                Component.literal("Close"), button -> onClose());
        this.mainPanel.addChild(closeButton);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Background overlay
        guiGraphics.fill(0, 0, this.width, this.height, 0x80000000);
        
        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;
        
        // Main panel background
        guiGraphics.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, BACKGROUND_COLOR);
        drawBorder(guiGraphics, panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT);
        
        // Header
        guiGraphics.fill(panelX + 1, panelY + 1, panelX + PANEL_WIDTH - 1, panelY + 35, HEADER_COLOR);
        
        // Title
        Component title = Component.literal("Character Stats").withStyle(ChatFormatting.BOLD);
        int titleWidth = this.font.width(title);
        guiGraphics.drawString(this.font, title, panelX + (PANEL_WIDTH - titleWidth) / 2, panelY + 10, TEXT_COLOR);
        
        // Available stat points
        int availablePoints = statsAttachment.getStatPoints();
        Component pointsText = Component.literal("Available Points: ")
                .append(Component.literal(String.valueOf(availablePoints)).withStyle(ChatFormatting.GOLD));
        guiGraphics.drawString(this.font, pointsText, panelX + 10, panelY + 22, TEXT_COLOR);
        
        // Render stats
        renderStats(guiGraphics, panelX, panelY, mouseX, mouseY);
        
        // Update button states
        updateButtonStates();
        
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        // Render stat description tooltip
        renderStatTooltip(guiGraphics, mouseX, mouseY);
    }

    private void renderStats(GuiGraphics guiGraphics, int panelX, int panelY, int mouseX, int mouseY) {
        if (statsAttachment == null) return;
        
        StatTypes[] statTypes = StatTypes.values();
        hoveredStatIndex = -1;
        
        for (int i = 0; i < statTypes.length; i++) {
            StatTypes statType = statTypes[i];
            Stat stat = statsAttachment.getLivingStats().getStat(statType);
            
            if (stat == null) continue; // Skip if stat is null
            
            int y = panelY + 60 + (i * STAT_ROW_HEIGHT);
            int x = panelX + 10;
            
            // Check if mouse is hovering over this stat row
            if (mouseX >= x && mouseX <= panelX + PANEL_WIDTH - 40 && 
                mouseY >= y && mouseY <= y + STAT_ROW_HEIGHT) {
                hoveredStatIndex = i;
                // Highlight background
                guiGraphics.fill(x - 5, y, panelX + PANEL_WIDTH - 35, y + STAT_ROW_HEIGHT, 0x40FFFFFF);
            }
            
            // Stat name
            String statName = statType.getName().substring(0, 1).toUpperCase() + 
                            statType.getName().substring(1);
            guiGraphics.drawString(this.font, statName, x, y + 8, TEXT_COLOR);
            
            // Stat value
            String statValue = String.valueOf(stat.getValue());
            int valueX = panelX + 150;
            guiGraphics.drawString(this.font, statValue, valueX, y + 8, STAT_VALUE_COLOR);
            
            // Cost to upgrade (if has points)
            if (statsAttachment.getStatPoints() > 0) {
                int cost = stat.getIncreasePerPoint();
                String costText = "(" + cost + ")";
                int costX = panelX + PANEL_WIDTH - 80;
                guiGraphics.drawString(this.font, costText, costX, y + 8, 0xFFAAAAAA);
            }
        }
    }

    private void renderStatTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (hoveredStatIndex >= 0 && hoveredStatIndex < StatTypes.values().length && statsAttachment != null) {
            StatTypes statType = StatTypes.values()[hoveredStatIndex];
            Stat stat = statsAttachment.getLivingStats().getStat(statType);
            
            if (stat != null) {
                List<Component> tooltip = new ArrayList<>();
                tooltip.add(Component.literal(statType.getName().toUpperCase()).withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD));
                
                // Add current value
                tooltip.add(Component.literal("Current Value: " + stat.getValue()).withStyle(ChatFormatting.WHITE));
                
                // Add cost information
                int cost = stat.getIncreasePerPoint();
                tooltip.add(Component.literal("Cost to Upgrade: " + cost + " points").withStyle(ChatFormatting.YELLOW));
                
                // Add empty line
                tooltip.add(Component.empty());
                
                // Add stat description with +1 point preview
                List<Component> effectDescription = stat.getEffectDescription(1);
                if (effectDescription != null) {
                    tooltip.addAll(effectDescription);
                }
                
                // Render tooltip
                guiGraphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
            }
        }
    }

    private void updateButtonStates() {
        if (statsAttachment == null) return;
        
        int availablePoints = statsAttachment.getStatPoints();
        StatTypes[] statTypes = StatTypes.values();
        
        for (int i = 0; i < upgradeButtons.size() && i < statTypes.length; i++) {
            Button button = upgradeButtons.get(i);
            Stat stat = statsAttachment.getLivingStats().getStat(statTypes[i]);
            if (stat != null) {
                int cost = stat.getIncreasePerPoint();
                button.active = availablePoints >= cost;
            } else {
                button.active = false;
            }
        }
    }

    private void upgradeStatPoint(StatTypes statType) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || statsAttachment == null) return;
        
        Stat stat = statsAttachment.getLivingStats().getStat(statType);
        int cost = stat.getIncreasePerPoint();
        
        if (statsAttachment.getStatPoints() >= cost) {
            // Use the attachment's method which handles sync automatically
            statsAttachment.useStatPoint(player, statType, 1);
            
            // Play sound effect
            player.playSound(net.minecraft.sounds.SoundEvents.EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
        }
    }

    private void drawBorder(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        // Draw border like XpHudOverlay
        guiGraphics.fill(x, y, x + width, y + 1, BORDER_COLOR);
        guiGraphics.fill(x, y + height - 1, x + width, y + height, BORDER_COLOR);
        guiGraphics.fill(x, y, x + 1, y + height, BORDER_COLOR);
        guiGraphics.fill(x + width - 1, y, x + width, y + height, BORDER_COLOR);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // Don't pause the game
    }
}