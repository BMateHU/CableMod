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
        this.direction = buf.readEnum(Direction.class);
        this.ioEnergy = buf.readEnum(CableBlock.IOEnergy.class);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeEnum(direction);
        buf.writeEnum(ioEnergy);
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
        });

        contextSupplier.get().setPacketHandled(true);
    }
}
