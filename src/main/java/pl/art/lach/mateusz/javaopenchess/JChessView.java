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

import pl.art.lach.mateusz.javaopenchess.core.Game;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.*;
import java.io.File;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.ActionMap;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.io.FileUtils;
import pl.art.lach.mateusz.javaopenchess.utils.GUI;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;
import pl.art.lach.mateusz.javaopenchess.display.windows.JChessAboutBox;
import pl.art.lach.mateusz.javaopenchess.display.windows.PawnPromotionWindow;
import pl.art.lach.mateusz.javaopenchess.display.windows.ThemeChooseWindow;
import org.apache.log4j.Logger;
import org.jdesktop.application.Application;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.DataExporter;
import pl.art.lach.mateusz.javaopenchess.display.windows.NewGameWindow;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.DataImporter;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.DataTransferFactory;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.TransferFormat;
import pl.art.lach.mateusz.javaopenchess.core.exceptions.ReadGameError;
import pl.art.lach.mateusz.javaopenchess.display.windows.JChessTabbedPane;



/**
 * The application's main frame.
 */
public class JChessView extends FrameView implements ActionListener, ComponentListener
{
    private static final Logger LOG = Logger.getLogger(JChessView.class);
    
    private static final String TAB_LABEL_STRING_FORMAT = "%s vs %s";
    
    private static final String DOT_REGEXP = "\\.";
    
    private static final String DOT = ".";
    
    protected static GUI gui = null;

    /**
     * @return the gui
     */
    public static GUI getGui()
    {
        return gui;
    }

    public Game addNewTab(String title)
    {
        Game game = new Game();
        this.gamesPane.addTab(title, game);
        return game;
    }
    
    public void addNewTab(Game game)
    {
        if (null != game)
        {
            String title = String.format(TAB_LABEL_STRING_FORMAT,
                game.getSettings().getPlayerWhite().getName(),
                game.getSettings().getPlayerBlack().getName()
            );
            this.gamesPane.addTab(title, game);
        }
    }

    public Component getTabComponent(Game game)
    {
        int tabNumber = this.getTabNumber(game);
        if (0 <= tabNumber)
        {
            return this.gamesPane.getComponent(tabNumber);
        }
        return null;
    }
    
