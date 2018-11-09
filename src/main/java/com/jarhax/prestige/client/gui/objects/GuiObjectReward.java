package com.jarhax.prestige.client.gui.objects;

import com.jarhax.prestige.Prestige;
import com.jarhax.prestige.api.Reward;
import com.jarhax.prestige.client.gui.GuiPrestigeBase;
import com.jarhax.prestige.client.utils.RenderUtils;
import com.jarhax.prestige.config.Config;
import com.jarhax.prestige.packet.PacketAttemptPurchase;
import net.minecraft.client.renderer.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class GuiObjectReward extends GuiObject {
    
    private static final ResourceLocation BACKGROUND = new ResourceLocation("prestige", "textures/gui/gui_prestige_icons.png");
    private final Reward reward;
    private boolean purchased;
    private ItemStack renderStack;
    
    public GuiObjectReward(GuiPrestigeBase parent, int x, int y, int width, int height, Reward reward) {
        
        super(parent, x, y, width, height);
        this.reward = reward;
        this.renderStack = reward.getIcon();
    }
    
    public GuiObjectReward(GuiPrestigeBase parent, Reward reward) {
        
        super(parent, reward.getX(), reward.getY(), 32, 32);
        this.reward = reward;
        this.renderStack = reward.getIcon();
    }
    
    @Override
    public void update() {
        
        super.update();
    }
    
    @Override
    public void draw(int left, int top, int mouseX, int mouseY, float partialTicks) {
        
        this.mc.getTextureManager().bindTexture(BACKGROUND);
        if(isPurchased()) {
            float[] argb = Config.getARGB(Config.purchasedColour);
            GlStateManager.color(argb[1], argb[2], argb[3]);
        } else if(!Prestige.clientPlayerData.canPurchase(getReward())) {
            float[] argb = Config.getARGB(Config.unavailableColour);
            GlStateManager.color(argb[1], argb[2], argb[3]);
        }else{
            float[] argb = Config.getARGB(Config.purchaseableColour);
            GlStateManager.color(argb[1], argb[2], argb[3]);
        }
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        
        this.mc.getTextureManager().bindTexture(BACKGROUND);
        RenderUtils.drawTexturedModalRect(getX(), getY(), 0, 0, this.getWidth(), this.getHeight());
        if(isPurchased() || !Prestige.clientPlayerData.canPurchase(getReward())) {
            GlStateManager.color(1, 1f, 1f);
        }
        renderIcon();
        if(collides(mouseX, mouseY, mouseX, mouseY)) {
            drawText(mouseX, mouseY);
        }
    }
    
    
    public void renderIcon() {
        GL11.glPushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glTranslated(getX() + 8, getY() + 8, -50);
        mc.getRenderItem().renderItemIntoGUI(renderStack, 0, 0);
        GL11.glTranslated(-(getX() + 8), -(getY() + 8), 50);
        RenderHelper.disableStandardItemLighting();
        GL11.glPopMatrix();
    }
    
    
    public void drawText(int mouseX, int mouseY) {
        GL11.glPushMatrix();
        GlStateManager.translate(0, 0, 500);
        GlStateManager.disableAlpha();
        GlStateManager.enableLighting();
        List<String> text = new LinkedList<>();
        text.add(reward.getTitle());
        text.add("");
        for(String s : reward.getDescription().split("\\\\n")) {
            text.add("- " + s.replaceAll("\\\\t", "    "));
        }
        text.add("- costs: " + reward.getCost());
        
        getParent().drawHoveringText(text, mouseX, mouseY);
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
        GL11.glPopMatrix();
    }
    
    
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if(collides(mouseX, mouseY, mouseX, mouseY)) {
            if(Prestige.clientPlayerData.canPurchase(getReward())) {
                setPurchased(true);
                parent.getRewardsToGive().add(this);
                Prestige.NETWORK.sendToServer(new PacketAttemptPurchase(getReward()));
            }
        }
    }
    
    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        addX(-(((float) this.getParent().getPrevMX() - mouseX)) / 1.5f);
        addY(-(((float) this.getParent().getPrevMY() - mouseY)) / 1.5f);
        
        setX(Math.round(x));
        setY(Math.round(y));
    }
    
    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        
        super.mouseReleased(mouseX, mouseY, state);
        
    }
    
    public Reward getReward() {
        return reward;
    }
    
    public boolean isPlaced() {
        return reward.isPlaced();
    }
    
    public boolean isPurchased() {
        return purchased;
    }
    
    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }
}
