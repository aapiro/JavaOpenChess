/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.art.lach.mateusz.javaopenchess.network;

import java.io.IOException;
import java.util.ArrayList;
import pl.art.lach.mateusz.javaopenchess.core.players.PlayerType;
import pl.art.lach.mateusz.javaopenchess.utils.GameModes;
import pl.art.lach.mateusz.javaopenchess.utils.GameTypes;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;
import pl.art.lach.mateusz.javaopenchess.utils.SettingsFactory;

/**
 *
 * @author Mateusz  Lach ( matlak, msl )
 */
public class Table //Table: {two player, one chessboard and x observers}
{
    private ServerClient clientPlayerWhite;

    private ServerClient clientPlayerBlack;

    private ArrayList<ServerClient> clientObservers;

    private Settings playerWhiteSettings;

    private Settings playerBlackSettings;

    private Settings observerSettings;

    private String password;

    private boolean canObserversJoin;

    private boolean enableChat;

    private ArrayList<Move> movesList;

    Table(String password, boolean canObserversJoin, boolean enableChat) 
    {
        this.password = password;
        this.enableChat = enableChat;
        this.canObserversJoin = canObserversJoin;

        if(canObserversJoin)
        {
            clientObservers = new ArrayList<>();
        }

        movesList = new ArrayList<>();
    }

    /** 
     * Helper method to generate settings
     * generate settings for both players and observers
     */
    public void generateSettings()
    {
        setPlayerWhiteSettings(SettingsFactory.getInstance(GameModes.NEW_GAME,
                GameTypes.NETWORK,
                getClientPlayerWhite().getNick(),
                getClientPlayerBlack().getNick(),
                PlayerType.LOCAL_USER,
                PlayerType.NETWORK_USER
        ));

        setPlayerBlackSettings(SettingsFactory.getInstance(GameModes.NEW_GAME,
                GameTypes.NETWORK,
                getClientPlayerWhite().getNick(),
                getClientPlayerBlack().getNick(),
                PlayerType.NETWORK_USER,
                PlayerType.LOCAL_USER
        ));

        if(canObserversJoin())
        {
            setObserverSettings(SettingsFactory.getInstance(GameModes.NEW_GAME,
                    GameTypes.NETWORK,
                    getClientPlayerWhite().getNick(),
                    getClientPlayerBlack().getNick(),
                    PlayerType.NETWORK_USER,
                    PlayerType.NETWORK_USER
            ));
        }
    }

    public void sendSettingsToAll() throws IOException //send generated settings to all clients on this table
    {
        Server.print("running function: sendSettingsToAll()");

        sendSettingsCommand(getClientPlayerWhite(), getPlayerWhiteSettings());
        sendSettingsCommand(getClientPlayerBlack(), getPlayerBlackSettings());

        if(canObserversJoin())
        {
            for(ServerClient observer: getClientObservers())
            {
                sendSettingsCommand(observer, getObserverSettings());
            }
        }
    }

    private void sendSettingsCommand(ServerClient client, Settings settings)
            throws IOException
    {
        client.output.writeUTF(Commands.SETTINGS);
        client.output.writeObject(settings);
        client.output.flush();
    }

    /** 
     * send all settings and moves to new observer
     * warning: used only if game started
     * @throws  IOException
     **/
    public void sendSettingsAndMovesToNewObserver()
            throws IOException
    {
        ServerClient observer = getClientObservers().get(getClientObservers().size()-1);

        observer.output.writeUTF(Commands.SETTINGS);
        observer.output.writeObject(getObserverSettings());
        observer.output.flush();

        for(Move m: movesList)
        {
            sendMoveCommand(observer, m.getbX(), m.getbY(), m.eX, m.eY, m.promoted);
        }
        observer.output.flush();
    }

    public void sendMoveToOther(ServerClient sender, int beginX, int beginY,
            int endX, int endY, String promoted)
            throws IOException
    {
        Server.print(String.format(
            "running function: sendMoveToOther(%s, %s, %s, %s, %s)",
            sender.getNick(), beginX, beginY, endX, endY
        ));

        if(isSenderOneOfPlayers(sender))
        {
            if(getClientPlayerWhite() != sender)
            {
                sendMoveCommand(getClientPlayerWhite(), beginX, beginY, endX, endY, promoted);
            }

            if(getClientPlayerBlack() != sender)
            {
                sendMoveCommand(getClientPlayerBlack(), beginX, beginY, endX, endY, promoted);
            }

            if(canObserversJoin())
            {
                for(ServerClient observer: getClientObservers())
                {
                    sendMoveCommand(observer, beginX, beginY, endX, endY, promoted);
                }
            }
            this.movesList.add(new Move(beginX, beginY, endX, endY, promoted));
        }
    }

    private boolean isSenderOneOfPlayers(ServerClient sender)
    {
        return sender == getClientPlayerWhite() || sender == getClientPlayerBlack();
    }

