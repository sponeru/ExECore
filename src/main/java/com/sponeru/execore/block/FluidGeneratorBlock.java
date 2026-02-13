package com.sponeru.execore.block;

import java.util.function.Supplier;
import com.sponeru.execore.block.entity.FluidGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class FluidGeneratorBlock extends Block implements EntityBlock {
    private final int capacity;
    private final FluidStack fluid;
    private final int amount;
    private final Supplier<? extends BlockEntityType<? extends FluidGeneratorBlockEntity>> type;

    public FluidGeneratorBlock(Properties properties, int capacity, FluidStack fluid, int amount,
            Supplier<? extends BlockEntityType<? extends FluidGeneratorBlockEntity>> type) {
        super(properties);
        this.capacity = capacity;
        this.fluid = fluid;
        this.amount = amount;
        this.type = type;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FluidGeneratorBlockEntity(type.get(), pos, state, capacity, fluid, amount);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> blockEntityType) {
        if (blockEntityType != type.get()) {
            return null;
        }
        return level.isClientSide ? null : (level1, pos, state1, be) -> {
            if (be instanceof FluidGeneratorBlockEntity generatorBe) {
                FluidGeneratorBlockEntity.tick(level1, pos, state1, generatorBe);
            }
        };
    }
}
