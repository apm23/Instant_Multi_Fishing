package dev.apm23.instantmultifishing;

import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class InstantMultiFishingGameTest {
    @GameTest
    public void castCreatesFiveManagedHooks(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ServerPlayer player = FakePlayer.get(level);
        ItemStack rod = new ItemStack(Items.FISHING_ROD);
        player.setItemInHand(InteractionHand.MAIN_HAND, rod);

        InstantMultiFishing.cast(level, player, rod);

        helper.assertTrue(InstantMultiFishing.managedHookCount(player) == 5,
                "Instant cast must create exactly five managed fishing hooks");
        helper.assertTrue(InstantMultiFishing.hasActiveHooks(player),
                "At least one managed hook must be alive immediately after cast");

        InstantMultiFishing.removeOldHooks(player);
        helper.assertTrue(InstantMultiFishing.managedHookCount(player) == 0,
                "Managed hooks must cleanly clear after cleanup");
        helper.succeed();
    }

    @GameTest
    public void reelConsumesManagedHookGroupWithoutCrash(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ServerPlayer player = FakePlayer.get(level);
        ItemStack rod = new ItemStack(Items.FISHING_ROD);
        player.setItemInHand(InteractionHand.MAIN_HAND, rod);

        InstantMultiFishing.cast(level, player, rod);
        helper.assertTrue(InstantMultiFishing.managedHookCount(player) == 5,
                "Precondition failed: cast did not create five hooks");

        InstantMultiFishing.reel(level, player, rod, InteractionHand.MAIN_HAND);

        helper.assertTrue(InstantMultiFishing.managedHookCount(player) == 0,
                "Reel must consume and clear the whole managed hook group");
        helper.assertTrue(!InstantMultiFishing.hasActiveHooks(player),
                "No managed hook may remain active after reel");
        helper.succeed();
    }
}
