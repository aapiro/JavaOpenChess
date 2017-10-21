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
package pl.art.lach.mateusz.javaopenchess.core.pieces.traits.behaviors.implementation;

import java.util.HashSet;
import java.util.Set;
import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.Colors;
import pl.art.lach.mateusz.javaopenchess.core.Square;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.King;
import pl.art.lach.mateusz.javaopenchess.core.pieces.traits.behaviors.Behavior;

/**
 *
 * @author Mateusz  Lach ( matlak, msl )
 */
public class PawnBehavior extends Behavior
{
    
    public PawnBehavior(Piece piece)
    {
        super(piece);
    }

    @Override
    public Set<Square> getSquaresInRange()
    {
        Set<Square> list = new HashSet<>();
        Square sq;
        Square sq1;
        int first = piece.getSquare().getPozY() - 1;//number where to move
        int second = piece.getSquare().getPozY() - 2;//number where to move (only in first move)
        Chessboard chessboard = piece.getChessboard();     
        
        if (piece.getPlayer().isGoDown()) //check if player "go" down or up
        {
            first = piece.getSquare().getPozY() + 1;//if yes, change value
            second = piece.getSquare().getPozY() + 2;//if yes, change value
        }
        if (piece.isOut(first, first)) //out of bounds protection
        {
            return list;//return empty list
        }
        sq = chessboard.getSquare(piece.getSquare().getPozX(), first);
        if (sq.getPiece() == null) //if next is free
        {
            King kingWhite = chessboard.getKingWhite();
            King kingBlack = chessboard.getKingBlack();

            list.add(chessboard.getSquares()[piece.getSquare().getPozX()][first]);

            if ((piece.getPlayer().isGoDown() && piece.getSquare().getPozY() == 1) || (!piece.getPlayer().isGoDown() && piece.getSquare().getPozY() == 6))
            {
                sq1 = chessboard.getSquare(piece.getSquare().getPozX(), second);
                if (sq1.getPiece() == null)
                {
                    list.add(chessboard.getSquare(piece.getSquare().getPozX(), second));
                }
            }
        }
        if (!piece.isOut(piece.getSquare().getPozX() - 1, piece.getSquare().getPozY())) //out of bounds protection
        {
            //capture
            sq = chessboard.getSquares()[piece.getSquare().getPozX() - 1][first];
            if (sq.getPiece() != null) //check if can hit left
            {
                if (piece.getPlayer() != sq.getPiece().getPlayer())
                {
                    list.add(chessboard.getSquares()[piece.getSquare().getPozX() - 1][first]);
                }
            }

            //En passant
            sq = chessboard.getSquares()[piece.getSquare().getPozX() - 1][piece.getSquare().getPozY()];
            if (Chessboard.wasEnPassant(sq)) //check if can hit left
            {
                if (piece.getPlayer() != sq.getPiece().getPlayer()) // unnecessary
                {
                    list.add(chessboard.getSquares()[piece.getSquare().getPozX() - 1][first]);
                }
            }
        }
        if (!piece.isOut(piece.getSquare().getPozX() + 1, piece.getSquare().getPozY())) //out of bounds protection
        {
            //capture
            sq = chessboard.getSquares()[piece.getSquare().getPozX() + 1][first];
            if (sq.getPiece() != null) //check if can hit right
            {
                if (piece.getPlayer() != sq.getPiece().getPlayer())
                {
                    list.add(chessboard.getSquares()[piece.getSquare().getPozX() + 1][first]);
                }
            }

            //En passant
            sq = chessboard.getSquares()[piece.getSquare().getPozX() + 1][piece.getSquare().getPozY()];
            if (Chessboard.wasEnPassant(sq)) //check if can hit left
            {
                if (piece.getPlayer() != sq.getPiece().getPlayer()) // unnecessary
                {

                    //list.add(sq);
                    if (piece.getPlayer().getColor() == Colors.WHITE) //white
                    {
                        list.add(chessboard.getSquares()[piece.getSquare().getPozX() + 1][first]);
                    }
                    else //or black
                    {
                        list.add(chessboard.getSquares()[piece.getSquare().getPozX() + 1][first]);
                    }
                }
            }
        }
        return list;
    }
    

}
