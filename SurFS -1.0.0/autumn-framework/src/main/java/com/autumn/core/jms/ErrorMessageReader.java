/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.jms;

import com.autumn.core.ClassManager;
import com.autumn.core.ThreadPools;
import com.autumn.util.IOUtils;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * <p>Title: 从磁盘读出缓存</p>
 *
 * <p>Description: 只有读出的数据全部处理完毕，才更新指针</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class ErrorMessageReader<V> extends AbstractReader {

    private ErrorBuffer<V> smbuffer;
    private int threadnum = 5;
    private ThreadPools pool = null;

    @SuppressWarnings("unchecked")
    public ErrorMessageReader(ErrorBuffer<V> smbuffer) throws IOException {
        super(smbuffer);
        threadnum = smbuffer.getThreadnum();
        this.smbuffer = smbuffer;
        pool = new ThreadPools(threadnum);
    }

    /**
     * 处理数据，并发执行，等待结果，保存指针
     *
     * @param list
     */
    private void execute(List<MessageWrapper<V>> list) {
        if (list.isEmpty()) {
            return;
        }
        Map<Future, MessageWrapper<V>> futures = new HashMap<Future, MessageWrapper<V>>();
        for (MessageWrapper<V> message : list) {//并发执行
            ErrorMessageCaller<V> emc = new ErrorMessageCaller<V>(message, smbuffer);
            Future future = pool.submit(emc);
            futures.put(future, message);
        }
        Set<Future> set = futures.keySet();
        for (Future future : set) {//等待结果
            try {
                future.get();
            } catch (InterruptedException ex) {//中断
                MessageWrapper<V> msg = futures.get(future);
                msg.lastHT = System.currentTimeMillis();
                try {
                    smbuffer.writer.write(msg);
                    smbuffer.log.error("中断doMessage函数：" + msg.getMessage().toString(), ErrorMessageReader.class);
                } catch (IOException ex1) {//不可能发生，发生时为灾难故障
                    smbuffer.log.error("写入缓存失败,在中断doMessage函数["
                            + msg.getMessage().toString() + "]后:" + ex1.getMessage(), ErrorMessageReader.class);
                }
                Thread.currentThread().interrupt();
            } catch (ExecutionException ex) {//执行错误，不可能 
            }
        }
        saveAttributeValue("cfg.pos", Long.toString(position));//保存指针
        saveAttributes();
        list.clear();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        List<MessageWrapper<V>> list = new ArrayList<MessageWrapper<V>>();
        while (!this.isInterrupted()) {
            byte type;
            try {
                type = reader.readByte();
                long len = reader.readLong();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                for (int ii = 0; ii < len; ii++) {
                    os.write(reader.readByte());
                }
                byte[] bs = os.toByteArray();
                if (type == Buffer.EOF_TYPE) {
                    execute(list);
                    String line = new String(bs, "utf-8");
                    changReadFile(line);
                } else {
                    MessageWrapper<V> line = (MessageWrapper<V>) IOUtils.bytesToObject(bs, ClassManager.getClassLoader());
                    position = position + 9 + len;
                    list.add(line);
                    long step = System.currentTimeMillis() - line.lastHT;
                    if (step < 15000) {//小于15秒
                        sleep(15000 - step);
                    }
                    if (list.size() >= threadnum) {
                        execute(list);
                    }
                }
            } catch (InterruptedException ex) {
                break;
            } catch (Exception e) {
                if (e instanceof EOFException) {
                    execute(list);
                    try {
                        sleep(15000);
                    } catch (InterruptedException ex) {
                        break;
                    }
                } else { //致命错误退出,一般是cfg文件被破坏，磁盘满等灾难故障
                    smbuffer.log.trace("读取缓存数据出现致命错误!", e, ErrorMessageReader.class);
                    break;
                }
            }
        }
        execute(list);
        stopRead();//退出线程
        if (pool != null) {
            pool.shutdown();
        }
    }
}
