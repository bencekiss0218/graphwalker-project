package org.graphwalker.core.algorithm;

import org.graphwalker.core.machine.Context;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.model.Path;

import java.lang.reflect.ParameterizedType;
import java.util.*;

import static org.graphwalker.core.model.Edge.RuntimeEdge;
import static org.graphwalker.core.model.Vertex.RuntimeVertex;

public class AllTransitionState implements Algorithm {

  private final Context context;

  private List<Element> states = new ArrayList<>();
  private List<Element> allTransitionOfTheModel = new ArrayList<>();
  private Map<Element,List<Element>> transitionStates = new HashMap<>();
  private Map<Element,Path<Element>> transitionPaths = new LinkedHashMap<>();
  private Map<Element,Path<Element>> otherTransitionPaths = new LinkedHashMap<>();
  private Map<Element,List<Element>> transitionNeighbours = new HashMap<>();
  private Path<Element> finalPath;
  private List<Path<Element>> testSet = new ArrayList<>();


  public AllTransitionState(Context context) {
    this.context = context;
  }


  public Map<Element,List<Element>> getReachableStates(){
    return transitionStates;
  }


  private void clearLists() {
    this.states.clear();
    this.allTransitionOfTheModel.clear();
    this.transitionStates.clear();
    this.transitionPaths.clear();
    this.otherTransitionPaths.clear();
    this.transitionNeighbours.clear();
    this.testSet.clear();
  }

  //return the test set after finishing all the steps
  public List<Path<Element>> returnTestSet(Element root){
    Path<Element> path;
    List<Path<Element>> inCaseOfNotStronglyConnectedGraph;
    clearLists();

    fillStatesAndTransitions(root);

    allReachableStates();


    for(Element transition : allTransitionOfTheModel){
      if(!equalsIgnore(transitionStates.get(transition),states)){
        inCaseOfNotStronglyConnectedGraph = new ArrayList<>();
        path = new Path<>();
        System.out.println("transition states: " + transitionStates.get(transition) + " states : " + states);
        System.out.println("GRAPH IS NOT STRONGLY CONNECTED");
        path.add(root);
        inCaseOfNotStronglyConnectedGraph.add(path);
        return inCaseOfNotStronglyConnectedGraph;
      }
    }


    lookForPaths(root);

    return testSet;
  }

  public List<Path<Element>> getTestSet(){
    return testSet;
  }

  public Path<Element> getFinalPath(){
    return finalPath;
  }

  //get the paths for each transition and create test set
  public void lookForPaths(Element root){

    pathsForTransitions();

    otherPathForTransitions();
    finalPath = getFinalPath((RuntimeVertex) root);


    testSet = getTestSet(finalPath,otherTransitionPaths, root);
    System.out.println("TEST SET: " + testSet);
  }

  //for each transition of the modell look for path to reach all states
  public void pathsForTransitions() {
    for(Element elem : allTransitionOfTheModel){
      transitionNeighboursAndPath((RuntimeEdge) elem);
    }
  }

  //all reachable states for each transition
  public void allReachableStates(){
    for(Element elem : allTransitionOfTheModel){
      List<Element> reachedStates = getReachableStates((RuntimeEdge) elem);
      transitionStates.put(elem,reachedStates);
    }
  }


