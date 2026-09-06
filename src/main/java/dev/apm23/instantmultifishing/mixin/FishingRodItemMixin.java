package dev.apm23.instantmultifishing.mixin;

import dev.apm23.instantmultifishing.InstantMultiFishing;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingRodItem.class)
public abstract class FishingRodItemMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void instantMultiFishing$use(
            Level level,
            Player player,
            InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        ItemStack rod = player.getItemInHand(hand);
        if (!InstantMultiFishing.hasInstantCatch(rod)) {
            return;
        }

        if (InstantMultiFishing.hasActiveHooks(serverPlayer)) {
            InstantMultiFishing.reel(serverLevel, serverPlayer, rod, hand);
        } else {
            InstantMultiFishing.cast(serverLevel, serverPlayer, rod);
        }

        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}
