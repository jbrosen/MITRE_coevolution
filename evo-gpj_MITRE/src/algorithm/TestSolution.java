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
package algorithm;

import fitness.CSVData;
import fitness.SRFitness;
import genotype.Tree;
import gp.Individual;
import gp.MersenneTwisterFast;

import java.util.ArrayList;
import java.util.List;

import math.Function;

/**
 * 
 * @author Owen Derby
 *
 */
public class TestSolution {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 3) {
			System.err
					.println("must specify solution, data used in training, and test data file");
			System.exit(1);
		}
		Tree t = new Tree(new MersenneTwisterFast(0), args[0],
				new ArrayList<String>(), new ArrayList<String>());
		Individual solution = new Individual(t);
		// System.out.println("Evaluating " + solution.toString());
		SRFitness train_fitness = new SRFitness(new CSVData(args[1]));
		SRFitness test_fitness = new SRFitness(new CSVData(args[2]));
		test_fitness.eval(solution);
		System.out.println("Fitness of " + solution.getFitness());
		// System.out.println("rMSE of " + calc_rMSE(test_fitness, t));

		// Calculate the optimal scaling parameters (c/o Keijzer, 2003)
		double total = 0.0;
		double target = 0.0;
		final List<Double> phenotype_tmp = new ArrayList<Double>();
		final Function func = t.generate();
		for (int i = 0; i < train_fitness.getTarget().size(); i++) {
			target += train_fitness.getTarget().get(i);
			Double val = func.eval(train_fitness.getFitnessCases().get(i));
			phenotype_tmp.add(val);
			total += val;
		}
		double avg_target = target / train_fitness.getTarget().size();
		double avg_total = total
				/ train_fitness.getFitnessCases().size();

		Double slope_num = 0.0;
		Double slope_den = 0.0;
		for (int i = 0; i < train_fitness.getTarget().size(); i++) {
			Double off = phenotype_tmp.get(i) - avg_total;
			slope_num += (train_fitness.getTarget().get(i) - avg_target) * off;
			slope_den += Math.pow(off, 2);
		}
		Double slope = slope_num / slope_den;

		Double error = 0.0;
		for (int i = 0; i < test_fitness.getTarget().size(); i++) {
			Double val = func.eval(test_fitness.getFitnessCases().get(i));
			val = slope * (val - avg_total) + avg_target;
			error += Math.pow((test_fitness.getTarget().get(i) - val), 2);
		}
		error = error / test_fitness.getTarget().size();
		String sol_prime = "(+ " + (avg_target - slope * avg_total) + " (* "
				+ slope + " " + args[0] + "))";
		System.out.println("MSE=" + error);
		System.out.println("Optimal solution: " + sol_prime);
	}

	/*
	 * public static Double calc_rMSE(SRFitness fitness, Tree t) { Double
	 * min_val = null; Double max_val = null; final List<Double> phenotype_tmp =
	 * new ArrayList<Double>(); final Function func = t.generate(); for
	 * (List<Double> d : fitness.getFitnessCases()) { Double val = func.eval(d);
	 * phenotype_tmp.add(val); if (min_val == null || val < min_val) { min_val =
	 * val; } else if (max_val == null || val > max_val) { max_val = val; } }
	 * 
	 * final Double delta = max_val - min_val; final Double target_delta =
	 * fitness.getTarget().max - fitness.getTarget().min; Double error = 0.0;
	 * for (int i=0; i < fitness.getTarget().size(); i++) { Double val =
	 * (phenotype_tmp.get(i) - min_val)/delta; val = val*(target_delta) +
	 * fitness.getTarget().min; error += Math.pow((fitness.getTarget().get(i) -
	 * val), 2); }
	 * 
	 * return error/(double)fitness.getTarget().size(); }
	 */
}
