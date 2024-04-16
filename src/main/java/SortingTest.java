import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

public class SortingTest {
    static final int ARRAY_SIZE = 10;
    static final boolean PRINT_ARRAY = false;

    static <T> void fillArray(RandomElementGenerator<T> generator, T[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = generator.generate(i);
        }
    }

    static <T> void printArray(T[] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i]);
            System.out.print(" ");
        }
        System.out.println();
    }

    static void genFile(String filename, int count) {
        try {
            var writer = new PrintStream("res/" + filename);
            var arr = new Integer[count];
            fillArray(index -> (int) (Math.random() * 10000), arr);
            for (int element : arr) {
                writer.println(element);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Can't write file");
        }
    }

    static void genFiles() {
        new File("res").mkdir();
        var random = new Random();
        for (int i = 0; i < 100; i++) {
            genFile(i + ".txt", random.nextInt(100, 10000));
        }
    }

    static void readFiles(Consumer<Integer[]> consumer) {
        try {
            System.setOut(new PrintStream("out.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("Can't set out");
        }
        var files = new File("res").listFiles();
        assert files != null;
        for (File file : files) {
            int[] arr = null;
            try {
                var reader = new BufferedReader(new FileReader(file));
                arr = reader.lines().mapToInt(Integer::parseInt).toArray();
            } catch (Exception e) {
                System.out.println("Can't read file");
            }
            consumer.accept(Arrays.stream(arr).boxed().toArray(Integer[]::new));
        }
    }

    private static void assertArraySorted(Integer[] array, Integer[] original) {
        List<Integer> arrayList = Arrays.asList(array);
        List<Integer> originalList = Arrays.asList(original);
        check(arrayList.containsAll(originalList), () -> {});
        check(originalList.containsAll(arrayList), () -> {});
        for (int i = 1; i < array.length; i++) {
            int finalI = i;
            check(array[i - 1] <= array[i], () -> System.out.println(finalI));
        }
    }

    private static void check(boolean c, Runnable b) {
        if (!c) {
            b.run();
            throw new AssertionError();
        }
    }

    static void random(Consumer<Integer[]> consumer) {
        Integer[] arr = {
                6547,
                8977,
                5173,
                742,
                6926,
                3478,
                7440,
                8836,
                6661,
                1402,
                5019,
                8274,
                7045,
                6654,
                1681,
                5561,
                1073,
                7259,
                4914,
                724,
                8778,
                8192,
                6447,
                3157,
                5433,
                5396,
                4756,
                3942,
                6275,
                85,
                7372,
                9478,
                9465,
                9334,
                4590,
                5176,
                1057,
                2328,
                9056,
                3377,
                1898,
                1638,
                7602,
                6692,
                6583,
                5402,
                2729,
                6492,
                8860,
                9412,
                1152,
                6419,
                6215,
                7733,
                5944,
                2489,
                8736,
                6736,
                6990,
                4577,
                3850,
                183,
                1707,
                8933,
                9004,
                2360,
                4262,
                7714,
                8964,
                2160,
        };
        consumer.accept(arr);
    }

    public static void main(String[] args) {
        readFiles(array -> {
            if (PRINT_ARRAY) {
                printArray(array);
            }
            Integer[] original = new Integer[array.length];
            System.arraycopy(array, 0, original, 0, array.length);
            Sorting<Integer> sorting = new TimSort<>();
            long startTime = System.nanoTime();
            int iterations = sorting.sort(array, Integer::compare);
            long endTime = System.nanoTime();
            if (PRINT_ARRAY) {
                printArray(array);
            }
            assertArraySorted(array, original);
            //System.out.println(iterations);

            System.out.println((endTime - startTime) / 1e6);
        });

    }
}