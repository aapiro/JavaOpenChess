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
package pl.art.lach.mateusz.javaopenchess.core.data_transfer.implementations;

import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.Colors;
import pl.art.lach.mateusz.javaopenchess.core.Game;
import pl.art.lach.mateusz.javaopenchess.core.GameFactory;
import pl.art.lach.mateusz.javaopenchess.core.Square;
import pl.art.lach.mateusz.javaopenchess.core.Squares;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.DataExporter;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.DataImporter;
import pl.art.lach.mateusz.javaopenchess.core.exceptions.ReadGameError;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.core.pieces.PieceFactory;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.King;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Pawn;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Rook;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;
import pl.art.lach.mateusz.javaopenchess.core.players.PlayerType;
import pl.art.lach.mateusz.javaopenchess.utils.GameModes;
import pl.art.lach.mateusz.javaopenchess.utils.GameTypes;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;

/**
 *
 * @author Mateusz  Lach (matlak, msl)
 */
public class FenNotation implements DataImporter, DataExporter
{
    public static final String INITIAL_STATE = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    
    private static final String BLACK_QUEEN_SYMBOL = "q";
    
    private static final String BLACK_KING_SYMBOL = "k";
    
    private final String WHITE_QUEEN_SYMBOL = "Q";
    
    private static final String WHITE_KING_SYMBOL = "K";    
    
    private static final int PIECES_STATE_NUM = 0;
    
    private static final int ACTIVE_PLAYER_NUM = 1;
    
    private static final int CASTLING_STATE_NUM = 2;   
    
    private static final int EN_PASSANT_STATE_NUM = 3;
    
    private static final int HALF_COUNTER_STATE_NUM = 4;
    
    private static final int FULL_COUNTER_STATE_NUM = 5;

    private static final String CHAR_PLAYER_WHITE = "w";
    
    private static final String CHAR_PLAYER_BLACK = "b";
    
    public static final String ROW_SEPARATOR = "/";
    
    public static final String FIELD_SEPARATOR = " ";
    
    public static final String FIELD_EMPTY = "-";  
    
    private static final String SQUARE_PREFIX = "SQ_";

    @Override
    public Game importData(String data) throws ReadGameError
    {
        String whiteName = "--";
        String blackName = "--";
        Game game = GameFactory.instance(
            GameModes.LOAD_GAME,
            GameTypes.LOCAL,
            whiteName,
            blackName,
            PlayerType.LOCAL_USER,
            PlayerType.LOCAL_USER,
            true,
            false
        );  
        importData(data, game);
        game.getChessboard().repaint();
        return game;
    }
    
    @Override
    public void importData(String data, Game game) throws ReadGameError
    {
        Chessboard chessboard = game.getChessboard();
        chessboard.clear();
        String[] fields = data.split(FIELD_SEPARATOR);
        if (NUMBER_OF_FIELDS != fields.length)
        {
            throw new ReadGameError(
                Settings.lang("invalid_fen_state"),
                Settings.lang("invalid_fen_number_of_fields")
            );
        }
        Player blackPlayer = game.getSettings().getPlayerBlack();
        Player whitePlayer = game.getSettings().getPlayerWhite();
        importPieces(fields[PIECES_STATE_NUM], game, whitePlayer, blackPlayer);
        importActivePlayer(fields[ACTIVE_PLAYER_NUM], game);
        importCastlingState(fields[CASTLING_STATE_NUM], chessboard);
        importEnPassantState(fields[EN_PASSANT_STATE_NUM], chessboard, game);
        importCounters(fields, game);
    }
    private static final int NUMBER_OF_FIELDS = 6;

    private void importCounters(String[] fields, Game game) throws ReadGameError
    {
        try
        {
            Integer halfCounter = Integer.parseInt(fields[HALF_COUNTER_STATE_NUM]); 
            game.getChessboard().setHalfCounter(halfCounter);
            game.getChessboard().setFullMoveCounterAdd(
                Integer.parseInt(fields[FULL_COUNTER_STATE_NUM])
            );
        }
        catch (NumberFormatException exc)
        {
            throw new ReadGameError(Settings.lang("invalid_fen_state"), fields[HALF_COUNTER_STATE_NUM]);
        }
    }

