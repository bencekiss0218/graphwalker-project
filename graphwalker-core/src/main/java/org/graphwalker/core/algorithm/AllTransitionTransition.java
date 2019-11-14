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
    for( Element t : allTransitions) {
      getReachableTransitions((RuntimeEdge) t);
    }
    for (Element t : allTransitions){
      getTransitionPaths((RuntimeEdge) t, transitionTransitions);
    }

   // System.out.println(allTransitions.get(3));
    System.out.println("PATH: " + transitionPaths);
    return transitionTransitions;
  }



  private void getReachableTransitions(RuntimeEdge transition){
    Deque<RuntimeEdge> q = new ArrayDeque<>();
    List<Element> transitionTransitions = new ArrayList<Element>();
    Vertex.RuntimeVertex vertexTarget = transition.getTargetVertex();

    for (RuntimeEdge edge : context.getModel().getOutEdges(vertexTarget)) {
      if(!edge.equals(transition)) {
        q.add(edge);
        transitionTransitions.add(edge);
      }
    }
    while(!q.isEmpty()) {
      RuntimeEdge subTransition = q.pop();
      Vertex.RuntimeVertex subVertexTarget = subTransition.getTargetVertex();
        for (RuntimeEdge edge : context.getModel().getOutEdges(subVertexTarget)) {
          if(!transitionTransitions.contains(edge) && !edge.equals(transition)){
            q.add(edge);
            transitionTransitions.add(edge);
          }
        }
      }

    this.transitionTransitions.put(transition, transitionTransitions);
  }

  private void getTransitionPaths(RuntimeEdge transition, Map<Element,List<Element>> transitionTransitions){
    Deque<RuntimeEdge> q = new ArrayDeque<>();
    Path<Element> transitionPath = new Path<>();
    Map<Element, ElementStatus> transitionStatusMap = new HashMap<>();
    List<Element> unreachedTransitions = new ArrayList<>();
    unreachedTransitions = transitionTransitions.get(transition);
    List<Element> reachedTransitions = new ArrayList<>();
    transitionStatusMap = createElementStatusMap(allTransitions);
    RuntimeEdge nominatedEdge = transition;
    boolean loopEdge = false;

    Vertex.RuntimeVertex vertexSource = transition.getSourceVertex();
    Vertex.RuntimeVertex vertexTarget = transition.getTargetVertex();

    if(vertexSource.equals(vertexTarget)) {
      loopEdge = true;
      transitionStatusMap.put(transition,ElementStatus.REACHABLE);
    }

    transitionPath.add(vertexSource);
    transitionPath.add(transition);
    transitionPath.add(vertexTarget);
    transitionStatusMap.put(transition,ElementStatus.REACHABLE);


    for(RuntimeEdge edge : context.getModel().getOutEdges(vertexTarget)){
      //ha a tranzicio nem egyenlő magával a tranzicioval
      if(!edge.getTargetVertex().equals(vertexSource)) {
        q.add(edge);
        break;
      }

    }

    while(!q.isEmpty()){
      RuntimeEdge subTransition = q.pop();
      //System.out.println("POP : " + subTransition + "Unreached Transitions: " + unreachedTransitions);
      Vertex.RuntimeVertex subVertexTarget = subTransition.getTargetVertex();

      Vertex.RuntimeVertex target = subTransition.getTargetVertex();

      if(unreachedTransitions.size() == reachedTransitions.size()){
        transitionPaths.put(transition,transitionPath);
        return;
      }

       transitionPath.add(subTransition);
       transitionPath.add(target);
       transitionStatusMap.put(subTransition,ElementStatus.REACHABLE);
       reachedTransitions.add(subTransition);
       //unreachedTransitions.remove(subTransition);

      boolean unreachableEdge = false;
      List<RuntimeEdge> outedges = context.getModel().getOutEdges(subVertexTarget);
      int i = 0;
      while (!unreachableEdge && i<outedges.size()){
        if(transitionStatusMap.get(outedges.get(i)).equals(ElementStatus.UNREACHABLE)){
          unreachableEdge = true;
          q.add(outedges.get(i));
        }
        i++;
      }

      if(!unreachableEdge){
        for(RuntimeEdge edge : context.getModel().getOutEdges(subVertexTarget)) {
          Vertex.RuntimeVertex rVertexSource = edge.getSourceVertex();
          Vertex.RuntimeVertex rVertexTarget = edge.getTargetVertex();
          if(!rVertexSource.equals(rVertexTarget)){
            q.add(edge);
            break;
          }
        }
      }
    }


    System.out.println("Transition Path : " + transitionPath);
    transitionPaths.put(transition,transitionPath);
  }


  private List<Element> fillTransitionsWithBreadthFirstSearch(Element root){
    BreadthFirstSearch BFS = context.getAlgorithm(BreadthFirstSearch.class);
    BFS.getConnectedComponents(root);
    return BFS.getConnectedTransitions();
  }

  private Map<Element, ElementStatus> createElementStatusMap(List<Element> elements) {
    Map<Element, ElementStatus> elementStatusMap = new HashMap<>();
    for (Element element : elements) {
      elementStatusMap.put(element, ElementStatus.UNREACHABLE);
    }
    return elementStatusMap;
  }


  private enum ElementStatus {
    UNREACHABLE, REACHABLE
  }

}
