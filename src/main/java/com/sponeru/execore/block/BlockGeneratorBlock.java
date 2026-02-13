package com.sponeru.execore.block;

import com.sponeru.execore.ExECore;
import com.sponeru.execore.block.entity.BlockGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockGeneratorBlock extends Block implements EntityBlock {
    private final String generatorId;
    private final int tickRate;
    private final int generateAmount;
    private final int inventorySize;

    public BlockGeneratorBlock(Properties properties, String generatorId, int tickRate, int generateAmount,
            int inventorySize) {
        super(properties);
        this.generatorId = generatorId;
        this.tickRate = tickRate;
        this.generateAmount = generateAmount;
        this.inventorySize = inventorySize;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BlockGeneratorBlockEntity(pos, state, generatorId, tickRate, generateAmount, inventorySize);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> type) {
        if (level.isClientSide)
            return null;
        return (l, p, s, be) -> {
            if (be instanceof BlockGeneratorBlockEntity generator) {
                generator.tick(l, p, s);
            }
        };
    }
}
