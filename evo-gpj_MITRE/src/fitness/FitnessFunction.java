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
package fitness;

import gp.Individual;

/**
 * Base class for all fitness evaluators. Fitness functions are fundamentally
 * different from the other operators in the operators package because they need
 * to interact with both genotypes and phenotypes of individuals and do not
 * manipulate the genotype of individuals.
 * 
 * @author Owen Derby
 */
public abstract class FitnessFunction {

	/**
	 * Evaluate an individual to calculate their fitness
	 * 
	 * @param ind Individual to evaluate
	 */
	public abstract void eval(Individual ind);

}
