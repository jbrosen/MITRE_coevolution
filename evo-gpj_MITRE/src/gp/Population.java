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
package gp;

import java.util.ArrayList;

/**
 * Class representing collection of all individuals in the run. Just a wrapper
 * around List implementation.
 * 
 * @author Owen Derby
 */
public class Population extends ArrayList<Individual> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6111020814262385165L;

	public Population() {
		super();
	}

	/**
	 * Instantiate population given a starting population of elite individuals.
	 * 
	 * @param elite
	 */
	public Population(Population elite) {
		for (Individual i : elite) {
			this.add(i.copy());
		}
	}

}
