import java.util.Comparator;

public interface Sorting<T> {
    int sort(T[] array, Comparator<? super T> c);
}