package org.hpar;

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
    static final String save_file = "nodeTree.dot";
    StringBuilder sb = new StringBuilder();
    Node node;

    public Visualization(Node node) {
        this.node = node;
    }

    public void ShowNodeTree() {
        header();
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
                if (d.rank > last_rank) sb.append("}\n{");
                sb.append('\"');
                sb.append(d.node.toString()); // 输出节点信息
                sb.append("\" ");
                if (d.node.childNodeSize() != 0) {
                    for (Node n : d.node.childNodes()) {
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

    void header() {
        sb.append("digraph G {");
    }

    void footer() {
        sb.append('}');

        try {
            PrintWriter writer = new PrintWriter(save_file, "UTF-8");
            writer.print(sb.toString());
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


}
