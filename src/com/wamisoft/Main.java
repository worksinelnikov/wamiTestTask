package com.wamisoft;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        final String startLog = "resources/tag_read_start.log";
        final String finishLog = "resources/tag_reads_finish.log";
        try {
            Map<String, Instant> startMap = new HashMap<>();
            Files.readAllLines(new File(startLog).toPath(), Charset.defaultCharset()).forEach(l -> startMap.putIfAbsent(l.substring(4, 16), getInstantFromString(l.substring(20, 32))));

            Map<String, Instant> finishMap = new HashMap<>();
            Files.readAllLines(new File(finishLog).toPath(), Charset.defaultCharset()).forEach(l -> finishMap.put(l.substring(4, 16), getInstantFromString(l.substring(20, 32))));

            Map<String, Long> durationMap = new HashMap<>();
            finishMap.keySet().forEach(k -> startMap.keySet().stream().filter(k::equals).forEach(keystart -> durationMap.put(k, Duration.between(startMap.get(k), finishMap.get(k)).getSeconds())));

            getSortedMap(durationMap).entrySet().stream().limit(10).forEach(c -> System.out.println(c.getKey() + "---" + c.getValue()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static Instant getInstantFromString(String string) {
        return LocalDateTime.parse(string, DateTimeFormatter.ofPattern("yyMMddHHmmss")).atZone(ZoneId.of("Europe/Paris")).toInstant();
    }

    private static Map<String, Long> getSortedMap(Map<String, Long> source) {
        return source.entrySet()
                .stream()
                .sorted((i1, i2) -> i1.getValue().compareTo(i2.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
}
