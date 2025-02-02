package com.finchy.pipeorgans.init;

import com.finchy.pipeorgans.PipeOrgans;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class AllTags {

    public static class Blocks {

        public static final TagKey<Block> VALID_WHISTLES = tag("valid_whistles");


        private static TagKey<Block> tag(String name) {
            return BlockTags.create(new ResourceLocation(PipeOrgans.MOD_ID, name));
        }
    }

    public static class Items {

        public static final TagKey<Item> ADDS_STRING_THICKNESS = tag("adds_string_thickness"); //Items can add thickness from string instruments
        public static final TagKey<Item> REMOVES_STRING_THICKNESS = tag("removes_string_thickness"); //Items can remove thickness from string instruments
        public static final TagKey<Item> STRING_THICKNESS_DAMAGES = tag("string_thickness_damages"); //Items lose 1 durability from changing thickness
        public static final TagKey<Item> STRING_THICKNESS_USES = tag("string_thickness_uses"); //Items lose 1 quantity from changing thickness

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(new ResourceLocation(PipeOrgans.MOD_ID, name));
        }
    }

}
