package org.graphwalker.core.algorithm;


import org.graphwalker.core.machine.Context;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.model.Path;
import org.graphwalker.core.model.Vertex;

import static org.graphwalker.core.model.Edge.RuntimeEdge;

import java.util.*;

/**
 * @author Bence Kiss
 */
public class AllTransitionTransition {

  private final Context context;
  private Map<Element, Path<Element>> transitionPaths = new LinkedHashMap<Element,Path<Element>>();
  private Map<Element,List<Element>> transitionNeighbours =  new HashMap<Element,List<Element>>();
  private Map<Element,List<Element>> transitionTransitions = new HashMap<Element,List<Element>>();
  private List<Element> allTransitions = new ArrayList<>();

  public AllTransitionTransition(Context context) {
    this.context = context;
  }

  public Map<Element,List<Element>> returnTransitionTransitions(Element root){
    allTransitions = fillTransitionsWithBreadthFirstSearch(root);
    for( Element t : allTransitions){
      getTransitionTransitions((RuntimeEdge) t);
    }
    return transitionTransitions;
  }

  private void getTransitionTransitions(RuntimeEdge transition){
    Deque<RuntimeEdge> q = new ArrayDeque<>();
    List<Element> states = new ArrayList<Element>();
    List<Element> transitions = new ArrayList<Element>();
    Path<Element> transitionPath = new Path<>();

    Vertex.RuntimeVertex vertexSource = transition.getSourceVertex();
    Vertex.RuntimeVertex vertexTarget = transition.getTargetVertex();

    if(vertexSource.equals(vertexTarget))
      states.add(vertexSource);
    else{
      states.add(vertexSource);
      states.add(vertexTarget);
    }

    transitionPath.add(vertexSource);
    transitionPath.add(transition);
    transitionPath.add(vertexTarget);

    //get the transitionNeighbours for the given transition
    List<Element> neighbourTransitions = new ArrayList<Element>();
    for (RuntimeEdge edge : context.getModel().getOutEdges(vertexSource)) {
      if(!edge.equals(transition))
        neighbourTransitions.add(edge);
    }
    transitionNeighbours.put(transition, neighbourTransitions);

    for (RuntimeEdge edge : context.getModel().getOutEdges(vertexTarget)) {
      Vertex.RuntimeVertex vertex = edge.getTargetVertex();
      transitions.add(edge);
      if(!states.contains(vertex)) {
        q.add(edge);
      }
    }


    while(!q.isEmpty()){
      Edge.RuntimeEdge subTransition = q.pop();
      Vertex.RuntimeVertex subVertex = subTransition.getTargetVertex();
      if(!states.contains(subVertex)){
        transitionPath.add(subTransition);
        transitionPath.add(subVertex);
        states.add(subVertex);
      }
      for (Edge.RuntimeEdge edge : context.getModel().getOutEdges(subVertex)) {
        Vertex.RuntimeVertex vertex = edge.getTargetVertex();
        if(!transitions.contains(edge) && !transitions.contains(subTransition)){
          transitions.add(edge);
        }
        if(!states.contains(vertex)) {
          q.add(edge);
        }
      }
    }

    transitionPaths.put(transition,transitionPath);
    transitionTransitions.put(transition, transitions);
  }

  private List<Element> fillTransitionsWithBreadthFirstSearch(Element root){
    BreadthFirstSearch BFS = context.getAlgorithm(BreadthFirstSearch.class);
    BFS.getConnectedComponents(root);
    return BFS.getConnectedTransitions();
  }

}
