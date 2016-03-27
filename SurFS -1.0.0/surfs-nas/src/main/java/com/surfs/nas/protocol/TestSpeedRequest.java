/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.protocol;

import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import com.surfs.nas.transport.LongResponse;
import com.surfs.nas.transport.OriginRequest;
import com.surfs.nas.transport.TcpCommandType;

public class TestSpeedRequest extends OriginRequest {

    private static final Logger log = LogFactory.getLogger(TestSpeedRequest.class);
    public static final long MAX_LENGTH = 1024l * 1024l * 1024l;
    public static final int MAX_TIME = 1000 * 10 * 10;

    public TestSpeedRequest() {
        super(TcpCommandType.SYS_SPEED_TEST);
    }

    public TestSpeedRequest(byte commandType, int sequence) {
        super(commandType, sequence);
    }

    @Override
    public void run() {
        LongResponse response = new LongResponse(this);
        TestSpeedSession action = (TestSpeedSession) this.getSession().getTestSpeedSession();
        if (action == null) {
            action = new TestSpeedSession();
            this.getSession().setTestSpeedSession(action);
            
        }
        action.byteCount = action.byteCount + data.length;
        if (action.byteCount >= MAX_LENGTH) {
            response.setValue(action.byteCount);
        } else {
            if (System.currentTimeMillis() - action.starttime >= MAX_TIME) {
                response.setValue(action.byteCount);
            } else {
                response.setValue(-1);
            }
        }
        if (response.getValue() > 0) {
            long time = System.currentTimeMillis() - action.starttime;
            int count = (int) ((float) action.byteCount / 1024f / 1024f);
            float speed = (float) count / ((float) time / 1000f);
            response.setValue((long) speed);
            
            this.getSession().setTestSpeedSession(null);
        }
        this.getSession().sendMessage(response);
    }

    public class TestSpeedSession {

        private final long starttime = System.currentTimeMillis();
        private long byteCount = 0;
    }

}
