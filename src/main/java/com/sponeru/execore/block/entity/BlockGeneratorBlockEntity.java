package com.sponeru.execore.block.entity;

import com.sponeru.execore.ExECore;
import com.sponeru.execore.recipe.BlockGeneratorRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockGeneratorBlockEntity extends BlockEntity {
    private ItemStackHandler inventory;
    private LazyOptional<IItemHandler> inventoryHolder = LazyOptional.empty();

    private String generatorId = "";
    private int tickRate = 20;
    private int generateAmount = 1;
    private int inventorySize = 9;
    private int timer = 0;

    public BlockGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ExECore.BLOCK_GENERATOR_BLOCK_ENTITY.get(), pos, state);
        initInventory(inventorySize);
    }

    public BlockGeneratorBlockEntity(BlockPos pos, BlockState state, String generatorId, int tickRate,
            int generateAmount, int inventorySize) {
        super(ExECore.BLOCK_GENERATOR_BLOCK_ENTITY.get(), pos, state);
        this.generatorId = generatorId;
        this.tickRate = tickRate;
        this.generateAmount = generateAmount;
        this.inventorySize = inventorySize;
        initInventory(inventorySize);
    }

    private void initInventory(int size) {
        this.inventorySize = size;
        this.inventory = new ItemStackHandler(size) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
        this.inventoryHolder = LazyOptional.of(() -> inventory);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide)
            return;

        timer++;
        if (timer >= tickRate) {
            timer = 0;
            processGeneration(level, pos);
        }

        exportToAbove(level, pos);
    }

    private void processGeneration(Level level, BlockPos pos) {
        Fluid fNorth = level.getFluidState(pos.north()).getType();
        Fluid fSouth = level.getFluidState(pos.south()).getType();
        Fluid fEast = level.getFluidState(pos.east()).getType();
        Fluid fWest = level.getFluidState(pos.west()).getType();
        Block bBelow = level.getBlockState(pos.below()).getBlock();

        List<BlockGeneratorRecipe> recipes = level.getRecipeManager()
                .getAllRecipesFor(ExECore.BLOCK_GENERATOR_RECIPE_TYPE.get());
        for (BlockGeneratorRecipe recipe : recipes) {
            if (recipe.matches(fNorth, fSouth, fEast, fWest, bBelow, generatorId)) {
                ItemStack output = recipe.assemble(null, level.registryAccess());
                output.setCount(generateAmount);

                ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, output, false);
                if (remainder.getCount() < output.getCount()) {
                    setChanged();
                }
                break;
            }
        }
    }

    private void exportToAbove(Level level, BlockPos pos) {
        BlockEntity aboveBe = level.getBlockEntity(pos.above());
        if (aboveBe != null) {
            aboveBe.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.DOWN).ifPresent(aboveInventory -> {
                for (int i = 0; i < inventory.getSlots(); i++) {
                    ItemStack stack = inventory.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        ItemStack toExport = stack.copy();
                        toExport.setCount(1);
                        ItemStack remainder = ItemHandlerHelper.insertItemStacked(aboveInventory, toExport, false);
                        if (remainder.isEmpty()) {
                            inventory.extractItem(i, 1, false);
                            setChanged();
                            break;
                        }
                    }
                }
            });
        }
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.generatorId = tag.getString("GeneratorId");
        this.tickRate = tag.getInt("TickRate");
        this.generateAmount = tag.getInt("GenerateAmount");
        int savedSize = tag.getInt("InventorySize");
        if (savedSize != inventorySize) {
            initInventory(savedSize);
        }
        inventory.deserializeNBT(tag.getCompound("Inventory"));
        this.timer = tag.getInt("Timer");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("GeneratorId", generatorId);
        tag.putInt("TickRate", tickRate);
        tag.putInt("GenerateAmount", generateAmount);
        tag.putInt("InventorySize", inventorySize);
        tag.put("Inventory", inventory.serializeNBT());
        tag.putInt("Timer", timer);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryHolder.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        inventoryHolder.invalidate();
    }
}
