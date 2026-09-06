package dev.apm23.instantmultifishing;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

public final class InstantCatchRegistrySmokeTest {
    @GameTest
    public void instantCatchIsRegisteredAndApplicable(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ResourceKey<Enchantment> key = ResourceKey.create(Registries.ENCHANTMENT, InstantMultiFishing.INSTANT_CATCH_ID);
        Holder.Reference<Enchantment> enchantment = level.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .get(key)
                .orElseThrow(() -> new AssertionError("instant_multi_fishing:instant_catch is missing from the enchantment registry"));

        ItemStack rod = new ItemStack(Items.FISHING_ROD);
        rod.enchant(enchantment, 1);

        helper.assertTrue(InstantMultiFishing.hasInstantCatch(rod),
                "Instant Catch must be present on a fishing rod after applying the registry enchantment");
        helper.succeed();
    }
}
