package com.bmatehu.examplemod.blocks;

import com.bmatehu.examplemod.ExampleMod;
import com.bmatehu.examplemod.registers.EBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CableBlockEntity extends BlockEntity implements IEnergyStorage {

    protected int energy = 0;
    protected int capacity = 200000;
    protected int maxReceive = 1000;
    protected int maxExtract = 1000;

    public CableBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(EBlockEntityTypes.CABLE_BLOCK.get(), p_155229_, p_155230_);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);

        CompoundTag data = nbt.getCompound(ExampleMod.MODID);
        data.putInt("energy", energy);
        data.putInt("capacity", capacity);
        data.putInt("max_receive", maxReceive);
        data.putInt("max_extract", maxExtract);
        nbt.put(ExampleMod.MODID, data);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        CompoundTag data = nbt.getCompound(ExampleMod.MODID);
        energy = data.getInt("energy");
        capacity = data.getInt("capacity");
        maxReceive = data.getInt("max_receive");
        maxExtract = data.getInt("max_extract");
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if(!canReceive())
            return 0;
        int energyReceived = Math.min(capacity - energy, Math.min(maxReceive, this.maxReceive));
        if(!simulate) {
            energy += energyReceived;
            setChanged();
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if(!canExtract())
            return 0;
        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if(!simulate) {
            energy -= energyExtracted;
            setChanged();
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        return energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return energy > 0;
    }

    @Override
    public boolean canReceive() {
        return capacity - energy > 0;
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, T t) {
        if(t instanceof IEnergyStorage currentBlock) {
            for(Direction direction : Direction.values()) {
                BlockEntity bl = level.getBlockEntity(blockPos.relative(direction));

                if (bl instanceof IEnergyStorage aboveBlock) {
                    if (currentBlock.canExtract())
                        aboveBlock.receiveEnergy(currentBlock.extractEnergy(10, false), false);
                }
            }
        }
    }
}
