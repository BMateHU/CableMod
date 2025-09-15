package bmatehu.cablemod.menu;

import bmatehu.cablemod.CableMod;
import bmatehu.cablemod.blocks.CableBlock;
import bmatehu.cablemod.blocks.CableBlockEntity;
import bmatehu.cablemod.network.CableBlockPacket;
import bmatehu.cablemod.network.CableModPacketHandler;
import bmatehu.cablemod.registers.EBlocks;
import bmatehu.cablemod.registers.EMenuTypes;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Map;

public class CableMenu extends AbstractContainerMenu {

    public static ContainerLevelAccess access;

    public static ImageButton north_setting;
    public static ImageButton south_setting;
    public static ImageButton east_setting;
    public static ImageButton west_setting;
    public static ImageButton up_setting;
    public static ImageButton down_setting;

    private ContainerData data;

    //client side constructor
    public CableMenu(int containerId, Inventory inventory, FriendlyByteBuf additionalData) {
        this(containerId, inventory, inventory.player.level().getBlockEntity(additionalData.readBlockPos()), new SimpleContainerData(5));
    }

    //server side constructor
    public CableMenu(int containerId, Inventory inventory, BlockEntity blockEntity, ContainerData data) {
        super(EMenuTypes.CABLE_MENU.get(), containerId);

        if(!(blockEntity instanceof CableBlockEntity)) {
            return;
        }

        this.data = data;
        CableBlockEntity cableBlockEntity = inventory.player.level().getBlockEntity(getPos()) instanceof CableBlockEntity ? (CableBlockEntity) inventory.player.level().getBlockEntity(getPos()) : (CableBlockEntity) blockEntity;

        access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        createPlayerHotbar(inventory);
        createPlayerInventory(inventory);
        initButtons(cableBlockEntity);

        inventory.player.sendSystemMessage(Component.literal("Energy: " + cableBlockEntity.getEnergy().getEnergyStored()));

        addDataSlots(data);
    }

    private void createPlayerHotbar(Inventory playerInventory) {
        for(int j = 0; j < 9; ++j) {
            addSlot(new Slot(playerInventory, j, 8 + j * 18, 198));
        }
    }

    private void createPlayerInventory(Inventory playerInventory) {
        for(int j = 0; j < 9; ++j) {
            for(int i = 0; i < 3; ++i) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 140 + i * 18));
            }
        }
    }

    private void initButtons(CableBlockEntity cableBlockEntity) {
        north_setting = new ImageButton(0, 0, 20, 18, 0, 0, 19,
                ResourceLocation.fromNamespaceAndPath(CableMod.MODID, "textures/gui/north_button.png"),
                button -> stateChanger(Direction.NORTH, cableBlockEntity));
        south_setting = new ImageButton(0, 0, 20, 18, 0, 0, 19,
                ResourceLocation.fromNamespaceAndPath(CableMod.MODID, "textures/gui/south_button.png"),
                button -> stateChanger(Direction.SOUTH, cableBlockEntity));
        east_setting = new ImageButton(0, 0, 20, 18, 0, 0, 19,
                ResourceLocation.fromNamespaceAndPath(CableMod.MODID, "textures/gui/east_button.png"),
                button -> stateChanger(Direction.EAST, cableBlockEntity));
        west_setting = new ImageButton(0, 0, 20, 18, 0, 0, 19,
                ResourceLocation.fromNamespaceAndPath(CableMod.MODID, "textures/gui/west_button.png"),
                button -> stateChanger(Direction.WEST, cableBlockEntity));
        up_setting = new ImageButton(0, 0, 20, 18, 0, 0, 19,
                ResourceLocation.fromNamespaceAndPath(CableMod.MODID, "textures/gui/up_button.png"),
                button -> stateChanger(Direction.UP, cableBlockEntity));
        down_setting = new ImageButton(0, 0, 20, 18, 0, 0, 19,
                ResourceLocation.fromNamespaceAndPath(CableMod.MODID, "textures/gui/down_button.png"),
                button -> stateChanger(Direction.DOWN, cableBlockEntity));
    }

    private static void stateChanger(Direction property, CableBlockEntity cableBlockEntity) {
        Map<Direction, CableBlock.IOEnergy> sides = cableBlockEntity.getEnabledSides();
        CableBlock.IOEnergy value = sides.get(property).equals(CableBlock.IOEnergy.INPUT_OUTPUT) ? CableBlock.IOEnergy.NONE : CableBlock.IOEnergy.INPUT_OUTPUT;
        CableModPacketHandler.sendToServer(new CableBlockPacket(cableBlockEntity.getBlockPos(), property, value));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (pIndex == 0) {
                if (!this.moveItemStackTo(itemstack1, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (pIndex >= 1 && pIndex < 5) {
                if (!this.moveItemStackTo(itemstack1, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (pIndex >= 5 && pIndex < 9) {
                if (!this.moveItemStackTo(itemstack1, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (pIndex >= 9 && pIndex < 36) {
                if (!this.moveItemStackTo(itemstack1, 36, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (pIndex >= 36 && pIndex < 45) {
                if (!this.moveItemStackTo(itemstack1, 9, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 9, 45, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
            if (pIndex == 0) {
                player.drop(itemstack1, false);
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return AbstractContainerMenu.stillValid(access, player, EBlocks.CABLE_BLOCK.get());
    }

    public int getEnergy() {
        return this.data.get(0);
    }

    public int getCapacity() {
        return this.data.get(1);
    }

    public BlockPos getPos() {
        return new BlockPos(this.data.get(2), this.data.get(3), this.data.get(4));
    }
}
