package com.finchy.pipeorgans.init;

import com.finchy.pipeorgans.PipeOrgans;
import com.finchy.pipeorgans.item.generic.GenericChordophoneStringItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class AllItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, PipeOrgans.MOD_ID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    private static <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> block) {
        return ITEMS.register(name, block);
    }

    //public static final RegistryEntry<Item> WHOOPS = REGISTRATE.item("whoops", Item::new).register();

    public static final RegistryObject<GenericChordophoneStringItem> TEST_CHORDOPHONE_STRING = registerItem("test_cordophone_string",
            () -> new GenericChordophoneStringItem(new Item.Properties(), AllBlocks.HARP_STRING));

}
