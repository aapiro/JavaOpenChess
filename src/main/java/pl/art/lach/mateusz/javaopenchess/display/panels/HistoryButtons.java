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
package pl.art.lach.mateusz.javaopenchess.display.panels;

import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JButton;
import javax.swing.JPanel;
import pl.art.lach.mateusz.javaopenchess.core.Game;

/**
 * Class responsible for rendering and handling buttons responsible for
 * undo/redo operations on moves history.
 * @author: Mateusz  Lach ( matlak, msl )
 */
public class HistoryButtons extends JPanel
{
    private static final Dimension HISTORY_BUTTON_SIZE = new Dimension(45, 30);    

    private static final String REDO_ALL = ">|";
    
    private static final String REDO = ">";
    
    private static final String UNDO = "<";
    
    private static final String UNDO_ALL = "|<";    
    
    private JButton undoButton;
    
    private JButton undoAllButton;
    
    private JButton redoButton;
    
    private JButton redoAllButton;
    
    private Game game;
    
    public HistoryButtons(Game game)
    {
        super();
        this.game = game;
        initHistoryButtons();
    }
    
    protected final void initHistoryButtons()
    {
        this.setLayout(null);
        initButton(this.undoAllButton, UNDO_ALL, new Point(0, 0))
        .addActionListener((evt) -> {
            if (null != game)
            {
                while (game.undo());
            }
        });
        
        initButton(this.undoButton, UNDO, new Point(45, 0))
        .addActionListener((evt) -> {
            if (null != game)
            {
                game.undo();
            }
        });

        initButton(this.redoButton, REDO, new Point(90, 0))
        .addActionListener((evt) -> {
            if (null != game)
            {
                game.redo();
            }
        });

        initButton(this.redoAllButton, REDO_ALL, new Point(135, 0))
        .addActionListener((evt) -> {
            if (null != game)
            {
                while (game.redo());
            }
        });
    }

    private JButton initButton(JButton button, String buttonText, Point location)
    {
        button = new JButton(buttonText);
        button.setSize(HISTORY_BUTTON_SIZE);
        button.setLocation(location);
        add(button);
        return button;
    }
    
    @Override
    public void repaint()
    {
        if (null != undoButton)
        {
            undoButton.repaint();
        }
        if (null != undoAllButton)
        {
            undoAllButton.repaint();
        }
        if (null != redoButton)
        {
            redoButton.repaint();
        }
        if (null != redoAllButton)
        {
            redoAllButton.repaint();
        }
    }
}
