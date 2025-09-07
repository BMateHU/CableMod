package bmatehu.cablemod.energy;

import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyHelper extends EnergyStorage {

    public EnergyHelper(int capacity) {
        super(capacity);
    }

    public EnergyHelper(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public EnergyHelper(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public EnergyHelper(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return super.receiveEnergy(maxReceive, simulate);
    }

    public int receiveEnergy(int maxReceive, boolean simulate, BlockEntity fromEntity, Direction side) {
        if(fromEntity.getCapability(ForgeCapabilities.ENERGY, side).isPresent()) {
            return super.receiveEnergy(((IEnergyStorage) fromEntity).extractEnergy(maxReceive, simulate), simulate);
        }
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return super.extractEnergy(maxExtract, simulate);
    }

    public int extractEnergy(int maxExtract, boolean simulate, BlockEntity destEntity, Direction side) {
        int energy = super.extractEnergy(maxExtract, simulate);
        if(destEntity.getCapability(ForgeCapabilities.ENERGY, side).isPresent())
            return ((IEnergyStorage)destEntity).receiveEnergy(energy, false);
        return 0;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setReceive(int receive) {
        this.maxReceive = Math.min(capacity, receive);
    }

    public void setExtract(int extract) {
        this.maxExtract = Math.min(capacity, extract);
    }

    @Override
    public int getEnergyStored() {
        return super.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return super.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return super.canExtract();
    }

    @Override
    public boolean canReceive() {
        return super.canReceive();
    }

    @Override
    public Tag serializeNBT() {
        return super.serializeNBT();
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        super.deserializeNBT(nbt);
    }
}
