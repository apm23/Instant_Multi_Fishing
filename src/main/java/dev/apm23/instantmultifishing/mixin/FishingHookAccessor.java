package dev.apm23.instantmultifishing.mixin;

import dev.apm23.instantmultifishing.FishingHookAccess;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FishingHook.class)
public interface FishingHookAccessor extends FishingHookAccess {
    @Accessor("nibble")
    @Override
    void instantMultiFishing$setNibble(int value);

    @Accessor("openWater")
    @Override
    void instantMultiFishing$setOpenWater(boolean value);
}
