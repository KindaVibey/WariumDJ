package net.vibey.wariumdj.client;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import net.vibey.wariumdj.djConfigManager;

public class wariumdjGui extends Screen {

    public wariumdjGui() {
        super(Component.literal("volume config"));
    }

    @Override
    protected void init() {
        int y = 40;

        // Add a slider for each known sound ID
        for (String soundId : djConfigManager.getAllSoundIds()) {
            float vol = djConfigManager.getVolume(soundId);
            this.addRenderableWidget(new VolumeSlider(this.width / 2 - 100, y, 200, 20, soundId, vol));
            y += 25;
        }

        // Done button
        this.addRenderableWidget(Button.builder(Component.literal("Done"), b -> {
            this.minecraft.setScreen(null);
        }).bounds(this.width / 2 - 100, this.height - 40, 200, 20).build());
    }

    /**
     * Inner slider class so we don't need a separate file.
     */
    private static class VolumeSlider extends AbstractSliderButton {
        private final String soundId;

        public VolumeSlider(int x, int y, int width, int height, String soundId, double currentVolume) {
            super(x, y, width, height,
                    Component.literal(formatLabel(soundId, currentVolume)),
                    currentVolume);
            this.soundId = soundId;
        }

        private static String formatLabel(String soundId, double value) {
            return soundId + ": " + (int)(value * 100) + "%";
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Component.literal(formatLabel(soundId, this.value)));
        }

        @Override
        protected void applyValue() {
            djConfigManager.setVolume(soundId, (float) this.value);
        }
    }
}
