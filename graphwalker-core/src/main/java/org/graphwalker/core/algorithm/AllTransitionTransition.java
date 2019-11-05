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

  /*private void getTransitionTransitions(RuntimeEdge transition){
    Deque<RuntimeEdge> q = new ArrayDeque<>();
    List<Element> states = new ArrayList<Element>();
    List<Element> transitionTransitions = new ArrayList<Element>();
    Path<Element> transitionPath = new Path<>();
    Map<Element, ElementStatus> transitionStatusMap = new HashMap<>();
    List<Element> unreachedTransitions = new ArrayList<>();
    transitionStatusMap = createElementStatusMap(allTransitions);

    //transitionStatusMap.put(transition, ElementStatus.REACHABLE);

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
      Vertex.RuntimeVertex target = edge.getTargetVertex();
      Vertex.RuntimeVertex src = edge.getSourceVertex();
      if(target.equals(src) && AllTransitionTransition.ElementStatus.UNREACHABLE.equals(transitionStatusMap.get(edge))  ){
        if(edge.equals(transition)){
          transitionPath.add(edge);
          transitionPath.add(target);
          transitionStatusMap.put(edge, AllTransitionTransition.ElementStatus.REACHABLE);
        }else {
          transitionPath.add(edge);
          transitionPath.add(target);
          transitionTransitions.add(edge);
          transitionStatusMap.put(edge, AllTransitionTransition.ElementStatus.REACHABLE);
        }
      }

      if( transitionTransitions.size() != this.allTransitions.size() && AllTransitionTransition.ElementStatus.UNREACHABLE.equals(transitionStatusMap.get(edge))) {
        q.add(edge);
        unreachedTransitions.add(edge);
      }
    }

    while(!q.isEmpty()) {


      RuntimeEdge subTransition = q.pop();
      Vertex.RuntimeVertex subVertexSource = subTransition.getSourceVertex();
      Vertex.RuntimeVertex subVertexTarget = subTransition.getTargetVertex();

      System.out.println("pop: " + subTransition);
      if (transitionTransitions.size() != this.allTransitions.size() - 1) {

        boolean condition = (subVertexSource.equals(transitionPath.getLast()) && subVertexSource.equals(subVertexTarget));
        System.out.println( " SubvertexSource is : " + subVertexSource + " getLast: " + transitionPath.getLast() + "Condition is : " + condition + " tpath " + transitionPath + "Traansitions: " + transitionTransitions);
        if (((subVertexSource.equals(transitionPath.getLast()) && subVertexSource.equals(subVertexTarget)) || subVertexSource.equals(transitionPath.getLast())) && !subTransition.equals(transition)) {
          transitionStatusMap.put(subTransition, AllTransitionTransition.ElementStatus.REACHABLE);
          unreachedTransitions.remove(subTransition);
          transitionPath.add(subTransition);
          transitionPath.add(subVertexTarget);
          if(unreachedTransitions.isEmpty())
            transitionStatusMap.put(transition, ElementStatus.REACHABLE);

          if(!transitionTransitions.contains(subTransition))
            transitionTransitions.add(subTransition);

        }else if(!unreachedTransitions.isEmpty() && subTransition.equals(transition)){
          transitionPath.add(subTransition);
          transitionPath.add(subVertexTarget);
        }


        List<RuntimeEdge> outedges =context.getModel().getOutEdges(subVertexTarget);
        for (RuntimeEdge edge : outedges) {
          Vertex.RuntimeVertex target = edge.getTargetVertex();
          Vertex.RuntimeVertex src = edge.getSourceVertex();
          if(target.equals(src) && AllTransitionTransition.ElementStatus.UNREACHABLE.equals(transitionStatusMap.get(edge))) {
            transitionPath.add(edge);
            transitionPath.add(target);
            transitionTransitions.add(edge);
            transitionStatusMap.put(edge, AllTransitionTransition.ElementStatus.REACHABLE);
          }
          if(AllTransitionTransition.ElementStatus.UNREACHABLE.equals(transitionStatusMap.get(edge))) {
            if ((!transitionTransitions.contains(edge))
                 && transitionTransitions.size() != this.allTransitions.size() - 1) {
              //System.out.println("add to Q: " + edge);
              q.add(edge);
              transitionTransitions.add(edge);
            }
          }
        }
      }
    }

    System.out.println(transitionPath);
    transitionPaths.put(transition,transitionPath);
    this.transitionTransitions.put(transition, transitionTransitions);
  }*/

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
      System.out.println("POP : " + subTransition + "Unreached Transitions: " + unreachedTransitions);
      Vertex.RuntimeVertex subVertexTarget = subTransition.getTargetVertex();

      if(loopEdge){
        nominatedEdge = subTransition;
      }

      Vertex.RuntimeVertex source = subTransition.getSourceVertex();
      Vertex.RuntimeVertex target = subTransition.getTargetVertex();

      if(unreachedTransitions.isEmpty()){
        transitionPaths.put(transition,transitionPath);
        return;
      }


       transitionPath.add(subTransition);
       transitionPath.add(target);
       transitionStatusMap.put(subTransition,ElementStatus.REACHABLE);
       unreachedTransitions.remove(subTransition);


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
