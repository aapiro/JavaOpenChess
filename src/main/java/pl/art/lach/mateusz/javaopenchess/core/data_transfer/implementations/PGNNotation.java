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
package pl.art.lach.mateusz.javaopenchess.core.data_transfer.implementations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Calendar;
import org.apache.log4j.Logger;
import pl.art.lach.mateusz.javaopenchess.JChessApp;
import pl.art.lach.mateusz.javaopenchess.core.Game;
import pl.art.lach.mateusz.javaopenchess.core.GameFactory;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.DataExporter;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.DataImporter;
import pl.art.lach.mateusz.javaopenchess.core.players.PlayerType;
import pl.art.lach.mateusz.javaopenchess.utils.GameModes;
import pl.art.lach.mateusz.javaopenchess.utils.GameTypes;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;
import pl.art.lach.mateusz.javaopenchess.core.exceptions.ReadGameError;

/**
 *
 * @author Mateusz Lach (matlak, msl)
 */
public class PGNNotation implements DataImporter, DataExporter
{
    
    private static final Logger LOG = Logger.getLogger(Game.class);
    
    private static final String BLACK_COLOR_INTRO = "[Black";
    
    private static final String WHITE_COLOR_INTRO = "[White";    
    
    private static final String START_MOVES_LINE_INTRO = "1.";

    @Override
    public Game importData(String data) throws ReadGameError
    {
        String tempStr;
        String blackName;
        String whiteName;
        BufferedReader br = new BufferedReader(new StringReader(data));
        try
        {
            //TODO: REFACTOR
            tempStr = getLineWithVar(br, WHITE_COLOR_INTRO);
            whiteName = getValue(tempStr);
            tempStr = getLineWithVar(br, BLACK_COLOR_INTRO);
            blackName = getValue(tempStr);
            tempStr = getLineWithVar(br, START_MOVES_LINE_INTRO);
        }
        catch (ReadGameError err)
        {
            LOG.error("Error reading file: " + err);
            return null;
        }
        Game game = GameFactory.instance(
            GameModes.LOAD_GAME,
            GameTypes.LOCAL,
            whiteName,
            blackName,
            PlayerType.LOCAL_USER,
            PlayerType.LOCAL_USER,
            true,
            false
        );
        importData(tempStr, game);
        game.getChessboard().repaint();
        return game;
    }

    @Override
    public void importData(String data, Game game) throws ReadGameError
    {
        game.setBlockedChessboard(true);
        importData(new BufferedReader(new StringReader(data)), game);
        game.setBlockedChessboard(false);
    }
    
    private void importData(BufferedReader br, Game game) throws ReadGameError
    {
        game.getMoves().setMoves(getLineWithVar(br, START_MOVES_LINE_INTRO));
    }

    @Override
    public String exportData(Game game)
    {
        Calendar cal = Calendar.getInstance();
        Settings sett = game.getSettings();
        StringBuilder strBuilder = new StringBuilder();
        String header = String.format(
            "[Event \"Game\"]\n[Date \"%s.%s.%s\"]\n[White \"%s\"]\n[Black \"%s\"]\n\n",
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH),
            sett.getPlayerWhite().getName(), sett.getPlayerBlack().getName()
        );
        strBuilder.append(header);
        strBuilder.append(game.getMoves().getMovesInString());
        return strBuilder.toString();
    }

    /** Method checking in with of line there is an error
     *  @param  br BufferedReader class object to operate on
     *  @param  srcStr String class object with text which variable you want to get in file
     *  @return String with searched variable in file (whole line)
     *  @throws ReadGameError class object when something goes wrong when reading file
     */
    static private String getLineWithVar(BufferedReader br, String srcStr) throws ReadGameError
    {
        String str = new String();
        while (true)
        {
            try
            {
                str = br.readLine();
            }
            catch (IOException exc)
            {
                LOG.error("Something wrong reading file: ", exc);
                throw new ReadGameError("Something wrong reading file: " + exc);
            }
            if (str == null)
            {
                LOG.error("Something wrong reading file, str == null.");
                throw new ReadGameError("Something wrong reading file, str == null.");
            }
            if (str.startsWith(srcStr))
            {
                return str;
            }
        }
    }

    /** Method to get value from loaded txt line in PGN notation
     *  @param line Line which is readed
     *  @return result String with loaded value
     *  @throws ReadGameError object class when something goes wrong
     */
    static private String getValue(String line) throws ReadGameError
    {
        int from = line.indexOf("\"");
        int to = line.lastIndexOf("\"");
        int size = line.length() - 1;
        String result = "";
        if (to < from || from > size || to > size || to < 0 || from < 0)
        {
            throw new ReadGameError("Error reading value from PGN header section.");
        }
        try
        {
            result = line.substring(from + 1, to);
        }
        catch (StringIndexOutOfBoundsException exc)
        {
            LOG.error("error getting value: ", exc);
            return "none";
        }
        return result;
    }    
}