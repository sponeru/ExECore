package com.sponeru.execore.jei;

import com.sponeru.execore.ExECore;
import com.sponeru.execore.recipe.BlockGeneratorRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JeiPlugin
public class ExECoreJEIPlugin implements IModPlugin {
    private final Map<String, BlockGeneratorRecipeCategory> categories = new HashMap<>();

    @Override
    @SuppressWarnings("null")
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ExECore.MODID, "jei_plugin");
    }

    @Override
    @SuppressWarnings("null")
    public void registerCategories(IRecipeCategoryRegistration registration) {
        ForgeRegistries.BLOCKS.getValues().stream()
                .filter(b -> b instanceof com.sponeru.execore.block.BlockGeneratorBlock)
                .map(b -> (com.sponeru.execore.block.BlockGeneratorBlock) b)
                .map(com.sponeru.execore.block.BlockGeneratorBlock::getGeneratorId)
                .distinct()
                .forEach(generatorId -> {
                    mezz.jei.api.recipe.RecipeType<BlockGeneratorRecipe> recipeType = mezz.jei.api.recipe.RecipeType
                            .create(ExECore.MODID, "block_generator_" + generatorId, BlockGeneratorRecipe.class);
                    BlockGeneratorRecipeCategory category = new BlockGeneratorRecipeCategory(
                            registration.getJeiHelpers().getGuiHelper(), recipeType, generatorId);
                    categories.put(generatorId, category);
                    registration.addRecipeCategories(category);
                });
    }

    @Override
    @SuppressWarnings("null")
    public void registerRecipes(IRecipeRegistration registration) {
        assert Minecraft.getInstance().level != null;
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        List<BlockGeneratorRecipe> allRecipes = recipeManager
                .getAllRecipesFor(ExECore.BLOCK_GENERATOR_RECIPE_TYPE.get());

        for (BlockGeneratorRecipeCategory category : categories.values()) {
            String generatorId = categories.entrySet().stream()
                    .filter(entry -> entry.getValue() == category)
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse("");

            List<BlockGeneratorRecipe> filteredRecipes = allRecipes.stream()
                    .filter(r -> r.getGeneratorId().isEmpty() || r.getGeneratorId().equals(generatorId))
                    .toList();

            registration.addRecipes(category.getRecipeType(), filteredRecipes);
        }
    }

    @Override
    @SuppressWarnings("null")
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        ForgeRegistries.BLOCKS.getValues().stream()
                .filter(b -> b instanceof com.sponeru.execore.block.BlockGeneratorBlock)
                .map(b -> (com.sponeru.execore.block.BlockGeneratorBlock) b)
                .forEach(b -> {
                    String generatorId = b.getGeneratorId();
                    if (categories.containsKey(generatorId)) {
                        registration.addRecipeCatalyst(new ItemStack(b), categories.get(generatorId).getRecipeType());
                    }
                });
    }
}
