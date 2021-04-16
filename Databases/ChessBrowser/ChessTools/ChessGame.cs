using System;

namespace ChessTools
{
    public class ChessGame
    {
        public string Event;
        public string Site;
        public string Round;
        public string White;
        public string Black;
        public int WhiteElo;
        public int BlackElo;
        public char Result;
        public string EventDate;
        public string Moves;

        public ChessGame()
        {
            Event = "";
            Site = "";
            Round = "";
            White = "";
            Black = "";
            WhiteElo = -1;
            BlackElo = -1;
            Result = 'E';
            EventDate = "";
            Moves = "";
        }
    }
}
