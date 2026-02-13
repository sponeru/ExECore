package com.sponeru.execore.jei;

import com.sponeru.execore.ExECore;
import com.sponeru.execore.recipe.BlockGeneratorRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidStack;

public class BlockGeneratorRecipeCategory implements IRecipeCategory<BlockGeneratorRecipe> {
    private final RecipeType<BlockGeneratorRecipe> recipeType;
    private final String generatorId;

    private final IDrawable background;
    private final IDrawable icon;

    @SuppressWarnings({ "null", "deprecation" })
    public BlockGeneratorRecipeCategory(IGuiHelper guiHelper, RecipeType<BlockGeneratorRecipe> recipeType,
            String generatorId) {
        this.recipeType = recipeType;
        this.generatorId = generatorId;
        this.background = guiHelper.createDrawable(
                new ResourceLocation(ExECore.MODID, "textures/gui/jei_block_generator.png"), 0, 0, 120, 60);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.COBBLESTONE));
    }

    @Override
    public RecipeType<BlockGeneratorRecipe> getRecipeType() {
        return recipeType;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.execore.block_generator").append(" (" + generatorId + ")");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    @SuppressWarnings("null")
    public void setRecipe(IRecipeLayoutBuilder builder, BlockGeneratorRecipe recipe, IFocusGroup focuses) {
        // Fluid 1 (Left)
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 10)
                .addIngredient(ForgeTypes.FLUID_STACK, new FluidStack(recipe.getFluid1(), 1000))
                .setFluidRenderer(1000, false, 16, 16);

        // Fluid 2 (Right)
        builder.addSlot(RecipeIngredientRole.INPUT, 90, 10)
                .addIngredient(ForgeTypes.FLUID_STACK, new FluidStack(recipe.getFluid2(), 1000))
                .setFluidRenderer(1000, false, 16, 16);

        // Block Below (Bottom Center)
        builder.addSlot(RecipeIngredientRole.CATALYST, 50, 40)
                .addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(recipe.getBelowBlock()));

        // Output (Center)
        builder.addSlot(RecipeIngredientRole.OUTPUT, 50, 10)
                .addItemStack(recipe.getResultItem(null));
    }
}
