package com.miniblas.model;

import com.miniblas.model.base.IOrdenableAdapteeCollection;

import java.util.Collection;
import java.util.List;

/**
 * Created by alberto on 13/11/14.
 */
public class MiniBlasCestaCollection implements IOrdenableAdapteeCollection<MiniBlasCesta> {
    private final List<MiniBlasCesta> baskets;

    public MiniBlasCestaCollection(List<MiniBlasCesta> baskets) {
        this.baskets = baskets;
    }

    @Override
    public int size() {
        return baskets.size();
    }

    @Override
    public MiniBlasCesta get(final int index) {
        return baskets.get(index);
    }

    @Override
    public void add(MiniBlasCesta element) {
        baskets.add(element);
    }

    @Override
    public void remove(MiniBlasCesta element) {
        baskets.remove(element);
    }

    @Override
    public void addAll(Collection<MiniBlasCesta> elements) {
        baskets.addAll(elements);
    }

    @Override
    public void removeAll(Collection<MiniBlasCesta> elements) {
        baskets.removeAll(elements);
    }

    @Override public void clear() {
        baskets.clear();
    }


    @Override
    public void add(int index, MiniBlasCesta element) {
        baskets.add(index, element);
    }
}
