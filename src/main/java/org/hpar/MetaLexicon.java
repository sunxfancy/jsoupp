package org.hpar;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class tag {
    int pos;
    int size;
    int type;

    public tag(int begin, int end, int type) {
        this.pos = begin;
        this.size = end - begin;
        this.type = type;
    }

    public static final int script_begin = 1;
    public static final int script_end = 2;
    public static final int comment_begin = 3;
    public static final int comment_end = 4;
}


public class MetaLexicon {
    char[] data;
    int pos;
    int data_begin, data_end;

    List<tag> tags = new ArrayList<>();

    public MetaLexicon(String data, int data_begin, int data_end) {
        this.data = data.toCharArray();
        this.data_begin = data_begin;
        this.data_end = data_end;
        reset();
    }

    public void reset() {
        pos = data_begin;
    }

    public boolean match() {
        Stack<Integer> stack = new Stack<>();
        for (tag t: tags) {
            switch (t.type) {
                case 1:
                case 3: { stack.push(t.type); break; }
                case 2:
                case 4: {
                    if (stack.empty() || stack.peek() != t.type - 1) return false;
                    stack.pop();
                }
            }
        }
        return true;
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
                    } else if (data[pos] == '!' && pos+2 < data_end
                            && data[pos+1]=='-' && data[pos+2]=='-') {
                        pos += 3;
                        tags.add(new tag(first, pos, tag.comment_begin));
                        comment_open = true;
                    } else {
                        if (findScript(first))
                            tag_open = true;
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
        skipSpace();
        if (!(pos < data_end && data[pos] == 's')) return false;
        ++pos;
        if (data[pos] == 'c') {
            ++pos;
            for (char c: script) {
                if (pos < data_end && data[pos] == c) ++pos;
                else return false;
            }
        } else if (data[pos] == 't') {
            ++pos;
            for (char c: style) {
                if (pos < data_end && data[pos] == c) ++pos;
                else return false;
            }
        } else return false;
        while (pos < data_end && data[pos] != '>') ++pos;
        if (data[pos-1] == '/') return false;
        int end = ++pos;
        tags.add(new tag(begin, end, tag.script_begin));
        return true;
    }


    boolean findScriptClose(int begin) {
        skipSpace();
        if (!(pos < data_end && data[pos] == 's')) return false;
        ++pos;
        if (data[pos] == 'c') {
            ++pos;
            for (char c: script) {
                if (pos < data_end && data[pos] == c) ++pos;
                else return false;
            }
        } else if (data[pos] == 't') {
            ++pos;
            for (char c: style) {
                if (pos < data_end && data[pos] == c) ++pos;
                else return false;
            }
        } else return false;
        skipSpace();
        if (pos < data_end && data[pos] == '>') {
            int end = ++pos;
            tags.add(new tag(begin, end, tag.script_end));
            return true;
        }
        return false;
    }

}




/**
 *
 * Created by sxf on 4/19/16.
 */
class Lex {
    String data;
    int begin;
    int end;

    public Lex(String data, int begin, int end) {
        this.data = data;
        this.begin = begin;
        this.end = end;
        script = Pattern.compile("(<\\s*script[^>]*>)|(</\\s*script\\s*>)|(<!--)|(-->)| \'([^']|\\\\')\' | \"([^\"]|\\\\\")\"");
    }

    public void find() {

    }

    Pattern script;
    List<tag> tags = new ArrayList<>();

    public List<tag> find_tags() {
        Matcher m = script.matcher(data);
        m = m.region(begin, end);
        while (m.find()) {
            for (int i = 1; i <= 4; i++) {
                if (m.group(i)!=null && !m.group(i).isEmpty()) {
//                    System.out.println("Found: " + m.group(i));
//                    System.out.println("from: " + m.start(i) + " - " + m.end(i));
                    tags.add(new tag(m.start(i), m.end(i),i));
                }
            }
        }
        return tags;
    }


}