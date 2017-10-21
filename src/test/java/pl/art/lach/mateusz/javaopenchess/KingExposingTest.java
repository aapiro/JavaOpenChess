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
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Bishop;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.King;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Pawn;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Rook;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;
import pl.art.lach.mateusz.javaopenchess.core.players.PlayerType;
import org.junit.Test;
import org.junit.Before;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;
import org.junit.Ignore;
import pl.art.lach.mateusz.javaopenchess.core.GameFactory;
import pl.art.lach.mateusz.javaopenchess.utils.GameModes;
import pl.art.lach.mateusz.javaopenchess.utils.GameTypes;

/**
 *
 * @author Mateusz Lach (matlak, msl)
 */
public class KingExposingTest
{
    private  Game game;;
    
    private Chessboard chessboard;
    
    @Before
    public void setup() 
    { 
        game = new Game();
        Settings sett = game.getSettings();//sett LOCAL settings variable
        chessboard = game.getChessboard();
        Player playerWhite = sett.getPlayerWhite();//set LOCAL player variable
        Player playerBlack = sett.getPlayerBlack();//set LOCAL player variable
        sett.setGameMode(GameModes.NEW_GAME);
        playerBlack.setName("");//set name of player
        playerWhite.setName("");//set name of player
        playerWhite.setType(PlayerType.LOCAL_USER);//set type of player
        playerBlack.setType(PlayerType.LOCAL_USER);//set type of player
        sett.setGameType(GameTypes.LOCAL);
        game.setActivePlayer(playerWhite);
        
        King kingWhite = new King(chessboard, playerWhite);
        King kingBlack = new King(chessboard, playerBlack);
        
        chessboard.getSquare(3, 1).setPiece(new Rook(chessboard, playerBlack));
        chessboard.getSquare(3, 6).setPiece(new Rook(chessboard, playerWhite)); 
        
        chessboard.setKingBlack(kingBlack, chessboard.getSquare(0, 0));
        chessboard.setKingWhite(kingWhite, chessboard.getSquare(3, 7));
    }
    
    @Test
    public void checkSetting()
    {
        assertThat(chessboard.getSquare(3, 7).getPiece(), instanceOf(King.class));
        assertThat(chessboard.getSquare(0, 0).getPiece(), instanceOf(King.class));
        assertThat(chessboard.getSquare(3, 6).getPiece(), instanceOf(Rook.class));
        assertThat(chessboard.getSquare(3, 1).getPiece(), instanceOf(Rook.class));
        
    }
  
    @Test
    public void checkKingSafeness()
    {
        Piece piece = chessboard.getSquare(3, 6).getPiece();
        King kingWhite = chessboard.getKingWhite();
        assertFalse(kingWhite.willBeSafeAfterMove(piece.getSquare(), chessboard.getSquare(5, 6)));
        assertFalse(kingWhite.willBeSafeAfterMove(piece.getSquare(), chessboard.getSquare(0, 6)));
        assertTrue(kingWhite.willBeSafeAfterMove(piece.getSquare(), chessboard.getSquare(3, 5)));
    }
       
    @Test
    public void checkKingLegalMoves()
    {
        DataExporter fenExporter = new FenNotation();
        String exportedState = fenExporter.exportData(game);
        assertEquals("k7/3r4/8/8/8/8/3R4/3K4 w  - 0 1", exportedState);
    }
}
