package com.autumn.core.log;

import java.io.*;

/**
 * <p>Title: 日志分析器-执行查询</p>
 *
 * <p>Description: 读文件,计算总行数，搜索关键字,显示行内容</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class FinderCall {

    private FileFinder finder = null;

    /**
     * 构造函数
     *
     * @param find FileFinder
     */
    protected FinderCall(FileFinder find) {
        finder = find;
    }

    /**
     * 获取总行数，忽视关键字，最快
     *
     * @param file
     * @return long
     * @throws IOException
     */
    private long getLineCount(File file) {
        long nn = 0;
        BufferedInputStream is = null;
        finder.lineMark.clear();
        long count = 0;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            byte[] c = new byte[8192];
            int readChars;
            while ((readChars = is.read(c)) != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        nn++;
                        if (nn % 1000 == 0 && nn > 1) {
                            finder.lineMark.put(nn, new Long[]{count + i + 1});
                        }
                    }
                }
                count = count + readChars;
            }
        } catch (IOException r) {
            LogFactory.error("读取文件出现错误:" + r.getMessage(), FinderCall.class);
        } finally {
            try {
                is.close();
            } catch (Exception r) {
            }
        }
        return nn;
    }

    /**
     * 获取行数,包含搜索词的行数
     *
     * @param file
     * @return long
     */
    private long getLineCountWithkey(File file) {
        finder.lineMark.clear();
        long count = 0;//总字节数
        byte[] upbs = finder.getFindKey().toUpperCase().getBytes();
        byte[] lowbs = finder.getFindKey().toLowerCase().getBytes();
        int len = upbs.length;
        long nn = 0, nnn = 0;//带关键字行,总行数
        int index = 0;
        BufferedInputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            byte[] c = new byte[8192];
            int readChars;
            while ((readChars = is.read(c)) != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        nnn++;
                        if (index == -1) {
                            nn++;
                            if (nn % 1000 == 0) {
                                finder.lineMark.put(nn, new Long[]{count + i + 1, nnn});
                            }
                        }
                        index = 0;
                    } else {
                        if (index != -1) {
                            if (c[i] == upbs[index] || c[i] == lowbs[index]) {
                                index++;
                                if (index >= len) {
                                    index = -1;
                                }
                            } else {
                                index = 0;
                            }
                        }
                    }
                }
                count = count + readChars;
            }
        } catch (IOException r) {
            LogFactory.error("读取文件出现错误:" + r.getMessage(), FinderCall.class);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception r) {
            }
        }
        return nn;
    }

    /**
     * 移动到指定行
     *
     * @param lineNumber
     */
    public void move(long lineNumber) {
        finder.result.clear();
        File file = new File(finder.getFileName());
        LineNumberReader li = null;
        long count = finder.getLineCount();
        if (count == 0) {
            finder.LineNumber = 0;
            if (finder.getFindKey() != null) {
                finder.result.add("找不到关键字：".concat(finder.getFindKey()));
            }
            return;
        }
        int size = finder.getStepSize();
        long line = lineNumber;
        if (line <= 0) {
            line = size;
        }
        if (line >= count) {
            line = count;
        }
        long nn = line - size;
        if (nn > 0) {
            nn = nn - (nn % 1000);
        }
        try {
            FileInputStream fis = new FileInputStream(file);
            if (finder.lineMark.get(nn) != null && finder.lineMark.get(nn)[0] > 0) {
                fis.skip(finder.lineMark.get(nn)[0]);
            }
            InputStreamReader isr = new InputStreamReader(fis);
            li = new LineNumberReader(isr);
            if (finder.lineMark.get(nn) != null) {
                if (finder.getFindKey() == null) {
                    li.setLineNumber((int) nn);
                } else {
                    li.setLineNumber(finder.lineMark.get(nn)[1].intValue());
                }
            }
            String str;
            while ((str = li.readLine()) != null) {
                if (finder.getFindKey() != null && (!str.toLowerCase().contains(finder.getFindKey()))) {
                    continue;
                }
                if (nn >= (line - size)) {
                    finder.result.add(new StringBuilder(li.getLineNumber()).append(" ").append(str).append("\r\n").toString());
                }
                nn++;
                if (nn >= line) {
                    break;
                }
            }
            finder.LineNumber = line;
        } catch (IOException e) {
            finder.result.add("读取文件出现错误：" + e.getMessage());
        } finally {
            try {
                if (li != null) {
                    li.close();
                }
            } catch (Exception er) {
            }
        }
    }

    /**
     * 获取行数
     */
    public void getLineCount() {
        File file = new File(finder.getFileName());
        if (finder.getFindKey() == null) {
            finder.lineCount = getLineCount(file);
        } else {
            finder.lineCount = getLineCountWithkey(file);
        }
    }
}
