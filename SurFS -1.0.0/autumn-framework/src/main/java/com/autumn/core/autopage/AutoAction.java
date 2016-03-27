/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.autopage;

import com.autumn.core.web.Action;
import com.autumn.core.web.ActionMethod;
import com.autumn.core.web.Forward;
import com.autumn.core.web.PlainForward;


/**
 * <p>Title: AUTOACTION</p>
 *
 * <p>Description: 执行表操作</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class AutoAction extends Action {

    private ActionConfig cfg = null;

    @ActionMethod(contentType = "*/*")
    public Forward insert() {
        try {
            cfg = this.getAutoActionConfig();
        } catch (Exception re) {
            return new PlainForward("AutoAction配置错误:" + re.getMessage());
        }
        InsertAction insert = new InsertAction(this);
        return insert.execute();
    }

    @ActionMethod(contentType = "*/*")
    public Forward update() {
        try {
            cfg = this.getAutoActionConfig();
        } catch (Exception re) {
            return new PlainForward("AutoAction配置错误:" + re.getMessage());
        }
        UpdateAction update = new UpdateAction(this);
        return update.execute();
    }

    @Override
    public Forward execute() {
        try {
            cfg = this.getAutoActionConfig();
        } catch (Exception re) {
            return new PlainForward("AutoAction配置错误:" + re.getMessage());
        }
        String query_button = getRequest().getParameter(DeleteAction.SUBMIT_BUTTON_NAME);
        if (query_button != null) {//需要执行删除操作
            DeleteAction delete = new DeleteAction(this);
            delete.execute();
        }
        QueryAction query = new QueryAction(this);
        return query.execute();
    }

    /**
     * @return the cfg
     */
    public ActionConfig getCfg() {
        return cfg;
    }

    public void setMessage(String message) {
        this.setAttribute("domessage", message);
    }
}
