package net.vibey.wariumdj;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Collections;

public class djConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File FILE = new File("config/djConfig.json");

    private static final Map<String, Float> volumeMap = new HashMap<>();

    static {
        load();
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

    protected static void load() {
        File configFile = new File("config/djConfig");
        if (!configFile.exists()) {
            // First run: copy default JSON from resources
            try (InputStream stream = djConfigManager.class.getClassLoader().getResourceAsStream("assets/wariumdj/config/default_volumes.json")) {
                if (stream != null) {
                    Files.createDirectories(configFile.getParentFile().toPath());
                    Files.copy(stream, configFile.toPath());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Now load the JSON as usual
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                Map<?, ?> map = GSON.fromJson(reader, Map.class);
                if (map != null) {
                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                        if (entry.getKey() instanceof String && entry.getValue() instanceof Number) {
                            volumeMap.put((String) entry.getKey(), ((Number) entry.getValue()).floatValue());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void save() {
        try (FileWriter writer = new FileWriter(FILE)) {
            GSON.toJson(volumeMap, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
