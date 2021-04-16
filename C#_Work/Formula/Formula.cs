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
///    Formula class which contains, evaluates, and prints a constructed valid formula.
///    
/// </summary>


using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Collections;

namespace SpreadsheetUtilities
{
    /// <summary>
    /// Represents formulas written in standard infix notation using standard precedence
    /// rules.  The allowed symbols are non-negative numbers written using double-precision 
    /// floating-point syntax (without unary preceeding '-' or '+'); 
    /// variables that consist of a letter or underscore followed by 
    /// zero or more letters, underscores, or digits; parentheses; and the four operator 
    /// symbols +, -, *, and /.  
    /// 
    /// Spaces are significant only insofar that they delimit tokens.  For example, "xy" is
    /// a single variable, "x y" consists of two variables "x" and y; "x23" is a single variable; 
    /// and "x 23" consists of a variable "x" and a number "23".
    /// 
    /// Associated with every formula are two delegates:  a normalizer and a validator.  The
    /// normalizer is used to convert variables into a canonical form, and the validator is used
    /// to add extra restrictions on the validity of a variable (beyond the standard requirement 
    /// that it consist of a letter or underscore followed by zero or more letters, underscores,
    /// or digits.)  Their use is described in detail in the constructor and method comments.
    /// </summary>
    public class Formula
    {

        //the underlying Array which contains the formula.
        private ArrayList formulaArray;
        /// <summary>
        /// Creates a Formula from a string that consists of an infix expression written as
        /// described in the class comment.  If the expression is syntactically invalid,
        /// throws a FormulaFormatException with an explanatory Message.
        /// 
        /// The associated normalizer is the identity function, and the associated validator
        /// maps every string to true.  
        /// </summary>
        public Formula(String formula) :
            this(formula, s => s, s => true)
        {
        }

        /// <summary>
        /// Creates a Formula from a string that consists of an infix expression written as
        /// described in the class comment.  If the expression is syntactically incorrect,
        /// throws a FormulaFormatException with an explanatory Message.
        /// 
        /// The associated normalizer and validator are the second and third parameters,
        /// respectively.  
        /// 
        /// If the formula contains a variable v such that normalize(v) is not a legal variable, 
        /// throws a FormulaFormatException with an explanatory message. 
        /// 
        /// If the formula contains a variable v such that isValid(normalize(v)) is false,
        /// throws a FormulaFormatException with an explanatory message.
        /// 
        /// Suppose that N is a method that converts all the letters in a string to upper case, and
        /// that V is a method that returns true only if a string consists of one letter followed
        /// by one digit.  Then:
        /// 
        /// new Formula("x2+y3", N, V) should succeed
        /// new Formula("x+y3", N, V) should throw an exception, since V(N("x")) is false
        /// new Formula("2x+y3", N, V) should throw an exception, since "2x+y3" is syntactically incorrect.
        /// </summary>
        public Formula(String formula, Func<string, string> normalize, Func<string, bool> isValid)
        {
            //checks that the string is a valid string.
            if(string.IsNullOrEmpty(formula))
                throw new FormulaFormatException("Empty or null string was passed.");
            //initializes the validOperators, tokenArray to be added to, and the amount of each type of parentheses.
            ArrayList validOperators = new ArrayList() { "*", "/", "+", "-", "(", ")" };
            ArrayList tokenArray = new ArrayList();
            int openingParentheses = 0;
            int closingParentheses = 0;
            
            foreach(string x in GetTokens(formula))
            {
                //if it is not a validOperator, the it must be a number or variable.
                if(!validOperators.Contains(x))
                {
                    if(double.TryParse(x, out double result))
                    {
                        tokenArray.Add(result.ToString());
                    }
                    else
                    {
                        if(isValid(normalize(x)))
                            tokenArray.Add(normalize(x));
                        else
                            throw new FormulaFormatException("One of the Tokens was an invalid variable.");
                    }
                }
                else
                {
                    if(x.Equals("("))
                    {
                        openingParentheses++;
                        tokenArray.Add(x);
                    }
                    //if for the first token, we do not find either a number, variable or opening parentheses, we
                    //throw an error.
                    else if(tokenArray.Count == 0)
                    {
                        throw new FormulaFormatException("The first token was not a number, variable, or opening parentheses.");
                    }

                    else if(x.Equals(")"))
                    {
                        closingParentheses++;
                        tokenArray.Add(x);
                        //if there are too many closing parentheses, the we have a mismatched pair and it throws an 
                        // exception.
                        if(closingParentheses > openingParentheses)
                            throw new FormulaFormatException("Found a closing paratheses without a pair.");
                    }
                    else
                    {
                        tokenArray.Add(x);
                    }
                }
                if(!parenthesesAndOperatorRule(tokenArray))
                    throw new FormulaFormatException("A invalid token was found after a parentheses or operator.");
                if(!numberAndVariableRule(tokenArray))
                    throw new FormulaFormatException("A invalid token was found after a number, variable, or parentheses.");
            }
            //must have as many closing as opening parentheses at the end of the Formula.
            if(closingParentheses != openingParentheses)
                throw new FormulaFormatException("Formula didn't have a pair for one or more parentheses.");
            if(!tokenArray.lastTokenIsValid())
                throw new FormulaFormatException("Last token in the formula was not a number, variable, or closing parentheses.");
            formulaArray = tokenArray;
        }

