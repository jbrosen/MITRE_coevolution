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

import evogpj.evaluation.FitnessFunction;

import evogpj.gp.GPException;
import evogpj.gp.Individual;
import evogpj.gp.MersenneTwisterFast;
import evogpj.gp.Population;
import evogpj.evaluation.TaxFitness;
import evogpj.evaluation.TaxCodeFitness;

import interpreter.misc.Graph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;


import evogpj.operator.Crossover;
import evogpj.operator.CrowdedTournamentSelection;
import evogpj.operator.Initialize;
import evogpj.operator.ListSinglePointCrossover;
import evogpj.operator.Mutate;
import evogpj.operator.Select;
import evogpj.operator.ListMutate;
import evogpj.operator.TournamentSelection;
import evogpj.sort.CrowdingSort;
import evogpj.sort.DominatedCount;
import evogpj.sort.DominatedCount.DominationException;
import evogpj.operator.ListInitialize;

/**
 * This class contains the main method that runs the GP algorithm.
 * 
 * @author Owen Derby
 **/
public class SymbRegMOO {
    
    /* PARAMETERS GOVERNING THE GENETIC PROGRAMMING PROCESS */
    // POPULATION SIZE
    protected int POP_SIZE = Parameters.Defaults.POP_SIZE;
    // NUMBER OF GENERATIONS
    protected int NUM_GENS = Parameters.Defaults.NUM_GENS;
    // start time
    protected Long startTime;
    // TIME OUT
    protected Long TIMEOUT;
    
    // MUTATION RATE
    protected double MUTATION_RATE = Parameters.Defaults.MUTATION_RATE;
    // CROSSOVER RATE
    protected double XOVER_RATE = Parameters.Defaults.XOVER_RATE;
    
    

    // DEFAULT MUTATION OPERATOR
    protected String SELECT = Parameters.Defaults.SELECT;
    // DEFAULT CROSSOVER OPERATOR

    protected String FITNESS = Parameters.Defaults.FITNESS;

//    added by jbrosen, 1/15/2014
    protected int MAX_PRODUCTION_CHOICES = 100;
    
    // RANDOM SEED
    protected Long SEED = Parameters.Defaults.SEED;
    
    /* CANDIDATE SOLUTIONS MAINTAINED DURING THE SEARCH */
    // CURRENT POPULATION
    protected Population pop;
    // OFFSPRING
    protected Population childPop;
    // OFFSPRING + PARENTS
    protected Population totalPop;
    // CURRENT NON-DOMINATED SOLUTIONS
    protected Population paretoFront;
    // CURRENT GENERATION'S BEST INDIVIDUAL
    protected Individual best;
    // BEST INDIVIDUAL OF EACH GENERATION
    protected Population bestPop;
    
    public ArrayList<String> testingFitness;
    /* OPERATORS EMPLOYED IN THE SEARCH PROCESS */
    // RANDOM NUMBER GENERATOR
    protected MersenneTwisterFast rand;
    // INITIALIZATION METHOD
    protected Initialize initialize;
    // CROSSOVER
    protected Crossover xover;
    // SELECTION
    protected Select select;
    // MUTATION
    protected Mutate mutate;
    // FITNESS FUNCTIONS
    protected FitnessFunction fitnessFunction;
    
    
    /* CONTROL FOR END OF EVOLUTIONARY PROCESS*/
    // CURRENT GENERATION
    protected Integer generation;
    // CONTROL FOR END OF PROCESS
    protected Boolean finished;
    

    /**
     * Create an instance of the algorithm. This simply initializes all the
     * operators to the default parameters or whatever they are set to in the
     * passed in properties object. Use {@link #run_population()} to actually
     * run the population for the specified number of generations.
     * <p>
     * If an invalid operator type is specified, then the program will
     * terminate, indicating which parameter is incorrect.
     * 
     * @param props
     *            Properties object created from a .properties file specifying
     *            parameters for the algorithm
     * @param seed
     *            A seed to use for the RNG. This allows for repeating the same
     *            trials over again.
     */
    public SymbRegMOO(Properties props,long timeout) throws IOException {
        this();
        if (timeout > 0)
            TIMEOUT = startTime + (timeout * 1000);
        loadParams(props);
        SEED = Long.parseLong("1391419054419");
        this.testingFitness = new ArrayList<String>();
        create_operators(props,SEED);
    }
    
    public SymbRegMOO(Properties aProps,String propFile,long timeout) throws IOException {
        this();
        Properties props = loadProps(propFile);
        if (timeout > 0)
            TIMEOUT = startTime + (timeout * 1000);
        loadParams(props);
        Object[] presetProperties = (Object[])aProps.stringPropertyNames().toArray();
        for(int i=0;i<presetProperties.length;i++){
            String keyAux = (String)presetProperties[i];
            String valueAux = aProps.getProperty(keyAux);
            props.setProperty(keyAux, valueAux);
        }
        loadParams(props);
        create_operators(props,SEED);
    }
    
