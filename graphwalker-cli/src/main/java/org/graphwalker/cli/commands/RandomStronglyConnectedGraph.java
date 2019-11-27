package org.graphwalker.cli.commands;

/*-
 * #%L
 * GraphWalker Command Line Interface
 * %%
 * Copyright (C) 2005 - 2019 GraphWalker
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
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.graphwalker.io.factory.ContextFactoryScanner;

@Parameters(commandDescription = "Convert a graph in file format, to some other format. See http://graphwalker.org/docs/command_line_syntax")
public class RandomStronglyConnectedGraph {

  @Parameter(names = {"--vertices", "-v"}, required = true, arity = 1,
    description = "This command requires an input number, represents the number of the vertices.")
  public String input = "";

  @Parameter(names = {"--outedgesmin", "-omin"}, required = true, arity = 1,
    description = "Which is the number of the outedges of a vertex")
  public String outputmin = "";

  @Parameter(names = {"--outedgesmax", "-omax"}, required = true, arity = 1,
    description = "Which is the number of the outedges of a vertex")
  public String outputmax = "";

  @Parameter(names = {"--average", "-avg"}, arity = 1,
    description = "Which is the average of the outedges for the vertices in the model")
  public String average = "0";

  @Parameter(names = {"--filename", "-f"}, required = true, arity = 1,
    description = "Which is the name of the further created MODEL JSON file")
  public String filename = "";

}
