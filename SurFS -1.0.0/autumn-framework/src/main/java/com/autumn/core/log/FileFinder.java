package com.autumn.core.log;

import com.autumn.util.TextUtils;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Title: 日志分析器</p>
 *
 * <p>Description: 对日志文件分页显示，搜索关键字</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public final class FileFinder implements Serializable {

    private static final long serialVersionUID = 2010012010;
    public static final int FindMaxSize = 1024 * 1024 * 1024; //大于1024M不查询
    protected long lineCount = -1;
    private int stepSize = 500; //<100,>5 每页显示行数
    protected long LineNumber = 0; //当前行
    private String findKey = null; //<>null
    private String fileName; //文件名
    protected List<String> result = new ArrayList<String>();
    protected HashMap<Long, Long[]> lineMark = new HashMap<Long, Long[]>();//行指针
    private String fileinfo = "";//该日志文件简单信息

    /**
     * 日志文件名，即大小信息
     *
     * @param filename String
     * @throws Exception
     */
    private void setfilename(String filename) throws Exception {
        File file = new File(filename);
        if (!file.exists()) {
            throw new Exception("文件未找到");
        }
        long size = file.length();
        if (size > FindMaxSize) {
            throw new Exception("文件(".concat(file.getAbsolutePath()).concat(")尺寸(").concat(String.valueOf(size)).concat(")大于1024M"));
        }
        fileinfo = ",修改时间:".concat(TextUtils.Date2String(new Date(file.lastModified()))).concat(",大小：");
        if (size <= 1024) {
            fileinfo = fileinfo.concat(String.valueOf(size)).concat("字节");
        } else if (size > 1024 && size <= 1024 * 1024) {
            fileinfo = fileinfo.concat(Long.toString(size / 1024)).concat("KB").concat(" (").concat(String.valueOf(size)).concat("字节)");
        } else {
            fileinfo = fileinfo.concat(Long.toString(size / 1024 / 1024)).concat("MB").concat(" (").concat(String.valueOf(size)).concat("字节)");
        }
        this.fileName = file.getAbsolutePath();
    }

    /**
     * 构造函数
     *
     * @param name String 文件名
     * @throws Exception
     */
    public FileFinder(String name) throws Exception {
        this.setfilename(name);
    }

    /**
     * 构造函数
     *
     * @param name String 文件名
     * @param size int 每页显示行数
     * @throws Exception
     */
    public FileFinder(String name, int size) throws Exception {
        this.setfilename(name);
        this.setStepSize(size);
    }

    /**
     * 构造函数
     *
     * @param name String 文件名
     * @param size int 每页显示行数
     * @param key String 查找的关键字
     * @throws Exception
     */
    public FileFinder(String name, int size, String key) throws Exception {
        this.setfilename(name);
        this.setStepSize(size);
        this.setFindKey(key);
    }

    /**
     * 指针移至第一行
     */
    public void firstLine() {
        moveToLine(this.getStepSize());
    }

    /**
     * 指针移至最后一行
     */
    public void lastLine() {
        moveToLine(this.getLineCount());
    }

    /**
     * 指针移至下一页，起始行为上次移动的行指针
     */
    public void nextLine() {
        moveToLine(this.getLineNumber() + this.getStepSize());
    }

    /**
     * 指针移至上一页，起始行为上次移动的行指针
     */
    public void previousLine() {
        moveToLine(this.getLineNumber() - this.getStepSize());
    }

    /**
     * 指针移至指定行
     *
     * @param lineNumber int
     */
    public void moveToLine(long lineNumber) {
        FinderCall mycall = null;
        mycall = new FinderCall(this);
        mycall.move(lineNumber);
    }

    /**
     * 获取文件行数
     *
     * @return int
     */
    public long getLineCount() {
        if (lineCount >= 0) {
            return lineCount;
        }
        FinderCall mycall = null;
        mycall = new FinderCall(this);
        mycall.getLineCount();
        return lineCount;
    }

    /**
     * 获取当前行
     *
     * @return int
     */
    public long getLineNumber() {
        return LineNumber;
    }

    /**
     * 获取文件名
     *
     * @return String
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 获取搜索关键字
     *
     * @return String
     */
    public String getFindKey() {
        return findKey;
    }

    /**
     * 搜索结果
     *
     * @return List
     */
    public List<String> getResult() {
        return result;
    }

    /**
     * 每页显示行数
     *
     * @return int
     */
    public int getStepSize() {
        return stepSize;
    }

    /**
     * 设置每页显示行数
     *
     * @param pageSize int
     */
    public void setStepSize(int pageSize) {
        pageSize = pageSize > 500 ? 500 : pageSize;
        pageSize = pageSize < 100 ? 100 : pageSize;
        if (this.stepSize == pageSize) {
            return;
        }
        this.lineCount = -1;
        this.LineNumber = 0;
        this.result.clear();
        this.stepSize = pageSize;
        this.lineMark.clear();
    }

    /**
     * 设置搜索关键字
     *
     * @param findKey String
     */
    public void setFindKey(String findKey) {
        findKey = "".equals(findKey) ? null : findKey;
        if (this.findKey == null && findKey == null) {
            return;
        }
        if (this.findKey != null && findKey != null) {
            if (findKey.equalsIgnoreCase(this.findKey)) {
                return;
            }
        }
        this.lineCount = -1;
        this.LineNumber = 0;
        this.result.clear();
        this.findKey = findKey == null ? null : findKey.toLowerCase();
        this.lineMark.clear();
    }

    /**
     * 设置行指针，下次搜索的起始行
     *
     * @param lineNumber int
     */
    public void setLineNumber(long lineNumber) {
        this.LineNumber = lineNumber;
    }

    /**
     * @return the fileinfo
     */
    public String getFileinfo() {
        return fileinfo;
    }
}
