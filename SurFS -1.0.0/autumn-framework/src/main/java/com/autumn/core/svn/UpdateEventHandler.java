package com.autumn.core.svn;

import com.autumn.core.log.LogFactory;
import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNEventAction;
import org.tmatesoft.svn.core.wc.SVNStatusType;

/**
 * <p>Title: SVN文件更新句柄</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class UpdateEventHandler implements ISVNEventHandler {

    @Override
    public void handleEvent(SVNEvent event, double progress) {
        SVNEventAction action = event.getAction();
        String pathChangeType = " ";
        if (action == SVNEventAction.UPDATE_ADD) {
            pathChangeType = "A";
        } else if (action == SVNEventAction.UPDATE_DELETE) {
            pathChangeType = "D";
        } else if (action == SVNEventAction.UPDATE_UPDATE) {
            SVNStatusType contentsStatus = event.getContentsStatus();
            if (contentsStatus == SVNStatusType.CHANGED) {
                pathChangeType = "U";
            } else if (contentsStatus == SVNStatusType.CONFLICTED) {
                pathChangeType = "C";
            } else if (contentsStatus == SVNStatusType.MERGED) {
                pathChangeType = "G";
            }
        } else if (action == SVNEventAction.UPDATE_EXTERNAL) {
            LogFactory.info("Fetching external item into '"
                    + event.getFile().getAbsolutePath() + "'",
                    this.getClass());
            LogFactory.info("External at revision " + event.getRevision(),
                    this.getClass());
            return;
        } else if (action == SVNEventAction.UPDATE_COMPLETED) {
            LogFactory.info("At revision " + event.getRevision(), getClass());
            return;
        } else if (action == SVNEventAction.ADD) {
            LogFactory.info("A     " + event.getFile().getAbsoluteFile(), getClass());
            return;
        } else if (action == SVNEventAction.DELETE) {
            LogFactory.info("D     " + event.getFile().getAbsoluteFile(), getClass());
            return;
        } else if (action == SVNEventAction.LOCKED) {
            LogFactory.info("L     " + event.getFile().getAbsoluteFile(), getClass());
            return;
        } else if (action == SVNEventAction.LOCK_FAILED) {
            LogFactory.info("failed to lock    " + event.getFile().getAbsoluteFile(),
                    getClass());
            return;
        }

        SVNStatusType propertiesStatus = event.getPropertiesStatus();
        String propertiesChangeType = " ";
        if (propertiesStatus == SVNStatusType.CHANGED) {
            propertiesChangeType = "U";
        } else if (propertiesStatus == SVNStatusType.CONFLICTED) {
            propertiesChangeType = "C";
        } else if (propertiesStatus == SVNStatusType.MERGED) {
            propertiesChangeType = "G";
        }

        String lockLabel = " ";
        SVNStatusType lockType = event.getLockStatus();

        if (lockType == SVNStatusType.LOCK_UNLOCKED) {
            lockLabel = "B";
        }
        LogFactory.info(pathChangeType + propertiesChangeType + lockLabel
                + "       " + event.getFile().getAbsoluteFile(), this.getClass());
    }

    @Override
    public void checkCancelled() throws SVNCancelException {
    }
}
