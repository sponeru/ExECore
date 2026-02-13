package com.sponeru.execore.kubejs;

import com.sponeru.execore.block.BlockGeneratorBlock;
import com.sponeru.execore.block.entity.BlockGeneratorBlockEntity;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

public class BlockGeneratorBlockBuilder extends BlockBuilder {
    private String generatorId = "";
    private int tickRate = 20;
    private int generateAmount = 1;
    private int inventorySize = 9;

    private static RegistryObject<BlockEntityType<BlockGeneratorBlockEntity>> TYPE;

    public BlockGeneratorBlockBuilder(ResourceLocation i) {
        super(i);
    }

    public static void setType(RegistryObject<BlockEntityType<BlockGeneratorBlockEntity>> type) {
        TYPE = type;
    }

    public BlockGeneratorBlockBuilder generatorId(String id) {
        this.generatorId = id;
        return this;
    }

    public BlockGeneratorBlockBuilder tickRate(int rate) {
        this.tickRate = rate;
        return this;
    }

    public BlockGeneratorBlockBuilder generateAmount(int amount) {
        this.generateAmount = amount;
        return this;
    }

    public BlockGeneratorBlockBuilder inventorySize(int size) {
        this.inventorySize = size;
        return this;
    }

    @Override
    public Block createObject() {
        return new BlockGeneratorBlock(createProperties(), generatorId, tickRate, generateAmount, inventorySize);
    }
}
