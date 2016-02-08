/**
 * Copyright (c) 2015, Ecole des Mines de Nantes
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    This product includes software developed by the <organization>.
 * 4. Neither the name of the <organization> nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.chocosolver.solver.search.strategy;

import org.chocosolver.solver.search.strategy.assignments.DecisionOperator;
import org.chocosolver.solver.search.strategy.selectors.IntValueSelector;
import org.chocosolver.solver.search.strategy.selectors.RealValueSelector;
import org.chocosolver.solver.search.strategy.selectors.SetValueSelector;
import org.chocosolver.solver.search.strategy.selectors.VariableSelector;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainRandom;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainRandomBound;
import org.chocosolver.solver.search.strategy.selectors.values.SetDomainMin;
import org.chocosolver.solver.search.strategy.selectors.variables.ActivityBased;
import org.chocosolver.solver.search.strategy.selectors.variables.DomOverWDeg;
import org.chocosolver.solver.search.strategy.selectors.variables.MinDelta;
import org.chocosolver.solver.search.strategy.selectors.variables.Random;
import org.chocosolver.solver.search.strategy.strategy.*;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.RealVar;
import org.chocosolver.solver.variables.SetVar;

import static org.chocosolver.solver.search.strategy.selectors.ValSelectorFactory.*;
import static org.chocosolver.solver.search.strategy.selectors.VarSelectorFactory.*;

public class SearchStrategyFactory {

    // ************************************************************************************
   	// GENERIC PATTERNS
   	// ************************************************************************************

    /**
   	 * Use the last conflict heuristic as a pluggin to improve a former search heuristic
   	 * Should be set after specifying a search strategy.
   	 * @return last conflict strategy
   	 */
    public static AbstractStrategy lastConflict(AbstractStrategy formerSearch) {
   		return new LastConflict(formerSearch.getVariables()[0].getModel(), formerSearch,1);
   	}

   	/**
   	 * Make the input search strategy greedy, that is, decisions can be applied but not refuted.
   	 * @param search a search heuristic building branching decisions
   	 * @return a greedy form of search
   	 */
    public static AbstractStrategy greedySearch(AbstractStrategy search){
   		return new GreedyBranching(search);
   	}

    public static AbstractStrategy sequencer(AbstractStrategy... searches){
   		return new StrategiesSequencer(searches);
   	}

    // ************************************************************************************
    // SETVAR STRATEGIES
    // ************************************************************************************

    /**
     * Generic strategy to branch on set variables
     *
     * @param varS         variable selection strategy
     * @param valS         integer  selection strategy
     * @param enforceFirst branching order true = enforce first; false = remove first
	 * @param sets         SetVar array to branch on
     * @return a strategy to instantiate sets
     */
    public static SetStrategy setVarSearch(VariableSelector<SetVar> varS, SetValueSelector valS, boolean enforceFirst, SetVar... sets) {
        return new SetStrategy(sets, varS, valS, enforceFirst);
    }

    /**
     * strategy to branch on sets by choosing the first unfixed variable and forcing its first unfixed value
     *
     * @param sets variables to branch on
     * @return a strategy to instantiate sets
     */
    public static SetStrategy setVarSearch(SetVar... sets) {
        return setVarSearch(new MinDelta(), new SetDomainMin(), true, sets);
    }

    // ************************************************************************************
    // REALVAR STRATEGIES
    // ************************************************************************************

    /**
     * Generic strategy to branch on real variables, based on domain splitting
     * @param varS  variable selection strategy
     * @param valS  strategy to select where to split domains
     * @param rvars RealVar array to branch on
     * @return a strategy to instantiate reals
     */
    public static RealStrategy realVarSearch(VariableSelector<RealVar> varS, RealValueSelector valS, RealVar... rvars) {
        return new RealStrategy(rvars, varS, valS);
    }

    /**
     * strategy to branch on real variables by choosing sequentially the next variable domain
     * to split in two, wrt the middle value
     * @param reals variables to branch on
     * @return a strategy to instantiate real variables
     */
    public static RealStrategy realVarSearch(RealVar... reals) {
        return realVarSearch(nextVarSelector(), midRValSelector(), reals);
    }

    // ************************************************************************************
    // INTVAR STRATEGIES
    // ************************************************************************************

    /**
     * Builds your own search strategy based on <b>binary</b> decisions.
     *
     * @param varSelector defines how to select a variable to branch on.
     * @param valSelector defines how to select a value in the domain of the selected variable
     * @param decisionOperator defines how to modify the domain of the selected variable with the selected value
     * @param vars         variables to branch on
     * @return a custom search strategy
     */
    public static IntStrategy intVarSearch(VariableSelector<IntVar> varSelector,
                                     IntValueSelector valSelector,
                                     DecisionOperator<IntVar> decisionOperator,
                                     IntVar... vars) {
        return new IntStrategy(vars, varSelector, valSelector, decisionOperator);
    }

    /**
     * Builds your own assignment strategy based on <b>binary</b> decisions.
     * Selects a variable X and a value V to make the decision X = V.
     * Note that value assignments are the public static decision operators.
     * Therefore, they are not mentioned in the search heuristic name.
     * @param varSelector defines how to select a variable to branch on.
     * @param valSelector defines how to select a value in the domain of the selected variable
     * @param vars         variables to branch on
     * @return a custom search strategy
     */
    public static IntStrategy intVarSearch(VariableSelector<IntVar> varSelector,
                                     IntValueSelector valSelector,
                                     IntVar... vars) {
        return intVarSearch(varSelector, valSelector, DecisionOperator.int_eq, vars);
    }

    /**
     * Assignment strategy which selects a variable according to <code>DomOverWDeg</code> and assign it to its lower bound
     * @param vars list of variables
     * @return assignment strategy
     */
    public static AbstractStrategy<IntVar> domOverWDegSearch(IntVar... vars) {
        return new DomOverWDeg(vars, 0, minValSelector());
    }

    /**
     * Create an Activity based search strategy.
     * <p>
     * <b>"Activity-Based Search for Black-Box Constraint Propagramming Solver"<b/>,
     * Laurent Michel and Pascal Van Hentenryck, CPAIOR12.
     * <br/>
     * Uses public static parameters (GAMMA=0.999d, DELTA=0.2d, ALPHA=8, RESTART=1.1d, FORCE_SAMPLING=1)
     *
     * @param vars collection of variables
     * @return an Activity based search strategy.
     */
    public static AbstractStrategy<IntVar> activityBasedSearch(IntVar... vars) {
        return new ActivityBased(vars);
    }

    /**
     * Randomly selects a variable and assigns it to a value randomly taken in
     * - the domain in case the variable has an enumerated domain
     * - {LB,UB} (one of the two bounds) in case the domain is bounded
     *
     * @param vars list of variables
     * @param seed a seed for random
     * @return assignment strategy
     */
    public static IntStrategy randomSearch(IntVar[] vars, long seed) {
        IntValueSelector value = new IntDomainRandom(seed);
        IntValueSelector bound = new IntDomainRandomBound(seed);
        IntValueSelector selector = (IntValueSelector) var -> {
            if (var.hasEnumeratedDomain()) {
                return value.selectValue(var);
            } else {
                return bound.selectValue(var);
            }
        };
        return intVarSearch(new Random<IntVar>(seed), selector, vars);
    }

    // ************************************************************************************
    // SOME EXAMPLES OF STRATEGIES YOU CAN BUILD
    // ************************************************************************************

    /**
     * Assigns the first non-instantiated variable to its lower bound.
     * @param vars list of variables
     * @return int strategy based on value assignments
     */
    public static IntStrategy firstLBSearch(IntVar... vars) {
        return intVarSearch(firstVarSelector(), minValSelector(), vars);
    }

    /**
     * Assigns the first non-instantiated variable to its upper bound.
     * @param vars list of variables
     * @return assignment strategy
     */
    public static IntStrategy firstUBSearch(IntVar... vars) {
        return intVarSearch(firstVarSelector(), maxValSelector(), vars);
    }

    /**
     * Assigns the non-instantiated variable of smallest domain size to its lower bound.
     * @param vars list of variables
     * @return assignment strategy
     */
    public static IntStrategy minDomLBSearch(IntVar... vars) {
        return intVarSearch(minDomVarSelector(), minValSelector(), vars);
    }

    /**
     * Assigns the non-instantiated variable of smallest domain size to its upper bound.
     * @param vars list of variables
     * @return assignment strategy
     */
    public static IntStrategy minDomUBSearch(IntVar... vars) {
        return intVarSearch(minDomVarSelector(), maxValSelector(), vars);
    }
}