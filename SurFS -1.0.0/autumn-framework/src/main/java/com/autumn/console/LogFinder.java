package com.autumn.console;

import com.autumn.core.log.FileFinder;
import com.autumn.core.log.LogFactory;
import com.autumn.core.web.Action;
import com.autumn.core.web.ActionForward;
import com.autumn.core.web.Forward;
import com.autumn.util.TextUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>Title: 框架控制台</p>
 *
 * <p>Description: 日志查看页</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class LogFinder extends Action {

    private String logname = ""; //目录
    private String datestr = ""; // 日志日期
    private boolean showWarnline = false;
    private long lineCount = 0; //总行数
    private int stepSize = 500; //显示行数，<100,>5
    private int direct = 1; //1向下，0向上
    private long lineNumber = 10000000; //搜索的起始行位置
    private String findKey = ""; //搜索内容
    private List<String> findResult = new ArrayList<String>();
    private String filename = "";
    private String submit = null; //是否提交

    @Override
    public Forward execute() {
        if (getSubmit() == null) { //初始化
            if (LogFactory.findLogger(getLogname()) != null) {
                setDatestr(TextUtils.Date2String(new Date(), "yyyy-MM-dd"));
            } else {
                getFindResult().add("没找到日志目录" + getLogname());
            }
            this.setAttribute("smlogfind", this);
            return new ActionForward("smlogfind.jsp");
        } else { //执行查询
            FileFinder logf = null;
            if (LogFactory.findLogger(getLogname()) == null) {
                getFindResult().add("没找到日志目录 " + getLogname());
            } else {
                if (getDatestr().trim().equals("")) {
                    setDatestr(TextUtils.Date2String(new Date(), "yyyy-MM-dd"));
                }
                String date = getDatestr().replaceAll("-", "").replaceAll(" ", "");
                try {
                    logf = LogFactory.getLogFinder(getLogname(), date); //默认当天日志
                } catch (Exception r) {
                    getFindResult().add(r.getMessage());
                }
            }
            if (logf != null) {
                logf.setStepSize(getStepSize());
                if (getFindKey().trim().length() >= 2 || isShowWarnline()) { //执行搜索
                    if (isShowWarnline()) {
                        logf.setFindKey("[FATAL]");
                    } else {
                        logf.setFindKey(getFindKey());
                    }
                }
                logf.setLineNumber(getLineNumber());
                if (getDirect() == 1) {
                    logf.nextLine();
                } else {
                    logf.previousLine();
                }
                setLineCount(logf.getLineCount());
                setLineNumber(logf.getLineNumber());
                setFilename("日志路径：" + logf.getFileName());
                this.setAttribute("fileinfo", logf.getFileinfo());
                findResult = logf.getResult();
            }
            this.setAttribute("smlogfind", this);
            return new ActionForward("smlogfind.jsp");
        }
    }

    /**
     * @return the logname
     */
    public String getLogname() {
        return logname;
    }

    /**
     * @param logname the logname to set
     */
    public void setLogname(String logname) {
        this.logname = logname;
    }

    /**
     * @return the datestr
     */
    public String getDatestr() {
        return datestr;
    }

    /**
     * @param datestr the datestr to set
     */
    public void setDatestr(String datestr) {
        this.datestr = datestr;
    }

    /**
     * @return the showWarnline
     */
    public boolean isShowWarnline() {
        return showWarnline;
    }

    /**
     * @param showWarnline the showWarnline to set
     */
    public void setShowWarnline(boolean showWarnline) {
        this.showWarnline = showWarnline;
    }

    /**
     * @return the lineCount
     */
    public long getLineCount() {
        return lineCount;
    }

    /**
     * @param lineCount the lineCount to set
     */
    public void setLineCount(long lineCount) {
        this.lineCount = lineCount;
    }

    /**
     * @return the stepSize
     */
    public int getStepSize() {
        return stepSize;
    }

    /**
     * @param stepSize the stepSize to set
     */
    public void setStepSize(int stepSize) {
        this.stepSize = stepSize;
    }

    /**
     * @return the direct
     */
    public int getDirect() {
        return direct;
    }

    /**
     * @param direct the direct to set
     */
    public void setDirect(int direct) {
        this.direct = direct;
    }

    /**
     * @return the lineNumber
     */
    public long getLineNumber() {
        return lineNumber;
    }

    /**
     * @param lineNumber the lineNumber to set
     */
    public void setLineNumber(long lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * @return the findKey
     */
    public String getFindKey() {
        return findKey;
    }

    /**
     * @param findKey the findKey to set
     */
    public void setFindKey(String findKey) {
        this.findKey = findKey;
    }

    /**
     * @return the findResult
     */
    public List<String> getFindResult() {
        return findResult;
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @return the submit
     */
    public String getSubmit() {
        return submit;
    }

    /**
     * @param submit the submit to set
     */
    public void setSubmit(String submit) {
        this.submit = submit;
    }
}
