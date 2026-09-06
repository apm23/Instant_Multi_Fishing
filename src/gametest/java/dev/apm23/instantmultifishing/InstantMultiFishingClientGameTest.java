package dev.apm23.instantmultifishing;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

@SuppressWarnings("UnstableApiUsage")
public final class InstantMultiFishingClientGameTest implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
        if (!FabricLoader.getInstance().isModLoaded("instant_multi_fishing")) {
            throw new AssertionError("instant_multi_fishing is not loaded in client runtime");
        }

        try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
            ResourceKey<Enchantment> key = ResourceKey.create(Registries.ENCHANTMENT, InstantMultiFishing.INSTANT_CATCH_ID);
            singleplayer.getServer().runOnServer(server -> {
                boolean present = server.registryAccess()
                        .lookupOrThrow(Registries.ENCHANTMENT)
                        .get(key)
                        .isPresent();
                if (!present) {
                    throw new AssertionError("instant_multi_fishing:instant_catch missing from integrated-server registry");
                }
            });
        }
    }
}
