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
package pl.art.lach.mateusz.javaopenchess.core.moves;

import javax.swing.table.DefaultTableModel;

/*
 * Overriding DefaultTableModel and  isCellEditable method
 * (history cannot be edited by player)
 * @author Mateusz  Lach (matlak, msl)
 */
class NotEditableTableModel extends DefaultTableModel
{

    NotEditableTableModel()
    {
        super();
    }

    @Override
    public boolean isCellEditable(int a, int b)
    {
        return false;
    }
}