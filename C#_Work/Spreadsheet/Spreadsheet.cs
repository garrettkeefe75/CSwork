/// <summary> 
/// Author:    Garrett Keefe 
/// Partner:   None 
/// Date:      02/14/2020 
/// Course:    CS 3500, University of Utah, School of Computing 
/// Copyright: CS 3500 and Garrett Keefe - This work may not be copied for use in Academic Coursework. 
/// 
/// I, Garrett Keefe, certify that I wrote this code from scratch and did not copy it in part or whole from  
/// another source.  All references used in the completion of the assignment are cited in my README file. 
/// 
/// File Contents 
/// 
///   Spreadsheet Class which acts as the implementer of AbstractSpreadsheets methods. Also Contains the Cell class for containing data within the spreadsheet.
///    
/// </summary>

using SpreadsheetUtilities;
using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.IO;

namespace SS
{
    /// <summary>
    /// Class which acts as the data container for our spreadsheet.
    /// </summary>
    class Cell
    {
        private object cellContents;
        private Formula cellFormula;
        public string cellName;
        /// <summary>
        /// Object constructor. Sets the name of the object and its original contents.
        /// </summary>
        /// <param name="name">name of the Cell</param>
        /// <param name="cellObject">Object/Data contained within Cell</param>
        public Cell(string name, object cellObject)
        {
            cellName = name;
            cellContents = cellObject;
            cellFormula = null;
        }

        /// <summary>
        /// Formula based constructor. Sets the name of the object and its original contents.
        /// </summary>
        /// <param name="name">name of the Cell</param>
        /// <param name="cellObject">Formula contained within Cell</param>
        public Cell(string name, Formula cellObject)
        {
            cellName = name;
            cellContents = cellObject;
            cellFormula = cellObject;
        }

        /// <summary>
        /// Data Setter. This method changes the contents of the cell to whatever object you pass it.
        /// </summary>
        /// <param name="cellObject">New Cell Data</param>
        public void setCellContents(object cellObject)
        {
            cellContents = cellObject;
            cellFormula = null;
        }

        /// <summary>
        /// Data Setter. This method changes the contents of the cell to whatever object you pass it.
        /// </summary>
        /// <param name="cellObject">New Cell Data</param>
        public void setCellContents(Formula cellObject)
        {
            cellContents = cellObject;
            cellFormula = cellObject;
        }

        /// <summary>
        /// Returns the current cell contents/ Data.
        /// </summary>
        /// <returns>See Above</returns>
        public object getCellContents()
        {
            return cellContents;
        }

        /// <summary>
        /// returns the contents of the cell if its not a Formula, otherwise runs evaluate on the Formula
        /// </summary>
        /// <param name="lookup">Delegate which handles looking for the variable value</param>
        /// <returns>The cells value</returns>
        public object getCellValue(Func<string, double> lookup)
        {
            if (cellFormula is null)
                return cellContents;
            else
                return cellFormula.Evaluate(lookup);
        }
    }
    public class Spreadsheet : AbstractSpreadsheet
    {
        /// <summary>
        /// The dicitionary for use in containing non-empty cells and the dependency graph to contain all dependencies between cells.
        /// </summary>
        private Dictionary<string, Cell> spreadSheetCells;
        private DependencyGraph dependencies;

        public override bool Changed { get; protected set; }

        /// <summary>
        /// Constructor Method, sets all data values to new.
        /// </summary>
        public Spreadsheet(Func<string, bool> isValid, Func<string, string> normalize, string version): base(isValid, normalize, version)
        {
            spreadSheetCells = new Dictionary<string, Cell>();
            dependencies = new DependencyGraph();
            Changed = false;
        }

        /// <summary>
        /// Constructor Method, sets all data values to new.
        /// </summary>
        public Spreadsheet() : base(s => true, s => s, "default")
        {
            spreadSheetCells = new Dictionary<string, Cell>();
            dependencies = new DependencyGraph();
            Changed = false;
        }

        /// <summary>
        /// Constructor Method, sets all data values to new. Also reads and creates a spreadsheet based off of the 
        /// data passed in the file within pathToFile.
        /// </summary>
        public Spreadsheet(string pathToFile, Func<string, bool> isValid, Func<string, string> normalize, string version) : base(isValid, normalize, version)
        {
            if (!GetSavedVersion(pathToFile).Equals(version))
                throw new SpreadsheetReadWriteException("Version did not match given file");
            
            spreadSheetCells = new Dictionary<string, Cell>();
            dependencies = new DependencyGraph();
            string name = null;
            string content = null;
            using (XmlReader reader = XmlReader.Create(pathToFile))
            {
                while (reader.Read())
                {
                    if (reader.Name.Equals("cell"))
                        constructorHelper(name, content);
                    if (reader.Name.Equals("name"))
                        name = reader.ReadElementContentAsString();
                    if (reader.Name.Equals("contents"))
                        content = reader.ReadElementContentAsString();
                }
                constructorHelper(name, content);
            }

            Changed = false;
        }


