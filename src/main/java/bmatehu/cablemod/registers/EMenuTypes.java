package bmatehu.cablemod.registers;

import bmatehu.cablemod.CableMod;
import bmatehu.cablemod.menu.CableMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EMenuTypes {

    public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.MENU_TYPES, CableMod.MODID);

    public static final RegistryObject<MenuType<CableMenu>> CABLE_MENU = REGISTER.register("cable_menu", () -> IForgeMenuType.create(CableMenu::new));
}
