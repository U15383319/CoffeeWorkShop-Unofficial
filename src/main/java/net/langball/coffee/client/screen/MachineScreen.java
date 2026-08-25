package net.langball.coffee.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.langball.coffee.CoffeeWork;
import net.langball.coffee.menu.MachineGuiLayout;
import net.langball.coffee.menu.MachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class MachineScreen extends AbstractContainerScreen<MachineMenu> {
    public MachineScreen(MachineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        MachineGuiLayout layout = menu.layout();
        ResourceLocation texture = new ResourceLocation(
                CoffeeWork.MODID, "textures/gui/" + layout.textureName() + ".png");
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(texture, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int burn = menu.burnPixels(13);
        if (burn > 0 && layout.hasBurnIndicator()) {
            graphics.blit(texture,
                    leftPos + layout.burnX(), topPos + layout.burnBaseY() + 12 - burn,
                    176, 12 - burn, 14, burn + 1);
        }
        int progress = menu.progressPixels(layout.progressWidth());
        if (progress > 0) {
            graphics.blit(texture,
                    leftPos + layout.progressX(), topPos + layout.progressY(),
                    176, 14, progress + 1, layout.progressHeight());
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
