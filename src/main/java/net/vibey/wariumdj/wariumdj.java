package net.vibey.wariumdj;

import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;

@Mod(wariumdj.MODID)
public class wariumdj {

    public static final String MODID = "wariumdj";

    public wariumdj() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public class ClientModEvents {
        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            djConfigManager.load();
        }
    }


    @SubscribeEvent
    public void onPlaySound(PlaySoundEvent event) {
        SoundInstance original = event.getSound();
        if (original == null) return;  //get sound

        String soundId = original.getLocation().toString();
        float scale = djConfigManager.getVolume(soundId);
        //edit sound volume
        if (scale != 1.0f) {
            event.setSound(new VolumeAdjustedSound(original, scale));
        }
    }

    // Wrapper to scale volume
    static class VolumeAdjustedSound implements SoundInstance {
        private final SoundInstance original;
        private final float scale;

        VolumeAdjustedSound(SoundInstance original, float scale) {
            this.original = original;
            this.scale = scale;
        }

        @Override public float getVolume() { return original.getVolume() * scale; }
        @Override public ResourceLocation getLocation() { return original.getLocation(); }

        @Override
        public @Nullable WeighedSoundEvents resolve(SoundManager soundManager) {
            return null;
        }

        @Override public net.minecraft.client.resources.sounds.Sound getSound() { return original.getSound(); }
        @Override public net.minecraft.sounds.SoundSource getSource() { return original.getSource(); }

        @Override
        public boolean isLooping() {
            return false;
        }

        @Override public float getPitch() { return original.getPitch(); }
        @Override public double getX() { return original.getX(); }
        @Override public double getY() { return original.getY(); }
        @Override public double getZ() { return original.getZ(); }
        @Override public Attenuation getAttenuation() { return original.getAttenuation(); }
        @Override public boolean isRelative() { return original.isRelative(); }
        @Override public int getDelay() { return original.getDelay(); }
        @Override public boolean canStartSilent() { return original.canStartSilent(); }
        @Override public boolean canPlaySound() { return original.canPlaySound(); }
    }

}