        /// <summary>
        /// checks to make sure that if the 2nd to last element is a operator or parentheses, that another operator does
        /// not show up right after, causing an invalid formula.
        /// </summary>
        /// <param name="array"></param>
        /// <returns></returns>
        private bool parenthesesAndOperatorRule(ArrayList array)
        {
            ArrayList elementsToBeChecked = new ArrayList(){"(", "*", "/", "+", "-"};
            ArrayList undesirableElements = new ArrayList(){")", "*", "/", "+", "-"};
            //checks if the 2nd to last element is the one we are looking for.
            if(array.Count > 1 && elementsToBeChecked.Contains(array[array.Count - 2]))
            {
                //checks if the next element is an invalid token.
                return !undesirableElements.Contains(array[array.Count - 1]);
            }
            return true;
        }

        /// <summary>
        /// checks to make sure that if the 2nd to last element is a number or variable, that another number does
        /// not show up right after, causing an invalid formula.
        /// </summary>
        /// <param name="array"> array to be checked through </param>
        /// <returns> true if the rule is followed correctly </returns>
        private bool numberAndVariableRule(ArrayList array)
        {
            ArrayList elementsToBeChecked = new ArrayList(){")", "*", "/", "+", "-"};
            ArrayList undesirableElements = new ArrayList(){"(", "*", "/", "+", "-"};
            //checks that the 2nd to last element is what we are looking for.
            if(array.Count > 1 && !undesirableElements.Contains(array[array.Count - 2]))
            {
                //checks if the next element is the wrong type of token.
                return elementsToBeChecked.Contains(array[array.Count - 1]);
            }
            return true;
        }

