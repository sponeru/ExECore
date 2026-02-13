package com.sponeru.execore.kubejs;

import com.sponeru.execore.block.FluidGeneratorBlock;
import com.sponeru.execore.block.entity.FluidGeneratorBlockEntity;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.RegistryObject;

public class FluidGeneratorBlockBuilder extends BlockBuilder {
    private int capacity = 1000;
    private FluidStack fluid = FluidStack.EMPTY;
    private int amount = 10;

    // BlockEntityTypeを後から解決するための保持
    private static RegistryObject<BlockEntityType<FluidGeneratorBlockEntity>> TYPE;

    public FluidGeneratorBlockBuilder(ResourceLocation i) {
        super(i);
    }

    public static void setType(RegistryObject<BlockEntityType<FluidGeneratorBlockEntity>> type) {
        TYPE = type;
    }

    public FluidGeneratorBlockBuilder capacity(int c) {
        this.capacity = c;
        return this;
    }

    public FluidGeneratorBlockBuilder fluid(Object f) {
        dev.architectury.fluid.FluidStack archStack = FluidStackJS.of(f).getFluidStack();
        this.fluid = new net.minecraftforge.fluids.FluidStack(archStack.getFluid(), (int) archStack.getAmount(),
                archStack.getTag());
        return this;
    }

    public FluidGeneratorBlockBuilder amount(int a) {
        this.amount = a;
        return this;
    }

    @Override
    public Block createObject() {
        return new FluidGeneratorBlock(createProperties(), capacity, fluid, amount, TYPE);
    }
}