        private void constructorHelper(string name, string content)
        {
            if (!(name is null) && !(content is null))
            {
                try
                {
                    if (name is null || !isNameValid(name) || !IsValid(name))
                        throw new SpreadsheetReadWriteException("Invalid Variable.");
                    SetContentsOfCell(name, content);
                }
                catch (FormulaFormatException)
                {
                    throw new SpreadsheetReadWriteException("Invalid Formula.");
                }
                catch (CircularException)
                {
                    throw new SpreadsheetReadWriteException("File has a Circular Dependency.");
                }
            }
        }

        /// <summary>
        /// If name is null or invalid, throws an InvalidNameException.
        /// 
        /// Otherwise, returns the contents (as opposed to the value) of the named cell.  The return
        /// value should be either a string, a double, or a Formula.
        public override object GetCellContents(string name)
        {
            name = Normalize(name);

            if (name is null || !isNameValid(name) || !IsValid(name))
                throw new InvalidNameException();

            if (spreadSheetCells.TryGetValue(name, out Cell cell))
                return cell.getCellContents();
            else
            {
                return "";
            }
        }

        /// <summary>
        /// Enumerates the names of all the non-empty cells in the spreadsheet.
        /// </summary>
        public override IEnumerable<string> GetNamesOfAllNonemptyCells()
        {
            return this.spreadSheetCells.Keys;
        }

        /// <summary>
        /// If name is null or invalid, throws an InvalidNameException.
        /// 
        /// Otherwise, the contents of the named cell becomes number.  The method returns a
        /// list consisting of name plus the names of all other cells whose value depends, 
        /// directly or indirectly, on the named cell.
        /// 
        /// For example, if name is A1, B1 contains A1*2, and C1 contains B1+A1, the
        /// list {A1, B1, C1} is returned.
        /// </summary>
        protected override IList<string> SetCellContents(string name, double number)
        {
            List<string> newDependencies = new List<string>();
            dependencies.ReplaceDependees(name, newDependencies);

            Changed = true;

            if (spreadSheetCells.TryGetValue(name, out Cell cell))
            {
                cell.setCellContents(number);
                return getDependents(name);
            }
            else
            {
                spreadSheetCells.Add(name, new Cell(name, number));
                return getDependents(name);
            }
        }

        /// <summary>
        /// If text is null, throws an ArgumentNullException.
        /// 
        /// Otherwise, if name is null or invalid, throws an InvalidNameException.
        /// 
        /// Otherwise, the contents of the named cell becomes text.  The method returns a
        /// list consisting of name plus the names of all other cells whose value depends, 
        /// directly or indirectly, on the named cell.
        /// 
        /// For example, if name is A1, B1 contains A1*2, and C1 contains B1+A1, the
        /// list {A1, B1, C1} is returned.
        /// </summary>
        protected override IList<string> SetCellContents(string name, string text)
        {
            if (text is null)
                throw new ArgumentNullException();

            if (text.Equals(""))
                return getDependents(name);

            List<string> newDependencies = new List<string>();
            dependencies.ReplaceDependees(name, newDependencies);

            Changed = true;

            if (spreadSheetCells.TryGetValue(name, out Cell cell))
            {
                cell.setCellContents(text);
                return getDependents(name);
            }
            else
            {
                spreadSheetCells.Add(name, new Cell(name, text));
                return getDependents(name);
            }
        }

        /// <summary>
        /// If the formula parameter is null, throws an ArgumentNullException.
        /// 
        /// Otherwise, if name is null or invalid, throws an InvalidNameException.
        /// 
        /// Otherwise, if changing the contents of the named cell to be the formula would cause a 
        /// circular dependency, throws a CircularException, and no change is made to the spreadsheet.
        /// 
        /// Otherwise, the contents of the named cell becomes formula.  The method returns a
        /// list consisting of name plus the names of all other cells whose value depends,
        /// directly or indirectly, on the named cell.
        /// 
        /// For example, if name is A1, B1 contains A1*2, and C1 contains B1+A1, the
        /// list {A1, B1, C1} is returned.
        /// </summary>
        protected override IList<string> SetCellContents(string name, Formula formula)
        {
            if (formula is null)
                throw new ArgumentNullException();

            IEnumerable<string> oldDependencies = dependencies.GetDependees(name);
            List<string> newDependencies = new List<string>();
            foreach (string x in formula.GetVariables())
            {
                newDependencies.Add(x);
            }
            dependencies.ReplaceDependees(name, newDependencies);

            List<string> listToReturn = new List<string>();
            try
            {
                listToReturn = getDependents(name);
            }
            catch
            {
                dependencies.ReplaceDependees(name, oldDependencies);
                throw new CircularException();
            }

            Changed = true;

            if (spreadSheetCells.TryGetValue(name, out Cell cell))
            {
                cell.setCellContents(formula);
                return listToReturn;
            }
            else
            {
                spreadSheetCells.Add(name, new Cell(name, formula));
                return listToReturn;
            }
        }

