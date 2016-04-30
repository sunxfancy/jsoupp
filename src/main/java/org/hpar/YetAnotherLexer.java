package org.hpar;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 *
 * Created by sxf on 4/27/16.
 */
public class YetAnotherLexer {
    char[] data;
    int pos;
    int data_begin, data_end;

    List<tag> tags = new ArrayList<>();
    int[] list;
    int list_p = 0;

    public YetAnotherLexer(String data, int data_begin, int data_end) {
        this.data = data.toCharArray();
        this.data_begin = data_begin;
        this.data_end = data_end;
        list = new int[(data_end-data_begin)/10];
        reset();
    }

    public void reset() {
        pos = data_begin;
    }

    public boolean match() {
        Stack<tag> stack = new Stack<>();
        boolean ans = true;
        for (tag t: tags) {
            if ((t.type & 1) == 1) { stack.push(t); break; }
            else {
                if (stack.empty() || stack.peek().type != t.type - 1) {
                    ans = false;
                    continue;
                }
                tag s = stack.pop();
                s.match = t;
                t.match = s;
            }
        }
        return ans;
    }

    boolean isInRightPlace(int p) {
        for (tag t: tags) {
            if (t.match != null && (t.type==1 || t.type == 3)) {
                int b = t.pos;
                int e = t.match.pos+t.match.size;
                if (p >= b && p < e) return true;
            }
        }
        return false;
    }


    void skipSpace() {
        while (pos < data_end) {
            if (!Character.isWhitespace(data[pos]))
                return;
            ++pos;
        }
    }

    void skipQuote() {
        char quote = data[pos];
        ++pos;
        while (pos < data_end) {
            if (data[pos] == quote) {
                ++pos;
                return;
            }
            if (data[pos] == '\\')
                ++pos;
            ++pos;
        }
    }

    public void print() {
        for (tag t: tags) {
            System.out.println("Match: "+String.copyValueOf(data, t.pos, t.size));
            System.out.println("from: "+t.pos+" - "+(t.pos+t.size));
        }


    }

    public List<tag> find() {
        reset();
        boolean tag_open = false;
        boolean comment_open = false;

        while (pos < data_end) {
            switch (data[pos]) {
                case '<': {
                    if (comment_open) { ++pos; break; }
                    int first = pos++;
                    if (data[pos] == '/') {
                        pos++;
                        if (findScriptClose(first))
                            tag_open = false;
                        else
                            tags.add(new tag(first, pos, tag.other_end));
//                            list[list_p++] = pos;
                    } else if (data[pos] == '!' && pos+2 < data_end
                            && data[pos+1]=='-' && data[pos+2]=='-') {
                        pos += 3;
                        tags.add(new tag(first, pos, tag.comment_begin));
                        comment_open = true;
                    } else {
                        if (findScript(first))
                            tag_open = true;
                        else
                            tags.add(new tag(first, pos, tag.other_begin));
//                            list[list_p++] = first;
                    }
                } break;
                case '-': {
                    if (tag_open) { ++pos; break; }
                    if (pos+2 < data_end
                            && data[pos+1]=='-' && data[pos+2]=='>') {
                        tags.add(new tag(pos, pos+3, tag.comment_end));
                        pos += 2;
                        comment_open = false;
                    }
                    ++pos;
                } break;
                case '\'':
                case '\"': {
                    if (comment_open||!tag_open) { ++pos; break; }
                    skipQuote();
                } break;
                default:
                    ++pos;
            }
        }
        return tags;
    }

    static char[] script = "ript".toCharArray();
    static char[] style = "yle".toCharArray();



    boolean findScript(int begin) {
        boolean ans = true;
        skipSpace();
        if (!(pos < data_end && data[pos] == 's')) ans = false;
        else {
            ++pos;
            if (data[pos] == 'c') {
                ++pos;
                for (char c : script) {
                    if (pos < data_end && data[pos] == c) ++pos;
                    else ans = false;
                }
            } else if (data[pos] == 't') {
                ++pos;
                for (char c : style) {
                    if (pos < data_end && data[pos] == c) ++pos;
                    else ans = false;
                }
            } else ans = false;
        }
        while (pos < data_end && data[pos] != '>') ++pos;
        if (data[pos-1] == '/' || !ans) { ++pos; return false; }
        int end = ++pos;
        tags.add(new tag(begin, end, tag.script_begin));
        return true;
    }


    boolean findScriptClose(int begin) {
        boolean ans = true;
        skipSpace();
        if (!(pos < data_end && data[pos] == 's')) ans = false;
        else {
            ++pos;
            if (data[pos] == 'c') {
                ++pos;
                for (char c : script) {
                    if (pos < data_end && data[pos] == c) ++pos;
                    else ans = false;
                }
            } else if (data[pos] == 't') {
                ++pos;
                for (char c : style) {
                    if (pos < data_end && data[pos] == c) ++pos;
                    else ans = false;
                }
            } else ans = false;
        }
        skipSpace();
        if (pos < data_end && data[pos] == '>') {
            int end = ++pos;
            tags.add(new tag(begin, end, tag.script_end));
            return ans;
        }
        while (pos < data_end && data[pos] != '>') ++pos;
        ++pos;
        return false;
    }

}


