package com.musicviz.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.musicviz.model.SynthPreset;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

// Saves and loads a List<SynthPreset> to/from a JSON file using Gson.
public class JsonUtil {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String PRESETS_PATH = "src/main/resources/presets/presets.json";

    public static void save(List<SynthPreset> presets) {
        try (Writer writer = new FileWriter(PRESETS_PATH)) {
            GSON.toJson(presets, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<SynthPreset> load() {
        try (Reader reader = new FileReader(PRESETS_PATH)) {
            Type listType = new TypeToken<List<SynthPreset>>() {}.getType();
            List<SynthPreset> result = GSON.fromJson(reader, listType);
            return result != null ? result : new ArrayList<>();
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
