package bmatehu.cablemod.blocks;

import bmatehu.cablemod.registers.EBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

public class CableBlock extends Block implements EntityBlock {

    public static final EnumProperty<RedstoneSide> NORTH = BlockStateProperties.NORTH_REDSTONE;
    public static final EnumProperty<RedstoneSide> SOUTH = BlockStateProperties.SOUTH_REDSTONE;
    public static final EnumProperty<RedstoneSide> EAST = BlockStateProperties.EAST_REDSTONE;
    public static final EnumProperty<RedstoneSide> WEST = BlockStateProperties.WEST_REDSTONE;
    public static final EnumProperty<RedstoneSide> ABOVE = EnumProperty.create("above", RedstoneSide.class);
    public static final EnumProperty<RedstoneSide> BELOW = EnumProperty.create("below", RedstoneSide.class);

    public CableBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, RedstoneSide.NONE)
                .setValue(SOUTH, RedstoneSide.NONE)
                .setValue(EAST, RedstoneSide.NONE)
                .setValue(WEST, RedstoneSide.NONE)
                .setValue(ABOVE, RedstoneSide.NONE)
                .setValue(BELOW, RedstoneSide.NONE));
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return makeShape(pState);
    }

    @Override
    public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return 1.0f;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, ABOVE, BELOW);
    }

    public static EnumProperty<RedstoneSide> getSideProperty(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            case UP -> ABOVE;
            case DOWN -> BELOW;
        };
    }

    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
        List<Direction> directions = new ArrayList<>();
        for(Direction direction : Direction.values()) {
            BlockEntity beNeigh = pLevel.getBlockEntity(pPos.relative(direction));
            BlockEntity be = pLevel.getBlockEntity(pPos);
            if(beNeigh != null && be != null)
                if(beNeigh.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).isPresent() && be.getCapability(ForgeCapabilities.ENERGY, direction).isPresent())
                    directions.add(direction);
        }
        BlockState state = this.defaultBlockState();
        for(Direction direction : directions) {
            state = state.setValue(getSideProperty(direction), RedstoneSide.SIDE);
        }
        pLevel.setBlock(pPos, state, Block.UPDATE_CLIENTS);
    }

    @ParametersAreNonnullByDefault
    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState state) {
        return new CableBlockEntity(blockPos, state);
    }

    @ParametersAreNonnullByDefault
    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType) {
        return entityType == EBlockEntityTypes.CABLE_BLOCK.get() ? CableBlockEntity::tick : null;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext placeContext) {
        List<Direction> directions = new ArrayList<>();
        for(Direction direction : Direction.values()) {
            BlockEntity beNeigh = placeContext.getLevel().getBlockEntity(placeContext.getClickedPos().relative(direction));
            BlockEntity be = placeContext.getLevel().getBlockEntity(placeContext.getClickedPos());
            if(beNeigh != null && be != null)
                if(beNeigh.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).isPresent() && be.getCapability(ForgeCapabilities.ENERGY, direction).isPresent())
                    directions.add(direction);
        }
        BlockState state = this.defaultBlockState();
        for(Direction direction : directions) {
            state = state.setValue(getSideProperty(direction), RedstoneSide.SIDE);
        }
        return state;
    }

    @ParametersAreNonnullByDefault
    @Override
    @SuppressWarnings("deprecation")
    public @NotNull InteractionResult use( BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if(hand == InteractionHand.MAIN_HAND && !level.isClientSide) {
            if (level.getBlockEntity(pos) instanceof CableBlockEntity cableBlockEntity) {
                if (player.isShiftKeyDown()) {
                    cableBlockEntity.energy.receiveEnergy(100, false);
                    cableBlockEntity.setChanged();
                    player.sendSystemMessage(Component.literal("Received 100"));
                    player.sendSystemMessage(Component.literal("Energy: " + cableBlockEntity.energy.getEnergyStored()));
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
                if (player instanceof ServerPlayer serverPlayer) {
                    NetworkHooks.openScreen(serverPlayer, cableBlockEntity, pos);
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }
        return super.use(state, level, pos, player, hand, result);
    }

    public static Collection<EnumProperty<RedstoneSide>> getSides() {
        return Arrays.asList(NORTH, SOUTH, EAST, WEST, ABOVE, BELOW);
    }

    public enum IOEnergy {
        INPUT_OUTPUT, NONE;
    }

    public VoxelShape makeShape(BlockState state){
        VoxelShape shape = Shapes.box(0.375,0.375,0.375,0.625,0.625,0.625);
        shape = Shapes.or(shape, Shapes.box(0.390625,0.3125,0.390625,0.609375,0.6875,0.609375));
        shape = Shapes.or(shape, Shapes.box(0.3125,0.390625,0.390625,0.6875,0.609375,0.609375));
        shape = Shapes.or(shape, Shapes.box(0.390625,0.390625,0.3125,0.609375,0.609375,0.6875));
        if(state.getValue(NORTH) == RedstoneSide.SIDE) {
            shape = Shapes.or(shape, Shapes.box(0.390625,0.390625,0,0.609375,0.609375,0.3125));
        }
        if(state.getValue(SOUTH) == RedstoneSide.SIDE) {
            shape = Shapes.or(shape, Shapes.box(0.390625,0.390625,0.6875,0.609375,0.609375,1));
        }
        if(state.getValue(EAST) == RedstoneSide.SIDE) {
            shape = Shapes.or(shape, Shapes.box(0.6875,0.390625,0.390625,1,0.609375,0.609375));
        }
        if(state.getValue(WEST) == RedstoneSide.SIDE) {
            shape = Shapes.or(shape, Shapes.box(0,0.390625,0.390625,0.3125,0.609375,0.609375));
        }
        if(state.getValue(ABOVE) == RedstoneSide.SIDE) {
            shape = Shapes.or(shape, Shapes.box(0.390625,0.6875,0.390625,0.609375,1,0.609375));
        }
        if(state.getValue(BELOW) == RedstoneSide.SIDE) {
            shape = Shapes.or(shape, Shapes.box(0.390625,0,0.390625,0.609375,0.3125,0.609375));
        }
        return shape;
    }
}
