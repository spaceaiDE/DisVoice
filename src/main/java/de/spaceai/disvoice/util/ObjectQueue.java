package de.spaceai.disvoice.util;

import com.google.common.collect.Lists;

import java.util.List;

public class ObjectQueue<T> {

    private List<T> list;

    public ObjectQueue() {
        this.list = Lists.newArrayList();
    }

    public void add(T param) {
        this.list.add(param);
    }

    public T getAndRemove() {
        T param = this.list.get(0);
        this.list.remove(param);
        return param;
    }

    public int size() {
        return this.list.size();
    }

    public boolean contains(T param) {
        return this.list.contains(param);
    }

}