    public int getTabNumber(Game game)
    {
        for (int i=0; i < this.gamesPane.getTabCount(); i++)
        {
            Component component = this.gamesPane.getComponent(i);
            if (game == component)
            {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public void actionPerformed(ActionEvent event)
    {
        Object target = event.getSource();
        if (target == newGameItem)
        {
            newGame();
        }
        else if (target == saveGameItem) //saveGame
        { 
            saveGame();
        }
        else if (target == loadGameItem)//loadGame
        { 
            loadGame();
        }
        else if (target == this.themeSettingsMenu)
        {
            runThemeSettingsWindow();
        }
    }
    ///--endOf- don't delete, becouse they're interfaces for MouseEvent

    private void runThemeSettingsWindow() throws HeadlessException
    {
        try
        {
            ThemeChooseWindow choose = new ThemeChooseWindow(this.getFrame());
            JChessApp.getApplication().show(choose);
        }
        catch(Exception exc)
        {
            LOG.error(
                "Something wrong creating window - perhaps themeList is null: ",
                exc
            );
            JOptionPane.showMessageDialog(
                JChessApp.getApplication().getMainFrame(),
                exc.getMessage()
            );
        }
    }

    private boolean saveGame() throws HeadlessException
    {
        if (this.gamesPane.getTabCount() == 0)
        {
            JOptionPane.showMessageDialog(
                null,
                Settings.lang("save_not_called_for_tab")
            );
            return true;
        }
        while (true) //until
        {
            JFileChooser fc = initFileChooser();
            int retVal = fc.showSaveDialog(this.gamesPane);
            if (retVal == JFileChooser.APPROVE_OPTION)
            {
                File selFile = fc.getSelectedFile();
                TransferFormat tf = getDataTransfer(selFile, (FileNameExtensionFilter)fc.getFileFilter());
                selFile = getRenamedFile(selFile, tf);
                int index = this.gamesPane.getSelectedIndex();
                Game tempGUI = (Game) this.gamesPane.getComponentAt(index);
                if (!selFile.exists())
                {
                    try
                    {
                        selFile.createNewFile();
                    }
                    catch (IOException exc)
                    {
                        LOG.error("error creating file: ", exc);
                    }
                }
                else if (selFile.exists())
                {
                    int opt = JOptionPane.showConfirmDialog(
                        tempGUI,
                        Settings.lang("file_exists"),
                        Settings.lang("file_exists"),
                        JOptionPane.YES_NO_OPTION
                    );
                    if (opt == JOptionPane.NO_OPTION)//if user choose to now overwrite
                    {
                        continue; // go back to file choose
                    }
                }
                if (selFile.canWrite())
                {
                    try
                    {
                        DataExporter dataExporter = DataTransferFactory.getExporterInstance(tf);
                        tempGUI.saveGame(selFile, dataExporter);
                    } 
                    catch (IllegalArgumentException exc)
                    {
                        LOG.error(exc);
                        JOptionPane.showMessageDialog(
                            null,
                            Settings.lang("unknown_format")
                        );
                    }
                }
                LOG.debug(fc.getSelectedFile().isFile());
                break;
            }
            else if (retVal == JFileChooser.CANCEL_OPTION)
            {
                break;
            }
        }
        return false;
    }
    
    private File getRenamedFile(File selFile, TransferFormat tf) 
    {
        String fullPath  = selFile.getAbsolutePath();
        File resultFile = null;
        if (fullPath.lastIndexOf("\\") - 1 == fullPath.length()) 
        {
            fullPath = fullPath.substring(0, fullPath.lastIndexOf("\\"));
        }
        int lastDotPos   = fullPath.lastIndexOf(DOT);
        String newFilePath = selFile.getAbsolutePath() + DOT + tf.name().toLowerCase();
        if (lastDotPos >= 0)
        {
            String extension = fullPath.substring(fullPath.lastIndexOf(DOT), 
                fullPath.length() - fullPath.lastIndexOf(DOT)
            );
            if (!extension.equalsIgnoreCase(tf.name())) 
            {
                resultFile = new File(newFilePath);
            }
        } 
        else 
        {
            resultFile = new File(newFilePath);
        }
        return resultFile == null ? selFile : resultFile;
    }

    private void newGame()
    {
        this.setNewGameFrame(new NewGameWindow());
        JChessApp.getApplication().show(this.getNewGameFrame());
    }

    private void loadGame() throws HeadlessException
    {
        JFileChooser fc = initFileChooser();
        int retVal = fc.showOpenDialog(this.gamesPane);
        if (retVal == JFileChooser.APPROVE_OPTION)
        {
            File file = fc.getSelectedFile();
            if (file.exists() && file.canRead())
            {
                DataImporter di = DataTransferFactory.getImporterInstance(
                    getDataTransfer(file, (FileNameExtensionFilter)fc.getFileFilter())
                );
                Game game;
                try
                {
                    game = di.importData(FileUtils.readFileToString(file));
                    this.addNewTab(game);
                    if (null != JChessApp.getJavaChessView())
                    {
                        //TODO: refactor this... 
                        JChessApp.getJavaChessView().setLastTabAsActive();
                    }
                }
                catch (IOException exc)
                {
                    LOG.error(exc);
                    JOptionPane.showMessageDialog(
                        null,
                        Settings.lang("error_writing_to_file")+": " + exc
                    );
                }
                catch (ReadGameError exc)
                {
                    LOG.error(exc);
                    JOptionPane.showMessageDialog(null, exc.getMessage());
                }
                catch (IllegalArgumentException exc)
                {
                    LOG.error(exc);
                    JOptionPane.showMessageDialog(
                        null,
                        Settings.lang("unknown_format")
                    );
                } 
            }
        }
    }

    private TransferFormat getDataTransfer(File file, FileNameExtensionFilter fileFilter)
    {
        String name = file.getName();
        String[] nameParts = name.split(DOT_REGEXP);
        String extension = nameParts[nameParts.length - 1];
        if (!extension.equalsIgnoreCase(fileFilter.getDescription()))
        {
            extension = fileFilter.getExtensions()[0].toUpperCase();
        }
        return TransferFormat.valueOf(extension.toUpperCase());
    }

    private JFileChooser initFileChooser()
    {
        JFileChooser fc = new JFileChooser();
        FileFilter pgnFilter = new FileNameExtensionFilter(
            Settings.lang("pgn_file"),
            new String[] {"pgn"}
        );
        FileFilter fenFilter = new FileNameExtensionFilter(
            Settings.lang("fen_file"),
            new String[] {"fen"}
        );
        fc.setFileFilter(fenFilter);
        fc.setFileFilter(pgnFilter);
        return fc;
    }
        
    public JChessView(SingleFrameApplication app) 
    {
        super(app);
        initComponents();
        /* status bar initialization - message timeout,
          idle icon and busy animation, etc */
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, (ActionEvent e) -> {
            statusMessageLabel.setText("");
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, (ActionEvent e) -> {
            busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
            statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener((evt) -> {
                String propertyName = evt.getPropertyName();
                switch (propertyName) 
                {
                    case "started":
                        if (!busyIconTimer.isRunning())
                        {
                            statusAnimationLabel.setIcon(busyIcons[0]);
                            busyIconIndex = 0;
                            busyIconTimer.start();
                        }
                        progressBar.setVisible(true);
                        progressBar.setIndeterminate(true);
                        break;
                    case "done":
                        busyIconTimer.stop();
                        statusAnimationLabel.setIcon(idleIcon);
                        progressBar.setVisible(false);
                        progressBar.setValue(0);
                        break;
                    case "message":
                        String text = (String)(evt.getNewValue());
                        statusMessageLabel.setText((text == null) ? "" : text);
                        messageTimer.restart();
                        break;
                    case "progress":
                        int value = (Integer)(evt.getNewValue());
                        progressBar.setVisible(true);
                        progressBar.setIndeterminate(false);
                        progressBar.setValue(value);
                        break;
                }
        });
        
    }

    @Action
    public void showAboutBox()
    {
        if (aboutBox == null)
        {
            JFrame mainFrame = JChessApp.getApplication().getMainFrame();
            aboutBox = new JChessAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        JChessApp.getApplication().show(aboutBox);
    }

    public String showPawnPromotionBox(String color)
    {
        if (promotionBox == null)
        {
            JFrame mainFrame = JChessApp.getApplication().getMainFrame();
            promotionBox = new PawnPromotionWindow(mainFrame, color);
            promotionBox.setLocationRelativeTo(mainFrame);
            promotionBox.setModal(true);
            
        }
        promotionBox.setColor(color);
        JChessApp.getApplication().show(promotionBox);
        return promotionBox.result;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents()
    {
        JMenu fileMenu = new JMenu();
        JMenuItem exitMenuItem = new JMenuItem();
        JMenu helpMenu = new JMenu();
        JMenuItem aboutMenuItem = new JMenuItem();
        JSeparator statusPanelSeparator = new JSeparator();
        mainPanel = new JPanel();
        gamesPane = new JChessTabbedPane();
        menuBar = new JMenuBar();
        
        loadGameItem = new JMenuItem();
        saveGameItem = new JMenuItem();
        gameMenu = new JMenu();
        moveBackItem = new JMenuItem();
        moveForwardItem = new JMenuItem();
        rewindToBegin = new JMenuItem();
        rewindToEnd = new JMenuItem();
        optionsMenu = new JMenu();
        themeSettingsMenu = new JMenuItem();
        statusPanel = new JPanel();
        statusMessageLabel = new JLabel();
        progressBar = new JProgressBar();

        mainPanel.setMaximumSize(new Dimension(800, 600));
        mainPanel.setMinimumSize(new Dimension(800, 600));
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new Dimension(800, 600));

        gamesPane.setName("gamesPane"); // NOI18N

        GroupLayout mainPanelLayout = new GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(gamesPane, GroupLayout.DEFAULT_SIZE, 776, Short.MAX_VALUE)
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(gamesPane, GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE))
        );

        menuBar.setName("menuBar"); // NOI18N

        ResourceMap resourceMap = Application.getInstance(JChessApp.class)
                .getContext()
                .getResourceMap(JChessView.class);
        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        initNewGameItem(resourceMap, fileMenu);

        loadGameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK));
        loadGameItem.setText(resourceMap.getString("loadGameItem.text")); // NOI18N
        loadGameItem.setName("loadGameItem"); // NOI18N
        fileMenu.add(loadGameItem);
        loadGameItem.addActionListener(this);

        saveGameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        saveGameItem.setText(resourceMap.getString("saveGameItem.text")); // NOI18N
        saveGameItem.setName("saveGameItem"); // NOI18N
        fileMenu.add(saveGameItem);
        saveGameItem.addActionListener(this);

        ActionMap actionMap = Application.getInstance(JChessApp.class)
                .getContext()
                .getActionMap(JChessView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        gameMenu.setText(resourceMap.getString("gameMenu.text")); // NOI18N
        gameMenu.setName("gameMenu"); // NOI18N

        moveBackItem.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK)
        );
        moveBackItem.setText(resourceMap.getString("moveBackItem.text")); // NOI18N
        moveBackItem.setName("moveBackItem"); // NOI18N
        moveBackItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                moveBackItemMouseClicked(evt);
            }
        });
        moveBackItem.addActionListener((evt) -> {
            moveBackItemActionPerformed(evt);
        });
        gameMenu.add(moveBackItem);

        moveForwardItem.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK)
        );
        moveForwardItem.setText(resourceMap.getString("moveForwardItem.text")); // NOI18N
        moveForwardItem.setName("moveForwardItem"); // NOI18N
        moveForwardItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                moveForwardItemMouseClicked(evt);
            }
        });
        moveForwardItem.addActionListener((evt) -> {
            moveForwardItemActionPerformed(evt);
        });
        gameMenu.add(moveForwardItem);

        rewindToBegin.setAccelerator(
            KeyStroke.getKeyStroke(
                KeyEvent.VK_Z, InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK
            )
        );
        rewindToBegin.setText(resourceMap.getString("rewindToBegin.text")); // NOI18N
        rewindToBegin.setName("rewindToBegin"); // NOI18N
        rewindToBegin.addActionListener((evt) -> {
            rewindToBeginActionPerformed(evt);
        });
        gameMenu.add(rewindToBegin);

        rewindToEnd.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK)
        );
        rewindToEnd.setText(resourceMap.getString("rewindToEnd.text")); // NOI18N
        rewindToEnd.setName("rewindToEnd"); // NOI18N
        rewindToEnd.addActionListener((evt) -> {
            rewindToEndActionPerformed(evt);
        });
        gameMenu.add(rewindToEnd);

        menuBar.add(gameMenu);

        optionsMenu.setText(resourceMap.getString("optionsMenu.text")); // NOI18N
        optionsMenu.setName("optionsMenu"); // NOI18N

        themeSettingsMenu.setText(resourceMap.getString("themeSettingsMenu.text")); // NOI18N
        themeSettingsMenu.setName("themeSettingsMenu"); // NOI18N
        optionsMenu.add(themeSettingsMenu);
        themeSettingsMenu.addActionListener(this);

        menuBar.add(optionsMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        
        
        JMenuItem donateMenuItem = initDonateMenuItem(resourceMap);
        helpMenu.add(aboutMenuItem);
        
        menuBar.add(helpMenu);
        menuBar.add(donateMenuItem);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        initStatusAnimationLabel();

        progressBar.setName("progressBar"); // NOI18N

        GroupLayout statusPanelLayout = initStatusPanelLayout(statusPanelSeparator);
        statusPanel.setLayout(statusPanelLayout);

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }

    private JMenuItem initDonateMenuItem(ResourceMap resourceMap)
    {
        JMenuItem donateMenuItem = new JMenuItem();
        donateMenuItem.setText(resourceMap.getString("donateMenu.text")); // NOI18N
        donateMenuItem.setName("donateMenu"); // NOI18N  
        donateMenuItem.addActionListener((event) -> {
            showDonateWindow();
        });
        return donateMenuItem;
    }

    private void initStatusAnimationLabel()
    {
        statusAnimationLabel = new JLabel();
        statusAnimationLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N
    }

    private GroupLayout initStatusPanelLayout(JSeparator statusPanelSeparator)
    {
        GroupLayout statusPanelLayout = new GroupLayout(statusPanel);
        statusPanelLayout.setHorizontalGroup(
                statusPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(statusPanelSeparator, GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
                        .addGroup(statusPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(statusMessageLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 616, Short.MAX_VALUE)
                                .addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(statusAnimationLabel)
                                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
                statusPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(statusPanelLayout.createSequentialGroup()
                                .addComponent(statusPanelSeparator, GroupLayout.PREFERRED_SIZE, 2, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(statusPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(statusMessageLabel)
                                        .addComponent(statusAnimationLabel)
                                        .addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(3, 3, 3))
        );
        return statusPanelLayout;
    }

    private void initNewGameItem(ResourceMap resourceMap, JMenu fileMenu)
    {
        newGameItem = new JMenuItem();
        newGameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        newGameItem.setText(resourceMap.getString("newGameItem.text")); // NOI18N
        newGameItem.setName("newGameItem"); // NOI18N
        fileMenu.add(newGameItem);
        newGameItem.addActionListener(this);
    }

    private void moveBackItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_moveBackItemActionPerformed
    {//GEN-HEADEREND:event_moveBackItemActionPerformed
        if(getGui() != null && getGui().getGame() != null)
        {
            getGui().getGame().undo();
        }
        else
        {
            try 
            {
                Game activeGame = this.getActiveTabGame();
                if(!activeGame.undo())
                {
                    JOptionPane.showMessageDialog(
                        null,
                        Settings.lang("noMoreUndoMovesInMemory")
                    );
                }
            } 
            catch(ArrayIndexOutOfBoundsException exc)
            {
                JOptionPane.showMessageDialog(
                    null,
                    Settings.lang("activeTabDoesNotExists")
                );
            }
            catch(UnsupportedOperationException exc)
            {
                JOptionPane.showMessageDialog(null , exc.getMessage());
            }
        }

    }//GEN-LAST:event_moveBackItemActionPerformed

    private void moveBackItemMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_moveBackItemMouseClicked
    {//GEN-HEADEREND:event_moveBackItemMouseClicked
        // TODO add your handling code here:
       
    }//GEN-LAST:event_moveBackItemMouseClicked

    private void moveForwardItemMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_moveForwardItemMouseClicked
    {//GEN-HEADEREND:event_moveForwardItemMouseClicked
        // TODO add your handling code here:
             
    }//GEN-LAST:event_moveForwardItemMouseClicked

    private void moveForwardItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_moveForwardItemActionPerformed
    {//GEN-HEADEREND:event_moveForwardItemActionPerformed
        // TODO add your handling code here:
        if (getGui() != null && getGui().getGame() != null)
        {
            getGui().getGame().redo();
        }
        else
        {
            try
            {
                Game activeGame = this.getActiveTabGame();
                if(!activeGame.redo())
                {
                    JOptionPane.showMessageDialog(
                        null,
                        Settings.lang("noMoreRedoMovesInMemory")
                    );
                }
            } 
            catch (ArrayIndexOutOfBoundsException exc)
            {
                JOptionPane.showMessageDialog(
                    null,
                    Settings.lang("activeTabDoesNotExists")
                );
            }
            catch (UnsupportedOperationException exc)
            {
                JOptionPane.showMessageDialog(null , exc.getMessage());
            }
        }        
    }//GEN-LAST:event_moveForwardItemActionPerformed

    private void rewindToBeginActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_rewindToBeginActionPerformed
    {//GEN-HEADEREND:event_rewindToBeginActionPerformed
        try
        {
            Game activeGame = this.getActiveTabGame();
            if (!activeGame.rewindToBegin())
            {
                JOptionPane.showMessageDialog(
                    null,
                    Settings.lang("noMoreRedoMovesInMemory")
                );
            }
        }   
        catch (ArrayIndexOutOfBoundsException exc)
        {
            JOptionPane.showMessageDialog(
                null,
                Settings.lang("activeTabDoesNotExists")
            );
        }
        catch (UnsupportedOperationException exc)
        {
            JOptionPane.showMessageDialog(null , exc.getMessage());
        }
    }//GEN-LAST:event_rewindToBeginActionPerformed

    private void showDonateWindow()
    {
        if(Desktop.isDesktopSupported())
        {
            try 
            {
                ResourceMap resourceMap = Application.getInstance(JChessApp.class)
                        .getContext()
                        .getResourceMap(JChessApp.class);
                Desktop.getDesktop().browse(
                    new URI(resourceMap.getString("Application.donateUrl"))
                );
            } 
            catch (URISyntaxException | IOException ex)
            {
                LOG.error(ex.getMessage());
            }
        }
    }
    
    private void rewindToEndActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_rewindToEndActionPerformed
    {//GEN-HEADEREND:event_rewindToEndActionPerformed
        try
        {
            Game activeGame = this.getActiveTabGame();
            if (!activeGame.rewindToEnd())
            {
                JOptionPane.showMessageDialog(
                    null,
                    Settings.lang("noMoreUndoMovesInMemory")
                );
            }
        }   
        catch (ArrayIndexOutOfBoundsException exc)
        {
            JOptionPane.showMessageDialog(
                null,
                Settings.lang("activeTabDoesNotExists")
            );
        }
        catch (UnsupportedOperationException exc)
        {
            JOptionPane.showMessageDialog(null , exc.getMessage());
        }        
    }//GEN-LAST:event_rewindToEndActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu gameMenu;
    private javax.swing.JTabbedPane gamesPane;
    private javax.swing.JMenuItem loadGameItem;
    public javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem moveBackItem;
    private javax.swing.JMenuItem moveForwardItem;
    private javax.swing.JMenuItem newGameItem;
    private javax.swing.JMenu optionsMenu;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JMenuItem rewindToBegin;
    private javax.swing.JMenuItem rewindToEnd;
    private javax.swing.JMenuItem saveGameItem;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JMenuItem themeSettingsMenu;
    // End of variables declaration//GEN-END:variables
    //private JTabbedPaneWithIcon gamesPane;
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;
    private PawnPromotionWindow promotionBox;
    private JDialog  newGameFrame;

    @Override
    public void componentResized(ComponentEvent e) {
        LOG.debug("jchessView has been resized !");
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public Game getActiveTabGame() throws ArrayIndexOutOfBoundsException
    {
        Game activeGame = (Game)this.gamesPane.getComponentAt(this.gamesPane.getSelectedIndex());
        return activeGame;
    }
    
    public void setActiveTabGame(int index) throws ArrayIndexOutOfBoundsException
    {
        this.gamesPane.setSelectedIndex(index);
    }
    
    public void setLastTabAsActive()
    {
        this.gamesPane.setSelectedIndex(this.gamesPane.getTabCount() - 1);
    }
    
    public int getNumberOfOpenedTabs()
    {
        return this.gamesPane.getTabCount();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void componentShown(ComponentEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @return the newGameFrame
     */
    public JDialog getNewGameFrame()
    {
        return newGameFrame;
    }

    /**
     * @param newGameFrame the newGameFrame to set
     */
    public void setNewGameFrame(JDialog newGameFrame)
    {
        this.newGameFrame = newGameFrame;
    }
    
    public JTabbedPane getGamesPane()
    {
        return this.gamesPane;
    }
    
}
