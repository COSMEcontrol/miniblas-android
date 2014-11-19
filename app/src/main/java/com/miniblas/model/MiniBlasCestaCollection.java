package com.miniblas.model;

import com.miniblas.model.base.IOrdenableAdapteeCollection;
import com.pedrogomez.renderers.AdapteeCollection;

import java.util.Collection;
import java.util.List;

/**
 * Created by alberto on 13/11/14.
 */
public class MiniBlasCestaCollection implements IOrdenableAdapteeCollection<MiniBlasCesta> {
    private final List<MiniBlasCesta> videos;

    public MiniBlasCestaCollection(List<MiniBlasCesta> videos) {
        this.videos = videos;
    }

    @Override
    public int size() {
        return videos.size();
    }

    @Override
    public MiniBlasCesta get(final int index) {
        return videos.get(index);
    }

    @Override
    public void add(MiniBlasCesta element) {
        videos.add(element);
    }

    @Override
    public void remove(MiniBlasCesta element) {
        videos.remove(element);
    }

    @Override
    public void addAll(Collection<MiniBlasCesta> elements) {
        videos.addAll(elements);
    }

    @Override
    public void removeAll(Collection<MiniBlasCesta> elements) {
        videos.removeAll(elements);
    }

    @Override public void clear() {
        videos.clear();
    }


    @Override
    public void add(int index, MiniBlasCesta element) {
        videos.add(index, element);
    }
}
