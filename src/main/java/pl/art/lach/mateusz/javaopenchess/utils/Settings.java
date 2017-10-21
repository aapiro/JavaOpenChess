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
package pl.art.lach.mateusz.javaopenchess.utils;

import pl.art.lach.mateusz.javaopenchess.core.players.implementation.HumanPlayer;
import java.io.Serializable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import org.apache.log4j.Logger;
import pl.art.lach.mateusz.javaopenchess.JChessApp;
import pl.art.lach.mateusz.javaopenchess.core.Colors;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;
import pl.art.lach.mateusz.javaopenchess.core.players.PlayerType;

/** 
 * Class representings game settings available for the current player
 * @author Mateusz  Lach (matlak, msl)
 * @author Damian Marciniak
 */
public class Settings implements Serializable
{
    private static final Logger LOG = Logger.getLogger(Settings.class);
    
    private static final String RESOURCES_I18N_MAIN = ".resources.i18n.main";
    
    private static ResourceBundle loc = null;
    
    protected int timeForGame;
    
    protected boolean runningChat;
    
    protected boolean runningGameClock;
    
    /**
     * tell us if player choose time 4 game or it's infinity
     */
    protected boolean timeLimitSet = false;
    
    protected boolean upsideDown;
    
    protected boolean displayLegalMovesEnabled = true;
    
    protected GameModes gameMode;
    
    protected Player playerWhite;
    
    protected Player playerBlack;

    protected GameTypes gameType;
    
    protected boolean renderLabels = true;

    public Settings()
    {
        this(
            new HumanPlayer("", Colors.WHITE.getColorName()),
            new HumanPlayer("", Colors.BLACK.getColorName())
        );
    }
    
    public Settings(Player playerWhite, Player playerBlack)
    {
        this.playerWhite = playerWhite;
        this.playerBlack = playerBlack;
        this.timeLimitSet = false;

        gameMode = GameModes.NEW_GAME;
    }

    /**
     * @return the runningChat
     */
    public boolean isRunningChat()
    {
        return runningChat;
    }

    /**
     * @return the runningGameClock
     */
    public boolean isRunningGameClock()
    {
        return runningGameClock;
    }

    /**
     * @return the timeLimitSet
     */
    public boolean isTimeLimitSet()
    {
        return timeLimitSet;
    }
   
    public boolean isUpsideDown()
    {
        return upsideDown;
    }

    /**
     * @return the gameMode
     */
    public GameModes getGameMode()
    {
        return gameMode;
    }

    /**
     * @return the playerWhite
     */
    public Player getPlayerWhite()
    {
        return playerWhite;
    }

    /**
     * @return the playerBlack
     */
    public Player getPlayerBlack()
    {
        return playerBlack;
    }
    
    public void setPlayerWhite(Player player)
    {
        this.playerWhite = player;
    }
    
    public void setPlayerBlack(Player player)
    {
        this.playerBlack = player;
    }

    /**
     * @return the gameType
     */
    public GameTypes getGameType()
    {
        return gameType;
    }

    /**
     * @return the renderLabels
     */
    public boolean isRenderLabels()
    {
        return renderLabels;
    }
     
    public void setRenderLabels(boolean renderLabels)
    {
        this.renderLabels = renderLabels;
    }

    /**
     * @param upsideDown the upsideDown to set
     */
    public void setUpsideDown(boolean upsideDown)
    {
        this.upsideDown = upsideDown;
    }

    /**
     * @param gameMode the gameMode to set
     */
    public void setGameMode(GameModes gameMode)
    {
        this.gameMode = gameMode;
    }

    /**
     * @param gameType the gameType to set
     */
    public void setGameType(GameTypes gameType)
    {
        this.gameType = gameType;
    }

    /**
     * @param timeForGame the timeForGame to set
     */
    public void setTimeForGame(int timeForGame)
    {
        this.timeLimitSet = true;
        this.timeForGame = timeForGame;
    }

    /**
     * @return the isDisplayLegalMovesEnabled
     */
    public boolean isDisplayLegalMovesEnabled()
    {
        return displayLegalMovesEnabled;
    }

    /**
     * @param displayLegalMovesEnabled the isDisplayLegalMovesEnabled to set
     */
    public void setDisplayLegalMovesEnabled(boolean displayLegalMovesEnabled)
    {
        this.displayLegalMovesEnabled = displayLegalMovesEnabled;
    }

    /**
     *  Method to get game time set by player
     *  @return timeFofGame int with how long the game will leasts
     */
    public int getTimeForGame()
    {
        return this.timeForGame;
    }
    
    public boolean isGameAgainstComputer()
    {
        return playerBlack.getPlayerType() == PlayerType.COMPUTER
                || playerWhite.getPlayerType() == PlayerType.COMPUTER;
    }

    public static String lang(String key)
    {
        if (Settings.loc == null)
        {
            Settings.loc = PropertyResourceBundle.getBundle(JChessApp.MAIN_PACKAGE_NAME + RESOURCES_I18N_MAIN);
            Locale.setDefault(Locale.ENGLISH);
        }
        String result = "";
        try
        {
            result = Settings.loc.getString(key);
        }
        catch (MissingResourceException exc)
        {
            result = key;
        }
        LOG.debug("Locale: " + Settings.loc.getLocale().toString());
        return result;
    }
}
