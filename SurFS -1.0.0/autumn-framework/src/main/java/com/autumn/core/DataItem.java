/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core;

import java.io.*;

/**
 * <p>Title: 数据指令行指令参数</p>
 *
 * <p>Description:参数和值之间以“=”分隔，如果值含特殊字符需要加码则以“:=”分隔。指令标识和参数名都不区分大小写
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public final class DataItem implements Externalizable {

    private static final long serialVersionUID = 20120701000001L;
    private String itemID = ""; //参数标识
    protected String strEnc = "";//编码
    protected boolean needDoCode = false; //是否要输出时加码或在输入时解码
    protected byte[] itemParam;  //参数值

    public DataItem() {
    }

    public DataItem(String newItemID, String itemParam) {
        setItemID(newItemID);
        this.itemParam = strToByte(itemParam);
    }

    /**
     * 创建参数对象
     *
     * @param newItemID 参数名
     * @param itemParam 参数值，如果needDoCode，则是加过码的参数值
     * @param needDoCode 指出itemParam是否已经加过码
     */
    public DataItem(String newItemID, String itemParam, boolean needDoCode) {
        setItemID(newItemID);
        if (needDoCode) {
            this.itemParam = DataCommand.deCode(itemParam);
        } else {
            this.itemParam = strToByte(itemParam);
        }
        this.needDoCode = needDoCode;
    }

    /**
     * 创建参数对象
     *
     * @param newItemID 参数名
     * @param itemParam 参数字节数组
     * @param needDoCode 指出参数值是否需要加码
     */
    public DataItem(String newItemID, byte[] itemParam, boolean needDoCode) {
        setItemID(newItemID);
        this.itemParam = itemParam;
        this.needDoCode = needDoCode;
    }

    /**
     * 创建参数对象
     *
     * @param newItemID 参数名
     * @param itemParam 参数字节数组
     * @param needDoCode 指出参数值是否需要加码
     * @param strEnc 参数值的内码格式，例如“UTF-8”等
     */
    public DataItem(String newItemID, byte[] itemParam, boolean needDoCode, String strEnc) {
        setItemID(newItemID);
        this.strEnc = strEnc.trim();
        this.itemParam = itemParam;
        this.needDoCode = needDoCode;
    }

    /**
     * 参数串，参数标识和参数值之间如果不需加码解码以“=”，需要则以“:=”分隔
     *
     * @param strItem
     */
    public DataItem(String strItem) {
        initItem(strItem);
    }

    /**
     * 创建参数对象
     *
     * @param strItem 参数串，参数标识和参数值之间如果不需加码解码以“=”，需要则以“:=”分隔
     * @return 创建的实例
     */
    public static DataItem newItem(String strItem) {
        if (strItem.length() == 0) {
            return null;
        }
        return new DataItem(strItem);
    }

    /**
     * 根据对象默认的编码把字符串转换成字节数组
     *
     * @param str 源字符串
     * @return 结果字节数组
     */
    private byte[] strToByte(String str) {
        byte[] rc;
        if (str.length() == 0) {
            return null;
        }
        if (strEnc.length() > 0) {
            try {
                rc = str.getBytes(strEnc);
            } catch (UnsupportedEncodingException ex) {
                rc = str.getBytes();
            }
        } else {
            rc = str.getBytes();
        }
        return rc;
    }

    /**
     * 根据对象默认的编码把字节数组转换成字符串
     *
     * @param buf 源字节数组
     * @return 结果字符串
     */
    private String byteToStr(byte[] buf) {
        String rc;
        if (buf == null) {
            return "";
        }
        if (strEnc.length() > 0) {
            try {
                rc = new String(buf, strEnc);
            } catch (UnsupportedEncodingException ex) {
                rc = new String(buf);
            }
        } else {
            rc = new String(buf);
        }
        return rc;
    }

    /**
     * 返回byte[]值
     *
     * @return byte[]
     */
    public byte[] getValueBytes() {
        return itemParam;
    }

    /**
     * 克隆
     *
     * @return DataItem
     */
    public DataItem newCopy() {
        DataItem rc = new DataItem();
        rc.setItemID(getItemID());
        rc.itemParam = itemParam;
        rc.needDoCode = needDoCode;
        return rc;
    }

    /**
     * 获取对象代表的参数串
     *
     * @return 参数串
     */
    public String getItemResult() {
        if (needDoCode) {
            return getItemID() + ":=" + DataCommand.enCode(itemParam);
        } else {
            return getItemID() + "=" + byteToStr(itemParam);
        }
    }

    /**
     * 获取对象代表的日志参数串，加码串将都被解码以在日志中方便浏览
     *
     * @return 日志参数串
     */
    public String getDispItemResult() {
        String strResult;
        if (needDoCode) {
            strResult = getItemID() + ":=" + byteToStr(itemParam);
        } else {
            strResult = getItemID() + "=" + byteToStr(itemParam);
        }
        if (strResult.compareTo("=") == 0 || strResult.compareTo(":=") == 0) {
            return "";
        } else {
            return strResult;
        }
    }

    /**
     * 以参数串初始化该对象
     *
     * @param strItem 参数串，参数标识和参数值之间如果不需加码解码以“=”，需要则以“:=”分隔
     */
    public void initItem(String strItem) {
        strItem = strItem.trim();
        if (strItem.length() > 0 && strItem.charAt(0) == '&') {
            strItem = strItem.substring(1);
        }
        int pos = strItem.indexOf('=');
        if (pos < 0) {
            setItemID(strItem.toLowerCase());
            itemParam = null;
            return;
        }
        if (pos == 0) {
            setItemID("");
            if (strItem.length() > 1) {
                itemParam = strToByte(strItem.substring(1).trim());
            } else {
                itemParam = null;
            }
            return;
        }
        if (strItem.charAt(pos - 1) == ':') {
            needDoCode = true;
            setItemID(strItem.substring(0, pos - 1).toLowerCase());
            if (strItem.length() > pos + 1) {
                itemParam = DataCommand.deCode(strItem.substring(pos + 1).trim());
            } else {
                itemParam = null;
            }
        } else {
            needDoCode = false;
            setItemID(strItem.substring(0, pos).toLowerCase());
            if (strItem.length() > pos + 1) {
                itemParam = strToByte(strItem.substring(pos + 1).trim());
            } else {
                itemParam = null;
            }
        }
    }

    /**
     * 获取参数名
     *
     * @return 参数名
     */
    public String getItemID() {
        return itemID;
    }

    /**
     * 设置参数名
     *
     * @param newItemID 参数名
     */
    public void setItemID(String newItemID) { //设置参数标识
        itemID = newItemID.toLowerCase();
    }

    /**
     * 获取参数值
     *
     * @return 参数值
     */
    public String getItemParam() {
        if (needDoCode) {
            return DataCommand.enCode(itemParam);
        } else {
            return byteToStr(itemParam);
        }
    }

    /**
     * 设置参数值
     *
     * @param itemParam 参数值
     */
    public void setItemParam(String itemParam) {
        this.itemParam = strToByte(itemParam.trim());
    }

    /**
     * 判断是否需要加码
     *
     * @return boolean
     */
    public boolean isNeedDoCode() {
        return needDoCode;
    }

    /**
     * 设置是否需要加码
     *
     * @param needDoCode
     */
    public void setNeedDoCode(boolean needDoCode) {
        this.needDoCode = needDoCode;
    }

    /**
     * 返回整型值
     *
     * @return int
     */
    public int getIntItemValue() {
        int rc;
        try {
            rc = Integer.valueOf(getItemParam()).intValue();
        } catch (NumberFormatException e) {
            rc = 0;
        }
        return rc;
    }

    /**
     * 返回整型值
     *
     * @return short
     */
    public short getShortItemValue() {
        short rc;
        try {
            rc = Short.valueOf(getItemParam()).shortValue();
        } catch (NumberFormatException e) {
            rc = 0;
        }
        return rc;
    }

    /**
     * 返回整型值
     *
     * @return long
     */
    public long getLongItemValue() {
        long rc;
        try {
            rc = Long.valueOf(getItemParam()).longValue();
        } catch (NumberFormatException e) {
            rc = 0;
        }
        return rc;
    }

    /**
     * 返回浮点
     *
     * @return float
     */
    public float getFloatItemValue() {
        float rc;
        try {
            rc = Float.valueOf(getItemParam()).floatValue();
        } catch (NumberFormatException e) {
            rc = 0;
        }
        return rc;
    }

    /**
     * 返回浮点值
     *
     * @return double
     */
    public double getDoubleItemValue() {
        double rc;
        try {
            rc = Double.valueOf(getItemParam()).doubleValue();
        } catch (NumberFormatException e) {
            rc = 0;
        }
        return rc;
    }

    /**
     * 返回byte值
     *
     * @return byte
     */
    public byte getByteItemValue() {
        byte rc;
        try {
            rc = Short.valueOf(getItemParam()).byteValue();
        } catch (NumberFormatException e) {
            rc = 0;
        }
        return rc;
    }

    /**
     * 返回布尔值
     *
     * @return boolean
     */
    public boolean getBooleanItemValue() {
        boolean rc;
        try {
            rc = (Byte.valueOf(getItemParam()).byteValue() != 0);
        } catch (NumberFormatException e) {
            rc = false;
        }
        return rc;
    }

    /**
     * 返回字符串值
     *
     * @param len
     * @return String
     */
    public String getItemValue(int len) {
        if (itemParam == null) {
            return "";
        }
        byte[] value = itemParam;
        if (value.length > len) {
            if (strEnc.length() > 0) {
                try {
                    return new String(value, 0, len, strEnc);
                } catch (java.io.UnsupportedEncodingException ex) {
                    return new String(value, 0, len);
                }
            } else {
                return new String(value, 0, len);
            }
        } else {
            if (strEnc.length() > 0) {
                try {
                    return new String(value, strEnc);
                } catch (java.io.UnsupportedEncodingException ex) {
                    return new String(value);
                }
            } else {
                return new String(value);
            }
        }
    }

    /**
     * 返回字符串值
     *
     * @return String
     */
    public String getItemValue() {
        if (itemParam == null) {
            return "";
        }
        String rc;
        if (strEnc.length() > 0) {
            try {
                rc = new String(itemParam, strEnc);
            } catch (java.io.UnsupportedEncodingException ex) {
                rc = new String(itemParam);
            }
        } else {
            rc = new String(itemParam);
        }
        return rc;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        byte[] idbuf = getItemID().getBytes();
        byte[] encbuf = strEnc.getBytes();
        int itemlen = 0;
        if (itemParam != null) {
            itemlen = itemParam.length;
        }
        int totlelen = idbuf.length + encbuf.length + itemlen + 3;
        out.writeInt(totlelen);
        out.write(idbuf.length);
        out.write(idbuf);
        out.write(encbuf.length);
        if (encbuf.length > 0) {
            out.write(encbuf);
        }
        if (needDoCode) {
            out.write(1);
        } else {
            out.write(0);
        }
        if (itemlen > 0) {
            out.write(itemParam);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        byte[] idbuf, encbuf;
        int totlelen = in.readInt();
        int idlen = in.read();
        if (idlen > 0) {
            idbuf = new byte[idlen];
            in.read(idbuf);
            itemID = new String(idbuf);
        }
        int enclen = in.read();
        if (enclen > 0) {
            encbuf = new byte[enclen];
            in.read(encbuf);
            strEnc = new String(encbuf);
        }
        int sign = in.read();
        if (sign == 0) {
            needDoCode = false;
        } else {
            needDoCode = true;
        }
        int itemlen = totlelen - idlen - enclen - 3;
        if (itemlen > 0) {
            itemParam = new byte[itemlen];
            in.read(itemParam);
        } else {
            itemParam = null;
        }
    }
}
