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
  private static final Vertex v3 = new Vertex().setName("v3");
  private static final Edge e0 = new Edge().setName("e0").setSourceVertex(v0).setTargetVertex(v1);
  private static final Edge e1 = new Edge().setName("e1").setSourceVertex(v1).setTargetVertex(v2);
  private static final Edge e2 = new Edge().setName("e2").setSourceVertex(v2).setTargetVertex(v3);
  private static final Edge e3 = new Edge().setName("e3").setSourceVertex(v3).setTargetVertex(v0);
  private static final Edge e4 = new Edge().setName("e4").setSourceVertex(v0).setTargetVertex(v1);
  private static final Edge e5 = new Edge().setName("e5").setSourceVertex(v1).setTargetVertex(v0);
  private static final Edge e6 = new Edge().setName("e6").setSourceVertex(v2).setTargetVertex(v1);
  private static final Edge e7 = new Edge().setName("e7").setSourceVertex(v3).setTargetVertex(v1);

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
  private static final Model modell = rgg.generateRandomGraph(10,2,6, 0);
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
    AllTransitionState allTransitionState = new AllTransitionState(new TestExecutionContext().setModel(model.build()));

    List<Edge> edges = model.getEdges();
    for(Edge e : edges){
      System.out.println("---EDGE--- " + e.build() + " SOURCE----- " + e.build().getSourceVertex() + " TARGET ---- " + e.build().getTargetVertex());
    }

    System.out.println(allTransitionState.returnTestSet(v0.build()));
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
