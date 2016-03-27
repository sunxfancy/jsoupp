package org.hpar;


import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

import java.io.IOException;
import java.io.*;
/**
 * The App class
 *
 */
public class App
{
    public static void main(String[] args) {
        String data = "";
        try {
            data = readFile(args[1]);
        } catch(IOException e) {
            e.printStackTrace();
        }

        ParallelParser pp = new ParallelParser(data, 4);
        Document d = pp.parse();
    }

    public static String readFile(String fileName) throws IOException {
        StringBuffer buffer = new StringBuffer();
        InputStream is = new FileInputStream(fileName);
        String line; // 用来保存每行读取的内容
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine(); // 读取第一行
        while (line != null) { // 如果 line 为空说明读完了
            buffer.append(line); // 将读到的内容添加到 buffer 中
            buffer.append("\n"); // 添加换行符
            line = reader.readLine(); // 读取下一行
        }
        reader.close();
        is.close();
        return buffer.toString();
    }
}
