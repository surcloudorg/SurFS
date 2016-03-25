package com.autumn.core.security;

public interface Checksum {

    public void update(byte[] buf, int index, int len);

    public void reset();

    public long getValue();

    public byte[] toBytes();
}
