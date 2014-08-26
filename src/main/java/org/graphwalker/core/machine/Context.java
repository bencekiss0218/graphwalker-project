package org.graphwalker.core.machine;

/*
 * #%L
 * GraphWalker Core
 * %%
 * Copyright (C) 2011 - 2014 GraphWalker
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

import org.graphwalker.core.algorithm.Algorithm;
import org.graphwalker.core.generator.PathGenerator;
import org.graphwalker.core.model.*;
import org.graphwalker.core.statistics.Profiler;

import javax.script.ScriptEngine;
import java.util.List;
import java.util.Map;

import static org.graphwalker.core.model.Edge.RuntimeEdge;
import static org.graphwalker.core.model.Model.RuntimeModel;

/**
 * @author Nils Olsson
 */
public interface Context {
    ExecutionStatus getExecutionStatus();
    Context setExecutionStatus(ExecutionStatus executionStatus);
    ScriptEngine getScriptEngine();
    RuntimeModel getModel();
    Context setModel(Model model);
    Profiler getProfiler();
    PathGenerator getPathGenerator();
    Context setPathGenerator(PathGenerator pathGenerator);
    Element getCurrentElement();
    Context setCurrentElement(Element element);
    Element getNextElement();
    Context setNextElement(Builder<? extends Element> nextElement) ;
    Context setNextElement(Element nextElement);
    List<Requirement> getRequirements();
    List<Requirement> getRequirements(RequirementStatus status);
    @SuppressWarnings("unchecked")
    public <A extends Algorithm> A getAlgorithm(Class<A> clazz);
    <E> List<E> filter(List<E> elements);
    boolean isAvailable(RuntimeEdge edge);
    void execute(Action action);
    void execute(String name);
    Map<String, String> getKeys();
}
