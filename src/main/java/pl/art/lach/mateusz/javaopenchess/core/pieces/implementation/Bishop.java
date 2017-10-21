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

/*
 * Authors:
 * Mateusz  Lach ( matlak, msl )
 * Damian Marciniak
 */
package pl.art.lach.mateusz.javaopenchess.core.pieces.implementation;

import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;
import pl.art.lach.mateusz.javaopenchess.core.pieces.traits.behaviors.implementation.BishopBehavior;

/**
 * Class to represent a chess pawn bishop
 * Bishop can move across the chessboard
 *<br/>
|_|_|_|_|_|_|_|X|7<br/>
|X|_|_|_|_|_|X|_|6<br/>
|_|X|_|_| |X|_|_|5<br/>
|_|_|X|_|X|_|_|_|4<br/>
|_|_|_|B|_|_|_|_|3<br/>
|_| |X|_|X|_|_|_|2<br/>
|_|X|_|_|_|X|_|_|1<br/>
|X|_|_|_|_|_|X|_|0<br/>
0 1 2 3 4 5 6 7<br/>
 */
public class Bishop extends Piece
{

    public Bishop(Chessboard chessboard, Player player)
    {
        super(chessboard, player); //call initializer of super type: Piece
        this.value = 3;
        this.symbol = "B";
        this.addBehavior(new BishopBehavior(this));
    }
}
