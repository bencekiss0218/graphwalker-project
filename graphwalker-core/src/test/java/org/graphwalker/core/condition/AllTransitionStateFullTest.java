package org.graphwalker.core.condition;

import org.graphwalker.core.algorithm.AllTransitionState;
import org.graphwalker.core.generator.AllTransitionStatePath;
import org.graphwalker.core.machine.Context;
import org.graphwalker.core.machine.TestExecutionContext;
import org.graphwalker.core.model.*;
import org.graphwalker.core.statistics.SimpleProfiler;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class AllTransitionStateFullTest {

  private Vertex v0 = new Vertex().setName("v0").setId("start");
  private Vertex v1 = new Vertex().setName("v1");
  private Vertex v2 = new Vertex().setName("v2");
  private Vertex v3 = new Vertex().setName("v3");
  private Edge e0 = new Edge().setName("e0").setSourceVertex(v0).setTargetVertex(v1);
  private Edge e1 = new Edge().setName("e1").setSourceVertex(v1).setTargetVertex(v2);
  private Edge e2 = new Edge().setName("e2").setSourceVertex(v2).setTargetVertex(v3);
  private Edge e3 = new Edge().setName("e3").setSourceVertex(v3).setTargetVertex(v0);
  private Edge e4 = new Edge().setName("e4").setSourceVertex(v0).setTargetVertex(v1);
  private Edge e5 = new Edge().setName("e5").setSourceVertex(v1).setTargetVertex(v0);
  private Edge e6 = new Edge().setName("e6").setSourceVertex(v2).setTargetVertex(v1);
  private Edge e7 = new Edge().setName("e7").setSourceVertex(v3).setTargetVertex(v1);

  @Test
  public void testFulfilment() throws Exception {
    Model model = new Model().addEdge(e0).addEdge(e1).addEdge(e2).addEdge(e3).addEdge(e4).addEdge(e5).addEdge(e6).addEdge(e7);
    StopCondition condition = new AllTransitionStateFull();
    Context context = new TestExecutionContext(model, new AllTransitionStatePath(condition)).setCurrentElement(v0.build());
    context.setProfiler(new SimpleProfiler());
    Deque<Builder<? extends Element>> expectedElements = new ArrayDeque<>(
      Arrays.asList(v0, e0, v1, e1, v2, e2, v3, e3, v0, e4, v1, e5, v0, e0, v1, e1, v2, e6, v1, e1, v2, e2, v3, e7, v1, e1, v2, e2, v3, e3, v0,
        v0, e4, v1, e1, v2, e2, v3, v0, e0, v1, e1, v2, e2, v3)
    );
    context.setCurrentElement(context.getModel().getElementById("start"));
    while (context.getPathGenerator().hasNextStep()) {
      context.getPathGenerator().getNextStep();
      context.getProfiler().start(context);
      context.getProfiler().stop(context);
    }
    assertThat((int)(condition.getFulfilment()), is(1));
  }

  @Test
  public void testIsFulfilled() throws Exception {
    Model model = new Model().addEdge(e0).addEdge(e1).addEdge(e2).addEdge(e3).addEdge(e4).addEdge(e5).addEdge(e6).addEdge(e7);
    StopCondition condition = new AllTransitionStateFull();
    Context context = new TestExecutionContext(model, new AllTransitionStatePath(condition)).setCurrentElement(v0.build());
    context.setProfiler(new SimpleProfiler());
    Deque<Builder<? extends Element>> expectedElements = new ArrayDeque<>(
      Arrays.asList(v0, e0, v1, e1, v2, e2, v3, e3, v0, e4, v1, e5, v0, e0, v1, e1, v2, e6, v1, e1, v2, e2, v3, e7, v1, e1, v2, e2, v3, e3, v0,
        v0, e4, v1, e1, v2, e2, v3, v0, e0, v1, e1, v2, e2, v3)
    );

    context.setCurrentElement(context.getModel().getElementById("start"));
    while (context.getPathGenerator().hasNextStep()) {
      context.getPathGenerator().getNextStep();
      context.getProfiler().start(context);
      context.getProfiler().stop(context);
    }
    assertTrue(condition.isFulfilled());
  }
}
