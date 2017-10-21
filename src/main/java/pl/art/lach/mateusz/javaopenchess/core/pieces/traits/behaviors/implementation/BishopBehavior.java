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

/**
 *
 * @author Mateusz  Lach ( matlak, msl )
 */
public class BishopBehavior extends LongRangePieceBehavior
{
    
    public BishopBehavior(Piece piece)
    {
        super(piece);
    }
    
    /**
     *  Annotation to superclass Piece changing pawns location
     * @return  ArrayList with new possition of piece
     */
    @Override
    public Set<Square> getSquaresInRange()
    {
        Set<Square> list = new HashSet<>();
        
        list.addAll(getMovesForDirection(DIRECTION_LEFT,   DIRECTION_UP)); //left-up
        list.addAll(getMovesForDirection(DIRECTION_LEFT,   DIRECTION_BOTTOM)); //left-down
        list.addAll(getMovesForDirection(DIRECTION_RIGHT,  DIRECTION_UP)); //right-up
        list.addAll(getMovesForDirection(DIRECTION_RIGHT,  DIRECTION_BOTTOM)); //right-down
        
        return list;
    }
    
}
