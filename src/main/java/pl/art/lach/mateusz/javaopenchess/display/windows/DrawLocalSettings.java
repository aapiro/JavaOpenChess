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
import pl.art.lach.mateusz.javaopenchess.core.Game;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.TextListener;
import java.awt.event.TextEvent;
import java.awt.*;
import javax.swing.text.BadLocationException;
import pl.art.lach.mateusz.javaopenchess.core.Colors;
import pl.art.lach.mateusz.javaopenchess.core.ai.AIFactory;
import pl.art.lach.mateusz.javaopenchess.core.ai.joc_ai.*;
import pl.art.lach.mateusz.javaopenchess.core.players.PlayerFactory;
import pl.art.lach.mateusz.javaopenchess.core.players.PlayerType;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;
import org.apache.log4j.Logger;
import pl.art.lach.mateusz.javaopenchess.utils.GameModes;
import pl.art.lach.mateusz.javaopenchess.utils.GameTypes;

/**
 * Class responsible for drawing the fold with LOCAL game settings
 * @author: Mateusz  Lach ( matlak, msl )
 */
public class DrawLocalSettings extends JPanel implements ActionListener, TextListener
{

    private static final Logger LOG = Logger.getLogger(DrawLocalSettings.class);
    
    JDialog parent;//needet to close NEW_GAME window
    JComboBox color;//to choose color of player
    JRadioButton oponentComp;//choose oponent
    JRadioButton oponentHuman;//choose oponent (human)
    ButtonGroup oponentChoos;//group 4 radio buttons
    JFrame localPanel;
    JLabel compLevLab;
    JSlider computerLevel;//slider to choose jChess Engine level
    JTextField firstName;//editable field 4 nickname
    JTextField secondName;//editable field 4 nickname
    JLabel firstNameLab;
    JLabel secondNameLab;
    JCheckBox upsideDown;//if true draw chessboard upsideDown(white on top)
    GridBagLayout gbl;
    GridBagConstraints gbc;
    Container cont;
    JSeparator sep;
    JButton okButton;
    JCheckBox timeGame;
    JComboBox time4Game;
    String colors[] =
    {
        Settings.lang("white"), Settings.lang("black")
    };
    
    String times[] =
    {
        "1", "3", "5", "8", "10", "15", "20", "25", "30", "60", "120"
    };
        
    /** 
     * Method witch is checking correction of edit tables
     * @param e Object where is saving this what contents edit tables
     */
    @Override
    public void textValueChanged(TextEvent e)
    {
        Object target = e.getSource();
        if (target == this.firstName || target == this.secondName)
        {
            JTextField temp = new JTextField();
            if (target == this.firstName)
            {
                temp = this.firstName;
            }
            else if (target == this.secondName)
            {
                temp = this.secondName;
            }

            int len = temp.getText().length();
            if (len > 8)
            {
                try
                {
                    temp.setText(temp.getText(0, 7));
                }
                catch (BadLocationException exc)
                {
                    LOG.error("BadLocationException: Something wrong in editables, msg: " + exc.getMessage() + " object: ", exc);
                }
            }
        }
    }

