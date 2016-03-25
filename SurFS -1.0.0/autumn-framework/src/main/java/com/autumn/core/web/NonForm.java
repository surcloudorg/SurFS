package com.autumn.core.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Title: WEB框架-action函数</p>
 *
 * <p>Description: 添加@ActionForm注释提供注入配置</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NonForm {
}
