package com.autumn.util.zlib;

import static com.autumn.util.zlib.Constant.*;

/**
 *
 * Title: deflate压缩-核心算法
 *
 * Copyright: Autumn Copyright (c) 2011
 *
 * Company: Autumn
 *
 * @author 刘社朋
 * @version 2.0
 */
public final class Deflate {

    DefStream strm;        // pointer back to this zlib stream
    int status;           // as the name implies
    byte[] pending_buf;   // output still pending
    int pending_out;      // next pending byte to output to the stream
    int pending;          // nb of bytes in the pending buffer
    int wrap = 1;
    byte data_type;       // UNKNOWN, BINARY or ASCII
    int last_flush;       // value of flush param for previous deflate call
    byte[] window;
    short[] prev;
    short[] head; // Heads of the hash chains or NIL.
    int ins_h;          // hash index of string to be inserted
    int block_start;
    int match_length;           // length of best match
    int prev_match;             // previous match
    int match_available;        // set if previous match exists
    int strstart;               // start of string to insert
    int match_start;            // start of matching string
    int lookahead;              // number of valid bytes ahead in window
    int prev_length;
    short[] dyn_ltree;       // literal and length tree
    short[] dyn_dtree;       // distance tree
    short[] bl_tree;         // Huffman tree for bit lengths
    Tree l_desc = new Tree();  // desc for literal tree
    Tree d_desc = new Tree();  // desc for distance tree
    Tree bl_desc = new Tree(); // desc for bit length tree
    short[] bl_count = new short[Constant.MAX_WBITS + 1];
    short[] next_code = new short[Constant.MAX_WBITS + 1];
    int[] heap = new int[2 * Constant.L_CODES + 1];
    int heap_len;               // number of elements in the heap
    int heap_max;               // element of largest frequency
    byte[] depth = new byte[2 * Constant.L_CODES + 1];
    byte[] l_buf;               // index for literals or lengths */
    int last_lit;      // running index in l_buf
    int d_buf;         // index of pendig_buf
    int opt_len;        // bit length of current block with optimal trees
    int static_len;     // bit length of current block with static trees
    int matches;        // number of string matches in current block
    int last_eob_len;   // bit length of EOB code for last block
    short bi_buf;
    int bi_valid;

