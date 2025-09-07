package bmatehu.cablemod.network;

import bmatehu.cablemod.CableMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class CableModPacketHandler {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(CableMod.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        INSTANCE.messageBuilder(CableBlockPacket.class, 0, NetworkDirection.PLAY_TO_SERVER)
                .encoder(CableBlockPacket::encode)
                .decoder(CableBlockPacket::new)
                .consumerMainThread(CableBlockPacket::handle)
                .add();
    }

    public static void sendToServer(Object packet) {
        INSTANCE.send(PacketDistributor.SERVER.noArg(), packet);
    }

    public static void sendToAll(Object packet) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), packet);
    }

    public static void sendToPlayer(Object packet, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
}
