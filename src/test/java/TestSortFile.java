import org.example.SortFile;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Time;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.*;

public class TestSortFile {
    private int countLineInSortedFile = 0;
    @Test
    public void test() {
        generate("data.txt", 1_000_000);
        long startTime = System.currentTimeMillis();
        File sortedFile = SortFile.sortFile(new File("data.txt"));
        long endTime = System.currentTimeMillis();
        System.out.println("затрачено секунд: " + (endTime - startTime) / 1000);
        assertTrue(isSorted(sortedFile));
        assertEquals(1_000_000, countLineInSortedFile);
        System.out.println();
    }

    private File generate(String name, int count) {
        Random random = new Random();
        File file = new File(name);
        try (PrintWriter pw = new PrintWriter(file)) {
            for (int i = 0; i < count; i++) {
                Long key = random.nextLong();
                String value = generateRandomString();
                pw.println(key + ":" + value);
            }
            pw.flush();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
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

    private boolean isSorted(File file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
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
