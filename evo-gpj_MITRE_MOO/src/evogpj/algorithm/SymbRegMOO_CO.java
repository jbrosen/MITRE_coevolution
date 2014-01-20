package evogpj.algorithm;

import interpreter.misc.Graph;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Properties;

import evogpj.algorithm.SymbRegMOO.FitnessComparator;
import evogpj.evaluation.FitnessFunction;
import evogpj.evaluation.TaxCodeFitness;
import evogpj.evaluation.TaxFitness;
import evogpj.gp.GPException;
import evogpj.gp.Individual;	
import evogpj.gp.MersenneTwisterFast;
import evogpj.gp.Population;
import evogpj.operator.Crossover;
import evogpj.operator.Initialize;
import evogpj.operator.ListInitialize;
import evogpj.operator.ListMutate;
import evogpj.operator.ListSinglePointCrossover;
import evogpj.operator.Mutate;
import evogpj.operator.Select;
import evogpj.operator.TournamentSelection;

public class SymbRegMOO_CO {
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
    
    // RANDOM SEED
    protected Long SEED = Parameters.Defaults.SEED;
    
    protected LinkedHashMap<String, ListInitialize> initializers;
    protected LinkedHashMap<String, Population> pops;
    protected LinkedHashMap<String, Population> bestPops;
    protected LinkedHashMap<String, Individual> bestOnes;
    protected LinkedHashMap<String, Population> childPops;
    protected LinkedHashMap<String, Population> totalPops;
    protected Population childPop;
	
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
    protected LinkedHashMap<String, FitnessFunction> fitnessFunctions;
    
    protected int MAX_PRODUCTION_CHOICES = 100;
    protected int NUM_CHOOSE = Parameters.Defaults.NUM_CHOOSE;
    /* CONTROL FOR END OF EVOLUTIONARY PROCESS*/
    // CURRENT GENERATION
    protected Integer generation;
    // CONTROL FOR END OF PROCESS
    protected Boolean finished;
    
    public SymbRegMOO_CO(Properties props,long timeout) throws IOException {
        this();
        if (timeout > 0)
            TIMEOUT = startTime + (timeout * 1000);
        loadParams(props);
        create_operators(props,SEED);
    }
	
    /**
     * Empty constructor, to allow subclasses to override
     */
    public SymbRegMOO_CO() {
        fitnessFunctions = new LinkedHashMap<String, FitnessFunction>();
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
        if (props.containsKey(Parameters.Names.MUTATION_RATE))
            MUTATION_RATE = Double.valueOf(props.getProperty(Parameters.Names.MUTATION_RATE));
        if (props.containsKey(Parameters.Names.XOVER_RATE))
            XOVER_RATE = Double.valueOf(props.getProperty(Parameters.Names.XOVER_RATE));
        if (props.containsKey(Parameters.Names.POP_SIZE))
            POP_SIZE = Integer.valueOf(props.getProperty(Parameters.Names.POP_SIZE));
        if (props.containsKey(Parameters.Names.NUM_GENS))
            NUM_GENS = Integer.valueOf(props.getProperty(Parameters.Names.NUM_GENS));
     
    }
    
