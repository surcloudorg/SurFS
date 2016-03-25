package com.surfs.nas.examples;

import com.autumn.util.TextUtils;
import com.surfs.nas.StorageSources;
import com.surfs.nas.client.NasRandomAccessor;
import com.surfs.nas.client.SurFile;
import com.surfs.nas.client.SurFileFactory;
import java.io.*;
import java.util.Date;

public class NasFileTest {

    public static void main(String[] args) throws Exception {
        try {
            //baseTest();
            //listTest();
            //move();
            writeTest();
            readTest();
            //deleteTest();
        } catch (Exception e) {
            
            e.printStackTrace();
            StorageSources.terminate();
        }
        System.in.read();
        StorageSources.terminate();
        com.autumn.core.ThreadPools.shutdownThreadpools();
    }
    static String wfilename = "E:\\Micros Windows XP\\XP_64bit.iso";
    static String dfilename = "d:\\XP_64bit.iso";

    /**
     * 基本接口测试
     */
    private static void baseTest() throws Exception {

        SurFile sf = SurFileFactory.newInstance("/testdir/testsubdir", "uspod1/uscluster1");


        SurFile file = SurFileFactory.newInstance("/XP_64bit.iso");

        boolean b = file.createNewFile();



        SurFile dir = SurFileFactory.newInstance("/surfs/sute");
        int res = dir.mkdir();


        dir.mkdirs();


        boolean exist = file.exists();

        int ii = file.delete();


        long size = file.length();



        long time = file.lastModified();
      


        boolean isfile = file.isFile();



        boolean isdir = file.isDirectory();



        boolean exist1 = file.exists();
    }

    /**
     * list
     */
    private static void listTest() throws Exception {
        SurFile dir = SurFileFactory.newInstance("/");
        //top 50000
        SurFile[] files = dir.listFiles();
        for (SurFile f : files) {

        }
    }

    /**
     * list
     */
    private static void move() throws Exception {
        SurFile src = SurFileFactory.newInstance("/testdir/testsubdir/testfile");
        SurFile dst = SurFileFactory.newInstance("/testdir/testfile1");

        src.renameTo(dst);


        SurFile srcdir = SurFileFactory.newInstance("/testdir/testsubdir");
        SurFile dstdir = SurFileFactory.newInstance("/testsubdir");

        srcdir.renameTo(dstdir);
    }


    private static void writeTest() throws Exception {
        long l = System.currentTimeMillis();
        File f = new File(wfilename);
        InputStream is = new FileInputStream(f);
        SurFile file = SurFileFactory.newInstance("isotest1");
        NasRandomAccessor os = new NasRandomAccessor(file);
        long count = 0;
        try {
            int len;
            byte[] buf = new byte[1024 * 64];
            while ((len = is.read(buf)) > 0) {
                os.write(buf, 0, len, count);
                count = count + len;
            }
        } catch (IOException e) {
            throw e;
        } finally {
            os.close();
            is.close();
        }
    }


    private static void readTest() throws Exception {
        long l = System.currentTimeMillis();
        SurFile file = SurFileFactory.newInstance("isotest1");
        NasRandomAccessor is = new NasRandomAccessor(file);
        OutputStream os = new FileOutputStream(dfilename);
        long count = 0;
        try {
            int len;
            byte[] buf = new byte[1024*64];
            while ((len = is.read(buf, 0, buf.length, count)) != -1) {
                os.write(buf, 0, len);
                count = count + len;
            }
        } catch (IOException e) {
            throw e;
        } finally {
            os.close();
            is.close();
        }
    }


    private static void deleteTest() throws Exception {
        SurFile file = SurFileFactory.newInstance("testdir/testsubdir/testfile");
        int res = file.delete();
    }
}
