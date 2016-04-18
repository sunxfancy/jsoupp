package org.hpar;

/**
 * 多字符串匹配神器
 * Created by sxf on 4/18/16.
 */
public class ACautomation {
    private String data;
    private node root;

    public ACautomation(String data, int begin, int end) {
        this.data = data;
    }

    public void create(String... word) {

    }
}

class node {
    node fail;
    node[] next = new node[30]; // 0 空格和不可见; 1-26 英文字母a-z忽略大小写; 27 < ; 28 > ; 29 其他

    static int map(char c) {
        if (Character.isLowerCase(c)) return c-96;
        if (Character.isUpperCase(c)) return c-64;
        if (Character.isSpaceChar(c)) return 0;
        switch (c) {
            case '<': return 27;
            case '>': return 28;
            default:  return 29;
        }
    }
}