    public SymbRegMOO(String propFile,long timeout) throws IOException {
        this();
        if (timeout > 0)
            TIMEOUT = startTime + (timeout * 1000);
        Properties props = loadProps(propFile);
        loadParams(props);
        create_operators(props,SEED);
    }
    
    public SymbRegMOO(String propFile) throws IOException {
        this();
        Properties props = loadProps(propFile);
        loadParams(props);
        create_operators(props,SEED);
    }

    /**
     * Empty constructor, to allow subclasses to override
     */
    public SymbRegMOO() {
        finished = false;
        generation = 0;
        startTime = System.currentTimeMillis();
    }

    /**
     * Read parameters from the Property object and set Algorithm variables.
     * 
     * @see Parameters
     */
    private void loadParams(Properties props) {
        if (props.containsKey(Parameters.Names.SEED))
            SEED = Long.valueOf(props.getProperty(Parameters.Names.SEED)).longValue();
        if (props.containsKey(Parameters.Names.FITNESS))
            FITNESS = props.getProperty(Parameters.Names.FITNESS);            
        if (props.containsKey(Parameters.Names.MUTATION_RATE))
            MUTATION_RATE = Double.valueOf(props.getProperty(Parameters.Names.MUTATION_RATE));
        if (props.containsKey(Parameters.Names.XOVER_RATE))
            XOVER_RATE = Double.valueOf(props.getProperty(Parameters.Names.XOVER_RATE));
        if (props.containsKey(Parameters.Names.POP_SIZE))
            POP_SIZE = Integer.valueOf(props.getProperty(Parameters.Names.POP_SIZE));
        if (props.containsKey(Parameters.Names.NUM_GENS))
            NUM_GENS = Integer.valueOf(props.getProperty(Parameters.Names.NUM_GENS));
    }
    
    /**
     * Handle parsing the FITNESS field (fitness_op), which could contain
     * multiple fitness operators
     * 
     * @return a LinkedHashMap with properly ordered operators and null
     *         FitnessFunctions. This enforces the iteration order
     */
    protected LinkedHashMap<String, FitnessFunction> splitFitnessOperators(String fitnessOpsRaw) {
        LinkedHashMap<String, FitnessFunction> fitnessOperators = new LinkedHashMap<String, FitnessFunction>();
        List<String> fitnessOpsSplit = Arrays.asList(fitnessOpsRaw.split("\\s*,\\s*"));
        for (String f : fitnessOpsSplit) {
            fitnessOperators.put(f, null);
        }
        return fitnessOperators;
    }

    
    /**
     * Create all the operators from the loaded params. Seed is the seed to use
     * for the rng. If specified, d_in is some DataJava to use. Otherwise, d_in
     * should be null and fitness will load in the appropriate data.
     * 
     * @param seed
     * 
     */
    
    private void create_operators(Properties props, long seed) throws IOException {
    	
    	System.out.println("Running evogpj with seed: " + seed);
        rand = new MersenneTwisterFast(seed);
    	Graph graph = new Graph();
    	
    	
     	if (FITNESS.equals("fitness.TaxFitness")) {
     		fitnessFunction = new TaxFitness(graph);
    	}
    	else if (FITNESS.equals("fitness.TaxCodeFitness")) {
    		fitnessFunction = new TaxCodeFitness(graph);
    	}
    	else {
    		System.out.print("Invalid Fitness Function Operator");
    		System.exit(-1);
    	}
     	
     	initialize = new ListInitialize(rand);
    	
     	
        // Set up operators.
        if (SELECT.equals(Parameters.Operators.TOURNEY_SELECT)) {
            select = new TournamentSelection(rand, props);
        } else if (SELECT.equals(Parameters.Operators.CROWD_SELECT)) {
            select = new CrowdedTournamentSelection(rand, props);
        } else {
            System.err.format("Invalid select function %s specified%n", SELECT);
            System.exit(-1);
        }

        mutate = new ListMutate(rand, POP_SIZE);
        xover = new ListSinglePointCrossover(rand);
        
		//initialize
		int SETSIZE = POP_SIZE;
		//int MAX_PRODUCTION_CHOICES = 100;
		
		ArrayList<ArrayList> SET = new ArrayList<ArrayList>();
		for(int i=0;i<SETSIZE;i++){
			ArrayList<Integer> array = new ArrayList<Integer>();
			for(int j=0;j<MAX_PRODUCTION_CHOICES;j++){
				array.add(rand.nextInt(Integer.MAX_VALUE));
			}
			SET.add(array);
		}
		

	        // to set up equalization operator, we need to evaluate all the
	        // individuals first
        pop = initialize.listInitialize(POP_SIZE,SET);
        
        fitnessFunction.evalPop(pop);
        for (Individual ind : pop) {
        	String s = ind.getPhenotype().getPhenotype();
        	s += ", " + ind.getFitness().toString();
        	this.testingFitness.add(s);
        }
        this.testingFitness.add("END OF INITIALIZATION");
        
        /*
         * the DominatedCount class is only useful for when there are multiple objective functions that need to be standardized,
         * While we may need to deal with that kind of thing later, for know we can just compare total fitnesses
         * which we do above
         */
        // calculate domination counts of initial population for tournament selection
        Collections.sort(pop, new FitnessComparator());
        for (int i = 0 ; i < POP_SIZE ; ++i) {
        	pop.get(i).setDominationCount(i);
        }
    }
	
