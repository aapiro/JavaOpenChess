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

/**
 * Squares Enum that represents square values for easier access.
 * @author: Mateusz  Lach ( matlak, msl )
 */
public enum Squares
{
    SQ_A(0),
    
    SQ_B(1),
    
    SQ_C(2),
    
    SQ_D(3),
    
    SQ_E(4),
    
    SQ_F(5),
    
    SQ_G(6),
    
    SQ_H(7),
    
    SQ_1(7),
    
    SQ_2(6),
    
    SQ_3(5),
    
    SQ_4(4),
    
    SQ_5(3),
    
    SQ_6(2),
    
    SQ_7(1),
    
    SQ_8(0);
    
    private int value;
    
    Squares(int value)
    {
        this.value = value;
    }

    /**
     * @return the value
     */
    public int getValue()
    {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(int value)
    {
        this.value = value;
    }
}