    private void importEnPassantState(String enPassantState, Chessboard chessboard, Game game) throws ReadGameError
    {
        if (!FIELD_EMPTY.equals(enPassantState) && enPassantState.length() == 2)
        {
            try
            {
                Squares sqX = Squares.valueOf(SQUARE_PREFIX + enPassantState.substring(0, 1).toUpperCase());
                Squares sqY = Squares.valueOf(SQUARE_PREFIX + enPassantState.substring(1, 2).toUpperCase());       
                if (Squares.SQ_3 == sqY)
                {
                    sqY = Squares.SQ_4;
                }
                else if (Squares.SQ_5 == sqY)
                {
                    sqY = Squares.SQ_6;
                }
                else
                {
                    throw new ReadGameError(Settings.lang("invalid_fen_state"), enPassantState);
                }
                Square sq = chessboard.getSquare(sqX, sqY);
                Piece piece = sq.getPiece(); 
                if (null != piece && Pawn.class == piece.getClass())
                {
                    game.getChessboard().setTwoSquareMovedPawn((Pawn)sq.getPiece());
                }
            }
            catch (IllegalStateException exc)
            {
                throw new ReadGameError(Settings.lang("invalid_fen_state"), enPassantState);
            }
        }
    }

    private void importCastlingState(String castlingState, Chessboard chessboard) throws ReadGameError
    {
        for (int i = 0, size = castlingState.length(); i < size; i++)
        {
            String state = castlingState.substring(i, (i+1));
            if (!FIELD_EMPTY.equals(state))
            {
                switch (state)
                {
                    case WHITE_KING_SYMBOL:
                        setupCastlingState(
                            chessboard.getSquare(Squares.SQ_E, Squares.SQ_1),
                            chessboard.getSquare(Squares.SQ_H, Squares.SQ_1)
                        );
                        break;
                    case WHITE_QUEEN_SYMBOL:
                        setupCastlingState(
                            chessboard.getSquare(Squares.SQ_E, Squares.SQ_1),
                            chessboard.getSquare(Squares.SQ_A, Squares.SQ_1)
                        );
                        break;
                    case BLACK_KING_SYMBOL:
                        setupCastlingState(
                            chessboard.getSquare(Squares.SQ_E, Squares.SQ_8),
                            chessboard.getSquare(Squares.SQ_H, Squares.SQ_8)
                        );
                        break;
                    case BLACK_QUEEN_SYMBOL:
                        setupCastlingState(
                            chessboard.getSquare(Squares.SQ_E, Squares.SQ_8),
                            chessboard.getSquare(Squares.SQ_A, Squares.SQ_8)
                        );
                        break;
                    default:
                        break;
                }
            }
            
        }
    }

    private void setupCastlingState(Square kingSquare, Square rookSquare) throws ReadGameError
    {
        King king;
        Rook rook;
        Piece piece = kingSquare.getPiece();
        if (King.class == piece.getClass())
        {
            king = (King) piece;
            king.setWasMotioned(false);
        }
        else
        {
            throw new ReadGameError(Settings.lang("invalid_fen_state"));
        }
        piece = rookSquare.getPiece();
        if (Rook.class == piece.getClass())
        {
            rook = (Rook) piece;
            rook.setWasMotioned(false);
        }
        else
        {
            throw new ReadGameError(Settings.lang("invalid_fen_state"));
        }
    }

    private void importActivePlayer(String activePlayer, Game game)
    {
        if (CHAR_PLAYER_WHITE.equals(activePlayer))
        {
            game.setActivePlayer(game.getSettings().getPlayerWhite());
        }
        else
        {
            game.setActivePlayer(game.getSettings().getPlayerBlack());
        }
    }


    private void importPieces(String piecesStateString, Game game, 
            Player whitePlayer, Player blackPlayer) throws ReadGameError
    {
        int currentY = Squares.SQ_8.getValue();
        String[] rows = piecesStateString.split(ROW_SEPARATOR);
        if (NUMBER_OF_ROWS != rows.length)
        {
            throw new ReadGameError(
                Settings.lang("invalid_fen_state"),
                Settings.lang("invalid_fen_number_of_rows")
            );
        }
        for (String row : piecesStateString.split(ROW_SEPARATOR))
        {
            int currentX = Squares.SQ_A.getValue();
            for (int i = 0; i < row.length(); i++)
            {
                String currChar = row.substring(i, (i+1));
                try
                {
                    Integer currNumber = Integer.parseInt(currChar);
                    currentX += currNumber;
                }
                catch (NumberFormatException nfe)
                {
                    Piece piece = PieceFactory.getPieceFromFenNotation(
                        game.getChessboard(),
                        currChar,
                        whitePlayer,
                        blackPlayer
                    );
                    Square square = game.getChessboard().getSquare(currentX, currentY);
                    square.setPiece(piece);
                    currentX++;
                }
            }
            currentY++;
        }
    }
    private static final int NUMBER_OF_ROWS = 8;
    
