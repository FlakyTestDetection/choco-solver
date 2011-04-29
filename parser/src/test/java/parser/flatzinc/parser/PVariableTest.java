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

package parser.flatzinc.parser;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import solver.variables.BoolVar;
import solver.variables.IntVar;
import solver.variables.domain.BitSetIntDomain;
import solver.variables.domain.BooleanDomain;
import solver.variables.domain.IntervalIntDomain;

/*
* User : CPRUDHOM
* Mail : cprudhom(a)emn.fr
* Date : 12 janv. 2010
* Since : Choco 2.1.1
* 
*/
public class PVariableTest {

    FZNParser fzn;
    @BeforeTest
    public void before(){
        fzn = new FZNParser();
    }

    @Test
    public void testBool(){
        TerminalParser.parse(fzn.PAR_VAR_DECL, "var bool: bb::var_is_introduced::is_defined_var;");
        Object o = fzn.map.get("bb");
        Assert.assertTrue(BoolVar.class.isInstance(o));
        BoolVar oi = (BoolVar)o;
        Assert.assertEquals(oi.getDomain().getClass(), BooleanDomain.class);
        Assert.assertEquals(oi.getLB(), 0);
        Assert.assertEquals(oi.getUB(), 1);
    }

    @Test
    public void testBound(){
        TerminalParser.parse(fzn.PAR_VAR_DECL, "var 0 .. 9: A::var_is_introduced;");
        Object o = fzn.map.get("A");
        Assert.assertTrue(IntVar.class.isInstance(o));
        IntVar oi = (IntVar)o;
        Assert.assertEquals(oi.getDomain().getClass(), IntervalIntDomain.class);
        Assert.assertEquals(oi.getLB(), 0);
        Assert.assertEquals(oi.getUB(), 9);
        Assert.assertEquals(oi.getDomainSize(), 10);
    }

    @Test
    public void testEnum(){
        TerminalParser.parse(fzn.PAR_VAR_DECL, "var {0,3,18}: B::var_is_introduced;");
        Object o = fzn.map.get("B");
        Assert.assertTrue(IntVar.class.isInstance(o));
        IntVar oi = (IntVar)o;
        Assert.assertEquals(oi.getDomain().getClass(), BitSetIntDomain.class);
        Assert.assertEquals(oi.getLB(), 0);
        Assert.assertEquals(oi.getUB(), 18);
        Assert.assertEquals(oi.getDomainSize(), 3);
    }
//
//    @Test
//    public void testSetBound(){
//        TerminalParser.parse(fzn.PAR_VAR_DECL, "var set of 0..9: S::var_is_introduced;");
//        Object o = fzn.map.get("S");
//        Assert.assertTrue(SetVariable.class.isInstance(o));
//        SetVariable oi = (SetVariable)o;
//        Assert.assertNull(oi.getValues());
//        Assert.assertEquals(0, oi.getLowB());
//        Assert.assertEquals(9, oi.getUppB());
//    }
//
//    @Test
//    public void testSetEnum(){
//        TerminalParser.parse(fzn.PAR_VAR_DECL, "var set of {0,3,18}: S::var_is_introduced;");
//        Object o = fzn.map.get("S");
//        Assert.assertTrue(SetVariable.class.isInstance(o));
//        SetVariable oi = (SetVariable)o;
//        Assert.assertNotNull(oi.getValues());
//        Assert.assertArrayEquals(new int[]{0,3,18}, oi.getValues());
//    }
//
    @Test
    public void testArray(){
        TerminalParser.parse(fzn.PAR_VAR_DECL, "array[1 .. 3] of var 0 .. 9: C::output_array([ 1 .. 3 ]);");
        Object o = fzn.map.get("C");
        Assert.assertTrue(o.getClass().isArray());
        IntVar[] oi = (IntVar[])o;
        Assert.assertEquals(oi.length, 3);
        Assert.assertEquals(oi[0].getName(), "C_1");
        Assert.assertEquals(oi[0].getDomainSize(), 10);
    }

    @Test
    public void testArray2(){
        TerminalParser.parse(fzn.PAR_VAR_DECL, "var 1 .. 5: a ::output_var;");
        TerminalParser.parse(fzn.PAR_VAR_DECL, "var 1 .. 5: b::output_var;");
        TerminalParser.parse(fzn.PAR_VAR_DECL, "var 1 .. 5: c::output_var;");
        TerminalParser.parse(fzn.PAR_VAR_DECL, "array[1 .. 3] of var 1 .. 5: alpha = [ a, b, c];");
        Object o = fzn.map.get("alpha");
        Assert.assertTrue(o.getClass().isArray());
        IntVar[] oi = (IntVar[])o;
        Assert.assertEquals(oi.length, 3);
        Assert.assertEquals(oi[0].getName(), "a");
        Assert.assertEquals(oi[0].getDomainSize(), 5);
    }

}