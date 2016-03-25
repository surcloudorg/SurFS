package com.autumn.core.cookie;

/**
 * <p>Title: Cookie工具</p>
 *
 * <p>Description: HttpHeader数据模型-条目</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class HeaderEntry implements Cloneable {

    private String key;
    private String value;

    private HeaderEntry() {
    }

    /**
     * 创建
     *
     * @param key
     * @param value
     */
    public HeaderEntry(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("The Key can't be null");
        }
        this.key = key;
        this.value = value;
    }

    /**
     * Gets the Key/Name.
     */
    public String getKey() {
        return (key);
    }

    /**
     * Gets the Value.
     */
    public String getValue() {
        return (value);
    }

    @Override
    public boolean equals(Object o) {
        int i = 0;
        if (o instanceof HeaderEntry) {
            HeaderEntry x = (HeaderEntry) o;
            if (key.equalsIgnoreCase(x.getKey())) {
                i++;
            }
            if (value != null) {
                if (value.equals(x.getValue())) {
                    i++;
                }
            } else if (x.getValue() == null) {
                i++;
            }
        }
        if (i != 2) {
            return (false);
        }
        return (true);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.key != null ? this.key.hashCode() : 0);
        hash = 23 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return (key + ":" + value);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return (super.clone());
    }
}
