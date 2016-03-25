package com.autumn.util;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.tools.*;

/**
 * <p>Title: 源码编译器</p>
 *
 * <p>Description: 在线编译源码，不支持资源文件拷贝</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class Compiler {

    private List<File> files = new ArrayList<File>();//要编译的文件，包含文件夹
    private String outputDir = null;//输出路径
    private String srcDir = null;//src路径
    private String extdir = null;//java.ext.dirs
    private String classpath = null;//classpath
    private String compilerMessage = null;//编译消息
    private Charset charset = null;//源码字符集

    /**
     * 获取加载class所在的classpath绝对路径
     *
     * @param cls
     * @return String
     */
    public static String getJarPath(Class cls) {
        URL url = null;
        url = cls.getResource("");
        if (url.getProtocol().equalsIgnoreCase("jar")) {
            String jarUrl = url.getPath();
            try {
                jarUrl = URLDecoder.decode(jarUrl, "utf-8");
            } catch (Exception r) {
            }
            if (jarUrl.startsWith("file:/")) {
                jarUrl = jarUrl.substring(6);
            }
            jarUrl = jarUrl.split("\\!")[0];
            return jarUrl;
        } else {
            url = cls.getResource("/");
            String jarUrl = url.getPath();
            try {
                jarUrl = URLDecoder.decode(jarUrl, "utf-8");
            } catch (Exception r) {
            }
            if (jarUrl.startsWith("/")) {
                jarUrl = jarUrl.substring(1);
            }
            if (jarUrl.endsWith("/")) {
                jarUrl = jarUrl.substring(0, jarUrl.length() - 1);
            }
            return jarUrl;
        }
    }

    /**
     * 获取autumn.jar中MANIFEST.MF指定的jar文件绝对路径
     *
     * @return String[]
     */
    private String[] getManifestClassPath() {
        try {
            String jar = getJarPath(Compiler.class);
            File f = new File(jar);
            InputStream is = Compiler.class.getResourceAsStream("/META-INF/MANIFEST.MF");
            java.util.jar.Manifest mf = new java.util.jar.Manifest(is);
            String ss = mf.getMainAttributes().getValue("Class-Path");
            String[] fs = ss.replace('/', File.separatorChar).split(" ");
            List<String> list = new ArrayList<String>();
            for (int ii = 0, count = fs.length; ii < count; ii++) {
                String fname = fs[ii];
                if (!fname.contains("jsp2.0")) {
                    list.add(f.getParentFile().getParent() + fname.substring(2));
                }
            }
            String[] newfs = new String[list.size()];
            return list.toArray(newfs);
        } catch (Exception re) {
        }
        return null;
    }

    /**
     * 编译
     *
     * @param srcDir
     * @param outputDir
     * @param charset
     * @throws Exception
     */
    public Compiler(String srcDir, String outputDir, String charset) throws Exception {
        File ff = new File(srcDir);
        if ((!ff.exists()) || (!ff.isDirectory())) {
            throw new Exception("指定的源路径无效！");
        }
        this.srcDir = ff.getAbsolutePath();
        File f = new File(outputDir);
        if (!f.exists()) {
            f.mkdirs();
        }
        if ((!f.exists()) || (!f.isDirectory())) {
            throw new Exception("指定的输出路径无效！");
        }
        this.outputDir = f.getAbsolutePath();
        this.charset = Charset.forName(charset);
        this.classpath = System.getProperty("java.class.path");
        this.extdir = System.getProperty("java.ext.dirs");
    }

    //编译
    public Compiler(String srcDir, String outputDir) throws Exception {
        this(srcDir, outputDir, "UTF-8");
    }

    /**
     * 添加编译源文件
     *
     * @param file
     * @throws Exception
     */
    public void addSrcFile(String file) throws Exception {
        File f = new File(file);
        if (!f.exists()) {
            throw new Exception("指定的文件无效！");
        }
        if (f.isFile()) {
            if (f.getAbsolutePath().toLowerCase().endsWith(".java")) {
                addFile(f);
            }
        } else {
            List<File> fs = getJavaFile(f);
            for (File ff : fs) {
                addFile(ff);
            }
        }
    }

    /**
     * 添加编译源文件
     *
     * @param file
     */
    private void addFile(File file) {
        for (File f : files) {
            if (file.getAbsolutePath().equals(f.getAbsolutePath())) {
                return;
            }
        }
        files.add(file);
    }

    /**
     * 获取所有需要编译的源文件
     *
     * @param file
     * @return List<File>
     */
    private List<File> getJavaFile(File file) {
        List<File> filelists = new ArrayList<File>();
        File[] tempList = file.listFiles();
        for (File temp : tempList) {
            if (temp.isFile()) {
                if (temp.getName().toLowerCase().endsWith(".java")) {
                    filelists.add(temp);
                }
            } else if (temp.isDirectory()) {
                List<File> lists = getJavaFile(temp);
                if (!lists.isEmpty()) {
                    filelists.addAll(lists);
                }
            } else {
                continue;
            }
        }
        return filelists;
    }

    /**
     * 指定java.ext.dirs
     *
     * @param extdir
     * @throws Exception
     */
    public void setExtdir(String extdir) throws Exception {
        File f = new File(extdir);
        if (!f.exists()) {
            throw new Exception("指定的文件无效！");
        }
        this.extdir = extdir;
    }

    /**
     * 指定classpath
     *
     * @param classpath
     * @throws Exception
     */
    public void addClassPath(String classpath) {
        File f = new File(classpath);
        if (!f.exists()) {
            return;
        }
        classpath = f.getAbsolutePath();
        if (this.classpath == null) {
            this.classpath = classpath;
        } else {
            if (!(File.pathSeparator + this.classpath + File.pathSeparator).contains(File.pathSeparator + classpath + File.pathSeparator)) {
                this.classpath = this.classpath + File.pathSeparator + classpath;
            }
        }
    }

    /**
     * 添加classpath
     *
     * @param classpath
     */
    public void addLibPath(String classpath) {
        File f = new File(classpath);
        if (!f.exists() || f.isFile()) {
            return;
        }
        File[] fs = f.listFiles();
        for (File file : fs) {
            String filename = file.getAbsolutePath();
            if (filename.toLowerCase().endsWith(".jar") || filename.toLowerCase().endsWith(".zip")) {
                addClassPath(filename);
            }
        }
    }

    /**
     * 编译
     *
     * @return boolean
     */
    @SuppressWarnings("unchecked")
    public boolean make() {
        if (files.isEmpty()) {
            return true;
        }
        String[] fs = getManifestClassPath();
        if (fs != null) {
            for (String ss : fs) {
                addClassPath(ss);
            }
        }
        List<String> options = new ArrayList<String>();
        options.add("-d");
        options.add(outputDir);
        if (extdir != null) {
            options.add("-extdirs");
            options.add(extdir);
        }
        options.add("-sourcepath");
        options.add(srcDir);
        options.add("-classpath");
        options.add(classpath);
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, charset);
        Iterable it = fileManager.getJavaFileObjectsFromFiles(files);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, it);
        boolean success = task.call();
        StringBuilder msg = new StringBuilder();
        Iterator dts = diagnostics.getDiagnostics().iterator();
        while (dts.hasNext()) {
            Diagnostic diagnostic = (Diagnostic) dts.next();
            msg.append(diagnostic.getMessage(null));
            msg.append("\r\n");
        }
        compilerMessage = msg.toString();
        if (success) {
            compilerMessage = compilerMessage.isEmpty() ? "编译成功！" : ("编译成功：" + compilerMessage);
        } else {
            compilerMessage = "编译错误：" + compilerMessage;
        }
        try {
            fileManager.close();
        } catch (Exception e) {
        }
        return success;
    }

    /**
     * @return the compilerMessage
     */
    public String getCompilerMessage() {
        return compilerMessage;

    }
}
