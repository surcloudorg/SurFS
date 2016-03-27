/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.svn;

import com.autumn.core.log.LogFactory;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNLock;
import org.tmatesoft.svn.core.wc.*;

/**
 * <p>Title: SVN文件状态</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class StatusHandler implements ISVNStatusHandler, ISVNEventHandler {

    private boolean myIsRemote;
    private List<Properties> statusList = new ArrayList<Properties>();

    public StatusHandler(boolean isRemote) {
        myIsRemote = isRemote;
    }

    public static void printProperties(Properties info) {
        LogFactory.debug("-----------------Properties-----------------",
                StatusHandler.class);
        Enumeration e = info.propertyNames();
        while (e.hasMoreElements()) {
            String key = e.nextElement().toString();
            String value = info.getProperty(key);
            LogFactory.debug(key + "=" + value, StatusHandler.class);
        }
    }

    @Override
    public void handleStatus(SVNStatus status) {
        SVNStatusType contentsStatus = status.getContentsStatus();
        String pathChangeType = " ";
        boolean isAddedWithHistory = status.isCopied();
        if (contentsStatus == SVNStatusType.STATUS_MODIFIED) {
            pathChangeType = "M";
        } else if (contentsStatus == SVNStatusType.STATUS_CONFLICTED) {
            pathChangeType = "C";
        } else if (contentsStatus == SVNStatusType.STATUS_DELETED) {
            pathChangeType = "D";
        } else if (contentsStatus == SVNStatusType.STATUS_ADDED) {
            pathChangeType = "A";
        } else if (contentsStatus == SVNStatusType.STATUS_UNVERSIONED) {
            pathChangeType = "?";
        } else if (contentsStatus == SVNStatusType.STATUS_EXTERNAL) {
            pathChangeType = "X";
        } else if (contentsStatus == SVNStatusType.STATUS_IGNORED) {
            pathChangeType = "I";
        } else if (contentsStatus == SVNStatusType.STATUS_MISSING
                || contentsStatus == SVNStatusType.STATUS_INCOMPLETE) {
            pathChangeType = "!";
        } else if (contentsStatus == SVNStatusType.STATUS_OBSTRUCTED) {
            pathChangeType = "~";
        } else if (contentsStatus == SVNStatusType.STATUS_REPLACED) {
            pathChangeType = "R";
        } else if (contentsStatus == SVNStatusType.STATUS_NONE
                || contentsStatus == SVNStatusType.STATUS_NORMAL) {
            pathChangeType = " ";
        }
        String remoteChangeType = null;
        if (status.getRemotePropertiesStatus() != SVNStatusType.STATUS_NONE
                || status.getRemoteContentsStatus() != SVNStatusType.STATUS_NONE) {
            remoteChangeType = "" + status.getRemoteContentsStatus().getCode();
        }
        SVNStatusType propertiesStatus = status.getPropertiesStatus();
        String propertiesChangeType = " ";
        if (propertiesStatus == SVNStatusType.STATUS_MODIFIED) {
            propertiesChangeType = "M";
        } else if (propertiesStatus == SVNStatusType.STATUS_CONFLICTED) {
            propertiesChangeType = "C";
        }
        boolean isLocked = status.isLocked();
        boolean isSwitched = status.isSwitched();
        SVNLock localLock = status.getLocalLock();
        SVNLock remoteLock = status.getRemoteLock();
        String lockLabel = " ";
        if (localLock != null) {
            lockLabel = "K";
            if (remoteLock != null) {
                if (!remoteLock.getID().equals(localLock.getID())) {
                    lockLabel = "T";
                }
            } else {
                if (myIsRemote) {
                    lockLabel = "B";
                }
            }
        } else if (remoteLock != null) {
            lockLabel = "O";
        }
        long workingRevision = status.getRevision().getNumber();
        long lastChangedRevision = status.getCommittedRevision().getNumber();
        Properties p = new Properties();
        p.setProperty("pathChangeType", pathChangeType);
        p.setProperty("propertiesChangeType", propertiesChangeType);
        p.setProperty("isLocked", Boolean.toString(isLocked));
        p.setProperty("isAddedWithHistory", Boolean.toString(isAddedWithHistory));
        p.setProperty("isSwitched", Boolean.toString(isSwitched));
        p.setProperty("lockLabel", lockLabel);
        if (remoteChangeType != null) {
            p.setProperty("remoteChangeType", remoteChangeType);
        }
        p.setProperty("workingRevision", Long.toString(workingRevision));
        p.setProperty("lastChangedRevision", (lastChangedRevision >= 0 ? String.valueOf(lastChangedRevision)
                : "?"));
        p.setProperty("author", (status.getAuthor() != null ? status.getAuthor()
                : "?"));
        p.setProperty("filename", status.getFile().getPath());
        statusList.add(p);
    }

    @Override
    public void handleEvent(SVNEvent event, double progress) {
        SVNEventAction action = event.getAction();
        if (action == SVNEventAction.STATUS_COMPLETED) {
            LogFactory.info("Status against revision:  " + event.getRevision(),
                    StatusHandler.class);
        }

    }

    @Override
    public void checkCancelled() throws SVNCancelException {
    }

    public List<Properties> getStatusList() {
        return statusList;
    }
}
