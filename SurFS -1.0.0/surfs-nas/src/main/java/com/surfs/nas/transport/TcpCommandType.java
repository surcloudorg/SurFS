package com.surfs.nas.transport;

public class TcpCommandType {

    //error
    public static final byte ERROR = 0x00;

    //fileop command  0x10-0x4F
    public static final byte DELETE = 0x12;
    public static final byte WRITE = 0x14;
    public static final byte READ = 0x15;
    public static final byte TRUNC = 0x16;
    public static final byte QUOTA = 0x18;
    public static final byte CLOSE = 0x20;

    //system command  0x50-0xFF
    public static final byte SYS_SPEED_TEST = 0x50;
    public static final byte SYS_NODE_HANDLER = 0x51;
    public static final byte SYS_VOLUME_HANDLER = 0x52;
    public static final byte SYS_VOLUME_SET = 0x53;
    public static final byte SYS_NODE_SET = 0x54;
    public static final byte SYS_VOLUME_SPACE_GET = 0x55;
    public static final byte SYS_VOLUME_SCAN = 0x56;
    public static final byte SYS_VOLUME_SCAN_INIT = 0x66;
    public static final byte SYS_VOLUME_INIT = 0x67;
    public static final byte SYS_ACTIVE_TEST = 0x57;
    public static final byte SYS_VOLUME_LS = 0x58;

}
