/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.svn;

import com.autumn.core.log.LogFactory;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.ISVNReporter;
import org.tmatesoft.svn.core.io.ISVNReporterBaton;

/**
 * <p>Title: 导出SVN目录-报告</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class ExportReporterBaton implements ISVNReporterBaton {

    private long exportRevision;

    public ExportReporterBaton(long revision) {
        exportRevision = revision;
    }

    @Override
    public void report(ISVNReporter reporter) throws SVNException {
        try {
            reporter.setPath("", null, exportRevision, SVNDepth.INFINITY, true);
            reporter.finishReport();
        } catch (SVNException svne) {
            reporter.abortReport();
            LogFactory.error("Export操作失败:", getClass());
            SVNErrorMessage err = svne.getErrorMessage();
            while (err != null) {
                LogFactory.error(err.getErrorCode().getCode() + " : " + err.getMessage(), getClass());
                err = err.getChildErrorMessage();
            }
        }
    }
}
