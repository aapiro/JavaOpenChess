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
import pl.art.lach.mateusz.javaopenchess.core.Square;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.King;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Rook;
import pl.art.lach.mateusz.javaopenchess.core.pieces.traits.behaviors.Behavior;

/**
 *
 * @author Mateusz  Lach ( matlak, msl )
 */
public class KingBehavior extends Behavior
{
    
    public KingBehavior(King piece)
    {
        super(piece);
    }
    
    /**
     * 
     *   // King all moves<br/>
     *   |_|_|_|_|_|_|_|_|7<br/>
     *   |_|_|_|_|_|_|_|_|6<br/>
     *   |_|_|_|_|_|_|_|_|5<br/>
     *   |_|_|X|X|X|_|_|_|4<br/>
     *   |_|_|X|K|X|_|_|_|3<br/>
     *   |_|_|X|X|X|_|_|_|2<br/>
     *   |_|_|_|_|_|_|_|_|1<br/>
     *   |_|_|_|_|_|_|_|_|0<br/>
     *   0 1 2 3 4 5 6 7<br/>
     *   //<br/>
     * @return  Set with squares in range
     */
    @Override
    public Set<Square> getSquaresInRange()
    {
        Set<Square> list = new HashSet<>();
        Square sq;
        Square sq1;
        King king = (King)piece;
        
        for (int i = king.getSquare().getPozX() - 1; i <= king.getSquare().getPozX() + 1; i++)
        {
            for (int y = king.getSquare().getPozY() - 1; y <= king.getSquare().getPozY() + 1; y++)
            {
                if (!king.isOut(i, y)) //out of bounds protection
                {
                    sq = king.getChessboard().getSquare(i, y);
                    if (king.getSquare() == sq) //if we're checking square on which is King
                    {
                        continue;
                    }
                    if (null == sq.getPiece() || sq.getPiece().getPlayer() != piece.getPlayer()) //if square is empty or other player
                    {
                        list.add(sq);
                    }
                }
            }
        }

        if (!king.getWasMotioned()) //check if king was not moved before
        {
            if (king.getChessboard().getSquares()[0][king.getSquare().getPozY()].getPiece() != null
                    && king.getChessboard().getSquares()[0][king.getSquare().getPozY()].getPiece().getName().equals("Rook"))
            {
                boolean canCastling = true;

                Rook rook = (Rook) king.getChessboard().getSquare(0, king.getSquare().getPozY()).getPiece();
                if (!rook.getWasMotioned())
                {
                    for (int i = king.getSquare().getPozX() - 1; i > 0; i--)
                    {//go left
                        if (king.getChessboard().getSquare(i, king.getSquare().getPozY()).getPiece() != null)
                        {
                            canCastling = false;
                            break;
                        }
                    }
                    sq = king.getChessboard().getSquare(king.getSquare().getPozX() - 2, king.getSquare().getPozY());
                    sq1 = king.getChessboard().getSquare(king.getSquare().getPozX() - 1, king.getSquare().getPozY());
                    
                    if (canCastling) //can do castling when none of Sq,sq1 is checked
                    { 
                        list.add(sq);
                    }
                }
            }
            if (king.getChessboard().getSquares()[7][king.getSquare().getPozY()].getPiece() != null
                    && king.getChessboard().getSquares()[7][king.getSquare().getPozY()].getPiece().getName().equals("Rook"))
            {
                boolean canCastling = true;
                Rook rook = (Rook) king.getChessboard().getSquares()[7][king.getSquare().getPozY()].getPiece();
                if (!rook.getWasMotioned()) //if king was not moves before and is not checked
                {
                    for (int i = king.getSquare().getPozX() + 1; i < 7; i++) //go right
                    {
                        if (king.getChessboard().getSquares()[i][king.getSquare().getPozY()].getPiece() != null) //if square is not empty
                        {
                            canCastling = false;//cannot castling
                            break; // exit
                        }
                    }
                    sq = king.getChessboard().getSquares()[king.getSquare().getPozX() + 2][king.getSquare().getPozY()];
                    sq1 = king.getChessboard().getSquares()[king.getSquare().getPozX() + 1][king.getSquare().getPozY()];
                    if (canCastling) //can do castling when none of Sq,sq1 is checked
                    {
                        list.add(sq);
                    }
                }
            }
        }
        return list;
    }
    
    @Override
    public Set<Square> getLegalMoves()
    {
        Set<Square> list   = super.getLegalMoves();
        Set<Square> result = new HashSet<>();
        for (Square sq : list)
        {
            if (((King)piece).isSafe(sq))
            {
                result.add(sq);
            }
        }
        return result;
    }
    
}
