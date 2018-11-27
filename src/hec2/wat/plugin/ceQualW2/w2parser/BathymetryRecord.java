package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class BathymetryRecord<T> implements Collection<T> {
    private List<T> values;
    private String identifier;

    public BathymetryRecord(String identifier) {
        values = new ArrayList<>();
        this.identifier = identifier;
    }

    BathymetryRecord(List<T> values, String identifier) {
        this.values = values;
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public List<T> getValues() {
        return values;
    }

    public void setValues(List<T> values) {
        this.values = values;
    }

    /**
     * Determine the number of significant digits
     * (to the left of the decimal point, if it is a
     * floating point number).
     */
    int getCharacteristic() {
        int characteristic = 0;
        for (T value : values) {
            int c = (int) (Math.floor(Math.log10((Double) value)) + 1);
            if (c > characteristic)
                characteristic = c;
        }
        return characteristic;
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return values.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return values.iterator();
    }

    @Override
    public Object[] toArray() {
        return values.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return values.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return values.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return values.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return values.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return values.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return values.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return values.retainAll(c);
    }

    @Override
    public void clear() {
        values.clear();
    }

    public void set(int index, T value) {
        values.set(index, value);
    }

    public T get(int index) {
        return values.get(index);
    }
}