    private void create_operators(Properties props, long seed) throws IOException {
    	System.out.println("Running evogpj with seed: " + seed);
        rand = new MersenneTwisterFast(seed);
    	Graph graph = new Graph();
    	fitnessFunctions.clear();
    	
		fitnessFunctions.put("TaxFitness", new TaxFitness(graph));
		fitnessFunctions.put("TaxCodeFitness", new TaxCodeFitness(graph));
//      NOTE: both populations use same random number for initializer, maybe change in the future
		initializers = new LinkedHashMap<String, ListInitialize>();
    	initializers.put("TaxFitness",new ListInitialize(rand));
    	initializers.put("TaxCodeFitness",new ListInitialize(rand));
    	
//    	Tournament selector by default, maybe change later
        select = new TournamentSelection(rand, props);

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
		
    	pops = new LinkedHashMap<String, Population>();
    	for (String fname : new ArrayList<String>(initializers.keySet())) {
    		pops.put(fname,initializers.get(fname).listInitialize(POP_SIZE, SET));
    	}
		
    	
//    	get the two population names in an ArrayList
    	ArrayList<String> fitnessNames = new ArrayList<String>(fitnessFunctions.keySet());
//    	This is where the population subset selector is going to go (SelectPop interface)
//    	for now, just select the whole other population
    	Population schemes = pops.get(fitnessNames.get(0));
    	Population clauses = pops.get(fitnessNames.get(1));
//    	first evaluate fitness for all of the schemes against all of the clauses
    	FitnessFunction f1 = fitnessFunctions.get(fitnessNames.get(0));
    	for (Individual ind : pops.get(fitnessNames.get(0))) {
    		f1.eval(ind, getSubPopulation(clauses));
    	}
//    	next, evaluate fitness for all of the clauses against all of the schemes
    	FitnessFunction f2 = fitnessFunctions.get(fitnessNames.get(1));
    	for (Individual ind : pops.get(fitnessNames.get(1))) {
    		f2.eval(ind, getSubPopulation(schemes));
    	}
//    	sort the two populations in descending order by fitness and set domination count as the index in the list because it is the number
//    	of individuals that have a higher fitness than them
    	Collections.sort(schemes, new FitnessComparator());
    	Collections.sort(clauses, new FitnessComparator());
    	for (int i=0 ; i<schemes.size() ; ++i )
    		schemes.get(i).setDominationCount(i);
    	for (int i=0 ; i<clauses.size() ; ++i )
    		clauses.get(i).setDominationCount(i);
    }
    
