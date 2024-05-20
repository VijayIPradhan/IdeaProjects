package com.s2c.utils;


import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class FileWriter {

    public void writeToFile(List<StringBuilder> lines, BufferedWriter writer) {
        lines.stream()
                .filter(Objects::nonNull)
                .forEach(line -> {
                    try {
                        writer.write(String.valueOf(line));
                        writer.newLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
    public void writeFirstLines(BufferedWriter writer) throws IOException {
        writer.write("A0901SY2SECFIL111715     PE                                361634    361634     ");
        writer.newLine();
        writer.write("B  0901ME0PE                                               CRIMSONTEST1         ");
        writer.newLine();
    }

    public void writeLastLines(BufferedWriter writer) throws IOException {
        writer.write("Y  0901ME0PE                                                                    ");
        writer.newLine();
        writer.write("Z0901SY2      111715                                                            ");
        writer.newLine();
    }
}