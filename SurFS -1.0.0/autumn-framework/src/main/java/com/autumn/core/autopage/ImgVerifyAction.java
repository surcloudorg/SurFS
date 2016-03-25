package com.autumn.core.autopage;

import com.autumn.core.web.Action;
import com.autumn.core.web.ActionForward;
import com.autumn.core.web.Forward;
import com.autumn.core.web.ObjectForward;
import com.autumn.util.Function;


/**
 * <p>Title: 输入图形验证码ACTION</p>
 *
 * <p>Description: 手工处理VerifyCodeEnter维护的队列（lists）</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class ImgVerifyAction extends Action {

    private String dotype = null; //操作类型
    private int enterHashcode = 0;
    private String code = null;

    @Override
    public Forward execute() {
        if (dotype == null || enterHashcode == 0) {
            VerifyCodeEnter enter = VerifyCodeEnter.getEnterUser();
            if (enter != null) {
                enterHashcode = enter.hashCode();
                setAttribute("enter", enter);
                setAttribute("verify", this);
            }
            return new ActionForward("verify.jsp");
        }
        VerifyCodeEnter vce = VerifyCodeEnter.getEnter(enterHashcode);
        if (vce == null) {
            vce = VerifyCodeEnter.getEnterUser();
            if (vce != null) {
                enterHashcode = vce.hashCode();
                setAttribute("enter", vce);
                setAttribute("verify", this);
            }
            return new ActionForward("empty.jsp");
        }
        if (dotype.equalsIgnoreCase("看不清，换一张")) {
            try {
                vce.clearImgContent();
                setAttribute("enter", vce);
                setAttribute("verify", this);
            } catch (Exception r) {
                VerifyCodeEnter username = VerifyCodeEnter.getEnterUser();
                if (username != null) {
                    enterHashcode = username.hashCode();
                    setAttribute("enter", username);
                    setAttribute("verify", this);
                }
            }
            return new ActionForward("verify.jsp");
        } else if (dotype.equalsIgnoreCase("getimg")) {//写入byte【】
            byte[] bs = vce.getImgContent();
            return new ObjectForward(bs, Function.getContentType(bs));
        } else {
            if (code != null) {
                vce.setCode(code);
            }
            VerifyCodeEnter username = VerifyCodeEnter.getEnterUser();
            if (username != null) {
                enterHashcode = username.hashCode();
                setAttribute("enter", username);
                setAttribute("verify", this);
            }
            return new ActionForward("verify.jsp");
        }
    }

    /**
     * @return the dotype
     */
    public String getDotype() {
        return dotype;
    }

    /**
     * @param dotype the dotype to set
     */
    public void setDotype(String dotype) {
        this.dotype = dotype;
    }

    /**
     * @return the enterHashcode
     */
    public int getEnterHashcode() {
        return enterHashcode;
    }

    /**
     * @param enterHashcode the enterHashcode to set
     */
    public void setEnterHashcode(int enterHashcode) {
        this.enterHashcode = enterHashcode;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }
}