        /// <summary>
        /// Evaluates this Formula, using the lookup delegate to determine the values of
        /// variables.  When a variable symbol v needs to be determined, it should be looked up
        /// via lookup(normalize(v)). (Here, normalize is the normalizer that was passed to 
        /// the constructor.)
        /// 
        /// For example, if L("x") is 2, L("X") is 4, and N is a method that converts all the letters 
        /// in a string to upper case:
        /// 
        /// new Formula("x+7", N, s => true).Evaluate(L) is 11
        /// new Formula("x+7").Evaluate(L) is 9
        /// 
        /// Given a variable symbol as its parameter, lookup returns the variable's value 
        /// (if it has one) or throws an ArgumentException (otherwise).
        /// 
        /// If no undefined variables or divisions by zero are encountered when evaluating 
        /// this Formula, the value is returned.  Otherwise, a FormulaError is returned.  
        /// The Reason property of the FormulaError should have a meaningful explanation.
        ///
        /// This method should never throw an exception.
        /// </summary>
        public object Evaluate(Func<string, double> lookup)
        {
            //Defines initial Stacks, the valid operators for Evaluate
            ArrayList validOperators = new ArrayList() { "*", "/", "+", "-", "(", ")" };
            Stack<double> valueStack = new Stack<double>();
            Stack<string> operatorStack = new Stack<string>();
            //Iterates through the tokens of the expression
            foreach(string token in formulaArray)
            {
                if(!validOperators.Contains(token))
                {
                    double currentValue;
                    if(double.TryParse(token, out double result))
                        currentValue = result;
                    else
                    {
                        try
                        {
                            currentValue = lookup(token);
                        }
                        catch
                        {
                            return new FormulaError("Invalid Variable.");
                        }
                    }
                    valueStack.Push(currentValue);
                    if(operatorStack.hasOnTop("*"))
                    {
                        operatorStack.Pop();
                        valueStack.multiplyTopValues();
                    }
                    else if(operatorStack.hasOnTop("/"))
                    {
                        if(valueStack.Peek().Equals(0))
                            return new FormulaError("Division by zero.");
                        operatorStack.Pop();
                        valueStack.divideTopValues();
                    }
                }
                else
                {
                    if (token.Equals("*") || token.Equals("/") || token.Equals("("))
                    {
                        operatorStack.Push(token);
                    }
                    //Completes the Parentheses when the ending token is found
                    else if (token.Equals(")"))
                    {
                        if (operatorStack.hasOnTop("+"))
                        {
                            operatorStack.Pop();
                            valueStack.addTopValues();
                        }
                        else if (operatorStack.hasOnTop("-"))
                        {
                            operatorStack.Pop();
                            valueStack.subtractTopValues();
                        }

                        operatorStack.Pop();

                        if (operatorStack.hasOnTop("*"))
                        {
                            operatorStack.Pop();
                            valueStack.multiplyTopValues();
                        }
                        else if (operatorStack.hasOnTop("/"))
                        {
                            if (valueStack.Peek().Equals(0))
                                return new FormulaError("Division by zero.");
                            operatorStack.Pop();
                            valueStack.divideTopValues();
                        }
                    }
                    else
                    {
                        if (operatorStack.hasOnTop("+"))
                        {
                            operatorStack.Pop();
                            valueStack.addTopValues();
                        }
                        else if (operatorStack.hasOnTop("-"))
                        {
                            operatorStack.Pop();
                            valueStack.subtractTopValues();
                        }
                        operatorStack.Push(token);
                    }
                }
            }
            
            if (operatorStack.Count > 0)
            {
                if (operatorStack.hasOnTop("+"))
                {
                    operatorStack.Pop();
                    valueStack.addTopValues();
                }
                else if (operatorStack.hasOnTop("-"))
                {
                    operatorStack.Pop();
                    valueStack.subtractTopValues();
                }
            }

            return valueStack.Pop();
        }

        /// <summary>
        /// Enumerates the normalized versions of all of the variables that occur in this 
        /// formula.  No normalization may appear more than once in the enumeration, even 
        /// if it appears more than once in this Formula.
        /// 
        /// For example, if N is a method that converts all the letters in a string to upper case:
        /// 
        /// new Formula("x+y*z", N, s => true).GetVariables() should enumerate "X", "Y", and "Z"
        /// new Formula("x+X*z", N, s => true).GetVariables() should enumerate "X" and "Z".
        /// new Formula("x+X*z").GetVariables() should enumerate "x", "X", and "z".
        /// </summary>
        public IEnumerable<String> GetVariables()
        {
            ArrayList operators = new ArrayList() { "*", "/", "+", "-", "(", ")" };
            IEnumerable<string> variables = new List<string>();
            foreach(string token in formulaArray)
            {
                //if the token is not a double, operator, or if it is not already contained, appends it to the list.
                if(!double.TryParse(token, out double _) && !operators.Contains(token) && !variables.Contains(token))
                {
                    variables = variables.Append(token).ToList();
                }
            }
            return variables;
        }

        /// <summary>
        /// Returns a string containing no spaces which, if passed to the Formula
        /// constructor, will produce a Formula f such that this.Equals(f).  All of the
        /// variables in the string should be normalized.
        /// 
        /// For example, if N is a method that converts all the letters in a string to upper case:
        /// 
        /// new Formula("x + y", N, s => true).ToString() should return "X+Y"
        /// new Formula("x + Y").ToString() should return "x+Y"
        /// </summary>
        public override string ToString()
        {
            //creates a stringbuilder and adds to it for every element in the formulaArray
            StringBuilder stringOfFormula = new StringBuilder();
            foreach(string x in formulaArray)
            {
                stringOfFormula.Append(x);
            }
            return stringOfFormula.ToString();
        }

