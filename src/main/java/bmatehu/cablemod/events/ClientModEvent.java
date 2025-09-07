package bmatehu.cablemod.events;

import bmatehu.cablemod.CableMod;
import bmatehu.cablemod.registers.EMenuTypes;
import bmatehu.cablemod.client.screens.CableMenuScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = CableMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvent {

    @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            event.enqueueWork(() -> {
                MenuScreens.register(EMenuTypes.CABLE_MENU.get(), CableMenuScreen::new);
            });
        }
}
