package com.autumn.core.log;

/**
 * <p>Title: 报警接口</p>
 *
 * <p>Description: 报警接口，用户可实现此接口完成告警定制</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public interface WarnImpl {

    public void execute(WarnCommand command);
}
