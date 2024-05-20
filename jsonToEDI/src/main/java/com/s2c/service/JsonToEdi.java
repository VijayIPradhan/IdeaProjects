package com.s2c.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.s2c.exceptions.InvalidDataException;
import com.s2c.initializers.Positions;
import com.s2c.utils.FileWriter;
import io.micrometer.common.util.StringUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class JsonToEdi implements EDIConverter{
    private final Positions positions;
    private final FileWriter fileWriter;
    public JsonToEdi(Positions positions, FileWriter fileWriter) {
        this.positions = positions;
        this.fileWriter = fileWriter;
    }

    public JsonToEdi() {
        this(new Positions(), new FileWriter());
    }

    private final Map<String, StringBuilder> nonRepeatableLinesMap = new HashMap<>();
    private final Map<String, List<StringBuilder>> groupRepeatableMap = new HashMap<>();
    Set<String> keySet = new HashSet<>();
    List<StringBuilder> allEntries = new ArrayList<>();
    List<String> finalOutputFile=new ArrayList<>();
    private static List<String> segmentOrder = null;

    private static Map<String, String> POSITION = null;

    @Override
    public String convert(JsonNode jsonData, String agencyCode) {
        validateInput(jsonData, agencyCode);
        Path filePath = getFilePath();
        try {
            feedMappingDetails(agencyCode);
            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                fileWriter.writeFirstLines(writer);
                finalOutputFile.addAll(List.of("A0901SY2SECFIL111715     PE                                361634    361634     ","B  0901ME0PE                                               CRIMSONTEST1         "));
                processJsonData(jsonData, writer);
                fileWriter.writeLastLines(writer);
                finalOutputFile.addAll(List.of("Y  0901ME0PE                                                                    ","Z0901SY2      111715                                                            "));
                return convertListToString(finalOutputFile);
            } catch (IOException e) {
                throw new RuntimeException("Error in writing edi file, " + e.getMessage());
            }
        } catch (InvalidDataException e) {
            if (Files.exists(filePath)) {
                try {
                    Files.delete(filePath);
                } catch (IOException ex) {
                    throw new RuntimeException("Error deleting file, " + ex.getMessage());
                }
            }
            throw new InvalidDataException("Error processing JSON data, " + e.getMessage());
        }
    }
    public String convertListToString(List<String> list) {
        StringJoiner joiner = new StringJoiner("\n");
        list.forEach(joiner::add);
        return joiner.toString();
    }


    private void validateInput(JsonNode jsonData, String agencyCode) {
        if (jsonData == null) {
            throw new InvalidDataException("The provided json data is null");
        }
        if (StringUtils.isBlank(agencyCode)) {
            throw new InvalidDataException("Agency code can not be null or empty");
        }
    }

    private Path getFilePath() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());
        String fileName = "output_" + timestamp + ".txt";
        Path downloadsPath = Paths.get(System.getProperty("user.home"), "Downloads");
        return downloadsPath.resolve(fileName);
    }

    private void feedMappingDetails(String agencyCode) {
        if (agencyCode.equals("FDA")) {
            Map<List<String>, Map<String, String>> fda = positions.getFDA();
            if (!fda.isEmpty()) {
                Map.Entry<List<String>, Map<String, String>> entry = fda.entrySet().iterator().next();
                segmentOrder = entry.getKey();
                POSITION = entry.getValue();
            } else {
                throw new InvalidDataException("No mapping information found for the entered Agency code " + agencyCode);
            }
        } else {
            throw new InvalidDataException("No mapping information found for the entered Agency code " + agencyCode);
        }
    }

    //    private void sortLines() {
