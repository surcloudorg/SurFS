package com.autumn.core.autopage;

import com.autumn.util.TextUtils;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: AUTOPAGE-表字段附加属性</p>
 *
 * <p>Description: 在insert,update中设置的附加属性</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class EditField extends ActionField {

    private boolean hiden = false;//隐藏
    private boolean readonly = false;//只读

    public EditField(ActionField actionfield) {
        super(actionfield);
        this.setItems(actionfield.getItems());
        this.setComment(actionfield.getComment());
        this.setFullName(actionfield.getFullName());
        this.setDefaultValue(actionfield.getDefaultValue());
        this.setDatefmt(actionfield.getDatefmt());
    }

    public EditField newCopy() {
        EditField q = new EditField(this);
        q.hiden = this.hiden;
        q.readonly = this.readonly;
        return q;
    }

    /**
     * @return the hiden
     */
    public boolean isHiden() {
        return hiden;
    }

    /**
     * @param hiden the hiden to set
     */
    public void setHiden(boolean hiden) {
        this.hiden = hiden;
    }

    public void setHiden(String hiden) {
        if (hiden != null) {
            try {
                this.hiden = TextUtils.parseBoolean(hiden);
            } catch (Exception e) {
            }
        }
    }

    /**
     * @return the readonly
     */
    public boolean isReadonly() {
        return readonly;
    }

    /**
     * @param readonly the readonly to set
     */
    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    public void setReadonly(String readonly) {
        if (readonly != null) {
            try {
                this.readonly = TextUtils.parseBoolean(readonly);
            } catch (Exception e) {
            }
        }
    }

    /**
     * 生成在页面显示的代码 class统一使用textbox
     *
     * @return String
     */
    public String getHtmlText(HashMap<String, String> map) {
        int editType = getEditType();
        StringBuilder sb = new StringBuilder();
        if (this.isHiden()) {//隐藏域
            sb.append("<input name=\"").append(getName()).append("\" type=\"hidden\" value=\"").append(getFieldValue()).append("\"/>");
            return sb.toString();
        }
        String readonlystr = "";
        if (isReadonly()) {
            readonlystr = "readonly=\"true\"";
        }
        if (editType == 3) {//组合框
            if (isReadonly()) {
                String ss = map.get(getFieldValue());
                if (ss == null) {
                    ss = getFieldValue();
                }
                sb.append("<input ").append(readonlystr).append(" name=\"").append(getName()).append("\" class=\"");
                sb.append(ActionField.CLASS_TEXTBOX).append("\"");
                sb.append(" value=\"").append(ss).append("\" ");
                sb.append(readonlystr).append("/>\r\n");
            } else {
                sb.append("<select  name=\"").append(this.getName());
                sb.append("\" class=\"").append(ActionField.CLASS_TEXTBOX).append("\">\r\n");
                for(Map.Entry<String,String> entry:map.entrySet()){
                    String key=entry.getKey();
                    String value = entry.getValue();
                    sb.append("<option value=\"").append(key).append("\"");
                    if (key.equalsIgnoreCase(this.getFieldValue())) {
                        sb.append(" selected=\"selected\"");
                    }
                    sb.append(">").append(value).append("</option>\r\n"); 
                }              
                sb.append("</select>");
            }
        } else if (editType == 4) {//日期
            if (isReadonly()) {
                sb.append("<input ").append(readonlystr).append(" name=\"").append(getName()).append("\" class=\"");
                sb.append(ActionField.CLASS_TEXTBOX).append("\" ");
                sb.append(" value=\"").append(getFieldValue()).append("\"/>\r\n");
            } else {
                sb.append("<input ").append(" name=\"").append(this.getName());
                sb.append("\" type=\"text\" class=\"").append(ActionField.CLASS_TEXTBOX);
                sb.append("\" id=\"").append(this.getName());
                sb.append("\" onfocus=\"WdatePicker({dateFmt:'").append(this.getDatefmt()).append("'})\" value=\"");
                sb.append(this.getFieldValue()).append("\" readonly=\"readonly\" />\r\n");
            }
        } else if (editType == 1) {//数字
            String js = "OnKeyUp=\"this.value=this.value.replace(/[^\\d]/g,'')\" onpaste=\"this.value=this.value.replace(/[^\\d]/g,'')\"";
            sb.append("<input ").append(readonlystr).append(" name=\"").append(getName()).append("\" class=\"");
            sb.append(ActionField.CLASS_TEXTBOX).append("\" ").append(js);
            sb.append(" value=\"").append(getFieldValue()).append("\"/>\r\n");
        } else if (editType == 2) {//数字
            String js = "OnKeyUp=\"this.value=this.value.replace(/[^\\d.]/g,'')\" onpaste=\"this.value=this.value.replace(/[^\\d.]/g,'')\"";
            sb.append("<input ").append(readonlystr).append(" name=\"").append(getName()).append("\" class=\"");
            sb.append(ActionField.CLASS_TEXTBOX).append("\" ").append(js);
            sb.append(" value=\"").append(getFieldValue()).append("\"/>\r\n");
        } else {//长度超过100,用textarea
            String js = "";
            if (this.getClassname().equalsIgnoreCase("java.lang.Boolean")) {//布尔
                js = " OnKeyUp=\"this.value=this.value.replace(/[^01]/g,'')\" onpaste=\"this.value=this.value.replace(/[^01]/g,'')\"";
            }
            if (this.getSize() <= 80) {
                sb.append("<input ").append(readonlystr).append(" name=\"").append(getName()).append("\" class=\"");
                if (this.getSize() <= 30) {
                    sb.append(ActionField.CLASS_TEXTBOX).append("\"");
                } else {
                    sb.append(ActionField.CLASS_BIG_TEXTBOX).append("\"");
                }
                sb.append(" value=\"").append(getFieldValue()).append("\" ");
                sb.append(readonlystr).append(js).append("/>\r\n");
            } else {
                int rows = (this.getSize() / 80) + 4;
                rows = rows > 20 ? 20 : rows;
                sb.append("<textarea ").append(readonlystr).append(" name=\"").append(getName()).append("\" cols=\"110\" width=\"400\" rows=\"");
                sb.append(rows).append("\" class=\"");
                sb.append(ActionField.CLASS_TEXTAREA);
                sb.append("\" ").append(readonlystr).append(">");
                sb.append(getFieldValue()).append("</textarea>");
            }
        }
        return sb.toString();
    }

    @Override
    public String isAvalid() {
        String value = this.getFieldValue();
        if ((value == null || value.equals("")) && getNullable() != 1) {
            return "字段" + this.getComment() + "不能为空!";
        }
        String res = super.isAvalid();
        return res;
    }
}