    /**
     * This is the heart of the algorithm. This corresponds to running the
     * {@link #pop} forward one generation
     * <p>
     * Basically while we still need to produce offspring, we select an
     * individual (or two) as parent(s) and perform a genetic operator, chosen
     * at random according to the parameters, to apply to the parent(s) to
     * produce children. Then evaluate the fitness of the new child(ren) and if
     * they are accepted by the equalizer, add them to the next generation.
     * <p>
     * The application of operators is mutually exclusive. That is, for each
     * iteration of this algorithm, we will choose exactly one of crossover,
     * mutation and replication. However, which one we choose is determined by
     * sampling from the distribution specified by the mutation and crossover
     * rates.
     * 
     * @returns a LinkedHashMap mapping fitness function name to the best
     *          individual for that fitness function
     * @throws GPException
     *             if any of the operators receive a individual with an
     *             unexpected genotype, this is an error.
     */
    protected void step() throws GPException {
        // generate children from previous population. don't use elitism
        // here since that's done later
        childPop = new Population();
        Population children;
        while (childPop.size() < POP_SIZE) {
            Individual p1 = select.select(pop);
            double prob = rand.nextDouble();
            // Select exactly one operator to use
            if (prob < XOVER_RATE) {
                Individual p2 = select.select(pop);
                children = xover.crossOver(p1, p2);
                for (Individual ind : children) {    
                    if(!ind.equals(p1) && !ind.equals(p2) && (childPop.size() < POP_SIZE)){
                        childPop.add(ind);
                    }
                }
            } else if (prob < MUTATION_RATE + XOVER_RATE) {
                Individual ind = mutate.mutate(p1);
                if(!ind.equals(p1) && (childPop.size() < POP_SIZE)){
                    childPop.add(ind);
                }
            } 
        }
        
        fitnessFunction.evalPop(childPop);
        
        // combine the children and parents for a total of 2*POP_SIZE
        totalPop = new Population(pop, childPop);
        
        // calculate domination counts of initial population for tournament selection
        Collections.sort(totalPop, new FitnessComparator());
        for (int i = 0 ; i < totalPop.size() ; ++i) {
        	totalPop.get(i).setDominationCount(i);
        }
        
        pop = new Population();
        for (int i =0 ; i < POP_SIZE ; ++i) {
        	pop.add(totalPop.get(i));
        }
        for (Individual ind : pop) {
        	String s = ind.getPhenotype().getPhenotype();
        	s += ", " + ind.getFitness().toString();
        	this.testingFitness.add(s);
        }
        this.testingFitness.add("END OF GENERATION "+this.generation);
        best = pop.get(0);
    }


    /**
     * get the best individual per generation in a Population object
     * 
     * @return the best individual per generation.
     */
    public Population getBestPop(){
        return bestPop;
    }
    /**
    * Run the current population for the specified number of generations FOR CO-EVOLUTION
    * 
    * @return the best individual found.
    */

    /**
    * Run the current population for the specified number of generations.
    * 
    * @return the best individual found.
    */
    public Individual run_population() throws IOException {
        
        bestPop = new Population();
        // get the best individual
        best = pop.get(0);
        System.out.println(best.getFitnesses());
        // record the best individual in models.txt
        bestPop.add(best);
        long timeStamp = (System.currentTimeMillis() - startTime) / 1000;
        System.out.println("ELAPSED TIME: " + timeStamp);
        while ((generation <= NUM_GENS) && (!finished)) {
            System.out.format("Generation %d\n", generation);
            System.out.flush();
            try {
                step();
            } catch (GPException e) {
                System.exit(-1);
            }
            // print information about this generation
            //System.out.format("Statistics: %d " + calculateStats() + "%n", generation);
            System.out.format("Best individual for generation %d:%n", generation);
            double MSE = best.getFitness();
            MSE = ((1-MSE) / (MSE + 1));
            System.out.println("Genotype: "+best.getGenotype().toString());
            System.out.println("Phenotype: "+best.getPhenotype().getPhenotype());
            System.out.println(best.getFitnesses());
            System.out.println(MSE);
            System.out.flush();
            bestPop.add(best);
            timeStamp = (System.currentTimeMillis() - startTime) / 1000;
            System.out.println("ELAPSED TIME: " + timeStamp);
            generation++;
            finished = stopCriteria();
            
        }
        return best;
    }
    
