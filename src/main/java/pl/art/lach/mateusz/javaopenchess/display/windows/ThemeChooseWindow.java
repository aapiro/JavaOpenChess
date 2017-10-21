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
package pl.art.lach.mateusz.javaopenchess.display.windows;

import pl.art.lach.mateusz.javaopenchess.JChessApp;
import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.io.File;
import java.util.Properties;
import java.io.FileOutputStream;
import java.io.IOException;
import pl.art.lach.mateusz.javaopenchess.utils.GUI;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;
import org.apache.log4j.Logger;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

/**
 * @author : Mateusz  Lach ( matlak, msl )
 * @author : Damian Marciniak
 */
public class ThemeChooseWindow extends JDialog implements ActionListener, ListSelectionListener
{
    private static final Logger LOG = Logger.getLogger(ThemeChooseWindow.class);
    
    JList themesList;
    ImageIcon themePreview;
    GridBagLayout gbl;
    public String result;
    GridBagConstraints gbc;
    JButton themePreviewButton;
    JButton okButton;

    public ThemeChooseWindow(Frame parent) throws Exception
    {
        super(parent);

        File dir = new File(GUI.getJarPath() + File.separator + "theme"+File.separator);

        LOG.debug("Theme path: " + dir.getPath());

        File[] files = dir.listFiles();
        if (files != null && dir.exists())
        {
            this.setTitle(Settings.lang("choose_theme_window_title"));
            Dimension winDim = new Dimension(550, 230);
            this.setMinimumSize(winDim);
            this.setMaximumSize(winDim);
            this.setSize(winDim);
            this.setResizable(false);
            this.setLayout(null);
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);            
            String[] dirNames = new String[files.length];
            for (int i = 0; i < files.length; i++)
            {
                dirNames[i] = files[i].getName();
            }
            this.themesList = new JList(dirNames);
            this.themesList.setLocation(new Point(10, 10));
            this.themesList.setSize(new Dimension(100, 120));
            this.add(this.themesList);
            this.themesList.setSelectionMode(0);
            this.themesList.addListSelectionListener(this);
            Properties prp = GUI.getConfigFile();
            
            this.gbl = new GridBagLayout();
            this.gbc = new GridBagConstraints();
            try
            {
                this.themePreview = new ImageIcon(GUI.loadImage("Preview.png"));
            }
            catch (NullPointerException exc)
            {
                LOG.error("NullPointerException: Cannot find preview image: ", exc);
                this.themePreview = new ImageIcon(JChessApp.class.getResource("theme/noPreview.png"));
                return;
            }
            this.result = "";
            this.themePreviewButton = new JButton(this.themePreview);
            this.themePreviewButton.setLocation(new Point(110, 10));
            this.themePreviewButton.setSize(new Dimension(420, 120));
            this.add(this.themePreviewButton);
            this.okButton = new JButton("OK");
            this.okButton.setLocation(new Point(175, 140));
            this.okButton.setSize(new Dimension(200, 50));
            this.add(this.okButton);
            this.okButton.addActionListener(this);
            this.setModal(true);
        }
        else
        {
            throw new Exception(Settings.lang("error_when_creating_theme_config_window"));
        }

    }

    @Override
    public void valueChanged(ListSelectionEvent event)
    {
        String element = this.themesList.getModel().getElementAt(this.themesList.getSelectedIndex()).toString();
        String path = GUI.getJarPath() + File.separator + "theme/";
        //String path  = JChessApp.class.getResource("theme/").getPath().toString();
        
        LOG.debug(path + element + "/images/Preview.png");
        
        this.themePreview = new ImageIcon(path + element + "/images/Preview.png");
        this.themePreviewButton.setIcon(this.themePreview);
    }

    /** Method wich is changing a pawn into queen, rook, bishop or knight
     * @param arg0 Capt information about performed action
     */
    public void actionPerformed(ActionEvent evt)
    {
        if (evt.getSource() == this.okButton)
        {
            Properties prp = GUI.getConfigFile();
            int element = this.themesList.getSelectedIndex();
            String name = this.themesList.getModel().getElementAt(element).toString();
            if (GUI.themeIsValid(name))
            {
                prp.setProperty("THEME", name);
                try
                {
                    FileOutputStream fOutStr = new FileOutputStream("config.txt");
                    prp.store(fOutStr, null);
                    fOutStr.flush();
                    fOutStr.close();
                }
                catch (IOException exc)
                {
                    LOG.error("actionPerformed/IOException: ", exc);
                }
                JOptionPane.showMessageDialog(this, Settings.lang("changes_visible_after_restart"));
                this.setVisible(false);

            }
            LOG.debug("property theme: " + prp.getProperty("THEME"));
        }
    }
}
