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

package solver.variables;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import solver.ICause;
import solver.exception.ContradictionException;
import solver.search.strategy.enumerations.values.heuristics.HeuristicVal;
import solver.variables.domain.IIntDomain;
import solver.variables.domain.delta.IntDelta;

/**
 * @author Charles Prud'homme
 * @since 18 nov. 2010
 */
public interface IntVar extends Variable<IntDelta> {

    /**
     * Removes <code>value</code>from the domain of <code>this</code>. The instruction comes from <code>propagator</code>.
     * <ul>
     * <li>If <code>value</code> is out of the domain, nothing is done and the return value is <code>false</code>,</li>
     * <li>if removing <code>value</code> leads to a dead-end (domain wipe-out),
     * a <code>ContradictionException</code> is thrown,</li>
     * <li>otherwise, if removing <code>value</code> from the domain can be done safely,
     * the event type is created (the original event can be promoted) and observers are notified
     * and the return value is <code>true</code></li>
     * </ul>
     *
     * @param value value to remove from the domain (int)
     * @param cause removal releaser
     * @return true if the value has been removed, false otherwise
     * @throws solver.exception.ContradictionException
     *          if the domain become empty due to this action
     */
    boolean removeValue(int value, ICause cause) throws ContradictionException;

    /**
     * Removes values between [<code>from, to</code>] from the domain of <code>this</code>. The instruction comes from <code>propagator</code>.
     * <ul>
     * <li>If union between values and the current domain is empty, nothing is done and the return value is <code>false</code>,</li>
     * <li>if removing a <code>value</code> leads to a dead-end (domain wipe-out),
     * a <code>ContradictionException</code> is thrown,</li>
     * <li>otherwise, if removing at least a <code>value</code> from the domain can be done safely,
     * the event type is created (the original event can be promoted) and observers are notified
     * and the return value is <code>true</code></li>
     * </ul>
     *
     * @param from lower bound of the interval to remove (int)
     * @param to upper bound of the intervak to remove(int)
     * @param cause removal releaser
     * @return true if the value has been removed, false otherwise
     * @throws solver.exception.ContradictionException
     *          if the domain become empty due to this action
     */
    boolean removeInterval(int from, int to, ICause cause) throws ContradictionException;

    /**
     * Instantiates the domain of <code>this</code> to <code>value</code>. The instruction comes from <code>propagator</code>.
     * <ul>
     * <li>If the domain of <code>this</code> is already instantiated to <code>value</code>,
     * nothing is done and the return value is <code>false</code>,</li>
     * <li>If the domain of <code>this</code> is already instantiated to another value,
     * then a <code>ContradictionException</code> is thrown,</li>
     * <li>Otherwise, the domain of <code>this</code> is restricted to <code>value</code> and the observers are notified
     * and the return value is <code>true</code>.</li>
     * </ul>
     *
     * @param value instantiation value (int)
     * @param cause instantiation releaser
     * @return true if the instantiation is done, false otherwise
     * @throws ContradictionException if the domain become empty due to this action
     */
    boolean instantiateTo(int value, ICause cause) throws ContradictionException;

    /**
     * Updates the lower bound of the domain of <code>this</code> to <code>value</code>.
     * The instruction comes from <code>propagator</code>.
     * <ul>
     * <li>If <code>value</code> is smaller than the lower bound of the domain, nothing is done and the return value is <code>false</code>,</li>
     * <li>if updating the lower bound to <code>value</code> leads to a dead-end (domain wipe-out),
     * a <code>ContradictionException</code> is thrown,</li>
     * <li>otherwise, if updating the lower bound to <code>value</code> can be done safely,
     * the event type is created (the original event can be promoted) and observers are notified
     * and the return value is <code>true</code></li>
     * </ul>
     *
     * @param value new lower bound (included)
     * @param cause updating releaser
     * @return true if the lower bound has been updated, false otherwise
     * @throws ContradictionException if the domain become empty due to this action
     */
    boolean updateLowerBound(int value, ICause cause) throws ContradictionException;

    /**
     * Updates the upper bound of the domain of <code>this</code> to <code>value</code>.
     * The instruction comes from <code>propagator</code>.
     * <ul>
     * <li>If <code>value</code> is greater than the upper bound of the domain, nothing is done and the return value is <code>false</code>,</li>
     * <li>if updating the upper bound to <code>value</code> leads to a dead-end (domain wipe-out),
     * a <code>ContradictionException</code> is thrown,</li>
     * <li>otherwise, if updating the upper bound to <code>value</code> can be done safely,
     * the event type is created (the original event can be promoted) and observers are notified
     * and the return value is <code>true</code></li>
     * </ul>
     *
     * @param value new upper bound (included)
     * @param cause update releaser
     * @return true if the upper bound has been updated, false otherwise
     * @throws ContradictionException if the domain become empty due to this action
     */
    boolean updateUpperBound(int value, ICause cause) throws ContradictionException;

    /**
     * Checks if a value <code>v</code> belongs to the domain of <code>this</code>
     *
     *
     * @param value int
     * @return <code>true</code> if the value belongs to the domain of <code>this</code>, <code>false</code> otherwise.
     */
    boolean contains(int value);

    /**
     * Checks wether <code>this</code> is instantiated to <code>val</code>
     *
     * @param value int
     * @return true if <code>this</code> is instantiated to <code>val</code>, false otherwise
     */
    boolean instantiatedTo(int value);

    /**
     * Retrieves the current value of the variable if instantiated, otherwier the lower bound.
     *
     * @return the current value (or lower bound if not yet instantiated).
     */
    int getValue();

    /**
     * Retrieves the lower bound of the variable
     *
     * @return the lower bound
     */
    int getLB();

    /**
     * Retrieves the upper bound of the variable
     *
     * @return the upper bound
     */
    int getUB();

    /**
     * Returns the domain of <code>this</code>
     *
     * @return a IIntDomain object
     */
    IIntDomain getDomain();

    /**
     * Returns the size of the domain of <code>this</code>
     *
     * @return size of the domain
     */
    int getDomainSize();

    //todo: move to domain

    int nextValue(int v);

    int previousValue(int v);

    /**
     * Provides an iterator over the values contained within the domain of <code>this</code>
     *
     * @return a disposable iterator (#dispose() must be called after)
     */
    //todo: move to domain
    DisposableIntIterator getIterator();

    /**
     * Defines the value iterator, ie the way to iterate over the domain's values, for <code>this</code>
     *
     * @param heuristicVal
     */
    void setHeuristicVal(HeuristicVal heuristicVal);

    /**
     * Returns the value iterator of <code>this</code>
     *
     * @return value iterator
     */
    HeuristicVal getHeuristicVal();

    boolean hasEnumeratedDomain();
}