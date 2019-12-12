package org.graphwalker.core.algorithm;

import org.graphwalker.core.machine.Context;
import org.graphwalker.core.model.Element;

import java.util.*;

import static org.graphwalker.core.model.Edge.RuntimeEdge;
import static org.graphwalker.core.model.Vertex.RuntimeVertex;


public class BreadthFirstSearch implements Algorithm {

  private final Context context;

  List<Element> transitions = new ArrayList<>();
  List<Element> states = new ArrayList<>();

  public BreadthFirstSearch(Context context){ this.context = context; }

  public List<Element> getConnectedTransitions() {
    return transitions;
  }

  public List<Element> getConnectedVertices() {
    return states;
  }

  public void getConnectedComponents(Element root) {
    fillConnectedTransitionsStates(createElementStatusMap(context.getModel().getElements()), root);
  }

  private Map<Element, ElementStatus> createElementStatusMap(List<Element> elements) {
    Map<Element, ElementStatus> elementStatusMap = new HashMap<>();
    for (Element element : elements) {
      elementStatusMap.put(element, ElementStatus.UNVISITED);
    }
    return elementStatusMap;
  }

  private void fillConnectedTransitionsStates(Map<Element, ElementStatus> elementStatusMap, Element root) {

    List<Element> connectedComponent = new ArrayList<>();
    LinkedList<Element> q = new LinkedList<>();
    q.add(root);
    connectedComponent.add(root);
    states.add(root);
    while (!q.isEmpty()) {

      Element element = q.pop();

      if (ElementStatus.UNVISITED.equals(elementStatusMap.get(element))) {
        if(!connectedComponent.contains(element)) {
          connectedComponent.add(element);
          elementStatusMap.put(element, ElementStatus.VISITED);
        }

        if (element instanceof RuntimeVertex) {
          RuntimeVertex vertex = (RuntimeVertex) element;

          for (RuntimeEdge edge : context.getModel().getOutEdges(vertex)) {
            connectedComponent.add(edge);
            transitions.add(edge);
            q.add(edge);
          }

        } else if (element instanceof RuntimeEdge) {
          RuntimeEdge edge = (RuntimeEdge) element;

          if(!connectedComponent.contains(edge.getTargetVertex())) {
            connectedComponent.add(edge.getTargetVertex());
            q.add(edge.getTargetVertex());
            states.add(edge.getTargetVertex());
          }
        }
      }
    }
  }

  private enum ElementStatus {
    UNVISITED, VISITED
  }
}
