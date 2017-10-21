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

package pl.art.lach.mateusz.javaopenchess.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.log4j.Logger;

/**
 * Class responsible for hashing the messages
 * 
 * @author : Mateusz  Lach ( matlak, msl )
 * @author : Damian Marciniak
 */
public class MD5
{
    private static final Logger LOG = Logger.getLogger(MD5.class);

    public static String encrypt(String str)
    {
        MessageDigest message;

        try
        {
            message = MessageDigest.getInstance("MD5");
            message.update(str.getBytes(), 0, str.length());
            return new BigInteger(1, message.digest()).toString(16);
        }
        catch (NoSuchAlgorithmException ex)
        {
            LOG.error("NoSuchAlgorithmException: " + ex);
            return null;
        }
    }
}
