import java.util.*;

public class TimSort<T> implements Sorting<T> {
    private record Run(int start, int size) {
    }

    @Override
    public int sort(T[] array, Comparator<? super T> c) {
        int iterations = 0;
        ArrayDeque<Run> runs = new ArrayDeque<>(array.length);
        iterations += runs(array, runs, c);
        return iterations;
    }

    private int runs(T[] array, ArrayDeque<Run> runs, Comparator<? super T> c) {
        int iterations = 0;
        int run = 1;
        int minRun = minRun(array.length);
        boolean isMore = c.compare(array[0], array[1]) > 0;
        for (int p = 1; p < array.length; p++) {
            iterations++;
            boolean currentIsMore = c.compare(array[p - 1], array[p]) > 0;
            if (currentIsMore == isMore) {
                run++;
            } else {
                int start = p - run;
                if (run < minRun) {
                    int end = Math.min(start + minRun - 1, array.length - 1);
                    if (isMore) {
                        iterations += reverse(array, start, p - 1);
                    }
                    iterations += insertSort(array, start, end, c);
                    runs.add(new Run(start, Math.min(minRun, array.length - start)));
                    p = end + 1;
                    tryMerge(array, runs, c);
                } else {
                    if (isMore) {
                        iterations += reverse(array, start, p - 1);
                    }
                    runs.add(new Run(p - run, run));
                    tryMerge(array, runs, c);
                }
                if (p + 1 < array.length) {
                    isMore = c.compare(array[p], array[p + 1]) > 0;
                }
                run = 1;
            }
        }
        Run last;
        if (runs.isEmpty()) {
            last = new Run(0, 0);
        } else {
            last = runs.getLast();
        }
        if (last.start + last.size < array.length) {
            int start = last.start + last.size;
            insertSort(array, start, array.length - 1, c);
            runs.add(new Run(start, array.length - start));
        }
        while (runs.size() > 1) {
            Run y = runs.pollLast();
            Run z = runs.pollLast();
            if (y.start < z.start) {
                iterations += merge(array, y.start, z.start - 1, z.start + z.size - 1, c);
                runs.add(new Run(y.start, y.size + z.size));
            } else  {
                iterations += merge(array, z.start, y.start - 1, y.start + y.size - 1, c);
                runs.add(new Run(z.start, y.size + z.size));
            }
        }
        return iterations;
    }

    private int tryMerge(T[] array, Deque<Run> runs, Comparator<? super T> c) {
        if (runs.size() < 3) return 0;
        int iterations = 0;
        Run z = runs.pollLast();
        Run y = runs.pollLast();
        Run x = runs.pollLast();
        if (x.size > y.size + z.size && y.size > z.size) {
            runs.add(x);
            runs.add(y);
            runs.add(z);
            return 0;
        } else if (x.size < z.size) {
            if (x.start < y.start) {
                iterations += merge(array, x.start, y.start - 1, y.start + y.size - 1, c);
            } else {
                iterations += merge(array, y.start, x.start - 1, x.start + x.size - 1, c);
            }
            runs.add(new Run(x.start, x.size + y.size));
            runs.add(z);
        } else {
            if (y.start < z.start) {
                iterations += merge(array, y.start, z.start - 1, z.start + z.size - 1, c);
            } else {
                iterations += merge(array, z.start, y.start - 1, y.start + y.size - 1, c);
            }
            runs.add(x);
            runs.add(new Run(y.start, y.size + z.size));
        }
        return iterations;
    }

    private <T1> int reverse(T1[] target, int start, int end) {
        int iterations = 0;
        for (int i = 0; i < (end - start) / 2 + 1; i++) {
            swap(target, start + i, end - i);
            iterations++;
        }
        return iterations;
    }

    private <T1> void swap(T1[] target, int i1, int i2) {
        T1 buf = target[i1];
        target[i1] = target[i2];
        target[i2] = buf;
    }

    private int minRun(int n) {
        int r = 0;
        while (n >= 64) {
            r |= n & 1;
            n >>= 1;
        }
        return n + r;
    }

    public int insertSort(T[] arr, int start, int end, Comparator<? super T> c) {
        int iterations = 0;
        for (int i = start; i <= end; i++) {
            int j = i;
            while (j > start && c.compare(arr[j], arr[j - 1]) < 0) {
                T temp = arr[j];
                arr[j] = arr[j - 1];
                arr[j - 1] = temp;
                j--;
                iterations++;
            }
        }
        return iterations;
    }

    private int merge(T[] arr, int start, int middle, int end, Comparator<? super T> c) {
        int iterations = 0;
        int len1 = middle - start + 1;
        int len2 = end - middle;
        T[] left = (T[]) new Object[len1];
        T[] right = (T[]) new Object[len2];
        for (int i = 0; i < len1; i++)
            left[i] = arr[start + i];
        iterations += len1;
        for (int i = 0; i < len2; i++)
            right[i] = arr[middle + 1 + i];
        iterations += len2;
        int i = 0;
        int j = 0;
        int k = start;

        while (i < len1 && j < len2) {
            if (c.compare(left[i], right[j]) <= 0) {
                arr[k] = left[i];
                i++;
            } else {
                arr[k] = right[j];
                j++;
            }
            k++;
            iterations++;
        }

        while (i < len1) {
            arr[k] = left[i];
            k++;
            i++;
            iterations++;
        }

        while (j < len2) {
            arr[k] = right[j];
            k++;
            j++;
            iterations++;
        }
        return iterations;
    }
}