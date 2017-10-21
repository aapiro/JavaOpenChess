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
#    along with this program.  If not, see <http://www.gnu.org/licenses/>..
 */
package pl.art.lach.mateusz.javaopenchess.display.windows;

import pl.art.lach.mateusz.javaopenchess.JChessView;
import pl.art.lach.mateusz.javaopenchess.core.GameClock;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.art.lach.mateusz.javaopenchess.utils.GUI;
import pl.art.lach.mateusz.javaopenchess.JChessApp;
import pl.art.lach.mateusz.javaopenchess.core.Game;
import org.apache.log4j.Logger;


/**
 * @authors: Mateusz  Lach ( matlak, msl )
 * @authors: Damian Marciniak
 */
public class JChessTabbedPane extends JTabbedPane implements MouseListener, ImageObserver, ChangeListener
{
    private static final Logger LOG = Logger.getLogger(GameClock.class);

    private TabbedPaneIcon closeIcon;
    
    private Image addIcon = null;
    
    private Image unclickedAddIcon = null;
    
    private Rectangle addIconRect = null;
    
    public static final Color DEFAULT_COLOR = Color.BLACK;
    
    public static final Color EVENT_COLOR = Color.RED;

    public JChessTabbedPane()
    {
        super();
        this.closeIcon = new TabbedPaneIcon(this.closeIcon);
        this.unclickedAddIcon = GUI.loadImage("add-tab-icon.png");
        this.addIcon = this.unclickedAddIcon;
        this.setDoubleBuffered(true);
        initListeners();
    }

    protected final void initListeners()
    {
        this.addChangeListener(this);
        this.addMouseListener(this);
    }

    @Override
    public void addTab(String title, Component component)
    {
        this.addTab(title, component, null);
    }

    public void addTab(String title, Component component, Icon closeIcon)
    {
        super.addTab(title, new TabbedPaneIcon(closeIcon), component);
        LOG.debug("Present number of tabs: " + this.getTabCount());
        this.updateAddIconRect();
    }

    @Override
    public void mouseReleased(MouseEvent e){}

    @Override
    public void mousePressed(MouseEvent e){}

    private void showNewGameWindow()
    {
        JChessView jcv = JChessApp.getJavaChessView();
        if (JChessApp.getJavaChessView().getNewGameFrame() == null)
        {
            jcv.setNewGameFrame(new NewGameWindow());
        }
        JChessApp.getApplication().show(JChessApp.getJavaChessView().getNewGameFrame());
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        Rectangle rect; 
        int tabNumber = getUI().tabForCoordinate(this, e.getX(), e.getY());
        if (tabNumber >= 0)
        {
            rect = ((TabbedPaneIcon) getIconAt(tabNumber)).getBounds();
            if (rect.contains(e.getX(), e.getY()))
            {
                LOG.debug("Removing tab with " + tabNumber + " number!...");
                this.removeTabAt(tabNumber);//remove tab
                this.updateAddIconRect();
                if(this.getTabCount() == 0)
                {
                    this.showNewGameWindow();
                }
            }
            if (0 < this.getTabCount() && null != this.getTabComponentAt(getSelectedIndex())) 
            {
                this.getTabComponentAt(getSelectedIndex()).repaint();
            }
            else if (0 < this.getTabCount() && null != this.getComponent(getSelectedIndex()))
            {
                Component activeTab = this.getComponent(getSelectedIndex());
                this.setForegroundAt(getSelectedIndex(), DEFAULT_COLOR);
                activeTab.repaint();                
            }
        }
        else if (this.addIconRect != null && this.addIconRect.contains(e.getX(), e.getY()))
        {
            LOG.debug("newGame by + button");
            this.showNewGameWindow();
        }
    }
    
    public void highlightTab(Game game) 
    {
        int tabNumber = pl.art.lach.mateusz.javaopenchess.JChessApp.getJavaChessView().getTabNumber(game);
        this.highlightTab(tabNumber);
    }
    
    public void highlightTab(int number)
    {
        if (number < this.getTabCount())
        {
            this.setForegroundAt(number, EVENT_COLOR);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e){}

    @Override
    public void mouseExited(MouseEvent e){}

    private void updateAddIconRect()
    {
        if (this.getTabCount() > 0)
        {
            Rectangle rect = this.getBoundsAt(this.getTabCount() - 1);
            this.addIconRect = new Rectangle(
                rect.x + rect.width + 5,
                rect.y,
                this.addIcon.getWidth(this),
                this.addIcon.getHeight(this)
            );
        }
        else
        {
            this.addIconRect = null;
        }
    }

    private Rectangle getAddIconRect()
    {
        return this.addIconRect;
    }

    @Override
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
    {
        super.imageUpdate(img, infoflags, x, y, width, height);
        this.updateAddIconRect();
        return true;
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        Rectangle rect = this.getAddIconRect();
        if (rect != null)
        {
            g.drawImage(this.addIcon, rect.x, rect.y, null);
        }
    }

    @Override
    public void update(Graphics g)
    {
        this.repaint();
    }

    @Override
    public void stateChanged(ChangeEvent changeEvent)
    {
        if (1 != getTabCount())
        {
            JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
            Game game = (Game) sourceTabbedPane.getSelectedComponent();
            if (null != game)
            {
                game.resizeGame(); 
            }
        }
    }
}


class TabbedPaneIcon implements Icon
{
    private int x_pos;
    
    private int y_pos;
    
    private int width;
    
    private int height;
    
    private Icon fileIcon;

    public TabbedPaneIcon(Icon fileIcon)
    {
        this.fileIcon = fileIcon;
        this.width = 16;
        this.height = 16;
    }//--endOf-TabbedPaneIcon--

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y)
    {
        this.x_pos = x;
        this.y_pos = y;

        Color col = g.getColor();

        g.setColor(Color.black);
        int yP = y + 2;
        g.drawLine(x + 3, yP + 3, x + 10, yP + 10);
        g.drawLine(x + 3, yP + 4, x + 9, yP + 10);
        g.drawLine(x + 4, yP + 3, x + 10, yP + 9);
        g.drawLine(x + 10, yP + 3, x + 3, yP + 10);
        g.drawLine(x + 10, yP + 4, x + 4, yP + 10);
        g.drawLine(x + 9, yP + 3, x + 3, yP + 9);
        g.setColor(col);
        if (fileIcon != null)
        {
            fileIcon.paintIcon(c, g, x + width, yP);
        }
    }//--endOf-PaintIcon--

    @Override
    public int getIconWidth()
    {
        return width + (fileIcon != null ? fileIcon.getIconWidth() : 0);
    }//--endOf-getIconWidth--

    @Override
    public int getIconHeight()
    {
        return height;
    }//--endOf-getIconHeight()--

    public Rectangle getBounds()
    {
        return new Rectangle(x_pos, y_pos, width, height);
    }
}