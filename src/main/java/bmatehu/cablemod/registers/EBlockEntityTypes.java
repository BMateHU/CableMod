package bmatehu.cablemod.registers;

import bmatehu.cablemod.CableMod;
import bmatehu.cablemod.blocks.CableBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class EBlockEntityTypes {

    public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CableMod.MODID);

    public static final RegistryObject<BlockEntityType<CableBlockEntity>> CABLE_BLOCK =
            REGISTER.register("cable_block_entity",
                    () -> BlockEntityType.Builder.of(CableBlockEntity::new, EBlocks.CABLE_BLOCK.get()).build(null));
}
