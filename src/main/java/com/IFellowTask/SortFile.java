package com.IFellowTask;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Класс SortFile сортирует большие файлы, которые не помещаются в память.
 */
public class SortFile {
    private static final long memory = (long) (Runtime.getRuntime().freeMemory() * 0.8);
    private static final Logger logger = LoggerFactory.getLogger(SortFile.class);

    /**
     * Сортирует файл, используя внешнюю сортировку.
     *
     * @param dataFile файл для сортировки
     */
    public static void sortFile(File dataFile) {
        File outputFile = new File("data_sorted.txt");
        List<Path> chunkPaths = new ArrayList<>();
        int chunkNum = 0;

        try (LineIterator lineIterator = FileUtils.lineIterator(dataFile, "UTF-8")) {
            while (lineIterator.hasNext()) {
                long currentChunkSize = 0;
                List<String> chunk = new ArrayList<>();
                String line = lineIterator.nextLine();
                long lineSizeInByte = 8 + line.length() * 2L;
                while (currentChunkSize + lineSizeInByte <= memory && line != null) {
                    currentChunkSize += lineSizeInByte;
                    chunk.add(line);
                    if (lineIterator.hasNext()) {
                        line = lineIterator.nextLine();
                    } else {
                        line = null;
                    }
                }
                chunk.sort(Comparator.comparingLong(s -> Long.parseLong(s.split(":")[0])));
                Path chunkPath = Path.of("chunk_" + chunkNum + ".txt");
                chunkPaths.add(chunkPath);
                Files.write(chunkPath, chunk);
                chunkNum++;
            }

            PriorityQueue<LineReader> queue = new PriorityQueue<>();
            try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(outputFile.toPath()))) {
                for (Path chunkPath : chunkPaths) {
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
            logger.error("Произошла ошибка при сортировке файла", e);
            throw new RuntimeException(e);
        } finally {
            chunkPaths.forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    logger.warn("Не удалось удалить временный файл: {}", path);
                }
            });
            try {
                String name = dataFile.getName();
                Files.delete(dataFile.toPath());
                outputFile.renameTo(new File(name));
            } catch (IOException e) {
                logger.warn("Не удалось удалить файл: {}", dataFile);
            }
            logger.info("Файл был разделен на {} частей", chunkNum);
        }
    }

    /**
     * Вспомогательный класс для чтения строк из файла и сравнения их.
     */
    private static class LineReader implements Comparable<LineReader> {

        private final BufferedReader reader;
        private Long value;
        private String string;

        /**
         * Создает новый объект LineReader для чтения строк из файла.
         *
         * @param filePath путь к файлу
         * @throws IOException если произошла ошибка при чтении файла
         */
        public LineReader(Path filePath) throws IOException {
            reader = Files.newBufferedReader(filePath);
            next();
        }

        public String getString() {
            return string;
        }

        public Long getValue() {
            return value;
        }

        /**
         * Читает следующую строку из файла.
         *
         * @return true, если строка была прочитана, false, если файл закончился
         * @throws IOException если произошла ошибка при чтении файла
         */
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
