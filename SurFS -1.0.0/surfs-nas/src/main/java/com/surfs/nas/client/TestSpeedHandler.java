package com.surfs.nas.client;

import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import com.surfs.nas.ReusableThread;
import com.surfs.nas.protocol.TestSpeedRequest;
import com.surfs.nas.transport.LongResponse;
import java.io.IOException;
import java.util.Arrays;

public class TestSpeedHandler extends ReusableThread {

    private static final Logger log = LogFactory.getLogger(TestSpeedHandler.class);
    private Node node = null;
    private long speed = 0l;

    public TestSpeedHandler(Node node) {
        super(30000, TestSpeedHandler.class.getName());
        this.node = node;
    }

    @Override
    public void doTask() throws Exception {
        speed = 0l;
        testSpeed();
    }

    /**
     * 链路测试
     *
     * @throws IOException
     */
    private void testSpeed() throws IOException {
        byte[] buf = new byte[1024 * 1024];
        Arrays.fill(buf, (byte) 0xFF);
        try {
            while (true) {
                TestSpeedRequest tr = new TestSpeedRequest();
                tr.setData(buf);
                LongResponse response = (LongResponse) node.tcpclient.get(tr);
                long count = response.getValue();
                if (count > 0) {
                    speed = count;
                    node.setState(true);
                    break;
                }
            }
        } catch (Exception r) {
            if (node.tcpclient.isDestory()) {
                this.interrupt();
            } else {
                throw r instanceof IOException ? (IOException) r : new IOException(r);
            }
        }
    }

    /**
     * @return the speed
     */
    public long getSpeed() {
        return speed;
    }
}