        /// <summary>
        /// If obj is null or obj is not a Formula, returns false.  Otherwise, reports
        /// whether or not this Formula and obj are equal.
        /// 
        /// Two Formulae are considered equal if they consist of the same tokens in the
        /// same order.  To determine token equality, all tokens are compared as strings 
        /// except for numeric tokens and variable tokens.
        /// Numeric tokens are considered equal if they are equal after being "normalized" 
        /// by C#'s standard conversion from string to double, then back to string. This 
        /// eliminates any inconsistencies due to limited floating point precision.
        /// Variable tokens are considered equal if their normalized forms are equal, as 
        /// defined by the provided normalizer.
        /// 
        /// For example, if N is a method that converts all the letters in a string to upper case:
        ///  
        /// new Formula("x1+y2", N, s => true).Equals(new Formula("X1  +  Y2")) is true
        /// new Formula("x1+y2").Equals(new Formula("X1+Y2")) is false
        /// new Formula("x1+y2").Equals(new Formula("y2+x1")) is false
        /// new Formula("2.0 + x7").Equals(new Formula("2.000 + x7")) is true
        /// </summary>
        public override bool Equals(object obj)
        {
            if(!(obj is Formula))
                return false;
            //checks that the string representations are the same and returns true if so, also checks that obj isn't null.
            if(!(obj is null) && this.ToString().Equals(obj.ToString()))
                return true;
            return false;
        }

        /// <summary>
        /// Reports whether f1 == f2, using the notion of equality from the Equals method.
        /// Note that if both f1 and f2 are null, this method should return true.  If one is
        /// null and one is not, this method should return false.
        /// </summary>
        public static bool operator ==(Formula f1, Formula f2)
        {
            if (f1 is null && f2 is null)
                return true;
            if (f1 is null && !(f2 is null))
                return false;
            if (!(f1 is null) && f2 is null)
                return false;
            if (f1.Equals(f2))
                return true;
            return false;
        }

        /// <summary>
        /// Reports whether f1 != f2, using the notion of equality from the Equals method.
        /// Note that if both f1 and f2 are null, this method should return false.  If one is
        /// null and one is not, this method should return true.
        /// </summary>
        public static bool operator !=(Formula f1, Formula f2)
        {
            if (f1 is null && !(f2 is null))
                return true;
            if (!(f1 is null) && f2 is null)
                return true;
            if (f1 is null && f2 is null)
                return false;
            if (!f1.Equals(f2))
                return true;
            return false;
        }

        /// <summary>
        /// Returns a hash code for this Formula.  If f1.Equals(f2), then it must be the
        /// case that f1.GetHashCode() == f2.GetHashCode().  Ideally, the probability that two 
        /// randomly-generated unequal Formulae have the same hash code should be extremely small.
        /// </summary>
        public override int GetHashCode()
        {
            //uses parts of the stored data to generate a number with a large chance of not returning the same value 
            //if not equal.
            int hashCode = 0;
            hashCode = ToString().Length * 71;
            foreach (string x in formulaArray.ToArray())
                hashCode += x.Length * 7;
            return hashCode;
        }

        /// <summary>
        /// Given an expression, enumerates the tokens that compose it.  Tokens are left paren;
        /// right paren; one of the four operator symbols; a string consisting of a letter or underscore
        /// followed by zero or more letters, digits, or underscores; a double literal; and anything that doesn't
        /// match one of those patterns.  There are no empty tokens, and no token contains white space.
        /// </summary>
        private static IEnumerable<string> GetTokens(String formula)
        {
            // Patterns for individual tokens
            String lpPattern = @"\(";
            String rpPattern = @"\)";
            String opPattern = @"[\+\-*/]";
            String varPattern = @"[a-zA-Z_](?: [a-zA-Z_]|\d)*";
            String doublePattern = @"(?: \d+\.\d* | \d*\.\d+ | \d+ ) (?: [eE][\+-]?\d+)?";
            String spacePattern = @"\s+";

            // Overall pattern
            String pattern = String.Format("({0}) | ({1}) | ({2}) | ({3}) | ({4}) | ({5})",
                                            lpPattern, rpPattern, opPattern, varPattern, doublePattern, spacePattern);

            // Enumerate matching tokens that don't consist solely of white space.
            foreach (String s in Regex.Split(formula, pattern, RegexOptions.IgnorePatternWhitespace))
            {
                if (!Regex.IsMatch(s, @"^\s*$", RegexOptions.Singleline))
                {
                    yield return s;
                }
            }

        }
    }

