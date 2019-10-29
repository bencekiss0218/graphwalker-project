package org.graphwalker.core.algorithm;

import org.graphwalker.core.machine.Context;
import org.graphwalker.core.model.Element;

import java.util.*;

import static org.graphwalker.core.model.Edge.RuntimeEdge;
import static org.graphwalker.core.model.Vertex.RuntimeVertex;

/**
 * @author Bence Kiss
 */
public class AllTransitionState implements Algorithm {

  private final Context context;
  private Map<Element,List<Element>> transitionStates = new HashMap<Element,List<Element>>();
  List<Element> states = new ArrayList<Element>();
  private Map<Element,List<Element>> transitionPaths = new LinkedHashMap<Element,List<Element>>();
  private Map<Element,List<Element>> alterTransitionPaths = new LinkedHashMap<Element,List<Element>>();
  private Map<Element,List<Element>> transitionNeighbours =  new HashMap<Element,List<Element>>();
  private List<List<Element>> testSet = new ArrayList<>();



  public AllTransitionState(Context context) {
    this.context = context;
  }


  public Map<Element,List<Element>> getReachableStates(Element root){
    clearLists();
    allReachableStates(root, transitionPaths);
    return transitionStates;
  }


  private void clearLists() {
    this.transitionStates.clear();
    this.states.clear();
    this.transitionPaths.clear();
  }

  private void allReachableStates(Element root, Map<Element,List<Element>> transitionPaths){
    List<Element> allTransitionOfTheModel;
    List<Element> finalPath;
    allTransitionOfTheModel = fillTransitionsWithBreadthFirstSearch(root);

    System.out.println("transitions are: " + allTransitionOfTheModel);

    for(Element elem : allTransitionOfTheModel){
      getTransitionStates((RuntimeEdge) elem);
    }
    //System.out.println("Path for each transition BEFORE " + transitionPaths);
    getPathForTransitions(allTransitionOfTheModel);
    //System.out.println("Path for each transition AFTER " + transitionPaths);
    //System.out.println("Neighbours for each transition: " + transitionNeighbours);
    finalPath = getFinalPath(transitionPaths);
    System.out.println("FINAL PATH: " + finalPath);
    testSet = getTestSet(finalPath,alterTransitionPaths,transitionStates,states);
    System.out.println("TEST SET: " + testSet);


  }

  private void getTransitionStates(RuntimeEdge transition){
    Deque<RuntimeEdge> q = new ArrayDeque<>();
    List<Element> states = new ArrayList<Element>();
    List<Element> transitionPath = new ArrayList<Element>();
    RuntimeVertex vertexSource = transition.getSourceVertex();
    RuntimeVertex vertexTarget = transition.getTargetVertex();

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
      RuntimeVertex vertex = edge.getTargetVertex();
      if(!states.contains(vertex)) {
        q.add(edge);
      }
    }


    while(!q.isEmpty()){
      RuntimeEdge subTransition = q.pop();
      RuntimeVertex subVertex = subTransition.getTargetVertex();
      if(!states.contains(subVertex)){
        transitionPath.add(subTransition);
        transitionPath.add(subVertex);
        states.add(subVertex);
      }
      for (RuntimeEdge edge : context.getModel().getOutEdges(subVertex)) {
        RuntimeVertex vertex = edge.getTargetVertex();
        if(!states.contains(vertex)) {
          q.add(edge);
        }
      }
    }

