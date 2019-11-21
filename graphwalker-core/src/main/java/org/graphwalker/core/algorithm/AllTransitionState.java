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
  private Map<Element,Path<Element>> transitionOtherPaths = new LinkedHashMap<Element,Path<Element>>();
  private Map<Element,Path<Element>> alterTransitionPaths = new LinkedHashMap<Element,Path<Element>>();
  private Map<Element,Path<Element>> otherTransitionPaths = new LinkedHashMap<Element,Path<Element>>();
  private Map<Element,List<Element>> transitionNeighbours =  new HashMap<Element,List<Element>>();
  private List<Path<Element>> testSet = new ArrayList<>();
  List<Element> allTransitionOfTheModel = new ArrayList<>();



  public AllTransitionState(Context context) {
    this.context = context;
  }


  public Map<Element,List<Element>> getReachableStates(){
    return transitionStates;
  }


  private void clearLists() {
    this.transitionStates.clear();
    this.states.clear();
    this.transitionPaths.clear();
  }

  public List<Path<Element>> returnTestSet(Element root){
    clearLists();
    allReachableStates(root);

    List<Path<Element>> inCaseOfNotStronglyConnectedGraph = new ArrayList<>();
    Path<Element> path = new Path<>();
    path.add(root);
    inCaseOfNotStronglyConnectedGraph.add(path);
    for(Element transition : allTransitionOfTheModel){
      if(!equalsIgnore(transitionStates.get(transition),states)){
        System.out.println("GRAPH IS NOT STRONGLY CONNECTED");
        return inCaseOfNotStronglyConnectedGraph;
      }
    }

    return testSet;
  }

  public List<Path<Element>> getTestSet(){
    return testSet;
  }

  private void allReachableStates(Element root){

    Path<Element> finalPath;
    allTransitionOfTheModel = fillTransitionsWithBreadthFirstSearch(root);
    states = fillStatesWithBreadthFirstSearch(root);

    for(Element elem : allTransitionOfTheModel){
      //ez jobb megoldás a kommentelt
      List<Element> reachedStates = getReachableStates((RuntimeEdge) elem);
      transitionStates.put(elem,reachedStates);
    }

    for(Element elem : allTransitionOfTheModel){
      transitionStatesNeighboursAndPath((RuntimeEdge) elem);
    }

    getPathForTransitions(allTransitionOfTheModel);

    //összefűzzük az élidegen utakat
    finalPath = getFinalPath((RuntimeVertex) root);
    System.out.println("LONG path is : " + finalPath);
    System.out.println("OTHER transitions for each T: " + otherTransitionPaths);
    //System.out.println("FINAL PATH: " + finalPath);
    testSet = getTestSet(finalPath,otherTransitionPaths,transitionStates,states, root);
    System.out.println("TEST SET: " + testSet);


  }


  private List<Element> getReachableStates(RuntimeEdge transition){
    Deque<RuntimeEdge> q = new ArrayDeque<>();
    List<Element> transitionStates = new ArrayList<Element>();
    Map<Element, ElementStatus> transitionStatusMap = new HashMap<>();
    transitionStatusMap = createElementStatusMap(allTransitionOfTheModel);

    RuntimeVertex vertexTarget = transition.getTargetVertex();
    RuntimeVertex vertexSource = transition.getSourceVertex();


    if(vertexSource.equals(vertexTarget))
      transitionStates.add(vertexSource);
    else{
      transitionStates.add(vertexSource);
      transitionStates.add(vertexTarget);
    }

    transitionStatusMap.put(transition, ElementStatus.REACHABLE);

    for (RuntimeEdge edge : context.getModel().getOutEdges(vertexTarget)) {
      if(!edge.equals(transition)) {
        q.add(edge);
      }
    }

    while(!q.isEmpty()) {
      RuntimeEdge subTransition = q.pop();
      transitionStatusMap.put(subTransition, ElementStatus.REACHABLE);
      RuntimeVertex subVertexTarget = subTransition.getTargetVertex();
      if(!transitionStates.contains(subVertexTarget)){

        transitionStates.add(subVertexTarget);
      }
      for (RuntimeEdge edge : context.getModel().getOutEdges(subVertexTarget)) {
        RuntimeVertex target = edge.getTargetVertex();
        if(!edge.equals(subTransition) && transitionStatusMap.get(edge).equals(ElementStatus.UNREACHABLE)){
          q.add(edge);
          if(!transitionStates.contains(target)) {
            transitionStates.add(target);
          }
        }
      }
    }
    return transitionStates;
  }

  public void getTransitionPath(RuntimeEdge transition, Path<Element> transitionPath, List<Element> transitionStates) {

    Deque<RuntimeEdge> q = new ArrayDeque<>();
    Map<Element, ElementStatus> transitionStatusMap = new HashMap<>();
    List<Element> unreachedStates = new ArrayList<>();
    unreachedStates = this.transitionStates.get(transition);
    transitionStatusMap = createElementStatusMap(allTransitionOfTheModel);


    RuntimeVertex vertexSource = transition.getSourceVertex();
    RuntimeVertex vertexTarget = transition.getTargetVertex();

    //ez volt kommentelve akkor a sourcet az elején nem adja hozzá
    if(vertexSource.equals(vertexTarget))
      transitionStates.add(vertexSource);
    else{
      transitionStates.add(vertexSource);
      transitionStates.add(vertexTarget);
    }

    transitionPath.add(vertexSource);
    transitionPath.add(transition);
    transitionPath.add(vertexTarget);
    transitionStatusMap.put(transition, ElementStatus.REACHABLE);


    for (RuntimeEdge edge : context.getModel().getOutEdges(vertexTarget)) {
      //ha a tranzicio nem egyenlő magával a tranzicioval
      RuntimeVertex edgeSource = edge.getSourceVertex();
      RuntimeVertex edgeTarget = edge.getTargetVertex();
      if (!edgeSource.equals(edgeTarget)) {
        q.add(edge);
        break;
      }
    }

    while (!q.isEmpty()) {

      RuntimeEdge subTransition = q.pop();
      RuntimeVertex subVertexTarget = subTransition.getTargetVertex();

      if (unreachedStates.size() == transitionStates.size()) {
        transitionPaths.put(transition, transitionPath);
        return;
      }

      transitionPath.add(subTransition);
      transitionPath.add(subVertexTarget);
      transitionStatusMap.put(subTransition, ElementStatus.REACHABLE);
      if(!transitionStates.contains(subVertexTarget))
        transitionStates.add(subVertexTarget);

      boolean unreachableEdge = false;
      List<RuntimeEdge> outedges = context.getModel().getOutEdges(subVertexTarget);
      int i = 0;
      while (!unreachableEdge && i < outedges.size()) {
        if (transitionStatusMap.get(outedges.get(i)).equals(ElementStatus.UNREACHABLE)) {
          unreachableEdge = true;
          q.add(outedges.get(i));
        }
        i++;
      }

      if (!unreachableEdge) {
        for (RuntimeEdge edge : context.getModel().getOutEdges(subVertexTarget)) {
          RuntimeVertex rVertexSource = edge.getSourceVertex();
          RuntimeVertex rVertexTarget = edge.getTargetVertex();
          if (!rVertexSource.equals(rVertexTarget)) {
            q.add(edge);
            break;
          }
        }
      }
    }
  }


  public void otherPathWithEqualStates(RuntimeEdge transition){
    List<Element> states;
    states = transitionStates.get(transition);
    Path<Element> path = transitionPaths.get(transition);
    List<Element> neighbours = transitionNeighbours.get(transition);
    boolean found = false;
    for(Element neighbour : neighbours){
      if(!neighbour.equals(transition) && !found) {
        Path<Element> alterPath = transitionPaths.get(neighbour);
        List<Element> alterStates = transitionStates.get(neighbour);
        if(equalsIgnore(alterStates, states) && !alterPath.contains(transition)){
          otherTransitionPaths.put(transition, alterPath);
          found = true;
        }
      }
    }

    if(!found){
      otherTransitionPaths.put(transition,path);
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
    getTransitionPath(transition,transitionPath,states);
    transitionPaths.put(transition,transitionPath);

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

  /*
  private Path<Element> getFinalPath(Map<Element,Path<Element>> transitionPaths){

    //System.out.println("transition states: " + transitionStates);
    //System.out.println("TransitionPaths beginning: " + transitionPaths);
    Path<Element> finalPath = new Path<>();

    //if duplicates are not deleted then the final path will be longer
    removeDuplicatePaths(transitionPaths);
    //System.out.println("TransitionPaths AFTER REMOVED COPIES: " + transitionPaths);
    Map.Entry<Element,Path<Element>> entry = transitionPaths.entrySet().iterator().next();
    Element start;
    Element end;
    Path<Element> longestTransition = entry.getValue();
    int max = 0;
    int i = 0;

    Element keyTransition = entry.getKey();
    finalPath.addAll(transitionPaths.get(keyTransition));

    int size = transitionPaths.keySet().size();

    //System.out.println("Keyset size: " + transitionPaths.keySet().size() + " Keyset: " + transitionPaths.keySet());
    while(i < size){
      end = transitionPaths.get(keyTransition).getLast();

      for(Element j : transitionPaths.keySet()){
        //longest path
        if(transitionPaths.get(j).size() > max){
          max = transitionPaths.get(j).size();
          longestTransition = transitionPaths.get(j);
        }

        if(keyTransition.equals(j))
          continue;

        start = transitionPaths.get(j).getFirst();
        //System.out.println("Loop number: " + i + " - Keytransition - " + keyTransition +  " - NextPath - " + j + " - End - " + end + " - Start - " + start);
        if(end.equals(start)){
          Path<Element> pathToAdd = new Path<>(transitionPaths.get(j));
          pathToAdd.removeFirst();

          List<Element> copyOfFinalPath = new ArrayList<>(finalPath);
          List<Element> copyOfPathToAdd = new ArrayList<>(pathToAdd);
          int contains = Collections.indexOfSubList(copyOfFinalPath, copyOfPathToAdd);
          if(contains == -1) {
            finalPath.addAll(pathToAdd);
            transitionPaths.remove(keyTransition);
          }
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
  */
  private Path<Element> getFinalPath(RuntimeVertex root){

    Path<Element> finalPath = new Path<>();
    Deque<RuntimeEdge> q = new ArrayDeque<>();
    RuntimeEdge transition = context.getModel().getOutEdges(root).get(0);
    Map<Element, ElementStatus> transitionStatusMap = new HashMap<>();
    List<Element> unreachedTransitions = new ArrayList<>(allTransitionOfTheModel);
    List<Element> unreachedStates = new ArrayList<>(states);
    transitionStatusMap = createElementStatusMap(allTransitionOfTheModel);


    RuntimeVertex vertexSource = transition.getSourceVertex();
    RuntimeVertex vertexTarget = transition.getTargetVertex();

    finalPath.add(vertexSource);
    finalPath.add(transition);
    finalPath.add(vertexTarget);
    transitionStatusMap.put(transition, ElementStatus.REACHABLE);
    unreachedTransitions.remove(transition);


    for (RuntimeEdge edge : context.getModel().getOutEdges(vertexTarget)) {
      //ha a tranzicio nem egyenlő magával a tranzicioval
      RuntimeVertex edgeSource = edge.getSourceVertex();
      RuntimeVertex edgeTarget = edge.getTargetVertex();
      if (!edgeSource.equals(edgeTarget)) {
        q.add(edge);
        break;
      }
    }

    while (!q.isEmpty()) {

      RuntimeEdge subTransition = q.pop();
      RuntimeVertex subVertexTarget = subTransition.getTargetVertex();
      RuntimeVertex subVertexSource = subTransition.getSourceVertex();


      if(!transitionStatusMap.containsValue(ElementStatus.UNREACHABLE) && unreachedStates.isEmpty())
        return finalPath;

      finalPath.add(subTransition);
      finalPath.add(subVertexTarget);
      transitionStatusMap.put(subTransition, ElementStatus.REACHABLE);

      if(unreachedTransitions.size() == 1){
        unreachedStates.remove(subVertexSource);
        unreachedStates.remove(subVertexTarget);
      }else{
        unreachedTransitions.remove(subTransition);
      }

      boolean unreachableEdge = false;
      List<RuntimeEdge> outedges = context.getModel().getOutEdges(subVertexTarget);
      int i = 0;
      while (!unreachableEdge && i < outedges.size()) {
        if (transitionStatusMap.get(outedges.get(i)).equals(ElementStatus.UNREACHABLE)) {
          unreachableEdge = true;
          q.add(outedges.get(i));
        }
        i++;
      }

      if (!unreachableEdge) {
        for (RuntimeEdge edge : context.getModel().getOutEdges(subVertexTarget)) {
          RuntimeVertex rVertexSource = edge.getSourceVertex();
          RuntimeVertex rVertexTarget = edge.getTargetVertex();
          if (!rVertexSource.equals(rVertexTarget)) {
            q.add(edge);
            break;
          }
        }
      }
    }


    return finalPath;
  }

  private List<Path<Element>> getTestSet(Path<Element> finalPath, Map<Element,Path<Element>> alterTransitionPaths, Map<Element,List<Element>> transitionStates, List<Element> states, Element start){
    List<Path<Element>> testSet = new ArrayList<>();
    removeDuplicatePaths(alterTransitionPaths);


    System.out.println("After removed duplicates :  " + alterTransitionPaths + " SIZE: " + alterTransitionPaths.keySet().size());
    System.out.println("TransitionPaths: " + transitionPaths + " SIZE: " + transitionPaths.keySet().size());

    testSet.add(finalPath);

    addNotAddedTransitionsToTestSet(testSet,(RuntimeVertex) start);

    return testSet;
  }

  private void addNotAddedTransitionsToTestSet(List<Path<Element>> testSet, RuntimeVertex root){
    List<Element> transitions = new ArrayList<>(otherTransitionPaths.keySet());

    for(Element transition : transitions){
      Element firstOutEdge = context.getModel().getOutEdges(root).get(0);
      Path<Element> newPath = new Path<>();
      List<Element> states = new ArrayList<>();
      boolean foundStartVertex = false;
      Path<Element> transitionPath = otherTransitionPaths.get(transition);
      Element startVertex = transitionPath.getFirst();
      Path<Element> firstPath = transitionPaths.get(firstOutEdge);
      Iterator<Element> iterator = firstPath.iterator();

      while(iterator.hasNext() && !foundStartVertex){
        Element element = iterator.next();

        if(element.equals(startVertex)){
          foundStartVertex = true;
          //newPath.addAll(transitionPath);

          Iterator<Element> subIterator = transitionPath.iterator();

          while(subIterator.hasNext() && states.size() != this.states.size()){
            Element subElement = subIterator.next();
            if(!states.contains(subElement) && subElement instanceof RuntimeVertex)
              newPath.add(subElement);
            else
              newPath.add(subElement);
          }
        }else {
          newPath.add(element);

          if(element instanceof RuntimeVertex && !states.contains(element))
            states.add(element);
        }
      }
      testSet.add(newPath);
    }
  }

  private void getPathForTransitions(List<Element> allTransitionOfTheModel) {

    for(Element edge : allTransitionOfTheModel) {
      otherPathWithEqualStates((RuntimeEdge) edge);
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
}
