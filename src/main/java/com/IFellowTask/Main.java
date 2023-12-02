package com.IFellowTask;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Пожалуйста, укажите путь к файлу для обработки!");
            return;
        }
        String filePath = args[0];
        try {
            long startTime = System.currentTimeMillis();
            SortFile.sortFile(new File(filePath));
            long endTime = System.currentTimeMillis();
            System.out.println("затрачено секунд: " + (endTime - startTime) / 1000);
        } catch (OutOfMemoryError e) {
            throw new RuntimeException(e);
        }
    }
}