    int deflate(int flush) {
        int old_flush;
        if (flush > Constant.Z_FINISH || flush < 0) {
            return Constant.Z_STREAM_ERROR;
        }
        if (strm.next_out == null
                || (strm.next_in == null && strm.avail_in != 0)
                || (status == Constant.FINISH_STATE && flush != Constant.Z_FINISH)) {
            strm.msg = Constant.z_errmsg[Constant.Z_NEED_DICT - (Constant.Z_STREAM_ERROR)];
            return Constant.Z_STREAM_ERROR;
        }
        if (strm.avail_out == 0) {
            strm.msg = Constant.z_errmsg[Constant.Z_NEED_DICT - (Constant.Z_BUF_ERROR)];
            return Constant.Z_BUF_ERROR;
        }
        old_flush = last_flush;
        last_flush = flush;

        if (status == Constant.INIT_STATE) {
            int header = (Constant.Z_DEFLATED + ((Constant.w_bits - 8) << 4)) << 8;
            int level_flags = ((Constant.level - 1) & 0xff) >> 1;
            if (level_flags > 3) {
                level_flags = 3;
            }
            header |= (level_flags << 6);
            if (strstart != 0) {
                header |= Constant.PRESET_DICT;
            }
            header += 31 - (header % 31);
            status = Constant.BUSY_STATE;
            putShortMSB(header);
            if (strstart != 0) {
                long adler = strm.adler.getValue();
                putShortMSB((int) (adler >>> 16));
                putShortMSB((int) (adler & 0xffff));
            }
            strm.adler.reset();
        }
        if (pending != 0) {
            strm.flush_pending();
            if (strm.avail_out == 0) {
                last_flush = -1;
                return Constant.Z_OK;
            }
        } else if (strm.avail_in == 0 && flush <= old_flush && flush != Constant.Z_FINISH) {
            strm.msg = Constant.z_errmsg[Constant.Z_NEED_DICT - (Constant.Z_BUF_ERROR)];
            return Constant.Z_BUF_ERROR;
        }
        if (status == Constant.FINISH_STATE && strm.avail_in != 0) {
            strm.msg = Constant.z_errmsg[Constant.Z_NEED_DICT - (Constant.Z_BUF_ERROR)];
            return Constant.Z_BUF_ERROR;
        }
        if (strm.avail_in != 0 || lookahead != 0 || (flush != Constant.Z_NO_FLUSH && status != Constant.FINISH_STATE)) {
            int bstate = deflate_slow(flush);
            if (bstate == Constant.FinishStarted || bstate == Constant.FinishDone) {
                status = Constant.FINISH_STATE;
            }
            if (bstate == Constant.NeedMore || bstate == Constant.FinishStarted) {
                if (strm.avail_out == 0) {
                    last_flush = -1; // avoid BUF_ERROR next call, see above
                }
                return Constant.Z_OK;
            }
            if (bstate == Constant.BlockDone) {
                if (flush == Constant.Z_PARTIAL_FLUSH) {
                    _tr_align();
                } else { // FULL_FLUSH or SYNC_FLUSH
                    _tr_stored_block(0, 0, false);
                    if (flush == Constant.Z_FULL_FLUSH) {
                        for (int i = 0; i < Constant.hash_size/*-1*/; i++) // forget history
                        {
                            head[i] = 0;
                        }
                    }
                }
                strm.flush_pending();
                if (strm.avail_out == 0) {
                    last_flush = -1; // avoid BUF_ERROR at next call, see above
                    return Constant.Z_OK;
                }
            }
        }
        if (flush != Constant.Z_FINISH) {
            return Constant.Z_OK;
        }
        if (wrap <= 0) {
            return Constant.Z_STREAM_END;
        }
        long adler = strm.adler.getValue();
        putShortMSB((int) (adler >>> 16));
        putShortMSB((int) (adler & 0xffff));
        strm.flush_pending();
        if (wrap > 0) {
            wrap = -wrap; // write the trailer only once!
        }
        return pending != 0 ? Constant.Z_OK : Constant.Z_STREAM_END;
    }

    private void scan_tree(short[] tree, int max_code) {
        int n;                     // iterates over all tree elements
        int prevlen = -1;          // last emitted length
        int curlen;                // length of current code
        int nextlen = tree[0 * 2 + 1]; // length of next code
        int count = 0;             // repeat count of the current code
        int max_count = 7;         // max repeat count
        int min_count = 4;         // min repeat count
        if (nextlen == 0) {
            max_count = 138;
            min_count = 3;
        }
        tree[(max_code + 1) * 2 + 1] = (short) 0xffff; // guard
        for (n = 0; n <= max_code; n++) {
            curlen = nextlen;
            nextlen = tree[(n + 1) * 2 + 1];
            if (++count < max_count && curlen == nextlen) {
                continue;
            } else if (count < min_count) {
                bl_tree[curlen * 2] += count;
            } else if (curlen != 0) {
                if (curlen != prevlen) {
                    bl_tree[curlen * 2]++;
                }
                bl_tree[Constant.REP_3_6 * 2]++;
            } else if (count <= 10) {
                bl_tree[Constant.REPZ_3_10 * 2]++;
            } else {
                bl_tree[Constant.REPZ_11_138 * 2]++;
            }
            count = 0;
            prevlen = curlen;
            if (nextlen == 0) {
                max_count = 138;
                min_count = 3;
            } else if (curlen == nextlen) {
                max_count = 6;
                min_count = 3;
            } else {
                max_count = 7;
                min_count = 4;
            }
        }
    }

    private int build_bl_tree() {
        int max_blindex;  // index of last bit length code of non zero freq
        scan_tree(dyn_ltree, l_desc.max_code);
        scan_tree(dyn_dtree, d_desc.max_code);
        bl_desc.build_tree(this);
        for (max_blindex = Constant.BL_CODES - 1; max_blindex >= 3; max_blindex--) {
            if (bl_tree[Tree.bl_order[max_blindex] * 2 + 1] != 0) {
                break;
            }
        }
        opt_len += 3 * (max_blindex + 1) + 5 + 5 + 4;
        return max_blindex;
    }

