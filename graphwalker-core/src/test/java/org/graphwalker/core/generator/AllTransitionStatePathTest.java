package org.graphwalker.core.generator;

import org.graphwalker.core.condition.EdgeCoverage;
import org.graphwalker.core.condition.Length;
import org.graphwalker.core.machine.Context;
import org.graphwalker.core.machine.TestExecutionContext;
import org.graphwalker.core.model.*;
import org.graphwalker.core.statistics.SimpleProfiler;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AllTransitionStatePathTest {

  @Test
  public void path() throws Exception {
    Vertex s1 = new Vertex().setId("start");
    s1.setName("s1");
    Vertex s2 = new Vertex().setName("s2");
    Edge t1 = new Edge().setName("t1").setSourceVertex(s1).setTargetVertex(s2);
   // Edge t2 = new Edge().setName("t2").setSourceVertex(s1).setTargetVertex(s1);
   // Edge t3 = new Edge().setName("t3").setSourceVertex(s1).setTargetVertex(s2);
   // Edge t4 = new Edge().setName("t4").setSourceVertex(s2).setTargetVertex(s1);
   // Edge t5 = new Edge().setName("t5").setSourceVertex(s2).setTargetVertex(s2);
    Model model = new Model().addEdge(t1);//.addEdge(t2).addEdge(t3).addEdge(t4).addEdge(t5);
    long length = 3;
    //Context context = new TestExecutionContext(model, new AllTransitionSatePath(new EdgeCoverage(100)));
    Context context = new TestExecutionContext(model, new AllTransitionSatePath(new Length(length)));
    context.setProfiler(new SimpleProfiler());
    Deque<Builder<? extends Element>> expectedElements = new ArrayDeque<>(
      Arrays.asList(s1, t1, s2)
      //Arrays.asList(s1, t2, s1, t3, s2, t4, s1, t1, s2)
    );
    context.setCurrentElement(context.getModel().getElementById("start"));

    execute(context, expectedElements);
    assertTrue(expectedElements.isEmpty());
  }

  private void execute(Context context, Deque<Builder<? extends Element>> expectedElements) {
    while (context.getPathGenerator().hasNextStep()) {
      context.getPathGenerator().getNextStep();
      context.getProfiler().start(context);
      context.getProfiler().stop(context);
      Element first = expectedElements.removeFirst().build();
      Element second = context.getCurrentElement();
      System.out.println("FIRST: " + first + "SECOND: " + second);
      assertEquals(first, second);
    }
  }

}
