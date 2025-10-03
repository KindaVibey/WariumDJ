package net.vibey.wariumdj.mixin;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.vibey.wariumdj.djConfigManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {

    @Inject(
            method = "calculateVolume(Lnet/minecraft/client/resources/sounds/SoundInstance;)F",
            at = @At("RETURN"),
            cancellable = true
    )
    private void wariumdj_adjustVolume(SoundInstance sound, CallbackInfoReturnable<Float> cir) {
        if (sound == null) return;

        String soundId = sound.getLocation().toString();
        float scale = djConfigManager.getVolume(soundId);

        if (Math.abs(scale - 1.0f) > 0.001f) {
            float originalVolume = cir.getReturnValue();
            float newVolume = originalVolume * scale;
            cir.setReturnValue(Math.max(0.0f, Math.min(1.0f, newVolume)));
        }
    }
}