    transitionPaths.put(transition,transitionPath);
    transitionStates.put(transition, states);

  }

  private void removeDuplicatePaths(Map<Element,List<Element>> map) {
    Iterator<Map.Entry<Element,List<Element>>> iterator = map.entrySet().iterator();
    HashSet<List<Element>> valueSet = new HashSet<>();
    while (iterator.hasNext()) {
      Map.Entry<Element,List<Element>> next = iterator.next();
      if (!valueSet.add(next.getValue())) {
        iterator.remove();
      }
    }
  }

  private List<Element> getFinalPath(Map<Element,List<Element>> transitionPaths){

    System.out.println("TransitionPaths beginning: " + transitionPaths);
    List<Element> finalPath = new ArrayList<>();
    Map<Element,List<Element>> copyOfTransitionPaths = transitionPaths;
    removeDuplicatePaths(copyOfTransitionPaths);
    Map.Entry<Element,List<Element>> entry = copyOfTransitionPaths.entrySet().iterator().next();
    Element start;
    Element end;
    List<Element> longestTransition = entry.getValue();
    int max = 0;
    int i = 0;

    Element keyTransition = entry.getKey();
    finalPath.addAll(copyOfTransitionPaths.get(keyTransition));

    int size = copyOfTransitionPaths.keySet().size();

    System.out.println("Keyset size: " + copyOfTransitionPaths.keySet().size() + " Keyset: " + copyOfTransitionPaths.keySet());
    while(i < size){
      System.out.println("ITERATION: " + i);
      end = copyOfTransitionPaths.get(keyTransition).get(copyOfTransitionPaths.get(keyTransition).size() - 1);

      if(copyOfTransitionPaths.get(keyTransition).size() > max){
        max = copyOfTransitionPaths.get(keyTransition).size();
        longestTransition = copyOfTransitionPaths.get(keyTransition);
      }

      for(Element j : copyOfTransitionPaths.keySet()){
        if(keyTransition.equals(j))
          continue;

        start = copyOfTransitionPaths.get(j).get(0);
        System.out.println("Loop number: " + i + " - Keytransition - " + keyTransition +  " - NextPath - " + j + " - End - " + end + " - Start - " + start);
        if(end.equals(start)){
          finalPath.addAll(copyOfTransitionPaths.get(j).subList(1,copyOfTransitionPaths.get(j).size()));
          copyOfTransitionPaths.remove(keyTransition);
          keyTransition = j;
          break;
        }
      }
      i++;
    }

    if(longestTransition.size() > finalPath.size()){
      finalPath = longestTransition;
    }

    return finalPath;
  }

  private List<List<Element>> getTestSet(List<Element> finalPath, Map<Element,List<Element>> alterTransitionPaths, Map<Element,List<Element>> transitionStates, List<Element> states){
    List<List<Element>> testSet = new ArrayList<>();
    List<Element> alterTransitionPath;
    boolean alterPath = false;

    testSet.add(finalPath);

    for(Element key : alterTransitionPaths.keySet()){

      if(equalsIgnore(transitionStates.get(key),states) && !alterPath) {
        int contains = Collections.indexOfSubList(finalPath,alterTransitionPaths.get(key));

        if( contains == -1 ) {
          testSet.add(alterTransitionPaths.get(key));
        }
      }
    }

    /*if(testSet.size() == 1){
      alterTransitionPath = getFinalPath(alterTransitionPaths);
      testSet.add(alterTransitionPath);
    }*/

    return testSet;
  }

  private void getPathForTransitions(List<Element> allTransitionOfTheModel) {
    List<Element> alternativePath;
    List<Element> alternativeStates;

    for(Element edge : allTransitionOfTheModel) {
      alternativePath = new ArrayList<>();
      alternativeStates = new ArrayList<>();
      getAlternativeTransitionStates((RuntimeEdge) edge, alternativePath, alternativeStates);

      if (equalsIgnore(transitionStates.get(edge),alternativeStates)) {
        alterTransitionPaths.put(edge, transitionPaths.get(edge));
        transitionPaths.replace(edge, alternativePath);
      }
    }
  }

  private boolean equalsIgnore(List<Element> a, List<Element> b) {
    boolean same = true;
    for(int i = 0; i<a.size() && same;i++){
      for(int j = 0; j<b.size();j++){
        if(a.get(i).equals(b.get(j))){
          break;
        }else if(j+1 == b.size()){
          same = false;
        }
      }
    }
    return same;
  }


  private List<Element> fillTransitionsWithBreadthFirstSearch(Element root){
    BreadthFirstSearch BFS = context.getAlgorithm(BreadthFirstSearch.class);
    return BFS.getConnectedComponent(root);
  }

  private void getAlternativeTransitionStates(RuntimeEdge transition, List<Element> alternativePath, List<Element> alternativeStates){
    Deque<RuntimeEdge> q = new ArrayDeque<>();
    RuntimeEdge neighbourTransition;
    for (Element edge : transitionNeighbours.get(transition)) {
      RuntimeEdge rEdge = (RuntimeEdge) edge;
      q.add(rEdge);
    }

    if(!q.isEmpty()) {
      neighbourTransition = q.pop();
      RuntimeVertex vertexSource = neighbourTransition.getSourceVertex();
      RuntimeVertex vertexTarget = neighbourTransition.getTargetVertex();

      if (vertexSource.equals(vertexTarget))
        alternativeStates.add(vertexSource);
      else {
        alternativeStates.add(vertexSource);
        alternativeStates.add(vertexTarget);

      }
      alternativePath.add(vertexSource);
      alternativePath.add(neighbourTransition);
      alternativePath.add(vertexTarget);
    }


    while(!q.isEmpty()){
      RuntimeEdge subTransition = q.pop();
      RuntimeVertex subVertex = subTransition.getTargetVertex();
      if(!alternativeStates.contains(subVertex)){
        alternativePath.add(subTransition);
        alternativePath.add(subVertex);
        alternativeStates.add(subVertex);
      }
      for (RuntimeEdge edge : context.getModel().getOutEdges(subVertex)) {
        RuntimeVertex vertex = edge.getTargetVertex();
        if(!alternativeStates.contains(vertex)) {
          q.add(edge);
        }
      }
    }



  }


   /*private void reachableStates(Element root, List<Element> states, Map<Element,List<Element>> transitionStates, List<Element> edgesBetweenTwoNode){

    System.out.println(root);
    System.out.println(states);

    // ha a csúcs benne van a listában
    if(states.contains(root))
      return;

    // csúcsot bele rakom a listába
    states.add(root);

    // ha a lista hossza 2
    if(states.size() == 2) {
      //ürítem a 2 cssúcs közötti listát
      edgesBetweenTwoNode.clear();

      //1. és 2. csúcs közötti éleket hozzáadom
      Element firstState = states.get(0);
      Element secondState = states.get(1);
      if (firstState instanceof RuntimeVertex && secondState instanceof RuntimeVertex) {
        RuntimeVertex firstVertex = (RuntimeVertex) firstState;
        RuntimeVertex secondVertex = (RuntimeVertex) secondState;
        for (RuntimeEdge firstEdge : context.getModel().getOutEdges(firstVertex)) {
          if (firstEdge.getTargetVertex().equals(secondVertex)) {
            edgesBetweenTwoNode.add(firstEdge);
          }
        }
      }
      //különben ha a lista > 2
    }else if(states.size() > 2){
      for(Element edge : edgesBetweenTwoNode){
        try {
          transitionStates.get(edge).add(root);
        }catch (Exception ex){
          //ha nem lenne még benne csúcs hozzáadom, hibát elkapva
          List<Element> list = new ArrayList<Element>();
          list.add(root);
          transitionStates.put(edge,list);
        }
      }
    }

    //egy ciklus a csúcs minden szomszédjára
    if (root instanceof RuntimeVertex) {
      RuntimeVertex vertex = (RuntimeVertex) root;
      for (RuntimeEdge edge : context.getModel().getOutEdges(vertex)) {
        reachableStates(edge.getTargetVertex(),states,transitionStates,edgesBetweenTwoNode);
      }
    }

    //csúcsot kiszedem a listából
    states.remove(states.size()-1);

  }*/
}
