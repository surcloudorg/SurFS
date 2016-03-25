package com.autumn.core.jms;

import com.autumn.core.ClassManager;
import com.autumn.core.security.Base64;
import com.autumn.util.IOUtils;
import java.io.IOException;
import java.io.Serializable;

/**
 * <p>Title: 远程磁盘缓存数据模型</p>
 *
 * <p>Description: 远程磁盘缓存数据模型，主要区分String/Object</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class Message implements Serializable {

    private static final long serialVersionUID = 20120720103955453L;
    private String message = null;
    private boolean text = true;

    public Message() {
    }

    public Message(Object obj) throws IOException {
        if (obj instanceof String) {
            message = obj.toString();
        } else {
            byte[] data = IOUtils.objectToBytes(obj);
            message = Base64.encode(data);
            text = false;
        }
    }

    /**
     * @return the message
     */
    public static Object getObject(Message message) throws ClassNotFoundException, IOException {
        if (message.isText()) {
            return message.getMessage();
        } else {
            byte[] bs = Base64.decode(message.getMessage());
            return IOUtils.bytesToObject(bs, ClassManager.getClassLoader());
        }
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the text
     */
    public boolean isText() {
        return text;
    }

    /**
     * @param textMessage the text to set
     */
    public void setText(boolean textMessage) {
        this.text = textMessage;
    }
}
