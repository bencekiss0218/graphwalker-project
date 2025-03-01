package org.graphwalker.core.algorithm;

/*
 * #%L
 * GraphWalker Core
 * %%
 * Copyright (C) 2005 - 2014 GraphWalker
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.graphwalker.core.machine.TestExecutionContext;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.model.Vertex;
import org.junit.Test;

/**
 * @author Nils Olsson
 */
public class DepthFirstSearchTest {

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

  private static final Model model = new Model()
      .addEdge(e1)
      .addEdge(e2)
      .addEdge(e3)
      .addEdge(e4)
      .addEdge(e5)
      .addEdge(ee1)
      .addEdge(t1)
      .addEdge(t2)
      .addEdge(t3)
      .addEdge(t4)
      .addEdge(t5)
      .addEdge(t6);

  @Test
  public void connectedComponent() throws Exception {
    DepthFirstSearch depthFirstSearch = new DepthFirstSearch(new TestExecutionContext().setModel(model.build()));
    assertThat(depthFirstSearch.getConnectedComponent(v00.build()).size(), is(10));
    assertThat(depthFirstSearch.getConnectedComponent(ve0.build()).size(), is(3));
    //System.out.println(depthFirstSearch.getConnectedComponent(v00.build()).toString());
    System.out.println(depthFirstSearch.getConnectedComponent(s1.build()).toString());
  }
}
