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
package pl.art.lach.mateusz.javaopenchess.core.players.implementation;

import pl.art.lach.mateusz.javaopenchess.JChessApp;
import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.Colors;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.core.pieces.PieceFactory;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Bishop;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Knight;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Queen;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Rook;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;
import pl.art.lach.mateusz.javaopenchess.core.players.PlayerType;


/**
 * Class representing the player in the game
 */
public class ComputerPlayer implements Player
{

    protected String name;

    protected Colors color;

    protected PlayerType playerType;
    
    protected boolean goDown;

    /**
     * Default constructor.
     */
    public ComputerPlayer()
    {
        this.playerType = PlayerType.COMPUTER;
    }

    /**
     * Constructor for Player class
     * @param name
     * @param color 
     */
    public ComputerPlayer(String name, String color)
    {
        this(name, Colors.valueOf(color.toUpperCase()));
    }
    
        /**
     * Constructor for Player class
     * @param name
     * @param color 
     */
    public ComputerPlayer(String name, Colors color)
    {
        this();
        this.name = name;
        this.color = color;
        this.goDown = false;
    }

    /** Method setting the players name
     *  @param name name of player
     */
    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    /** Method getting the players name
     *  @return name of player
     */
    @Override
    public String getName()
    {
        return this.name;
    }

    /** Method setting the players type
     *  @param type type of player - enumerate
     */
    @Override
    public void setType(PlayerType type)
    {
        this.playerType = type;
    }

    /**
     * @return the color
     */
    @Override
    public Colors getColor()
    {
        return color;
    }

    /**
     * @return the playerType
     */
    @Override
    public PlayerType getPlayerType()
    {
        return playerType;
    }

    /**
     * @return the goDown
     */
    @Override
    public boolean isGoDown()
    {
        return goDown;
    }    
    
    @Override
    public void setGoDown(boolean goDown)
    {
        this.goDown = goDown;
    }

    @Override
    public Piece getPromotionPiece(Chessboard chessboard) {
        return null;
    }

}
