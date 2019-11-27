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

  public void lookForPaths(Element root){

    pathsForTransitions();
    otherPathForTransitions();
    finalPath = getFinalPath((RuntimeVertex) root);
    //System.out.println("ORIGINAL TRANSITIONS: " + transitionPaths);
    //System.out.println("OTHER transitions for each transition: " + otherTransitionPaths);
    testSet = getTestSet(finalPath,otherTransitionPaths, root);
    System.out.println("TEST SET: " + testSet);

  }

  public void pathsForTransitions() {
    for(Element elem : allTransitionOfTheModel){
      transitionNeighboursAndPath((RuntimeEdge) elem);
    }
  }

  public void allReachableStates(){
    for(Element elem : allTransitionOfTheModel){
      List<Element> reachedStates = getReachableStates((RuntimeEdge) elem);
      transitionStates.put(elem,reachedStates);
    }
  }


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

    //transitionStatusMap.put(transition, ElementStatus.VISITED);
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

  private void transitionPath(RuntimeEdge transition, Path<Element> transitionPath, List<Element> transitionStates) {

    Deque<RuntimeEdge> q = new ArrayDeque<>();
    Map<Element, ElementStatus> transitionStatusMap;
    List<Element> unreachedStates;
    unreachedStates = this.transitionStates.get(transition);
    transitionStatusMap = createElementStatusMap(allTransitionOfTheModel);
    Map<Element, ElementStatus> stateStatusMap = createElementStatusMap(states);

    RuntimeVertex vertexSource = transition.getSourceVertex();
    RuntimeVertex vertexTarget = transition.getTargetVertex();

    //ez volt kommentelve akkor a sourcet az elején nem adja hozzá
    if(vertexSource.equals(vertexTarget)) {
      transitionStates.add(vertexSource);
      stateStatusMap.put(vertexSource, ElementStatus.VISITED);
    }
    else{
      transitionStates.add(vertexSource);
      transitionStates.add(vertexTarget);
      stateStatusMap.put(vertexSource, ElementStatus.VISITED);
      stateStatusMap.put(vertexTarget, ElementStatus.VISITED);
    }

    transitionPath.add(vertexSource);
    transitionPath.add(transition);
    transitionPath.add(vertexTarget);
    transitionStatusMap.put(transition, ElementStatus.VISITED);

    int index = 0;
    int outEdgesSize = context.getModel().getOutEdges(vertexTarget).size();
    boolean added = false;
    while(index < outEdgesSize && !added){
      RuntimeEdge edge = context.getModel().getOutEdges(vertexTarget).get(index);
      RuntimeVertex edgeSource = edge.getSourceVertex();
      RuntimeVertex edgeTarget = edge.getTargetVertex();
      if (!edgeSource.equals(edgeTarget)) {
        q.add(edge);
        added = true;
      }
      index++;
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
      transitionStatusMap.put(subTransition, ElementStatus.VISITED);
      if(!transitionStates.contains(subVertexTarget)) {
        transitionStates.add(subVertexTarget);
        stateStatusMap.put(subVertexTarget,ElementStatus.VISITED);
      }

      chooseNextEdge(q, transitionStatusMap, stateStatusMap, subVertexTarget);
    }
  }

  private void chooseNextEdge(Deque<RuntimeEdge> q, Map<Element, ElementStatus> transitionStatusMap, Map<Element, ElementStatus> stateStatusMap, RuntimeVertex subVertexTarget) {
    boolean unreachedEdge = false;
    boolean unreachedVertex = false;
    List<RuntimeEdge> outedges = context.getModel().getOutEdges(subVertexTarget);
    int i = 0;
    int j = 0;

    while (!unreachedVertex && i < outedges.size()) {
      if (stateStatusMap.get(outedges.get(i).getTargetVertex()).equals(ElementStatus.UNVISITED)) {
        unreachedVertex = true;
        unreachedEdge = true;
        q.add(outedges.get(i));
      }
      i++;
    }

    while (!unreachedEdge && j < outedges.size()) {
      if (transitionStatusMap.get(outedges.get(j)).equals(ElementStatus.UNVISITED)) {
        unreachedEdge = true;
        q.add(outedges.get(j));
      }
      j++;
    }

    if (!unreachedEdge) {
      for (RuntimeEdge edge : context.getModel().getOutEdges(subVertexTarget)) {
        RuntimeVertex vertexSource = edge.getSourceVertex();
        RuntimeVertex vertexTarget = edge.getTargetVertex();
        if (!vertexSource.equals(vertexTarget)) {
          q.add(edge);
          break;
        }
      }
    }
  }

  private void otherPathWithEqualStates(RuntimeEdge transition){
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


  private void transitionNeighboursAndPath(RuntimeEdge transition){
    List<Element> states = new ArrayList<>();
    Path<Element> transitionPath = new Path<>();
    RuntimeVertex vertexSource = transition.getSourceVertex();

    //get the transitionNeighbours for the given transition
    List<Element> neighbourTransitions = new ArrayList<>();
    for (RuntimeEdge edge : context.getModel().getOutEdges(vertexSource)) {
      if(!edge.equals(transition))
        neighbourTransitions.add(edge);
    }

    transitionNeighbours.put(transition, neighbourTransitions);
    transitionPath(transition,transitionPath,states);
    transitionPaths.put(transition,transitionPath);

  }

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

  private Path<Element> getFinalPath(RuntimeVertex root){

    Path<Element> finalPath = new Path<>();
    Deque<RuntimeEdge> q = new ArrayDeque<>();
    RuntimeEdge transition = context.getModel().getOutEdges(root).get(0);
    Map<Element, ElementStatus> transitionStatusMap = createElementStatusMap(allTransitionOfTheModel);
    Map<Element, ElementStatus> stateStatusMap = createElementStatusMap(states);
    List<Element> unreachedTransitions = new ArrayList<>(allTransitionOfTheModel);
    List<Element> unreachedStates = new ArrayList<>(states);


    RuntimeVertex vertexSource = transition.getSourceVertex();
    RuntimeVertex vertexTarget = transition.getTargetVertex();

    if(vertexSource.equals(vertexTarget)) {
      finalPath.add(vertexSource);
      finalPath.add(transition);
      stateStatusMap.put(vertexSource,ElementStatus.VISITED);
    }
    else{
      finalPath.add(vertexSource);
      finalPath.add(transition);
      finalPath.add(vertexTarget);
      stateStatusMap.put(vertexSource,ElementStatus.VISITED);
      stateStatusMap.put(vertexTarget,ElementStatus.VISITED);
    }

    transitionStatusMap.put(transition, ElementStatus.VISITED);
    unreachedTransitions.remove(transition);

    int index = 0;
    int outEdgesSize = context.getModel().getOutEdges(vertexTarget).size();
    boolean added = false;
    while(index < outEdgesSize && !added){
      RuntimeEdge edge = context.getModel().getOutEdges(vertexTarget).get(index);
      RuntimeVertex edgeSource = edge.getSourceVertex();
      RuntimeVertex edgeTarget = edge.getTargetVertex();
      if (!edgeSource.equals(edgeTarget)) {
        q.add(edge);
        added = true;
      }
      index++;
    }

    while (!q.isEmpty()) {

      RuntimeEdge subTransition = q.pop();
      RuntimeVertex subVertexTarget = subTransition.getTargetVertex();
      RuntimeVertex subVertexSource = subTransition.getSourceVertex();


      if(!transitionStatusMap.containsValue(ElementStatus.UNVISITED) && unreachedStates.isEmpty())
        return finalPath;

      finalPath.add(subTransition);
      finalPath.add(subVertexTarget);
      transitionStatusMap.put(subTransition, ElementStatus.VISITED);
      stateStatusMap.put(subVertexTarget,ElementStatus.VISITED);

      if(unreachedTransitions.size() == 1){
        unreachedStates.remove(subVertexSource);
        unreachedStates.remove(subVertexTarget);
      }else{
        unreachedTransitions.remove(subTransition);
      }

      chooseNextEdge(q, transitionStatusMap, stateStatusMap, subVertexTarget);
    }

    return finalPath;
  }

  private List<Path<Element>> getTestSet(Path<Element> finalPath, Map<Element, Path<Element>> alterTransitionPaths, Element start){
    List<Path<Element>> testSet = new ArrayList<>();
    removeDuplicatePaths(alterTransitionPaths);
    testSet.add(finalPath);
    addOtherPaths(testSet,(RuntimeVertex) start);

    return testSet;
  }

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
            //newPath.add(subElement);
            if( !states.contains(subElement) && subElement instanceof RuntimeVertex) {
              states.add(subElement);
              newPath.add(subElement);
            }
            else
              newPath.add(subElement);
          }
        }else {
          newPath.add(element);

          if(element instanceof RuntimeVertex && !states.contains(element))
            states.add(element);
        }
      }

      Iterator<Path<Element>> it = testSet.iterator();
      boolean l = false;
      List<Element> a = new ArrayList<>(newPath);
      List<Element> b;
      while (it.hasNext() && !l){
        b = new ArrayList<>(it.next());
        if(equalsIgnore(a,b)) {
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
