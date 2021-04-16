/// <summary> 
/// Author:    Garrett Keefe 
/// Partner:   None 
/// Date:      01/31/2020 
/// Course:    CS 3500, University of Utah, School of Computing 
/// Copyright: CS 3500 and Garrett Keefe - This work may not be copied for use in Academic Coursework. 
/// 
/// I, Garrett Keefe, certify that I wrote this code from scratch and did not copy it in part or whole from  
/// another source.  All references used in the completion of the assignment are cited in my README file. 
/// 
/// File Contents 
/// 
///    MSTest file which test the functionality of the formula class.
///    
/// </summary>

using Microsoft.VisualStudio.TestTools.UnitTesting;
using SpreadsheetUtilities;
using System.Collections.Generic;
using System;

namespace FormulaTests
{
    [TestClass]
    public class FormulaTests
    {
        /// <summary>
        /// Tests ToString for the formula class.
        /// </summary>
        [TestMethod]
        public void TestToString()
        {
            Formula f1 = new Formula("1+1");
            Assert.AreEqual("1+1", f1.ToString());
        }

        /// <summary>
        /// Test Evaluate for a simple formula
        /// </summary>
        [TestMethod]
        public void TestEvaluate()
        {
            Formula f1 = new Formula("1+1");
            double x = 2;
            Assert.AreEqual(x, f1.Evaluate(s => 0));
        }

        /// <summary>
        /// checks that ToString works with variables
        /// </summary>
        [TestMethod]
        public void TestToStringWithVariables()
        {
            Formula f1 = new Formula("X1+X2");
            Assert.AreEqual("X1+X2", f1.ToString());
        }

        /// <summary>
        /// Tests that .Equals() works for a simple formula
        /// </summary>
        [TestMethod]
        public void TestEquality()
        {
            Formula f1 = new Formula("X1+X2");
            Formula f2 = new Formula(" X1  +  X2 ");
            Assert.AreEqual(true, f1.Equals(f2));
        }

        /// <summary>
        /// Tests that .Equals correctly returns false when given a bad type
        /// </summary>
        [TestMethod]
        public void TestEqualityIsFalseObject()
        {
            Formula f1 = new Formula("X1+X2");
            int f2 = 2;
            Assert.AreEqual(false, f1.Equals(f2));
        }

        /// <summary>
        /// Tests that ==, != works for many types of values, both in the false and true direction
        /// </summary>
        [TestMethod]
        public void TestEqualityOperator()
        {
            Formula f1 = new Formula("X1+X2");
            Formula f2 = new Formula(" X1  +  X2 ");
            Formula f3 = new Formula("1+6");
            Assert.AreEqual(true, f1 == f2);
            Assert.AreEqual(true, f1 != f3);
            Assert.AreEqual(false, f1 == f3);
            Assert.AreEqual(false, f1 != f2);
        }

        /// <summary>
        /// Tests that ==, != works for many types of values including nulls, in both the false and true direction
        /// </summary>
        [TestMethod]
        public void TestEqualityOperatorBothNull()
        {
            Formula f1 = null;
            Formula f2 = null;
            Formula f3 = new Formula("1+6");
            Assert.AreEqual(true, f1 == f2);
            Assert.AreEqual(true, f1 != f3);
            Assert.AreEqual(false, f1 == f3);
            Assert.AreEqual(false, f1 != f2);
            Assert.AreEqual(false, f3 == f2);
            Assert.AreEqual(true, f3 != f2);
        }

        /// <summary>
        /// Tests that GetHashCode() correctly returns and fails on similar and dissimilar objects
        /// </summary>
        [TestMethod]
        public void TestGetHashCode()
        {
            Formula f1 = new Formula("X1+X2");
            Formula f2 = new Formula(" X1  +  X2 ");
            Formula f3 = new Formula("1+6");
            Assert.AreEqual(f1.ToString(), f2.ToString());
            Assert.AreEqual(f1.GetHashCode(), f2.GetHashCode());
            Assert.AreNotEqual(f1.GetHashCode(), f3.GetHashCode());
        }

        /// <summary>
        /// Test the GetVariables function correctly find the right tokens.
        /// </summary>
        [TestMethod]
        public void TestGetVariables()
        {
            Formula f1 = new Formula("X1+X2");
            List<string> variables = new List<string>();
            foreach (string x in f1.GetVariables())
            {
                variables.Add(x);
            }
            Assert.AreEqual(true, variables.Contains("X1"));
            Assert.AreEqual(true, variables.Contains("X2"));
            Assert.AreEqual(false, variables.Contains("X3"));
        }

        /// <summary>
        /// Test that the constructor finds that the string has too many operators.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(FormulaFormatException))]
        public void FailingConstructorTest1()
        {
            Formula f1 = new Formula("------");
            Assert.AreEqual("1+1", f1.ToString());
        }

