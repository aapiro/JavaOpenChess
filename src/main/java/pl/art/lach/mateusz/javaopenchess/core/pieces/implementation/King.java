/*
#    This program is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.art.lach.mateusz.javaopenchess.core.pieces.implementation;

/**
 * @author: Mateusz  Lach ( matlak, msl )
 * @author: Damian Marciniak
 * Class to represent a chess pawn king. King is the most important
 * piece for the game. Loose of king is the and of game.
 * When king is in danger by the opponent then it's a Checked, and when have
 * no other escape then stay on a square "in danger" by the opponent
 * then it's a CheckedMate, and the game is over.
 *
 *       |_|_|_|_|_|_|_|_|7
        |_|_|_|_|_|_|_|_|6
        |_|_|_|_|_|_|_|_|5
        |_|_|X|X|X|_|_|_|4
        |_|_|X|K|X|_|_|_|3
        |_|_|X|X|X|_|_|_|2
        |_|_|_|_|_|_|_|_|1
        |_|_|_|_|_|_|_|_|0
        0 1 2 3 4 5 6 7
 */
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;
import pl.art.lach.mateusz.javaopenchess.core.Square;
import pl.art.lach.mateusz.javaopenchess.core.pieces.traits.behaviors.implementation.KingBehavior;
import pl.art.lach.mateusz.javaopenchess.core.moves.Castling;

public class King extends Piece
{

    protected boolean wasMotioned = false;

    public King(Chessboard chessboard, Player player)
    {
        super(chessboard, player);
        this.value = 99;
        this.symbol = "K";
        this.addBehavior(new KingBehavior(this));
    }
    /** Method to check is the king is checked
     *  @return bool true if king is not save, else returns false
     */
    public boolean isChecked()
    {
        return !isSafe(this.square);
    }

    /** Method to check is the king is checked or stalemated
     *  @return int 0 if nothing, 1 if checkmate, else returns 2
     */
    public int isCheckmatedOrStalemated()
    {
        /*
         *returns: 0-nothing, 1-checkmate, 2-stalemate
         */
        if (this.getAllMoves().isEmpty())
        {
            for (int i = 0; i < 8; ++i)
            {
                for (int j = 0; j < 8; ++j)
                {
                    Piece piece = getChessboard().getSquare(i, j).getPiece();
                    if (null != piece && piece.getPlayer() == this.getPlayer() && !piece.getAllMoves().isEmpty())
                    {
                        return 0;
                    }
                }
            }

            if (this.isChecked())
            {
                return 1;
            }
            else
            {
                return 2;
            }
        }
        else
        {
            return 0;
        }
    }
    
    /**
     * Method to check is the king is checked by an opponent
     * @return bool true if king is save, else returns false
     */
    public boolean isSafe()
    {
        return isSafe(getSquare());
    }

    /** Method to check is the king is checked by an opponent
     * @param s Squere where is a king
     * @return bool true if king is save, else returns false
     */
    public boolean isSafe(Square s)
    {
        Square[][] squares = chessboard.getSquares();
        for (int i=0; i<squares.length; i++)
        {
            for (int j=0; j<squares[i].length; j++)
            {
                Square sq = squares[i][j];
                Piece piece = sq.getPiece();
                if(piece != null) 
                {
                    if(piece.getPlayer().getColor() != this.getPlayer().getColor() && piece != this)
                    {
                        if(piece.getSquaresInRange().contains(s))
                        {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /** Method to check will the king be safe when move
     *  @param currentSquare currentSquare object
     *  @param futureSquare futureSquare object
     *  @return bool true if king is save, else returns false
     */
    public boolean willBeSafeAfterMove(Square currentSquare, Square futureSquare)
    {
        Piece tmp = futureSquare.piece;
        futureSquare.piece  = currentSquare.piece; // move without redraw
        currentSquare.piece = null;

        boolean ret = false;
        if (futureSquare.getPiece().getClass() == King.class)
        {
            ret = isSafe(futureSquare);
        }
        else
        {
            ret = isSafe();
        }

        currentSquare.piece = futureSquare.piece;
        futureSquare.piece  = tmp;

        return ret;
    }
    
    /** Method to check will the king be safe when move
     *  @param futureSquare futureSquare object
     *  @return bool true if king is save, else returns false
     */
    public boolean willBeSafeAfterMove(Square futureSquare)
    {
        return willBeSafeAfterMove(this.getSquare(), futureSquare);
    }

    /**
     * @return the wasMotion
     */
    public boolean getWasMotioned()
    {
        return wasMotioned;
    }

    /**
     * @param wasMotioned the wasMotion to set
     */
    public void setWasMotioned(boolean wasMotioned)
    {
        this.wasMotioned = wasMotioned;
    }
    
    public static Castling getCastling(Square begin, Square end)
    {
        Castling result = Castling.NONE;
        if (begin.getPozX() + 2 == end.getPozX())
        {
            result = Castling.SHORT_CASTLING;
        }
        else if (begin.getPozX() - 2 == end.getPozX())
        {
            result = Castling.LONG_CASTLING;
        }
        return result;
    }
}