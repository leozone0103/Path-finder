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

package pathfinder.scriptTestRunner;

import graph.DirectlyLabeledGraph;
import graph.Edge;
import graph.Node;
import marvel.MarvelPaths;
import pathfinder.CampusMap;
import pathfinder.FindMinPath;
import pathfinder.datastructures.Path;
import pathfinder.datastructures.Point;

import java.io.*;
import java.util.*;

/**
 * This class implements a test driver that uses a script file format
 * to test an implementation of Dijkstra's algorithm on a graph.
 */
public class PathfinderTestDriver {

    private final Map<String, DirectlyLabeledGraph<String, Double>> graphs = new HashMap<String, DirectlyLabeledGraph<String, Double>>();
    private final PrintWriter output;
    private final BufferedReader input;

    // Leave this constructor public
    public PathfinderTestDriver(Reader r, Writer w) {
        input = new BufferedReader(r);
        output = new PrintWriter(w);
    }

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
        DirectlyLabeledGraph<String, Double> g = graphs.get(graphName);
        Node<String, Double> start = null;
        Node<String, Double> end = null;
        for(Node<String, Double> n : g.listNodes()) {
            if(n.getValue().equals(startName)) {
                start = n;
            }
            if(n.getValue().equals(endName)) {
                end = n;
            }
        }
        HashMap<String, Node<String, Double>> theMap = g.getMap();
        if(!theMap.containsKey(startName) && !theMap.containsKey(endName)) {
            output.println("unknown: " + startName);
            output.println("unknown: " + endName);
        } else if(!theMap.containsKey(startName)) {
            output.println("unknown: " + startName);
        } else if(!theMap.containsKey(endName)) {
            output.println("unknown: " + endName);
        } else {
            Path<String> path = FindMinPath.findMinPath(g, start, end, g.getMap());
            output.println("path from " + startName + " to " + endName + ":");
            if (path != null) {
                Iterator<Path<String>.Segment> results = path.iterator();
                while (results.hasNext()) {
                    Path<String>.Segment s = results.next();
                    String startString = s.getStart();
                    String endString = s.getEnd();
                    double cost = s.getCost();
                    output.println( startString + " to " + endString + " with weight " + String.format("%.3f",cost));
                }
                output.println("total cost: " + String.format("%.3f", path.getCost()));
            }
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

        graphs.put(graphName, new DirectlyLabeledGraph<String, Double>());
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

        DirectlyLabeledGraph<String, Double> g1 = graphs.get(graphName);
        g1.addNode(new Node<String, Double>(nodeName));
        output.println("added node " + nodeName + " to " + graphName);
    }

    private void addEdge(List<String> arguments) {
        if(arguments.size() != 4) {
            throw new CommandException("Bad arguments to AddEdge: " + arguments);
        }

        String graphName = arguments.get(0);
        String parentName = arguments.get(1);
        String childName = arguments.get(2);
        double edgeLabel = Double.parseDouble(arguments.get(3));

        addEdge(graphName, parentName, childName, edgeLabel);
    }

    private void addEdge(String graphName, String parentName, String childName,
                         Double edgeLabel) {


        DirectlyLabeledGraph<String, Double> g2 = graphs.get(graphName);
        Node<String, Double> parent = null, child = null;
        for(Node<String, Double> n : g2.listNodes()) {
            if(n.getValue().equals(parentName)) {
                parent = n;
            }
            if(n.getValue().equals(childName)) {
                child = n;
            }
        }
        g2.addEdge(edgeLabel, parent, child);
        output.println("added edge "+ String.format("%.3f", edgeLabel) + " from "+ parentName +" to " + childName + " in " + graphName);
    }

    private void listNodes(List<String> arguments) {
        if(arguments.size() != 1) {
            throw new CommandException("Bad arguments to ListNodes: " + arguments);
        }

        String graphName = arguments.get(0);
        listNodes(graphName);
    }

    private void listNodes(String graphName) {
        DirectlyLabeledGraph<String, Double> g3 = graphs.get(graphName);
        String res = graphName + " contains:";
        List<String> tempList = new ArrayList<>();
        for(Node<String, Double> n : g3.listNodes()) {
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

        DirectlyLabeledGraph<String, Double> g4 = graphs.get(graphName);
        String res = "the children of " + parentName +" in "+graphName + " are:";
        List<String> preRes = new ArrayList<>();
        Node<String, Double> parent = null;
        for(Node<String, Double> n : g4.listNodes()) {
            if(n.getValue().equals(parentName)) {
                parent = n;
            }
        }
        ArrayList<Edge<String, Double>> theList = (ArrayList<Edge<String, Double>>)parent.getChildren();
        if(parent != null) {
            for(Edge<String, Double> e : theList) {
                preRes.add(e.getEnd().getValue() + "(" + String.format("%.3f", e.getLabel()) + ")");
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
