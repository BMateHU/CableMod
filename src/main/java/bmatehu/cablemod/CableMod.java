package bmatehu.cablemod;

import bmatehu.cablemod.registers.*;
import bmatehu.cablemod.config.Config;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CableMod.MODID)
public class CableMod
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "cablemod";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public CableMod(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        // Register the Deferred Register to the mod event bus so blocks get registered
        EBlocks.REGISTER.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        EItems.REGISTER.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        ECreativeTabs.REGISTER.register(modEventBus);

        EBlockEntityTypes.REGISTER.register(modEventBus);

        EMenuTypes.REGISTER.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
    }
}
