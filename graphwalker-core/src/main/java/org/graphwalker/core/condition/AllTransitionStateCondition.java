package org.graphwalker.core.condition;

import org.graphwalker.core.algorithm.AllTransitionState;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.model.Path;

import java.util.List;

public class AllTransitionStateCondition extends StopConditionBase{

  private long length;

  public AllTransitionStateCondition() {
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
    length = 0;
    List<Path<Element>> testSet = getContext().getAlgorithm(AllTransitionState.class).getTestSet();
    for(Path<Element> p : testSet){
      length += p.size();
    }

    return (double) getContext().getProfiler().getTotalVisitCount() / length;
  }
}

