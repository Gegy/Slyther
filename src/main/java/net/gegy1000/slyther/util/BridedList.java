package net.gegy1000.slyther.util;

import java.util.*;

public class BridedList<E> extends AbstractList<E> {
    List<E> unveiled = new ArrayList<>();
    List<E> veiled = Collections.unmodifiableList(unveiled);

    public List<E> unmodifiable() {
        return veiled;
    }

    @Override
    public boolean add(E entry) {
        return unveiled.add(entry);
    }

    @Override
    public E set(int index, E element) {
        return unveiled.set(index, element);
    }

    @Override
    public void add(int index, E element) {
        unveiled.add(index, element);
    }

    @Override
    public E get(int index) {
        return unveiled.get(index);
    }

    @Override
    public E remove(int index) {
        return unveiled.remove(index);
    }

    @Override
    public void clear() {
        unveiled.clear();
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return unveiled.addAll(index, c);
    }

    @Override
    public Iterator<E> iterator() {
        return unveiled.iterator();
    }

    @Override
    public int size() {
        return unveiled.size();
    }
}