//        Stream.concat(nonRepeatableLinesMap.keySet().stream(), groupRepeatableMap.keySet().stream())
//                .filter(key -> !key.isEmpty())
//                .sorted(Comparator.comparingInt(segmentOrder::indexOf))
//                .filter(obj->!StringUtils.isBlank(obj))
//                .forEach(entry -> {
//                    if (nonRepeatableLinesMap.containsKey(entry)) {
//                        allEntries.add(nonRepeatableLinesMap.get(entry));
//                    } else if (groupRepeatableMap.containsKey(entry)) {
//                        allEntries.addAll(groupRepeatableMap.get(entry));
//                    } else {
//                        log.error("Not matching key :{}", entry);
//                    }
//                });
//    }
    private String sortLines() {
        List<String> sortedKeys = new ArrayList<>(nonRepeatableLinesMap.keySet());
        sortedKeys.addAll(groupRepeatableMap.keySet());
        sortedKeys = sortedKeys.stream().distinct().filter(key -> !key.isEmpty()).sorted(Comparator.comparingInt(segmentOrder::indexOf)).collect(Collectors.toList());
        String firstKey = sortedKeys.get(0);
        sortedKeys.forEach(entry -> {
            if (nonRepeatableLinesMap.containsKey(entry)) {
                allEntries.add(nonRepeatableLinesMap.get(entry));
            } else if (groupRepeatableMap.containsKey(entry)) {
                allEntries.addAll(groupRepeatableMap.get(entry));
            } else {
                System.out.println("Not matching key :"+entry);
            }
        });
        return firstKey;
    }

    private void processJsonData(JsonNode jsonData, BufferedWriter writer) {
        jsonData.fields().forEachRemaining(data -> {
            if (!data.getValue().isArray() && !data.getValue().isObject()) {
                String pgSegment = processField(data.getKey(), data.getValue().asText());
                keySet.add(pgSegment);
            } else if (data.getValue().isArray()) {
                processRootArray(data.getValue(), writer);
            }
        });
    }

    private void processRootArray(JsonNode jsonNode, BufferedWriter writer) {
        jsonNode.forEach(element -> {
            if (element.isObject()) {
                processObject(element);
            }
            writeToFileAndReset(writer);
        });
    }

    private void writeToFileAndReset(BufferedWriter writer) {
        processSegment(keySet);
        sortLines();
        fileWriter.writeToFile(allEntries, writer);
        finalOutputFile.addAll(allEntries.stream()
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .toList()
        );
        allEntries.clear();
        groupRepeatableMap.clear();
        nonRepeatableLinesMap.clear();
    }

    private void processArray(JsonNode arrayNode) {
        arrayNode.forEach(element -> {
            if (element.isObject()) {
                Set<String> pgSegment = processObject(element);
                processSegment(pgSegment);
            }
        });
    }

    private Set<String> processObject(JsonNode element) {
        Set<String> pgSegmentTracker = new HashSet<>();
        element.fields().forEachRemaining(field -> {
            String fieldName = field.getKey();
            JsonNode fieldValue = field.getValue();
            if (!fieldValue.isArray() && !fieldValue.isObject()) {
                String pgSegment = processField(fieldName, fieldValue.asText());
                pgSegmentTracker.add(pgSegment);
            } else if (fieldValue.isArray()) {
                processArray(fieldValue);
            }
        });
        return pgSegmentTracker;
    }

    private void processSegment(Set<String> pgSegment) {
        List<String> sortedValues = pgSegment.stream()
                .sorted(Comparator.comparingInt(segmentOrder::indexOf))
                .filter(Objects::nonNull)
                .toList();
        String firstPgSegment = sortedValues.get(0);
        sortedValues.forEach(segment -> {
            if (nonRepeatableLinesMap.containsKey(segment)) {
                groupRepeatableMap.computeIfAbsent(firstPgSegment, k -> new ArrayList<>())
                        .add(nonRepeatableLinesMap.get(segment));
                nonRepeatableLinesMap.remove(segment);
            }
        });
    }

    private String processField(String fieldName, String fieldValue) {
        String pgSegment = null;
        try {
            if (!StringUtils.isBlank(fieldValue)) {
                if (POSITION.containsKey(fieldName)) {
                    String[] mapping = POSITION.get(fieldName).split("/");
                    if (mapping.length != 6) {
                        throw new InvalidDataException("Invalid number of mapping fields for the field " + fieldName + " (expected 6, actual " + mapping.length + ")");
                    }
                    pgSegment = mapping[0];
                    int length = Integer.parseInt(mapping[1]);
                    if (fieldValue.length() > length) {
                        throw new InvalidDataException("Length of field value '" + fieldValue + "' exceeds the maximum length for the key '" + fieldName + "' ( expected " + length + " , actual " + fieldValue.length() + " )");
                    }
                    int startPosition = Integer.parseInt(mapping[2]) - 1;
                    char alignment = mapping[4].charAt(0);
                    updateLine(fieldValue, pgSegment, alignment, length, startPosition);
                }
            }
        } catch (NumberFormatException e) {
            throw new InvalidDataException("Error parsing integer value in mapping for field " + fieldName + ". Verify the mapping details for the key " + fieldName + " provided.");
        }
        return pgSegment;
    }

    private void updateLine(String fieldValue, String pgSegment, char alignment, int length, int startPosition) {
        if (!nonRepeatableLinesMap.containsKey(pgSegment)) {
            StringBuilder sb = new StringBuilder();
            sb.append(pgSegment);
            sb.append(" ".repeat(80 - pgSegment.length()));
            nonRepeatableLinesMap.put(pgSegment, sb);
        }
        StringBuilder currentLine = nonRepeatableLinesMap.get(pgSegment);
        if (alignment == 'R') {
            fieldValue = String.format("%" + length + "s", fieldValue).replace(' ', '0');
        }
        currentLine.replace(startPosition, startPosition + fieldValue.length(), fieldValue);
        nonRepeatableLinesMap.put(pgSegment, currentLine);
    }

}