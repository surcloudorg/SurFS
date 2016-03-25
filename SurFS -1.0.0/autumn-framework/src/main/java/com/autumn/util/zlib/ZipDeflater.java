package com.autumn.util.zlib;

import com.autumn.core.security.Adler32;
import com.autumn.core.security.Crc32;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import static java.util.zip.ZipOutputStream.DEFLATED;

/**
 *
 * Title: zip压缩-仅支持单个文件
 *
 * Copyright: Autumn Copyright (c) 2011
 *
 * Company: Autumn
 *
 * @author 刘社朋
 * @version 2.0
 */
public class ZipDeflater extends Deflater {

    private static final int flag = 2056;       // general purpose flag
    private static final long time = 0;
    private static final int method = DEFLATED;

    Crc32 crc = null;
    String name = null;
    long size = -1;     // uncompressed size of entry data
    long csize = -1;    // compressed size of entry data
    long written = 0;

    ZipDeflater(Adler32 adler, Deflate def) {
        super(adler, def);
        out = new ByteArrayOutputStream();
    }

    public ZipDeflater()  {
        this(null);
    }

    public ZipDeflater(String name)  {
        super();
        out = new ByteArrayOutputStream();
        this.name = name == null ? "file" : name;
    }

    /**
     * Writes local file (LOC) header for specified entry.
     */
    private void writeLOC() throws IOException {
        writeInt(0x04034b50L);               // LOC header signature
        writeShort(20);     // version needed to extract
        writeShort(flag);           // general purpose bit flag
        writeShort(method);       // compression method
        writeInt(time);           // last modification time
        writeInt(0);
        writeInt(0);
        writeInt(0);
        byte[] nameBytes = name.getBytes("utf-8");
        writeShort(nameBytes.length);
        writeShort(0);
        writeBytes(nameBytes, 0, nameBytes.length);
    }

    private byte[] setHead() throws IOException {
        byte[] head = null;
        if (crc == null) {
            writeLOC();
            head = out.toByteArray();
            out.reset();
        }
        return head;
    }

    /**
     * 压缩
     *
     * @param b
     * @param off
     * @param len
     * @return　byte[]
     * @throws java.io.IOException
     */
    @Override
    public synchronized byte[] doDeflate(byte[] b, int off, int len) throws IOException {
        if (finished) {
            throw new IOException("finished");
        } else if (len == 0) {
            return new byte[0];
        } else if (off < 0 | len < 0 | off + len > b.length) {
            throw new IndexOutOfBoundsException();
        }
        byte[] bs = writeHead(super.doDeflate(b, off, len), setHead());
        crc.update(b, off, len);
        return bs;
    }

    @Override
    public synchronized byte[] doFinal() throws IOException {
        if (finished) {
            throw new IOException("finished");
        }
        byte[] defbs = writeHead(super.doFinal(), setHead());
        return writeEnd(defbs);
    }

    /**
     * 写末尾
     *
     * @param defbs
     * @return
     * @throws IOException
     */
    private byte[] writeEnd(byte[] defbs) throws IOException {
        written = written - 6;
        size = getTotalIn();
        csize = getTotalOut() - 6;
        writeEXT();
        long off1 = written;
        writeCEN();
        writeEND(off1, written - off1);
        byte[] endbs = out.toByteArray();
        out.reset();
        byte[] res = new byte[defbs.length - 4 + endbs.length];
        System.arraycopy(defbs, 0, res, 0, defbs.length - 4);
        System.arraycopy(endbs, 0, res, defbs.length - 4, endbs.length);
        return res;
    }

    /**
     * 压缩完毕！
     *
     * @param b
     * @param off
     * @param len
     * @return byte[]
     * @throws java.io.IOException
     */
    @Override
    public synchronized byte[] doFinal(byte[] b, int off, int len) throws IOException {
        if (finished) {
            throw new IOException("finished");
        } else if (off < 0 | len < 0 | off + len > b.length) {
            throw new IndexOutOfBoundsException();
        }
        byte[] defbs = writeHead(super.doFinal(b, off, len), setHead());
        crc.update(b, off, len);
        return writeEnd(defbs);
    }

    /**
     * 写ziphead
     *
     * @param defbs
     * @param head
     * @return byte[]
     */
    private byte[] writeHead(byte[] defbs, byte[] head) {
        written = written + defbs.length;
        if (crc == null) {
            crc = new Crc32();
            if (defbs.length < 2) {
                throw new IndexOutOfBoundsException();
            } else {
                if (head == null) {
                    byte[] res = new byte[defbs.length - 2];
                    System.arraycopy(defbs, 2, res, 0, res.length);
                    defbs = res;
                } else {
                    byte[] res = new byte[defbs.length - 2 + head.length];
                    System.arraycopy(head, 0, res, 0, head.length);
                    System.arraycopy(defbs, 2, res, head.length, defbs.length - 2);
                    defbs = res;
                }
            }
        }
        return defbs;
    }

