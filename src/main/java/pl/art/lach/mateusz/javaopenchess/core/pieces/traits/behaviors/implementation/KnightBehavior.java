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
public class KnightBehavior extends Behavior
{
    
    public KnightBehavior(Piece piece)
    {
        super(piece);
    }
    
    /**
     * Annotation to superclass Piece changing pawns location
        // knight all moves<br/>
        //  _______________ Y:<br/>
        // |_|_|_|_|_|_|_|_|7<br/>
        // |_|_|_|_|_|_|_|_|6<br/>
        // |_|_|2|_|3|_|_|_|5<br/>
        // |_|1|_|_|_|4|_|_|4<br/>
        // |_|_|_|K|_|_|_|_|3<br/>
        // |_|8|_|_|_|5|_|_|2<br/>
        // |_|_|7|_|6|_|_|_|1<br/>
        // |_|_|_|_|_|_|_|_|0<br/>
        //X:0 1 2 3 4 5 6 7
        //
     * @return  ArrayList with new possition of pawn
     */
    @Override
    public Set<Square> getSquaresInRange()
    {
        Set<Square> list   = new HashSet<>();
        Square[][] squares = piece.getChessboard().getSquares();
        
        int pozX = piece.getSquare().getPozX();
        int pozY = piece.getSquare().getPozY();
        
        int[][] squaresInRange = {
            {pozX - 2, pozY + 1}, //1
            {pozX - 1, pozY + 2}, //2
            {pozX + 1, pozY + 2}, //3
            {pozX + 2, pozY + 1}, //4
            {pozX + 2, pozY - 1}, //5
            {pozX + 1, pozY - 2}, //6
            {pozX - 1, pozY - 2}, //7
            {pozX - 2, pozY - 1}, //8
        };
        
        for(int[] squareCoordinates : squaresInRange)
        {
            int x = squareCoordinates[0];
            int y = squareCoordinates[1];
            if (!piece.isOut(x, y))
            {
                list.add(squares[x][y]);
            }
        }
        return list;
    }
    
}
