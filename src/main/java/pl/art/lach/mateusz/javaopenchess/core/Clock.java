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

/** 
 *  Class to represent seperate wall-clock for one player.
 *  Full ChessClock is represented by GameClock object (two clock - one for each player)
 *  @author Mateusz  Lach (matlak, msl)
 *  @author Damian Marciniak
 */
public class Clock
{

    private int timeLeft;
    
    private Player player;

    /**
     * Default constructor
     */
    Clock()
    {
        this.init(timeLeft);
    }
    
    /**
     * Default constructor
     * @param time time to init clock
     */
    Clock(int time)
    {
        this.init(time);
    }

    /** Method to init clock with given value
     *  @param time tell method with how much time init clock
     */
    public final void init(int time)
    {
        this.timeLeft = time;
    }

    /** Method to decrement value of left time
     *  @return bool true if time_left > 0, else returns false
     */
    public boolean decrement()
    {
        if (this.timeLeft > 0)
        {
            this.timeLeft = this.timeLeft - 1;
            return true;
        }
        return false;
    }

    public void pause()
    {
    }

    /** Method to get left time in seconds
     *  @return Player int integer of seconds
     */
    public int getLeftTime()
    {
        return this.timeLeft;
    }

    /** Method to get player (owner of this clock)
     *  @param player  player to set as owner of clock
     */
    public void setPlayer(Player player)
    {
        this.player = player;
    }

    /** Method to get player (owner of this clock)
     *  @return  Reference to player class object
     */
    public Player getPlayer()
    {
        return this.player;
    }

    /** Method to prepare time in nice looking String
     *  @return  String of actual left game time with ':' digits in mm:ss format
     */
    public String prepareString()
    {
        String strMin = "";
        Integer time_min = new Integer(this.getLeftTime() / 60);
        Integer time_sec = new Integer(this.getLeftTime() % 60);
        if (time_min < 10) //prepare MINUTES
        {
            strMin = "0" + time_min.toString();
        }
        else
        {
            strMin = time_min.toString();
        }
        String result = strMin + ":";
        if (time_sec < 10) //prepare SECONDS
        { 
            result = result + "0" + time_sec.toString();
        }
        else
        {
            result = result + time_sec.toString();
        }

        return result;
    }
}