    private void sendMoveCommand(ServerClient client, int beginX, int beginY,
            int endX, int endY, String promoted)
            throws IOException
    {
        client.output.writeUTF(Commands.MOVE_CMD);
        client.output.writeInt(beginX);
        client.output.writeInt(beginY);
        client.output.writeInt(endX);
        client.output.writeInt(endY);
        client.output.writeUTF(promoted != null ? promoted : "");
        client.output.flush();
    }

    public void sendMessageToAll(String str) throws IOException
    {
        Server.print(String.format("running function: sendMessageToAll(%s)", str));

        if(getClientPlayerWhite() != null)
        {
            sendMessageCommand(getClientPlayerWhite(), str);
        }

        if(getClientPlayerBlack() != null)
        {
            sendMessageCommand(getClientPlayerBlack(), str);
        }

        if(canObserversJoin())
        {
            for(ServerClient observer: getClientObservers())
            {
                sendMessageCommand(observer, str);
            }
         }
    }

    private void sendMessageCommand(ServerClient client, String str)
            throws IOException
    {
        client.output.writeUTF(Commands.MESSAGE_CMD);
        client.output.writeUTF(str);
        client.output.flush();
    }

    public boolean isAllPlayers()
    {
        return getClientPlayerWhite() == null || getClientPlayerBlack() == null;
    }

    public boolean isObservers()
    {
        return !clientObservers.isEmpty();
    }

    public boolean canObserversJoin()
    {
        return this.canObserversJoin;
    }

    public void addPlayer(ServerClient client) //join player to game
    {
        if(getClientPlayerWhite() == null)
        {
            setClientPlayerWhite(client);
            Server.print("Player1 connected");
        }
        else if(getClientPlayerBlack() == null)
        {
            setClientPlayerBlack(client);
            Server.print("Player2 connected");
        }
    }

    public void addObserver(ServerClient client) //join observer to game
    {
        getClientObservers().add(client);
    }

    /**
     * @return the clientPlayerWhite
     */
    public ServerClient getClientPlayerWhite()
    {
        return clientPlayerWhite;
    }

    /**
     * @param clientPlayerWhite the clientPlayerWhite to set
     */
    public void setClientPlayerWhite(ServerClient clientPlayerWhite)
    {
        this.clientPlayerWhite = clientPlayerWhite;
    }

    /**
     * @return the clientPlayerBlack
     */
    public ServerClient getClientPlayerBlack()
    {
        return clientPlayerBlack;
    }

    /**
     * @param clientPlayerBlack the clientPlayerBlack to set
     */
    public void setClientPlayerBlack(ServerClient clientPlayerBlack)
    {
        this.clientPlayerBlack = clientPlayerBlack;
    }

    /**
     * @return the clientObservers
     */
    public ArrayList<ServerClient> getClientObservers()
    {
        return clientObservers;
    }

    /**
     * @param clientObservers the clientObservers to set
     */
    public void setClientObservers(ArrayList<ServerClient> clientObservers)
    {
        this.clientObservers = clientObservers;
    }

    /**
     * @return the playerWhiteSettings
     */
    public Settings getPlayerWhiteSettings()
    {
        return playerWhiteSettings;
    }

    /**
     * @param playerWhiteSettings the playerWhiteSettings to set
     */
    public void setPlayerWhiteSettings(Settings playerWhiteSettings)
    {
        this.playerWhiteSettings = playerWhiteSettings;
    }

    /**
     * @return the playerBlackSettings
     */
    public Settings getPlayerBlackSettings()
    {
        return playerBlackSettings;
    }

    /**
     * @param playerBlackSettings the playerBlackSettings to set
     */
    public void setPlayerBlackSettings(Settings playerBlackSettings)
    {
        this.playerBlackSettings = playerBlackSettings;
    }

    /**
     * @return the observerSettings
     */
    public Settings getObserverSettings()
    {
        return observerSettings;
    }

    /**
     * @param observerSettings the observerSettings to set
     */
    public void setObserverSettings(Settings observerSettings)
    {
        this.observerSettings = observerSettings;
    }

    /**
     * @return the password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    private class Move
    {
        private int bX;

        private int bY;

        int eX;

        int eY;

        String promoted;

        Move(int bX, int bY, int eX, int eY, String promoted) //beginX, beginY, endX, endY
        {
            this.bX = bX;
            this.bY = bY;
            this.eX = eX;
            this.eY = eY;
            this.promoted = promoted;
        }

        /**
         * @return the bX
         */
        public int getbX()
        {
            return bX;
        }

        /**
         * @param bX the bX to set
         */
        public void setbX(int bX)
        {
            this.bX = bX;
        }

        /**
         * @return the bY
         */
        public int getbY()
        {
            return bY;
        }

        /**
         * @param bY the bY to set
         */
        public void setbY(int bY)
        {
            this.bY = bY;
        }
    }
}
