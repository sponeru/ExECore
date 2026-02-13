package com.sponeru.execore.block.entity;

import com.sponeru.execore.ExECore;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidGeneratorBlockEntity extends BlockEntity {
    private final FluidTank tank;
    private final LazyOptional<IFluidHandler> holder;

    // KubeJSから注入される設定
    private FluidStack fluidToGenerate = FluidStack.EMPTY;
    private int amount = 0;
    private int tickCounter = 0;

    public FluidGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(getTypeFallback(), pos, state);
        this.tank = new FluidTank(1000) {
            @Override
            protected void onContentsChanged() {
                setChanged();
            }
        };
        this.holder = LazyOptional.of(() -> tank);
    }

    private static BlockEntityType<?> getTypeFallback() {
        try {
            return ExECore.FLUID_GENERATOR_BLOCK_ENTITY.get();
        } catch (Exception e) {
            // 登録前（KubeJSによるプローブ等）の場合はnullを許容する
            return null;
        }
    }

    // 実際のコンストラクタ
    public FluidGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int capacity,
            FluidStack fluid, int amount) {
        super(type, pos, state);
        this.fluidToGenerate = fluid;
        this.amount = amount;
        this.tank = new FluidTank(capacity) {
            @Override
            protected void onContentsChanged() {
                setChanged();
            }
        };
        this.holder = LazyOptional.of(() -> tank);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FluidGeneratorBlockEntity be) {
        if (level.isClientSide)
            return;

        be.tickCounter++;
        if (be.tickCounter >= 20) {
            be.tickCounter = 0;
            if (!be.fluidToGenerate.isEmpty() && be.amount > 0) {
                be.tank.fill(new FluidStack(be.fluidToGenerate, be.amount),
                        IFluidHandler.FluidAction.EXECUTE);
            }
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap,
            @Nullable net.minecraft.core.Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return holder.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        tank.readFromNBT(tag.getCompound("Tank"));
        tickCounter = tag.getInt("TickCounter");
        // 設定値も保存（リロード時に必要）
        if (tag.contains("FluidData")) {
            fluidToGenerate = FluidStack.loadFluidStackFromNBT(tag.getCompound("FluidData"));
        }
        amount = tag.getInt("Amount");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Tank", tank.writeToNBT(new CompoundTag()));
        tag.putInt("TickCounter", tickCounter);
        if (!fluidToGenerate.isEmpty()) {
            tag.put("FluidData", fluidToGenerate.writeToNBT(new CompoundTag()));
        }
        tag.putInt("Amount", amount);
    }
}
