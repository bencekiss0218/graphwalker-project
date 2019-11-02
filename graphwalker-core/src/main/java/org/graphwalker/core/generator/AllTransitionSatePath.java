package org.graphwalker.core.generator;

import org.graphwalker.core.algorithm.AllTransitionState;
import org.graphwalker.core.condition.StopCondition;
import org.graphwalker.core.machine.Context;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.model.Path;

import java.util.ArrayList;
import java.util.List;

import static org.graphwalker.core.common.Objects.isNull;

public class AllTransitionSatePath extends PathGeneratorBase<StopCondition> {

  private Path<Element> path = null;

  public AllTransitionSatePath(StopCondition stopCondition) {
    setStopCondition(stopCondition);
  }

  @Override
  public Context getNextStep(){
    Context context = super.getNextStep();
    if (isNull(path)) {
      path = getPath(context);
    }
    context.setCurrentElement(path.removeFirst());
    return context;
  }

  private Path<Element> getPath(Context context) {
    List<Path<Element>> paths;
    paths = context.getAlgorithm(AllTransitionState.class).returnTestSet(context.getCurrentElement());
    return paths.get(0);
  }

  @Override
  public boolean hasNextStep() {
    return !getStopCondition().isFulfilled();
  }
}
