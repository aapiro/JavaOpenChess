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
import pl.art.lach.mateusz.javaopenchess.core.Colors;
import pl.art.lach.mateusz.javaopenchess.core.Game;
import pl.art.lach.mateusz.javaopenchess.core.Square;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.DataExporter;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.implementations.FenNotation;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Bishop;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.King;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Knight;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Pawn;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Queen;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Rook;
import pl.art.lach.mateusz.javaopenchess.core.players.PlayerType;
import pl.art.lach.mateusz.javaopenchess.core.GameFactory;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.DataImporter;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.DataTransferFactory;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.TransferFormat;
import pl.art.lach.mateusz.javaopenchess.core.exceptions.ReadGameError;
import pl.art.lach.mateusz.javaopenchess.utils.GameModes;
import pl.art.lach.mateusz.javaopenchess.utils.GameTypes;
import pl.art.lach.mateusz.javaopenchess.core.Squares;

import org.junit.Test;
import org.junit.Before;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;
/**
 *
 * @author Mateusz Lach (matlak, msl)
 */
public class FenNotationTest
{
    
    private static Game game;
    
    private DataExporter dataExporter =  DataTransferFactory.getExporterInstance(TransferFormat.FEN);
    
    private DataImporter dataImporter =  DataTransferFactory.getImporterInstance(TransferFormat.FEN);
    
    private static Chessboard chessboard;
    
    @Before
    public void setup() 
    { 
        game = GameFactory.instance(
            GameModes.NEW_GAME,
            GameTypes.LOCAL,
            "",
            "",
            PlayerType.LOCAL_USER,
            PlayerType.LOCAL_USER
        );
        chessboard = game.getChessboard();
    }
    
    @Test
    public void checkRookSetting()
    {        
        assertThat(chessboard.getSquare(7, 7).getPiece(), instanceOf(Rook.class));
        assertThat(chessboard.getSquare(0, 7).getPiece(), instanceOf(Rook.class));
        assertThat(chessboard.getSquare(0, 0).getPiece(), instanceOf(Rook.class));
        assertThat(chessboard.getSquare(7, 0).getPiece(), instanceOf(Rook.class));
        
        for (int i = Chessboard.FIRST_SQUARE; i <= Chessboard.LAST_SQUARE; i++) 
        {
            assertThat(chessboard.getSquare(i, 1).getPiece(), instanceOf(Pawn.class));
            assertThat(chessboard.getSquare(i, 6).getPiece(), instanceOf(Pawn.class));
        }
        
        assertThat(chessboard.getSquare(6, 7).getPiece(), instanceOf(Knight.class));
        assertThat(chessboard.getSquare(1, 7).getPiece(), instanceOf(Knight.class));
        assertThat(chessboard.getSquare(6, 0).getPiece(), instanceOf(Knight.class));
        assertThat(chessboard.getSquare(1, 0).getPiece(), instanceOf(Knight.class));
        
        assertThat(chessboard.getSquare(5, 7).getPiece(), instanceOf(Bishop.class));
        assertThat(chessboard.getSquare(2, 7).getPiece(), instanceOf(Bishop.class));
        assertThat(chessboard.getSquare(5, 0).getPiece(), instanceOf(Bishop.class));
        assertThat(chessboard.getSquare(2, 0).getPiece(), instanceOf(Bishop.class));
        
        assertThat(chessboard.getSquare(4, 7).getPiece(), instanceOf(King.class));
        assertThat(chessboard.getSquare(3, 7).getPiece(), instanceOf(Queen.class));
        assertThat(chessboard.getSquare(4, 0).getPiece(), instanceOf(King.class));
        assertThat(chessboard.getSquare(3, 0).getPiece(), instanceOf(Queen.class));
    }
    
    @Test
    public void checkColors()
    {
        assertEquals(chessboard.getSquare(0, 0).getPiece().getPlayer().getColor(), Colors.BLACK);
    }
    
    @Test
    public void exportInitialState() 
    {
        String exportedState = dataExporter.exportData(game);
        assertEquals(FenNotation.INITIAL_STATE, exportedState);
    }
    
    @Test
    public void exportFirstMovesState()
    {
        Square from = chessboard.getSquare(4, 6);
        Square to   = chessboard.getSquare(4, 4);
        
        assertEquals("e2", from.getAlgebraicNotation());
        assertEquals("e4", to.getAlgebraicNotation());
        game.getChessboard().move(from, to);
        game.nextMove();
        
        assertEquals(1, game.getMoves().getMoveBackStack().size());
        String exportedState = dataExporter.exportData(game);
        assertEquals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1", exportedState);  
        assertEquals(1, game.getMoves().getMoveBackStack().size());
        
        from = chessboard.getSquare(2, 1);
        to   = chessboard.getSquare(2, 3);
        
        assertThat(from.getPiece(), instanceOf(Pawn.class));
        assertEquals("c7", from.getAlgebraicNotation());
        assertEquals("c5", to.getAlgebraicNotation());        
        chessboard.move(from, to);
        game.nextMove();
        
        exportedState = dataExporter.exportData(game);
        assertEquals(
            "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2",
            exportedState
        ); 
    }
    
    @Test
    public void testSimpleGame()
    {
        chessboard.move(
            chessboard.getSquare(Squares.SQ_G, Squares.SQ_1), 
            chessboard.getSquare(Squares.SQ_F, Squares.SQ_3) 
        );
        chessboard.move(
            chessboard.getSquare(Squares.SQ_G, Squares.SQ_8), 
            chessboard.getSquare(Squares.SQ_F, Squares.SQ_6) 
        );
        chessboard.move(
            chessboard.getSquare(Squares.SQ_G, Squares.SQ_2), 
            chessboard.getSquare(Squares.SQ_G, Squares.SQ_3) 
        );
        chessboard.move(
            chessboard.getSquare(Squares.SQ_G, Squares.SQ_7), 
            chessboard.getSquare(Squares.SQ_G, Squares.SQ_6) 
        );    
        chessboard.move(
            chessboard.getSquare(Squares.SQ_B, Squares.SQ_1), 
            chessboard.getSquare(Squares.SQ_C, Squares.SQ_3) 
        );    
        chessboard.move(
            chessboard.getSquare(Squares.SQ_F, Squares.SQ_8), 
            chessboard.getSquare(Squares.SQ_G, Squares.SQ_7) 
        );    
        assertEquals(
            "rnbqk2r/ppppppbp/5np1/8/8/2N2NP1/PPPPPP1P/R1BQKB1R w KQkq - 2 4",
            game.exportGame(dataExporter)
        );
    }
    
    @Test
    public void testSimpleImportAndExport() throws ReadGameError
    {    
        String state = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";
        game.importGame(state, dataImporter);
        assertEquals(state, game.exportGame(dataExporter));
    }

    @Test
    public void testSimpleImportAndExportWithFullAndHalfTurnCounter() throws ReadGameError
    {
        String state = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 4 5";
        game.importGame(state, dataImporter);
        assertEquals(state, game.exportGame(dataExporter));
    }
    
    @Test(expected = ReadGameError.class)
    public void testSimpleInvalidFile() throws ReadGameError
    {
        String state = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq 4 5";
        game.importGame(state, dataImporter);
        assertEquals(state, game.exportGame(dataExporter));
    }
    
    @Test(expected = ReadGameError.class)
    public void testSimpleInvalidNumberOfRows() throws ReadGameError
    {
        String state = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP b KQkq e3 4 5";
        game.importGame(state, dataImporter);
        assertEquals(state, game.exportGame(dataExporter));
    }    
}
