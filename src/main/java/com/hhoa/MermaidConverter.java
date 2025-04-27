package com.hhoa;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class MermaidConverter {
    private final List<Node> rootNodes = new ArrayList<>();
    private final String fileContents;
    private boolean addHeaders;

    public MermaidConverter(boolean addHeaders, String contents) {
        this.addHeaders = addHeaders;
        this.fileContents = contents;
    }

    public String convert() {
        parseMarkdown(this.fileContents);

        var mermaid = new StringBuilder();
        appendGraphHeader(mermaid);

        for (Node rootNode : rootNodes) {
            writeNodeAndLinks(mermaid, rootNode);
        }

        appendGraphEnd(mermaid);
        return mermaid.toString();
    }

    private void parseMarkdown(String markdown) {
        var lines = markdown.split("\n");

        // detect all indent‐lengths
        Pattern bullet = Pattern.compile("^(\\s*)([-*])\\s+.*$");
        var indentSet = new TreeSet<Integer>();
        for (String line : lines) {
            var m = bullet.matcher(line);
            if (m.find()) {
                indentSet.add(m.group(1).length());
            }
        }
        // build length -> depth map
        var sorted = new ArrayList<>(indentSet);  // e.g. [0,2,4]
        var depthMap = new HashMap<Integer,Integer>();
        for (int i = 0; i < sorted.size(); i++) {
            depthMap.put(sorted.get(i), i);
        }

        // 3) now do the real parse
        var nodeStack = new ArrayDeque<Node>();
        for (String line : lines) {
            var m = bullet.matcher(line);
            if (!m.find()) continue;

            String indent  = m.group(1);
            String content = m.group(0)
                .replaceFirst("^(\\s*[-*]\\s+)", "")
                .replaceAll("\\(([^)]+)\\):", " - $1")
                .trim();

            int depth = depthMap.get(indent.length());
            var node = new Node(depth);

            // pop back up to the correct parent
            while (!nodeStack.isEmpty() && nodeStack.peek().depth >= depth) {
                nodeStack.pop();
            }

            if (nodeStack.isEmpty()) {
                assignRootNodeId(node);
                rootNodes.add(node);
            } else {
                var parent = nodeStack.peek();
                parent.subNodes.add(node);
                assignChildNodeId(node, parent);
            }

            node.content = content;
            nodeStack.push(node);
        }
    }

    private void assignRootNodeId(Node node) {
        var id = (char) ('A' + rootNodes.size());
        node.id = String.valueOf(id);
        node.depth = 0;
    }

    private void assignChildNodeId(Node node, Node parent) {
        var index = parent.subNodes.size() - 1;
        var depth = parent.depth + 1;
        node.depth = depth;

        String suffix;
        if (depth % 2 == 1) {
            suffix = String.valueOf(index + 1);
        } else {
            suffix = Character.toString((char) ('a' + index));
        }
        node.id = parent.id + suffix;
    }

    private void writeNodeAndLinks(StringBuilder mermaid, Node node) {
        appendNode(mermaid, node);
        for (Node child : node.subNodes) {
            appendLink(mermaid, node.id, child.id);
            writeNodeAndLinks(mermaid, child);
        }
    }

    private void appendNode(StringBuilder mermaid, Node node) {
        mermaid.append("    ").append(node.id).append("[\"").append(node.content).append("\"]\n");
    }

    private void appendLink(StringBuilder mermaid, String parentId, String childId) {
        mermaid.append("    ").append(parentId).append(" --> ").append(childId).append("\n");
    }

    private void appendGraphHeader(StringBuilder mermaid) {
        if (this.addHeaders) {
            mermaid
                    .append("```mermaid\n");
        }
        mermaid
                .append("graph TD\n");
    }

    private void appendGraphEnd(StringBuilder mermaid) {
        if (!this.addHeaders) {
            return;
        }
        mermaid.append("```");
    }

    static class Node {
        String id;
        int depth;
        List<Node> subNodes = new ArrayList<>();
        String content;

        Node(int depth) {
            this.depth = depth;
        }
    }
}