    private void send_all_trees(int lcodes, int dcodes, int blcodes) {
        int rank;                    // index in bl_order
        send_bits(lcodes - 257, 5); // not +255 as stated in appnote.txt
        send_bits(dcodes - 1, 5);
        send_bits(blcodes - 4, 4); // not -3 as stated in appnote.txt
        for (rank = 0; rank < blcodes; rank++) {
            send_bits(bl_tree[Tree.bl_order[rank] * 2 + 1], 3);
        }
        send_tree(dyn_ltree, lcodes - 1); // literal tree
        send_tree(dyn_dtree, dcodes - 1); // distance tree
    }

    private void send_tree(short[] tree, int max_code) {
        int n;                     // iterates over all tree elements
        int prevlen = -1;          // last emitted length
        int curlen;                // length of current code
        int nextlen = tree[0 * 2 + 1]; // length of next code
        int count = 0;             // repeat count of the current code
        int max_count = 7;         // max repeat count
        int min_count = 4;         // min repeat count
        if (nextlen == 0) {
            max_count = 138;
            min_count = 3;
        }
        for (n = 0; n <= max_code; n++) {
            curlen = nextlen;
            nextlen = tree[(n + 1) * 2 + 1];
            if (++count < max_count && curlen == nextlen) {
                continue;
            } else if (count < min_count) {
                do {
                    send_code(curlen, bl_tree);
                } while (--count != 0);
            } else if (curlen != 0) {
                if (curlen != prevlen) {
                    send_code(curlen, bl_tree);
                    count--;
                }
                send_code(Constant.REP_3_6, bl_tree);
                send_bits(count - 3, 2);
            } else if (count <= 10) {
                send_code(Constant.REPZ_3_10, bl_tree);
                send_bits(count - 3, 3);
            } else {
                send_code(Constant.REPZ_11_138, bl_tree);
                send_bits(count - 11, 7);
            }
            count = 0;
            prevlen = curlen;
            if (nextlen == 0) {
                max_count = 138;
                min_count = 3;
            } else if (curlen == nextlen) {
                max_count = 6;
                min_count = 3;
            } else {
                max_count = 7;
                min_count = 4;
            }
        }
    }

    private void put_byte(byte[] p, int start, int len) {
        System.arraycopy(p, start, pending_buf, pending, len);
        pending += len;
    }

    private void put_byte(byte c) {
        pending_buf[pending++] = c;
    }

    private void put_short(int w) {
        put_byte((byte) (w/*&0xff*/));
        put_byte((byte) (w >>> 8));
    }

    private void putShortMSB(int b) {
        put_byte((byte) (b >> 8));
        put_byte((byte) (b/*&0xff*/));
    }

    private void send_code(int c, short[] tree) {
        int c2 = c * 2;
        send_bits((tree[c2] & 0xffff), (tree[c2 + 1] & 0xffff));
    }

    private void send_bits(int value, int length) {
        int len = length;
        if (bi_valid > (int) Constant.Buf_size - len) {
            int val = value;
            bi_buf |= ((val << bi_valid) & 0xffff);
            put_short(bi_buf);
            bi_buf = (short) (val >>> (Constant.Buf_size - bi_valid));
            bi_valid += len - Constant.Buf_size;
        } else {
            bi_buf |= (((value) << bi_valid) & 0xffff);
            bi_valid += len;
        }
    }

    private void _tr_align() {
        send_bits(Constant.STATIC_TREES << 1, 3);
        send_code(Constant.END_BLOCK, StaticTree.static_ltree);
        bi_flush();
        if (1 + last_eob_len + 10 - bi_valid < 9) {
            send_bits(Constant.STATIC_TREES << 1, 3);
            send_code(Constant.END_BLOCK, StaticTree.static_ltree);
            bi_flush();
        }
        last_eob_len = 7;
    }

