package com.autumn.core.svn;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * <p>Title: 提交SVN</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class Commit {

    private String repositoryUrl = "";
    private String userName;
    private String passWord;
    private SVNRepository repository = null;

    public Commit() {
        Setup.setupLibrary();
    }

    /**
     * 测试
     */
    public static void test(String[] args) throws SVNException, IOException {
        Commit commit = new Commit();
        commit.setRepositoryUrl("https://liushepeng.autumn.com/svn/liushepeng/");
        commit.setUserName("liushepeng");
        commit.setPassWord("");
        SVNCommitInfo info = commit.modifyFile("test", "test/file.txt", new File("D:\\qquser.txt"));
        //info = commit.copyDir("classes", "test2");
        //info = commit.deleteDir("test2");
        //info = commit.addFile("test", "test/file2.txt", new File("D:\\腾讯微博账号.txt"));
        System.out.println(info);
    }

    /**
     * 初始化
     *
     * @throws SVNException
     */
    private void init() throws SVNException {
        if (getRepository() != null) {
            return;
        }
        SVNURL svnurl = SVNURL.parseURIEncoded(getRepositoryUrl());
        setRepository(SVNRepositoryFactory.create(svnurl));
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(
                getUserName(), getPassWord());
        getRepository().setAuthenticationManager(authManager);
        SVNNodeKind nodeKind = getRepository().checkPath("", -1);
        if (nodeKind == SVNNodeKind.NONE) {
            SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.UNKNOWN, "SVN地址没有文件", getRepositoryUrl());
            throw new SVNException(err);
        } else if (nodeKind == SVNNodeKind.FILE) {
            SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.UNKNOWN, "SVN地址不是一个目录", getRepositoryUrl());
            throw new SVNException(err);
        }
    }

    /**
     * 添加文件
     *
     * @param dirPath
     * @param filePath
     * @param localfile
     * @return SVNCommitInfo
     * @throws SVNException
     * @throws IOException
     */
    public SVNCommitInfo addFile(String dirPath, String filePath, File localfile) throws
            SVNException, IOException {
        init();
        if (localfile == null || (!localfile.exists())) {
            return null;
        }
        ISVNEditor editor = getRepository().getCommitEditor("directory and file added", null);
        return addFile(editor, dirPath, filePath, new FileInputStream(localfile));
    }

    /**
     * 修改文件内容
     *
     * @param dirPath
     * @param filePath
     * @param localfile
     * @return SVNCommitInfo
     * @throws SVNException
     * @throws IOException
     */
    public SVNCommitInfo modifyFile(String dirPath, String filePath,
            File localfile) throws
            SVNException, IOException {
        init();
        if (localfile == null || (!localfile.exists())) {
            return null;
        }
        ISVNEditor editor = getRepository().getCommitEditor("file contents changed", null);
        return modifyFile(editor, dirPath, filePath, new FileInputStream(localfile));
    }

    /**
     * 复制目录
     *
     * @param srcDirPath
     * @param dstDirPath
     * @return SVNCommitInfo
     * @throws SVNException
     */
    public SVNCommitInfo copyDir(String srcDirPath,
            String dstDirPath) throws SVNException {
        init();
        String absoluteSrcPath = getRepository().getRepositoryPath(srcDirPath);
        long srcRevision = getRepository().getLatestRevision();
        ISVNEditor editor = getRepository().getCommitEditor("directory copied", null);
        return copyDir(editor, absoluteSrcPath, dstDirPath, srcRevision);
    }

    /**
     * 删除目录
     *
     * @param dirPath
     * @return SVNCommitInfo
     * @throws SVNException
     */
    public SVNCommitInfo deleteDir(String dirPath) throws SVNException {
        init();
        ISVNEditor editor = getRepository().getCommitEditor("directory deleted", null);
        return deleteDir(editor, dirPath);
    }

    /**
     * 添加文件
     *
     * @param editor
     * @param dirPath
     * @param filePath
     * @param data
     * @return SVNCommitInfo
     * @throws SVNException
     */
    public static SVNCommitInfo addFile(ISVNEditor editor, String dirPath,
            String filePath, InputStream data) throws SVNException {
        editor.openRoot(-1);
        try {
            editor.addDir(dirPath, null, -1);
        } catch (Exception er) {
        }
        editor.addFile(filePath, null, -1);
        editor.applyTextDelta(filePath, null);
        SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
        String checksum = deltaGenerator.sendDelta(filePath, data, editor, true);
        editor.closeFile(filePath, checksum);
        editor.closeDir();
        editor.closeDir();
        return editor.closeEdit();
    }

    /**
     * 修改文件
     *
     * @param editor
     * @param dirPath
     * @param filePath
     * @param data
     * @return SVNCommitInfo
     * @throws SVNException
     */
    public static SVNCommitInfo modifyFile(ISVNEditor editor, String dirPath,
            String filePath, InputStream data) throws
            SVNException {
        editor.openRoot(-1);
        editor.openDir(dirPath, -1);
        editor.openFile(filePath, -1);
        editor.applyTextDelta(filePath, null);
        SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
        String checksum = deltaGenerator.sendDelta(filePath,
                data, editor, true);
        editor.closeFile(filePath, checksum);
        editor.closeDir();
        editor.closeDir();
        return editor.closeEdit();
    }

    /**
     * 复制目录
     *
     * @param editor
     * @param srcDirPath
     * @param dstDirPath
     * @param revision
     * @return SVNCommitInfo
     * @throws SVNException
     */
    public static SVNCommitInfo copyDir(ISVNEditor editor, String srcDirPath,
            String dstDirPath, long revision) throws
            SVNException {
        editor.openRoot(-1);
        editor.addDir(dstDirPath, srcDirPath, revision);
        editor.closeDir();
        editor.closeDir();
        return editor.closeEdit();
    }

    /**
     * 删除目录
     *
     * @param editor
     * @param dirPath
     * @return SVNCommitInfo
     * @throws SVNException
     */
    public static SVNCommitInfo deleteDir(ISVNEditor editor, String dirPath) throws
            SVNException {
        editor.openRoot(-1);
        editor.deleteEntry(dirPath, -1);
        editor.closeDir();
        return editor.closeEdit();
    }

    /**
     * @return the repositoryUrl
     */
    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    /**
     * @param repositoryUrl the repositoryUrl to set
     */
    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the passWord
     */
    public String getPassWord() {
        return passWord;
    }

    /**
     * @param passWord the passWord to set
     */
    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    /**
     * @return the repository
     */
    public SVNRepository getRepository() {
        return repository;
    }

    /**
     * @param repository the repository to set
     */
    public void setRepository(SVNRepository repository) {
        this.repository = repository;
    }
}
