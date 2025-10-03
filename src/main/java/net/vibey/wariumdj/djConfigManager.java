package net.vibey.wariumdj;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.*;

public class djConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config/djConfig.json");

    private static final Map<String, Float> categoryVolumes = new HashMap<>();
    private static final Map<String, String> soundToCategory = new HashMap<>();

    public static void load() {
        try {
            Files.createDirectories(CONFIG_FILE.getParentFile().toPath());

            loadCategoryMappingsFromResources();

            if (!CONFIG_FILE.exists()) {
                for (String category : getAllCategoryNames()) {
                    categoryVolumes.put(category, 1.0f);
                }
                save();
                System.out.println("[WariumDJ] Created config with " + categoryVolumes.size() + " categories");
            } else {
                try (FileReader reader = new FileReader(CONFIG_FILE)) {
                    Map<?, ?> map = GSON.fromJson(reader, Map.class);
                    if (map != null) {
                        for (Map.Entry<?, ?> entry : map.entrySet()) {
                            if (entry.getKey() instanceof String && entry.getValue() instanceof Number) {
                                categoryVolumes.put((String) entry.getKey(), ((Number) entry.getValue()).floatValue());
                            }
                        }
                    }
                }
                System.out.println("[WariumDJ] Loaded config with " + categoryVolumes.size() + " categories");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadCategoryMappingsFromResources() {
        try (InputStream stream = djConfigManager.class.getClassLoader()
                .getResourceAsStream("assets/wariumdj/config/sound_categories.json")) {

            if (stream != null) {
                InputStreamReader reader = new InputStreamReader(stream);
                Map<String, List<String>> categories = GSON.fromJson(reader,
                        new TypeToken<Map<String, List<String>>>(){}.getType());

                soundToCategory.clear();

                for (Map.Entry<String, List<String>> entry : categories.entrySet()) {
                    String categoryName = entry.getKey();
                    for (String soundId : entry.getValue()) {
                        soundToCategory.put(soundId, categoryName);
                    }
                }
                System.out.println("[WariumDJ] Loaded " + categories.size() + " categories with " + soundToCategory.size() + " sounds");
            } else {
                System.err.println("[WariumDJ] ERROR: sound_categories.json not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static float getVolume(String soundId) {
        String category = soundToCategory.get(soundId);
        if (category != null) {
            return categoryVolumes.getOrDefault(category, 1.0f);
        }
        return 1.0f;
    }

    public static void setCategoryVolume(String category, float volume) {
        categoryVolumes.put(category, volume);
        save();
    }

    public static float getCategoryVolume(String category) {
        return categoryVolumes.getOrDefault(category, 1.0f);
    }

    public static Set<String> getAllCategoryNames() {
        return new HashSet<>(soundToCategory.values());
    }

    private static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(categoryVolumes, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}