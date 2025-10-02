package net.vibey.wariumdj;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class djConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config/soundtweaker.json");
    private static final Map<String, Float> volumeMap = new HashMap<>();

    public static void load() {
        try {
            // First run: copy default JSON if config does not exist
            if (!CONFIG_FILE.exists()) {
                Files.createDirectories(CONFIG_FILE.getParentFile().toPath());
                try (InputStream stream = djConfigManager.class.getClassLoader()
                        .getResourceAsStream("assets/soundtweaker/config/default_volumes.json")) {
                    if (stream != null) {
                        Files.copy(stream, CONFIG_FILE.toPath());
                        System.out.println("Default sound config copied!");
                    } else {
                        System.err.println("Default sound config NOT found in JAR!");
                    }
                }
            }

            // Now load JSON
            if (CONFIG_FILE.exists()) {
                try (FileReader reader = new FileReader(CONFIG_FILE)) {
                    Map<?, ?> map = GSON.fromJson(reader, Map.class);
                    if (map != null) {
                        for (Map.Entry<?, ?> entry : map.entrySet()) {
                            if (entry.getKey() instanceof String && entry.getValue() instanceof Number) {
                                volumeMap.put((String) entry.getKey(), ((Number) entry.getValue()).floatValue());
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static float getVolume(String soundId) {
        return volumeMap.getOrDefault(soundId, 1.0f);
    }

    public static void setVolume(String soundId, float volume) {
        volumeMap.put(soundId, volume);
        save();
    }

    public static Set<String> getAllSoundIds() {
        return Collections.unmodifiableSet(volumeMap.keySet());
    }

    private static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(volumeMap, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
