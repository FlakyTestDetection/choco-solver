/**
 * This file is part of choco-solver, http://choco-solver.org/
 *
 * Copyright (c) 2017, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.search.strategy.selectors.variables;

import gnu.trove.list.array.TIntArrayList;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Propagator;
import org.chocosolver.solver.search.loop.monitors.FailPerPropagator;
import org.chocosolver.solver.search.strategy.assignments.DecisionOperatorFactory;
import org.chocosolver.solver.search.strategy.decision.Decision;
import org.chocosolver.solver.search.strategy.selectors.values.IntValueSelector;
import org.chocosolver.solver.search.strategy.strategy.AbstractStrategy;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.objects.IntMap;

/**
 * Implementation of DowOverWDeg[1].
 *
 * [1]: F. Boussemart, F. Hemery, C. Lecoutre, and L. Sais, Boosting Systematic Search by Weighting Constraints, ECAI-04.
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 12/07/12
 */
public class DomOverWDeg extends AbstractStrategy<IntVar>{

    /**
     * Failure per propagators counter
     */
    private FailPerPropagator counter;

    /**
     * Kind of duplicate of pid2ari to limit calls of backtrackable objects
     */
    private IntMap pid2arity;

    /**
     * Temporary. Stores index of variables with the same (best) score
     */
    private TIntArrayList bests;

    /**
     * Randomness to break ties
     */
    private java.util.Random random;

    /**
     * The way value is selected for a given variable
     */
    private IntValueSelector valueSelector;

    /**
     * Creates a DomOverWDeg variable selector
     *
     * @param variables     decision variables
     * @param seed          seed for breaking ties randomly
     * @param valueSelector a value selector
     */
    public DomOverWDeg(IntVar[] variables, long seed, IntValueSelector valueSelector) {
        super(variables);
        Model model = variables[0].getModel();
        counter = new FailPerPropagator(model.getCstrs(), model);
        pid2arity = new IntMap(model.getCstrs().length * 3 / 2 + 1, -1);
        bests = new TIntArrayList();
        this.valueSelector = valueSelector;
        random = new java.util.Random(seed);
    }


    @Override
    public Decision<IntVar> computeDecision(IntVar variable) {
        if (variable == null || variable.isInstantiated()) {
            return null;
        }
        int currentVal = valueSelector.selectValue(variable);
        return variable.getModel().getSolver().getDecisionPath().makeIntDecision(variable, DecisionOperatorFactory.makeIntEq(), currentVal);
    }

    @Override
    public Decision<IntVar> getDecision() {
        IntVar best = null;
        bests.resetQuick();
        pid2arity.clear();
        long _d1 = Integer.MAX_VALUE;
        long _d2 = 0;
        for (int idx = 0; idx < vars.length; idx++) {
            int dsize = vars[idx].getDomainSize();
            if (dsize > 1) {
                int weight = weight(vars[idx]);
                long c1 = dsize * _d2;
                long c2 = _d1 * weight;
                if (c1 < c2) {
                    bests.clear();
                    bests.add(idx);
                    _d1 = dsize;
                    _d2 = weight;
                } else if (c1 == c2) {
                    bests.add(idx);
                }
            }
        }
        if (bests.size() > 0) {
            int currentVar = bests.get(random.nextInt(bests.size()));
            best = vars[currentVar];
        }
        return computeDecision(best);
    }

    private int weight(IntVar v) {
        int w = 1;
        int nbp =v.getNbProps();
        for (int i = 0; i < nbp; i++) {
            Propagator prop = v.getPropagator(i);
            int pid = prop.getId();
            // if the propagator has been already evaluated
            if (pid2arity.get(pid) > -1) {
                w += counter.getFails(prop);
            } else {
                // the arity of this propagator is not yet known
                int futVars = prop.arity();
                assert futVars > -1;
                pid2arity.put(pid, futVars);
                if (futVars > 1) {
                    w += counter.getFails(prop);
                }
            }
        }
        return w;
    }
}
