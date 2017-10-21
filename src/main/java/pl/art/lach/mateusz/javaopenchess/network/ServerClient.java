/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.art.lach.mateusz.javaopenchess.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.log4j.Logger;

/**
 * 
 * @author Mateusz  Lach ( matlak, msl )
 */

public class ServerClient implements Runnable //connecting client
{
    private static final Logger LOG = Logger.getLogger(ServerClient.class);
    
    public ObjectInputStream input;

    public ObjectOutputStream output;

    private String nick;

    private Table table;

    ServerClient(ObjectInputStream input, ObjectOutputStream output, String nick, Table table)
    {
        this.input = input;
        this.output = output;
        this.nick = nick;
        this.table = table;

        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() //listening
    {
        Server.print("running function: run()");
        while(true)
        {
            try
            {
                String in = input.readUTF();

                if(Commands.MOVE_CMD.equals(in))//new move
                {
                    int bX = input.readInt();
                    int bY = input.readInt();
                    int eX = input.readInt();
                    int eY = input.readInt();
                    String promoted = input.readUTF();
                    table.sendMoveToOther(this, bX, bY, eX, eY, promoted);
                }
                if(Commands.MESSAGE_CMD.equals(in))//new message
                {
                    String str = input.readUTF();
                    table.sendMessageToAll(getNick() + ": " + str);
                }
            }
            catch (IOException ex)
            {
                LOG.error("private Client/IOException: ", ex);
            }
        }
    }

    /**
     * @return the nick
     */
    public String getNick()
    {
        return nick;
    }

    /**
     * @param nick the nick to set
     */
    public void setNick(String nick)
    {
        this.nick = nick;
    }
}