    /**
     * Writes extra data descriptor (EXT) for specified entry.
     */
    private void writeEXT() throws IOException {
        writeInt(0x08074b50L);           // EXT header signature
        writeInt(crc.getValue());        // crc-32
        if (csize >= 0xFFFFFFFFL || size >= 0xFFFFFFFFL) {
            writeLong(csize);
            writeLong(size);
        } else {
            writeInt(csize);          // compressed size
            writeInt(size);           // uncompressed size
        }
    }

    /**
     * Writes end of central directory (END) header.
     */
    private void writeEND(long off, long len) throws IOException {
        boolean hasZip64 = false;
        long xlen = len;
        long xoff = off;
        if (xlen >= 0xFFFFFFFFL) {
            xlen = 0xFFFFFFFFL;
            hasZip64 = true;
        }
        if (xoff >= 0xFFFFFFFFL) {
            xoff = 0xFFFFFFFFL;
            hasZip64 = true;
        }
        if (hasZip64) {
            long off64 = written;
            writeInt(0x06064b50L);        // zip64 END record signature
            writeLong(56 - 12);  // size of zip64 end
            writeShort(45);                // version made by
            writeShort(45);                // version needed to extract
            writeInt(0);                   // number of this disk
            writeInt(0);                   // central directory start disk
            writeLong(1);    // number of directory entires on disk
            writeLong(1);    // number of directory entires
            writeLong(0);                // length of central directory
            writeLong(0);                // offset of central directory
            writeInt(0x07064b50L);        // zip64 END locator signature
            writeInt(0);                   // zip64 END start disk
            writeLong(off64);              // offset of zip64 END
            writeInt(1);                   // total number of disks (?)
        }
        writeInt(0x06054b50L);                 // END record signature
        writeShort(0);                    // number of this disk
        writeShort(0);                    // central directory start disk
        writeShort(1);                // number of directory entries on disk
        writeShort(1);                // total number of directory entries
        writeInt(xlen);                   // length of central directory
        writeInt(xoff);                   // offset of central directory
        writeShort(0);
    }

    /**
     * Write central directory (CEN) header for specified entry. REMIND: add
     * support for file attributes
     */
    private void writeCEN() throws IOException {
        int version = 20;
        long offset = 0;
        int e64len = 0;
        boolean hasZip64 = false;
        if (csize >= 0xFFFFFFFFL) {
            csize = 0xFFFFFFFFL;
            e64len += 8;              // csize(8)
            hasZip64 = true;
        }
        if (size >= 0xFFFFFFFFL) {
            size = 0xFFFFFFFFL;    // size(8)
            e64len += 8;
            hasZip64 = true;
        }
        writeInt(0x02014b50L);           // CEN header signature
        if (hasZip64) {
            writeShort(45);         // ver 4.5 for zip64
            writeShort(45);
        } else {
            writeShort(version);    // version made by
            writeShort(version);    // version needed to extract
        }
        writeShort(flag);           // general purpose bit flag
        writeShort(method);       // compression method
        writeInt(time);           // last modification time
        writeInt(crc.getValue());            // crc-32
        writeInt(csize);            // compressed size
        writeInt(size);             // uncompressed size
        byte[] nameBytes = name.getBytes("utf-8");
        writeShort(nameBytes.length);
        if (hasZip64) {
            writeShort(e64len + 4 + 0);
        } else {
            writeShort(0);
        }
        writeShort(0);
        writeShort(0);              // starting disk number
        writeShort(0);              // internal file attributes (unused)
        writeInt(0);                // external file attributes (unused)
        writeInt(offset);           // relative offset of local header
        writeBytes(nameBytes, 0, nameBytes.length);
        if (hasZip64) {
            writeShort(0x0001);// Zip64 extra
            writeShort(e64len);
            if (size == 0xFFFFFFFFL) {
                writeLong(size);
            }
            if (csize == 0xFFFFFFFFL) {
                writeLong(csize);
            }
        }
    }

    /**
     * Writes a 16-bit short to the output stream in little-endian byte order.
     */
    private void writeShort(int v) throws IOException {
        out.write((v) & 0xff);
        out.write((v >>> 8) & 0xff);
        written += 2;
    }

    /*
     * Writes a 32-bit int to the output stream in little-endian byte order.
     */
    private void writeInt(long v) throws IOException {
        out.write((int) ((v) & 0xff));
        out.write((int) ((v >>> 8) & 0xff));
        out.write((int) ((v >>> 16) & 0xff));
        out.write((int) ((v >>> 24) & 0xff));
        written += 4;
    }

    /*
     * Writes a 64-bit int to the output stream in little-endian byte order.
     */
    private void writeLong(long v) throws IOException {
        out.write((int) ((v) & 0xff));
        out.write((int) ((v >>> 8) & 0xff));
        out.write((int) ((v >>> 16) & 0xff));
        out.write((int) ((v >>> 24) & 0xff));
        out.write((int) ((v >>> 32) & 0xff));
        out.write((int) ((v >>> 40) & 0xff));
        out.write((int) ((v >>> 48) & 0xff));
        out.write((int) ((v >>> 56) & 0xff));
        written += 8;
    }

    /*
     * Writes an array of bytes to the output stream.
     */
    private void writeBytes(byte[] b, int off, int len) throws IOException {
        super.out.write(b, off, len);
        written += len;
    }
}