  //algorithm to get all reachable states for a transition
  public List<Element> getReachableStates(RuntimeEdge transition){
    Deque<RuntimeEdge> q = new ArrayDeque<>();
    List<Element> transitionStates = new ArrayList<Element>();
    Map<Element, ElementStatus> transitionStatusMap;
    transitionStatusMap = createElementStatusMap(allTransitionOfTheModel);

    RuntimeVertex vertexTarget = transition.getTargetVertex();
    RuntimeVertex vertexSource = transition.getSourceVertex();


    if(vertexSource.equals(vertexTarget))
      transitionStates.add(vertexSource);
    else{
      transitionStates.add(vertexSource);
      transitionStates.add(vertexTarget);
    }

    q.add(transition);

    while(!q.isEmpty()) {
      RuntimeEdge subTransition = q.pop();
      transitionStatusMap.put(subTransition, ElementStatus.VISITED);
      RuntimeVertex subVertexTarget = subTransition.getTargetVertex();
      if(!transitionStates.contains(subVertexTarget)){

        transitionStates.add(subVertexTarget);
      }
      for (RuntimeEdge edge : context.getModel().getOutEdges(subVertexTarget)) {
        RuntimeVertex target = edge.getTargetVertex();
        if(!edge.equals(subTransition) && transitionStatusMap.get(edge).equals(ElementStatus.UNVISITED)){
          q.add(edge);
          if(!transitionStates.contains(target)) {
            transitionStates.add(target);
          }
        }
      }
    }
    return transitionStates;
  }

  //generation of a path for a transition to reach all states
  private void transitionPath(RuntimeEdge transition, Path<Element> transitionPath) {

    List<Element> transitionStates = new ArrayList<>();
    RuntimeEdge nextEdge;
    Map<Element, ElementStatus> transitionStatusMap = createElementStatusMap(allTransitionOfTheModel);
    List<Element> unreachedStates = this.transitionStates.get(transition);
    Map<Element, ElementStatus> stateStatusMap = createElementStatusMap(states);

    RuntimeVertex vertexSource = transition.getSourceVertex();
    RuntimeVertex vertexTarget = transition.getTargetVertex();

    stateStatusMap.put(vertexTarget, ElementStatus.VISITED);
    transitionStates.add(vertexTarget);
    transitionStatusMap.put(transition, ElementStatus.VISITED);

    transitionPath.add(vertexSource);
    transitionPath.add(transition);
    transitionPath.add(vertexTarget);

    nextEdge = chooseNextEdge(transitionStatusMap,stateStatusMap,transitionStates, vertexTarget);

    while (unreachedStates.size() != transitionStates.size()) {
      RuntimeVertex nextEdgeTarget = nextEdge.getTargetVertex();
      transitionPath.add(nextEdge);
      transitionPath.add(nextEdgeTarget);
      transitionStatusMap.put(nextEdge, ElementStatus.VISITED);
      stateStatusMap.put(nextEdgeTarget, ElementStatus.VISITED);

      if (!transitionStates.contains(nextEdgeTarget)) {
        transitionStates.add(nextEdgeTarget);
      }

      nextEdge = chooseNextEdge(transitionStatusMap, stateStatusMap,transitionStates, nextEdgeTarget);
    }
    transitionPaths.put(transition, transitionPath);
  }

  //choosing the next edge from a given state
  private RuntimeEdge chooseNextEdge(Map<Element, ElementStatus> transitionStatusMap, Map<Element, ElementStatus> stateStatusMap, List<Element> transitionStates , RuntimeVertex subVertexTarget) {
    boolean unreachedVertex = false;
    RuntimeEdge nextEdge = null;
    List<RuntimeEdge> outedges = context.getModel().getOutEdges(subVertexTarget);
    int i = 0;
    int j = 0;
    int k = 0;

    while (!unreachedVertex && k < outedges.size() ) {
      if (!transitionStates.contains(outedges.get(k).getTargetVertex())) {
        unreachedVertex = true;
        nextEdge = outedges.get(k);
      }
      k++;
    }

    if(nextEdge == null) {
      while (!unreachedVertex && i < outedges.size()) {
        if (stateStatusMap.get(outedges.get(i).getTargetVertex()).equals(ElementStatus.UNVISITED)) {
          unreachedVertex = true;
          nextEdge = outedges.get(i);
        }
        i++;
      }

      while (!unreachedVertex && j < outedges.size()) {
        if (transitionStatusMap.get(outedges.get(j)).equals(ElementStatus.UNVISITED)) {
          RuntimeVertex vertexSource = outedges.get(j).getSourceVertex();
          RuntimeVertex vertexTarget = outedges.get(j).getTargetVertex();
          if (!vertexSource.equals(vertexTarget)) {
            unreachedVertex = true;
            nextEdge = outedges.get(j);
            resetElementStatusMap(stateStatusMap);
          }
        }
        j++;
      }

      Random random = new Random(System.nanoTime());
      boolean loopEdge = true;
      while(loopEdge && !unreachedVertex){
        RuntimeEdge edge = outedges.get(random.nextInt(outedges.size()));
        RuntimeVertex vertexSource = edge.getSourceVertex();
        RuntimeVertex vertexTarget = edge.getTargetVertex();
        if (!vertexSource.equals(vertexTarget)) {
          nextEdge = edge;
          loopEdge = false;
        }
      }
    }

    return nextEdge;
  }

