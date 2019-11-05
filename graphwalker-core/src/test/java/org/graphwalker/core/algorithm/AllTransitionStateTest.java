package org.graphwalker.core.algorithm;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.graphwalker.core.graphgenerator.RandomGraphGenerator;
import org.graphwalker.core.machine.TestExecutionContext;
import org.graphwalker.core.model.*;
import org.junit.Test;

import java.util.*;

public class AllTransitionStateTest {

  //robodog
  private static final Vertex s1 = new Vertex().setName("s1");
  private static final Vertex s2 = new Vertex().setName("s2");
  private static final Edge t1 = new Edge().setName("t1").setSourceVertex(s1).setTargetVertex(s2);
  private static final Edge t2 = new Edge().setName("t2").setSourceVertex(s1).setTargetVertex(s1);
  private static final Edge t3 = new Edge().setName("t3").setSourceVertex(s1).setTargetVertex(s2);
  private static final Edge t4 = new Edge().setName("t4").setSourceVertex(s2).setTargetVertex(s1);
  private static final Edge t5 = new Edge().setName("t5").setSourceVertex(s2).setTargetVertex(s2);
  private static final Edge t6 = new Edge().setName("t6").setSourceVertex(s2).setTargetVertex(s1);

  private static final Vertex v0 = new Vertex().setName("v0");
  private static final Vertex v1 = new Vertex().setName("v1");
  private static final Vertex v2 = new Vertex().setName("v2");
  private static final Edge e0 = new Edge().setName("e0").setSourceVertex(v0).setTargetVertex(v1);
  private static final Edge e1 = new Edge().setName("e1").setSourceVertex(v1).setTargetVertex(v2);
  private static final Edge e2 = new Edge().setName("e2").setSourceVertex(v2).setTargetVertex(v0);
  private static final Edge e3 = new Edge().setName("e3").setSourceVertex(v0).setTargetVertex(v1);
  private static final Edge e4 = new Edge().setName("e4").setSourceVertex(v1).setTargetVertex(v0);
  private static final Edge e5 = new Edge().setName("e5").setSourceVertex(v2).setTargetVertex(v1);

  //small
  private static final Vertex k1 = new Vertex().setName("k1");
  private static final Vertex k2 = new Vertex().setName("k2");
  private static final Vertex k3 = new Vertex().setName("k3");
  private static final Edge l1 = new Edge().setName("l1").setSourceVertex(k1).setTargetVertex(k2);
  private static final Edge l2 = new Edge().setName("l2").setSourceVertex(k2).setTargetVertex(k1);
  private static final Edge l3 = new Edge().setName("l3").setSourceVertex(k2).setTargetVertex(k3);



  private static final RandomGraphGenerator rgg = new RandomGraphGenerator();
  private static final Model modell = rgg.generateRandomGraph(4,2,2);
  private static final Vertex vert = modell.getVertices().get(0);

  private static final Model model = new Model()
    .addEdge(l1)
    .addEdge(l2)
    .addEdge(l3)
    .addEdge(e0)
    .addEdge(e1)
    .addEdge(e2)
    .addEdge(e3)
    .addEdge(e4)
    .addEdge(e5)
    .addEdge(t1)
    .addEdge(t2)
    .addEdge(t3)
    .addEdge(t4)
    .addEdge(t5)
    .addEdge(t6);


  @Test
  public void testSet() throws Exception {
    AllTransitionState allTransitionState = new AllTransitionState(new TestExecutionContext().setModel(model.build()));

    List<Edge> edges = model.getEdges();
    for(Edge e : edges){
      System.out.println("---EDGE--- " + e.build() + " SOURCE----- " + e.build().getSourceVertex() + " TARGET ---- " + e.build().getTargetVertex());
    }

    System.out.println(allTransitionState.returnTestSet(k1.build()));

  }


}
