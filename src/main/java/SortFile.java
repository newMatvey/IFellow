package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class SortFile {
    private static long memory;

    private static long chengeMaxChankSize() {
        memory = Runtime.getRuntime().maxMemory() - 10_000;
        return memory;
    }

    public static File sortFile(File dataFile) {
        final long chunkSize = chengeMaxChankSize();
        File outputFile = new File("data_sorted.txt");
        List<String> chunkPaths = new ArrayList<>();
        try {
            int chankNum = 0;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile)))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    long currentBatchSize = 0;
                    List<String> chunk = new ArrayList<>();
                    long lineSize = 8 + line.length() * 2L;
                    while (currentBatchSize + lineSize <= chunkSize && line != null) {
                        currentBatchSize += lineSize;
                        chunk.add(line);
                        line = reader.readLine();
                    }
                    chunk = chunk.stream()
                            .sorted((str1, str2) -> {
                                long num1 = Long.parseLong(str1.split(":")[0]);
                                long num2 = Long.parseLong(str2.split(":")[0]);
                                return Long.compare(num1, num2);
                            })
                            .toList();
                    String chunkPath = "chunk_" + chankNum + ".txt";
                    chunkPaths.add(chunkPath);
                    try (FileWriter writer = new FileWriter(chunkPath)) {
                        for (String number : chunk) {
                            writer.write(number + "\n");
                        }
                    }
                    chankNum++;
                }
            }

            PriorityQueue<LineReader> queue = new PriorityQueue<>();
            try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
                for (String chunkPath : chunkPaths) {
                    queue.add(new LineReader(chunkPath));
                }
                while (!queue.isEmpty()) {
                    LineReader reader = queue.poll();
                    writer.println(reader.getValue() + ":" + reader.getString());
                    if (reader.next()) {
                        queue.offer(reader);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            int countChunk = 0;
            for (String chunkPath : chunkPaths) {
                countChunk++;
                new File(chunkPath).delete();
            }
            System.out.println("была выполнена сортировка за " + countChunk + " подходов к файлу");
        }
        return outputFile;
    }

    private static class LineReader implements Comparable<LineReader> {

        private final BufferedReader reader;
        private Long value;
        private String string;

        public LineReader(String filePath) throws IOException {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            next();
        }

        public String getString() {
            return string;
        }

        public Long getValue() {
            return value;
        }

        public boolean next() throws IOException {
            String str;
            if ((str = reader.readLine()) != null) {
                String[] toPars = str.split(":");
                value = Long.parseLong(toPars[0]);
                string = toPars[1];
                return true;
            } else {
                reader.close();
                return false;
            }
        }

        @Override
        public int compareTo(LineReader other) {
            return value.compareTo(other.getValue());
        }
    }
}