  //choose next edge from a given state at the finalpath
  private RuntimeEdge chooseNextEdgeFinal(Map<Element, ElementStatus> transitionStatusMap, Map<Element, ElementStatus> stateStatusMap,List<Element> reachedStates, RuntimeVertex subVertexTarget) {
    boolean unreachedVertex = false;
    RuntimeEdge nextEdge = null;
    List<RuntimeEdge> outedges = context.getModel().getOutEdges(subVertexTarget);
    int i = 0;
    int j = 0;
    int k = 0;

    while (!unreachedVertex && k < outedges.size()) {
      if (transitionStatusMap.get(outedges.get(k)).equals(ElementStatus.UNVISITED) && !reachedStates.contains(outedges.get(k).getTargetVertex())) {
        unreachedVertex = true;
        nextEdge = outedges.get(k);
      }
      k++;
    }

    if(nextEdge == null) {
      while (!unreachedVertex && j < outedges.size()) {
        if (transitionStatusMap.get(outedges.get(j)).equals(ElementStatus.UNVISITED)) {
          unreachedVertex = true;
          nextEdge = outedges.get(j);
          resetElementStatusMap(stateStatusMap);
        }
        j++;
      }

      while (!unreachedVertex && i < outedges.size()) {
        if (stateStatusMap.get(outedges.get(i).getTargetVertex()).equals(ElementStatus.UNVISITED)) {
          unreachedVertex = true;
          nextEdge = outedges.get(i);
        }
        i++;
      }

      Random random = new Random(System.nanoTime());
      boolean loopEdge = true;
      while(loopEdge && !unreachedVertex){
        RuntimeEdge edge = outedges.get(random.nextInt(outedges.size()));
        RuntimeVertex vertexSource = edge.getSourceVertex();
        RuntimeVertex vertexTarget = edge.getTargetVertex();
        if (!vertexSource.equals(vertexTarget)) {
          nextEdge = edge;
          loopEdge = false;
        }
      }
    }

    return nextEdge;
  }

  //setting other path for transitions with equal states
  private void otherPathWithEqualStates(RuntimeEdge transition){
    List<Element> states;
    states = transitionStates.get(transition);
    List<Element> neighbours = transitionNeighbours.get(transition);

    for(Element neighbour : neighbours){
      if(!neighbour.equals(transition)) {
        Path<Element> alterPath = transitionPaths.get(neighbour);
        List<Element> alterStates = transitionStates.get(neighbour);
        if(equalsIgnore(alterStates, states) && !alterPath.contains(transition) && !otherTransitionPaths.containsValue(alterPath)){
          otherTransitionPaths.put(transition, alterPath);
        }
      }
    }
  }


  private void transitionNeighboursAndPath(RuntimeEdge transition){
    Path<Element> transitionPath = new Path<>();
    RuntimeVertex vertexSource = transition.getSourceVertex();

    List<Element> neighbourTransitions = new ArrayList<>();
    for (RuntimeEdge edge : context.getModel().getOutEdges(vertexSource)) {
      RuntimeVertex start = edge.getSourceVertex();
      RuntimeVertex end = edge.getTargetVertex();
      if(!edge.equals(transition) && !start.equals(end))
        neighbourTransitions.add(edge);
    }

    transitionNeighbours.put(transition, neighbourTransitions);
    transitionPath(transition,transitionPath);
    transitionPaths.put(transition,transitionPath);
  }

