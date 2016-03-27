package org.hpar;

import org.jsoup.Jsoup;
import org.jsoup.helper.DescendableLinkedList;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import java.util.*;

public class ParallelParser {

    int numThreads;
    String input;
    Document[] docs;
    String[] inputs;
    DescendableLinkedList<Element> stack; // open emlement stack

    public ParallelParser(String input, int numThreads) {
        this.input = input;
        this.numThreads = numThreads;
        inputs = partition(input);
        // inputs = partitionByLine(input);
        docs = new Document[numThreads];
    }

    public Document parse() {

        Thread[] pparsers = new ParserThread[numThreads];

        long sta = System.nanoTime();
        for (int i = 0; i < numThreads; i++) {
            pparsers[i] = new ParserThread(i + "", inputs[i]);
            pparsers[i].start();
        }

        for (int i = 0; i < numThreads; i++) {
            try {
                pparsers[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long mid = System.nanoTime();
        Document doc = postprocess(docs);
        long end = System.nanoTime();

        System.out.println("thread parsing time: " + (mid - sta));
        System.out.println("postprocessing time: " + (end - mid));

        return doc;
    }

    String[] partition(String input) {
        String[] inputs = new String[numThreads];
        int length = input.length();
        int start = 0;
        int end = 0;
        int step = length / numThreads;
        for (int i = 0; i < numThreads; i++) {
            start = end;
            end = start + step;
            if (end >= length)
                end = length;
            else {
                // find a good partition point i.e. after '>'
                while (input.charAt(end) != '>') {
                    end++;
                }
                end++;
            }
            inputs[i] = input.substring(start, end);
            System.out.println("input[" + i + "]  from " + start + "~" + end);
        }
        for (int i = 1; i < numThreads; i++) {
            inputs[i] = "<body>" + inputs[i];
        }

        return inputs;
    }

    String[] partitionByLine(String input) {
        String[] inputs = new String[numThreads];
        StringTokenizer tokenizer = new StringTokenizer(input, "\n");
        for (int i = 0; i < numThreads; i++) {
            if (tokenizer.hasMoreTokens())
                inputs[i] = tokenizer.nextToken();
            else {
                System.out.println("no enough input lines");
                break;
            }
        }
        for (int i = 0; i < numThreads; i++)
            System.out.println("input[" + i + "]" + inputs[i]);
        return inputs;
    }

    // parser worker thread
    class ParserThread extends Thread {
        String input;

        ParserThread(String name, String input) {
            super(name);
            this.input = input;
        }

        public void run() {
            int threadID = Integer.parseInt(getName());
            docs[threadID] = Jsoup.parse(input);
        }
    }

    // merge trees to form single DOM tree
    Document postprocess(Document[] docs) {
        for (int i = 1; i < numThreads; i++) {
            merge(docs, i);
        }
        return docs[0];
    }

    // merge docs[i]'s body'children to docs[0]'s body
    void merge(Document[] docs, int index) {

        Element body0 = docs[0].body();
        Node rightMost = getRightMost(body0);

        List<Node> bodys = docs[index].childNodes();
        List<Node> children = null;
        // handle broken comments
        if (rightMost.nodeName().equals("#startComment")) {

            // if 2 body versions, version 0 is comment interpreting;
            // version 1 is normal interpreting, which is useless here
            if (bodys.size() == 2) {
                children = bodys.get(0).childNodes();
                // remove version 1
                bodys.get(0).remove();
                // merge EndComment and StartComment
                String comment = ((StartComment) rightMost).getData()
                        + ((EndComment) children.get(0)).getData();
                // remove EndComment from bodys.get(0)
                children.get(0).remove();
                // replace StartComment with Comment
                Node commentNode = new Comment(comment, rightMost.baseUri());
                Element parent = (Element) rightMost.parent();
                rightMost.remove();
                parent.appendChild(commentNode);
                rightMost = commentNode;
            }

            // the whole inputs[index] should be treated as comment
            else if (bodys.size() == 1) {
                ((StartComment) rightMost).setData(((StartComment) rightMost).getData()
                        + this.inputs[index]);
                return;
            }

            else
                System.out.println("unexpected case in merge.");
        }
        children = bodys.get(0).childNodes();

        // handle broken scripts
        if (rightMost.nodeName().equals("DataNode")) {

            rightMost = rightMost.parent();
            if (rightMost.nodeName().equals("script") && ((Element) rightMost).onlyStartTag == true) {
                if (children.get(1).nodeName().equals("script")
                        && ((Element) children.get(1)).onlyEndTag == true) {

                    if (rightMost.nodeName().equals("script") == false
                            || ((Element) rightMost).onlyStartTag == false)
                        System.out.println("err in merging");

                    String newData = ((DataNode) rightMost.childNode(0)).getWholeData()
                            + ((TextNode) children.get(0)).getWholeText();

                    ((DataNode) rightMost.childNode(0)).setWholeData(newData);
                    children.get(0).remove();
                } else {
                    // the whole inputs[index] is part of a script
                    String newData = ((DataNode) rightMost.childNode(0)).getWholeData()
                            + this.inputs[index].substring(6, this.inputs[index].length());
                    ((DataNode) rightMost.childNode(0)).setWholeData(newData);
                    return;
                }
            }
        }
        children = bodys.get(0).childNodes();

        // merge two trees
        Node current = rightMost;
        for (int i = 0; i < children.size(); i++) {
            // move to next start tag
            while (true) {
                if (current.nodeName().equals("Element") == false) {
                    current = current.parent();
                    continue;
                }

                if (((Element) current) == docs[0].body())
                    break;

                if (((Element) current).onlyStartTag == true)
                    break;
                current = current.parent();
            }

            // if match
            if (current.nodeName().equals(children.get(i).nodeName())
                    && ((Element) children.get(i)).onlyEndTag == true) {
                ((Element) current).onlyStartTag = false;
                continue;
            } else if (current.nodeName().equals("tbody") && i < children.size() - 1
                    && children.get(i+1).nodeName().equals("table")) {
                ((Element) current).onlyStartTag = false;
                continue;
            }

            ((Element) current).appendChild(children.get(i));
        }

    }

    Node getRightMost(Node root) {

        Node rightMost = root;
        if (rightMost.childNodes().size() == 0)
            return root;
        List<Node> children;
        do {
            children = rightMost.childNodes();
            rightMost = children.get(children.size() - 1);
        } while (children.size() != 0);

        return rightMost;
    }
}
