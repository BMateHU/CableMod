package com.bmatehu.examplemod.registers;

import com.bmatehu.examplemod.ExampleMod;
import com.bmatehu.examplemod.blocks.CableBlockEntity;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;
import java.util.function.Supplier;

public class EBlockEntityTypes {

    public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ExampleMod.MODID);

    public static final RegistryObject<BlockEntityType<CableBlockEntity>> CABLE_BLOCK =
            REGISTER.register("cable_block_entity",
                    () -> BlockEntityType.Builder.of(CableBlockEntity::new, EBlocks.EXAMPLE_BLOCK.get()).build(null));
}
