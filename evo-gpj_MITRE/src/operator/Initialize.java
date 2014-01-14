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

import gp.Population;

import java.util.ArrayList;
import java.util.List;

public interface Initialize {

	/**
	 * Instantiate a new population of individuals
	 * 
	 * @param popSize Number of individuals in the new population.
	 * @param funcset Set of functions available to create individuals
	 * @param termset Set of terminals (vars and constants) available to use.
	 * @return the new population.
	 */
	public abstract Population initialize(int popSize, List<String> funcset,
			List<String> termset);


	Population listInitialize(int popSize, ArrayList<ArrayList> set);
	

	
}
