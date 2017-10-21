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

import pl.art.lach.mateusz.javaopenchess.core.players.Player;
import pl.art.lach.mateusz.javaopenchess.core.players.PlayerType;
import pl.art.lach.mateusz.javaopenchess.utils.GameModes;
import pl.art.lach.mateusz.javaopenchess.utils.GameTypes;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;

/**
 * GameFactory class.
 * 
 * @author: Mateusz  Lach ( matlak, msl )
 */
public class GameFactory
{
    public static Game instance(GameModes gameMode, GameTypes gameType,
            String whiteName, String blackName, PlayerType whiteType, PlayerType blackType, 
            boolean setPieces4newGame)
    {
        Game game = new Game();
        Settings sett = game.getSettings();//sett LOCAL settings variable
        Player whitePlayer = sett.getPlayerWhite();//set LOCAL player variable
        Player blackPlayer = sett.getPlayerBlack();//set LOCAL player variable
        sett.setGameMode(GameModes.NEW_GAME);
        blackPlayer.setName(whiteName);//set name of player
        whitePlayer.setName(blackName);//set name of player
        whitePlayer.setType(whiteType);//set type of player
        blackPlayer.setType(blackType);//set type of player
        sett.setGameType(GameTypes.LOCAL);
        if (setPieces4newGame)
        {
            game.getChessboard().setPieces4NewGame(whitePlayer, blackPlayer);
        }
        game.setActivePlayer(whitePlayer);
        return game;
    }
    
    public static Game instance(GameModes gameMode, GameTypes gameType,
        String whiteName, String blackName, PlayerType whiteType, PlayerType blackType)
    {
        return instance(gameMode, gameType, whiteName, blackName, whiteType, blackType, true);
    }
    
    public static Game instance(GameModes gameMode, GameTypes gameType,
            String whiteName, String blackName,
            PlayerType whiteType, PlayerType blackType, boolean setPieces4newGame, boolean chatEnabled)
    {
        Game game = instance(gameMode, gameType, whiteName, blackName, whiteType, blackType);
        game.getChat().setEnabled(chatEnabled);
        return game;
    }
    
    public static Game instance(GameModes gameMode, GameTypes gameType,
            Player whitePlayer, Player blackPlayer)
    {
        return instance(gameMode,
            gameType,
            whitePlayer.getName(), 
            blackPlayer.getName(),
            whitePlayer.getPlayerType(),
            blackPlayer.getPlayerType()
        );
    }
}
