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

import genotype.Genotype;
import phenotype.Phenotype;

/**
 * This class represents the individuals in a population. Individuals have a
 * genotype, a phenotype and a fitness. Genotypes are what genetic operations
 * use to create new offspring. Phenotypes are like a preview of how this
 * individual looks, in the problem space (ie the output of an individual in a
 * SR problem for each of the test inputs). Fitness is the standard number
 * representing how fit this individual is. As is typical, higher fitness is
 * better.
 * 
 * @author Owen Derby
 */
public class Individual {

	private final Genotype genotype;
	private Phenotype phenotype;
	private Double fitness;

	public Individual(Genotype genotype) {
		this.genotype = genotype;
		this.fitness = null;
	}

	public Individual(Individual i) {
		this.genotype = i.genotype.copy();
		this.fitness = i.fitness;
		//this.phenotype = i.phenotype.copy();
	}

	/**
	 * deep copy of an individual
	 * 
	 * @return new individual
	 */
	public Individual copy() {
		return new Individual(this);
	}

	public Genotype getGenotype() {
		return this.genotype;
	}

	public void setPhenotype(Phenotype p) {
		this.phenotype = p;
	}

	public Phenotype getPhenotype() {
		return this.phenotype;
	}

	public Double getFitness() {
		return this.fitness;
	}

	public void setFitness(Double d) {
		this.fitness = d;
	}

	@Override
	public String toString() {
		return this.genotype.toString();
	}
}
