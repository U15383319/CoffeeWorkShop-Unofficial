package net.langball.coffee.content;

import net.langball.coffee.CoffeeWork;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ContentCatalog {
    public static final List<String> BLOCK_IDS = readLines("blocks.txt");
    public static final List<String> ITEM_IDS = readLines("items.txt");
    public static final Set<String> FOOD_IDS = readSet("foods.txt");
    public static final Set<String> DRINK_IDS = readSet("drinks.txt");
    public static final Map<String, String> BLOCK_KINDS = readTable("block_kinds.tsv");

    private ContentCatalog() {
    }

    private static List<String> readLines(String name) {
        try (BufferedReader reader = open(name)) {
            return reader.lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .toList();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load Coffee Workshop content catalog " + name, exception);
        }
    }

    private static Set<String> readSet(String name) {
        return Collections.unmodifiableSet(new LinkedHashSet<>(readLines(name)));
    }

    private static Map<String, String> readTable(String name) {
        Map<String, String> values = new LinkedHashMap<>();
        for (String line : readLines(name)) {
            String[] columns = line.split("\\t", 2);
            if (columns.length != 2) {
                throw new IllegalStateException("Invalid catalog row in " + name + ": " + line);
            }
            values.put(columns[0], columns[1]);
        }
        return Collections.unmodifiableMap(values);
    }

    private static BufferedReader open(String name) {
        String path = "data/" + CoffeeWork.MODID + "/catalog/" + name;
        InputStream stream = ContentCatalog.class.getClassLoader().getResourceAsStream(path);
        if (stream == null) {
            throw new IllegalStateException("Missing required classpath resource: " + path);
        }
        return new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }
}
