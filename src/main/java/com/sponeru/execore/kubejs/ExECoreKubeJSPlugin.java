package com.sponeru.execore.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.RegistryInfo;

public class ExECoreKubeJSPlugin extends KubeJSPlugin {
    @Override
    public void init() {
        System.out.println("ExECore KubeJS Plugin Initializing (init)...");
        RegistryInfo.BLOCK.addType("fluid_generator", FluidGeneratorBlockBuilder.class,
                FluidGeneratorBlockBuilder::new);
    }

    @Override
    public void initStartup() {
        System.out.println("ExECore KubeJS Plugin Initializing (initStartup)...");
        RegistryInfo.BLOCK.addType("fluid_generator", FluidGeneratorBlockBuilder.class,
                FluidGeneratorBlockBuilder::new);
    }
}
