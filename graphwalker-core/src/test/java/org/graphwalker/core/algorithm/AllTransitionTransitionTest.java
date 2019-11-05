package org.graphwalker.core.algorithm;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.graphwalker.core.graphgenerator.RandomGraphGenerator;
import org.graphwalker.core.machine.TestExecutionContext;
import org.graphwalker.core.model.*;
import org.junit.Test;

import java.util.*;

public class AllTransitionTransitionTest {
  private static final Vertex v00 = new Vertex().setName("v00");
  private static final Vertex v01 = new Vertex().setName("v01");
  private static final Vertex v10 = new Vertex().setName("v10");
  private static final Vertex v20 = new Vertex().setName("v20");
  private static final Vertex v31 = new Vertex().setName("v31");

  private static final Edge e1 = new Edge().setName("e1").setSourceVertex(v00).setTargetVertex(v01);
  private static final Edge e2 = new Edge().setName("e2").setSourceVertex(v00).setTargetVertex(v10);
  private static final Edge e3 = new Edge().setName("e3").setSourceVertex(v10).setTargetVertex(v20);
  private static final Edge e4 = new Edge().setName("e4").setSourceVertex(v20).setTargetVertex(v31);
  private static final Edge e5 = new Edge().setName("e5").setSourceVertex(v01).setTargetVertex(v31);


  private static final Vertex ve0 = new Vertex().setName("ve0");
  private static final Vertex ve1 = new Vertex().setName("ve1");
  private static final Edge ee1 = new Edge().setName("e1").setSourceVertex(ve0).setTargetVertex(ve1);

  //robodog
  private static final Vertex s1 = new Vertex().setName("s1");
  private static final Vertex s2 = new Vertex().setName("s2");
  private static final Edge t1 = new Edge().setName("t1").setSourceVertex(s1).setTargetVertex(s2);
  private static final Edge t2 = new Edge().setName("t2").setSourceVertex(s1).setTargetVertex(s1);
  private static final Edge t3 = new Edge().setName("t3").setSourceVertex(s1).setTargetVertex(s2);
  private static final Edge t4 = new Edge().setName("t4").setSourceVertex(s2).setTargetVertex(s1);
  private static final Edge t5 = new Edge().setName("t5").setSourceVertex(s2).setTargetVertex(s2);
  private static final Edge t6 = new Edge().setName("t6").setSourceVertex(s2).setTargetVertex(s1);

  private static final RandomGraphGenerator rgg = new RandomGraphGenerator();
  private static final Model modell = rgg.generateRandomGraph(4,2,2);
  private static final Vertex vertex = modell.getVertices().get(0);

  private static final Model model = new Model()
    .addEdge(e1)
    .addEdge(e2)
    .addEdge(e3)
    .addEdge(e4)
    .addEdge(e5)
    .addEdge(t1)
    .addEdge(t2)
    .addEdge(t3)
    .addEdge(t4)
    .addEdge(t5);
  //.addEdge(t6);


  @Test
  public void testSet() throws Exception {
    AllTransitionTransition allTransitionTransition = new AllTransitionTransition(new TestExecutionContext().setModel(modell.build()));

    System.out.println(allTransitionTransition.returnTransitionTransitions(vertex.build()));

  }


}
