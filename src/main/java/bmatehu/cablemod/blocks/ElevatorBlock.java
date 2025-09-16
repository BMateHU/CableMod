package bmatehu.cablemod.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ElevatorBlock extends Block {

    public ElevatorBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
        for(int i = -64; i <= 320; i++) {
            if(pLevel.getBlockState(new BlockPos(pPos.getX(), i, pPos.getZ())).getBlock() instanceof ElevatorBlock) {
                if(pLevel.isEmptyBlock(new BlockPos(pPos.getX(), i+1, pPos.getZ())) && pLevel.isEmptyBlock(new BlockPos(pPos.getX(), i+2, pPos.getZ())) && pEntity.isShiftKeyDown()) {
                    pEntity.setPos(pEntity.getX(), i + 1, pEntity.getZ());
                    return;
                }
            }
        }
    }
}