    @Override
    public String exportData(Game game)
    {
        StringBuilder result = new StringBuilder();
        result.append(exportChessboardFields(game));
        result.append(FIELD_SEPARATOR);
        result.append(exportActivePlayer(game));
        result.append(FIELD_SEPARATOR);
        result.append(exportCastlingState(game));
        result.append(FIELD_SEPARATOR);
        result.append(exportEnPassantState(game));
        result.append(FIELD_SEPARATOR);
        result.append(game.getChessboard().getHalfCounter());
        result.append(exportFullMoveCounter(game));
        return result.toString();
    }

    private String exportFullMoveCounter(Game game) 
    {
        int size = game.getMoves().getMoveBackStack().size();
        int counter    = (((int)(size / 2)) + 1);
        int counterAdd = game.getChessboard().getFullMoveCounterAdd();
        if (counterAdd > 0)
        {
            counter += counterAdd - 1;
        }
        return FIELD_SEPARATOR + counter;
    }

    private String exportEnPassantState(Game game)
    {
        StringBuilder result = new StringBuilder();
        Pawn pawn = (Pawn)game.getChessboard().getTwoSquareMovedPawn();
        if (null != pawn)
        {
            Square pawnSquare = pawn.getSquare();
            Square testSquare = null;
            if (Colors.WHITE == pawn.getPlayer().getColor())
            {
                testSquare = new Square(pawnSquare.getPozX(), pawnSquare.getPozY() + 1, null);
            }
            else
            {
                testSquare = new Square(pawnSquare.getPozX(), pawnSquare.getPozY() - 1, null);
            }
            result.append(testSquare.getAlgebraicNotation());
        }
        else
        {
            result.append(FIELD_EMPTY);
        }
        return result.toString();
    }

    private String exportCastlingState(Game game) 
    {
        String result = "";
        Chessboard chessboard = game.getChessboard();
        result += exportCastlingOfOneColor(chessboard.getKingWhite(), chessboard, Squares.SQ_1);
        result += exportCastlingOfOneColor(chessboard.getKingBlack(), chessboard, Squares.SQ_8);
        return result;
    }

    private String exportCastlingOfOneColor(King king, Chessboard chessboard, Squares squareLine)
    {
        String result = "";
        Colors color = king.getPlayer().getColor();
        if (!king.getWasMotioned())
        {
            Piece piece = chessboard.getSquare(Squares.SQ_A, squareLine).getPiece();
            if (piece instanceof Rook)
            {
                Rook rightRook = (Rook)piece;
                if (rightRook.getWasMotioned())
                {
                    result += FIELD_EMPTY;
                }
                else 
                {
                    result += color == Colors.WHITE ? WHITE_KING_SYMBOL : BLACK_KING_SYMBOL;
                }
            }
            
            piece = chessboard.getSquare(Squares.SQ_H, squareLine).getPiece();
            if (piece instanceof Rook)
            {
                Rook leftRook = (Rook)piece;
                if (leftRook.getWasMotioned())
                {
                    result += FIELD_EMPTY;
                }
                else 
                {
                    result += color == Colors.WHITE ? WHITE_QUEEN_SYMBOL : BLACK_QUEEN_SYMBOL;
                }
            }
            
        }
        else
        {
            result += "-";
        }
        return result;
    }

    private String exportChessboardFields(Game game)
    {
        String result = "";
        Chessboard chessboard = game.getChessboard();
        for (int y = Chessboard.FIRST_SQUARE; y <= Chessboard.LAST_SQUARE; y++) 
        {
            int emptySquares = 0;
            for (int x = Chessboard.FIRST_SQUARE; x <= Chessboard.LAST_SQUARE; x++)
            {
                Square sq = chessboard.getSquare(x, y);
                Piece piece = sq.getPiece();
                if (null == piece)
                {
                    emptySquares++;
                }
                else
                {
                    if (0 != emptySquares)
                    {
                        result += emptySquares;
                        emptySquares = 0;
                    }
                    String symbol = null;
                    if (piece instanceof Pawn)
                    {
                        symbol = "P";
                    }
                    else
                    {
                        symbol = piece.getSymbol();
                    }
                    result += piece.getPlayer().getColor() == Colors.WHITE ? symbol.toUpperCase() : symbol.toLowerCase();
                }
            }
            if (0 != emptySquares) 
            {
                result += emptySquares;
            }
            if (Chessboard.LAST_SQUARE != y)
            {
                result += ROW_SEPARATOR;
            }
            emptySquares = 0;
        }
        return result;
    }

    private String exportActivePlayer(Game game)
    {
        String result = "";
        if (Colors.WHITE == game.getActivePlayer().getColor())
        {
            result += CHAR_PLAYER_WHITE;
        }
        else
        {
            result += CHAR_PLAYER_BLACK;
        }
        return result;
    }


}
