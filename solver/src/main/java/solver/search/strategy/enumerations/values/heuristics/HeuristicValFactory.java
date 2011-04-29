/**
 *  Copyright (c) 1999-2011, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package solver.search.strategy.enumerations.values.heuristics;

import gnu.trove.THashMap;
import solver.search.strategy.enumerations.values.heuristics.nary.SeqN;
import solver.search.strategy.enumerations.values.heuristics.unary.DropN;
import solver.search.strategy.enumerations.values.heuristics.unary.Filter;
import solver.search.strategy.enumerations.values.heuristics.unary.FirstN;
import solver.search.strategy.enumerations.values.heuristics.zeroary.UnsafeEnum;
import solver.search.strategy.enumerations.values.metrics.Metric;
import solver.search.strategy.enumerations.values.predicates.Member;
import solver.variables.domain.IIntDomain;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 31/01/11
 */
public class HeuristicValFactory {

    HeuristicValFactory() {
    }

    /**
     * Build an heuristic val for RotateLeft  : SeqN(DropN(orig, metric), FirstN(orig, metric))
     * <br/>
     * BEWARE: metric already defined its own action (could be different from default action in DropN and FirstN)
     *
     * @param orig   sub heuristic val
     * @param metric number of element to rotate
     * @return {@link SeqN}
     */
    public static SeqN rotateLeft(HeuristicVal orig, Metric metric) {
        return new SeqN(new DropN(orig, metric), new FirstN(orig.duplicate(new THashMap<HeuristicVal, HeuristicVal>()), metric));
    }

    /**
     * Build an heuristic val for RotateLeft  : SeqN(DropN(orig, metric, action), FirstN(orig, metric, action)).
     * <br/>
     * BEWARE: metric already defined its own action (could be different from <code>action</code> in parameter)
     *
     * @param orig   sub heuristic val
     * @param metric number of element to rotate
     * @param action used for DropN and FirstN
     * @return {@link SeqN}
     */
    public static SeqN rotateLeft(HeuristicVal orig, Metric metric, Action action) {
        return new SeqN(
                new DropN(orig, metric, action),
                new FirstN(orig.duplicate(new THashMap<HeuristicVal, HeuristicVal>()), metric, action));
    }

    /**
     * Build an UnsafeEnum heuristic val from a domain
     * TODO : mieux commenter
     *
     * @param domain parameter
     * @return {@link UnsafeEnum}
     */
    public static UnsafeEnum unsafeEnum(IIntDomain domain) {
        return new UnsafeEnum(domain.getLB(), 1, domain.getUB());
    }

    /**
     * Build an UnsafeEnum heuristic val from a domain and an action
     * TODO : mieux commenter
     *
     * @param domain parameter
     * @param action action
     * @return {@link UnsafeEnum}
     */
    public static UnsafeEnum unsafeEnum(IIntDomain domain, Action action) {
        return new UnsafeEnum(domain.getLB(), 1, domain.getUB(), action);
    }

    /**
     * Build an UnsafeEnum heuristic val from a domain
     * TODO : mieux commenter
     *
     * @param from  starting value
     * @param delta gap
     * @param to    ending value
     * @return {@link UnsafeEnum}
     */
    public static UnsafeEnum unsafeEnum(int from, int delta, int to) {
        return new UnsafeEnum(from, delta, to);
    }

    /**
     * Build an UnsafeEnum heuristic val from a domain and an action
     * TODO : mieux commenter
     *
     * @param from   starting value
     * @param delta  gap
     * @param to     ending value
     * @param action action
     * @return {@link UnsafeEnum}
     */
    public static UnsafeEnum unsafeEnum(int from, int delta, int to, Action action) {
        return new UnsafeEnum(from, delta, to, action);
    }

    /**
     * Build the following heuristic val: Filter(Member(domain), UnsafeEnum(domain))
     *
     * @param domain domain of the variable
     * @return a {@link Filter}
     */
    public static Filter enumVal(IIntDomain domain) {
        return new Filter(new Member(domain), unsafeEnum(domain));
    }

    /**
     * Build the following heuristic val: Filter(Member(domain, action), UnsafeEnum(domain, action)
     *
     * @param domain domain of the variable
     * @param action action of Member and UnsafeEnum
     * @return a {@link Filter}
     */
    public static Filter enumVal(IIntDomain domain, Action action) {
        return new Filter(new Member(domain, action), unsafeEnum(domain, action));
    }

    /**
     * Build the following heuristic val: Filter(Member(domain), UnsafeEnum(domain))
     *
     * @param domain domain of the variable
     * @param from   starting value
     * @param delta  gap
     * @param to     ending value
     * @return a {@link Filter}
     */
    public static Filter enumVal(IIntDomain domain, int from, int delta, int to) {
        return new Filter(new Member(domain), unsafeEnum(from, delta, to));
    }

    /**
     * Build the following heuristic val: Filter(Member(domain, action), UnsafeEnum(domain, action)
     *
     * @param domain domain of the variable
     * @param from   starting value
     * @param delta  gap
     * @param to     ending value
     * @param action action of Member and UnsafeEnum
     * @return a {@link Filter}
     */
    public static Filter enumVal(IIntDomain domain, int from, int delta, int to, Action action) {
        return new Filter(new Member(domain, action), unsafeEnum(from, delta, to, action));
    }
}