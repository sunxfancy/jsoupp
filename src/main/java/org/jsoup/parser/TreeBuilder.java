package org.jsoup.parser;

import org.jsoup.helper.Validate;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.ArrayList;
import java.util.Stack;

/**
 * @author Jonathan Hedley
 */
abstract class TreeBuilder {
    CharacterReader reader;
    Tokeniser tokeniser;
    protected Document doc; // current doc we are building into
    protected ArrayList<Element> stack; // the stack of open elements
    protected String baseUri; // current base uri, for creating new elements
    protected Token currentToken; // currentToken is used only for error tracking.
    protected ParseErrorList errors; // null when not tracking errors

    private Token.StartTag start = new Token.StartTag(); // start tag to process
    private Token.EndTag end  = new Token.EndTag();

    // zhijia add input
    protected String input;

    // zhijia added
    void reset() {
        doc.body().removeChildNodes();
        reader = new CharacterReader(input);
        this.errors = ParseErrorList.tracking(100);
        tokeniser = new Tokeniser(reader, errors);
        Element ebody = stack.get(0);
        stack.clear();
        stack.add(ebody);
    }

    protected void initialiseParse(String input, String baseUri, ParseErrorList errors) {
        Validate.notNull(input, "String input must not be null");
        Validate.notNull(baseUri, "BaseURI must not be null");

        doc = new Document(baseUri);
        reader = new CharacterReader(input);
        this.errors = errors;
        tokeniser = new Tokeniser(reader, errors);
        stack = new ArrayList<Element>(32);
        this.baseUri = baseUri;
    }

    Document parse(String input, String baseUri) {
        return parse(input, baseUri, ParseErrorList.noTracking());
    }

    Document parse(String input, String baseUri, ParseErrorList errors) {
        initialiseParse(input, baseUri, errors);
        runParser();
        return doc;
    }

    protected void runParser() {
        while (true) {
            Token token = tokeniser.read();
//            if(Thread.currentThread().getName().equals("1"))
//                System.out.println("type: " + token.tokenType() + " token: " + token);
            process(token);
            token.reset();

            if (token.type == Token.TokenType.EOF)
                break;
        }
        updateDoc();
    }


    // zhijia add to update doc tree according to open stack emlement
    void updateDoc() {
        Node root = (Node) doc;
        Stack<Node> shadow = new Stack<Node>();

        // get html Element
        Node rightMost = null;
        for (int i = 0; i < root.childNodesAsArray().length; i++) {
            if (root.childNode(i).nodeName().endsWith("html")) {
                rightMost = root.childNode(i);
                break;
            }
        }

        shadow.push(rightMost);
        // get body Element, considering body1
        assert rightMost != null;
        while (rightMost.childNodesAsArray().length > 0) {
            rightMost = rightMost.childNodesAsArray()[rightMost.childNodesAsArray().length - 1];
            shadow.push(rightMost);
        }
        shadow.push(rightMost);

        // pop out the elements those are supposed to be complete
        while (shadow.size() > stack.size())
            shadow.pop();


        while (shadow.size() > 0) {
            Node current = shadow.pop();
            // System.out.println("stack.peekLast().nodeName(): "+stack.peekLast().nodeName());
            // System.out.println("current.nodeName()         : "+current.nodeName());
            if (stack.get(stack.size()-1).nodeName().equals("body")
                    || stack.get(stack.size() - 1).nodeName().equals("head")
                    || current.nodeName().equals("body") || current.nodeName().equals("head"))
                break;

            // TODO: 也许需要再考虑一下用nodeName判断是否会因为用户的部分标签遗漏而导致匹配错误
            if (current.nodeName().equals(stack.get(stack.size()-1).nodeName())) {
                stack.remove(stack.size()-1);
                ((Element) current).onlyStartTag = true;
            } else {
                System.out.println(Thread.currentThread().getName()
                        + " err: stack doesn't match with shadow stack");
            }
        }
    }

    protected abstract boolean process(Token token);

    protected boolean processStartTag(String name) {
        if (currentToken == start) { // don't recycle an in-use token
            return process(new Token.StartTag().name(name));
        }
        return process(start.reset().name(name));
    }

    public boolean processStartTag(String name, Attributes attrs) {
        if (currentToken == start) { // don't recycle an in-use token
            return process(new Token.StartTag().nameAttr(name, attrs));
        }
        start.reset();
        start.nameAttr(name, attrs);
        return process(start);
    }

    protected boolean processEndTag(String name) {
        if (currentToken == end) { // don't recycle an in-use token
            return process(new Token.EndTag().name(name));
        }
        return process(end.reset().name(name));
    }


    protected Element currentElement() {
        int size = stack.size();
        return size > 0 ? stack.get(size-1) : null;
    }
}
