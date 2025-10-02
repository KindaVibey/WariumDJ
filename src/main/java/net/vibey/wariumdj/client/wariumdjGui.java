package net.vibey.wariumdj.client;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.vibey.wariumdj.djConfigManager;

import java.util.ArrayList;
import java.util.List;

public class wariumdjGui extends Screen {

    public wariumdjGui() {
        super(Component.literal("Sound Volume Categories"));
    }

    @Override
    protected void init() {
        super.init();

        List<String> categories = new ArrayList<>(djConfigManager.getAllCategoryNames());
        categories.sort(String::compareToIgnoreCase);

        int y = 40;
        int spacing = 30;

        for (String category : categories) {
            float vol = djConfigManager.getCategoryVolume(category);
            this.addRenderableWidget(new VolumeSlider(
                    this.width / 2 - 100,
                    y,
                    200,
                    20,
                    category,
                    vol
            ));
            y += spacing;
        }

        this.addRenderableWidget(Button.builder(
                Component.literal("Done"),
                b -> this.minecraft.setScreen(null)
        ).bounds(this.width / 2 - 100, this.height - 40, 200, 20).build());
    }

    private static class VolumeSlider extends AbstractSliderButton {
        private final String category;

        public VolumeSlider(int x, int y, int width, int height, String category, double currentVolume) {
            super(x, y, width, height,
                    Component.literal(formatLabel(category, currentVolume)),
                    currentVolume);
            this.category = category;
        }

        private static String formatLabel(String category, double value) {
            return category + ": " + (int)(value * 100) + "%";
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Component.literal(formatLabel(category, this.value)));
        }

        @Override
        protected void applyValue() {
            djConfigManager.setCategoryVolume(category, (float) this.value);
        }
    }
}