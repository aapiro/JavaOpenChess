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
package pl.art.lach.mateusz.javaopenchess;

import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;
import pl.art.lach.mateusz.javaopenchess.core.Game;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.DataExporter;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.implementations.FenNotation;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Bishop;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.King;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Pawn;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;
import pl.art.lach.mateusz.javaopenchess.core.players.PlayerType;
import org.junit.Test;
import org.junit.Before;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;
import org.junit.Ignore;
import pl.art.lach.mateusz.javaopenchess.utils.GameModes;
import pl.art.lach.mateusz.javaopenchess.utils.GameTypes;

/**
 *
 * @author Mateusz Lach (matlak, msl)
 */
public class KingSafetyTest
{
    private  Game game;;
    
    private Chessboard chessboard;
    
    @Before
    public void setup() 
    { 
        game = new Game();
        Settings sett = game.getSettings();//sett LOCAL settings variable
        chessboard = game.getChessboard();
        Player pl1 = sett.getPlayerWhite();//set LOCAL player variable
        Player pl2 = sett.getPlayerBlack();//set LOCAL player variable
        sett.setGameMode(GameModes.NEW_GAME);
        pl2.setName("");//set name of player
        pl1.setName("");//set name of player
        pl1.setType(PlayerType.LOCAL_USER);//set type of player
        pl2.setType(PlayerType.LOCAL_USER);//set type of player
        sett.setGameType(GameTypes.LOCAL);
        game.setActivePlayer(pl1);
        
        King kingWhite = new King(chessboard, pl1);
        King kingBlack = new King(chessboard, pl2);
        
        chessboard.getSquare(4, 6).setPiece(new Pawn(chessboard, pl2));
        chessboard.getSquare(2, 4).setPiece(new Bishop(chessboard, pl2)); 
        
        chessboard.setKingBlack(kingBlack, chessboard.getSquare(0, 0));
        chessboard.setKingWhite(kingWhite, chessboard.getSquare(3, 7));
    }
    
    @Test
    public void checkSetting()
    {
        assertThat(chessboard.getSquare(3, 7).getPiece(), instanceOf(King.class));
        assertThat(chessboard.getSquare(0, 0).getPiece(), instanceOf(King.class));
        assertThat(chessboard.getSquare(4, 6).getPiece(), instanceOf(Pawn.class));
        assertThat(chessboard.getSquare(2, 4).getPiece(), instanceOf(Bishop.class));
        
    }
  
    @Test
    public void checkKingSafety()
    {
        King king = chessboard.getKingWhite();
        assertFalse(king.willBeSafeAfterMove(king.getSquare(), chessboard.getSquare(4, 6)));
    }
       
    @Test
    public void checkKingLegalMoves()
    {
        DataExporter fenExporter = new FenNotation();
        String exportedState = fenExporter.exportData(game);
        assertEquals("k7/8/8/8/2b5/8/4p3/3K4 w  - 0 1", exportedState);
    }
}
