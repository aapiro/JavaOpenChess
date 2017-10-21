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
import pl.art.lach.mateusz.javaopenchess.core.pieces.traits.behaviors.implementation.RookBehavior;


/**
 * 
 * @author: Mateusz  Lach ( matlak, msl )
 * @author: Damian Marciniak
 * Class to represent a chess pawn rook
 * Rook can move:
 *       |_|_|_|X|_|_|_|_|7
         |_|_|_|X|_|_|_|_|6
         |_|_|_|X|_|_|_|_|5
         |_|_|_|X|_|_|_|_|4
         |X|X|X|B|X|X|X|X|3
         |_|_|_|X|_|_|_|_|2
         |_|_|_|X|_|_|_|_|1
         |_|_|_|X|_|_|_|_|0
          0 1 2 3 4 5 6 7
 *
 */
public class Rook extends Piece
{

    protected boolean wasMotioned = false;
    
    public Rook(Chessboard chessboard, Player player)
    {
        super(chessboard, player);//call initializer of super type: Piece
        this.value = 5;
        this.symbol = "R";
        this.addBehavior(new RookBehavior(this));
    }

    /**
     * @return the wasMotioned
     */
    public boolean getWasMotioned()
    {
        return wasMotioned;
    }

    /**
     * @param wasMotioned the wasMotioned to set
     */
    public void setWasMotioned(boolean wasMotioned)
    {
        this.wasMotioned = wasMotioned;
    }
}
