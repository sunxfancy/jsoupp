package org.hpar;

/**
 *
 * Created by sxf on 4/30/16.
 */
public class tag {
    int pos;
    int size;
    int type;
    tag match;

    public tag(int begin, int end, int type) {
        this.pos = begin;
        this.size = end - begin;
        this.type = type;
    }

    public static final int script_begin = 1;
    public static final int script_end = 2;
    public static final int comment_begin = 3;
    public static final int comment_end = 4;
    public static final int other_begin = 5;
    public static final int other_end = 6;

    public static final int self_end = 250;
}