package sypztep.tyrannus.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import sypztep.tyrannus.client.screen.panel.UIPanel;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseScreen extends Screen {
    private final List<UIPanel> panels = new ArrayList<>();
    
    protected BaseScreen(Component title) {
        super(title);
    }
    
    public void addPanel(UIPanel panel) {
        this.panels.add(panel);
    }
    
    public void removePanel(UIPanel panel) {
        this.panels.remove(panel);
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Render panels first
        for (UIPanel panel : this.panels) {
            panel.render(guiGraphics, mouseX, mouseY, partialTick);
        }
        
        // Then render the standard screen components
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Check panels first
        for (UIPanel panel : this.panels) {
            if (panel.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        
        // Then check standard screen components
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        // Check panels first
        for (UIPanel panel : this.panels) {
            if (panel.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        
        // Then check standard screen components
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        // Check panels first
        for (UIPanel panel : this.panels) {
            if (panel.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                return true;
            }
        }
        
        // Then check standard screen components
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        // Check panels first
        for (UIPanel panel : this.panels) {
            if (panel.mouseScrolled(mouseX, mouseY, deltaX, deltaY)) {
                return true;
            }
        }
        
        // Then check standard screen components
        return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Check panels first
        for (UIPanel panel : this.panels) {
            if (panel.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        
        // Then check standard screen components
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        // Check panels first
        for (UIPanel panel : this.panels) {
            if (panel.keyReleased(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        
        // Then check standard screen components
        return super.keyReleased(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        // Check panels first
        for (UIPanel panel : this.panels) {
            if (panel.charTyped(codePoint, modifiers)) {
                return true;
            }
        }
        
        // Then check standard screen components
        return super.charTyped(codePoint, modifiers);
    }
    
    protected List<UIPanel> getPanels() {
        return new ArrayList<>(panels);
    }
}