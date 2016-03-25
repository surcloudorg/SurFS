package com.autumn.core.jms;

import com.autumn.core.ClassManager;
import com.autumn.util.IOUtils;
import java.io.*;

/**
 * <p>Title: 从磁盘读出缓存</p>
 *
 * <p>Description: 从磁盘读出缓存-Buffer</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class BufReader<V> extends AbstractReader<V> {

    private Buffer<V> smbuffer = null;//实例

    /**
     * 由Buffer创建
     *
     * @param smbuffer
     * @throws IOException
     */
    protected BufReader(Buffer<V> smbuffer) throws IOException {
        super(smbuffer);
        this.smbuffer = smbuffer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        int times = 0;
        int minsize = (int) (smbuffer.getMemMaxLine() * 0.382);
        minsize = minsize < 1 ? 1 : minsize;
        while (!this.isInterrupted()) {
            byte type;
            try {
                if (smbuffer.size() > minsize) {
                    sleep(1000);
                    continue;
                }
                type = reader.readByte();
                long len = reader.readLong();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                for (int ii = 0; ii < len; ii++) {
                    os.write(reader.readByte());
                }
                byte[] bs = os.toByteArray();
                if (type == Buffer.EOF_TYPE) {
                    String line = new String(bs, "utf-8");
                    changReadFile(line);
                    times = 0;
                    continue;
                } else {
                    V line;
                    if (type == Buffer.STRING_TYPE) {
                        line = (V) new String(bs, "utf-8");
                    } else {
                        line = (V) IOUtils.bytesToObject(bs,ClassManager.getClassLoader());
                    }
                    if (line != null) {
                        smbuffer.put(line);
                    }
                    position = position + 9 + len;
                    saveAttributeValue("cfg.pos", Long.toString(position));
                }
                times++;
            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {
                if (e instanceof EOFException) {
                    try {
                        sleep(5000);
                    } catch (InterruptedException ex) {
                        break;
                    }
                } else { //致命错误退出,一般是cfg文件被破坏，磁盘满等灾难故障
                    smbuffer.log.trace("读取缓存数据出现致命错误!", e, BufReader.class);
                    break;
                }
            }
            if (times >= smbuffer.getMemMaxLine()) { //读30行存一次进度
                times = 0;
                saveAttributes();
            }
        }
        //退出线程
        stopRead();
        saveAttributes();
    }
}
