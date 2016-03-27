/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.svn;

import com.autumn.core.log.LogFactory;
import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNEventAction;

/**
 * <p>Title: 提交SVN--操作明细</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class CommitEventHandler implements ISVNEventHandler {

    @Override
    public void handleEvent(SVNEvent event, double progress) {
        SVNEventAction action = event.getAction();
        if (action == SVNEventAction.COMMIT_MODIFIED) {
            LogFactory.info("Sending   " + event.getFile().getName(), CommitEventHandler.class);
        } else if (action == SVNEventAction.COMMIT_DELETED) {
            LogFactory.info("Deleting   " + event.getFile().getName(), CommitEventHandler.class);
        } else if (action == SVNEventAction.COMMIT_REPLACED) {
            LogFactory.info("Replacing   " + event.getFile().getName(), CommitEventHandler.class);
        } else if (action == SVNEventAction.COMMIT_DELTA_SENT) {
            LogFactory.debug("Transmitting file data....", CommitEventHandler.class);
        } else if (action == SVNEventAction.COMMIT_ADDED) {
            String mimeType = event.getMimeType();
            if (SVNProperty.isBinaryMimeType(mimeType)) {
                LogFactory.info("Adding  (bin)  " + event.getFile().getName(), CommitEventHandler.class);
            } else {
                LogFactory.info("Adding         " + event.getFile().getName(), CommitEventHandler.class);
            }
        }
    }

    @Override
    public void checkCancelled() throws SVNCancelException {
    }
}
