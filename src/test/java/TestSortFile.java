import com.IFellowTask.SortFile;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class TestSortFile {
    private int countLineInSortedFile = 0;
    private final File file = Path.of("data.txt").toFile();

    @Test
    public void test() {
//        generate();
//        long startTime = System.currentTimeMillis();
//        SortFile.sortFile(file);
//        long endTime = System.currentTimeMillis();
//        System.out.println("затрачено секунд: " + (endTime - startTime) / 1000);
//        assertTrue(isSorted());
//        assertEquals(30_500_000, countLineInSortedFile);
    }

    private void generate() {
        Random random = new Random();
        try (PrintWriter pw = new PrintWriter(file)) {
            for (int i = 0; i < 30_500_000; i++) {
                Long key = random.nextLong();
                String value = generateRandomString();
                pw.println(key + ":" + value);
            }
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String generateRandomString() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomString = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int index = new Random().nextInt(characters.length());
            randomString.append(characters.charAt(index));
        }
        return randomString.toString();
    }

    private boolean isSorted() {
        try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            long prev = Long.MIN_VALUE;
            String line;
            while ((line = reader.readLine()) != null) {
                countLineInSortedFile++;
                String[] keyValue = line.split(":");
                long current = Long.parseLong(keyValue[0]);
                if (current < prev) {
                    return false;
                } else {
                    prev = current;
                }
            }
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
