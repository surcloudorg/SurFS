/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.console;

import com.autumn.core.ClassFileFilter;
import com.autumn.core.web.*;
import com.autumn.util.Compiler;
import com.autumn.util.FileOperation;
import com.autumn.util.IOUtils;
import com.autumn.util.TextUtils;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;

/**
 * <p>Title: 框架控制台</p>
 *
 * <p>Description: 文件操作</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class FileManager extends Action {

    private static final long Max_Editble_Size = 1024 * 1024;//仅允许编辑小于1M的文本
    private String pathStr = "";
    private List<FileProp> fileList = new ArrayList<FileProp>();
    private List<FileProp> linkList = new ArrayList<FileProp>();
    private String dotype = "";
    private String doMsg = "";
    private FileItem file = null;
    private String dirname = "";
    private String[] selfilename = null;
    private String content = "";
    private boolean showmakebtn = false;
    private String forward = null;

    /**
     * 解压缩
     *
     * @param is InputStream
     * @param outputDirectory String
     * @throws IOException
     * @throws FileNotFoundException
     */
    private void unzip(InputStream is, String outputDirectory) throws
            IOException, FileNotFoundException {
        ZipInputStream in = new ZipInputStream(new BufferedInputStream(is));
        ZipEntry z = in.getNextEntry();
        while (z != null) {
            if ((!z.isDirectory()) && (!z.getName().endsWith("/"))) {
                File f = new File(outputDirectory + File.separator + z.getName());
                File path = new File(f.getParent());
                if (!path.exists()) {
                    path.mkdirs();
                }
                f.createNewFile();
                OutputStream out = new BufferedOutputStream(new FileOutputStream(f));
                int b;
                while ((b = in.read()) != -1) {
                    out.write(b);
                }
                out.close();
                f.setLastModified(z.getTime());
                info("解压缩" + f.getAbsolutePath() + "完成");
            } else {
                File f = new File(outputDirectory + File.separator + z.getName());
                if (!f.exists()) {
                    f.mkdirs();
                }
            }
            z = in.getNextEntry();
        }
        in.close();
    }

    /**
     * 删除目录/文件
     *
     * @param path
     */
    private void delFile(String path) {
        File f = new File(path);
        if (!f.exists()) {
            return;
        }
        if (f.isDirectory()) {
            File[] tempList = f.listFiles();
            for (File temp : tempList) {
                delFile(temp.getAbsolutePath());
            }
        }
        f.delete();
    }

    /**
     * 编译java
     *
     * @return
     */
    private boolean compileJava() {
        try {
            String srcrootpath = Initializer.getWebpath() + "WEB-INF" + File.separator + "src";
            String srcpath = pathStr;
            srcpath = srcpath.substring(srcrootpath.length());
            int index = srcpath.indexOf(File.separator, 1);
            if (index > 0) {
                srcpath = srcrootpath + srcpath.substring(0, index);
            }
            String classpath = Initializer.getWebpath() + "WEB-INF" + File.separator + "classes";
            Compiler cc = new Compiler(srcpath, classpath, "UTF-8");
            for (String fname : selfilename) {
                File f = new File(pathStr + fname);
                cc.addSrcFile(f.getAbsolutePath());
            }
            cc.addClassPath(classpath);
            String ss = Compiler.getJarPath(HttpServletRequest.class);
            cc.addClassPath(ss);
            cc.addLibPath(Initializer.getWebpath() + "WEB-INF" + File.separator + "lib");
            boolean b = cc.make();
            doMsg = cc.getCompilerMessage();
            warn(doMsg);
            if (doMsg.length() > 100 && b) {
                doMsg = doMsg.substring(0, 100) + "...";
            }
            return b;
        } catch (Exception e) {
            doMsg = "编译失败：" + e.getMessage();
            trace("编译失败!", e);
            return false;
        }
    }

    @Override
    public Forward execute() {
        String root = Initializer.getWebpath();
        if (getDotype().equals("返回")) {
            if (!(getForward() == null || getForward().isEmpty())) {
                return new RedirectForward(getForward());
            }
        }
        //初始化当前路径
        if (getPathStr().trim().equals("")) {
            setPathStr(root);
        }
        if (!pathStr.trim().endsWith(File.separator)) {
            setPathStr(getPathStr().trim() + File.separator);
        }
        if (getDotype().equals("上传") && this.getAccessPermission() > 1) {
            if ((getFile() != null)) {
                String filestr = getFile().getName();
                if (filestr.toLowerCase().endsWith("zip")) {
                    try {
                        InputStream is = getFile().getInputStream();
                        unzip(is, getPathStr());
                        warn("上传压缩包" + filestr + "完成！");
                        setDoMsg("上传压缩包" + filestr + "完成");
                    } catch (Exception e) {
                        error("解压缩" + filestr + "失败！");
                        setDoMsg("解压缩" + filestr + "失败");
                    }
                } else {
                    filestr = getPathStr() + filestr;
                    try {
                        file.write(new File(filestr));
                        info("上传" + filestr + "完成！");
                        setDoMsg("上传" + filestr + "成功！");
                    } catch (Exception er) {
                        error("上传" + filestr + "失败！");
                        setDoMsg("上传" + filestr + "失败！");
                    }
                }
            }
        }
        if (getDotype().equals("删除选中") && getSelfilename() != null && this.getAccessPermission() > 1) {
            if (getSelfilename().length == 0) {
                setDoMsg("没有选定删除文件！");
            } else {
                for (String fname : getSelfilename()) {
                    delFile(getPathStr() + fname);
                    warn("删除" + getPathStr() + fname + "！");
                }
                if (getDoMsg().equals("")) {
                    setDoMsg("删除成功！");
                }
            }
        }
        if (dotype.equals("编译选中文件") && selfilename != null && this.getAccessPermission() > 1) {
            if (pathStr.startsWith(Initializer.getWebpath() + "WEB-INF" + File.separator + "src")) {
                if (compileJava()) {
                    if (doMsg.equals("")) {
                        doMsg = "编译完毕！";
                    }
                } else {
                    setAttribute("smupload", this);
                    return new ActionForward("compileresult.jsp");
                }
            }
        }
        if (getDotype().equals("view")) {
            File f = new File(getPathStr() + getDirname());
            if (getDirname().toString().endsWith("class")
                    || getDirname().toString().endsWith("jar")
                    || getDirname().toString().endsWith("zip")
                    || getDirname().toString().endsWith("rar")
                    || getDirname().toString().endsWith("bmp")
                    || getDirname().toString().endsWith("jpg")
                    || getDirname().toString().endsWith("gif")
                    || (f.length() > Max_Editble_Size)) {
                setDoMsg(f.getAbsolutePath() + "不允许编辑");
            } else {
                try {
                    String contenttype = FileOperation.getCharset(f);
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    IOUtils.copy(new FileInputStream(f), os);
                    setContent(new String(os.toByteArray(), contenttype));
                } catch (Exception e) {
                    setContent("读取文件" + f.getAbsolutePath() + "失败");
                }
                setAttribute("smupload", this);
                return new ActionForward("fileedit.jsp");
            }
        }
        if (getDotype().equals("download")) {
            File f = new File(getPathStr() + getDirname());
            try {
                String ext = getDirname();
                if (f.isDirectory()) {
                    ext = getDirname() + ".zip";
                }
                if (this.getRequest().getHeader("User-Agent").toLowerCase().contains("firefox")) {
                    ext = new String(ext.getBytes("UTF-8"), "iso8859-1");
                } else {
                    ext = URLEncoder.encode(ext, "UTF-8");
                }
                HttpServletResponse httpServletResponse = this.getResponse();
                if (f.isDirectory()) {
                    httpServletResponse.reset();
                    httpServletResponse.setContentType("application/octet-stream; charset=UTF-8");
                    httpServletResponse.addHeader("Content-Disposition", "attachment; filename=\"" + ext + "\"");
                    //Zipper.zip(httpServletResponse.getOutputStream(), f.getAbsolutePath());
                    return null;
                } else {
                    if (f.length() <= 0) {
                        setDoMsg("文件" + getPathStr() + getDirname() + "为空!");
                    } else {
                        httpServletResponse.reset();
                        httpServletResponse.setContentType("application/octet-stream; charset=UTF-8");
                        httpServletResponse.addHeader("Content-Disposition", "attachment; filename=\"" + ext + "\"");
                        DataHandler dh = new DataHandler(new FileDataSource(f.getAbsolutePath()));
                        dh.writeTo(httpServletResponse.getOutputStream());
                        return null;
                    }
                }
            } catch (Exception e) {
                error("下载文件" + getPathStr() + getDirname() + "错误:" + e.getMessage());
                setDoMsg("下载文件" + getPathStr() + getDirname() + "错误!");
            }
        }
        if (getDotype().equals("保存") && this.getAccessPermission() > 1) {
            File f = new File(getPathStr() + getDirname());
            if (getDirname().toString().endsWith("class")
                    || getDirname().toString().endsWith("jar")
                    || getDirname().toString().endsWith("zip")
                    || getDirname().toString().endsWith("rar")
                    || getDirname().toString().endsWith("bmp")
                    || getDirname().toString().endsWith("jpg")
                    || getDirname().toString().endsWith("gif")) {
                setDoMsg(f.getAbsolutePath() + "不允许编辑");
            } else if (f.length() > Max_Editble_Size) {
                setDoMsg("文件" + f.getAbsolutePath() + "太大，不允许编辑");
            } else {
                try {
                    String contenttype = FileOperation.getCharset(f);
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    IOUtils.copy(new FileInputStream(f), os);
                    String oldcontent = new String(os.toByteArray(), contenttype);
                    if (!oldcontent.trim().equals(content.trim())) {
                        String fname = f.getAbsolutePath();
                        FileOperation.writeFile(content.trim().getBytes(contenttype), fname);
                        setDoMsg("保存" + fname);
                        warn(getDoMsg());
                    }
                } catch (IOException e) {
                    error("保存" + f.getAbsolutePath() + "失败:" + e.getMessage());
                    setDoMsg("保存" + f.getAbsolutePath() + "失败");
                }
            }
            if (!(getForward() == null || getForward().isEmpty())) {
                return new ActionForward(getForward() + "?doMsg=" + getDoMsg());
            }
        }
        if (this.getAccessPermission() > 1 && getDotype().equals("新建") && (!dirname.trim().equals(""))) {
            String filestr = getPathStr() + getDirname();
            try {
                File myFilePath = new File(filestr);
                if (myFilePath.exists()) {
                    throw new Exception("");
                }
                if (!myFilePath.mkdir()) {
                    throw new Exception("");
                }
                warn("创建目录" + filestr + "完成！");
                setDoMsg("创建目录" + filestr + "成功！");
            } catch (Exception er) {
                warn("创建目录" + filestr + "失败！");
                setDoMsg("创建目录" + filestr + "失败！");
            }

        }

        //列表显示
        try {
            ClassFileFilter classFilter = new ClassFileFilter();
            File myfile = new File(getPathStr());
            File[] currentDirFiles = myfile.listFiles(classFilter);
            for (File f : currentDirFiles) {
                if (f.getName().equalsIgnoreCase(".svn")) {
                    continue;
                }
                FileProp fp = new FileProp();
                fp.setFilename(f.getName());
                fp.setPath(getPathStr() + f.getName());
                fp.setLastmodify(TextUtils.Date2String(new Date(f.lastModified()),
                        "yyyy-MM-dd HH:mm:ss"));
                if (f.isDirectory()) {
                    fp.setDir(true);
                    getFileList().add(0, fp);
                } else {
                    fp.setSize(f.length() + " 字节");
                    getFileList().add(fp);
                }
            }
            Collections.sort(getFileList());
            if (getDoMsg().equals("")) {
                setDoMsg("共" + getFileList().size() + "个对象");
            }
            //地址栏显示
            String str = "", dir;
            String pathstr = getPathStr().trim();
            if (pathstr.startsWith(root)) {
                dir = root;
                pathstr = pathstr.substring(dir.length());
                str = str + dir;
                FileProp fp = new FileProp();
                fp.setPath(dir);
                fp.setFilename(str);
                getLinkList().add(fp);
            }
            if (!pathstr.endsWith(File.separator)) {
                pathstr = pathstr + File.separator;
            }
            while (!pathstr.equals("")) {
                if (pathstr.indexOf(File.separator) == 0) {
                    pathstr = pathstr.substring(1);
                }
                if (pathstr.indexOf(File.separator) > 0) {
                    dir = pathstr.substring(0,
                            pathstr.indexOf(File.separator) + 1);
                    pathstr = pathstr.substring(pathstr.indexOf(File.separator)
                            + 1);
                    str = str + dir;
                    FileProp fp = new FileProp();
                    fp.setPath(dir);
                    fp.setFilename(str);
                    getLinkList().add(fp);
                }
            }
            if (pathStr.startsWith(Initializer.getWebpath() + "WEB-INF" + File.separator + "src")) {
                showmakebtn = true;
            }
            setAttribute("smupload", this);
            return new ActionForward("upload.jsp");
        } catch (Exception ex) {
            warn("未知错误:" + ex);
        }
        return null;
    }

    /**
     * @return the pathStr
     */
    public String getPathStr() {
        return pathStr;
    }

    /**
     * @param pathStr the pathStr to set
     */
    public void setPathStr(String pathStr) {
        this.pathStr = pathStr;
    }

    /**
     * @return the fileList
     */
    public List<FileProp> getFileList() {
        return fileList;
    }

    /**
     * @param fileList the fileList to set
     */
    public void setFileList(List<FileProp> fileList) {
        this.fileList = fileList;
    }

    /**
     * @return the linkList
     */
    public List<FileProp> getLinkList() {
        return linkList;
    }

    /**
     * @param linkList the linkList to set
     */
    public void setLinkList(List<FileProp> linkList) {
        this.linkList = linkList;
    }

    /**
     * @return the dotype
     */
    public String getDotype() {
        return dotype;
    }

    /**
     * @param dotype the dotype to set
     */
    public void setDotype(String dotype) {
        this.dotype = dotype;
    }

    /**
     * @return the doMsg
     */
    public String getDoMsg() {
        return doMsg;
    }

    /**
     * @param doMsg the doMsg to set
     */
    public void setDoMsg(String doMsg) {
        this.doMsg = doMsg;
    }

    /**
     * @return the file
     */
    public FileItem getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(FileItem file) {
        this.file = file;
    }

    /**
     * @return the dirname
     */
    public String getDirname() {
        return dirname;
    }

    /**
     * @param dirname the dirname to set
     */
    public void setDirname(String dirname) {
        this.dirname = dirname;
    }

    /**
     * @return the selfilename
     */
    public String[] getSelfilename() {
        return selfilename;
    }

    /**
     * @param selfilename the selfilename to set
     */
    public void setSelfilename(String[] selfilename) {
        this.selfilename = selfilename;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the showmakebtn
     */
    public boolean isShowmakebtn() {
        return showmakebtn;
    }

    /**
     * @return the forward
     */
    public String getForward() {
        return forward;
    }

    /**
     * @param forward the forward to set
     */
    public void setForward(String forward) {
        this.forward = forward;
    }
}
