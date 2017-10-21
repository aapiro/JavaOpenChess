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
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.core.pieces.traits.behaviors.Behavior;

/**
 *
 * @author Mateusz  Lach ( matlak, msl )
 */
abstract class LongRangePieceBehavior extends Behavior
{
    
    protected static final int DIRECTION_LEFT = -1;
    
    protected static final int DIRECTION_RIGHT = 1;
    
    protected static final int DIRECTION_UP    = -1;
    
    protected static final int DIRECTION_BOTTOM = 1;
    
    protected static final int DIRECTION_NILL   = 0;
    
    public LongRangePieceBehavior(Piece piece)
    {
        super(piece);
    }
    
    /**
     * Helper method to fetch all moves for given direction.
     * Useful for Bishop, Rook and Queen.
     * @param moveX X direction to move in
     * @param moveY Y diretion to move in
     * @return ArrayList with possible moves
     */
    protected Set<Square> getMovesForDirection(int moveX, int moveY)
    {
        Set<Square> list = new HashSet<>();
        
        for (int h = piece.getSquare().getPozX(), i = piece.getSquare().getPozY(); !piece.isOut(h, i); h += moveX, i += moveY) //right-down
        {
            if(piece.getSquare().getPozX() == h && piece.getSquare().getPozY() == i)
            {
                continue; //omit same square
            }
            Square sq = piece.getChessboard().getSquare(h, i);
            if (null == sq.getPiece() || piece.getPlayer() != sq.getPiece().getPlayer()) //if on this sqhuare isn't piece
            {
                list.add(piece.getChessboard().getSquare(h, i));

                if (piece.otherOwner(h, i))
                {
                    break;
                }
            }
            else
            {
                break;//we've to break becouse we cannot go beside other piece!!
            }
        }  
        return list;
    }        
}
