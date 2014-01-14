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

import genotype.Tree;
import gp.Individual;

import java.util.List;

import math.Function;
import phenotype.SRPhenotype;

/**
 * Implements fitness evaluation for symbolic regression.
 * 
 * @author Owen Derby
 */
public class SRFitness extends FitnessFunction {
	final private Data data;
	private PNorm norm_func;
	private boolean is_double;

	public SRFitness(Data data) {
		this(2, data, true);
	}

	public SRFitness(int pNorm, Data data, boolean is_double) {
		this.data = data;
		norm_func = PNorm.newNorm(pNorm);
		this.is_double = is_double;
	}

	/**
	 * @see Function
	 */
	@Override
	public void eval(Individual ind) {
		if (ind.getFitness() != null) {
			return;
		}
		SRPhenotype phenotype = new SRPhenotype();
		Tree genotype = (Tree) ind.getGenotype();
		norm_func.clear();
		final SRPhenotype phenotype_tmp = new SRPhenotype();
		final Function func = genotype.generate();
		// This is the most time-consuming part of any GP run - evaluation of
		// individuals!
		for (List<Double> d : this.getFitnessCases()) {
			Double val = func.eval(d);
			phenotype_tmp.setDataValue(val);
		}

		// scale predictions to [0,1] range (same as scaled_data) and then
		// compute error
		final Double delta = phenotype_tmp.max - phenotype_tmp.min;
		final Double year_delta = this.data.getTargetMax()
				- this.data.getTargetMin();
		for (int i = 0; i < this.data.getScaledTargetValues().size(); i++) {
			Double scaled_val = (phenotype_tmp.getDataValue(i) - phenotype_tmp.min)
					/ delta;
			if (!this.is_double) {
				// If we're working with integer output space, we need to
				// perform rounding in the output space.
				Double unscaled_pre_val = scaled_val * year_delta
						+ this.data.getTargetMin();
				scaled_val = (unscaled_pre_val.intValue() - this.data
						.getTargetMin()) / year_delta;
			}
			phenotype.setDataValue(scaled_val);
			norm_func.addErr(this.data.getScaledTargetValues().get(i)
					- scaled_val);
		}
		Double error = norm_func.calcErr();

		// Because of scaling and normalization (done automatically by
		// norm_func, error is always in range [0,1]
		phenotype.setError(error);
		ind.setPhenotype(phenotype);
		if (error.isNaN()) {
			ind.setFitness(0.0);
		} else {
			ind.setFitness((1.0 - error) / (1 + error));
		}
	}

	/**
	 * @return the fitnessCases
	 */
	public List<List<Double>> getFitnessCases() {
		return data.getInputValues();
	}

	/**
	 * @return the scaled_target
	 */
	public List<Double> getTarget() {
		return data.getScaledTargetValues();
	}
}
