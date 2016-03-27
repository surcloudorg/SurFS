/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas;

import com.surfs.nas.client.SurFile;
import java.io.IOException;
import java.util.List;
import java.util.Map;
 

public interface NasMetaAccessor {

    /**
     *
     * @param nasfile
     * @return int
     * @throws IOException
     */
    public int getNasFile(SurFile nasfile) throws IOException;

    /**
     * @param path
     * @return
     * @throws IOException
     */
    public long[] getDirectoryID(String path) throws IOException;

    /**
     *
     * @param nasFile
     * @return
     * @throws IOException
     */
    public int mkDirectory(SurFile nasFile) throws IOException;

    /**
     *
     * @param src
     * @param dst
     * @throws IOException
     */
    public void mvDirectory(SurFile src, SurFile dst) throws IOException;

    /**
     *
     * @param nasFile
     * @return NasMeta
     * @throws IOException
     */
    public int rmDirectory(SurFile nasFile) throws IOException;

    /**
     *
     * @param dirid
     * @throws IOException
     */
    public void updateDirectory(long dirid) throws IOException;

    /**
     *
     * @param dirid
     * @return long
     * @throws IOException
     */
    public long totalDirectory(long dirid) throws IOException;

    /**
     *
     * @param metadata
     * @throws IOException
     */
    public void deleteNasMeta(NasMeta metadata) throws IOException;

    /**
     *
     * @param path
     * @return
     * @throws IOException
     */
    public long getQuata(String path) throws IOException;

    /**
     *
     * @return List<>
     * @throws IOException
     */
    public List<String> getMountList() throws IOException;

    /**
     *
     * @param username
     * @return
     * @throws IOException
     */
    public UserAccount getUserAccount(String username) throws IOException;

    /**
     *
     * @param sharename
     * @return
     * @throws IOException
     */
    public Map<String, String> getPermission(String sharename) throws IOException;

    /**
     *
     * @param metadata
     * @throws IOException
     */
    public void storeNasMeta(NasMeta metadata) throws IOException;

    /**
     *
     * @param pid
     * @param fid
     * @return
     * @throws IOException
     */
    public NasMeta queryNasMeta(long pid, long fid) throws IOException;

    /**
     *
     * @param metadata
     * @param length
     * @throws IOException
     */
    public void updateNasMeta(NasMeta metadata, boolean length) throws IOException;

    /**
     *
     * @param src
     * @param dst
     * @throws IOException
     */
    public void mvNasMeta(SurFile src, SurFile dst) throws IOException;

    /**
     *
     * @param nasFile
     * @param top
     * @return NasFile[]
     * @throws IOException
     */
    public SurFile[] listNasMeta(SurFile nasFile, int top) throws IOException;

}
