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
package pl.art.lach.mateusz.javaopenchess.core.pieces;

import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.Colors;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Bishop;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.King;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Knight;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Pawn;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Queen;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Rook;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;

/**
 * @author Mateusz  Lach (matlak, msl)
 */
public class PieceFactory 
{

    public static final Piece getPiece(Chessboard chessboard, Colors color, String pieceType, Player player) 
    {
        return PieceFactory.getPiece(chessboard, color.getColorName(), pieceType, player);
    }
    
    public static final Piece getPiece(Chessboard chessboard, String color, String pieceType, Player player) 
    {
        Piece piece = null;
        switch (pieceType)
        {
            case "Queen":
                piece = new Queen(chessboard, player);
                break;
            case "Rook":
                piece = new Rook(chessboard, player);
                break;
            case "Bishop":
                piece = new Bishop(chessboard, player);
                break;
            case "Knight":
                piece = new Knight(chessboard, player);
                break;
            case "Pawn":
                piece = new Pawn(chessboard, player);
                break;                
        }
        return piece;
    }
    
    public static final Piece getPieceFromFenNotation(Chessboard chessboard, String pieceChar, Player whitePlayer, Player blackPlayer)
    {
        Piece result = null;
        Player player = null;
        if (pieceChar.toLowerCase().equals(pieceChar)) 
        {
            player = blackPlayer;
        }
        else
        {
            player = whitePlayer;
        }
        pieceChar = pieceChar.toLowerCase();
        switch(pieceChar)
        {
            case "p":
                result = new Pawn(chessboard, player);
                break;
            case "b":
                result = new Bishop(chessboard, player);
                break;
            case "q":
                result = new Queen(chessboard, player);
                break;
            case "r":
                result = new Rook(chessboard, player);
                break;
            case "k":
                result = new King(chessboard, player);
                break;
            case "n":
                result = new Knight(chessboard, player);
                break;
            default:
                result = null;
                break;
        }
        return result;
    }
}
