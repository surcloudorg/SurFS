package com.autumn.util.zlib;

/**
 *
 * Title: deflate压缩-参数
 *
 * Copyright: Autumn Copyright (c) 2011
 *
 * Company: Autumn
 *
 * @author 刘社朋
 * @version 2.0
 */
final public class Constant {

    static final public String[] z_errmsg = {
        "need dictionary", // Z_NEED_DICT       2
        "stream end", // Z_STREAM_END      1
        "", // Z_OK              0
        "file error", // Z_ERRNO         (-1)
        "stream error", // Z_STREAM_ERROR  (-2)
        "data error", // Z_DATA_ERROR    (-3)
        "insufficient memory", // Z_MEM_ERROR     (-4)
        "buffer error", // Z_BUF_ERROR     (-5)
        "incompatible version",// Z_VERSION_ERROR (-6)
        ""
    };
    static final public int Z_NO_FLUSH = 0;
    static final public int Z_SYNC_FLUSH = 2;
    static final public int Z_FINISH = 4;

    static final public int Z_OK = 0;
    static final public int Z_STREAM_END = 1;
    static final public int Z_BUF_ERROR = -5;
    
    static final public int Z_DEFAULT_COMPRESSION = 6;
    static final public int MAX_WBITS = 15;            // 32K LZ77 window
    static final public int DEF_MEM_LEVEL = 8;

    static final public int NeedMore = 0;
    static final public int BlockDone = 1;
    static final public int FinishStarted = 2;
    static final public int FinishDone = 3;
    static final public int PRESET_DICT = 0x20;
     
    static final public int Z_FILTERED = 1;
    static final public int Z_HUFFMAN_ONLY = 2;
    static final public int Z_DEFAULT_STRATEGY = 0;
    static final public int Z_PARTIAL_FLUSH = 1;
    static final public int Z_FULL_FLUSH = 3;
    static final public int Z_NEED_DICT = 2;
    static final public int Z_STREAM_ERROR = -2;
    static final public int Z_DATA_ERROR = -3;
    static final public int INIT_STATE = 42;
    static final public int BUSY_STATE = 113;
    static final public int FINISH_STATE = 666;
    static final public int Z_DEFLATED = 8;
    static final public int STORED_BLOCK = 0;
    static final public int STATIC_TREES = 1;
    static final public int DYN_TREES = 2;
    static final public int Z_BINARY = 0;
    static final public int Z_ASCII = 1;
    static final public int Z_UNKNOWN = 2;
    static final public int Buf_size = 8 * 2;
    static final public int REP_3_6 = 16;
    static final public int REPZ_3_10 = 17;
    static final public int REPZ_11_138 = 18;
    static final public int MIN_MATCH = 3;
    static final public int MAX_MATCH = 258;
    static final public int MIN_LOOKAHEAD = (MAX_MATCH + MIN_MATCH + 1);
    static final public int D_CODES = 30;
    static final public int BL_CODES = 19;
    static final public int LENGTH_CODES = 29;
    static final public int LITERALS = 256;
    static final public int L_CODES = (LITERALS + 1 + LENGTH_CODES);
    static final public int HEAP_SIZE = (2 * L_CODES + 1);
    static final public int END_BLOCK = 256;    
    
    static final public int lit_bufsize = 1 << (DEF_MEM_LEVEL + 6); // 16K elements by default
    static final public int pending_buf_size = lit_bufsize * 3;     // size of pending_buf
    static final public int w_bits = MAX_WBITS;                     // log2(w_size)  (8..16)
    static final public int w_size = 1 << w_bits;                   // LZ77 window size (32K by default)
    static final public int w_mask = w_size - 1;                    // w_size - 1
    static final public int window_size = 2 * w_size;
    static final public int hash_bits = DEF_MEM_LEVEL + 7;           // log2(hash_size)
    static final public int hash_size = 1 << hash_bits;      // number of elements in hash table
    static final public int hash_mask = hash_size - 1;      // hash_size-1
    static final public int hash_shift = ((hash_bits + MIN_MATCH - 1) / MIN_MATCH);
    static final public int level = Z_DEFAULT_COMPRESSION;    // compression level (1..9)
    static final public int strategy = Z_DEFAULT_STRATEGY;    // favor or force Huffman coding
    static final public int max_chain_length = Config.config_table[level].max_chain;
    static final public int max_lazy_match = Config.config_table[level].max_lazy;
    static final public int good_match = Config.config_table[level].good_length;
    static final public int nice_match = Config.config_table[level].nice_length;
    
}
