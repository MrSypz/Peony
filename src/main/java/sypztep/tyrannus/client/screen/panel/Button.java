package sypztep.tyrannus.client.screen.panel;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.Consumer;

public class Button extends AbstractWidget {
    private static final int BUTTON_COLOR_NORMAL = 0xFF666666;
    private static final int BUTTON_COLOR_HOVERED = 0xFF888888;
    private static final int BUTTON_COLOR_DISABLED = 0xFF333333;
    private static final int BUTTON_BORDER_COLOR = 0xFF000000;
    private static final int TEXT_COLOR_NORMAL = 0xFFFFFFFF;
    private static final int TEXT_COLOR_DISABLED = 0xFF999999;
    
    private final Consumer<Button> onClick;
    
    public Button(int x, int y, int width, int height, Component label, Consumer<Button> onClick) {
        super(x, y, width, height, label);
        this.onClick = onClick;
    }
    
    @Override
    public void onClick(double mouseX, double mouseY) {
        if (this.active && this.onClick != null) {
            this.onClick.accept(this);
        }
    }
    
    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int backgroundColor;
        int textColor;
        
        if (!this.active) {
            backgroundColor = BUTTON_COLOR_DISABLED;
            textColor = TEXT_COLOR_DISABLED;
        } else if (this.isHoveredOrFocused()) {
            backgroundColor = BUTTON_COLOR_HOVERED;
            textColor = TEXT_COLOR_NORMAL;
        } else {
            backgroundColor = BUTTON_COLOR_NORMAL;
            textColor = TEXT_COLOR_NORMAL;
        }
        
        // Draw button background
        guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, backgroundColor);
        
        // Draw button border
        guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + 1, BUTTON_BORDER_COLOR);
        guiGraphics.fill(this.getX(), this.getY() + this.height - 1, this.getX() + this.width, this.getY() + this.height, BUTTON_BORDER_COLOR);
        guiGraphics.fill(this.getX(), this.getY(), this.getX() + 1, this.getY() + this.height, BUTTON_BORDER_COLOR);
        guiGraphics.fill(this.getX() + this.width - 1, this.getY(), this.getX() + this.width, this.getY() + this.height, BUTTON_BORDER_COLOR);
        
        // Draw button text
        String buttonText = this.getMessage().getString();
        int textWidth = guiGraphics.guiFont().width(buttonText);
        int textX = this.getX() + (this.width - textWidth) / 2;
        int textY = this.getY() + (this.height - 8) / 2;
        
        guiGraphics.drawString(guiGraphics.guiFont(), buttonText, textX, textY, textColor);
    }
    
    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }
}