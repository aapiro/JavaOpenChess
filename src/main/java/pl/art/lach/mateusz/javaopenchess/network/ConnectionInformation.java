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
package pl.art.lach.mateusz.javaopenchess.network;

/**
 * ConnectionInformation enum. 
 * @author Mateusz Lach (matlak, msl)
 */
public enum ConnectionInformation
{
    EVERYTHING_OK(0),
    
    ERROR_INVALID_TABLE_ID(1),
    
    ERROR_TABLE_IS_FULL(2),
    
    ERROR_OBSERVERS_NOT_ALLOWED(3),
    
    ERROR_INVALID_PASSWORD(4);

    private int value;

    ConnectionInformation(int value)
    {
        this.value = value;
    }

    public static ConnectionInformation get(int id)
    {
        switch(id)
        {
            case 0:
                return ConnectionInformation.EVERYTHING_OK;
            case 1:
                return ConnectionInformation.ERROR_INVALID_TABLE_ID;
            case 2:
                return ConnectionInformation.ERROR_TABLE_IS_FULL;
            case 3:
                return ConnectionInformation.ERROR_OBSERVERS_NOT_ALLOWED;
            case 4:
                return ConnectionInformation.ERROR_INVALID_PASSWORD;
            default:
                return null;
        }
    }

    public int getValue()
    {
        return value;
    }
}