package dev.apm23.instantmultifishing;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class InstantMultiFishing implements ModInitializer {
    public static final String MOD_ID = "instant_multi_fishing";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Identifier INSTANT_CATCH_ID = Identifier.fromNamespaceAndPath(MOD_ID, "instant_catch");

    private static final double[] SPREAD_DEGREES = {-12.0D, -6.0D, 0.0D, 6.0D, 12.0D};
    private static final Map<UUID, List<FishingHook>> ACTIVE_HOOKS = new ConcurrentHashMap<>();

    @Override
    public void onInitialize() {
        LOGGER.info("Instant Multi Fishing loaded: five-hook server-side fishing enabled.");
    }

    public static boolean hasInstantCatch(ItemStack stack) {
        for (Holder<Enchantment> enchantment : stack.getEnchantments().keySet()) {
            if (enchantment.unwrapKey()
                    .map(key -> key.location().equals(INSTANT_CATCH_ID))
                    .orElse(false)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasActiveHooks(ServerPlayer player) {
        List<FishingHook> hooks = ACTIVE_HOOKS.get(player.getUUID());
        return hooks != null && hooks.stream().anyMatch(FishingHook::isAlive);
    }

    public static boolean isManagedHook(FishingHook hook, ServerPlayer player) {
        List<FishingHook> hooks = ACTIVE_HOOKS.get(player.getUUID());
        return hooks != null && hooks.contains(hook);
    }

    public static void cast(ServerLevel level, ServerPlayer player, ItemStack rod) {
        removeOldHooks(player);

        int luck = EnchantmentHelper.getFishingLuckBonus(level, rod, player);
        int lureSpeed = (int) (EnchantmentHelper.getFishingTimeReduction(level, rod, player) * 20.0F);
        List<FishingHook> hooks = new ArrayList<>(SPREAD_DEGREES.length);

        for (double angle : SPREAD_DEGREES) {
            FishingHook hook = new FishingHook(player, level, luck, lureSpeed);
            hook.setDeltaMovement(rotateY(hook.getDeltaMovement(), Math.toRadians(angle)));
            level.addFreshEntity(hook);
            hooks.add(hook);
        }

        ACTIVE_HOOKS.put(player.getUUID(), hooks);
    }

    public static void reel(ServerLevel level, ServerPlayer player, ItemStack rod, InteractionHand hand) {
        List<FishingHook> hooks = ACTIVE_HOOKS.remove(player.getUUID());
        if (hooks == null || hooks.isEmpty()) {
            return;
        }

        int rodDamage = 0;
        for (FishingHook hook : hooks) {
            if (!hook.isAlive()) {
                continue;
            }

            FishingHookAccess access = (FishingHookAccess) hook;
            access.instantMultiFishing$setNibble(1);
            access.instantMultiFishing$setOpenWater(true);
            rodDamage = Math.max(rodDamage, hook.retrieve(rod));
        }

        // Five catches count as one reel for durability, as specified.
        rod.hurtAndBreak(Math.max(1, rodDamage), player, hand);
    }

    public static void removeOldHooks(ServerPlayer player) {
        List<FishingHook> old = ACTIVE_HOOKS.remove(player.getUUID());
        if (old != null) {
            old.stream().filter(FishingHook::isAlive).forEach(FishingHook::discard);
        }
    }

    private static Vec3 rotateY(Vec3 velocity, double radians) {
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);
        return new Vec3(
                velocity.x * cos - velocity.z * sin,
                velocity.y,
                velocity.x * sin + velocity.z * cos
        );
    }
}
