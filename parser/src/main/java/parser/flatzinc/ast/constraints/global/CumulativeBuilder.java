/*
 * Copyright (c) 1999-2012, Ecole des Mines de Nantes
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Ecole des Mines de Nantes nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package parser.flatzinc.ast.constraints.global;

import parser.flatzinc.ast.constraints.IBuilder;
import parser.flatzinc.ast.expression.EAnnotation;
import parser.flatzinc.ast.expression.Expression;
import solver.Solver;
import solver.constraints.Constraint;
import solver.constraints.nary.Sum;
import solver.constraints.nary.scheduling.Cumulative;
import solver.variables.IntVar;
import solver.variables.VariableFactory;
import solver.variables.view.Views;

import java.util.List;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 26/01/11
 */
public class CumulativeBuilder implements IBuilder {

    @Override
    public Constraint build(Solver solver, String name, List<Expression> exps, List<EAnnotation> annotations) {
        IntVar[] starts = exps.get(0).toIntVarArray(solver);
        IntVar[] durations = exps.get(1).toIntVarArray(solver);
        IntVar[] resources = exps.get(2).toIntVarArray(solver);
        IntVar[] ends = new IntVar[starts.length];
        boolean decomp = false; // can be true IFF durations, resources and limit are fixed!

        for (int i = 0; i < starts.length; i++) {
            if (durations[i].instantiated()) {
                ends[i] = Views.offset(starts[i], durations[i].getValue());
            } else {
                ends[i] = VariableFactory.bounded(starts[i].getName() + "_" + durations[i].getName(),
                        starts[i].getLB() + durations[i].getLB(),
                        starts[i].getUB() + durations[i].getUB(),
                        solver);
                solver.post(Sum.eq(new IntVar[]{starts[i], durations[i]}, ends[i], solver));
                decomp = true;
            }
            if (!resources[i].instantiated()) {
                decomp = true;
            }
        }
        IntVar limit = exps.get(3).intVarValue(solver);
        if (!limit.instantiated()) {
            decomp = true;
        }

        if (!decomp) {
            if (starts.length > 10000) {
                return new Cumulative(starts, durations, ends, resources, limit, solver, Cumulative.Type.GREEDY);
            } else {
                return new Cumulative(starts, durations, ends, resources, limit, solver, Cumulative.Type.DYNAMIC_SWEEP);
            }
        }
        return null;
    }
}
