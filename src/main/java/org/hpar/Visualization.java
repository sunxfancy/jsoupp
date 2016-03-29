package org.hpar;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Visualization for
 * Created by sxf on 3/28/16.
 */
public class Visualization {
    String save_file = "nodeTree.dot";
    StringBuilder sb = new StringBuilder();
    Node node;
    int num = 0;

    public Visualization(Node node, String save_file) {
        this.node = node;
        this.save_file = save_file;
    }

    public void ShowNodeTree() {
        header();
        drawEdge(node);
        findRank();
        footer();
    }

    class PairNode{
        public Node node;
        public int rank;

        public PairNode(Node node, int rank) {
            this.rank = rank;
            this.node = node;
        }
    }

    void findRank() {
        sb.append("rank = same\n{");
        LinkedBlockingQueue<PairNode> queue = new LinkedBlockingQueue<>();
        try {
            queue.put(new PairNode(node, 1));

            int last_rank = 1;
            while(queue.size() != 0) {
                PairNode d = queue.poll();
                if (d.rank > last_rank) {
                    sb.append("}\n{");
                    last_rank = d.rank;
                }
                sb.append('\"');
                sb.append(d.node.nodeName()).append(d.node.showNum); // 输出节点信息
                sb.append("\" ");
                if (d.node.childNodesAsArray().length != 0) {
                    for (Node n : d.node.childNodesAsArray()) {
                        // 父节点的rank+1为子节点的rank
                        queue.put(new PairNode(n, d.rank+1));
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sb.append("}\n");
    }

    void drawEdge(Node root) {
        num++;
        root.showNum = num;

        if (root instanceof Element) {
            Element e = (Element) root;
            if (e.onlyEndTag)
                sb.append("\"").append(root.nodeName()).append(root.showNum)
                  .append("\"[color=red style = filled]\n");
            else if (e.onlyStartTag)
                sb.append("\"").append(root.nodeName()).append(root.showNum)
                        .append("\"[color=green style = filled]\n");
        }

        if (root.childNodeSize() != 0)
        for (Node n : root.childNodesAsArray()) {
            drawEdge(n);
            sb.append("\"").append(root.nodeName()).append(root.showNum)
                    .append("\" -> \"").append(n.nodeName()).append(n.showNum)
                    .append("\"\n");
        }
    }

    void header() {
        sb.append("digraph G {");
    }

    void footer() {
        sb.append('}');
        try {
            PrintWriter writer = new PrintWriter(save_file, "UTF-8");
            writer.print(sb.toString());
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


}
