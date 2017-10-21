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

import pl.art.lach.mateusz.javaopenchess.core.Square;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Mateusz  Lach (matlak, msl)
 */
public class SquareTest
{
    

    @Test
    public void checkAlgebraicConversion()
    {
        Square sq = new Square(0, 0, null);
        
        assertEquals("a8", sq.getAlgebraicNotation());
        sq.setPozX(1);
        assertEquals("b8", sq.getAlgebraicNotation());
        sq.setPozX(7);
        assertEquals("h8", sq.getAlgebraicNotation());
        sq.setPozY(7);
        assertEquals("h1", sq.getAlgebraicNotation());
    }
    
}
