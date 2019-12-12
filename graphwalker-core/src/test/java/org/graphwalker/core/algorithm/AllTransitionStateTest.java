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
  private static final Vertex s3 = new Vertex().setName("s3");
  private static final Vertex s4 = new Vertex().setName("s4");
  private static final Vertex s5 = new Vertex().setName("s5");
  private static final Edge t1 = new Edge().setName("t1").setSourceVertex(s1).setTargetVertex(s2);
  private static final Edge t2 = new Edge().setName("t2").setSourceVertex(s1).setTargetVertex(s1);
  private static final Edge t3 = new Edge().setName("t3").setSourceVertex(s1).setTargetVertex(s2);
  private static final Edge t4 = new Edge().setName("t4").setSourceVertex(s2).setTargetVertex(s1);
  private static final Edge t5 = new Edge().setName("t5").setSourceVertex(s2).setTargetVertex(s2);
  private static final Edge t6 = new Edge().setName("t6").setSourceVertex(s1).setTargetVertex(s3);
  private static final Edge t7 = new Edge().setName("t7").setSourceVertex(s3).setTargetVertex(s2);
  private static final Edge t8 = new Edge().setName("t8").setSourceVertex(s3).setTargetVertex(s4);
  private static final Edge t9 = new Edge().setName("t9").setSourceVertex(s4).setTargetVertex(s3);
  private static final Edge t10 = new Edge().setName("t10").setSourceVertex(s4).setTargetVertex(s5);
  private static final Edge t11 = new Edge().setName("t11").setSourceVertex(s5).setTargetVertex(s4);
  //private static final Edge t8 = new Edge().setName("t8").setSourceVertex(s3).setTargetVertex(s3);
  //private static final Edge t9 = new Edge().setName("t9").setSourceVertex(s2).setTargetVertex(s3);
  //private static final Edge t10 = new Edge().setName("t10").setSourceVertex(s3).setTargetVertex(s1);

  private static final Vertex v0 = new Vertex().setName("v0");
  private static final Vertex v1 = new Vertex().setName("v1");
  private static final Vertex v2 = new Vertex().setName("v2");
  private static final Vertex v3 = new Vertex().setName("v3");
  private static final Edge e0 = new Edge().setName("e0").setSourceVertex(v0).setTargetVertex(v1);
  private static final Edge e1 = new Edge().setName("e1").setSourceVertex(v1).setTargetVertex(v2);
  private static final Edge e6 = new Edge().setName("e6").setSourceVertex(v2).setTargetVertex(v1);
  private static final Edge e2 = new Edge().setName("e2").setSourceVertex(v2).setTargetVertex(v3);
  private static final Edge e3 = new Edge().setName("e3").setSourceVertex(v3).setTargetVertex(v0);
  private static final Edge e4 = new Edge().setName("e4").setSourceVertex(v0).setTargetVertex(v1);
  private static final Edge e5 = new Edge().setName("e5").setSourceVertex(v1).setTargetVertex(v0);
  private static final Edge e7 = new Edge().setName("e7").setSourceVertex(v3).setTargetVertex(v1);

  //problem
  private static final Vertex j0 = new Vertex().setName("v0");
  private static final Vertex j1 = new Vertex().setName("v1");
  private static final Vertex j2 = new Vertex().setName("v2");
  private static final Edge q0 = new Edge().setName("e0").setSourceVertex(j0).setTargetVertex(j1);
  private static final Edge q1 = new Edge().setName("e1").setSourceVertex(j1).setTargetVertex(j2);
  private static final Edge q2 = new Edge().setName("e2").setSourceVertex(j2).setTargetVertex(j0);
  private static final Edge q3 = new Edge().setName("e3").setSourceVertex(j0).setTargetVertex(j1);
  private static final Edge q4 = new Edge().setName("e4").setSourceVertex(j1).setTargetVertex(j0);
  private static final Edge q5 = new Edge().setName("e5").setSourceVertex(j2).setTargetVertex(j1);

  //small
  private static final Vertex k1 = new Vertex().setName("k1");
  private static final Vertex k2 = new Vertex().setName("k2");
  private static final Vertex k3 = new Vertex().setName("k3");
  private static final Edge l1 = new Edge().setName("l1").setSourceVertex(k1).setTargetVertex(k2);
  private static final Edge l2 = new Edge().setName("l2").setSourceVertex(k2).setTargetVertex(k1);
  private static final Edge l3 = new Edge().setName("l3").setSourceVertex(k2).setTargetVertex(k3);

  //new
  private static final Vertex b0 = new Vertex().setName("b0");
  private static final Vertex b1 = new Vertex().setName("b1");
  private static final Vertex b2 = new Vertex().setName("b2");
  private static final Vertex b3 = new Vertex().setName("b3");
  private static final Vertex b4 = new Vertex().setName("b4");

  private static final Edge f0 = new Edge().setName("f0").setSourceVertex(b0).setTargetVertex(b1);
  private static final Edge f1 = new Edge().setName("f1").setSourceVertex(b1).setTargetVertex(b2);
  private static final Edge f2 = new Edge().setName("f2").setSourceVertex(b2).setTargetVertex(b3);
  private static final Edge f3 = new Edge().setName("f3").setSourceVertex(b3).setTargetVertex(b4);
  private static final Edge f4 = new Edge().setName("f4").setSourceVertex(b4).setTargetVertex(b0);
  private static final Edge f5 = new Edge().setName("f5").setSourceVertex(b0).setTargetVertex(b0);
  private static final Edge f6 = new Edge().setName("f6").setSourceVertex(b0).setTargetVertex(b3);
  private static final Edge f7 = new Edge().setName("f7").setSourceVertex(b0).setTargetVertex(b2);
  private static final Edge f8 = new Edge().setName("f8").setSourceVertex(b0).setTargetVertex(b2);
  private static final Edge f9 = new Edge().setName("f9").setSourceVertex(b1).setTargetVertex(b1);
  private static final Edge f10 = new Edge().setName("f10").setSourceVertex(b2).setTargetVertex(b2);
  private static final Edge f11 = new Edge().setName("f11").setSourceVertex(b2).setTargetVertex(b2);
  private static final Edge f12 = new Edge().setName("f12").setSourceVertex(b2).setTargetVertex(b1);
  private static final Edge f13 = new Edge().setName("f13").setSourceVertex(b2).setTargetVertex(b2);
  private static final Edge f14 = new Edge().setName("f14").setSourceVertex(b2).setTargetVertex(b2);
  private static final Edge f15 = new Edge().setName("f15").setSourceVertex(b2).setTargetVertex(b3);
  private static final Edge f16 = new Edge().setName("f16").setSourceVertex(b4).setTargetVertex(b1);
  private static final Edge f17= new Edge().setName("f17").setSourceVertex(b4).setTargetVertex(b0);
  private static final Edge f18= new Edge().setName("f18").setSourceVertex(b4).setTargetVertex(b0);



  private static final RandomGraphGenerator rgg = new RandomGraphGenerator();
  private static final Model modell = rgg.generateRandomGraph(20,2,12, 0);
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
    .addEdge(e6)
    .addEdge(e7)

    .addEdge(t1)
    .addEdge(t2)
    .addEdge(t3)
    .addEdge(t4)
    .addEdge(t5)
   .addEdge(t6)
   .addEdge(t7)
   .addEdge(t8)
   .addEdge(t9)
    .addEdge(t10)
    .addEdge(t11)
    .addEdge(q0)
    .addEdge(q1)
    .addEdge(q2)
    .addEdge(q3)
    .addEdge(q4)
    .addEdge(q5)



    .addEdge(f0)
    .addEdge(f1)
    .addEdge(f2)
    .addEdge(f3)
    .addEdge(f4)
    .addEdge(f5)
    .addEdge(f6)
    .addEdge(f7)
    .addEdge(f8)
    .addEdge(f9)
    .addEdge(f10)
    .addEdge(f11)
    .addEdge(f12)
    .addEdge(f13)
    .addEdge(f14)
    .addEdge(f15)
    .addEdge(f16)
    .addEdge(f17)
    .addEdge(f18);


  @Test
  public void testSet() throws Exception {
    AllTransitionState allTransitionState = new AllTransitionState(new TestExecutionContext().setModel(modell.build()));

    List<Edge> edges = modell.getEdges();
    for(Edge e : edges){
      System.out.println("---EDGE--- " + e.build() + " SOURCE----- " + e.build().getSourceVertex() + " TARGET ---- " + e.build().getTargetVertex());
    }

    System.out.println(allTransitionState.returnTestSet(vert.build()));
  }

  @Test
  public void testReachableStates() throws Exception{
    AllTransitionState allTransitionState = new AllTransitionState(new TestExecutionContext().setModel(model.build()));
    allTransitionState.fillStatesAndTransitions(v0.build());
    List<Element> states = allTransitionState.getReachableStates(e0.build());
    List<Element> expectedStates = new ArrayList<Element>(){
      {
        add(v0.build());
        add(v1.build());
        add(v2.build());
        add(v3.build());
      }
    };
    assertThat(states, is(expectedStates));
  }

  @Test
  public void testTransitionNeighbours() throws Exception{
    AllTransitionState allTransitionState = new AllTransitionState(new TestExecutionContext().setModel(model.build()));
    allTransitionState.fillStatesAndTransitions(v0.build());
    allTransitionState.allReachableStates();
    allTransitionState.pathsForTransitions();
    List<Element> expectedNeighbours = new ArrayList<Element>(){
      {
        add(e4.build());
      }
    };
    List<Element> neighbours = allTransitionState.getNeighbours().get(e0.build());
    assertThat(neighbours, is(expectedNeighbours));
  }

  @Test
  public void testPathForTransition() throws Exception{
    AllTransitionState allTransitionState = new AllTransitionState(new TestExecutionContext().setModel(model.build()));
    allTransitionState.fillStatesAndTransitions(v0.build());
    allTransitionState.allReachableStates();
    allTransitionState.pathsForTransitions();
    List<Element> expectedPath = new ArrayList<Element>(){
      {
        add(v0.build());
        add(e0.build());
        add(v1.build());
        add(e1.build());
        add(v2.build());
        add(e2.build());
        add(v3.build());
        add(e3.build());
        add(v0.build());
      }
    };

    List<Element> path = new ArrayList<>(allTransitionState.getTransitionPaths().get(e0.build()));
    assertThat(path, is(expectedPath));
  }

  @Test
  public void testOtherPathForTransition() throws Exception{
    AllTransitionState allTransitionState = new AllTransitionState(new TestExecutionContext().setModel(model.build()));
    allTransitionState.fillStatesAndTransitions(v0.build());
    allTransitionState.allReachableStates();
    allTransitionState.pathsForTransitions();
    allTransitionState.otherPathForTransitions();
    List<Element> expectedOtherPath = new ArrayList<Element>(){
      {
        add(v0.build());
        add(e4.build());
        add(v1.build());
        add(e1.build());
        add(v2.build());
        add(e2.build());
        add(v3.build());
        add(e3.build());
        add(v0.build());
      }
    };
    List<Element> otherPath = new ArrayList<>(allTransitionState.getOtherTransitionPaths().get(e0.build()));
    assertThat(otherPath, is(expectedOtherPath));
  }

  @Test
  public void testFinalPath() throws Exception{
    AllTransitionState allTransitionState = new AllTransitionState(new TestExecutionContext().setModel(model.build()));
    allTransitionState.fillStatesAndTransitions(v0.build());
    allTransitionState.allReachableStates();
    allTransitionState.lookForPaths(v0.build());
    List<Element> expectedFinalPath = new ArrayList<Element>(){
      {
        add(v0.build());
        add(e0.build());
        add(v1.build());
        add(e1.build());
        add(v2.build());
        add(e2.build());
        add(v3.build());
        add(e3.build());
        add(v0.build());
        add(e4.build());
        add(v1.build());
        add(e5.build());
        add(v0.build());
        add(e0.build());
        add(v1.build());
        add(e1.build());
        add(v2.build());
        add(e6.build());
        add(v1.build());
        add(e1.build());
        add(v2.build());
        add(e2.build());
        add(v3.build());
        add(e7.build());
        add(v1.build());
        add(e1.build());
        add(v2.build());
        add(e2.build());
        add(v3.build());
        add(e3.build());
        add(v0.build());
      }
    };
    List<Element> finalPath = new ArrayList<>(allTransitionState.getFinalPath());
    assertThat(finalPath, is(expectedFinalPath));
  }

  @Test
  public void testTestSet()throws Exception{
    AllTransitionState allTransitionState = new AllTransitionState(new TestExecutionContext().setModel(model.build()));
    allTransitionState.fillStatesAndTransitions(v0.build());
    allTransitionState.allReachableStates();
    allTransitionState.lookForPaths(v0.build());
    int expectedTestSetSize = 3;
    List<Path<Element>> testSet = allTransitionState.getTestSet();
    assertThat(testSet.size(), is(expectedTestSetSize));
  }


}
