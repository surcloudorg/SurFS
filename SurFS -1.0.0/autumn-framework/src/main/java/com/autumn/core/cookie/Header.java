package com.autumn.core.cookie;

import java.util.*;

/**
 * <p>Title: Cookie工具</p>
 *
 * <p>Description: HttpHeader数据模型</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
@SuppressWarnings("unchecked")
public class Header extends AbstractCollection implements java.io.Serializable, java.lang.Cloneable {

    private static final long serialVersionUID = 20120701000008L;
    private String topLine;
    private List<HeaderEntry> theHeader;

    /**
     * 创建
     */
    public Header() {
        topLine = null;
        theHeader = new ArrayList();
    }

    /**
     * 创建
     *
     * @param c Collection->Header
     */
    public Header(Collection c) {
        topLine = null;
        theHeader = new ArrayList();
        addAll(c);
    }

    /**
     * 创建
     *
     * @param topLine
     * @param c
     */
    public Header(String topLine, Collection c) {
        this.topLine = topLine;
        theHeader = new ArrayList();
        addAll(c);
    }

    /**
     * 设置 the top-line of this Header.
     *
     * @param topLine
     */
    public void setTopLine(String topLine) {
        this.topLine = topLine;
    }

    /**
     * 获取 the top-line of this Header.
     *
     * @return topLine
     */
    public String getTopLine() {
        return (topLine);
    }

    /**
     * 添加参数
     */
    public boolean add(String key, String value) {
        return (add(new HeaderEntry(key, value)));
    }

    /**
     * 返回指定索引条目
     */
    public HeaderEntry getEntryAt(int index) {
        return ((HeaderEntry) theHeader.get(index));
    }

    /**
     * 返回指定索引条目名称
     */
    public String getHeaderFieldKey(int i) {
        return (getEntryAt(i).getKey());
    }

    /**
     * 返回指定索引条目参数值
     */
    public String getHeaderField(int i) {
        return (getEntryAt(i).getValue());
    }

    /**
     * 检测是否包含指定参数
     */
    public boolean containsKey(String s) {
        if (s == null) {
            throw new IllegalArgumentException("Key can't be null");
        }
        HeaderEntry he;
        Iterator iter = iterator();
        while (iter.hasNext()) {
            he = (HeaderEntry) iter.next();
            if (s.equalsIgnoreCase(he.getKey())) {
                return (true);
            }
        }
        return (false);
    }

    /**
     * 检测是否包含指定参数值
     */
    public boolean containsValue(String s) {
        HeaderEntry he;
        Iterator iter = iterator();
        while (iter.hasNext()) {
            he = (HeaderEntry) iter.next();
            if (s == null) {
                if (he.getValue() == null) {
                    return (true);
                }
            } else if (s.equals(he.getValue())) {
                return (true);
            }
        }
        return (false);
    }

    /**
     * 返回参数名为s的第一个条目
     */
    public HeaderEntry getFirstEntryForKey(String s) {
        return (getEntryForKey(s, -1));
    }

    /**
     * 返回参数值为s的第一个条目
     */
    public HeaderEntry getFirstEntryForValue(String s) {
        return (getEntryForValue(s, -1));
    }

    /**
     * 返回参数名为s的第一个条目
     */
    public HeaderEntry getEntryForKey(String s, int j) {
        if (s == null) {
            throw new IllegalArgumentException("Key can't be null");
        }
        if (j < 0) {
            j = 0;
        } else if (j > theHeader.size() - 1) {
            j = theHeader.size() - 1;
        }
        HeaderEntry he;
        for (int i = j + 1; i < theHeader.size(); i++) {
            try {
                he = getEntryAt(i);
            } catch (IndexOutOfBoundsException ioobe) {
                break;
            }
            if (s.equalsIgnoreCase(he.getKey())) {
                return (he);
            }
        }
        return (null);
    }

    /**
     * 返回参数值为s的第一个条目
     */
    public HeaderEntry getEntryForValue(String s, int j) {
        HeaderEntry he;
        if (j < 0) {
            j = 0;
        } else if (j > theHeader.size() - 1) {
            j = theHeader.size() - 1;
        }
        for (int i = j + 1; i < theHeader.size(); i++) {
            try {
                he = getEntryAt(i);
            } catch (IndexOutOfBoundsException ioobe) {
                break;
            }
            if (s == null) {
                if (he.getValue() == null) {
                    return (he);
                }
            } else if (s.equalsIgnoreCase(he.getValue())) {
                return (he);
            }
        }
        return (null);
    }

    /**
     * 返回Header指定参数名
     */
    public Header getEntriesForKey(String s) {
        if (s == null) {
            throw new IllegalArgumentException("Key can't be null.");
        }
        HeaderEntry he;
        Header h = new Header();
        Iterator iter = iterator();
        while (iter.hasNext()) {
            he = (HeaderEntry) iter.next();
            if (s.equalsIgnoreCase(he.getKey())) {
                h.add(he);
            }
        }
        return (h);
    }

    /**
     * 返回Header指定参数值
     */
    public Header getEntriesForValue(String s) {
        HeaderEntry he;
        Header h = new Header();
        Iterator iter = iterator();
        while (iter.hasNext()) {
            he = (HeaderEntry) iter.next();
            if (s == null) {
                if (he.getValue() == null) {
                    h.add(he);
                }
            } else if (s.equals(he.getValue())) {
                h.add(he);
            }
        }
        return (h);
    }

    /**
     * 加入条目
     * @param entry
     * @return  boolean
     */
    @Override
    public boolean add(Object entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Null entry.");
        } else if (!(entry instanceof HeaderEntry)) {
            throw new ClassCastException("Not a HeaderEntry");
        }
        if (contains(entry)) {
            return (false);
        }
        theHeader.add((HeaderEntry) entry);
        return (true);
    }

    @Override
    public Iterator iterator() {
        return (theHeader.iterator());
    }

    @Override
    public int size() {
        return (theHeader.size());
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return ("{}");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (topLine != null) {
            sb.append(topLine);
            sb.append("\n[");
        }
        for (int i = 0; i < theHeader.size(); i++) {
            sb.append(theHeader.get(i).toString());
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]}");
        return (sb.toString());
    }

    @Override
    public boolean isEmpty() {
        return (theHeader.isEmpty() && topLine == null);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return (super.clone());
    }
}
