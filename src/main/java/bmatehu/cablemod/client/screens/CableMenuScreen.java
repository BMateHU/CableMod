package bmatehu.cablemod.client.screens;

import bmatehu.cablemod.CableMod;
import bmatehu.cablemod.menu.CableMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

///Client-side screen for the CableMenu
public class CableMenuScreen extends AbstractContainerScreen<CableMenu> {

    /// Texture for the screen
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CableMod.MODID, "textures/gui/cable_menu.png");

    /**
     * Default constructor -> registered in ClientModEvents
     * @param cableMenu The server-side menu, which is displayed on the screen
     * @param inventory Inventory, which to be displayed on the screen
     * @param component Title of the screen
     */
    public CableMenuScreen(CableMenu cableMenu, Inventory inventory, Component component) {
        super(cableMenu, inventory, component);

        this.imageWidth = 176;
        this.imageHeight = 222;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    ///Renders the background texture
    @Override
    protected void renderBg(@NotNull GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        renderBackground(pGuiGraphics);
        pGuiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    /// Renders plus widgets, tooltip
    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int mouseX, int mouseY, float pPartialTick) {
        super.render(pGuiGraphics, mouseX, mouseY, pPartialTick);
        renderTooltip(pGuiGraphics, mouseX, mouseY);

        CableMenu.down_setting.setPosition(this.leftPos+100, this.topPos+80);
        CableMenu.up_setting.setPosition(this.leftPos+100, this.topPos+40);
        CableMenu.north_setting.setPosition(this.leftPos+75, this.topPos+40);
        CableMenu.south_setting.setPosition(this.leftPos+75, this.topPos+80);
        CableMenu.east_setting.setPosition(this.leftPos+100, this.topPos+60);
        CableMenu.west_setting.setPosition(this.leftPos+50, this.topPos+60);

        if(CableMenu.down_setting.isHovered()) {
            pGuiGraphics.renderTooltip(this.font, Component.literal("Down"), mouseX, mouseY);
        }
        if(CableMenu.up_setting.isHovered()) {
            pGuiGraphics.renderTooltip(this.font, Component.literal("Up"), mouseX, mouseY);
        }
        if(CableMenu.north_setting.isHovered()) {
            pGuiGraphics.renderTooltip(this.font, Component.literal("North"), mouseX, mouseY);
        }
        if(CableMenu.south_setting.isHovered()) {
            pGuiGraphics.renderTooltip(this.font, Component.literal("South"), mouseX, mouseY);
        }
        if(CableMenu.east_setting.isHovered()) {
            pGuiGraphics.renderTooltip(this.font, Component.literal("East"), mouseX, mouseY);
        }
        if(CableMenu.west_setting.isHovered()) {
            pGuiGraphics.renderTooltip(this.font, Component.literal("West"), mouseX, mouseY);
        }

        addRenderableWidget(CableMenu.down_setting);
        addRenderableWidget(CableMenu.up_setting);
        addRenderableWidget(CableMenu.north_setting);
        addRenderableWidget(CableMenu.south_setting);
        addRenderableWidget(CableMenu.east_setting);
        addRenderableWidget(CableMenu.west_setting);
    }
}
