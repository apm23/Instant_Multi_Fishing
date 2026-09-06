package dev.apm23.instantmultifishing.mixin;

import dev.apm23.instantmultifishing.InstantMultiFishing;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin {
    @Redirect(
            method = "shouldStopFishing",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/player/Player;fishing:Lnet/minecraft/world/entity/projectile/FishingHook;"
            )
    )
    private FishingHook instantMultiFishing$allowSiblingHooks(Player player) {
        FishingHook self = (FishingHook) (Object) this;
        if (player instanceof ServerPlayer serverPlayer && InstantMultiFishing.isManagedHook(self, serverPlayer)) {
            return self;
        }
        return player.fishing;
    }
}
