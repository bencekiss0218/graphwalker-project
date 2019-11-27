package org.graphwalker.core.graphgenerator;


import org.graphwalker.core.algorithm.AllTransitionState;
import org.graphwalker.core.machine.TestExecutionContext;
import org.graphwalker.core.model.*;
import org.graphwalker.core.graphgenerator.RandomGraphGenerator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RandomGraphGeneratorTest {
  RandomGraphGenerator rgg = new RandomGraphGenerator();
  Model model = rgg.generateRandomGraph(5,2,2, 0);



  @Test
  public void testGenerator() throws Exception{
    AllTransitionState allTransitionState = new AllTransitionState(new TestExecutionContext().setModel(model.build()));
    allTransitionState.fillStatesAndTransitions(model.getVertices().get(0).build());
    List<Element> states = new ArrayList<>();
    boolean stronglyConnected = true;
    for(Vertex v : model.getVertices()){
      states.add(v.build());
    }
    List<Element> transitions = new ArrayList<>();
    for (Edge e : model.getEdges()){
      transitions.add(e.build());
    }

    allTransitionState.allReachableStates();
    Map<Element,List<Element>> transitionStates = allTransitionState.getReachableStates();

    for(Element e : transitions){
      if(!allTransitionState.equalsIgnore(transitionStates.get(e),states)){
        stronglyConnected = false;
      }
    }

    assertThat(stronglyConnected, is(true));
  }
}
