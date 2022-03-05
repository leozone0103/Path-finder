/*
 * Copyright (C) 2021 Kevin Zatloukal.  All rights reserved.  Permission is
 * hereby granted to students registered for University of Washington
 * CSE 331 for use solely during Spring Quarter 2021 for purposes of
 * the course.  No other use, copying, distribution, or modification
 * is permitted without prior written consent. Copyrights for
 * third-party components of this work must be honored.  Instructors
 * interested in reusing these course materials should contact the
 * author.
 */

/*
 * Copyright (C) 2021 Kevin Zatloukal.  All rights reserved.  Permission is
 * hereby granted to students registered for University of Washington
 * CSE 331 for use solely during Spring Quarter 2021 for purposes of
 * the course.  No other use, copying, distribution, or modification
 * is permitted without prior written consent. Copyrights for
 * third-party components of this work must be honored.  Instructors
 * interested in reusing these course materials should contact the
 * author.
 */

package marvel.scriptTestRunner;

//import graph.junitTests.DirectlyLabeledGraphTest;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import java.util.StringTokenizer;
import graph.DirectlyLabeledGraph;
import graph.Node;
import graph.Edge;
import marvel.MarvelPaths;

/**
 * This class implements a testing driver which reads test scripts
 * from files for testing Graph.
 **/
public class MarvelTestDriver {


    private final Map<String, DirectlyLabeledGraph<String, String>> graphs = new HashMap<String, DirectlyLabeledGraph<String, String>>();
    private final PrintWriter output;
    private final BufferedReader input;

    /**
     * @spec.requires r != null && w != null
     * @spec.effects Creates a new GraphTestDriver which reads command from
     * {@code r} and writes results to {@code w}
     **/
    // Leave this constructor public
    public MarvelTestDriver(Reader r, Writer w) {
        input = new BufferedReader(r);
        output = new PrintWriter(w);
    }


    /**
     * @throws IOException if the input or output sources encounter an IOException
     * @spec.effects Executes the commands read from the input and writes results to the output
     **/
    // Leave this method public
    public void runTests() throws IOException {
        String inputLine;
        while((inputLine = input.readLine()) != null) {
            if((inputLine.trim().length() == 0) ||
                    (inputLine.charAt(0) == '#')) {
                // echo blank and comment lines
                output.println(inputLine);
            } else {
                // separate the input line on white space
                StringTokenizer st = new StringTokenizer(inputLine);
                if(st.hasMoreTokens()) {
                    String command = st.nextToken();

                    List<String> arguments = new ArrayList<>();
                    while(st.hasMoreTokens()) {
                        arguments.add(st.nextToken());
                    }

                    executeCommand(command, arguments);
                }
            }
            output.flush();
        }
    }

    private void executeCommand(String command, List<String> arguments) {
        try {
            switch(command) {
                case "LoadGraph":
                    LoadGraph(arguments);
                    break;
                case "FindPath":
                    findPath(arguments);
                    break;
                case "CreateGraph":
                    createGraph(arguments);
                    break;
                case "AddNode":
                    addNode(arguments);
                    break;
                case "AddEdge":
                    addEdge(arguments);
                    break;
                case "ListNodes":
                    listNodes(arguments);
                    break;
                case "ListChildren":
                    listChildren(arguments);
                    break;
                default:
                    output.println("Unrecognized command: " + command);
                    break;
            }
        } catch(Exception e) {
            String formattedCommand = command;
            formattedCommand += arguments.stream().reduce("", (a, b) -> a + " " + b);
            output.println("Exception while running command: " + formattedCommand);
            e.printStackTrace(output);
        }
    }

    private void LoadGraph(List<String> arguments) throws CommandException{
        if (arguments.size() != 2) {
            throw new CommandException("Bad arguments to loadGraph: " + arguments);
        }

        String graphName = arguments.get(0);
        String filename = arguments.get(1);

        LoadGraph(graphName, filename);
    }

    private void LoadGraph(String graphName,String fileName) throws CommandException {
        //fileName = "resources/data/" + fileName;
        graphs.put(graphName, MarvelPaths.graphCreation(fileName));
        output.println("loaded graph " + graphName);
    }


    private void findPath(List<String> arguments) {
        if (arguments.size() != 3) {
            throw new CommandException("Bad arguments to findPath: " + arguments);
        }

        String graphName = arguments.get(0);
        String startName = arguments.get(1);
        String endName = arguments.get(2);

        findPath(graphName, startName, endName);
    }

