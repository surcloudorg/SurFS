/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core;

import com.autumn.util.TextUtils;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>Title: 数据指令行指令类</p>
 *
 * <p>Description: 可以分解和合成数据指令行指令，指令标识和参数总串之间以空格分隔，各参数串之间以“&”分隔。</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public final class DataCommand implements Externalizable {

    private static final long serialVersionUID = 20120701000000L;
    private String commID = "";//指令名
    private final Map<String, DataItem> items = Collections.synchronizedMap(new HashMap<String, DataItem>());  //参数类的集合
    private final static AtomicInteger idnum = new AtomicInteger(0);

    public DataCommand() {
    }

    /**
     * 创建类实例
     *
     * @param strCommand 符合鸿讯命令行协议的命令串
     */
    public DataCommand(String strCommand) {
        setCommand(strCommand);
    }

    /**
     * 根据目标实例复制产生新实例
     *
     * @param comm 复制的目标实例
     */
    public DataCommand(DataCommand comm) {
        commID = comm.getCommID();
        synchronized (comm.items) {
            DataItem newitem;
            for (DataItem item : comm.items.values()) {
                newitem = item.newCopy();
                items.put(newitem.getItemID(), newitem);
            }
        }
    }

    /**
     * 为数据指令提供一段时间内唯一的循环递增的整形Commandid，参见鸿讯通讯协议
     *
     * @return 16位字串格式表示的整形数串
     */
    public static String getNewCommandid() {
        return Integer.toHexString(idnum.incrementAndGet());
    }

    /**
     * 序列化输出数据
     *
     * @param out 输出流
     * @throws IOException IO失败抛出异常
     */
    @Override
    public void writeExternal(ObjectOutput out)
            throws IOException {
        byte[] idbuf = commID.getBytes();
        out.write(idbuf.length);
        out.write(idbuf);
        synchronized (items) {
            out.writeInt(items.values().size());
            for (DataItem item : items.values()) {
                item.writeExternal(out);
            }
        }
    }

    /**
     * 序列化输入数据
     *
     * @param in 输入流
     * @throws IOException IO失败抛出异常
     * @throws ClassNotFoundException 类无法识别异常
     */
    @Override
    public void readExternal(ObjectInput in)
            throws IOException, ClassNotFoundException {
        byte[] idbuf;
        int idlen = in.read();
        DataItem item;
        if (idlen > 0) {
            idbuf = new byte[idlen];
            in.read(idbuf);
            commID = new String(idbuf);
        }
        int itemsize = in.readInt();
        for (int ii = 0; ii < itemsize; ii++) {
            item = new DataItem();
            item.readExternal(in);
            items.put(item.getItemID(), item);
        }
    }

    /**
     * 生成100年内唯一的Msgid
     *
     * @return 生成的Msgid
     */
    public static String getMsgid() {
        return TextUtils.Date2String(new Date(), "yyMMddHHmmss") + getNewCommandid();
    }

    /**
     * 判断Commandid是否匹配
     *
     * @param strCommandId 用于比较的Commandid
     * @return 比较结果，true表示相同，false表示不同
     */
    public boolean isCommandId(String strCommandId) {
        return commID.equalsIgnoreCase(strCommandId);
    }

    /**
     * 清空所有数据
     */
    public void Clear() {
        commID = "";
        items.clear();
    }

    /**
     * 创建一个拷贝的实例
     *
     * @return 新拷贝的实例
     */
    public DataCommand newCopy() {
        DataCommand newcomm = new DataCommand();
        newcomm.commID = commID;
        synchronized (items) {
            DataItem newitem;
            for (DataItem item : items.values()) {
                newitem = item.newCopy();
                newcomm.items.put(newitem.getItemID(), newitem);
            }
        }
        return newcomm;
    }

    /**
     * 获取数据指令串(含回车换行)的字节数组
     *
     * @return 数据指令串的字节数组
     */
    public byte[] getCommBytes() {
        return getCommand().getBytes();
    }

    public String getCommand() {
        return getCommand(null);
    }

    /**
     * 获取数据指令串，含回车换行
     *
     * @param exclusionItemName 不包含的参数名称数组，参数名前后可用加通配符*表示扩展匹配
     * @return 数据指令串，含回车换行
     */
    public String getCommand(String[] exclusionItemName) {
        synchronized (items) {
            if (items.values().isEmpty()) {
                return commID + "\r\n";
            }
            StringBuilder strCommand = new StringBuilder();
            String strResult, itemname, tmpname;
            int cmptype;
            loop1:
            for (DataItem item : items.values()) {
                if (exclusionItemName != null) {
                    itemname = item.getItemID();
                    for (int kk = 0; kk < exclusionItemName.length; kk++) {
                        tmpname = exclusionItemName[kk];
                        cmptype = 0;
                        if (tmpname.startsWith("*")) {
                            cmptype++;
                        }
                        if (tmpname.endsWith("*")) {
                            cmptype += 2;
                        }
                        switch (cmptype) {
                            case 0:
                                if (itemname.equals(tmpname)) {
                                    continue loop1;
                                }
                                break;
                            case 1:
                                if (itemname.endsWith(tmpname.substring(1))) {
                                    continue loop1;
                                }
                                break;
                            case 2:
                                if (itemname.startsWith(tmpname.substring(0, tmpname.length() - 1))) {
                                    continue loop1;
                                }
                                break;
                            case 3:
                                if (itemname.indexOf(tmpname.substring(1, tmpname.length() - 1)) != -1) {
                                    continue loop1;
                                }
                                break;
                        }
                    }
                }
                strResult = item.getItemResult();
                if (strCommand.length() == 0) {
                    strCommand.append(strResult);
                } else {
                    strCommand.append("&");
                    strCommand.append(strResult);
                }
            }
            strCommand.append("\r\n");
            return commID + " " + strCommand.toString();
        }
    }

    /**
     * 获取指令名commID后面的指令参数的内容，不含回车换行
     *
     * @return 指令名commID后面的指令参数的内容，不含回车换行
     */
    public String getCommandBody() {
        synchronized (items) {
            StringBuilder strCommand = new StringBuilder();
            String strResult;
            for (DataItem item : items.values()) {
                strResult = item.getItemResult();
                if (strCommand.length() == 0) {
                    strCommand.append(strResult);
                } else {
                    strCommand.append("&");
                    strCommand.append(strResult);
                }
            }
            return strCommand.toString();
        }
    }

    /**
     * 获取为HTTP传输用的数据指令串
     *
     * @param exclusionItemName 不包含的参数名称数组，参数名前后可用加通配符*表示扩展匹配
     * @return 为HTTP传输用的数据指令串
     */
    public String getHttpCommand(String[] exclusionItemName) {
        synchronized (items) {
            if (items.values().isEmpty()) {
                return "command=" + commID + "\r\n";
            }
            StringBuilder strCommand = new StringBuilder();
            String strResult, itemname, tmpname;
            int cmptype;
            loop1:
            for (DataItem item : items.values()) {
                if (exclusionItemName != null) {
                    itemname = item.getItemID();
                    for (int kk = 0; kk < exclusionItemName.length; kk++) {
                        tmpname = exclusionItemName[kk];
                        cmptype = 0;
                        if (tmpname.startsWith("*")) {
                            cmptype++;
                        }
                        if (tmpname.endsWith("*")) {
                            cmptype += 2;
                        }
                        switch (cmptype) {
                            case 0:
                                if (itemname.equals(tmpname)) {
                                    continue loop1;
                                }
                                break;
                            case 1:
                                if (itemname.endsWith(tmpname.substring(1))) {
                                    continue loop1;
                                }
                                break;
                            case 2:
                                if (itemname.startsWith(tmpname.substring(0, tmpname.length() - 1))) {
                                    continue loop1;
                                }
                                break;
                            case 3:
                                if (itemname.indexOf(tmpname.substring(1, tmpname.length() - 1)) != -1) {
                                    continue loop1;
                                }
                                break;
                        }
                    }
                }
                strResult = item.getItemResult();
                if (strCommand.length() == 0) {
                    strCommand.append(strResult);
                } else {
                    strCommand.append("&");
                    strCommand.append(strResult);
                }
            }
            strResult = strCommand.toString().trim();
            if (strResult.length() == 0) {
                return "command=" + commID + "\r\n";
            } else {
                return "command=" + commID + "&" + strResult + "\r\n";
            }
        }
    }

    /**
     * 获取为HTTP传输用的数据指令串
     *
     * @return 为HTTP传输用的数据指令串
     */
    public String getHttpCommand() {
        return getHttpCommand(null);
    }

    /**
     * 获取日志命令串
     *
     * @param exclusionItemName 不包含的参数名称数组，参数名前后可用加通配符*表示扩展匹配
     * @return 去掉排除参数后的日志命令串
     */
    public String getDispCommand(String[] exclusionItemName) {
        synchronized (items) {
            if (items.values().isEmpty()) {
                return commID;
            }
            StringBuilder strCommand = new StringBuilder();
            String strResult, itemname, tmpname;
            int cmptype;
            loop1:
            for (DataItem item : items.values()) {
                if (exclusionItemName != null) {
                    itemname = item.getItemID();
                    for (int kk = 0; kk < exclusionItemName.length; kk++) {
                        tmpname = exclusionItemName[kk];
                        cmptype = 0;
                        if (tmpname.startsWith("*")) {
                            cmptype++;
                        }
                        if (tmpname.endsWith("*")) {
                            cmptype += 2;
                        }
                        switch (cmptype) {
                            case 0:
                                if (itemname.equals(tmpname)) {
                                    continue loop1;
                                }
                                break;
                            case 1:
                                if (itemname.endsWith(tmpname.substring(1))) {
                                    continue loop1;
                                }
                                break;
                            case 2:
                                if (itemname.startsWith(tmpname.substring(0, tmpname.length() - 1))) {
                                    continue loop1;
                                }
                                break;
                            case 3:
                                if (itemname.indexOf(tmpname.substring(1, tmpname.length() - 1)) != -1) {
                                    continue loop1;
                                }
                                break;
                        }
                    }
                }
                strResult = item.getDispItemResult();
                if (strCommand.length() == 0) {
                    strCommand.append(strResult);
                } else {
                    strCommand.append("&");
                    strCommand.append(strResult);
                }
            }
            return commID + " " + strCommand.toString();
        }
    }

    /**
     * 获取日志命令串
     *
     * @return 用于日志的命令串
     */
    public String getDispCommand() {
        return getDispCommand(null);
    }

    /**
     * 以源数据指令重新刷新当前对象数据，以保证两者内容相同
     *
     * @param src 源数据指令
     */
    public void CopyFromCommand(DataCommand src) {
        synchronized (src.items) {
            items.clear();
            commID = src.commID;
            DataItem newitem;
            for (DataItem item : src.items.values()) {
                newitem = item.newCopy();
                items.put(newitem.getItemID(), newitem);
            }
        }
    }

    /**
     * 根据数据指令串创建对象
     *
     * @param strCommand 需要分解的数据指令串
     * @return 根据数据指令串创建的对象
     */
    public static DataCommand fillCommand(String strCommand) {
        DataCommand rc = null;
        strCommand = strCommand.trim();
        if (strCommand.length() > 0) {
            rc = new DataCommand(strCommand);
        }
        return rc;
    }

    /**
     * 设置数据指令串
     *
     * @param strCommand 需要分解的数据指令串
     */
    public void setCommand(String strCommand) {
        items.clear();
        strCommand = strCommand.trim();
        int len = strCommand.length();
        if (len >= 2 && strCommand.charAt(len - 2) == '\r' && strCommand.charAt(len - 1) == '\n') {
            strCommand = strCommand.substring(0, len - 2);
        }
        int pos = strCommand.indexOf(' ');
        if (pos < 0) {
            commID = strCommand.toLowerCase();
        } else if (pos == 0) {
            commID = "";
        } else {
            commID = strCommand.substring(0, pos).toLowerCase();
        }
        int nextpos;
        while (pos >= 0) {
            nextpos = strCommand.indexOf('&', pos + 1);
            if (nextpos >= 0) {
                if (nextpos > pos + 1) {
                    addItem(new DataItem(strCommand.substring(pos + 1, nextpos)));
                }
            } else {
                if (strCommand.length() > pos + 1) {
                    addItem(new DataItem(strCommand.substring(pos + 1)));
                }
            }
            pos = nextpos;
        }
    }

    /**
     * 加码函数，以十六进制无符号整数串形式返回一个字节数组的字符串表示形式。
     *
     * @param bsrc 需要加码的字节数组
     * @return 加码产生的结果字符串
     */
    public static String enCode(byte[] bsrc) {
        if (bsrc == null) {
            return "";
        }
        String str;
        StringBuilder dest = new StringBuilder(bsrc.length * 2);
        byte bb;
        int num;
        for (int ii = 0; ii < bsrc.length; ii++) {
            bb = bsrc[ii];
            if (bb >= 0) {
                num = bb;
            } else {
                num = (bb & 0x7F) + (1 << 7);
            }
            str = Integer.toHexString(num);
            if (str.length() < 2) {
                str = "0" + str;
            }
            dest.append(str.toUpperCase());
        }
        return dest.toString();
    }

    /**
     * 将加码串还原为一个GBK编码的字符串
     *
     * @param src 加码串
     * @return 还原的结果串
     * @throws UnsupportedEncodingException
     */
    public static String deCodeToStr(String src) throws UnsupportedEncodingException {
        byte[] buf = deCode(src);
        return new String(buf, "UTF-8");
    }

    /**
     * 将加码串还原为字节数组
     *
     * @param src 加码串
     * @return 还原的字节数组
     */
    public static byte[] deCode(String src) {
        if (src.length() < 2) {
            return new byte[0];
        }
        byte[] dest = new byte[src.length() / 2];
        byte rb;
        String str;
        Arrays.fill(dest, (byte) 0);
        int index = 0;
        for (int ii = 0; ii < src.length() - 1; ii++) {
            str = "#" + src.substring(ii, ii + 2);
            try {
                rb = (byte) Integer.decode(str).intValue();
            } catch (NumberFormatException e) {
                return new byte[0];
            }
            dest[index++] = rb;
            ii++;
        }
        return dest;
    }

    /**
     * 增加参数成员对象
     *
     * @param item 参数成员对象
     */
    public void addItem(DataItem item) {
        if (item == null || item.getItemID().compareTo("") == 0) {
            return;
        }
        items.put(item.getItemID(), item);
    }

    /**
     * 根据参数名删除参数成员对象
     *
     * @param itemID 要删除的参数名
     */
    public void removeItem(String itemID) {
        items.remove(itemID.toLowerCase());
    }

    /**
     * 判断参数值是否加码
     *
     * @param itemID 参数名
     * @return boolean
     */
    public boolean isItemDoCode(String itemID) {
        DataItem item = findItem(itemID);
        if (item != null) {
            return item.isNeedDoCode();
        }
        return false;
    }

    /**
     * 根据参数名查找参数对象
     *
     * @param itemID 参数名
     * @return 找到的参数对象，如果没找到返回null
     */
    public DataItem findItem(String itemID) {
        return items.get(itemID.toLowerCase());
    }

    /**
     * 获取参数个数
     *
     * @return 参数个数
     */
    public int ItemSize() {
        return items.values().size();
    }

    /**
     * 根据参数名获取参数字符串值
     *
     * @param itemID 参数名
     * @return 字符串参数值
     */
    public String getItemValue(String itemID) {
        DataItem item;
        item = findItem(itemID);
        if (item == null) {
            return "";
        }
        return item.getItemValue();
    }

    /**
     * 根据参数名获取参数字符串
     *
     * @param itemID 参数名
     * @return 参数字符串
     */
    public String getItemParam(String itemID) {
        DataItem item;
        item = findItem(itemID);
        if (item == null) {
            return "";
        }
        return item.getItemParam();
    }

    /**
     * 根据参数名获取参数字符串值
     *
     * @param itemID 参数名
     * @param enc 编码格式串，例如“UTF-8”等
     * @return 参数字符串值
     */
    public String getItemValue(String itemID, String enc) {
        DataItem item;
        String rc;
        item = findItem(itemID);
        if (item == null) {
            return "";
        }
        try {
            rc = new String(item.getValueBytes(), enc);
        } catch (UnsupportedEncodingException e) {
            rc = "";
        }
        return rc;
    }

    /**
     * 根据参数名获取参数字节数组值
     *
     * @param itemID 参数名
     * @return 参数字节数组值
     */
    public byte[] getItemValueBytes(String itemID) {
        DataItem item;
        item = findItem(itemID);
        if (item == null) {
            return null;
        }
        return item.getValueBytes();
    }

    /**
     * 根据参数名获取参数整形值
     *
     * @param itemID 参数名
     * @return 参数整形值
     */
    public int getIntItemValue(String itemID) {
        DataItem item;
        item = findItem(itemID);
        if (item == null) {
            return 0;
        }
        return item.getIntItemValue();
    }

    /**
     * 根据参数名获取参数短整形值
     *
     * @param itemID 参数名
     * @return 参数短整形值
     */
    public short getShortItemValue(String itemID) {
        DataItem item;
        item = findItem(itemID);
        if (item == null) {
            return 0;
        }
        return item.getShortItemValue();
    }

    /**
     * 根据参数名获取参数长整形值
     *
     * @param itemID 参数名
     * @return 参数长整形值
     */
    public long getLongItemValue(String itemID) {
        DataItem item;
        item = findItem(itemID);
        if (item == null) {
            return 0;
        }
        return item.getLongItemValue();
    }

    /**
     * 根据参数名获取参数浮点值
     *
     * @param itemID 参数名
     * @return 参数浮点值
     */
    public float getFloatItemValue(String itemID) {
        DataItem item;
        item = findItem(itemID);
        if (item == null) {
            return 0;
        }
        return item.getFloatItemValue();
    }

    /**
     * 根据参数名获取参数双精度浮点值
     *
     * @param itemID 参数名
     * @return 参数双精度浮点值
     */
    public double getDoubleItemValue(String itemID) {
        DataItem item;
        item = findItem(itemID);
        if (item == null) {
            return 0;
        }
        return item.getDoubleItemValue();
    }

    /**
     * 根据参数名获取参数字节值
     *
     * @param itemID 参数名
     * @return 参数字节值
     */
    public byte getByteItemValue(String itemID) {
        DataItem item;
        item = findItem(itemID);
        if (item == null) {
            return 0;
        }
        return item.getByteItemValue();
    }

    /**
     * 根据参数名获取参数逻辑值
     *
     * @param itemID 参数名
     * @return 参数逻辑值
     */
    public boolean getBooleanItemValue(String itemID) {
        DataItem item;
        item = findItem(itemID);
        if (item == null) {
            return false;
        }
        return item.getBooleanItemValue();
    }

    /**
     * 根据参数名获取回定长串值
     *
     * @param itemID 参数名
     * @param len 最大返回长度
     * @return 返回定长值串
     */
    public String getItemValue(String itemID, int len) {
        DataItem item;
        item = findItem(itemID);
        if (item == null) {
            return "";
        } else {
            return item.getItemValue(len);
        }
    }

    /**
     * 获取数据指令标识
     *
     * @return 指令标识
     */
    public String getCommID() {
        return commID;
    }

    /**
     * 设置数据指令标识
     *
     * @param commID 指令标识
     */
    public void setCommID(String commID) {
        this.commID = commID.toLowerCase();
    }

    /**
     * 拷贝源数据指令对象的指定参数到当前对象
     *
     * @param src 源数据指令对象
     * @param itemID 要拷贝的参数名
     * @return true：拷贝成功；false：源数据指令对象不存在该参数
     */
    public boolean copyValue(DataCommand src, String itemID) {
        DataItem citem = src.findItem(itemID);
        if (citem == null) {
            return false;
        }
        addItem(citem.newCopy());
        return true;
    }

    /**
     * 拷贝源数据指令对象的指定参数到当前对象，并重新定义参数名
     *
     * @param src 源数据指令对象
     * @param srcitemID 要拷贝的参数名
     * @param newItemid 新定义的参数名
     * @return true：拷贝成功；false：源数据指令对象不存在该参数
     */
    public boolean copyValue(DataCommand src, String srcitemID, String newItemid) {
        DataItem citem = src.findItem(srcitemID);
        if (citem == null) {
            return false;
        }
        citem = citem.newCopy();
        citem.setItemID(newItemid);
        addItem(citem);
        return true;
    }

    /**
     * 增加新参数
     *
     * @param itemID 参数名
     * @param itemParam 参数值
     */
    public void AddNewItem(String itemID, String itemParam) {
        addItem(new DataItem(itemID, itemParam));
    }

    /**
     * 增加新参数
     *
     * @param itemID 参数名
     * @param itemParam 参数值，如果needDoCode，则是加过码的参数值
     * @param needDoCode itemParam是否已经加过码
     */
    public void AddNewItem(String itemID, String itemParam, boolean needDoCode) {
        addItem(new DataItem(itemID, itemParam, needDoCode));
    }

    /**
     * 增加新参数
     *
     * @param itemID 参数名
     * @param itemParam 参数字节数组
     * @param needDoCode 指定参数值是否需要加码
     */
    public void AddNewItem(String itemID, byte[] itemParam, boolean needDoCode) {
        addItem(new DataItem(itemID, itemParam, needDoCode));
    }

    /**
     * 增加新参数
     *
     * @param itemID 参数名
     * @param itemParam 参数字节数组
     * @param needDoCode 指定参数值是否需要加码
     * @param strEnc 参数值的内码格式，例如“UTF-8”等
     */
    public void AddNewItem(String itemID, byte[] itemParam, boolean needDoCode, String strEnc) {
        addItem(new DataItem(itemID, itemParam, needDoCode, strEnc));
    }

    /**
     * 根据参数串增加新参数
     *
     * @param strItem 要解析的参数串
     */
    public void AddNewItem(String strItem) {
        addItem(new DataItem(strItem));
    }
}
