package com.sponeru.execore.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.RegistryInfo;

public class ExECoreKubeJSPlugin extends KubeJSPlugin {
    @Override
    public void init() {
        System.out.println("ExECore KubeJS Plugin Initializing (init)...");
        RegistryInfo.BLOCK.addType("fluid_generator", FluidGeneratorBlockBuilder.class,
                FluidGeneratorBlockBuilder::new);
        RegistryInfo.BLOCK.addType("block_generator", BlockGeneratorBlockBuilder.class,
                BlockGeneratorBlockBuilder::new);
        RegistryInfo.ITEM.addType("exdeorum_mesh", MeshItemBuilder.class,
                MeshItemBuilder::new);
    }
}
