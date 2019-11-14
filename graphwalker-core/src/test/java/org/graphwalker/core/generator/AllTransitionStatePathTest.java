package org.graphwalker.core.generator;

import org.graphwalker.core.condition.EdgeCoverage;
import org.graphwalker.core.condition.Length;
import org.graphwalker.core.condition.TimeDuration;
import org.graphwalker.core.machine.Context;
import org.graphwalker.core.machine.TestExecutionContext;
import org.graphwalker.core.model.*;
import org.graphwalker.core.statistics.SimpleProfiler;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AllTransitionStatePathTest {

  @Test
  public void path() throws Exception {
    /*Vertex s1 = new Vertex().setId("start");
    s1.setName("s1");
    Vertex s2 = new Vertex().setName("s2");
    Edge t1 = new Edge().setName("t1").setSourceVertex(s1).setTargetVertex(s2);*/
   // Edge t2 = new Edge().setName("t2").setSourceVertex(s1).setTargetVertex(s1);
   // Edge t3 = new Edge().setName("t3").setSourceVertex(s1).setTargetVertex(s2);
   // Edge t4 = new Edge().setName("t4").setSourceVertex(s2).setTargetVertex(s1);
   // Edge t5 = new Edge().setName("t5").setSourceVertex(s2).setTargetVertex(s2);

    Vertex v0 = new Vertex().setName("v0").setId("start");
    Vertex v1 = new Vertex().setName("v1");
    Vertex v2 = new Vertex().setName("v2");
    Vertex v3 = new Vertex().setName("v3");
    Edge e0 = new Edge().setName("e0").setSourceVertex(v0).setTargetVertex(v1);
    Edge e1 = new Edge().setName("e1").setSourceVertex(v1).setTargetVertex(v2);
    Edge e2 = new Edge().setName("e2").setSourceVertex(v2).setTargetVertex(v3);
    Edge e3 = new Edge().setName("e3").setSourceVertex(v3).setTargetVertex(v0);
    Edge e4 = new Edge().setName("e4").setSourceVertex(v0).setTargetVertex(v1);
    Edge e5 = new Edge().setName("e5").setSourceVertex(v1).setTargetVertex(v0);
    Edge e6 = new Edge().setName("e6").setSourceVertex(v2).setTargetVertex(v1);
    Edge e7 = new Edge().setName("e7").setSourceVertex(v3).setTargetVertex(v1);


    Model model = new Model().addEdge(e0).addEdge(e1).addEdge(e2).addEdge(e3).addEdge(e4).addEdge(e5).addEdge(e6).addEdge(e7);
    long length = 38;
    Context context = new TestExecutionContext(model, new AllTransitionSatePath(new Length(length)));
    context.setProfiler(new SimpleProfiler());
    Deque<Builder<? extends Element>> expectedElements = new ArrayDeque<>(
      Arrays.asList(v0, e4, v1, e1, v2, e2, v3, e3, v0, e0, v1, e1, v2, e2, v3, e3, v0, e0, v1, e1, v2, e2, v3, e3, v0, e0, v1, e1, v2, e2, v3,v0, e0, v1, e1, v2, e2, v3)
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
