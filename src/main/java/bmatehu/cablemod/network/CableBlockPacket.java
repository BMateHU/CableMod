package bmatehu.cablemod.network;

import bmatehu.cablemod.blocks.CableBlock;
import bmatehu.cablemod.blocks.CableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class CableBlockPacket {

    private BlockPos pos;
    private List<Enum<RedstoneSide>> side = new ArrayList<>();
    private Direction direction;
    private CableBlock.IOEnergy ioEnergy;

    public CableBlockPacket(BlockPos pos, Direction direction, CableBlock.IOEnergy ioEnergy) {
        this.pos = pos;
        this.direction = direction;
        this.ioEnergy = ioEnergy;
    }

    public CableBlockPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
//        for(int i = 0; i < 6; i++)
//            side.add(buf.readEnum(RedstoneSide.class));
        //this.map = buf.readMap((buf1) -> buf1.readEnum(Direction.class), (buf2) -> buf2.readEnum(CableBlock.IOEnergy.class));
        this.direction = buf.readEnum(Direction.class);
        this.ioEnergy = buf.readEnum(CableBlock.IOEnergy.class);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeEnum(direction);
        buf.writeEnum(ioEnergy);
        //buf.writeMap(map, FriendlyByteBuf::writeEnum, FriendlyByteBuf::writeEnum);
//        buf.writeEnum(state.getValue(CableBlock.NORTH));
//        buf.writeEnum(state.getValue(CableBlock.EAST));
//        buf.writeEnum(state.getValue(CableBlock.SOUTH));
//        buf.writeEnum(state.getValue(CableBlock.WEST));
//        buf.writeEnum(state.getValue(CableBlock.ABOVE));
//        buf.writeEnum(state.getValue(CableBlock.BELOW));
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            ServerPlayer player = contextSupplier.get().getSender();
            ServerLevel level = player.serverLevel();
            if(level.getBlockEntity(pos) instanceof CableBlockEntity cableBlockEntity) {
                cableBlockEntity.getEnabledSides().put(direction, ioEnergy);
                cableBlockEntity.setChanged();
                level.setBlockAndUpdate(pos, level.getBlockState(pos));
                level.setBlockAndUpdate(pos.relative(direction), level.getBlockState(pos.relative(direction)));
            }
//            state = level.getBlockState(pos)
//                    .setValue(CableBlock.NORTH, (RedstoneSide) side.get(0))
//                    .setValue(CableBlock.EAST, (RedstoneSide) side.get(1))
//                    .setValue(CableBlock.SOUTH, (RedstoneSide) side.get(2))
//                    .setValue(CableBlock.WEST, (RedstoneSide) side.get(3))
//                    .setValue(CableBlock.ABOVE, (RedstoneSide) side.get(4))
//                    .setValue(CableBlock.BELOW, (RedstoneSide) side.get(5));

        });

        contextSupplier.get().setPacketHandled(true);
    }
}
