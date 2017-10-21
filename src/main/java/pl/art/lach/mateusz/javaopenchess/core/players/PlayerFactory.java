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
package pl.art.lach.mateusz.javaopenchess.core.players;

import pl.art.lach.mateusz.javaopenchess.core.Colors;
import pl.art.lach.mateusz.javaopenchess.core.players.implementation.ComputerPlayer;
import pl.art.lach.mateusz.javaopenchess.core.players.implementation.HumanPlayer;
import pl.art.lach.mateusz.javaopenchess.core.players.implementation.NetworkPlayer;

/**
 *
 * @author Mateusz  Lach (matlak, msl)
 */
public class PlayerFactory
{
  public static final String COMPUTER_NAME = "CPU";
  
  public static Player getInstance(String name, Colors color, PlayerType playerType)
  {
    Player player = null;
    switch (playerType)
    {
        case LOCAL_USER:
            player = new HumanPlayer(name, color);
            break;
        case NETWORK_USER:
            player = new NetworkPlayer(name, color);
            break;
        case COMPUTER:
            player = new ComputerPlayer(name, color);
            player.setName(COMPUTER_NAME);
            break;
        default:
            player = new HumanPlayer(name, color);
            break;
    }
    return player;
  }
  
  public static Player getInstance(String name, String color, PlayerType playerType)
  {
    return getInstance(name, Colors.valueOf(color.toUpperCase()), playerType);
  }
}
