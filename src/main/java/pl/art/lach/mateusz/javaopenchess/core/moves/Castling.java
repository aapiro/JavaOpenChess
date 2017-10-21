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
package pl.art.lach.mateusz.javaopenchess.core.moves;

import pl.art.lach.mateusz.javaopenchess.core.Colors;

/**
 * @author Mateusz  Lach (matlak, msl)
 */
public enum Castling
{
    NONE("", new int[4], new int[4]), 
    
    SHORT_CASTLING("0-0", new int[] {4, 7, 6, 7}, new int[] {4, 0, 6, 0}),
    
    LONG_CASTLING("0-0-0", new int[] { 4, 7, 2, 7}, new int[] {4, 0, 2, 0});
    
    protected String symbol;
    
    protected int[] whiteMove;
    
    protected int[] blackMove;
    
    Castling(String symbol, int[] whiteMove, int[] blackMove)
    {
        this.symbol = symbol;
        this.whiteMove = whiteMove;
        this.blackMove = blackMove;
    }
    
    public String getSymbol()
    {
        return symbol;
    }
    
    public int[] getMove(Colors color)
    {
        if (Colors.BLACK == color)
        {
            return blackMove;
        }
        else 
        {
            return whiteMove;
        }
    }
    
    public static boolean isCastling(String moveInPGN)
    {
        return moveInPGN.equals(Castling.SHORT_CASTLING.getSymbol()) 
                || moveInPGN.equals(Castling.LONG_CASTLING.getSymbol());
    }
}
