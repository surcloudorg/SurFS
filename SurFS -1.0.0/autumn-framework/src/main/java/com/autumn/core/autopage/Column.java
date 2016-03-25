package com.autumn.core.autopage;

import com.autumn.util.TextUtils;


/**
 * <p>Title: AUTOPAGE-数据显示的列属性</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class Column {

    private String caption = ""; //列标题
    private int width = 10; //列宽度（百分比）
    private String align = "left"; //对齐方式
    private String link = "";//连接如action.do?id=${id}
    private boolean check = false;//显示为一个复选框
    private String text = ""; //字段值${id}${icon.edit.png}
    private String orderField = null;//按照那个字段排序
    private String order = null;//asc/desc

    /**
     * @return the caption
     */
    public String getCaption() {
        return caption;
    }

    /**
     * @param caption the caption to set
     */
    public void setCaption(String caption) {
        if (caption != null) {
            this.caption = caption;
        }
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    public void setWidth(String width) {
        if (width != null) {
            try {
                this.width = Integer.parseInt(width);
            } catch (Exception e) {
            }
        }
    }

    /**
     * @return the align
     */
    public String getAlign() {
        return align;
    }

    /**
     * @param align the align to set
     */
    public void setAlign(String align) {
        if (align != null) {
            if (align.trim().equalsIgnoreCase("center")) {
                this.align = "center";
            } else if (align.trim().equalsIgnoreCase("right")) {
                this.align = "right";
            } else {
                this.align = "left";
            }
        }
    }

    /**
     * @return the link
     */
    public String getLink() {
        return link;
    }

    /**
     * @param link the link to set
     */
    public void setLink(String link) {
        if (link != null) {
            this.link = link;
        }
    }

    /**
     * @return the check
     */
    public boolean isCheck() {
        return check;
    }

    /**
     * @param check the check to set
     */
    public void setCheck(boolean check) {
        this.check = check;
    }

    public void setCheck(String check) {
        if (check != null) {
            try {
                this.check = TextUtils.parseBoolean(check);
            } catch (Exception e) {
            }
        }
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        if (text != null) {
            this.text = text;
        }
    }

    /**
     * @return the orderField
     */
    public String getOrderField() {
        return orderField;
    }

    /**
     * @param orderField the orderField to set
     */
    public void setOrderField(String orderField) {
        this.orderField = orderField;
    }

    /**
     * @return the order
     */
    public String getOrder() {
        return order;
    }

    /**
     * @param order the order to set
     */
    public void setOrder(String order) {
        this.order = order;
    }
}
