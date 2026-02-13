package com.sponeru.execore.recipe;

import com.google.gson.JsonObject;
import com.sponeru.execore.ExECore;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockGeneratorRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final Fluid fluid1;
    private final Fluid fluid2;
    private final Block belowBlock;
    private final String generatorId;

    public BlockGeneratorRecipe(ResourceLocation id, ItemStack output, Fluid fluid1, Fluid fluid2, Block belowBlock,
            String generatorId) {
        this.id = id;
        this.output = output;
        this.fluid1 = fluid1;
        this.fluid2 = fluid2;
        this.belowBlock = belowBlock;
        this.generatorId = generatorId;
    }

    @Override
    public boolean matches(@NotNull Container container, @NotNull Level level) {
        return false;
    }

    public boolean matches(Fluid fNorth, Fluid fSouth, Fluid fEast, Fluid fWest, Block bBelow, String gId) {
        if (!this.generatorId.isEmpty() && !this.generatorId.equals(gId)) {
            return false;
        }
        if (this.belowBlock != bBelow) {
            return false;
        }
        return isMatch(fNorth, fSouth) || isMatch(fEast, fWest);
    }

    private boolean isMatch(Fluid a, Fluid b) {
        return (a == fluid1 && b == fluid2) || (a == fluid2 && b == fluid1);
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull Container container, @NotNull RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
        return output;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ExECore.BLOCK_GENERATOR_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ExECore.BLOCK_GENERATOR_RECIPE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<BlockGeneratorRecipe> {
        @Override
        public @NotNull BlockGeneratorRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            Fluid fluid1 = ForgeRegistries.FLUIDS
                    .getValue(new ResourceLocation(GsonHelper.getAsString(json, "fluid1")));
            Fluid fluid2 = ForgeRegistries.FLUIDS
                    .getValue(new ResourceLocation(GsonHelper.getAsString(json, "fluid2")));
            Block belowBlock = ForgeRegistries.BLOCKS
                    .getValue(new ResourceLocation(GsonHelper.getAsString(json, "below")));
            String generatorId = GsonHelper.getAsString(json, "generator_id", "");
            return new BlockGeneratorRecipe(id, output, fluid1, fluid2, belowBlock, generatorId);
        }

        @Override
        public @Nullable BlockGeneratorRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
            ItemStack output = buf.readItem();
            Fluid fluid1 = ForgeRegistries.FLUIDS.getValue(buf.readResourceLocation());
            Fluid fluid2 = ForgeRegistries.FLUIDS.getValue(buf.readResourceLocation());
            Block belowBlock = ForgeRegistries.BLOCKS.getValue(buf.readResourceLocation());
            String generatorId = buf.readUtf();
            return new BlockGeneratorRecipe(id, output, fluid1, fluid2, belowBlock, generatorId);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull BlockGeneratorRecipe recipe) {
            buf.writeItem(recipe.output);
            buf.writeResourceLocation(ForgeRegistries.FLUIDS.getKey(recipe.fluid1));
            buf.writeResourceLocation(ForgeRegistries.FLUIDS.getKey(recipe.fluid2));
            buf.writeResourceLocation(ForgeRegistries.BLOCKS.getKey(recipe.belowBlock));
            buf.writeUtf(recipe.generatorId);
        }
    }
}
