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
package pl.art.lach.mateusz.javaopenchess.core.exceptions;

/**
 * @author Mateusz  Lach (matlak, msl)
 */
public class ReadGameError extends Exception
{
    private String message;
    
    private String move;
    
    public ReadGameError(String message)
    {
        this.message = message;
    }  
    
    public ReadGameError(String message, String move)
    {
        this(message);
        this.move = move;
    }

    /**
     * @return the message
     */
    @Override
    public String getMessage()
    {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * @return the move
     */
    public String getMove()
    {
        return move;
    }

    /**
     * @param move the move to set
     */
    public void setMove(String move)
    {
        this.move = move;
    }
}