import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

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
        assert arrayList.containsAll(originalList);
        assert originalList.containsAll(arrayList);
        for (int i = 1; i < array.length; i++) {
            assert array[i - 1] <= array[i];
        }
    }

    static void random(Consumer<Integer[]> consumer) {
        Integer[] arr = {
                10, 9, 8, 7, 6, 5, 4, 3, 2, 1
        };
        consumer.accept(arr);
    }

    public static void main(String[] args) throws FileNotFoundException {
        System.setOut(new PrintStream("out.txt"));
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
            assertArraySorted(array, original);
            System.out.println(iterations);
            if (PRINT_ARRAY) {
                printArray(array);
            }

            //System.out.println((endTime - startTime) / 1000);
        });

    }
}