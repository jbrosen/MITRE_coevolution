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

public interface Mutate {

	/**
	 * Produce a new individual by randomly mutating i. Implementing class
	 * encodes particular mutation procedure. Individual i is not mutated.
	 * Instead, a new individual is cloned from i, then mutated.
	 * 
	 * @param i Individual to mutate from.
	 * @return New individual
	 * @throws GPException
	 */
	public abstract Individual mutate(Individual i) throws GPException;
}
