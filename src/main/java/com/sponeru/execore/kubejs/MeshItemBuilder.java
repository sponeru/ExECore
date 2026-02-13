package com.sponeru.execore.kubejs;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import thedarkcolour.exdeorum.item.MeshItem;

public class MeshItemBuilder extends ItemBuilder {
    public MeshItemBuilder(ResourceLocation i) {
        super(i);
        // Ex DeorumのSieveで認識されるようにタグを自動付与
        tag(new ResourceLocation("exdeorum", "sieve_meshes"));
    }

    @Override
    public Item createObject() {
        return new MeshItem(createItemProperties());
    }
}