    public boolean stopCriteria(){
        boolean stop = false;
        if( System.currentTimeMillis() >= TIMEOUT){
            System.out.println("Timout exceeded, exiting.");
            return true;
        }
        return stop;
    }
    
    public static Properties loadProps(String propFile) {
            Properties props = new Properties();
            BufferedReader f;
            try {
                    f = new BufferedReader(new FileReader(propFile));
            } catch (FileNotFoundException e) {
                    return null;
            }
            try {
                    props.load(f);
            } catch (IOException e) {
            }
            
            if (Parameters.Defaults.VERBOSE)
            	System.out.println(props.toString());
            return props;
    }
    
    public static String getCurrentTimestamp() {
		Date date = new Date();
		Timestamp stamp = new Timestamp(date.getTime());
		return stamp.toString().replace(" ", "_");
    }
	/*
	 * Main
	 */
	public static void main(String args[]) throws FileNotFoundException {
		boolean bash = false;
//		different output stream depending on whether running from local machine or VM
		PrintStream out;
		String t_stamp = getCurrentTimestamp();
//		local output
		if (!bash)
			out = new PrintStream(new FileOutputStream("C:\\Users\\Jacob\\Documents\\MIT\\SCOTE\\MITRE_coevolution\\Tax\\Tax\\src\\interpreter\\output.txt"));
//		VM output, with time stamp
		else {
			out = new PrintStream(new FileOutputStream("output_"+t_stamp+".txt"));
		}
		
		System.setOut(out);
		
		Properties props = null;
		if (args.length > 0) {
			if(args[0].equals("TaxProperties.properties"))
				props = loadProps(args[0]);
		}
		if (props == null) {
			props = new Properties();
		}
		
		long seed = System.currentTimeMillis();
		
		if (props.containsKey("rng_seed"))
			seed = Integer.parseInt(props.getProperty("rng_seed"));

		SymbRegMOO srm;
		System.out.println("running trial ");
		try {
			srm = new SymbRegMOO(props, seed);
			
			Individual best = srm.run_population();
			if (best == null)
				System.out.println("failed");
			else
				System.out.println("terminated with genotype: " + best.getGenotype().toString());
				System.out.println("terminated with phenotype: " + best.getPhenotype().getPhenotype());
				System.out.println("terminated with fitness: " + best.getFitness());
				
				
				for (String s : srm.testingFitness) {
					System.out.println(s);
				}
		}

		
		
		catch (IOException e) {
			System.out.println("\nSomething's wrong\n");
		}
	}
    
    /**
     * calculate some useful statistics about the current generation of the
     * population
     *
     * @return String of the following form:
     *         "avg_fitness fitness_std_dev avg_size size_std_dev"
     */
    protected String calculateStats() {
        double mean_f = 0.0;
        double mean_l = 0.0;
        double min_f = 1.0;
        double max_f = -1.0;
        for (Individual i : pop) {
            mean_f += i.getFitness();
            mean_l += i.getGenotype().getGenotype().size();
            if (i.getFitness() < min_f) min_f = i.getFitness();
            if (i.getFitness() > max_f) max_f = i.getFitness();
        }
        mean_f /= pop.size();
        mean_l /= pop.size();
        double std_f = 0.0;
        double std_l = 0.0;
        for (Individual i : pop) {
            std_f += Math.pow(i.getFitness() - mean_f, 2);
            std_l += Math.pow(i.getGenotype().getGenotype().size() - mean_l, 2);
        }
        std_f = Math.sqrt(std_f / pop.size());
        std_l = Math.sqrt(std_l / pop.size());
        return String.format("%.5f %.5f %f %f %9.5f %9.5f", mean_f, std_f,min_f, max_f, mean_l, std_l);
    }
    
    /**
     * Save text to a filepath
     * @param filepath
     * @param text
     */
    protected void saveText(String filepath, String text, Boolean append) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filepath,append));
            PrintWriter printWriter = new PrintWriter(bw);
            printWriter.write(text);
            printWriter.flush();
            printWriter.close();
        } catch (IOException e) {
            System.exit(-1);
        }
    }
 
//    compares individuals based on their fitness value
    public static class FitnessComparator implements Comparator<Individual> {
    	@Override
    	public int compare(Individual i1, Individual i2) {
    		return i2.getFitness().compareTo(i1.getFitness());
    	}
    }
}