    private boolean _tr_tally(int dist, int lc) {
        pending_buf[d_buf + last_lit * 2] = (byte) (dist >>> 8);
        pending_buf[d_buf + last_lit * 2 + 1] = (byte) dist;
        l_buf[last_lit] = (byte) lc;
        last_lit++;
        if (dist == 0) {
            dyn_ltree[lc * 2]++;
        } else {
            matches++;
            dist--;             // dist = match distance - 1
            dyn_ltree[(Tree._length_code[lc] + Constant.LITERALS + 1) * 2]++;
            dyn_dtree[Tree.d_code(dist) * 2]++;
        }
        if ((last_lit & 0x1fff) == 0 && Constant.level > 2) {
            int out_length = last_lit * 8;
            int in_length = strstart - block_start;
            int dcode;
            for (dcode = 0; dcode < Constant.D_CODES; dcode++) {
                out_length += (int) dyn_dtree[dcode * 2]
                        * (5L + Tree.extra_dbits[dcode]);
            }
            out_length >>>= 3;
            if ((matches < (last_lit / 2)) && out_length < in_length / 2) {
                return true;
            }
        }
        return (last_lit == Constant.lit_bufsize - 1);
    }

    private void compress_block(short[] ltree, short[] dtree) {
        int dist;      // distance of matched string
        int lc;         // match length or unmatched char (if dist == 0)
        int lx = 0;     // running index in l_buf
        int code;       // the code to send
        int extra;      // number of extra bits to send
        if (last_lit != 0) {
            do {
                dist = ((pending_buf[d_buf + lx * 2] << 8) & 0xff00) | (pending_buf[d_buf + lx * 2 + 1] & 0xff);
                lc = (l_buf[lx]) & 0xff;
                lx++;
                if (dist == 0) {
                    send_code(lc, ltree); // send a literal byte
                } else {
                    code = Tree._length_code[lc];
                    send_code(code + Constant.LITERALS + 1, ltree); // send the length code
                    extra = Tree.extra_lbits[code];
                    if (extra != 0) {
                        lc -= Tree.base_length[code];
                        send_bits(lc, extra);       // send the extra length bits
                    }
                    dist--; // dist is now the match distance - 1
                    code = Tree.d_code(dist);
                    send_code(code, dtree);       // send the distance code
                    extra = Tree.extra_dbits[code];
                    if (extra != 0) {
                        dist -= Tree.base_dist[code];
                        send_bits(dist, extra);   // send the extra distance bits
                    }
                } // literal or match pair ?              
            } while (lx < last_lit);
        }
        send_code(Constant.END_BLOCK, ltree);
        last_eob_len = ltree[Constant.END_BLOCK * 2 + 1];
    }

    private void set_data_type() {
        int n = 0;
        int ascii_freq = 0;
        int bin_freq = 0;
        while (n < 7) {
            bin_freq += dyn_ltree[n * 2];
            n++;
        }
        while (n < 128) {
            ascii_freq += dyn_ltree[n * 2];
            n++;
        }
        while (n < Constant.LITERALS) {
            bin_freq += dyn_ltree[n * 2];
            n++;
        }
        data_type = (byte) (bin_freq > (ascii_freq >>> 2) ? Constant.Z_BINARY : Constant.Z_ASCII);
    }

    private void bi_flush() {
        if (bi_valid == 16) {
            put_short(bi_buf);
            bi_buf = 0;
            bi_valid = 0;
        } else if (bi_valid >= 8) {
            put_byte((byte) bi_buf);
            bi_buf >>>= 8;
            bi_valid -= 8;
        }
    }

    private void bi_windup() {
        if (bi_valid > 8) {
            put_short(bi_buf);
        } else if (bi_valid > 0) {
            put_byte((byte) bi_buf);
        }
        bi_buf = 0;
        bi_valid = 0;
    }

    private void copy_block(int buf, int len, boolean header) {
        bi_windup();      // align on byte boundary
        last_eob_len = 8; // enough lookahead for inflate
        if (header) {
            put_short((short) len);
            put_short((short) ~len);
        }
        put_byte(window, buf, len);
    }

    private void flush_block_only(boolean eof) {
        _tr_flush_block(block_start >= 0 ? block_start : -1,
                strstart - block_start, eof);
        block_start = strstart;
        strm.flush_pending();
    }

    private void _tr_stored_block(int buf, int stored_len, boolean eof) {
        send_bits((Constant.STORED_BLOCK << 1) + (eof ? 1 : 0), 3);  // send block type
        copy_block(buf, stored_len, true);          // with header
    }

