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
package pl.art.lach.mateusz.javaopenchess.core;

import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;

/**
 * Class to represent a chessboard square
 * 
 * @author: Mateusz  Lach ( matlak, msl )
 * @author: Damian Marciniak
 */
public class Square
{
    private static final int ASCII_OFFSET = 97;
    
    /**
     * X position of square 
     * 0-7, because 8 squares for row/column
     */
    protected int pozX; 

    /**
     * Y position of square
     * 0-7, because 8 squares for row/column
     */
    protected int pozY;
    
    /**
     * object Piece on square (and extending Piecie)
     */
    public Piece piece = null;

    public Square(int pozX, int pozY, Piece piece)
    {
        this.pozX = pozX;
        this.pozY = pozY;
        this.piece = piece;
    }/*--endOf-Square--*/


    public Square(Square square)
    {
        this.pozX = square.pozX;
        this.pozY = square.pozY;
        this.piece = square.piece;
    }

    public Square clone(Square square)
    {
        return new Square(square);
    }

    public void setPiece(Piece piece)
    {
        this.piece = piece;
        if (null != this.piece)
        {
            this.piece.setSquare(this);
        }
    }

    /**
     * @return the pozX
     */
    public int getPozX()
    {
        return pozX;
    }

    /**
     * @param pozX the pozX to set
     */
    public void setPozX(int pozX)
    {
        this.pozX = pozX;
    }

    /**
     * @return the pozY
     */
    public int getPozY()
    {
        return pozY;
    }

    /**
     * @param pozY the pozY to set
     */
    public void setPozY(int pozY)
    {
        this.pozY = pozY;
    }
    
    public Piece getPiece() {
        return piece;
    }
    
    public boolean isEmptyOrSamePiece(Piece piece)
    {
        return null == this.piece || this.piece == piece;
    }
    
    public String getAlgebraicNotation()
    {
        String letter = String.valueOf((char)(pozX + ASCII_OFFSET));
        String result = letter + (Math.abs(Chessboard.LAST_SQUARE - pozY) + 1);
        return result;
    }
}