    private void findPath(String graphName, String startName, String endName) {
        DirectlyLabeledGraph<String, String> g1 = graphs.get(graphName);
        HashMap<String, Node<String, String>> theMap = g1.getMap();
        if(!theMap.keySet().contains(startName) && !theMap.keySet().contains(endName)) {
            output.println("unknown: " + startName);
            output.println("unknown: " + endName);
        } else if(!theMap.containsKey(startName)) {
            output.println("unknown: " + startName);
        } else if(!theMap.containsKey(endName)) {
            output.println("unknown: " + endName);
        } else {
            ArrayList<Edge<String, String>> result = MarvelPaths.bfsSearch(g1, theMap.get(startName), theMap.get(endName));
            String res = "path from " + startName + " to " + endName + ":";
            String temp = startName;
            if(startName.equals(endName)) {
                res = res;
            } else if(result.isEmpty()) {
                res += "\n";
                res += "no path found";
            } else {
                for(Edge<String, String> e : result) {
                    res += "\n";
                    res += temp + " to " + e.getEnd().getValue() + " via " + e.getLabel();
                    temp = (String)e.getEnd().getValue();
                }
            }
            output.println(res);
        }
    }

    private void createGraph(List<String> arguments) {
        if(arguments.size() != 1) {
            throw new CommandException("Bad arguments to CreateGraph: " + arguments);
        }

        String graphName = arguments.get(0);
        createGraph(graphName);
    }

    private void createGraph(String graphName) {

        graphs.put(graphName, new DirectlyLabeledGraph<String, String>());
        output.println("created graph " + graphName);
    }

    private void addNode(List<String> arguments) {
        if(arguments.size() != 2) {
            throw new CommandException("Bad arguments to AddNode: " + arguments);
        }

        String graphName = arguments.get(0);
        String nodeName = arguments.get(1);

        addNode(graphName, nodeName);
    }

    private void addNode(String graphName, String nodeName) {

        DirectlyLabeledGraph<String, String> g1 = graphs.get(graphName);
        g1.addNode(new Node<String, String>(nodeName));
        output.println("added node " + nodeName + " to " + graphName);
    }

    private void addEdge(List<String> arguments) {
        if(arguments.size() != 4) {
            throw new CommandException("Bad arguments to AddEdge: " + arguments);
        }

        String graphName = arguments.get(0);
        String parentName = arguments.get(1);
        String childName = arguments.get(2);
        String edgeLabel = arguments.get(3);

        addEdge(graphName, parentName, childName, edgeLabel);
    }

    private void addEdge(String graphName, String parentName, String childName,
                         String edgeLabel) {


        DirectlyLabeledGraph<String, String> g2 = graphs.get(graphName);
        Node<String, String> parent = null, child = null;
        for(Node<String, String> n : g2.listNodes()) {
            if(n.getValue().equals(parentName)) {
                parent = n;
            }
            if(n.getValue().equals(childName)) {
                child = n;
            }
        }
        g2.addEdge(edgeLabel, parent, child);
        output.println("added edge "+ edgeLabel + " from "+ parentName +" to " + childName + " in " + graphName);
    }

    private void listNodes(List<String> arguments) {
        if(arguments.size() != 1) {
            throw new CommandException("Bad arguments to ListNodes: " + arguments);
        }

        String graphName = arguments.get(0);
        listNodes(graphName);
    }

    private void listNodes(String graphName) {
        DirectlyLabeledGraph<String, String> g3 = graphs.get(graphName);
        String res = graphName + " contains:";
        List<String> tempList = new ArrayList<>();
        for(Node<String, String> n : g3.listNodes()) {
            tempList.add((String)n.getValue());
        }
        java.util.Collections.sort(tempList);
        for(String s : tempList) {
            res += " "+s;
        }
        output.println(res);
    }

    private void listChildren(List<String> arguments) {
        if(arguments.size() != 2) {
            throw new CommandException("Bad arguments to ListChildren: " + arguments);
        }

        String graphName = arguments.get(0);
        String parentName = arguments.get(1);
        listChildren(graphName, parentName);
    }

    private void listChildren(String graphName, String parentName) {

        DirectlyLabeledGraph<String, String> g4 = graphs.get(graphName);
        String res = "the children of " + parentName +" in "+graphName + " are:";
        List<String> preRes = new ArrayList<>();
        Node<String, String> parent = null;
        for(Node<String, String> n : g4.listNodes()) {
            if(n.getValue().equals(parentName)) {
                parent = n;
            }
        }
        ArrayList<Edge<String, String>> theList = (ArrayList<Edge<String, String>>)parent.getChildren();
        if(parent != null) {
            for(Edge<String, String> e : theList) {
                preRes.add(e.getEnd().getValue() + "(" + e.getLabel() + ")");
            }
            java.util.Collections.sort(preRes);
            for(String s : preRes) {
                res += " "+s;
            }
        }

        output.println(res);
    }

    /**
     * This exception results when the input file cannot be parsed properly
     **/
    static class CommandException extends RuntimeException {

        public CommandException() {
            super();
        }

        public CommandException(String s) {
            super(s);
        }

        public static final long serialVersionUID = 3495;
    }
}

