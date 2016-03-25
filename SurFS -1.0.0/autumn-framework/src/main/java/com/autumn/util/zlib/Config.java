package com.autumn.util.zlib;

/**
 *
 * Title: deflate压缩率参数设置
 *
 * Copyright: Autumn Copyright (c) 2011
 *
 * Company: Autumn
 *
 * @author 刘社朋
 * @version 2.0
 */
public class Config {

    static final public Config[] config_table;
    static final public int STORED = 0;
    static final public int FAST = 1;
    static final public int SLOW = 2;

    static {
        config_table = new Config[10];
        config_table[0] = new Config(0, 0, 0, 0, STORED);
        config_table[1] = new Config(4, 4, 8, 4, FAST);
        config_table[2] = new Config(4, 5, 16, 8, FAST);
        config_table[3] = new Config(4, 6, 32, 32, FAST);
        config_table[4] = new Config(4, 4, 16, 16, SLOW);
        config_table[5] = new Config(8, 16, 32, 32, SLOW);
        config_table[6] = new Config(8, 16, 128, 128, SLOW);
        config_table[7] = new Config(8, 32, 128, 256, SLOW);
        config_table[8] = new Config(32, 128, 258, 1024, SLOW);
        config_table[9] = new Config(32, 258, 258, 4096, SLOW);
    }

    int good_length; // reduce lazy search above this match length
    int max_lazy;    // do not perform lazy search above this match length
    int nice_length; // quit search above this match length
    int max_chain;
    int func;

    Config(int good_length, int max_lazy, int nice_length, int max_chain, int func) {
        this.good_length = good_length;
        this.max_lazy = max_lazy;
        this.nice_length = nice_length;
        this.max_chain = max_chain;
        this.func = func;
    }
}