    private void _tr_flush_block(int buf, int stored_len, boolean eof) {
        int opt_lenb, static_lenb;// opt_len and static_len in bytes
        int max_blindex = 0;      // index of last bit length code of non zero freq
        if (Constant.level > 0) {
            if (data_type == Constant.Z_UNKNOWN) {
                set_data_type();
            }
            l_desc.build_tree(this);
            d_desc.build_tree(this);
            max_blindex = build_bl_tree();
            opt_lenb = (opt_len + 3 + 7) >>> 3;
            static_lenb = (static_len + 3 + 7) >>> 3;
            if (static_lenb <= opt_lenb) {
                opt_lenb = static_lenb;
            }
        } else {
            opt_lenb = static_lenb = stored_len + 5; // force a stored block
        }
        if (stored_len + 4 <= opt_lenb && buf != -1) {
            _tr_stored_block(buf, stored_len, eof);
        } else if (static_lenb == opt_lenb) {
            send_bits((Constant.STATIC_TREES << 1) + (eof ? 1 : 0), 3);
            compress_block(StaticTree.static_ltree, StaticTree.static_dtree);
        } else {
            send_bits((Constant.DYN_TREES << 1) + (eof ? 1 : 0), 3);
            send_all_trees(l_desc.max_code + 1, d_desc.max_code + 1, max_blindex + 1);
            compress_block(dyn_ltree, dyn_dtree);
        }
        ((Deflater) strm).init_block();
        if (eof) {
            bi_windup();
        }
    }

    private void fill_window() {
        int n, m;
        int p;
        int more;    // Amount of free space at the end of the window.
        do {
            more = (Constant.window_size - lookahead - strstart);
            if (more == 0 && strstart == 0 && lookahead == 0) {
                more = Constant.w_size;
            } else if (more == -1) {
                more--;
            } else if (strstart >= Constant.w_size + Constant.w_size - Constant.MIN_LOOKAHEAD) {
                System.arraycopy(window, Constant.w_size, window, 0, Constant.w_size);
                match_start -= Constant.w_size;
                strstart -= Constant.w_size; // we now have strstart >= MAX_DIST
                block_start -= Constant.w_size;
                n = Constant.hash_size;
                p = n;
                do {
                    m = (head[--p] & 0xffff);
                    head[p] = (m >= Constant.w_size ? (short) (m - Constant.w_size) : 0);
                } while (--n != 0);
                n = Constant.w_size;
                p = n;
                do {
                    m = (prev[--p] & 0xffff);
                    prev[p] = (m >= Constant.w_size ? (short) (m - Constant.w_size) : 0);
                } while (--n != 0);
                more += Constant.w_size;
            }
            if (strm.avail_in == 0) {
                return;
            }
            n = strm.read_buf(window, strstart + lookahead, more);
            lookahead += n;
            if (lookahead >= Constant.MIN_MATCH) {
                ins_h = window[strstart] & 0xff;
                ins_h = (((ins_h) << Constant.hash_shift) ^ (window[strstart + 1] & 0xff)) & Constant.hash_mask;
            }
        } while (lookahead < Constant.MIN_LOOKAHEAD && strm.avail_in != 0);
    }

