package net.vibey.wariumdj;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod(wariumdj.MODID)
public class wariumdj {

    public static final String MODID = "wariumdj";

    public wariumdj() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            djConfigManager.load();
        }
    }
}