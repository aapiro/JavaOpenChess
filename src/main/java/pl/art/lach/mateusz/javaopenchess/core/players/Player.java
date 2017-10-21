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
package pl.art.lach.mateusz.javaopenchess.core.players;

import java.io.Serializable;
import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.Colors;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;

/**
 * @author Mateusz  Lach (matlak, msl)
 */
public interface Player extends Serializable 
{

    public static final String CPU_NAME = "CPU";
    /**
     * @return the color
     */
    Colors getColor();

    /** Method getting the players name
     *  @return name of player
     */
    String getName();

    /**
     * @return the playerType
     */
    PlayerType getPlayerType();

    /**
     * @return the goDown
     */
    boolean isGoDown();

    void setGoDown(boolean goDown);

    /** Method setting the players name
     *  @param name name of player
     */
    void setName(String name);

    /** Method setting the players type
     *  @param type type of player - enumerate
     */
    void setType(PlayerType type);
    
    Piece getPromotionPiece(Chessboard chessboard);
    
}
