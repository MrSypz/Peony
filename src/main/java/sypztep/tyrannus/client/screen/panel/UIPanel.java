package sypztep.tyrannus.client.screen.panel;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;

import java.util.ArrayList;
import java.util.List;

public class UIPanel implements Renderable, GuiEventListener {
    private final List<GuiEventListener> children = new ArrayList<>();
    private final List<Renderable> renderables = new ArrayList<>();
    
    private int x;
    private int y;
    private int width;
    private int height;
    
    public UIPanel(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public void addChild(GuiEventListener child) {
        this.children.add(child);
        if (child instanceof Renderable renderable) {
            this.renderables.add(renderable);
        }
    }
    
    public void removeChild(GuiEventListener child) {
        this.children.remove(child);
        if (child instanceof Renderable renderable) {
            this.renderables.remove(renderable);
        }
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        for (Renderable renderable : this.renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (GuiEventListener child : this.children) {
            if (child.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (GuiEventListener child : this.children) {
            if (child.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (GuiEventListener child : this.children) {
            if (child.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        for (GuiEventListener child : this.children) {
            if (child.mouseScrolled(mouseX, mouseY, deltaX, deltaY)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (GuiEventListener child : this.children) {
            if (child.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        for (GuiEventListener child : this.children) {
            if (child.keyReleased(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for (GuiEventListener child : this.children) {
            if (child.charTyped(codePoint, modifiers)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void setFocused(boolean focused) {
        // UIPanel doesn't handle focus directly
    }
    
    @Override
    public boolean isFocused() {
        return false;
    }
    
    // Getters and setters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    public List<GuiEventListener> getChildren() {
        return new ArrayList<>(children);
    }
}