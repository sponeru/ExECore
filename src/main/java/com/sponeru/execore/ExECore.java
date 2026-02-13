package com.sponeru.execore;

import com.mojang.logging.LogUtils;
import com.sponeru.execore.block.entity.BlockGeneratorBlockEntity;
import com.sponeru.execore.kubejs.BlockGeneratorBlockBuilder;
import com.sponeru.execore.kubejs.FluidGeneratorBlockBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(ExECore.MODID)
public class ExECore {
        public static final String MODID = "execore";
        private static final Logger LOGGER = LogUtils.getLogger();

        public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
        public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
        public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
                        .create(Registries.CREATIVE_MODE_TAB, MODID);
        public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister
                        .create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
        public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister
                        .create(Registries.RECIPE_TYPE, MODID);
        public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister
                        .create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

        public static final RegistryObject<RecipeType<com.sponeru.execore.recipe.BlockGeneratorRecipe>> BLOCK_GENERATOR_RECIPE_TYPE = RECIPE_TYPES
                        .register("block_generator",
                                        () -> new RecipeType<com.sponeru.execore.recipe.BlockGeneratorRecipe>() {
                                                @Override
                                                public String toString() {
                                                        return "block_generator";
                                                }
                                        });

        public static final RegistryObject<RecipeSerializer<com.sponeru.execore.recipe.BlockGeneratorRecipe>> BLOCK_GENERATOR_SERIALIZER = RECIPE_SERIALIZERS
                        .register("block_generator", com.sponeru.execore.recipe.BlockGeneratorRecipe.Serializer::new);

        public static final RegistryObject<BlockEntityType<BlockGeneratorBlockEntity>> BLOCK_GENERATOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES
                        .register("block_generator", () -> {
                                Block[] blocks = ForgeRegistries.BLOCKS.getValues().stream()
                                                .filter(b -> b instanceof com.sponeru.execore.block.BlockGeneratorBlock)
                                                .toArray(Block[]::new);
                                return BlockEntityType.Builder.of(BlockGeneratorBlockEntity::new, blocks).build(null);
                        });

        // 旧FluidGenerator用のRegistryObject（後方互換性のため維持、またはタイポ修正）
        public static final RegistryObject<BlockEntityType<com.sponeru.execore.block.entity.FluidGeneratorBlockEntity>> FLUID_GENERATOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES
                        .register("fluid_generator", () -> {
                                Block[] blocks = ForgeRegistries.BLOCKS.getValues().stream()
                                                .filter(b -> b instanceof com.sponeru.execore.block.FluidGeneratorBlock)
                                                .toArray(Block[]::new);
                                return BlockEntityType.Builder
                                                .of(com.sponeru.execore.block.entity.FluidGeneratorBlockEntity::new,
                                                                blocks)
                                                .build(null);
                        });

        public ExECore(FMLJavaModLoadingContext context) {
                IEventBus modEventBus = context.getModEventBus();

                BLOCKS.register(modEventBus);
                ITEMS.register(modEventBus);
                CREATIVE_MODE_TABS.register(modEventBus);
                BLOCK_ENTITY_TYPES.register(modEventBus);
                RECIPE_TYPES.register(modEventBus);
                RECIPE_SERIALIZERS.register(modEventBus);

                BlockGeneratorBlockBuilder.setType(BLOCK_GENERATOR_BLOCK_ENTITY);
                FluidGeneratorBlockBuilder.setType(FLUID_GENERATOR_BLOCK_ENTITY);

                MinecraftForge.EVENT_BUS.register(this);
                context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        }
}
