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

package pathfinder;

import graph.DirectlyLabeledGraph;
import graph.Edge;
import graph.Node;
import pathfinder.datastructures.Path;
import pathfinder.datastructures.Point;
import pathfinder.parser.CampusBuilding;
import pathfinder.parser.CampusPath;
import pathfinder.parser.CampusPathsParser;

import java.util.Map;
import java.util.*;

public class CampusMap implements ModelAPI {

    @Override
    public boolean shortNameExists(String shortName) {
        List<CampusBuilding> buildings = CampusPathsParser.parseCampusBuildings("campus_buildings.csv");
        for(CampusBuilding n : buildings) {
            if(n.getShortName().equals(shortName)) {return true;}
        }
        return false;
    }

    @Override
    public String longNameForShort(String shortName) {
        if(!shortNameExists(shortName)) {
            throw new IllegalArgumentException("shortname provided does not exist");
        }
        Map<String, String> map = buildingNames();
        return map.get(shortName);

    }

    @Override
    public Map<String, String> buildingNames() {
        List<CampusBuilding> buildings = CampusPathsParser.parseCampusBuildings("campus_buildings.csv");
        Map<String, String> res = new HashMap<>();
        for(CampusBuilding n : buildings) {
            res.put(n.getShortName(), n.getLongName());
        }
        return res;
    }

    @Override
    public Path<Point> findShortestPath(String startShortName, String endShortName) {
        if(startShortName == null || endShortName == null) {
            throw new IllegalArgumentException("Inputs cannot be null");
        }
        if ((!shortNameExists(startShortName)) || (!shortNameExists(endShortName))) {
            throw new IllegalArgumentException("Shortnames not found");
        }

        DirectlyLabeledGraph<Point, Double> map = new DirectlyLabeledGraph<>();
        HashMap<String, Point> nameMap = new HashMap<>();

        List<CampusBuilding> buildings = CampusPathsParser.parseCampusBuildings("campus_buildings.csv");

        for (CampusBuilding building: buildings) {

            // map the shortname of the building to its Point position
            Point position = new Point(building.getX(), building.getY());
            nameMap.put(building.getShortName(), position);

            map.addNode(new Node<Point, Double>(position));
        }

        List<CampusPath> pathList = CampusPathsParser.parseCampusPaths("campus_paths.csv");
        for (CampusPath campusPath: pathList) {
            Point start = null;
            Point end = null;
            for (Point p: map.getMap().keySet()) {
                if(campusPath.getX1() == p.getX() && campusPath.getY1() == p.getY()) {
                    start = p;
                }
                if(campusPath.getX2() == p.getX() && campusPath.getY2() == p.getY()) {
                    end = p;
                }
            }
            if (start == null) {
                start = new Point(campusPath.getX1(), campusPath.getY1());
                map.addNode(new Node<Point, Double>(start));
            }
            if (end == null) {
                end = new Point(campusPath.getX2(), campusPath.getY2());
                map.addNode(new Node<Point, Double>(end));
            }
            map.addEdge(campusPath.getDistance(), map.getMap().get(start), map.getMap().get(end));
        }
        Point startPoint = nameMap.get(startShortName);
        Point endPoint = nameMap.get(endShortName);
        Node<Point, Double> start = map.getMap().get(startPoint);
        Node<Point, Double> end = map.getMap().get(endPoint);
        return FindMinPath.findMinPath(map, start, end, map.getMap());

    }


}