    /** Method responsible for changing the options which can make a player
 when he want to start new LOCAL game
     * @param e where is saving data of performed action
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object target = e.getSource(); 
        if (target == this.oponentComp) //toggle enabled of controls depends of oponent (if COMPUTER)
        {
            this.computerLevel.setEnabled(true);//enable level of COMPUTER abilities
            this.secondName.setEnabled(false);//disable field with name of player2
        }
        else if (target == this.oponentHuman) //else if oponent will be HUMAN
        {
            //this.computerLevel.setEnabled(false);//disable level of COMPUTER abilities
            this.secondName.setEnabled(true);//enable field with name of player2
        }
        else if (target == this.okButton) //if clicked OK button (on finish)
        {
            if (this.firstName.getText().length() > 9) //make names short to 10 digits
            {
                this.firstName.setText(this.trimString(firstName, 9));
            }
            if (this.secondName.getText().length() > 9)  //make names short to 10 digits
            {
                this.secondName.setText(this.trimString(secondName, 9));
            }
            if (!this.oponentComp.isSelected()
                    && (this.firstName.getText().length() == 0 || this.secondName.getText().length() == 0))
            {
                JOptionPane.showMessageDialog(this, Settings.lang("fill_names"));
                return;
            }
            if ((this.oponentComp.isSelected() && this.firstName.getText().length() == 0))
            {
                JOptionPane.showMessageDialog(this, Settings.lang("fill_name"));
                return;
            }
            String playerFirstName = this.firstName.getText();
            String playerSecondName= this.secondName.getText();

            String whiteName;
            String blackName;
            PlayerType whiteType;
            PlayerType blackType;
            if (0 == this.color.getSelectedIndex())
            {
                whiteName = playerFirstName;
                blackName = playerSecondName;
                whiteType = PlayerType.LOCAL_USER;
                blackType = (this.oponentComp.isSelected()) ? PlayerType.COMPUTER :  PlayerType.LOCAL_USER;
            }
            else
            {
                blackName = playerFirstName;
                whiteName = playerSecondName;
                blackType = PlayerType.LOCAL_USER;
                whiteType = (this.oponentComp.isSelected()) ? PlayerType.COMPUTER :  PlayerType.LOCAL_USER;
            }
            Player playerWhite = PlayerFactory.getInstance(whiteName, Colors.WHITE, whiteType);//sett.getPlayerWhite();//set LOCAL player variable
            Player playerBlack = PlayerFactory.getInstance(blackName, Colors.BLACK, blackType);
            Game newGUI = JChessApp.getJavaChessView().addNewTab(playerWhite.getName() + " vs " + playerBlack.getName());
            newGUI.getChat().setEnabled(false);
            Settings sett = newGUI.getSettings();//sett LOCAL settings variable            
            sett.setPlayerWhite(playerWhite);
            sett.setPlayerBlack(playerBlack);
            sett.setGameMode(GameModes.NEW_GAME);
            sett.setGameType(GameTypes.LOCAL); 
            sett.setUpsideDown(this.upsideDown.isSelected());
            newGUI.setActivePlayer(playerWhite);
            if (this.timeGame.isSelected()) //if timeGame is checked
            {
                String value = this.times[this.time4Game.getSelectedIndex()];//set time for game
                Integer val = new Integer(value);
                sett.setTimeForGame((int) val * 60);//set time for game and mult it to seconds
                newGUI.getGameClock().setTimes(sett.getTimeForGame(), sett.getTimeForGame());
                newGUI.getGameClock().start();
            }
            LOG.debug("this.time4Game.getActionCommand(): " + this.time4Game.getActionCommand());

            LOG.debug("****************\nStarting new game: " + playerWhite.getName() + " vs. " + playerBlack.getName()
                    + "\ntime 4 game: " + sett.getTimeForGame() + "\ntime limit set: " + sett.isTimeLimitSet()
                    + "\nwhite on top?: " + sett.isUpsideDown() + "\n****************");//4test
            
            newGUI.newGame();//start new Game
            this.parent.setVisible(false);//hide parent
             
            JChessApp.getJavaChessView().getActiveTabGame().repaint();
            JChessApp.getJavaChessView().setActiveTabGame(JChessApp.getJavaChessView().getNumberOfOpenedTabs()-1);
            if (this.oponentComp.isSelected())
            {
                Game activeGame = JChessApp.getJavaChessView().getActiveTabGame();
                activeGame.setAi(AIFactory.getAI(this.computerLevel.getValue()));
                if (activeGame.getSettings().isGameAgainstComputer()
                        && activeGame.getSettings().getPlayerWhite().getPlayerType() == PlayerType.COMPUTER)
                {
                    activeGame.doComputerMove();
                }
            }
        }
    }

    public DrawLocalSettings(JDialog parent)
    {
        super();
        //this.setA//choose oponent
        this.parent = parent;
        this.color = new JComboBox(colors);
        this.gbl = new GridBagLayout();
        this.gbc = new GridBagConstraints();
        this.sep = new JSeparator();
        this.okButton = new JButton(Settings.lang("ok"));
        this.compLevLab = new JLabel(Settings.lang("computer_level"));

        this.firstName = new JTextField("", 10);
        this.firstName.setSize(new Dimension(200, 50));
        this.secondName = new JTextField("", 10);
        this.secondName.setSize(new Dimension(200, 50));
        this.firstNameLab = new JLabel(Settings.lang("first_player_name") + ": ");
        this.secondNameLab = new JLabel(Settings.lang("second_player_name") + ": ");
        this.oponentChoos = new ButtonGroup();
        this.computerLevel = new JSlider();
        this.upsideDown = new JCheckBox(Settings.lang("upside_down"));
        this.timeGame = new JCheckBox(Settings.lang("time_game_min"));
        this.time4Game = new JComboBox(times);

        this.oponentComp = new JRadioButton(Settings.lang("against_computer"), false);
        this.oponentHuman = new JRadioButton(Settings.lang("against_other_human"), true);

        this.setLayout(gbl);
        this.oponentComp.addActionListener(this);
        this.oponentHuman.addActionListener(this);
        this.okButton.addActionListener(this);

        this.secondName.addActionListener(this);

        this.oponentChoos.add(oponentComp);
        this.oponentChoos.add(oponentHuman);
        this.computerLevel.setEnabled(false);
        this.computerLevel.setValue(1);
        this.computerLevel.setMaximum(2);
        this.computerLevel.setMinimum(1);
        this.computerLevel.setPaintTicks(true);
        this.computerLevel.setPaintLabels(true);
        this.computerLevel.setMajorTickSpacing(1);
        this.computerLevel.setMinorTickSpacing(1);

        this.gbc.gridx = 0;
        this.gbc.gridy = 0;
        this.gbc.insets = new Insets(3, 3, 3, 3);
        this.gbl.setConstraints(oponentComp, gbc);
        this.add(oponentComp);
        this.gbc.gridx = 1;
        this.gbl.setConstraints(oponentHuman, gbc);
        this.add(oponentHuman);
        this.gbc.gridx = 0;
        this.gbc.gridy = 1;
        this.gbl.setConstraints(firstNameLab, gbc);
        this.add(firstNameLab);
        this.gbc.gridx = 0;
        this.gbc.gridy = 2;
        this.gbl.setConstraints(firstName, gbc);
        this.add(firstName);
        this.gbc.gridx = 1;
        this.gbc.gridy = 2;
        this.gbl.setConstraints(color, gbc);
        this.add(color);
        this.gbc.gridx = 0;
        this.gbc.gridy = 3;
        this.gbl.setConstraints(secondNameLab, gbc);
        this.add(secondNameLab);
        this.gbc.gridy = 4;
        this.gbl.setConstraints(secondName, gbc);
        this.add(secondName);
        this.gbc.gridy = 5;
        this.gbc.insets = new Insets(0, 0, 0, 0);
        this.gbl.setConstraints(compLevLab, gbc);
        this.add(compLevLab);
        this.gbc.gridy = 6;
        this.gbl.setConstraints(computerLevel, gbc);
        this.add(computerLevel);
        this.gbc.gridy = 7;
        this.gbl.setConstraints(upsideDown, gbc);
        this.add(upsideDown);
        this.gbc.gridy = 8;
        this.gbc.gridwidth = 1;
        this.gbl.setConstraints(timeGame, gbc);
        this.add(timeGame);
        this.gbc.gridx = 1;
        this.gbc.gridy = 8;
        this.gbc.gridwidth = 1;
        this.gbl.setConstraints(time4Game, gbc);
        this.add(time4Game);
        this.gbc.gridx = 1;
        this.gbc.gridy = 9;
        this.gbc.gridwidth = 0;
        this.gbl.setConstraints(okButton, gbc);
        this.add(okButton);
        //this.oponentComp.setEnabled(false);//for now, becouse not implemented!

    }

    /**
     * Method responsible for triming white symbols from strings
     * @param txt Where is capt value to equal
     * @param length How long is the string
     * @return result trimmed String
     */
    public String trimString(JTextField txt, int length)
    {
        String result = new String();
        try
        {
            result = txt.getText(0, length);
        }
        catch (BadLocationException exc)
        {
            LOG.error("BadLocationException: Something wrong in trimString: \n", exc);
        }
        return result;
    }
}