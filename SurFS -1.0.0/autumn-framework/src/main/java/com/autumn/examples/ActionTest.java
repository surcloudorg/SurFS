package com.autumn.examples;

import com.autumn.core.web.Action;
import com.autumn.core.web.NonForm;
import com.autumn.core.web.ActionForward;
import com.autumn.core.web.ActionMethod;

/**
 * <p>Title: web控制器例子</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class ActionTest extends Action {

    private String button = null;

    @Override
    public ActionForward execute() {
        if (button == null) {
            return new ActionForward("input.jsp");
        } else {
            Demo demo = new Demo();
            this.putBean(demo);//注入
            SoapDemoImpl sdi = new SoapDemoImpl(null);
            try {
                sdi.addDemo(demo);//插入
                this.setAttribute("message", "插入数据库成功！");
                this.setAttribute("demo", demo);
                return new ActionForward("result.jsp");
            } catch (Exception r) {
                this.error("插入失败:" + r.getMessage());
                this.setAttribute("demo", demo);
                this.setAttribute("error", "插入失败:" + r.getMessage());
                return new ActionForward("input.jsp");
            }

        }
    }

    @ActionMethod(contentType = "text/plain; charset=gbk")
    public String getText() {
        return "测试getText函数";
    }

    @ActionMethod(contentType = "text/html; charset=gbk")
    public String getHtml() {
        String html = "<h1><strong>测试getHtml函数</strong></h1>";
        return html;
    }

    /**
     * @param button the button to set
     */
    public void setButton(String button) {
        this.button = button;
    }
}