        /// <summary>
        /// Helper method which makes use of the already implmented GetCellsToRecalculate to find all the dependencies.
        /// </summary>
        /// <param name="name">The cell whose dependencies we are to find</param>
        /// <returns>The cells dependents</returns>
        private List<string> getDependents(string name)
        {
            List<string> listToReturn = new List<string>() { };
            if (dependencies.HasDependents(name))
            {
                foreach (string x in this.GetCellsToRecalculate(name))
                    listToReturn.Add(x);
            }
            else
            {
                listToReturn.Add(name);
            }
            return listToReturn;
        }

        /// <summary>
        /// Returns an enumeration, without duplicates, of the names of all cells whose
        /// values depend directly on the value of the named cell.  In other words, returns
        /// an enumeration, without duplicates, of the names of all cells that contain
        /// formulas containing name.
        /// 
        /// For example, suppose that
        /// A1 contains 3
        /// B1 contains the formula A1 * A1
        /// C1 contains the formula B1 + A1
        /// D1 contains the formula B1 - C1
        /// The direct dependents of A1 are B1 and C1
        /// </summary>
        protected override IEnumerable<string> GetDirectDependents(string name)
        {
            name = Normalize(name);

            if (name is null || !isNameValid(name) || !IsValid(name))
                throw new InvalidNameException();

            return dependencies.GetDependents(name);
        }

        /// <summary>
        /// Helper method which checks if the passed string is a valid name format
        /// </summary>
        /// <param name="name">name to be checked</param>
        /// <returns>if it is a valid string</returns>
        private bool isNameValid(string name)
        {
            if (char.IsLetter(name[0]))
            {
                foreach (char x in name)
                {
                    if (!double.TryParse(x.ToString(), out double _) && !(char.IsLetter(x)))
                        return false;
                }
                return true;
            }
            return false;
        }

        /// <summary>
        ///   <para>Sets the contents of the named cell to the appropriate value. </para>
        ///   <para>
        ///       First, if the content parses as a double, the contents of the named
        ///       cell becomes that double.
        ///   </para>
        ///
        ///   <para>
        ///       Otherwise, if content begins with the character '=', an attempt is made
        ///       to parse the remainder of content into a Formula.  
        ///       There are then three possible outcomes:
        ///   </para>
        ///
        ///   <list type="number">
        ///       <item>
        ///           If the remainder of content cannot be parsed into a Formula, a 
        ///           SpreadsheetUtilities.FormulaFormatException is thrown.
        ///       </item>
        /// 
        ///       <item>
        ///           If changing the contents of the named cell to be f
        ///           would cause a circular dependency, a CircularException is thrown,
        ///           and no change is made to the spreadsheet.
        ///       </item>
        ///
        ///       <item>
        ///           Otherwise, the contents of the named cell becomes f.
        ///       </item>
        ///   </list>
        ///
        ///   <para>
        ///       Finally, if the content is a string that is not a double and does not
        ///       begin with an "=" (equal sign), save the content as a string.
        ///   </para>
        /// </summary>
        ///
        /// <exception cref="ArgumentNullException"> 
        ///   If the content parameter is null, throw an ArgumentNullException.
        /// </exception>
        /// 
        /// <exception cref="InvalidNameException"> 
        ///   If the name parameter is null or invalid, throw an InvalidNameException
        /// </exception>
        ///
        /// <exception cref="SpreadsheetUtilities.FormulaFormatException"> 
        ///   If the content is "=XYZ" where XYZ is an invalid formula, throw a FormulaFormatException.
        /// </exception>
        /// 
        /// <exception cref="CircularException"> 
        ///   If changing the contents of the named cell to be the formula would 
        ///   cause a circular dependency, throw a CircularException.  
        ///   (NOTE: No change is made to the spreadsheet.)
        /// </exception>
        /// 
        /// <param name="name"> The cell name that is being changed</param>
        /// <param name="content"> The new content of the cell</param>
        /// 
        /// <returns>
        ///       <para>
        ///           This method returns a list consisting of the passed in cell name,
        ///           followed by the names of all other cells whose value depends, directly
        ///           or indirectly, on the named cell. The order of the list MUST BE any
        ///           order such that if cells are re-evaluated in that order, their dependencies 
        ///           are satisfied by the time they are evaluated.
        ///       </para>
        ///
        ///       <para>
        ///           For example, if name is A1, B1 contains A1*2, and C1 contains B1+A1, the
        ///           list {A1, B1, C1} is returned.  If the cells are then evaluate din the order:
        ///           A1, then B1, then C1, the integrity of the Spreadsheet is maintained.
        ///       </para>
        /// </returns>
        public override IList<string> SetContentsOfCell(string name, string content)
        {
            name = Normalize(name);

            if (name is null || !isNameValid(name) || !IsValid(name))
                throw new InvalidNameException();

            if (content is null)
                throw new ArgumentNullException();

            if (double.TryParse(content, out double result))
                return SetCellContents(name, result);

            else if (content.IndexOf("=").Equals(0))
                return SetCellContents(name, new Formula(content.TrimStart('='), Normalize, IsValid));

            else
                return SetCellContents(name, content);
        }

