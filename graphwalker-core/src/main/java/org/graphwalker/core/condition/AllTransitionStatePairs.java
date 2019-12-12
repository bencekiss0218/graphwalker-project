package org.graphwalker.core.condition;

import org.graphwalker.core.algorithm.AllTransitionState;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.model.Path;

import java.util.List;

public class AllTransitionStatePairs extends StopConditionBase{

  public AllTransitionStatePairs() {
    super("");
  }

  public long getLength() {
    return getContext().getAlgorithm(AllTransitionState.class).getTestSet().get(0).size();
  }

  @Override
  public boolean isFulfilled() {
    return getFulfilment() >= FULFILLMENT_LEVEL && super.isFulfilled();
  }

  @Override
  public double getFulfilment() {

    long length = 0;
    List<Path<Element>> testSet = getContext().getAlgorithm(AllTransitionState.class).getTestSet();
    if(!testSet.isEmpty()){
      length = testSet.get(0).size();
    }

    if(length == 0){
      length = 2;
    }

    return (double) getContext().getProfiler().getTotalVisitCount() / length;
  }
}

