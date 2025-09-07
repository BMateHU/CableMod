package bmatehu.cablemod.events;

import bmatehu.cablemod.CableMod;
import bmatehu.cablemod.network.CableModPacketHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = CableMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonModEvents {

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event)
    {
        event.enqueueWork(CableModPacketHandler::register);
    }
}
