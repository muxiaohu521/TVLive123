package com.github.tvbox.osc.bean;

import java.io.Serializable;

public class LiveSourceItem implements Serializable, Comparable<LiveSourceItem> {
    private String name;
    private String url;

    public LiveSourceItem() {
    }

    public LiveSourceItem(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int compareTo(LiveSourceItem o) {
        if (name == null) return 1;
        if (o.name == null) return -1;
        return name.compareTo(o.name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LiveSourceItem that = (LiveSourceItem) obj;
        return name != null && url != null && name.equals(that.name) && url.equals(that.url);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }
}