    private int deflate_slow(int flush) {
        int hash_head = 0;    // head of hash chain
        boolean bflush;         // set if current block must be flushed
        while (true) {
            if (lookahead < MIN_LOOKAHEAD) {
                fill_window();
                if (lookahead < MIN_LOOKAHEAD && flush == Z_NO_FLUSH) {
                    return NeedMore;
                }
                if (lookahead == 0) {
                    break; // flush the current block
                }
            }
            if (lookahead >= MIN_MATCH) {
                ins_h = (((ins_h) << hash_shift) ^ (window[(strstart) + (MIN_MATCH - 1)] & 0xff)) & hash_mask;
                hash_head = (head[ins_h] & 0xffff);
                prev[strstart & w_mask] = head[ins_h];
                head[ins_h] = (short) strstart;
            }
            prev_length = match_length;
            prev_match = match_start;
            match_length = MIN_MATCH - 1;
            if (hash_head != 0 && prev_length < max_lazy_match && ((strstart - hash_head) & 0xffff) <= w_size - MIN_LOOKAHEAD) {
                if (strategy != Z_HUFFMAN_ONLY) {
                    match_length = longest_match(hash_head);
                }
                if (match_length <= 5 && (strategy == Z_FILTERED || (match_length == MIN_MATCH && strstart - match_start > 4096))) {
                    match_length = MIN_MATCH - 1;
                }
            }
            if (prev_length >= MIN_MATCH && match_length <= prev_length) {
                int max_insert = strstart + lookahead - MIN_MATCH;
                bflush = _tr_tally(strstart - 1 - prev_match, prev_length - MIN_MATCH);
                lookahead -= prev_length - 1;
                prev_length -= 2;
                do {
                    if (++strstart <= max_insert) {
                        ins_h = (((ins_h) << hash_shift) ^ (window[(strstart) + (MIN_MATCH - 1)] & 0xff)) & hash_mask;
                        hash_head = (head[ins_h] & 0xffff);
                        prev[strstart & w_mask] = head[ins_h];
                        head[ins_h] = (short) strstart;
                    }
                } while (--prev_length != 0);
                match_available = 0;
                match_length = MIN_MATCH - 1;
                strstart++;
                if (bflush) {
                    flush_block_only(false);
                    if (strm.avail_out == 0) {
                        return NeedMore;
                    }
                }
            } else if (match_available != 0) {
                bflush = _tr_tally(0, window[strstart - 1] & 0xff);
                if (bflush) {
                    flush_block_only(false);
                }
                strstart++;
                lookahead--;
                if (strm.avail_out == 0) {
                    return NeedMore;
                }
            } else {
                match_available = 1;
                strstart++;
                lookahead--;
            }
        }
        if (match_available != 0) {
            _tr_tally(0, window[strstart - 1] & 0xff);
            match_available = 0;
        }
        flush_block_only(flush == Z_FINISH);
        if (strm.avail_out == 0) {
            if (flush == Z_FINISH) {
                return FinishStarted;
            } else {
                return NeedMore;
            }
        }
        return flush == Z_FINISH ? FinishDone : BlockDone;
    }

    private int longest_match(int cur_match) {
        int chain_length = Constant.max_chain_length; // max hash chain length
        int scan = strstart;                 // current string
        int match;                           // matched string
        int len;                             // length of current match
        int best_len = prev_length;          // best match length so far
        int limit = strstart > (Constant.w_size - Constant.MIN_LOOKAHEAD)
                ? strstart - (Constant.w_size - Constant.MIN_LOOKAHEAD) : 0;
        int nice_match = Constant.nice_match;
        int wmask = Constant.w_mask;
        int strend = strstart + Constant.MAX_MATCH;
        byte scan_end1 = window[scan + best_len - 1];
        byte scan_end = window[scan + best_len];
        if (prev_length >= Constant.good_match) {
            chain_length >>= 2;
        }
        if (nice_match > lookahead) {
            nice_match = lookahead;
        }
        do {
            match = cur_match;
            if (window[match + best_len] != scan_end
                    || window[match + best_len - 1] != scan_end1
                    || window[match] != window[scan]
                    || window[++match] != window[scan + 1]) {
                continue;
            }
            scan += 2;
            match++;
            do {
            } while (window[++scan] == window[++match]
                    && window[++scan] == window[++match]
                    && window[++scan] == window[++match]
                    && window[++scan] == window[++match]
                    && window[++scan] == window[++match]
                    && window[++scan] == window[++match]
                    && window[++scan] == window[++match]
                    && window[++scan] == window[++match]
                    && scan < strend);
            len = Constant.MAX_MATCH - (int) (strend - scan);
            scan = strend - Constant.MAX_MATCH;
            if (len > best_len) {
                match_start = cur_match;
                best_len = len;
                if (len >= nice_match) {
                    break;
                }
                scan_end1 = window[scan + best_len - 1];
                scan_end = window[scan + best_len];
            }
        } while ((cur_match = (prev[cur_match & wmask] & 0xffff)) > limit && --chain_length != 0);
        if (best_len <= lookahead) {
            return best_len;
        }
        return lookahead;
    }
}
