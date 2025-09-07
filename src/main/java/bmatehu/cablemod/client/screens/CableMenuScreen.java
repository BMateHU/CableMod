package bmatehu.cablemod.client.screens;

import bmatehu.cablemod.CableMod;
import bmatehu.cablemod.menu.CableMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
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
        addWidget(CableMenu.down_setting.pos(this.leftPos+10, this.topPos+10).build());
        addWidget(CableMenu.up_setting.pos(this.leftPos+28, this.topPos+10).build());
        addWidget(CableMenu.north_setting.pos(this.leftPos+46, this.topPos+10).build());
        addWidget(CableMenu.south_setting.pos(this.leftPos+10, this.topPos+28).build());
        addWidget(CableMenu.east_setting.pos(this.leftPos+28, this.topPos+28).build());
        addWidget(CableMenu.west_setting.pos(this.leftPos+46, this.topPos+28).build());
    }
}
