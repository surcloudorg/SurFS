/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.svn;

import com.autumn.core.ThreadPools;
import java.util.HashMap;

/**
 * <p>Title: SVN工作拷贝-执行调度</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class WCopyActionMgr {

    public static final HashMap<String, WCopyAction> actions = new HashMap<String, WCopyAction>();

    /**
     * @param wcopyid 唯一执行标识
     * @return WCopyAction
     */
    public static WCopyAction get(String wcopyid) {
        synchronized (actions) {
            return actions.get(wcopyid);
        }
    }

    /**
     * 加入执行，如果未完成，不允许重复执行
     *
     * @param action
     * @return boolean
     */
    public static boolean put(WCopyAction action) {
        synchronized (actions) {
            if (actions.get(action.getWcopyId()) == null) {
                actions.put(action.getWcopyId(), action);
                ThreadPools.startThread(action);
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 移除
     *
     * @param action
     */
    public static void remove(WCopyAction action) {
        synchronized (actions) {
            actions.remove(action.getWcopyId());
        }
    }

    /**
     * 移除
     *
     * @param wcopyid
     * @return WCopyAction
     */
    public static WCopyAction remove(String wcopyid) {
        synchronized (actions) {
            return actions.remove(wcopyid);
        }
    }
}
