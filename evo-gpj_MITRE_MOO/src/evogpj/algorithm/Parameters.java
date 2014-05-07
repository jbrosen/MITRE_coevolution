/**
 * Copyright (c) 2011-2013 Evolutionary Design and Optimization Group
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
package evogpj.algorithm;

/**
 * Simple class to collect all default values and names, so they can be found in
 * one location and properly documented.
 * 
 * @author Owen Derby
 */
public final class Parameters {
    /**
     * Names used to identify properties in the properties file. Every key in
     * the properties file is matched against these strings by
     * {@link AlgorithmBase} to extract the new property value.
     * 
     * @author Owen Derby
     */
    public final static class Names {

        public static final String MUTATION_RATE = "mutation_rate";
        public static final String XOVER_RATE = "xover_rate";
        public static final String POP_SIZE = "pop_size";
        public static final String NUM_GENS = "num_gens";
        public static final String TIME_OUT = "timeout";

        public static final String PROBLEM_TYPE = "problem_type";
        public static final String PROBLEM_SIZE = "problem_size";
        public static final String EXTERNAL_THREADS = "external_threads";
        public static final String FUNCTION_SET = "function_set";

        public static final String FITNESS = "fitness_op";

        public static final String NUM_TRIALS = "num_trials";
        public static final String SEED = "rng_seed";
        public static final String TOURNEY_SIZE = "tourney_size";
    }

    /**
     * Names for specific operators, as understood by the library when reading
     * in values from the properties file.
     * 
     * @author Owen Derby
     */
    public final static class Operators {

        // FITNESS values

        public static final String LIST_INITIALIZE = "operator.ListInitialize";

        // SELECTION values
        public static final String TOURNEY_SELECT = "operator.TournamentSelection";
        public static final String CROWD_SELECT = "operator.CrowdedTournamentSelection";

        // MUTATE values
        public static final String LIST_MUTATE = "operator.ListMutate";

        // XOVER values
        // list single point crossover
        public static final String SPL_XOVER = "operator.ListSinglePointCrossOver";
    }

    /**
     * All default values for running the library.
     * <p>
     * To specify other values, please use the properties file.
     * 
     * @author Owen Derby
     */
    public final static class Defaults {
        /**
         * verbosity flag. Helpful for debugging.
         */
        public static final Boolean VERBOSE = true;
        
        public static final int POP_SIZE = 100;
        public static final int NUM_GENS = 5;
        public static final int NUM_CHOOSE = 10;
        public static final int TIME_OUT = 60;
        // Frequency for selecting each operator
        public static final double MUTATION_RATE = 0.1;
        public static final double XOVER_RATE = 0.7;
        // reproduction/replication frequency is implicitly defined as
        // (1 - XOVER_RATE - MUTATION_RATE)

        /**
         * number of best individuals to carry over to next generation
         */
        public static final int ELITE = 3;
        public static final int BIN_WIDTH = 5;
        public static final int TOURNEY_SIZE = 7;

        public static final String FITNESS = "fitness.TaxFitness";
//        used for co-evolution
        
        /**
         * the initial seed to use for the rng in the algorithm.
         */
        public static final long SEED = System.currentTimeMillis();

        public static final String INITIALIZE = Operators.LIST_INITIALIZE;
        public static final String SELECT = Operators.TOURNEY_SELECT;
        public static final String MUTATE = Operators.LIST_MUTATE;
        public static final String XOVER = Operators.SPL_XOVER;

    }
}
