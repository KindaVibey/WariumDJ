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

    // Maps category name -> volume multiplier
    private static final Map<String, Float> categoryVolumes = new HashMap<>();

    // Maps sound ID -> category name (loaded from resources, always overwrites)
    private static final Map<String, String> soundToCategory = new HashMap<>();

    public static void load() {
        try {
            // 1. Ensure config folder exists
            Files.createDirectories(CONFIG_FILE.getParentFile().toPath());

            // 2. ALWAYS load category mappings from resources (this overwrites)
            loadCategoryMappingsFromResources();

            // 3. Load or create user's volume preferences
            if (!CONFIG_FILE.exists()) {
                // Initialize all categories to 100%
                for (String category : getCategories()) {
                    categoryVolumes.put(category, 1.0f);
                }
                save();
                System.out.println("Created new config with " + categoryVolumes.size() + " categories");
            } else {
                // Load user's saved volumes
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
                // Add any new categories from resources that aren't in config yet
                for (String category : getCategories()) {
                    categoryVolumes.putIfAbsent(category, 1.0f);
                }
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

                // Clear old mappings and load new ones from resources
                soundToCategory.clear();

                for (Map.Entry<String, List<String>> entry : categories.entrySet()) {
                    String categoryName = entry.getKey();
                    for (String soundId : entry.getValue()) {
                        soundToCategory.put(soundId, categoryName);
                    }
                }
                System.out.println("Loaded " + categories.size() + " categories from resources");
            } else {
                System.err.println("WARNING: sound_categories.json not found in resources!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get volume for a specific sound based on its category
    public static float getVolume(String soundId) {
        String category = soundToCategory.get(soundId);
        if (category != null) {
            return categoryVolumes.getOrDefault(category, 1.0f);
        }
        return 1.0f; // Unmapped sounds play at full volume
    }

    // Set volume for an entire category
    public static void setCategoryVolume(String category, float volume) {
        categoryVolumes.put(category, volume);
        save();
    }

    // Get current volume for a category
    public static float getCategoryVolume(String category) {
        return categoryVolumes.getOrDefault(category, 1.0f);
    }

    // Get all category names for the GUI
    public static Set<String> getCategories() {
        Set<String> categories = new HashSet<>();
        for (String category : soundToCategory.values()) {
            categories.add(category);
        }
        return categories;
    }

    private static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(categoryVolumes, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}