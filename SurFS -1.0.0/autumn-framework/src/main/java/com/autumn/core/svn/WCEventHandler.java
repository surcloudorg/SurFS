package com.autumn.core.svn;

import com.autumn.core.log.LogFactory;
import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNEventAction;

/**
 * <p>Title: SVN工作拷贝时间监听</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class WCEventHandler implements ISVNEventHandler {

    @Override
    public void handleEvent(SVNEvent event, double progress) {
        SVNEventAction action = event.getAction();
        if (action == SVNEventAction.ADD) {
            LogFactory.info("A     " + event.getFile().getName(),
                    WCEventHandler.class);
        } else if (action == SVNEventAction.COPY) {
            LogFactory.info("A  +  " + event.getFile().getName(),
                    WCEventHandler.class);
        } else if (action == SVNEventAction.DELETE) {
            LogFactory.info("D     " + event.getFile().getName(),
                    WCEventHandler.class);
        } else if (action == SVNEventAction.LOCKED) {
            LogFactory.info("L     " + event.getFile().getName(),
                    WCEventHandler.class);
        } else if (action == SVNEventAction.LOCK_FAILED) {
            LogFactory.info("failed to lock    " + event.getFile().getName(),
                    WCEventHandler.class);
        }
    }

    @Override
    public void checkCancelled() throws SVNCancelException {
    }
}