    /*
     * Given a Population aPop, returns a Population object of NUM_CHOOSE randomly chosen Individuals from aPop
     */
    protected Population getSubPopulation(Population aPop) {
    	Population copy = new Population(aPop);
    	Collections.shuffle(copy);
    	Population ret = new Population();
    	for (int i=0;i<NUM_CHOOSE;++i) {
    		ret.add(copy.get(i));
    	}
    	return ret;
    }
    
    
    protected void step_evo() throws GPException {
//    	get the names of the two fitness functions
    	ArrayList<String> fitnessNames = new ArrayList<String>(fitnessFunctions.keySet());
//    	for both populations, this is basically the same as the previous step method
    	for (String fit : fitnessNames) {
	        // generate children from previous population. don't use elitism
	        // here since that's done later
	        childPop = new Population();
	        Population children;
	    	
//	        get the population that is evaluated the fitness function fit
	        Population thisPop = pops.get(fit);
	        while (childPop.size() < POP_SIZE) {
//	        	pick a random individual from that population using selection algorithm specified in Parameters class
	            Individual p1 = select.select(thisPop);
	            double prob = rand.nextDouble();
	            // Crossover with some probability
	            if (prob < XOVER_RATE) {
//	            	use crossover and selection method chosen in Parameters class
	                Individual p2 = select.select(thisPop);
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
	        

//	        Compare each individual from both populations to all individuals from the other
//	        pretty kludgy, fix later
	        Population otherPop;
	        if (fit == "TaxFitness") {
	        	otherPop = pops.get("TaxCodeFitness");
	        }
	        else
	        	otherPop = pops.get("TaxFitness");
	        FitnessFunction f = fitnessFunctions.get(fit);
	        
	        for (Individual ind : childPop) {
	        	f.eval(ind, getSubPopulation(otherPop));
	        }
	        
//	        Combine the child population and the last population and sort them by fitness
	        Population tmpTotalPop = new Population(thisPop,childPop);
	        Collections.sort(tmpTotalPop, new FitnessComparator());
	        for (int i=0 ; i<tmpTotalPop.size(); ++i) {
	        	tmpTotalPop.get(i).setDominationCount(i);
	        }
	        
//	        JUST make the first (best) POP_SIZE elements of tmpTotalPop the new population
	        Population newPop = new Population();
	        for (int i=0 ; i<POP_SIZE ; ++i) {
	        	newPop.add(tmpTotalPop.get(i));
	        }
	        pops.put(fit, newPop);
	        

//	        BECAUSE there is only one fitness function, we can just pull the first from the list, which will be the best
//	        fitnessed individual
//			NOTE this could cause problems, possibly go back to this
	        bestOnes.put(fit, pops.get(fit).get(0));
    	}
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
    public LinkedHashMap<String,Individual> run_population_evo() throws IOException {
  	
//    Hashmap of the best population that accumulates over the generations of the two populations
  	  bestPops = new LinkedHashMap<String,Population>();
      bestPops.put("TaxFitness", new Population());
      bestPops.put("TaxCodeFitness", new Population());
//      to record the best individuals from each generation
      bestOnes = new LinkedHashMap<String,Individual>();
      
//      add the best initial individual scheme to its bestPops element
      Population schemes = pops.get("TaxFitness");
      bestOnes.put("TaxFitness",schemes.get(0));
      Population bestSchemes = bestPops.get("TaxFitness");
      bestSchemes.add(schemes.get(0));
      
//      add the best initial individual clause to its bestPops element
      Population clauses = pops.get("TaxCodeFitness");
      bestOnes.put("TaxCodeFitness",clauses.get(0));
      Population bestClauses = bestPops.get("TaxCodeFitness");
      bestClauses.add(clauses.get(0));
      
      
      long timeStamp = (System.currentTimeMillis() - startTime) / 1000;
      System.out.println("ELAPSED TIME: " + timeStamp);
//      step_evo for each generation (see above)
      while ((generation <= NUM_GENS) && (!finished)) {
          System.out.format("Generation %d\n", generation);
          System.out.flush();
          try {
              step_evo();
          } catch (GPException e) {
              System.exit(-1);
          }
//          get the best overall scheme and clause from the generation and add it to the bestOnes HashMap
          Individual bestScheme = bestOnes.get("TaxFitness");
          bestSchemes = bestPops.get("TaxFitness");
          bestSchemes.add(bestScheme);
          
          Individual bestClause = bestOnes.get("TaxCodeFitness");
          bestClauses = bestPops.get("TaxCodeFitness");
          bestClauses.add(bestClause);
          
          timeStamp = (System.currentTimeMillis() - startTime) / 1000;
          System.out.println("ELAPSED TIME: " + timeStamp);
          generation++;
          finished = stopCriteria();
      }
      return bestOnes;
  }
    public static String getCurrentTimestamp() {
		Date date = new Date();
		Timestamp stamp = new Timestamp(date.getTime());
		return stamp.toString().replace(" ", "_");
    }
    
    public boolean stopCriteria(){
        boolean stop = false;
        if( System.currentTimeMillis() >= TIMEOUT){
            System.out.println("Timout exceeded, exiting.");
            return true;
        }
        return stop;
    }
	
	/*
	 * Main
	 */
	public static void main(String args[]) throws FileNotFoundException {
		boolean bash = true;
		
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

		SymbRegMOO_CO srm;
		System.out.println("running trial ");
		try {
			srm = new SymbRegMOO_CO(props, seed);
			LinkedHashMap<String,Individual> bests = srm.run_population_evo();
			ArrayList<String> fitnessNames = new ArrayList<String>(bests.keySet());
			for (String fname : fitnessNames) {
				Individual bestOne = (Individual)bests.get(fname);
				if (bestOne == null) {
					System.out.println("failed for fitness function "+fname);
				}
				else {
					System.out.println("Info for fitness function "+fname);
					System.out.println("terminated with genotype: " + bestOne.getGenotype().toString());
					System.out.println("terminated with phenotype: " + bestOne.getPhenotype().getPhenotype());
				}
			}
		}
		catch (IOException e) {
			System.out.println("\nSomething's wrong\n");
		}
	}

}
