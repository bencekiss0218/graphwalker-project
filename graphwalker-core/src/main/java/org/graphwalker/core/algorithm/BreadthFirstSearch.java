package org.graphwalker.core.algorithm;

import org.graphwalker.core.machine.Context;
import org.graphwalker.core.model.Element;

import java.util.*;

import static org.graphwalker.core.common.Objects.unmodifiableList;
import static org.graphwalker.core.model.Edge.RuntimeEdge;
import static org.graphwalker.core.model.Vertex.RuntimeVertex;


public class BreadthFirstSearch implements Algorithm {

  private final Context context;

  public BreadthFirstSearch(Context context){ this.context = context; }

  public List<Element> getConnectedComponent(Element root) {
    return createConnectedComponent(createElementStatusMap(context.getModel().getElements()), root);
  }

  private Map<Element, ElementStatus> createElementStatusMap(List<Element> elements) {
    Map<Element, ElementStatus> elementStatusMap = new HashMap<>();
    for (Element element : elements) {
      elementStatusMap.put(element, ElementStatus.UNREACHABLE);
    }
    return elementStatusMap;
  }

  private List<Element> createConnectedComponent(Map<Element, ElementStatus> elementStatusMap, Element root) {

    List<Element> connectedComponent = new ArrayList<>();
    LinkedList<Element> stack = new LinkedList<>();
    stack.add(root);
    //connectedComponent.add(root);
    while (!stack.isEmpty()) {

      Element element = stack.poll();

      if (ElementStatus.UNREACHABLE.equals(elementStatusMap.get(element))) {
        if(!connectedComponent.contains(element))
          //connectedComponent.add(element);
        elementStatusMap.put(element, ElementStatus.REACHABLE);

        if (element instanceof RuntimeVertex) {
          RuntimeVertex vertex = (RuntimeVertex) element;

          for (RuntimeEdge edge : context.getModel().getOutEdges(vertex)) {
            connectedComponent.add(edge);
            stack.add(edge);
          }

        } else if (element instanceof RuntimeEdge) {
          RuntimeEdge edge = (RuntimeEdge) element;

          if(!connectedComponent.contains(edge.getTargetVertex()))
            //connectedComponent.add(edge.getTargetVertex());
          stack.add(edge.getTargetVertex());
        }
      }
    }
    return unmodifiableList(connectedComponent);
  }

  private enum ElementStatus {
    UNREACHABLE, REACHABLE
  }
}
