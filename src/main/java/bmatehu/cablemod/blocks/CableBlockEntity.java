package bmatehu.cablemod.blocks;

import bmatehu.cablemod.CableMod;
import bmatehu.cablemod.energy.EnergyHelper;
import bmatehu.cablemod.menu.CableMenu;
import bmatehu.cablemod.registers.EBlockEntityTypes;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class CableBlockEntity extends BlockEntity implements MenuProvider {

    protected EnergyHelper energy = new EnergyHelper(10000, 1000, 1000);
    private final LazyOptional<EnergyStorage> energyCap = LazyOptional.of(() -> energy);
    private final Map<Direction, CableBlock.IOEnergy> enabledSides = new LinkedHashMap<>();

    //private final BiMap<BlockEntity, Integer> blockEntities = HashBiMap.create();

    private final ContainerData containerData = new ContainerData() {

        @Override
        public int get(int p_39284_) {
            return switch (p_39284_) {
                case 0 -> energy.getEnergyStored();
                case 1 -> energy.getMaxEnergyStored();
                case 2 -> CableBlockEntity.this.worldPosition.getX();
                case 3 -> CableBlockEntity.this.worldPosition.getY();
                case 4 -> CableBlockEntity.this.worldPosition.getZ();
                default -> throw new IllegalStateException("Unexpected value: " + p_39284_);
            };
        }

        @Override
        public void set(int p_39285_, int p_39286_) {
            switch (p_39285_) {
                case 0 -> energy.setEnergy(p_39286_);
                default -> throw new IllegalStateException("Unexpected value: " + p_39285_);
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public CableBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(EBlockEntityTypes.CABLE_BLOCK.get(), pPos, pBlockState);
        for(Direction direction : Direction.values())
            enabledSides.put(direction, CableBlock.IOEnergy.INPUT_OUTPUT);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);

        CompoundTag data = new CompoundTag();
        data.put("energy", this.energy.serializeNBT());
        for(EnumProperty<RedstoneSide> prop : CableBlock.getSides()) {
            data.putInt(prop.getName(), this.getBlockState().getValue(prop).ordinal());
        }
        for(Direction direction : Direction.values()) {
            data.putInt(direction.getName(), this.enabledSides.get(direction).ordinal());
        }
        nbt.put(CableMod.MODID, data);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        BlockState state = this.getBlockState();
        CompoundTag data = nbt.getCompound(CableMod.MODID);

        if(data.contains("energy", Tag.TAG_INT))
            this.energy.deserializeNBT(data.get("energy"));

        for(EnumProperty<RedstoneSide> prop : CableBlock.getSides())
            if(data.contains("below", Tag.TAG_INT))
                state.setValue(prop, data.get(prop.getName()).equals(2) ? RedstoneSide.NONE : RedstoneSide.SIDE);

        for(Direction direction : Direction.values()) {
            this.enabledSides.put(direction, CableBlock.IOEnergy.values()[data.getInt(direction.getName())]);
        }

        this.setChanged();
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        saveAdditional(nbt);
        return nbt;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ENERGY) {
            if (this.enabledSides.get(side).equals(CableBlock.IOEnergy.INPUT_OUTPUT)) {
                return getEnergyCap().cast();
            }
            else
                return LazyOptional.empty();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyCap.invalidate();
    }

    public EnergyStorage getEnergy() {
        return energy;
    }

    public LazyOptional<EnergyStorage> getEnergyCap() {
        return energyCap;
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, T t) {
        if(!level.isClientSide()) {
            for(Direction direction : Direction.values()) {
                LazyOptional<IEnergyStorage> originalCap = t.getCapability(ForgeCapabilities.ENERGY, direction);
                BlockEntity bl = level.getBlockEntity(blockPos.relative(direction));
                if(bl == null)
                    continue;
                LazyOptional<IEnergyStorage> neighCap = bl.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite());
                if (neighCap.isPresent() && originalCap.isPresent()) {
 //                   if (originalCap.canExtract())
 //                       aboveBlock.receiveEnergy(currentBlock.extractEnergy(1000, false), false);
                }
            }
        }
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("menu.examplemod.cable_menu");
    }

    @ParametersAreNonnullByDefault
    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player player) {
        return new CableMenu(pContainerId, pPlayerInventory, this, this.containerData);
    }

    public Map<Direction, CableBlock.IOEnergy> getEnabledSides() {
        return enabledSides;
    }

   // public BiMap<BlockEntity, Integer> getBlockEntities() {
   //     return blockEntities;
   // }

   // public void setBlockEntities(BiMap<BlockEntity, Integer> blockEntities) {
   //     this.blockEntities = blockEntities;
   // }
}
