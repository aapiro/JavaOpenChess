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
package pl.art.lach.mateusz.javaopenchess.core.data_transfer;

import pl.art.lach.mateusz.javaopenchess.core.data_transfer.implementations.FenNotation;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.implementations.PGNNotation;

/**
 * Factory to create exporter/importer instances.
 * @author Mateusz  Lach (matlak, msl)
 */
public class DataTransferFactory
{
    
    public static DataExporter getExporterInstance(TransferFormat format)
    {
        switch (format)
        {
            case FEN:
                return new FenNotation();
            case PGN:
                return new PGNNotation();
            default:
                return new FenNotation();
        }
    }
    
    public static DataImporter getImporterInstance(TransferFormat format)
    {
        switch (format)
        {
            case FEN:
                return new FenNotation();
            case PGN:
                return new PGNNotation();
            default:
                return new FenNotation();
        }
    }
}
