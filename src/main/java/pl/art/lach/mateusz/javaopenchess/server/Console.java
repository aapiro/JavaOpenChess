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

import java.io.IOException;
import java.util.Map;
import pl.art.lach.mateusz.javaopenchess.utils.MD5;
import org.apache.log4j.Logger;

/**
 * @author: Mateusz  Lach ( matlak, msl )
 * @author: Damian Marciniak
 */
public class Console
{
    private static final Logger LOG = Logger.getLogger(Console.class);

    public static void main(String[] args)
    {
        System.out.println("JChess Server Start!");

        Server server = new Server(); //create server
        server.isPrintEnable = false;

        boolean isOK = true;
        while (isOK)
        {
            System.out.println("--------------------");
            System.out.println("[1] New table");
            System.out.println("[2] List of active tables");
            System.out.println("[3] Turn on/off server messages");
            System.out.println("[4] Turn off server");
            System.out.print("-> ");
            String str = readString();

            if (str.equals("1")) //new table
            {
                System.out.print("ID of game: ");
                int gameID = Integer.parseInt(readString());

                System.out.print("Password: ");
                String pass = MD5.encrypt(readString());

                String observer;
                do
                {
                    System.out.print("Game with observers?[t/n] (t=YES, n=NO): ");
                    observer = readString();
                }
                while (!observer.equalsIgnoreCase("t") && !observer.equalsIgnoreCase("n"));

                boolean canObserver = observer.equalsIgnoreCase("t");

                server.newTable(gameID, pass, canObserver, true); //create new table
            }
            else if (str.equals("2")) //list of tables
            {
                for (Map.Entry<Integer, Table> entry : server.tables.entrySet())
                {
                    Integer id = entry.getKey();
                    Table table = entry.getValue();

                    String p1, p2;

                    if (table.clientPlayer1 == null || table.clientPlayer1.nick == null)
                    {
                        p1 = "empty";
                    }
                    else
                    {
                        p1 = table.clientPlayer1.nick;
                    }

                    if (table.clientPlayer2 == null || table.clientPlayer2.nick == null)
                    {
                        p2 = "empty";
                    }
                    else
                    {
                        p2 = table.clientPlayer2.nick;
                    }

                    System.out.println("\t" + id + ": " + p1 + " vs " + p2);
                }
            }
            else if (str.equals("3")) //on/off server's communicats
            {
                if (!Server.isPrintEnable)
                {
                    Server.isPrintEnable = true;
                    System.out.println("Messages of server has been turned on");
                }
                else
                {
                    Server.isPrintEnable = false;
                    System.out.println("Messages of server has been turned off");
                }
            }
            else if (str.equals("4")) //exit
            {
                isOK = false;
            }
            else //bad commant
            {
                System.out.println("Unrecognized command");
            }
        }
        System.exit(0);
    }

    public static String readString() //read string from console
    {
        int ch;
        StringBuilder sb = new StringBuilder();
        try
        {
            while ((ch = System.in.read()) != 10)
            {
                sb.append((char) ch);
            }
        }
        catch (IOException ex)
        {
            LOG.error("readString()/IOException: " + ex);
        }

        return sb.toString();
    }
}
