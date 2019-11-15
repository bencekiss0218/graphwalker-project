package org.graphwalker.core.graphgenerator;

import netscape.javascript.JSObject;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.model.Vertex;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class RandomGraphGenerator {

  private static final Model model = new Model();

  private final Random random = new Random(System.nanoTime());
  private JSONObject contextObj = new JSONObject();

  public Model generateRandomGraph(int numberOfVertices, int minOutEdge, int maxOutEdge) {
    Model model = new Model();
    List<Vertex> vertices = new ArrayList<>();
    List<Edge> edges = new ArrayList<>();
    String vertexId;
    String vertexName;
    String edgeId;
    String edgeName;
    Edge edge;
    int edgeIndex;


    JSONArray modelsArray = new JSONArray();
    JSONArray verticesArray = new JSONArray();
    JSONArray edgesArray = new JSONArray();
    JSONObject modelObj = new JSONObject();
    JSONObject vertexObj = new JSONObject();
    JSONObject edgeObj = new JSONObject();
    JSONObject propertiesObj = new JSONObject();


    try {
      modelObj.put("name", "firstModel");
      modelObj.put("id", 1323);
      modelObj.put("generator", "random(vertex_coverage(100))");
      //modelObj.put("actions", "");
    }catch (Exception e){
      System.out.println(e);
    }

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


    try {
      int xCord = 300;
      int yCord = 250;
      for(Vertex v : model.getVertices()){
        propertiesObj = new JSONObject();
        xCord += 100;
        vertexObj = new JSONObject();
        vertexObj.put("id", v.getId());
        vertexObj.put("name", v.getName());
        propertiesObj.put("x",xCord);
        propertiesObj.put("y",yCord);
        vertexObj.put("properties", propertiesObj);
        verticesArray.put(vertexObj);
      }
    }catch (Exception e){
      System.out.println(e);
    }

    try {
      for(Edge e : model.getEdges()){
        edgeObj = new JSONObject();
        edgeObj.put("id", e.getId());
        edgeObj.put("name", e.getName());
        edgeObj.put("sourceVertexId", e.getSourceVertex().getId());
        edgeObj.put("targetVertexId", e.getTargetVertex().getId());

        edgesArray.put(edgeObj);
      }
    }catch (Exception e){
      System.out.println(e);
    }

    try {
      modelObj.put("vertices", verticesArray);
      modelObj.put("edges", edgesArray);
      modelObj.put("startElementId", vertices.get(0).getId());
    }catch (Exception e){
      System.out.println(e);
    }

    modelsArray.put(modelObj);
    try {
      contextObj.put("models", modelsArray);
    }catch (Exception e){
      System.out.println(e);
    }

    System.out.println(contextObj);

    writeGeneratedGraph(contextObj);

    return model;
  }

  public JSONObject getContextObj(){
    return contextObj;
  }

  public void writeGeneratedGraph(JSONObject contextObj){

    try (FileWriter file = new FileWriter("randomgraph.json")) {

      file.write(contextObj.toString());
      file.flush();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
