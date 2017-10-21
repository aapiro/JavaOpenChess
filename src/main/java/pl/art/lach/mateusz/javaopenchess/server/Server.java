/**
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.art.lach.mateusz.javaopenchess.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import pl.art.lach.mateusz.javaopenchess.utils.MD5;
import org.apache.log4j.Logger;

/**
 * @author : Mateusz  Lach ( matlak, msl )
 * @author : Damian Marciniak
 */
public class Server implements Runnable
{
    private static final Logger LOG = Logger.getLogger(Server.class);
    
    public static boolean isPrintEnable = true; //print all messages (print function)
    public static Map<Integer, Table> tables;
    public static int port = 4449;
    private static ServerSocket ss;
    private static boolean isRunning = false;

    public Server()
    {
        if (!Server.isRunning) //run server if isn't running previous
        {
            runServer();

            Thread thread = new Thread(this);
            thread.start();

            Server.isRunning = true;
        }
    }

    public static boolean isRunning()
    {
        //is server running?
        return isRunning;
    }

    private static void runServer()
    {
        //run server
        try
        {
            ss = new ServerSocket(port);
            print("running");
        }
        catch (IOException ex)
        {
            LOG.error("IOException: " + ex);
        }

        tables = new HashMap<Integer, Table>();
    }

    public void run() 
    {
        //listening
        print("listening port: " + port);
        while (true)
        {
            Socket s;
            ObjectInputStream input;
            ObjectOutputStream output;

            try
            {
                s = ss.accept();
                input = new ObjectInputStream(s.getInputStream());
                output = new ObjectOutputStream(s.getOutputStream());

                print("new connection");

                //readed all data
                int tableID = input.readInt();
                print("readed table ID: " + tableID);
                boolean joinAsPlayer = input.readBoolean();
                print("readed joinAsPlayer: " + joinAsPlayer);
                String nick = input.readUTF();
                print("readed nick: " + nick);
                String password = input.readUTF();
                print("readed password: " + password);
                //---------------

                if (!tables.containsKey(tableID))
                {
                    print("bad table ID");
                    output.writeInt(ConnectionInfo.ERR_WRONG_TABLE_ID.getValue());
                    output.flush();
                    continue;
                }
                Table table = tables.get(tableID);

                if (!MD5.encrypt(table.password).equals(password))
                {
                    print("bad password: " + pl.art.lach.mateusz.javaopenchess.utils.MD5.encrypt(table.password) + " != " + password);
                    output.writeInt(ConnectionInfo.ERR_INVALID_PASSWORD.getValue());
                    output.flush();
                    continue;
                }

                if (joinAsPlayer)
                {
                    print("join as player");
                    if (table.isAllPlayers())
                    {
                        print("error: was all players at this table");
                        output.writeInt(ConnectionInfo.ERR_TABLE_IS_FULL.getValue());
                        output.flush();
                        continue;
                    }
                    else
                    {
                        print("wasn't all players at this table");

                        output.writeInt(ConnectionInfo.EVERYTHING_IS_OK.getValue());
                        output.flush();

                        table.addPlayer(new SClient(s, input, output, nick, table));
                        table.sendMessageToAll("** Gracz " + nick + " dołączył do gry **");

                        if (table.isAllPlayers())
                        {
                            table.generateSettings();

                            print("Send settings to all");
                            table.sendSettingsToAll();

                            table.sendMessageToAll("** Nowa gra, zaczna: " + table.clientPlayer1.nick + "**");
                        }
                        else
                        {
                            table.sendMessageToAll("** Oczekiwanie na drugiego gracza **");
                        }
                    }
                }
                else //join as observer
                {

                    print("join as observer");
                    if (!table.canObserversJoin())
                    {
                        print("Observers can't join");
                        output.writeInt(ConnectionInfo.ERR_GAME_WITHOUT_OBSERVERS.getValue());
                        output.flush();
                        continue;
                    }
                    else
                    {
                        output.writeInt(ConnectionInfo.EVERYTHING_IS_OK.getValue());
                        output.flush();

                        table.addObserver(new SClient(s, input, output, nick, table));

                        if (table.clientPlayer2 != null) //all players is playing
                        {
                            table.sendSettingsAndMovesToNewObserver();
                        }

                        table.sendMessageToAll("** Obserwator " + nick + " dołączył do gry **");
                    }
                }
            }
            catch (IOException ex)
            {
                LOG.error("IOException: " + ex);
                continue;
            }
        }
    }

    public static void print(String str)
    {
        if (isPrintEnable)
        {
            LOG.debug("Server: " + str);
        }
    }

    public void newTable(int idTable, String password, boolean withObserver, boolean enableChat) //create new table
    {

        print("create new table - id: " + idTable);
        tables.put(idTable, new Table(password, withObserver, enableChat));
    }
}
