package org.graphwalker.core.algorithm;

import org.graphwalker.core.machine.Context;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.model.Path;

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
  private Map<Element,Path<Element>> transitionPaths = new LinkedHashMap<Element,Path<Element>>();
  private Map<Element,Path<Element>> alterTransitionPaths = new LinkedHashMap<Element,Path<Element>>();
  private Map<Element,List<Element>> transitionNeighbours =  new HashMap<Element,List<Element>>();
  private List<Path<Element>> testSet = new ArrayList<>();
  List<Element> allTransitionOfTheModel = new ArrayList<>();



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

  public List<Path<Element>> returnTestSet(Element root){
    clearLists();
    allReachableStates(root, transitionPaths);
    return testSet;
  }

  private void allReachableStates(Element root, Map<Element,Path<Element>> transitionPaths){

    Path<Element> finalPath;
    allTransitionOfTheModel = fillTransitionsWithBreadthFirstSearch(root);
    states = fillStatesWithBreadthFirstSearch(root);

    System.out.println("STATES: " + states);

    System.out.println("transitions are: " + allTransitionOfTheModel);



    for(Element elem : allTransitionOfTheModel){
      transitionStatesNeighboursAndPath((RuntimeEdge) elem);
    }
    System.out.println("States for each transitions: " + transitionStates);

    System.out.println("Path for each transition BEFORE " + transitionPaths);
    getPathForTransitions(allTransitionOfTheModel);
    System.out.println("Path for each transition AFTER " + transitionPaths);
    System.out.println("Neighbours for each transition: " + transitionNeighbours);
    finalPath = getFinalPath(transitionPaths);
    System.out.println("FINAL PATH: " + finalPath);
    testSet = getTestSet(finalPath,alterTransitionPaths,transitionStates,states);
    System.out.println("TEST SET: " + testSet);


  }

  public void getTransitionStatesAndPath(RuntimeEdge transition, Path<Element> transitionPath, List<Element> transitionStates){

    Deque<RuntimeEdge> q = new ArrayDeque<>();
    RuntimeVertex vertexSource = transition.getSourceVertex();
    RuntimeVertex vertexTarget = transition.getTargetVertex();
    Map<Element, ElementStatus> transitionStatusMap = new HashMap<>();
    transitionStatusMap = createElementStatusMap(allTransitionOfTheModel);

    transitionStatusMap.put(transition, ElementStatus.REACHABLE);


    if(vertexSource.equals(vertexTarget))
      transitionStates.add(vertexSource);
    else{
      transitionStates.add(vertexSource);
      transitionStates.add(vertexTarget);
    }

    transitionPath.add(vertexSource);
    transitionPath.add(transition);
    transitionPath.add(vertexTarget);

    for (RuntimeEdge edge : context.getModel().getOutEdges(vertexTarget)) {
      RuntimeVertex vertex = edge.getTargetVertex();
      if((!transitionStates.contains(vertex) || ElementStatus.UNREACHABLE.equals(transitionStatusMap.get(edge))) && transitionStates.size() != this.states.size()) {
        q.add(edge);
      }
    }

    while(!q.isEmpty()) {
      RuntimeEdge subTransition = q.pop();
      transitionStatusMap.put(subTransition, ElementStatus.REACHABLE);
      RuntimeVertex subVertexSource = subTransition.getSourceVertex();
      RuntimeVertex subVertexTarget = subTransition.getTargetVertex();

     // System.out.println("POP: " + subTransition);

      if (transitionStates.size() != this.states.size()) {

        if (subVertexSource.equals(transitionPath.getLast())) {
          transitionPath.add(subTransition);
          transitionPath.add(subVertexTarget);

          if(!transitionStates.contains(subVertexTarget))
            transitionStates.add(subVertexTarget);
        }

        //if (!transitionStates.contains(subVertexTarget)) {
        //  transitionStates.add(subVertexTarget);
        //}
        for (RuntimeEdge edge : context.getModel().getOutEdges(subVertexTarget)) {
          RuntimeVertex vertex = edge.getTargetVertex();
          if(!q.contains(edge)) {
            if ((!transitionStates.contains(vertex) || ElementStatus.UNREACHABLE.equals(transitionStatusMap.get(edge))) && transitionStates.size() != this.states.size()) {
              //System.out.println("add to Q: " + edge);
              q.add(edge);
            }
          }
        }
      }
    }
  }

  public void transitionStatesNeighboursAndPath(RuntimeEdge transition){
    List<Element> states = new ArrayList<Element>();
    Path<Element> transitionPath = new Path<>();
    RuntimeVertex vertexSource = transition.getSourceVertex();

    //get the transitionNeighbours for the given transition
    List<Element> neighbourTransitions = new ArrayList<Element>();
    for (RuntimeEdge edge : context.getModel().getOutEdges(vertexSource)) {
      if(!edge.equals(transition))
        neighbourTransitions.add(edge);
    }
    transitionNeighbours.put(transition, neighbourTransitions);

    getTransitionStatesAndPath(transition, transitionPath, states);

    transitionPaths.put(transition,transitionPath);
    transitionStates.put(transition, states);

  }

  private void alternativeTransitionStateAndPathWithNeighbours(RuntimeEdge transition, Path<Element> alternativePath, List<Element> alternativeStates){
    Deque<RuntimeEdge> q = new ArrayDeque<>();
    RuntimeEdge neighbourTransition;

    for (Element edge : transitionNeighbours.get(transition)) {
      RuntimeEdge rEdge = (RuntimeEdge) edge;
      q.add(rEdge);
    }

    if(!q.isEmpty()) {
      neighbourTransition = q.pop();
      getTransitionStatesAndPath(neighbourTransition, alternativePath, alternativeStates);
    }

  }

  private void removeDuplicatePaths(Map<Element,Path<Element>> map) {

    Iterator<Map.Entry<Element,Path<Element>>> iterator = map.entrySet().iterator();
    HashSet<List<Element>> valueSet = new HashSet<>();
    while (iterator.hasNext()) {
      Map.Entry<Element,Path<Element>> next = iterator.next();
      List<Element> listToAdd = new ArrayList<>(next.getValue());
      if (!valueSet.add(listToAdd)) {
        iterator.remove();
      }
    }

  }

  private Path<Element> getFinalPath(Map<Element,Path<Element>> transitionPaths){

    System.out.println("transition states: " + transitionStates);
    System.out.println("TransitionPaths beginning: " + transitionPaths);
    Path<Element> finalPath = new Path<>();

    //if duplicates are not deleted then the final path will be longer
    removeDuplicatePaths(transitionPaths);
    System.out.println("TransitionPaths AFTER REMOVED COPIES: " + transitionPaths);
    Map.Entry<Element,Path<Element>> entry = transitionPaths.entrySet().iterator().next();
    Element start;
    Element end;
    Path<Element> longestTransition = entry.getValue();
    int max = 0;
    int i = 0;

    Element keyTransition = entry.getKey();
    finalPath.addAll(transitionPaths.get(keyTransition));

    int size = transitionPaths.keySet().size();

    System.out.println("Keyset size: " + transitionPaths.keySet().size() + " Keyset: " + transitionPaths.keySet());
    while(i < size){
      System.out.println("ITERATION: " + i);
      //end = copyOfTransitionPaths.get(keyTransition).get(copyOfTransitionPaths.get(keyTransition).size() - 1);
      end = transitionPaths.get(keyTransition).getLast();



      for(Element j : transitionPaths.keySet()){
        //longest path
        if(transitionPaths.get(j).size() > max){
          max = transitionPaths.get(j).size();
          longestTransition = transitionPaths.get(j);
        }

        if(keyTransition.equals(j))
          continue;

        //start = copyOfTransitionPaths.get(j).get(0);
        start = transitionPaths.get(j).getFirst();
        System.out.println("Loop number: " + i + " - Keytransition - " + keyTransition +  " - NextPath - " + j + " - End - " + end + " - Start - " + start);
        if(end.equals(start)){
          //finalPath.addAll(copyOfTransitionPaths.get(j).subList(1,copyOfTransitionPaths.get(j).size()));
          Path<Element> pathToAdd = new Path<>();
          pathToAdd = transitionPaths.get(j);
          pathToAdd.removeFirst();
          finalPath.addAll(pathToAdd);
          transitionPaths.remove(keyTransition);
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

  private List<Path<Element>> getTestSet(Path<Element> finalPath, Map<Element,Path<Element>> alterTransitionPaths, Map<Element,List<Element>> transitionStates, List<Element> states){
    List<Path<Element>> testSet = new ArrayList<>();
    //boolean alterPath = false;
    List<Element> copyOfFinalPath = new ArrayList<>(finalPath);


    testSet.add(finalPath);

    for(Element key : alterTransitionPaths.keySet()){

      System.out.println("A: " + transitionStates.get(key)  + "B: " +states ) ;
      if(equalsIgnore(transitionStates.get(key),states) ) {

        List<Element> copyOfalterTransitionPaths = new ArrayList<>(alterTransitionPaths.get(key));
          int contains = Collections.indexOfSubList(copyOfFinalPath, copyOfalterTransitionPaths);

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
    Path<Element> alternativePath;
    List<Element> alternativeStates;

    for(Element edge : allTransitionOfTheModel) {
      alternativePath = new Path<>();
      alternativeStates = new ArrayList<>();
      alternativeTransitionStateAndPathWithNeighbours((RuntimeEdge) edge, alternativePath, alternativeStates);

      System.out.println("Transition: " + edge + " ALTER STATES: " + alternativeStates + " ALTER PATH: " + alternativePath);


      if (equalsIgnore(transitionStates.get(edge),alternativeStates) && !alternativePath.contains(edge)) {
        alterTransitionPaths.put(edge, transitionPaths.get(edge));
        transitionPaths.replace(edge, alternativePath);
      }
    }
  }

  private boolean equalsIgnore(List<Element> a, List<Element> b) {
    boolean l = true;
    boolean k = false;
    int i = 0;
    int j = 0;

    if((a == null && b != null)
      || a != null && b == null
      || a.size() != b.size()){
      return false;
    }

    while(l && i <a.size()){
      while(!k && j <b.size()){
        if(a.get(i).equals(b.get(j))){
          l = true;
          k = true;
        }else {
          l = false;
        }
        j++;
      }
      i++;
    }
    return l;
  }


  private List<Element> fillTransitionsWithBreadthFirstSearch(Element root){
    BreadthFirstSearch BFS = context.getAlgorithm(BreadthFirstSearch.class);
    BFS.getConnectedComponents(root);
    return BFS.getConnectedTransitions();
  }

  private List<Element> fillStatesWithBreadthFirstSearch(Element root){
    BreadthFirstSearch BFS = context.getAlgorithm(BreadthFirstSearch.class);
    return BFS.getConnectedVertices();
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
