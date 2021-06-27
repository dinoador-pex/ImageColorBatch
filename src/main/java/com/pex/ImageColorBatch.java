package com.pex;

import com.opencsv.CSVWriter;
import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.*;

public class ImageColorBatch {

    public static int TOTAL_IMAGES = 0;
    public static int TOTAL_IMAGES_PROCESSED = 0;
    private final static int THREAD_POOL_SIZE = 5;

    private static Instant start;

    private static final String INPUT_FILE = "src/main/resources/input.txt";
    private static final String OUTPUT_FILE = "src/main/resources/output.csv";

    private static ThreadPoolExecutor executorService;

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    public static void main(String[] args) {
        start();
        executorService = (ThreadPoolExecutor)Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        Scanner sc = getScanner(new File(INPUT_FILE));
        while (sc.hasNextLine()) {
            process(read(sc));
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        end();
    }

    public static void start() {
        start = Instant.now();
        printMemory("start()");
        initializeOutput();
    }

    public static void end() {
        printStats(start, Instant.now());
        printMemoryAtEnd();
    }

    public static void taskStart() {
        printExecutorServiceStat();
        printMemory("taskStart()");
    }

    public static void taskEnd() {
        printMemory("taskEnd()");
    }

    public static void initializeOutput() {
        try {
            new PrintWriter(OUTPUT_FILE).close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Scanner getScanner(File file) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert inputStream != null;
        return new Scanner(inputStream);
    }

    public static String read(Scanner sc) {
        TOTAL_IMAGES++;
        return sc.nextLine();
    }

    public static void process(String line) {
        ImageColorTask imageColorTask = new ImageColorTask(line);
        executorService.submit(imageColorTask);
    }

    public static void printExecutorServiceStat() {
        System.out.println("Pool Size: " + executorService.getPoolSize());
        System.out.println("Queue Size: " + executorService.getQueue().size());
    }

    public static void printElapsedTime(Instant start, Instant end) {
        final long milliseconds = Duration.between(start, end).toMillis();
        final long minutes = (milliseconds / 1000) / 60;
        final long seconds = (milliseconds / 1000) % 60;
        System.out.println("Time Elapsed: " + String.format("%d:%02d", minutes, seconds));
    }

    public static void printStats(Instant start, Instant end) {
        printElapsedTime(start, end);
        System.out.println("TOTAL_IMAGES: " + TOTAL_IMAGES +
            "\nTOTAL_IMAGES_PROCESSED: " + TOTAL_IMAGES_PROCESSED);
    }

    public static void write(String[] line) {
        writeLineToCsv(line);
    }

    public static void writeLineToCsv(String[] line) {
        File file = new File(OUTPUT_FILE);
        try {
            FileWriter outputfile = new FileWriter(file, true);
            CSVWriter writer = new CSVWriter(outputfile);
            writer.writeNext(line);
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> T[] add2BeginningOfArray(T[] elements, T element) {
        T[] newArray = Arrays.copyOf(elements, elements.length + 1);
        newArray[0] = element;
        System.arraycopy(elements, 0, newArray, 1, elements.length);
        return newArray;
    }

    @SuppressWarnings({"BusyWait"})
    public static void printMemoryAtEnd() {
        long t = System.currentTimeMillis();
        long end = t + 2000;
        while(System.currentTimeMillis() < end) {
            printMemory("end()");
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void printMemory(String step) {
        System.gc();
        long id = Thread.currentThread().getId();
        String name = Thread.currentThread().getName();
        System.out.println("[thread id: " + id + ", thread name: " + name + "]" + " Memory usage at " + step + ": "
            + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1000*1000) + "M");
    }
}