        /// <summary>
        /// Tests that constructor finds a operator which doesn't have enough number to evaluate
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(FormulaFormatException))]
        public void FailingConstructorTest2()
        {
            Formula f1 = new Formula("1+1-");
            Assert.AreEqual("1+1", f1.ToString());
        }

        /// <summary>
        /// Tests that constructor finds one too many closing parentheses
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(FormulaFormatException))]
        public void FailingConstructorTest3()
        {
            Formula f1 = new Formula("(1+1))");
            Assert.AreEqual("1+1", f1.ToString());
        }

        /// <summary>
        /// Tests that constructor finds an empty string
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(FormulaFormatException))]
        public void FailingConstructorTest4()
        {
            Formula f1 = new Formula("");
            Assert.AreEqual("1+1", f1.ToString());
        }

        /// <summary>
        /// Tests that constructor finds one too many opening parentheses
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(FormulaFormatException))]
        public void FailingConstructorTest5()
        {
            Formula f1 = new Formula("((1+1)");
            Assert.AreEqual("1+1", f1.ToString());
        }

        /// <summary>
        /// Tests that constructor finds an improperly place closing parentheses
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(FormulaFormatException))]
        public void FailingConstructorTest6()
        {
            Formula f1 = new Formula("(1+)1");
            Assert.AreEqual("1+1", f1.ToString());
        }

        /// <summary>
        /// Tests that constructor finds two number values not seperated by an operator
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(FormulaFormatException))]
        public void FailingConstructorTest7()
        {
            Formula f1 = new Formula("1 X1 +1");
            Assert.AreEqual("1+1", f1.ToString());
        }

        /// <summary>
        /// Tests that constructor finds a variable which does not satisfy the isValid delegate
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(FormulaFormatException))]
        public void FailingConstructorTest8()
        {
            Formula f1 = new Formula("X1 +1", s => s, s => false);
            Assert.AreEqual("X1+1", f1.ToString());
        }

        /// <summary>
        /// Tests that a formula with a single number will know to return said number
        /// </summary>
        [TestMethod]
        public void SingleNumberTest()
        {
            Formula f1 = new Formula("5");
            double x = 5;
            Assert.AreEqual(x, f1.Evaluate(s => 0));
        }

        /// <summary>
        /// Tests that a formula will return the numberical value of a variable if it is the only item in the formula
        /// </summary>
        [TestMethod]
        public void SingleVariableTest()
        {
            Formula f1 = new Formula("X1");
            double x = 5;
            Assert.AreEqual(x, f1.Evaluate(s => 5));
        }

        /// <summary>
        /// Tests addition works correctly
        /// </summary>
        [TestMethod]
        public void TestAddition()
        {
            Formula f1 = new Formula("5 + 23");
            double x = 28;
            Assert.AreEqual(x, f1.Evaluate(s => 0));
        }

        /// <summary>
        /// Tests subtraction works correctly
        /// </summary>
        [TestMethod]
        public void TestSubtraction()
        {
            Formula f1 = new Formula("23 - 5");
            double x = 18;
            Assert.AreEqual(x, f1.Evaluate(s => 0));
        }

        /// <summary>
        /// Tests that multiplication works correctly
        /// </summary>
        [TestMethod]
        public void TestMultiplication()
        {
            Formula f1 = new Formula("5 * 5");
            double x = 25;
            Assert.AreEqual(x, f1.Evaluate(s => 0));
        }

        /// <summary>
        /// Tests that division works correctly
        /// </summary>
        [TestMethod]
        public void TestDivision()
        {
            Formula f1 = new Formula("23 / 5");
            double x = 23;
            double y = 5;
            double z = x / y;
            Assert.AreEqual(z.ToString(), f1.Evaluate(s => 0).ToString());
        }

        /// <summary>
        /// Tests addition works correctly even give a decimal point value
        /// </summary>
        [TestMethod]
        public void TestAdditionWithFloatingPoints()
        {
            Formula f1 = new Formula("23.7 + 5.2");
            double x = 28.9;
            Assert.AreEqual(x, f1.Evaluate(s => 0));
        }
        
        /// <summary>
        /// Tests addition with a scientific notation number
        /// </summary>
        [TestMethod]
        public void TestAdditionWithScientificNotation()
        {
            Formula f1 = new Formula("2.35e1 + 5");
            double x = 28.5;
            Assert.AreEqual(x, f1.Evaluate(s => 0)); 
        }

        /// <summary>
        /// Tests that Evaluate works along multiple branches given man different inputs.
        /// </summary>
        [TestMethod]
        public void TestComplexMultiVariable1()
        {
            Formula f1 = new Formula("x1+(x2+(x3+(x4+(x5+x6))))");
            double x = 6;
            Assert.AreEqual(x, f1.Evaluate(s => 1));
        }

        /// <summary>
        /// Tests that Evaluate works along multiple branches given man different inputs.
        /// </summary>
        [TestMethod]
        public void TestComplexMultiVariable2()
        {
            Formula f1 = new Formula("y1*3-8/2+4*(8-9*2)/14*x7");
            double x = -23;
            double y = 7;
            double z = -3.2857142857142865;
            
            Assert.AreEqual(z, f1.Evaluate(s => 5));
        }

        /// <summary>
        /// Tests that a division by zero error is correctly caught and returned as a FormulaError
        /// </summary>
        [TestMethod]
        public void TestDivisionByZero()
        {
            Formula f1 = new Formula("3/0");
            FormulaError x = new FormulaError("Division by zero.");
            Assert.AreEqual(x, f1.Evaluate(s => 0));
        }

        /// <summary>
        /// Tests that a parentheses is correctly evaluated and then the division preceding it is as well
        /// </summary>
        [TestMethod]
        public void TestDivisionAfterParentheses()
        {
            Formula f1 = new Formula("3/(1-2)");
            double x = -3;
            Assert.AreEqual(x, f1.Evaluate(s => 0));
        }

        /// <summary>
        /// tests multiple additions happening one after another
        /// </summary>
        [TestMethod]
        public void TestDoubleAddition()
        {
            Formula f1 = new Formula("3 + 5 + 7");
            double x = 15;
            Assert.AreEqual(x, f1.Evaluate(s => 0));
        }

        /// <summary>
        /// Tests that Evaluate correctly catches an invalid variable and returns it as a FormulaError
        /// </summary>
        [TestMethod]
        public void TestInvalidLookup()
        {
            Formula f1 = new Formula("X1/(1-2)");
            FormulaError x = new FormulaError("Invalid Variable.");
            Assert.AreEqual(x, f1.Evaluate(s => { if (s.Equals("X2")) return 3; throw new ArgumentException(); }));
        }
    }
}