    /// <summary>
    /// Used to report syntactic errors in the argument to the Formula constructor.
    /// </summary>
    public class FormulaFormatException : Exception
    {
        /// <summary>
        /// Constructs a FormulaFormatException containing the explanatory message.
        /// </summary>
        public FormulaFormatException(String message)
            : base(message)
        {
        }
    }

    /// <summary>
    /// Used as a possible return value of the Formula.Evaluate method.
    /// </summary>
    public struct FormulaError
    {
        /// <summary>
        /// Constructs a FormulaError containing the explanatory reason.
        /// </summary>
        /// <param name="reason"></param>
        public FormulaError(String reason)
            : this()
        {
            Reason = reason;
        }

        /// <summary>
        ///  The reason why this FormulaError was created.
        /// </summary>
        public string Reason { get; private set; }
    }

    /// <summary>
    /// This class contains extensions for use in the Formula class.
    /// </summary>
    public static class extensions
    {
        /// <summary>
        /// Checks if the last token within the Formula Constructor is a valid token.
        /// </summary>
        /// <param name="array"> Token array to be checked </param>
        /// <returns> True if the last token is valid and vice versa </returns>
        public static bool lastTokenIsValid(this ArrayList array)
        {
            ArrayList undesirableElements = new ArrayList(){"(", "*", "/", "+", "-"};
            if(undesirableElements.Contains(array[array.Count - 1]))
                return false;
            return true;
        }

        /// <summary>
        /// Checks the top value of the stack we are operating on if it is equivallent to a given string.
        /// </summary>
        /// <param name="stack">Stack we are operating on</param>
        /// <param name="x">The given string we are checking for</param>
        /// <returns> Boolean </returns>
        public static Boolean hasOnTop(this Stack<string> stack, string x)
        {
            if (stack.Count > 0 && stack.Peek().Equals(x))
                return true;
            return false;
        }
        /// <summary>
        /// Adds top values within a stack in order
        /// </summary>
        /// <param name="stack">Stack we are operating on</param>
        public static void addTopValues(this Stack<double> stack)
        {
            if (stack.Count > 1)
            {
                //pops values to be evaluated.
                double secondValue = stack.Pop();
                double firstValue = stack.Pop();
                stack.Push(firstValue + secondValue);
            }
            else
            {
                throw new ArgumentException("Not enough values in stack");
            }
        }
        /// <summary>
        /// Subtracts top values within a stack in order
        /// </summary>
        /// <param name="stack">Stack we are operating on</param>
        public static void subtractTopValues(this Stack<double> stack)
        {
            if (stack.Count > 1)
            {
                //pops values to be evaluated.
                double secondValue = stack.Pop();
                double firstValue = stack.Pop();
                stack.Push(firstValue - secondValue);
            }
            else
            {
                throw new ArgumentException("Not enough values in stack");
            }
        }
        /// <summary>
        /// Divides the top values within a stack in order
        /// </summary>
        /// <param name="stack">Stack we are operating on</param>
        public static void divideTopValues(this Stack<double> stack)
        {
            if (stack.Count > 1)
            {
                //pops values to be evaluated.
                double secondValue = stack.Pop();
                double firstValue = stack.Pop();

                if (secondValue.Equals(0))
                    throw new ArgumentException("Division by Zero");

                stack.Push(firstValue / secondValue);
            }
            else
            {
                throw new ArgumentException("Not enough values in stack");
            }
        }
        /// <summary>
        /// Multiplies the top values of a stack in order
        /// </summary>
        /// <param name="stack">Stack we are operating on</param>
        public static void multiplyTopValues(this Stack<double> stack)
        {
            if (stack.Count > 1)
            {
                //pops values to be evaluated.
                double secondValue = stack.Pop();
                double firstValue = stack.Pop();
                stack.Push(firstValue * secondValue);
            }
            else
            {
                throw new ArgumentException("Not enough values in stack");
            }
        }
    }

}

