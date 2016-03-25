package com.autumn.core.svn;

import com.autumn.core.log.LogFactory;
import java.util.ArrayList;
import java.util.List;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.wc.ISVNInfoHandler;
import org.tmatesoft.svn.core.wc.SVNInfo;

/**
 * <p>Title: SVN操作报告</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class InfoHandler implements ISVNInfoHandler {

    private List<SVNInfo> infos = new ArrayList<SVNInfo>();

    public static void printSVNInfo(SVNInfo info) {
        LogFactory.debug("-----------------INFO-----------------", InfoHandler.class);
        LogFactory.debug("Local Path: " + info.getPath(), InfoHandler.class);
        LogFactory.debug("URL: " + info.getURL(), InfoHandler.class);
        if (info.isRemote() && info.getRepositoryRootURL() != null) {
            LogFactory.debug("Repository Root URL: " + info.getRepositoryRootURL(), InfoHandler.class);
        }
        if (info.getRepositoryUUID() != null) {
            LogFactory.debug("Repository UUID: " + info.getRepositoryUUID(), InfoHandler.class);
        }
        LogFactory.debug("Revision: " + info.getRevision().getNumber(), InfoHandler.class);
        LogFactory.debug("Node Kind: " + info.getKind().toString(), InfoHandler.class);
        if (!info.isRemote()) {
            LogFactory.debug("Schedule: " + (info.getSchedule() != null ? info.getSchedule() : "normal"), InfoHandler.class);
        }
        LogFactory.debug("Last Changed Author: " + info.getAuthor(), InfoHandler.class);
        LogFactory.debug("Last Changed Revision: " + info.getCommittedRevision().getNumber(), InfoHandler.class);
        LogFactory.debug("Last Changed Date: " + info.getCommittedDate(), InfoHandler.class);
        if (info.getPropTime() != null) {
            LogFactory.debug("Properties Last Updated: " + info.getPropTime(), InfoHandler.class);
        }
        if (info.getKind() == SVNNodeKind.FILE && info.getChecksum() != null) {
            if (info.getTextTime() != null) {
                LogFactory.debug("Text Last Updated: " + info.getTextTime(), InfoHandler.class);
            }
            LogFactory.debug("Checksum: " + info.getChecksum(), InfoHandler.class);
        }
        if (info.getLock() != null) {
            if (info.getLock().getID() != null) {
                LogFactory.debug("Lock Token: " + info.getLock().getID(), InfoHandler.class);
            }
            LogFactory.debug("Lock Owner: " + info.getLock().getOwner(), InfoHandler.class);
            LogFactory.debug("Lock Created: " + info.getLock().getCreationDate(), InfoHandler.class);
            if (info.getLock().getExpirationDate() != null) {
                LogFactory.debug("Lock Expires: " + info.getLock().getExpirationDate(), InfoHandler.class);
            }
            if (info.getLock().getComment() != null) {
                LogFactory.debug("Lock Comment: " + info.getLock().getComment(), InfoHandler.class);
            }
        }
    }

    @Override
    public void handleInfo(SVNInfo info) {
        infos.add(info);
    }

    public List<SVNInfo> getInfos() {
        return infos;
    }
}
