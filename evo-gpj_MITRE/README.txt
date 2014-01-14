# Copyright (c) 2012 Evolutionary Design and Optimization Group
# 
# Licensed under the MIT License.
#
# See the "LICENSE" file for a copy of the license.
#

EvoGPJ is a Genetic Programming library in Java.

Notes and Warnings:

Currently the library is dependent (to varying degrees) upon the Tree
genotype implementation. Ideally, operators would either not depend
upon a particular genotype implementation or would make it more
explicit. This should probably be addressed in the future. For now,
just be aware of it.

This library is intended to be easily readable and extensible. The
cost of this goal is that there are multiple versions of GP currently
implemented in, with their specific operators and
representations. Ironically, this may make it harder to
read/understand.

Currently, individuals which produce NaN values are assigned a fitness
of 0. Is there something smarter we should be doing?

TODO


Old TODO (good ideas, not necessarily pertinent)
* Termination criteria and function and terminal sets should be
  extracted from the fitness function? We don't want if(problem_type)
  littered everywhere, eg in AlgorithmBase.

* Fix implementation of Silva's DynOpEq and replicate results:

Have emailed her -- she uses v = -v; v = v + abs(max_v) + abs(min_v)
to shift/scale fitness-minimising values to be positive, to be
suitable for fitness bins. But all our problems use fitness-maximising
anyway, right?

* Add a symbolic classification fitness function.

* Mutation Equalizer?

* An experiment comparing Dummy versus DynamicTree eqalisation for
  fitness and size

* HVL' mutation

  http://www.eecs.berkeley.edu/~gdurrett/DurNeuOre_foga11.pdf


* An experiment on Order and Majority

Vary population size, with and without mutation, to show that for
smaller population sizes, without mutation, GP can fail simply because
the right material is not available. Large population can compensate
for this. Analyse block acquisition.

Needs HVL'.

* Extreme dependence problem

Inherit from SemanticFitness

* Max problem:

Fitness is value of the tree -- aim is to make as large a value as
possible. Non-terminals and terminals: {+, *, 0.5}, max depth D =
3..8, best fitness is 4 ^ 2 ^ {D - 3} (root node counts as depth 0).
population 200, generations 500, 99.5% crossover, no mutation.
Tournament selection size 2-8. Initial depth 5, or D.

Needs HVL' for Frank's proposed experiment -- a hill-climber for Max.

* Implement stuff to suit Hadoop:

** Writable, Comparable, Cloneable (?) and Serializable on Individual

** Factory method on FitnessEvaluator?

** Island will be moral equivalent of AlgorithmBase.

Feeder method on Island will feed Individuals to Hadoop

* Implement lawnmower problem, and maze problem from Cuccu & Gomez paper?





DONE

* parameters for operator selection

How do you handle dependancies? It's up to the user.

* Best of run case - add all intervening bins with capacity of 1

* tournament+equalization operator

* auto accept brood (don't perform operator equalizer)

* parametrize maximum depth of xover and mutate (make all obey max depth, not size)

* Setting the seed - for multiple runs, make random seed set in deterministic way

* A parameters/properties mechanism

* One-line output per generation:

generation_number best_fitness mean_fitness fitness_sd mean_size size_sd

* Fix bug with elitism versus equalisation

* Create JavaDocs - focus on library specific features

* Operators should be on Individual(s) or List<Individual>

And return the same.

* BroodSelection

Crossed over but not mutated - new fitness not calculated? - mutated
individuals are discarded? Fixed.

* Add Order and Majority