  //removing duplicated paths from the given map
  private void removeDuplicatePaths(Map<Element,Path<Element>> map) {

    Iterator<Map.Entry<Element,Path<Element>>> iterator = map.entrySet().iterator();
    HashSet<List<Element>> pathsSet = new HashSet<>();
    while (iterator.hasNext()) {
      Map.Entry<Element,Path<Element>> next = iterator.next();
      List<Element> path = new ArrayList<>(next.getValue());
      if (!pathsSet.add(path)) {
        iterator.remove();
      }
    }

  }

  //algorithm to determine the final path
  private Path<Element> getFinalPath(RuntimeVertex root){

    Path<Element> finalPath = new Path<>();
    RuntimeEdge nextEdge;
    RuntimeEdge transition = context.getModel().getOutEdges(root).get(0);
    Map<Element, ElementStatus> transitionStatusMap = createElementStatusMap(allTransitionOfTheModel);
    Map<Element, ElementStatus> stateStatusMap = createElementStatusMap(states);
    List<Element> unreachedTransitions = new ArrayList<>(allTransitionOfTheModel);
    List<Element> reachedStates = new ArrayList<>();
    boolean reachedLastTransition = false;

    RuntimeVertex vertexSource = transition.getSourceVertex();
    RuntimeVertex vertexTarget = transition.getTargetVertex();

    stateStatusMap.put(vertexTarget,ElementStatus.VISITED);
    reachedStates.add(vertexTarget);
    transitionStatusMap.put(transition, ElementStatus.VISITED);
    unreachedTransitions.remove(transition);
    finalPath.add(vertexSource);
    finalPath.add(transition);
    finalPath.add(vertexTarget);

    nextEdge = chooseNextEdgeFinal(transitionStatusMap,stateStatusMap,reachedStates ,vertexTarget);

    while(!reachedLastTransition) {
      RuntimeVertex nextEdgeTarget = nextEdge.getTargetVertex();
      transitionStatusMap.put(nextEdge, ElementStatus.VISITED);
      stateStatusMap.put(nextEdgeTarget, ElementStatus.VISITED);

      if(!reachedStates.contains(nextEdgeTarget))
        reachedStates.add(nextEdgeTarget);

      unreachedTransitions.remove(nextEdge);
      if(unreachedTransitions.size() == 0){
        reachedLastTransition = true;
        Path<Element> lastTransitionPath = new Path<Element>(transitionPaths.get(nextEdge));
        lastTransitionPath.removeFirst();
        finalPath.addAll(lastTransitionPath);
      }else{
        finalPath.add(nextEdge);
        finalPath.add(nextEdgeTarget);
        nextEdge = chooseNextEdgeFinal(transitionStatusMap, stateStatusMap, reachedStates, nextEdgeTarget);
      }
      }

    return finalPath;
  }

  //collection to determine the testset
  private List<Path<Element>> getTestSet(Path<Element> finalPath, Map<Element, Path<Element>> otherTransitionPaths, Element start){
    List<Path<Element>> testSet = new ArrayList<>();
    removeDuplicatePaths(otherTransitionPaths);
    testSet.add(finalPath);
    addOtherPaths(testSet,(RuntimeVertex) start);

    return testSet;
  }

