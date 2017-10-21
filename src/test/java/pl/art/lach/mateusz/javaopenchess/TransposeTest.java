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
import pl.art.lach.mateusz.javaopenchess.core.moves.MovesHistory;
import pl.art.lach.mateusz.javaopenchess.display.views.chessboard.ChessboardView;
import pl.art.lach.mateusz.javaopenchess.display.views.chessboard.implementation.graphic2D.Chessboard2D;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;
import pl.art.lach.mateusz.javaopenchess.core.Game;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Ignore;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Mateusz  Lach (matlak, msl)
 */
public class TransposeTest
{
    
    private ChessboardView view;
    
    @Before
    public void setupOnce() 
    { 
        view = new Chessboard2D(new Chessboard(new Settings(), new MovesHistory(new Game())));
    }
    
    @Test
    public void transposeUpToHalfOfChessboard()
    {
        assertEquals(7, view.transposePosition(0));
        assertEquals(6, view.transposePosition(1));
        assertEquals(5, view.transposePosition(2));
        assertEquals(4, view.transposePosition(3));
    }
    
    @Test
    public void transposeUpFromHalfOfChessboard()
    {    
        assertEquals(3, view.transposePosition(4));
        assertEquals(2, view.transposePosition(5));
        assertEquals(1, view.transposePosition(6));
        assertEquals(0, view.transposePosition(7));
    }
}
