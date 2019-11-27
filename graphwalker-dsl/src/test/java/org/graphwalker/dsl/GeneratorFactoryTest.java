package org.graphwalker.dsl;

/*
 * #%L
 * GraphWalker Command Line Interface
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
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import org.graphwalker.core.algorithm.AllTransitionState;
import org.graphwalker.core.condition.*;
import org.graphwalker.core.generator.*;
import org.graphwalker.dsl.antlr.DslException;
import org.graphwalker.dsl.antlr.generator.GeneratorFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by krikar on 5/14/14.
 */
public class GeneratorFactoryTest {

  @Test(expected = DslException.class)
  public void unvalidGenerator() {
    GeneratorFactory.parse("kskskdhfh(edge_coverage(100))");
  }

  @Test(expected = DslException.class)
  public void unvalidStopCondition() {
    GeneratorFactory.parse("random(dkshdgej(100))");
  }

  @Test
  public void random_edge_coverage() {
    PathGenerator generator = GeneratorFactory.parse("random(edge_coverage(100))");
    Assert.assertThat(generator, instanceOf(RandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(EdgeCoverage.class));
    Assert.assertThat(((EdgeCoverage) generator.getStopCondition()).getPercent(), is(100));
  }

  @Test
  public void random_edgecoverage() {
    PathGenerator generator = GeneratorFactory.parse("random(edgecoverage(100))");
    Assert.assertThat(generator, instanceOf(RandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(EdgeCoverage.class));
    Assert.assertThat(((EdgeCoverage) generator.getStopCondition()).getPercent(), is(100));
  }

  @Test
  public void randompath_edge_coverage() {
    PathGenerator generator = GeneratorFactory.parse("randompath(edge_coverage(100))");
    Assert.assertThat(generator, instanceOf(RandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(EdgeCoverage.class));
    Assert.assertThat(((EdgeCoverage) generator.getStopCondition()).getPercent(), is(100));
  }

  @Test
  public void quick_random_edge_coverage() {
    PathGenerator generator = GeneratorFactory.parse("quick_random(edge_coverage(100))");
    Assert.assertThat(generator, instanceOf(QuickRandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(EdgeCoverage.class));
    Assert.assertThat(((EdgeCoverage) generator.getStopCondition()).getPercent(), is(100));
  }

  @Test
  public void quickrandom_edge_coverage() {
    PathGenerator generator = GeneratorFactory.parse("quickrandom(edge_coverage(100))");
    Assert.assertThat(generator, instanceOf(QuickRandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(EdgeCoverage.class));
    Assert.assertThat(((EdgeCoverage) generator.getStopCondition()).getPercent(), is(100));
  }

  @Test
  public void quickrandompath_edge_coverage() {
    PathGenerator generator = GeneratorFactory.parse("quickrandompath(edge_coverage(100))");
    Assert.assertThat(generator, instanceOf(QuickRandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(EdgeCoverage.class));
    Assert.assertThat(((EdgeCoverage) generator.getStopCondition()).getPercent(), is(100));
  }

  @Test
  public void shortest_all_paths_edge_coverage() {
    PathGenerator generator = GeneratorFactory.parse("shortest_all_paths(edge_coverage(100))");
    Assert.assertThat(generator, instanceOf(ShortestAllPaths.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(EdgeCoverage.class));
    Assert.assertThat(((EdgeCoverage) generator.getStopCondition()).getPercent(), is(100));
  }

  @Test
  public void shortestallpaths_edge_coverage() {
    PathGenerator generator = GeneratorFactory.parse("shortestallpaths(edge_coverage(100))");
    Assert.assertThat(generator, instanceOf(ShortestAllPaths.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(EdgeCoverage.class));
    Assert.assertThat(((EdgeCoverage) generator.getStopCondition()).getPercent(), is(100));
  }

  @Test
  public void random_vertex_coverage() {
    PathGenerator generator = GeneratorFactory.parse("random(vertex_coverage(100))");
    Assert.assertThat(generator, instanceOf(RandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(VertexCoverage.class));
    Assert.assertThat(((VertexCoverage) generator.getStopCondition()).getPercent(), is(100));
  }

  @Test
  public void random_vertexcoverage() {
    PathGenerator generator = GeneratorFactory.parse("random(vertexcoverage(100))");
    Assert.assertThat(generator, instanceOf(RandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(VertexCoverage.class));
    Assert.assertThat(((VertexCoverage) generator.getStopCondition()).getPercent(), is(100));
  }

  @Test
  public void a_star_reached_vertex() {
    PathGenerator generator = GeneratorFactory.parse("a_star(reached_vertex(v_ABC))");
    Assert.assertThat(generator, instanceOf(AStarPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(ReachedVertex.class));
    Assert.assertThat(generator.getStopCondition().getValue(), is("v_ABC"));
  }

  @Test
  public void random_reached_edge() {
    PathGenerator generator = GeneratorFactory.parse("random(reached_edge(edgeName))");
    Assert.assertThat(generator, instanceOf(RandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(ReachedEdge.class));
    Assert.assertThat(generator.getStopCondition().getValue(), is("edgeName"));
  }

  @Test
  public void random_reached_vertex() {
    PathGenerator generator = GeneratorFactory.parse("random(reached_vertex(vertexName))");
    Assert.assertThat(generator, instanceOf(RandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(ReachedVertex.class));
    Assert.assertThat(generator.getStopCondition().getValue(), is("vertexName"));
  }

  @Test
  public void random_time_duration() {
    PathGenerator generator = GeneratorFactory.parse("random(time_duration(600))");
    Assert.assertThat(generator, instanceOf(RandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(TimeDuration.class));
    Assert.assertThat(((TimeDuration) generator.getStopCondition()).getDuration(), is(600L));
  }

  @Test
  public void random_edge_coverage_and_vertex_coverage() {
    PathGenerator generator = GeneratorFactory.parse("random ( edge_coverage(100) and vertex_coverage (100) )");
    Assert.assertThat(generator, instanceOf(RandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(CombinedCondition.class));
  }

  @Test
  public void random_reached_vertex_or_reached_edge() {
    PathGenerator generator = GeneratorFactory.parse("random ( reached_vertex(Some_vertex) or reached_edge ( Some_edge ) )");
    Assert.assertThat(generator, instanceOf(RandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(AlternativeCondition.class));

    AlternativeCondition condition = (AlternativeCondition) generator.getStopCondition();
    Assert.assertThat(condition.getStopConditions().get(0), instanceOf(ReachedVertex.class));
    Assert.assertThat(condition.getStopConditions().get(0).getValue(), is("Some_vertex"));
    Assert.assertThat(condition.getStopConditions().get(1), instanceOf(ReachedEdge.class));
    Assert.assertThat(condition.getStopConditions().get(1).getValue(), is("Some_edge"));
  }

  @Test
  public void random_edge_coverage_or_vertex_coverage() {
    PathGenerator generator = GeneratorFactory.parse("random ( edge_coverage(100) or vertex_coverage (100) )");
    Assert.assertThat(generator, instanceOf(RandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(AlternativeCondition.class));
  }

  @Test
  public void random_edge_coverage_or_time_duration_a_star_reached_vertex() {
    PathGenerator generator = GeneratorFactory.parse("random( edge_coverage(100) or time_duration(500) ) a_star(reached_vertex(v_ABC))");
    Assert.assertThat(generator, instanceOf(CombinedPath.class));

    CombinedPath combinedPath = (CombinedPath) generator;
    Assert.assertThat(combinedPath.getPathGenerators().get(0), instanceOf(RandomPath.class));
    Assert.assertThat(combinedPath.getPathGenerators().get(1), instanceOf(AStarPath.class));

    StopCondition condition = combinedPath.getPathGenerators().get(0).getStopCondition();
    Assert.assertThat(condition, instanceOf(StopCondition.class));
    AlternativeCondition alternativeCondition = (AlternativeCondition) condition;
    Assert.assertThat(alternativeCondition.getStopConditions().get(0), instanceOf(EdgeCoverage.class));
    Assert.assertThat(alternativeCondition.getStopConditions().get(1), instanceOf(TimeDuration.class));

    condition = combinedPath.getPathGenerators().get(1).getStopCondition();
    Assert.assertThat(condition, instanceOf(ReachedVertex.class));
    Assert.assertThat(condition.getValue(), is("v_ABC"));
  }

  @Test
  public void capital_random_reached_vertex_or_reached_edge() {
    PathGenerator generator = GeneratorFactory.parse("RANDOM ( REACHED_VERTEX( Some_vertex) OR REACHED_EDGE( Some_edge ) )");
    Assert.assertThat(generator, instanceOf(RandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(AlternativeCondition.class));

    AlternativeCondition condition = (AlternativeCondition) generator.getStopCondition();
    Assert.assertThat(condition.getStopConditions().get(0), instanceOf(ReachedVertex.class));
    Assert.assertThat(condition.getStopConditions().get(0).getValue(), is("Some_vertex"));
    Assert.assertThat(condition.getStopConditions().get(1), instanceOf(ReachedEdge.class));
    Assert.assertThat(condition.getStopConditions().get(1).getValue(), is("Some_edge"));
  }

  @Test
  public void weighted_random_edge_coverage() {
    PathGenerator generator = GeneratorFactory.parse("weighted_random(edge_coverage(100))");
    Assert.assertThat(generator, instanceOf(WeightedRandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(EdgeCoverage.class));
    Assert.assertThat(((EdgeCoverage) generator.getStopCondition()).getPercent(), is(100));
  }

  @Test
  public void weightedrandompath_edge_coverage() {
    PathGenerator generator = GeneratorFactory.parse("weightedrandompath(edge_coverage(100))");
    Assert.assertThat(generator, instanceOf(WeightedRandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(EdgeCoverage.class));
    Assert.assertThat(((EdgeCoverage) generator.getStopCondition()).getPercent(), is(100));
  }

  @Test
  public void random_dependency_edge_coverage() {
    PathGenerator generator = GeneratorFactory.parse("random(dependency_edge_coverage(80))");
    Assert.assertThat(generator, instanceOf(RandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(DependencyEdgeCoverage.class));
    Assert.assertThat(((DependencyEdgeCoverage) generator.getStopCondition()).getDependency(), is(80));
  }

  @Test
  public void random_dependencyedgecoverage() {
    PathGenerator generator = GeneratorFactory.parse("random(dependencyedgecoverage(80))");
    Assert.assertThat(generator, instanceOf(RandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(DependencyEdgeCoverage.class));
    Assert.assertThat(((DependencyEdgeCoverage) generator.getStopCondition()).getDependency(), is(80));
  }

  @Test
  public void random_requirement_coverage() {
    PathGenerator generator = GeneratorFactory.parse("random(requirement_coverage(100))");
    Assert.assertThat(generator, instanceOf(RandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(RequirementCoverage.class));
    Assert.assertThat(((RequirementCoverage) generator.getStopCondition()).getPercent(), is(100));
  }

  @Test
  public void random_requirementcoverage() {
    PathGenerator generator = GeneratorFactory.parse("random(requirementcoverage(100))");
    Assert.assertThat(generator, instanceOf(RandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(RequirementCoverage.class));
    Assert.assertThat(((RequirementCoverage) generator.getStopCondition()).getPercent(), is(100));
  }
  
  @Test
  public void randompath_length_coverage() {
    PathGenerator generator = GeneratorFactory.parse("randompath(length(80))");
    Assert.assertThat(generator, instanceOf(RandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(Length.class));
    Assert.assertThat(((Length) generator.getStopCondition()).getLength(), is(80L));
  }

  @Test
  public void multipleCombinedStopConditions() {
    PathGenerator generator = GeneratorFactory.parse("random(reached_vertex(isPageOpened) and reached_vertex(isLanguageDetected) and reached_vertex(isTranslateCleared) and reached_edge(openPage))");
    Assert.assertThat(generator, instanceOf(RandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(CombinedCondition.class));
    Assert.assertThat(((CombinedCondition) generator.getStopCondition()).getStopConditions().size(), is(4));
  }

  @Test
  public void multipleAlternativeStopConditions() {
    PathGenerator generator = GeneratorFactory.parse("random(reached_vertex(isPageOpened) or reached_vertex(isLanguageDetected) or reached_vertex(isTranslateCleared))");
    Assert.assertThat(generator, instanceOf(RandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(AlternativeCondition.class));
    Assert.assertThat(((AlternativeCondition) generator.getStopCondition()).getStopConditions().size(), is(3));
  }

  @Test
  public void alltransitionstate_alltransitionstatefull() {
    PathGenerator generator = GeneratorFactory.parse("alltransitionstate(alltransitionstatefull)");
    Assert.assertThat(generator, instanceOf(AllTransitionStatePath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(AllTransitionStateFull.class));
  }

  @Test
  public void all_transition_state_alltransitionstatefull() {
    PathGenerator generator = GeneratorFactory.parse("all_transition_state(alltransitionstatefull)");
    Assert.assertThat(generator, instanceOf(AllTransitionStatePath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(AllTransitionStateFull.class));
  }

  @Test
  public void all_transition_state_all_transition_state_full() {
    PathGenerator generator = GeneratorFactory.parse("all_transition_state(all_transition_state_full)");
    Assert.assertThat(generator, instanceOf(AllTransitionStatePath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(AllTransitionStateFull.class));
  }

  @Test
  public void alltransitionstateall_transition_state_full() {
    PathGenerator generator = GeneratorFactory.parse("alltransitionstate(all_transition_state_full)");
    Assert.assertThat(generator, instanceOf(AllTransitionStatePath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(AllTransitionStateFull.class));
  }

  @Test
  public void alltransitionstate_alltransitionstatepairs() {
    PathGenerator generator = GeneratorFactory.parse("alltransitionstate(alltransitionstatepairs)");
    Assert.assertThat(generator, instanceOf(AllTransitionStatePath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(AllTransitionStatePairs.class));
  }

  @Test
  public void alltransitionstateall_transition_state_pairs() {
    PathGenerator generator = GeneratorFactory.parse("alltransitionstate(all_transition_state_pairs)");
    Assert.assertThat(generator, instanceOf(AllTransitionStatePath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(AllTransitionStatePairs.class));
  }


}