        /// <summary>
        /// Returns the version information of the spreadsheet saved in the named file.
        /// If there are any problems opening, reading, or closing the file, the method
        /// should throw a SpreadsheetReadWriteException with an explanatory message.
        /// </summary>
        public override string GetSavedVersion(string filename)
        {
            if (!File.Exists(filename))
                throw new SpreadsheetReadWriteException("File path does not exist.");
            try
            {
                using (XmlReader reader = XmlReader.Create(filename))
                {
                    while (reader.Read())
                    {
                        if (reader.IsStartElement())
                        {
                            switch (reader.Name)
                            {
                                case "spreadsheet":
                                    return reader.GetAttribute("version");
                            }
                        }
                    }
                }
            }
            catch
            {
                throw new SpreadsheetReadWriteException("Error while reading file.");
            }
            throw new SpreadsheetReadWriteException("Did not find a version parameter.");
        }

        /// <summary>
        /// Writes the contents of this spreadsheet to the named file using an XML format.
        /// The XML elements should be structured as follows:
        /// 
        /// <spreadsheet version="version information goes here">
        /// 
        /// <cell>
        /// <name>cell name goes here</name>
        /// <contents>cell contents goes here</contents>    
        /// </cell>
        /// 
        /// </spreadsheet>
        /// 
        /// There should be one cell element for each non-empty cell in the spreadsheet.  
        /// If the cell contains a string, it should be written as the contents.  
        /// If the cell contains a double d, d.ToString() should be written as the contents.  
        /// If the cell contains a Formula f, f.ToString() with "=" prepended should be written as the contents.
        /// 
        /// If there are any problems opening, writing, or closing the file, the method should throw a
        /// SpreadsheetReadWriteException with an explanatory message.
        /// </summary>
        public override void Save(string filename)
        {
            XmlWriterSettings settings = new XmlWriterSettings();

            settings.Indent = true;

            settings.IndentChars = "  ";

            try
            {
                using (XmlWriter writer = XmlWriter.Create(filename, settings))
                {
                    writer.WriteStartElement("spreadsheet");
                    writer.WriteAttributeString("version", Version);
                    foreach (string name in GetNamesOfAllNonemptyCells())
                    {
                        writer.WriteStartElement("cell");
                        writer.WriteElementString("name", name);

                        if (GetCellContents(name) is Formula)
                            writer.WriteElementString("contents", "=" + GetCellContents(name).ToString());

                        else
                            writer.WriteElementString("contents", GetCellContents(name).ToString());

                        writer.WriteEndElement();
                    }
                    writer.WriteEndElement();
                }
            }
            catch
            {
                throw new SpreadsheetReadWriteException("Error while writing contents.");
            }
            
            Changed = false;
        }

        /// <summary>
        /// If name is null or invalid, throws an InvalidNameException.
        /// 
        /// Otherwise, returns the value (as opposed to the contents) of the named cell.  The return
        /// value should be either a string, a double, or a SpreadsheetUtilities.FormulaError.
        /// </summary>
        public override object GetCellValue(string name)
        {
            name = Normalize(name);

            if (name is null || !isNameValid(name))
                throw new InvalidNameException();

            if (spreadSheetCells.TryGetValue(name, out Cell cell))
                return cell.getCellValue(getCellDouble);
            else
            {
                return "";
            }
        }

        /// <summary>
        /// Helper method which acts as the lookup method for evaluating formula
        /// </summary>
        /// <param name="name"></param>
        /// <returns></returns>
        private double getCellDouble(string name)
        {
            if (GetCellContents(name) is double)
                return double.Parse(GetCellContents(name).ToString());
            else if (GetCellContents(name) is Formula)
                return (double)spreadSheetCells[name].getCellValue(getCellDouble);
            else
                throw new ArgumentException();
        }
    }
}
