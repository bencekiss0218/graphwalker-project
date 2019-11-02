package org.graphwalker.core.algorithm;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.graphwalker.core.graphgenerator.RandomGraphGenerator;
import org.graphwalker.core.machine.TestExecutionContext;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.model.Vertex;
import org.junit.Test;

public class BreadthFirstSearchTest {
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

  private static final Vertex v1 = new Vertex().setName("v1");
  private static final Vertex v2 = new Vertex().setName("v2");
  private static final Edge v1e1 = new Edge().setName("v1e1").setSourceVertex(v1).setTargetVertex(v2);
  private static final Edge v1e2 = new Edge().setName("v1e2").setSourceVertex(v1).setTargetVertex(v2);
  private static final Edge v2e3 = new Edge().setName("v2e3").setSourceVertex(v2).setTargetVertex(v1);
  private static final Edge v2e4 = new Edge().setName("v2e4").setSourceVertex(v2).setTargetVertex(v1);

  private static final RandomGraphGenerator rgg = new RandomGraphGenerator();
  private static final Model model = rgg.generateRandomGraph(2,2,2);

  //robodog
  //private static final Vertex s1 = new Vertex().setName("s1");
  //private static final Vertex s2 = new Vertex().setName("s2");
  //private static final Edge t1 = new Edge().setName("t1").setSourceVertex(s1).setTargetVertex(s2);
  //private static final Edge t2 = new Edge().setName("t2").setSourceVertex(s1).setTargetVertex(s1);
  //private static final Edge t3 = new Edge().setName("t3").setSourceVertex(s1).setTargetVertex(s2);
  //private static final Edge t4 = new Edge().setName("t4").setSourceVertex(s2).setTargetVertex(s1);
  //private static final Edge t5 = new Edge().setName("t5").setSourceVertex(s2).setTargetVertex(s2);
  //private static final Edge t6 = new Edge().setName("t6").setSourceVertex(s2).setTargetVertex(s1);

  private static final Vertex vertex0 = model.getVertices().get(0);
  //
  // private static final Vertex vertex0 = new Vertex().setName("v0");
  //private static final Vertex vertex1 = new Vertex().setName("v1");

  //private static final Edge edge0 = new Edge().setName("edge0").setSourceVertex(vertex0).setTargetVertex(vertex1);
  //private static final Edge edge1 = new Edge().setName("edge1").setSourceVertex(vertex1).setTargetVertex(vertex0);
  //private static final Edge edge2 = new Edge().setName("edge2").setSourceVertex(vertex0).setTargetVertex(vertex0);
  //private static final Edge edge3 = new Edge().setName("edge3").setSourceVertex(vertex1).setTargetVertex(vertex0);



  //private static final Model model = new Model()
  //  //.addEdge(edge0)
  //  //.addEdge(edge1)
  //  //.addEdge(edge2)
  //  //.addEdge(edge3)
  //  ;

  @Test
  public void connectedComponent() throws Exception {
    BreadthFirstSearch breadthFirstSearch = new BreadthFirstSearch(new TestExecutionContext().setModel(model.build()));
    //Vertex vertex0 = new Vertex().setName("v0");
    //Vertex vertex1 = new Vertex().setName("v1");
    //Edge edge0 = new Edge().setName("edge0").setSourceVertex(vertex0).setTargetVertex(vertex1);
    //Edge edge1 = new Edge().setName("edge1").setSourceVertex(vertex1).setTargetVertex(vertex0);
    //Edge edge2 = new Edge().setName("edge2").setSourceVertex(vertex0).setTargetVertex(vertex0);
    //Edge edge3 = new Edge().setName("edge3").setSourceVertex(vertex1).setTargetVertex(vertex0);
    //model.addEdge(edge0)
    //  .addEdge(edge1)
    //  .addEdge(edge2)
    //  .addEdge(edge3);

    /*assertThat(breadthFirstSearch.getConnectedComponent(v00.build()).size(), is(10));
    assertThat(breadthFirstSearch.getConnectedComponent(ve0.build()).size(), is(3));*/
   //System.out.println(breadthFirstSearch.getConnectedComponent(v00.build()).toString());
   //System.out.println(breadthFirstSearch.getConnectedComponent(v1.build()).toString());
    breadthFirstSearch.getConnectedComponents(vertex0.build());
    System.out.println(breadthFirstSearch.getConnectedVertices().toString());
  }

}
