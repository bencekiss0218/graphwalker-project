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

  private void allReachableStates(Element root){

    Path<Element> finalPath;
    allTransitionOfTheModel = fillTransitionsWithBreadthFirstSearch(root);
    states = fillStatesWithBreadthFirstSearch(root);

    System.out.println("STATES: " + states);

    System.out.println("transitions are: " + allTransitionOfTheModel);


    for(Element elem : allTransitionOfTheModel){
      //ez jobb megoldás a kommentelt
      List<Element> reachedStates = getReachableStates((RuntimeEdge) elem);
      transitionStates.put(elem,reachedStates);
    }

    for(Element elem : allTransitionOfTheModel){
      transitionStatesNeighboursAndPath((RuntimeEdge) elem);
    }
    System.out.println("States for each transitions: " + transitionStates);
    System.out.println("Path for each transition BEFORE " + transitionPaths);
    getPathForTransitions(allTransitionOfTheModel);
    System.out.println("Path for each transition AFTER " + otherTransitionPaths);
    System.out.println("Neighbours for each transition: " + transitionNeighbours);
    //összefűzzük az élidegen utakat
    finalPath = getFinalPath(otherTransitionPaths);
    System.out.println("FINAL PATH: " + finalPath);
    testSet = getTestSet(finalPath,transitionPaths,transitionStates,states, root);
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

  private Path<Element> getFinalPath(Map<Element,Path<Element>> transitionPaths){

    //System.out.println("transition states: " + transitionStates);
    //System.out.println("TransitionPaths beginning: " + transitionPaths);
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


  private List<Path<Element>> getTestSet(Path<Element> finalPath, Map<Element,Path<Element>> alterTransitionPaths, Map<Element,List<Element>> transitionStates, List<Element> states, Element start){
    List<Path<Element>> testSet = new ArrayList<>();
    List<Path<Element>> optionalTestSet = new ArrayList<>();
    List<Element> copyOfFinalPath = new ArrayList<>(finalPath);

    testSet.add(finalPath);

    for(Element key : alterTransitionPaths.keySet()){

      //System.out.println("A: " + transitionStates.get(key)  + "B: " +states ) ;
      if(equalsIgnore(transitionStates.get(key),states) ) {

        List<Element> copyOfalterTransitionPaths = new ArrayList<>(alterTransitionPaths.get(key));
        int contains = Collections.indexOfSubList(copyOfFinalPath, copyOfalterTransitionPaths);

        /*if( contains == -1 ) {
          if(alterTransitionPaths.get(key).getFirst().equals(start)) {
            System.out.println(alterTransitionPaths.get(key) + " ADDED TO TESTSET");
            testSet.add(alterTransitionPaths.get(key));
          }
          else
            optionalTestSet.add(alterTransitionPaths.get(key));
        }*/
        //ha meg nem volt benne akkor adjuk hozza az optionaltestset hez majd egyesével megnézem hogy az optionaltestsetben levő pathok önmagukban startbol lefedik az összes csucsot,
        //akkor hozzaadom a testsethez, majd összefűzöm a maradék csúcsot
        if(contains == -1){
          if(alterTransitionPaths.get(key).getFirst().equals(start)) {
            System.out.println(alterTransitionPaths.get(key) + " ADDED TO TESTSET");
            testSet.add(alterTransitionPaths.get(key));
          }
          optionalTestSet.add(alterTransitionPaths.get(key));
        }
      }
    }

    Set<Path<Element>> toAdd = new HashSet<>();
    for(Path<Element> path : optionalTestSet){
      int i = 0;
      Path<Element> newPath = new Path<>();
      List<Element> newstates = new ArrayList<>();
      boolean isStart = false;
      Iterator<Element> iter = path.iterator();

      while(iter.hasNext()){
        Element item = iter.next();
        if(item.equals(start) && i > 0){
          isStart = true;
        }
        if(isStart){
          if(item instanceof RuntimeVertex && !newstates.contains(item)){
            newstates.add(item);
          }
          newPath.add(item);
        }
        i++;
      }
      if(equalsIgnore(newstates,states))
        toAdd.add(newPath);
    }

    System.out.println(toAdd  + " <- TOADD");
    if(!optionalTestSet.contains(toAdd))
      optionalTestSet.addAll(toAdd);

    Path<Element> mergedOptionalPath = new Path<>();
    Element startVertex;
    Element endVertex;

    int key = 0;
    for(int i = 0;i<optionalTestSet.size();i++){
      if(optionalTestSet.get(i).getFirst().equals(start)){
        key = i;
        break;
      }
    }

    mergedOptionalPath.addAll(optionalTestSet.get(key));
    for(int i = 0; i < optionalTestSet.size();i++) {
      endVertex = optionalTestSet.get(key).getLast();
      for (int j = 0; j < optionalTestSet.size(); j++) {
        if (i == j) {
          continue;
        }
        startVertex = optionalTestSet.get(j).getFirst();

        if (endVertex.equals(startVertex)) {
          Path<Element> pathToAdd = new Path<>(optionalTestSet.get(j));
          pathToAdd.removeFirst();

          List<Element> copyOfMergedOptionalPath = new ArrayList<>(mergedOptionalPath);
          List<Element> copyOfPathToAdd = new ArrayList<>(pathToAdd);
          int contains = Collections.indexOfSubList(copyOfMergedOptionalPath, copyOfPathToAdd);
          if (contains == -1) {
            mergedOptionalPath.addAll(pathToAdd);
          }
          key = j;
          break;
        }
      }
    }

    System.out.println("OPTIONAL TESTSET : " + optionalTestSet);
    testSet.add(mergedOptionalPath);

    addNotAddedTransitionsToTestSet(testSet,(RuntimeVertex) start);

    return testSet;
  }

  private void addNotAddedTransitionsToTestSet(List<Path<Element>> testSet, RuntimeVertex root){
    List<Element> transitions = new ArrayList<>(allTransitionOfTheModel);
    for(Path<Element> path : testSet){
      for(Element element : path){
        if(transitions.contains(element)){
          transitions.remove(element);
        }
      }
    }


    System.out.println("Transitions that have not been added yet: " + transitions);

    for(Element transition : transitions){
      Element firstOutEdge = context.getModel().getOutEdges(root).get(0);
      Path<Element> newPath = new Path<>();
      boolean foundVertex = false;
      Path<Element> transitionPath = transitionPaths.get(transition);
      Element startVertex = transitionPath.getFirst();
      Path<Element> firstPath = transitionPaths.get(firstOutEdge);
      Iterator<Element> iterator = firstPath.iterator();
      while(iterator.hasNext() && !foundVertex){
        Element element = iterator.next();
        if(element.equals(startVertex)){
          foundVertex = true;
          newPath.addAll(transitionPath);
        }else {
          newPath.add(element);
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
