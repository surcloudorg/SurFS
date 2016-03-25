package com.autumn.core.autopage;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: AUTOPAGE-表字段附加属性</p>
 *
 * <p>Description: 在query中设置的附加属性</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class QueryField extends ActionField {

    private String relation = "equals";//搜索方式，支持like,between

    public QueryField(ActionField actionfield) {
        super(actionfield);
        this.setItems(actionfield.getItems());
        this.setComment(actionfield.getComment());
        this.setFullName(actionfield.getFullName());
        this.setDefaultValue(actionfield.getDefaultValue());
        this.setDatefmt(actionfield.getDatefmt());
    }

    public QueryField newCopy() {
        QueryField q = new QueryField(this);
        q.setRelation(this.getRelation());
        return q;
    }

    /**
     * 生成在页面显示的代码 class统一使用textbox
     *
     * @return String
     */
    public String getHtmlText(HashMap<String, String> map) {
        int editType = getEditType();
        StringBuilder sb = new StringBuilder(getComment());
        if (this.getRelation().equals("between")) {
            if (editType == 3) {//组合框
                sb.append("<select name=\"").append(this.getName()).append("_begin");
                sb.append("\" class=\"").append(ActionField.CLASS_TEXTBOX).append("\">\r\n");
                sb.append("<option value=\"\" selected=\"selected\">全部</option>\r\n");
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    sb.append("<option value=\"").append(key).append("\"");
                    if (key.equalsIgnoreCase(this.getFieldValue())) {
                        sb.append(" selected=\"selected\"");
                    }
                    sb.append(">").append(value).append("</option>\r\n");
                }
                sb.append("</select>").append("-");
                sb.append("<select name=\"").append(this.getName()).append("_end");
                sb.append("\" class=\"").append(ActionField.CLASS_TEXTBOX).append("\">\r\n");
                sb.append("<option value=\"\" selected=\"selected\">全部</option>\r\n");
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    sb.append("<option value=\"").append(key).append("\"");
                    if (key.equalsIgnoreCase(this.getFieldValue2())) {
                        sb.append(" selected=\"selected\"");
                    }
                    sb.append(">").append(value).append("</option>\r\n");
                }
                sb.append("</select>");
            } else if (editType == 4) {//日期
                sb.append("<input name=\"").append(this.getName()).append("_begin");
                sb.append("\" type=\"text\" class=\"").append(ActionField.CLASS_TEXTBOX);
                sb.append("\" id=\"").append(this.getName());
                sb.append("\" onfocus=\"WdatePicker({dateFmt:'").append(this.getDatefmt()).append("'})\" value=\"");
                sb.append(this.getFieldValue()).append("\" readonly=\"readonly\" />-");
                sb.append("<input name=\"").append(this.getName()).append("_end");
                sb.append("\" type=\"text\" class=\"").append(ActionField.CLASS_TEXTBOX);
                sb.append("\" id=\"").append(this.getName());
                sb.append("\" onfocus=\"WdatePicker({dateFmt:'").append(this.getDatefmt()).append("'})\" value=\"");
                sb.append(this.getFieldValue2()).append("\" readonly=\"readonly\" />\r\n");
            } else if (editType == 1) {//数字
                String js = "OnKeyUp=\"this.value=this.value.replace(/[^\\d]/g,'')\" onpaste=\"this.value=this.value.replace(/[^\\d]/g,'')\"";
                sb.append("<input name=\"").append(getName()).append("_begin").append("\" class=\"");
                sb.append(ActionField.CLASS_TEXTBOX).append("\" ").append(js);
                sb.append(" value=\"").append(getFieldValue()).append("\"/>-");
                sb.append("<input name=\"").append(getName()).append("_end").append("\" class=\"");
                sb.append(ActionField.CLASS_TEXTBOX).append("\" ").append(js);
                sb.append(" value=\"").append(getFieldValue2()).append("\"/>\r\n");
            } else if (editType == 2) {//数字
                String js = "OnKeyUp=\"this.value=this.value.replace(/[^\\d.]/g,'')\" onpaste=\"this.value=this.value.replace(/[^\\d.]/g,'')\"";
                sb.append("<input name=\"").append(getName()).append("_begin").append("\" class=\"");
                sb.append(ActionField.CLASS_TEXTBOX).append("\" ").append(js);
                sb.append(" value=\"").append(getFieldValue()).append("\"/>-");
                sb.append("<input name=\"").append(getName()).append("_end").append("\" class=\"");
                sb.append(ActionField.CLASS_TEXTBOX).append("\" ").append(js);
                sb.append(" value=\"").append(getFieldValue2()).append("\"/>\r\n");
            } else {
                String js = "";
                if (this.getClassname().equalsIgnoreCase("java.lang.Boolean")) {//布尔
                    js = " OnKeyUp=\"this.value=this.value.replace(/[^01]/g,'')\" onpaste=\"this.value=this.value.replace(/[^01]/g,'')\"";
                }
                sb.append("<input name=\"").append(getName()).append("_begin").append("\" class=\"");
                sb.append(ActionField.CLASS_TEXTBOX).append("\"");
                sb.append(js);
                sb.append(" value=\"").append(getFieldValue()).append("\"/>-");
                sb.append("<input name=\"").append(getName()).append("_end").append("\" class=\"");
                sb.append(ActionField.CLASS_TEXTBOX).append("\"");
                sb.append(js);
                sb.append(" value=\"").append(getFieldValue2()).append("\"/>\r\n");
            }
        } else {
            if (editType == 3) {//组合框
                sb.append("<select name=\"").append(this.getName());
                sb.append("\" class=\"").append(ActionField.CLASS_TEXTBOX).append("\">\r\n");
                sb.append("<option value=\"\" selected=\"selected\">全部</option>\r\n");
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    sb.append("<option value=\"").append(key).append("\"");
                    if (key.equalsIgnoreCase(this.getFieldValue())) {
                        sb.append(" selected=\"selected\"");
                    }
                    sb.append(">").append(value).append("</option>\r\n");
                }
                sb.append("</select>");
            } else if (editType == 4) {//日期
                sb.append("<input name=\"").append(this.getName());
                sb.append("\" type=\"text\" class=\"").append(ActionField.CLASS_TEXTBOX);
                sb.append("\" id=\"").append(this.getName());
                sb.append("\" onfocus=\"WdatePicker({dateFmt:'").append(this.getDatefmt()).append("'})\" value=\"");
                sb.append(this.getFieldValue()).append("\" readonly=\"readonly\" />\r\n");
            } else if (editType == 1) {//数字
                String js = "OnKeyUp=\"this.value=this.value.replace(/[^\\d]/g,'')\" onpaste=\"this.value=this.value.replace(/[^\\d]/g,'')\"";
                sb.append("<input name=\"").append(getName()).append("\" class=\"");
                sb.append(ActionField.CLASS_TEXTBOX).append("\" ").append(js);
                sb.append(" value=\"").append(getFieldValue()).append("\"/>\r\n");
            } else if (editType == 2) {//数字
                String js = "OnKeyUp=\"this.value=this.value.replace(/[^\\d.]/g,'')\" onpaste=\"this.value=this.value.replace(/[^\\d.]/g,'')\"";
                sb.append("<input name=\"").append(getName()).append("\" class=\"");
                sb.append(ActionField.CLASS_TEXTBOX).append("\" ").append(js);
                sb.append(" value=\"").append(getFieldValue()).append("\"/>\r\n");
            } else {
                String js = "";
                if (this.getClassname().equalsIgnoreCase("java.lang.Boolean")) {//布尔
                    js = " OnKeyUp=\"this.value=this.value.replace(/[^01]/g,'')\" onpaste=\"this.value=this.value.replace(/[^01]/g,'')\"";
                }
                sb.append("<input name=\"").append(getName()).append("\" class=\"");
                sb.append(ActionField.CLASS_TEXTBOX).append("\"");
                sb.append(js);
                sb.append(" value=\"").append(getFieldValue()).append("\"/>\r\n");
            }
        }
        return sb.toString();
    }

    /**
     * @return the relation
     */
    public String getRelation() {
        return relation;
    }

    /**
     * @param relation the relation to set
     */
    public void setRelation(String relation) {
        if (relation == null) {
            this.relation = "equals";
            return;
        }
        if (relation.equalsIgnoreCase("like")) {
            this.relation = "like";
        } else if (relation.equalsIgnoreCase("between")) {
            this.relation = "between";
        } else {
            this.relation = "equals";
        }
    }
}
