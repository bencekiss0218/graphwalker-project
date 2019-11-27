package org.graphwalker.core.algorithm;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.graphwalker.core.graphgenerator.RandomGraphGenerator;
import org.graphwalker.core.machine.TestExecutionContext;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.model.Vertex;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BreadthFirstSearchTest {
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



  private static final Model model = new Model()
    .addEdge(e0)
    .addEdge(e1)
    .addEdge(e2)
    .addEdge(e3)
    .addEdge(e4)
    .addEdge(e5)
    .addEdge(e6)
    .addEdge(e7);


  @Test
  public void testConnectedVertices() throws Exception {
    BreadthFirstSearch breadthFirstSearch = new BreadthFirstSearch(new TestExecutionContext().setModel(model.build()));
    breadthFirstSearch.getConnectedComponents(v0.build());
    List<Element> expectedComponents = new ArrayList<Element>(){
      {
        add(v0.build());
        add(v1.build());
        add(v2.build());
        add(v3.build());
      }
    };
    List<Element> components = breadthFirstSearch.getConnectedVertices();
    assertThat(components, is(expectedComponents));
  }

  @Test
  public void testConnectedTransitions() throws Exception{
    BreadthFirstSearch breadthFirstSearch = new BreadthFirstSearch(new TestExecutionContext().setModel(model.build()));
    breadthFirstSearch.getConnectedComponents(v0.build());
    List<Element> expectedComponents = new ArrayList<Element>(){
      {
        add(e0.build());
        add(e4.build());
        add(e1.build());
        add(e5.build());
        add(e2.build());
        add(e6.build());
        add(e3.build());
        add(e7.build());
      }
    };
    List<Element> components = breadthFirstSearch.getConnectedTransitions();
    assertThat(components, is(expectedComponents));
  }

}
