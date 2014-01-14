/**
 * Copyright (c) 2011-2012 Evolutionary Design and Optimization Group
 * 
 * Licensed under the MIT License.
 * 
 * See the "LICENSE" file for a copy of the license.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.  
 *
 */
package math;

import java.util.List;

/**
 * Abstraction/extension of the genotype evaluation traversal. The problem: we
 * need to traverse the entire genotype tree to evaluate every single training
 * case. This is expensive (in running time). Solution: traverse tree once,
 * capture that traversal as a {@link Function}, and use that for evaluations.
 * That's the purpose of this interface and all implementations of this
 * interface. A further optimization might be to perform some sort of static
 * analysis to intelligently remove useless/dead subexpressions (such as those
 * which always evaluate to the same value, or don't affect any subsequent
 * evaluations).
 * 
 * @author Owen Derby
 */
public interface Function {

	Double eval(List<Double> t);

}