  //adding the otherpaths to the test set
  private void addOtherPaths(List<Path<Element>> testSet, RuntimeVertex root){
    List<Element> transitions = new ArrayList<>(otherTransitionPaths.keySet());
    Element firstTransition = context.getModel().getOutEdges(root).get(0);
    Path<Element> firstPath = transitionPaths.get(firstTransition);

    for(Element transition : transitions){
      Path<Element> newPath = new Path<>();
      List<Element> states = new ArrayList<>();
      boolean foundStartVertex = false;
      Path<Element> transitionPath = otherTransitionPaths.get(transition);
      Element startVertex = transitionPath.getFirst();
      Iterator<Element> iterator = firstPath.iterator();

      while(iterator.hasNext() && !foundStartVertex){
        Element element = iterator.next();

        if(element.equals(startVertex)){
          foundStartVertex = true;
          Iterator<Element> subIterator = transitionPath.iterator();

          while(subIterator.hasNext() && states.size() != this.states.size()){
            Element subElement = subIterator.next();
            if(subElement instanceof RuntimeVertex) {
              newPath.add(subElement);
            }
            else {
              RuntimeEdge edge = (RuntimeEdge) subElement;
              newPath.add(subElement);
              if(!states.contains(edge.getTargetVertex())) {
                states.add(edge.getTargetVertex());
                if(states.size() == this.states.size())
                  newPath.add(edge.getTargetVertex());
              }
            }
          }
        }else {
          newPath.add(element);

          if(element instanceof RuntimeEdge) {
            RuntimeEdge edge = (RuntimeEdge) element;
            if(!states.contains(edge.getTargetVertex()))
              states.add(edge.getTargetVertex());
          }
        }
      }

      Iterator<Path<Element>> it = testSet.iterator();
      boolean l = false;
      List<Element> a = new ArrayList<>(newPath);
      List<Element> b;
      while (it.hasNext() && !l){
        b = new ArrayList<>(it.next());
        if(equalsIgnore(a,b) || a.contains(transition)) {
          l = true;
        }
      }
      if(!l){
        testSet.add(newPath);
      }

    }
  }

  public void otherPathForTransitions() {

    for(Element edge : allTransitionOfTheModel) {
      otherPathWithEqualStates((RuntimeEdge) edge);
    }

  }

  public boolean equalsIgnore(List<Element> a, List<Element> b) {
    boolean l = false;
    int i = 0;
    int j = 0;

    if (a == b) {
      return true;
    }

    if((a == null && b != null)
      || a != null && b == null
      || a.size() != b.size()){
      return false;
    }

    while(i<a.size()){
      l = false;
      j = 0;
      while(j<b.size() && !l){
        if( a.get(i).equals(b.get(j)) && a.get(i).toString().equals(b.get(j).toString())){
          l = true;
          i++;
        }else{
          j++;
        }
      }
      if(j == b.size()){
        return false;
      }
    }

    return l;
  }

  public void fillStatesAndTransitions(Element root){
    BreadthFirstSearch BFS = context.getAlgorithm(BreadthFirstSearch.class);
    BFS.getConnectedComponents(root);
    List<Element> allTransitionOfTheModel;
    List<Element> states;
    allTransitionOfTheModel = BFS.getConnectedTransitions();
    states = BFS.getConnectedVertices();
    this.allTransitionOfTheModel = allTransitionOfTheModel;
    this.states = states;
  }

  private Map<Element, ElementStatus> createElementStatusMap(List<Element> elements) {
    Map<Element, ElementStatus> elementStatusMap = new HashMap<>();
    for (Element element : elements) {
      elementStatusMap.put(element, ElementStatus.UNVISITED);
    }
    return elementStatusMap;
  }

  private void resetElementStatusMap(Map<Element, ElementStatus> elementStatusMap) {
    for (Element element : elementStatusMap.keySet()) {
      elementStatusMap.put(element, ElementStatus.UNVISITED);
    }
  }

  public Map<Element,List<Element>> getNeighbours(){
    return transitionNeighbours;
  }

  public Map<Element,Path<Element>> getTransitionPaths(){
    return transitionPaths;
  }

  public Map<Element,Path<Element>> getOtherTransitionPaths(){
    return otherTransitionPaths;
  }

  private enum ElementStatus {
    UNVISITED, VISITED
  }
}
