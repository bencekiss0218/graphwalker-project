package org.graphwalker.core.graphgenerator;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.graphgenerator.RandomGraphGenerator;
import org.graphwalker.core.model.Vertex;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class RandomGraphGeneratorTest {
  RandomGraphGenerator rgg = new RandomGraphGenerator();
  Model model = rgg.generateRandomGraph(5,2,2);



  @Test
  public void testGenerator() throws Exception{
    List<Vertex> vertices = new ArrayList<>();
    List<Edge> edges = new ArrayList<>();
    vertices.addAll(model.getVertices());
    edges.addAll(model.getEdges());
    for(Vertex v : vertices){
      System.out.println(v.getName() + " outedges: " );
    }

    for(Edge e : edges){
      Edge.RuntimeEdge re = e.build();
      System.out.println("Edge: " + re + " -- SourceVertex: " + re.getSourceVertex() + " -- TargetVertex: " + re.getTargetVertex());
      e.build();
    }
  }
}
