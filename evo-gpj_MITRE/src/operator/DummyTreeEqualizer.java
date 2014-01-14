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

import genotype.Tree;
import gp.GPException;
import gp.Individual;
import gp.Population;

public class DummyTreeEqualizer extends TreeDynamicEqualizer {

	public DummyTreeEqualizer(int width, Population initPop) throws GPException {
		super(width, initPop);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean accept(Individual i) throws GPException {
		double fitness = i.getFitness();
		int bin_index = ((Tree) i.getGenotype()).getSize() / bin_width;
		Bin b;
		if (!bins.containsKey(bin_index)) {
			b = new Bin();
			bins.put(bin_index, b);
		} else {
			b = bins.get(bin_index);
		}
		b.add(i);
		if (fitness > b.most_fit)
			b.most_fit = fitness;
		return true;
	}

}
