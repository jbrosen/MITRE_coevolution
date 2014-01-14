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
package operator;

import gp.GPException;
import gp.Individual;
import gp.Population;

public interface Crossover {

	/**
	 * Perform crossover between two individuals, returning their offspring. The
	 * two individuals may be thought of as the parents. The returned population
	 * may contain one or more offspring, depending on the crossover
	 * implementation. Neither parents are mutated; copies of their genome are
	 * recombined to form offspring.
	 * 
	 * @param ind1
	 * @param ind2
	 * @return
	 * @throws GPException
	 */
	public abstract Population crossOver(Individual ind1, Individual ind2)
			throws GPException;
}
