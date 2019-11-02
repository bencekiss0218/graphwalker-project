package org.graphwalker.core.graphgenerator;

import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.model.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomGraphGenerator {

  private static final Model model = new Model();

  private final Random random = new Random(System.nanoTime());

  public Model generateRandomGraph(int numberOfVertices, int minOutEdge, int maxOutEdge){
    Model model = new Model();
    List<Vertex> vertices = new ArrayList<>();
    List<Edge> edges = new ArrayList<>();
    String vertexId;
    String vertexName;
    String edgeId;
    String edgeName;
    Edge edge;
    int edgeIndex;

    for(int i = 0; i < numberOfVertices + 1; i++){
      if(i<numberOfVertices){
        vertexId = "v_" + i;
        vertexName = "v" + i;
        Vertex v = new Vertex().setId(vertexId).setName(vertexName);
        vertices.add(v);
      }
      if(vertices.size() > 1){
        int id = i-1;
        edgeId = "e_" + id;
        edgeName = "e" + id;

        if(i == numberOfVertices){
          edge = new Edge().setId(edgeId).setName(edgeName).setSourceVertex(vertices.get(i-1)).setTargetVertex(vertices.get(0));
          edges.add(edge);
          model.addEdge(edge);
        }else{
          edge = new Edge().setId(edgeId).setName(edgeName).setSourceVertex(vertices.get(i-1)).setTargetVertex(vertices.get(i));
          edges.add(edge);
          model.addEdge(edge);
        }

      }
    }

    edgeIndex = edges.size();

    for(Vertex v : vertices){
      int outedges = random.nextInt((maxOutEdge - minOutEdge) + 1) + minOutEdge;
      for(int i = 0; i < outedges-1; i++){
        edgeId = "e_" + edgeIndex;
        edgeName = "e" + edgeIndex;
        Vertex source = v;
        edgeIndex++;
        int targetVertexId = random.nextInt((vertices.size() - 1));
        Vertex target = vertices.get(targetVertexId);
        edge = new Edge().setId(edgeId).setName(edgeName).setSourceVertex(source).setTargetVertex(target);
        model.addEdge(edge);
      }
    }

    return model;
  }
}
