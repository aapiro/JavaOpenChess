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

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import pl.art.lach.mateusz.javaopenchess.core.Game;
import org.junit.Test;
import org.junit.Before;
import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.Colors;
import pl.art.lach.mateusz.javaopenchess.core.players.PlayerType;
import pl.art.lach.mateusz.javaopenchess.utils.GameModes;
import pl.art.lach.mateusz.javaopenchess.utils.GameTypes;
import pl.art.lach.mateusz.javaopenchess.core.GameFactory;
import pl.art.lach.mateusz.javaopenchess.core.Squares;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.DataExporter;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.DataImporter;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.DataTransferFactory;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.TransferFormat;
import pl.art.lach.mateusz.javaopenchess.core.exceptions.ReadGameError;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Bishop;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.King;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Knight;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Pawn;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Queen;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Rook;

import static org.junit.Assert.*;

/**
 *
 * @author Mateusz  Lach (matlak, msl)
 */
public class PGNNotationTest
{
    private Game game;
    
    private Chessboard chessboard;
    
    private String pgnSimple;
    
    private String pgnComplex;
    
    private DataExporter dataExporter =  DataTransferFactory.getExporterInstance(TransferFormat.PGN);
    
    private DataImporter dataImporter =  DataTransferFactory.getImporterInstance(TransferFormat.PGN);
    
    @Before
    public void setupOnce() throws IOException
    {
        game = GameFactory.instance(
            GameModes.NEW_GAME,
            GameTypes.LOCAL,
            "",
            "",
            PlayerType.LOCAL_USER,
            PlayerType.LOCAL_USER,
            true
        );
        chessboard = game.getChessboard();
        InputStream is = PGNNotationTest.class.getResourceAsStream("resources/joChess-test1.pgn");
        pgnSimple = IOUtils.toString(is, "UTF-8");
        is = PGNNotationTest.class.getResourceAsStream("resources/joChess-test2.pgn");
        pgnComplex = IOUtils.toString(is, "UTF-8");
    }
    
    @Test
    public void checkBasicPNGExport()
    {     
        String pgn = "1. Ng1-f3 Ng8-f6 2. g2-g3 g7-g6 3. Nb1-c3 Bf8-g7";
        
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
        assertEquals(pgn, getLastLine(game.exportGame(dataExporter)));
    }
    
    @Test
    public void checkBasicPNGImport() throws ReadGameError
    {
        assertNotNull(pgnSimple);
        assertNotNull(dataImporter);
        assertFalse("".equals(pgnSimple));
        assertTrue(0 == game.getMoves().getMoves().size()); 
        assertEquals(6, pgnSimple.split("\n").length);
        
        game.importGame(pgnSimple, dataImporter);
        
        Piece piece = chessboard.getSquare(Squares.SQ_G, Squares.SQ_7).getPiece();
        assertTrue(piece.getClass() == Bishop.class && piece.getPlayer().getColor() == Colors.BLACK);
        
        piece = chessboard.getSquare(Squares.SQ_C, Squares.SQ_3).getPiece();
        assertTrue(piece.getClass() == Knight.class && piece.getPlayer().getColor() == Colors.WHITE);
        
        piece = chessboard.getSquare(Squares.SQ_G, Squares.SQ_6).getPiece();
        assertTrue(piece.getClass() == Pawn.class && piece.getPlayer().getColor() == Colors.BLACK);
        
        piece = chessboard.getSquare(Squares.SQ_G, Squares.SQ_3).getPiece();
        assertTrue(piece.getClass() == Pawn.class && piece.getPlayer().getColor() == Colors.WHITE);
        
        piece = chessboard.getSquare(Squares.SQ_F, Squares.SQ_6).getPiece();
        assertTrue(piece.getClass() == Knight.class && piece.getPlayer().getColor() == Colors.BLACK);
        
        piece = chessboard.getSquare(Squares.SQ_F, Squares.SQ_3).getPiece();
        assertTrue(piece.getClass() == Knight.class && piece.getPlayer().getColor() == Colors.WHITE);
    }
    
    @Test
    public void importAndExport() throws ReadGameError
    {
        game.importGame(pgnSimple, dataImporter);
        String export = game.exportGame(dataExporter);
        assertEquals(getLastLine(pgnSimple), getLastLine(export));
    }
    
    @Test
    public void importAndExportComplexPGN() throws ReadGameError
    {
        game.importGame(pgnComplex, dataImporter);
        
        Piece piece = chessboard.getSquare(Squares.SQ_A, Squares.SQ_8).getPiece();
        assertTrue(piece.getClass() == King.class && piece.getPlayer().getColor() == Colors.BLACK);
        
        assertTrue(((King) piece).isChecked());
        
        piece = chessboard.getSquare(Squares.SQ_A, Squares.SQ_6).getPiece();
        assertTrue(piece.getClass() == Rook.class && piece.getPlayer().getColor() == Colors.WHITE);
        
        piece = chessboard.getSquare(Squares.SQ_B, Squares.SQ_1).getPiece();
        assertTrue(piece.getClass() == Queen.class && piece.getPlayer().getColor() == Colors.WHITE);
        
        piece = chessboard.getSquare(Squares.SQ_H, Squares.SQ_1).getPiece();
        assertTrue(piece.getClass() == King.class && piece.getPlayer().getColor() == Colors.WHITE);
        
        assertFalse(((King) piece).isChecked());
        
        assertEquals(getLastLine(pgnComplex), getLastLine(game.exportGame(dataExporter)));
    }
    
    @Test(expected = ReadGameError.class)
    public void testInvalidSyntaxFileEmptyMoveList() throws IOException, ReadGameError
    {
        InputStream is = PGNNotationTest.class.getResourceAsStream("resources/joChess-test-is1.pgn");
        String pgn = IOUtils.toString(is, "UTF-8");
        game.importGame(pgn, dataImporter);
    }
    
    
    @Test(expected = ReadGameError.class)
    public void testInvalidSyntaxFileWrongOperator() throws IOException, ReadGameError
    {
        InputStream is = PGNNotationTest.class.getResourceAsStream("resources/joChess-test-is2.pgn");
        String pgn = IOUtils.toString(is, "UTF-8");
        game.importGame(pgn, dataImporter);
    }
    
    @Test(expected = ReadGameError.class)
    public void testIllegalMoveCorrectSyntax() throws IOException, ReadGameError
    {
        InputStream is = PGNNotationTest.class.getResourceAsStream("resources/joChess-test-im1.pgn");
        String pgn = IOUtils.toString(is, "UTF-8");
        game.importGame(pgn, dataImporter);
    }
    
    private String getLastLine(String str)
    {
        if (null == str) 
        {
            return null;
        }
        return str.substring(str.lastIndexOf("1. "), str.length() - 1).trim();
    }

}
