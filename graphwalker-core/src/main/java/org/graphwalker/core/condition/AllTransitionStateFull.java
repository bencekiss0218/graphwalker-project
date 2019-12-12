package org.graphwalker.core.condition;

import org.graphwalker.core.algorithm.AllTransitionState;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.model.Path;

import java.util.List;

public class AllTransitionStateFull extends StopConditionBase{

  boolean stop = false;

  public AllTransitionStateFull() {
    super("");
  }

  public long getLength() {
    return getContext().getAlgorithm(AllTransitionState.class).getTestSet().size();
  }

  @Override
  public boolean isFulfilled() {
    return getFulfilment() >= FULFILLMENT_LEVEL && super.isFulfilled();
  }

  @Override
  public double getFulfilment() {

    long length = 0;

    List<Path<Element>> testSet = getContext().getAlgorithm(AllTransitionState.class).getTestSet();
    for(Path<Element> p : testSet){
      stop = true;
      length += p.size();
    }

    if(length == 0 && !stop){
      length = 2;
    }


    return (double) getContext().getProfiler().getTotalVisitCount() / length;
  }
}

