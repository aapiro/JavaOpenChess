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
package pl.art.lach.mateusz.javaopenchess.server;

/**
 * @author Mateusz  Lach ( matlak, msl )
 * @author Damian Marciniak
 */
public enum ConnectionInfo
{

    EVERYTHING_IS_OK(0),
    
    ERR_WRONG_TABLE_ID(1),
    
    ERR_TABLE_IS_FULL(2),
    
    ERR_GAME_WITHOUT_OBSERVERS(3),
    
    ERR_INVALID_PASSWORD(4);
    
    private int value;

    ConnectionInfo(int value)
    {
        this.value = value;
    }

    public static ConnectionInfo get(int id)
    {
        switch (id)
        {
            case 0:
                return ConnectionInfo.EVERYTHING_IS_OK;
            case 1:
                return ConnectionInfo.ERR_WRONG_TABLE_ID;
            case 2:
                return ConnectionInfo.ERR_TABLE_IS_FULL;
            case 3:
                return ConnectionInfo.ERR_GAME_WITHOUT_OBSERVERS;
            case 4:
                return ConnectionInfo.ERR_INVALID_PASSWORD;
            default:
                return null;
        }
    }

    public int getValue()
    {
        return value;
    }
}
