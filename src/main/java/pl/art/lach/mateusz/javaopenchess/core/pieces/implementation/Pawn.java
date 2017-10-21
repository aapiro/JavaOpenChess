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

import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;
import pl.art.lach.mateusz.javaopenchess.core.Square;
import pl.art.lach.mateusz.javaopenchess.core.pieces.traits.behaviors.implementation.PawnBehavior;

/**
 * @author : Mateusz  Lach ( matlak, msl )
 * @author : Damian Marciniak
 * Class to represent a pawn piece
 * Pawn can move only forvard and can beat only across
 * In first move pawn can move 2 sqares
 * pawn can be upgreade to rook, knight, bishop, Queen if it's in the
 * squers nearest the side where opponent is lockated
 * Firat move of pawn:
 *       |_|_|_|_|_|_|_|_|7
         |_|_|_|_|_|_|_|_|6
         |_|_|_|X|_|_|_|_|5
         |_|_|_|X|_|_|_|_|4
         |_|_|_|P|_|_|_|_|3
         |_|_|_|_|_|_|_|_|2
         |_|_|_|_|_|_|_|_|1
         |_|_|_|_|_|_|_|_|0
         0 1 2 3 4 5 6 7
 *
 * Move of a pawn:
 *       |_|_|_|_|_|_|_|_|7
         |_|_|_|_|_|_|_|_|6
         |_|_|_|_|_|_|_|_|5
         |_|_|_|X|_|_|_|_|4
         |_|_|_|P|_|_|_|_|3
         |_|_|_|_|_|_|_|_|2
         |_|_|_|_|_|_|_|_|1
         |_|_|_|_|_|_|_|_|0
         0 1 2 3 4 5 6 7
 * Beats with can take pawn:
 *       |_|_|_|_|_|_|_|_|7
         |_|_|_|_|_|_|_|_|6
         |_|_|_|_|_|_|_|_|5
         |_|_|X|_|X|_|_|_|4
         |_|_|_|P|_|_|_|_|3
         |_|_|_|_|_|_|_|_|2
         |_|_|_|_|_|_|_|_|1
         |_|_|_|_|_|_|_|_|0
         0 1 2 3 4 5 6 7
 */
public class Pawn extends Piece
{
    protected boolean down;
        
    public Pawn(Chessboard chessboard, Player player)
    {
        super(chessboard, player);
        this.value = 1;
        this.symbol = "";
        this.behaviors.add(new PawnBehavior(this));
    }

    @Deprecated
    void promote(Piece newPiece)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @return the down
     */
    public boolean isDown()
    {
        return down;
    }
    
    public static boolean wasEnPassant(Square from, Square to) 
    { 
        return from.getPozX() != to.getPozX() && from.getPozY() != to.getPozY() && null == to.getPiece();
    }
    
    public static boolean wasTwoFieldsMove(Square from, Square to) 
    {
        return Math.abs(from.getPozY() - to.getPozY()) == 2;
    }
    
    public static boolean canBePromoted(Square end)
    {
        return (end.getPozY() == 0 || end.getPozY() == 7);
    }
}
