using System;
using System.Collections.Generic;
using System.IO;
using System.Text;

namespace ChessTools
{
    public class PGNReader
    {
        private string filePath;
        public PGNReader(string file)
        {
            filePath = file;
        }

        public List<ChessGame> getChessGames()
        {
            List<ChessGame> listToReturn = new List<ChessGame>();

            string[] lines = System.IO.File.ReadAllLines(filePath);

            ChessGame currentGame = new ChessGame();

            for(int i = 0; i < lines.Length; i++)
            {
                if (lines[i].StartsWith("1."))
                {
                    StringBuilder moves = new StringBuilder();
                    for(int j = i; j < lines.Length; j++)
                    {
                        moves.Append(lines[j]);
                        i++;
                        if (lines[j].EndsWith("1/2-1/2") || lines[j].EndsWith("1-0") || lines[j].EndsWith("0-1"))
                        {
                            moves.Append(lines[j]);
                            break;
                        }
                    }
                    currentGame.Moves = moves.ToString();
                    listToReturn.Add(currentGame);
                    currentGame = new ChessGame();
                }
                else if (lines[i].StartsWith("[Site "))
                {
                    string data = lines[i].Substring(5, lines[i].Length - 5);
                    char[] trimChars = new char[3] { ' ', '"',']' };
                    data = data.Trim(trimChars);
                    currentGame.Site = data;
                }
                else if (lines[i].StartsWith("[Round "))
                {
                    string data = lines[i].Substring(6, lines[i].Length - 6);
                    char[] trimChars = new char[3] { ' ', '"', ']' };
                    data = data.Trim(trimChars);
                    currentGame.Round = data;
                }
                else if (lines[i].StartsWith("[WhiteElo "))
                {
                    string data = lines[i].Substring(9, lines[i].Length - 9);
                    char[] trimChars = new char[3] { ' ', '"', ']' };
                    data = data.Trim(trimChars);
                    currentGame.WhiteElo = Int32.Parse(data);
                }
                else if (lines[i].StartsWith("[BlackElo "))
                {
                    string data = lines[i].Substring(9, lines[i].Length - 9);
                    char[] trimChars = new char[3] { ' ', '"', ']' };
                    data = data.Trim(trimChars);
                    currentGame.BlackElo = Int32.Parse(data);
                }
                else if (lines[i].StartsWith("[White "))
                {
                    string data = lines[i].Substring(6, lines[i].Length - 6);
                    char[] trimChars = new char[3] { ' ', '"', ']' };
                    data = data.Trim(trimChars);
                    currentGame.White = data;
                }
                else if (lines[i].StartsWith("[Black "))
                {
                    string data = lines[i].Substring(6, lines[i].Length - 6);
                    char[] trimChars = new char[3] { ' ', '"', ']' };
                    data = data.Trim(trimChars);
                    currentGame.Black = data;
                }
                else if (lines[i].StartsWith("[Result "))
                {
                    string data = lines[i].Substring(7, lines[i].Length - 7);
                    char[] trimChars = new char[3] { ' ', '"', ']' };
                    data = data.Trim(trimChars);
                    if(data == "1-0")
                        currentGame.Result = 'W';
                    else if(data == "0-1")
                        currentGame.Result = 'B';
                    else
                        currentGame.Result = 'D';
                }
                else if (lines[i].StartsWith("[EventDate "))
                {
                    string data = lines[i].Substring(10, lines[i].Length - 10);
                    char[] trimChars = new char[3] { ' ', '"', ']' };
                    data = data.Trim(trimChars);
                    currentGame.EventDate = data;
                }
                else if (lines[i].StartsWith("[Event "))
                {
                    string data = lines[i].Substring(6, lines[i].Length - 6);
                    char[] trimChars = new char[3] { ' ', '"', ']' };
                    data = data.Trim(trimChars);
                    currentGame.Event = data;
                }
            }

            return listToReturn;
        }
    }
}
