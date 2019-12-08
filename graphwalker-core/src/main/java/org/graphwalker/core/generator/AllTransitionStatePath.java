package org.graphwalker.core.generator;

import org.graphwalker.core.algorithm.AllTransitionState;
import org.graphwalker.core.condition.StopCondition;
import org.graphwalker.core.machine.Context;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.model.Path;

import java.util.ArrayList;
import java.util.List;

import static org.graphwalker.core.common.Objects.isNull;

public class AllTransitionStatePath extends PathGeneratorBase<StopCondition> {

  private Path<Element> path = null;
  //private int i = 0;

  //for cumulation
  double start;
  int sumTestSet;
  int sumPath;

  public AllTransitionStatePath(StopCondition stopCondition) {

    start = System.nanoTime();
    setStopCondition(stopCondition);

  }

  @Override
  public Context getNextStep(){

    //System.out.println("Step number is: " + i);
    //i++;
    Context context = super.getNextStep();
    if (isNull(path)) {
      path = getPath(context);
    }

    context.setCurrentElement(path.removeFirst());
    return context;
  }

  private Path<Element> getPath(Context context) {
    List<Path<Element>> paths;
    Path<Element> finalPath = new Path<>();
    paths = context.getAlgorithm(AllTransitionState.class).returnTestSet(context.getCurrentElement());

    sumPath = paths.get(0).size();

    for(Path<Element> path : paths){
      finalPath.addAll(path);
    }

    sumTestSet = finalPath.size();

    return finalPath;
  }

  @Override
  public boolean hasNextStep() {

    if(getStopCondition().isFulfilled()){
      double end = System.nanoTime() - start;
      System.out.println("Size of the AllTransitionState criterium path: " + sumPath);
      System.out.println("Size of the Test set: " + sumTestSet);
      System.out.println("Estimated time is: " + end / 1000000000);
      return false;
    }

    return !getStopCondition().isFulfilled();
  }
}
