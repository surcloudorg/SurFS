/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.svn;

import com.autumn.core.log.LogFactory;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.diff.SVNDeltaProcessor;
import org.tmatesoft.svn.core.io.diff.SVNDiffWindow;

/**
 * <p>Title: 导出SVN目录-记录详细文件明细</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class ExportEditor implements ISVNEditor {

    private File myRootDirectory;
    private SVNDeltaProcessor myDeltaProcessor;
    private List<String> addFiles = null;

    public ExportEditor(File root, List<String> addFiles) {
        this.addFiles = addFiles;
        myRootDirectory = root;
        myDeltaProcessor = new SVNDeltaProcessor();
    }

    @Override
    public void targetRevision(long revision) throws SVNException {
    }

    @Override
    public void openRoot(long revision) throws SVNException {
    }

    @Override
    public void addDir(String path, String copyFromPath,
            long copyFromRevision) throws SVNException {
        File newDir = new File(myRootDirectory, path);
        if (!newDir.exists()) {
            if (!newDir.mkdirs()) {
                SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.IO_ERROR, "error: failed to add the directory ''{0}''.",
                        newDir);
                throw new SVNException(err);
            }
        }
        LogFactory.debug("添加目录：" + path, this.getClass());
    }

    @Override
    public void openDir(String path, long revision) throws SVNException {
    }

    @Override
    public void changeDirProperty(String name, SVNPropertyValue property) throws
            SVNException {
    }

    @Override
    public void addFile(String path, String copyFromPath,
            long copyFromRevision) throws SVNException {
        File file = new File(myRootDirectory, path);
        if (file.exists()) {
            SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.IO_ERROR,
                    "error: exported file ''{0}'' already exists!", file);
            throw new SVNException(err);
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.IO_ERROR, "error: cannot create new  file ''{0}''",
                    file);
            throw new SVNException(err);
        }
    }

    @Override
    public void openFile(String path, long revision) throws SVNException {
    }

    @Override
    public void changeFileProperty(String path, String name,
            SVNPropertyValue property) throws
            SVNException {
    }

    @Override
    public void applyTextDelta(String path, String baseChecksum) throws
            SVNException {
        myDeltaProcessor.applyTextDelta((File) null,
                new File(myRootDirectory, path), false);
    }

    @Override
    public OutputStream textDeltaChunk(String path,
            SVNDiffWindow diffWindow) throws
            SVNException {
        return myDeltaProcessor.textDeltaChunk(diffWindow);
    }

    @Override
    public void textDeltaEnd(String path) throws SVNException {
        myDeltaProcessor.textDeltaEnd();
    }

    @Override
    public void closeFile(String path, String textChecksum) throws
            SVNException {
        addFiles.add(path);
        LogFactory.debug("添加文件：" + path, this.getClass());
    }

    @Override
    public void closeDir() throws SVNException {
    }

    @Override
    public void deleteEntry(String path, long revision) throws SVNException {
    }

    @Override
    public void absentDir(String path) throws SVNException {
    }

    @Override
    public void absentFile(String path) throws SVNException {
    }

    @Override
    public SVNCommitInfo closeEdit() throws SVNException {
        return null;
    }

    @Override
    public void abortEdit() throws SVNException {
    }
}
