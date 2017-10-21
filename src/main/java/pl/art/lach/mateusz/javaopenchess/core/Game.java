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

import pl.art.lach.mateusz.javaopenchess.core.players.Player;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.King;
import pl.art.lach.mateusz.javaopenchess.network.Chat;
import pl.art.lach.mateusz.javaopenchess.network.Client;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.awt.event.ComponentListener;
import org.apache.log4j.Logger;
import java.io.IOException;
import pl.art.lach.mateusz.javaopenchess.JChessApp;
import pl.art.lach.mateusz.javaopenchess.JChessView;
import pl.art.lach.mateusz.javaopenchess.core.ai.AI;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.DataExporter;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.DataImporter;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.DataTransferFactory;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.TransferFormat;
import pl.art.lach.mateusz.javaopenchess.core.exceptions.ReadGameError;
import pl.art.lach.mateusz.javaopenchess.core.moves.Move;
import pl.art.lach.mateusz.javaopenchess.core.moves.MovesHistory;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.core.pieces.PieceFactory;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Pawn;
import pl.art.lach.mateusz.javaopenchess.core.players.PlayerType;
import pl.art.lach.mateusz.javaopenchess.display.panels.HistoryButtons;
import pl.art.lach.mateusz.javaopenchess.display.panels.LocalSettingsView;
import pl.art.lach.mateusz.javaopenchess.display.views.chessboard.ChessboardView;
import pl.art.lach.mateusz.javaopenchess.display.windows.JChessTabbedPane;
import pl.art.lach.mateusz.javaopenchess.utils.GameTypes;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;
import pl.art.lach.mateusz.javaopenchess.utils.SettingsFactory;



/** 
 * Class responsible for the starts of new games, loading games,
 * saving it, and for ending it.
 * This class is also responsible for appoing player with have
 * a move at the moment
 * 
 * @author: Mateusz  Lach ( matlak, msl )
 * @author: Damian Marciniak
 */
public class Game extends JPanel implements ComponentListener, MouseListener
{
    
    private static final Logger LOG = Logger.getLogger(Game.class);
    
    private DataExporter fenExporter = DataTransferFactory.getExporterInstance(TransferFormat.FEN);
    
    private static final String FEN_PREFIX_NAME = "FEN: ";
    
    private static final int PADDING = 5;
    
    /**
     * Settings object of the current game
     */
    protected Settings settings;
 
    /**
     * if chessboard is blocked - true, false otherwise
     */
    private boolean blockedChessboard;
    
    /**
     * chessboard data object
     */    
    protected Chessboard chessboard;

    /**
     * Currently active player object
     */
    protected Player activePlayer;
    
    /**
     * Game clock object
     */
    protected GameClock gameClock;
    
    /**
     * Client object (NETWORK game)
     */       
    protected Client client;

    /**
     * History of moves object
     */       
    protected MovesHistory moves;
    
    /**
     * Chag object (NETWORK game)
     */       
    protected Chat chat;
    
    protected JTabbedPane tabPane;
    
    protected JTextField fenState;
   
    protected LocalSettingsView localSettingsView;
    
    private AI ai = null;
    
    private boolean isEndOfGame = false;
    
    private HistoryButtons historyButtons;

    public Game()
    {
        init();
    }

    protected final void init()
    {
        this.setLayout(null);
        this.chat = new Chat();
        this.settings = SettingsFactory.getInstance();
        this.moves = new MovesHistory(this);
        this.localSettingsView = new LocalSettingsView(this);
        this.chessboard = new Chessboard(this.getSettings(), this.moves);
        
        int chessboardWidth = initChessboardView();
        initGameClock();
        initMovesHistory();

        initTabPane(chessboardWidth);
        initFenState(chessboardWidth);
        initHistoryButtons();
        
        this.setBlockedChessboard(false);
        this.setLayout(null);
        this.setDoubleBuffered(true);
        this.addComponentListener(this);
    }

    private void initHistoryButtons()
    {
        this.historyButtons = new HistoryButtons(this);
        this.historyButtons.setSize(210, 40);
        this.add(this.historyButtons);
    }

    private int initChessboardView()
    {
        ChessboardView chessboardView = chessboard.getChessboardView();
        int chessboardWidth = chessboardView.getChessboardWidht(true);
        this.add(chessboardView);
        chessboardView.addMouseListener(this);
        return chessboardWidth;
    }

    private void initMovesHistory()
    {
        JScrollPane movesHistory = this.moves.getScrollPane();
        movesHistory.setSize(new Dimension(180, 350));
        movesHistory.setLocation(new Point(500, 121));
        this.add(movesHistory);
    }

    private void initGameClock()
    {
        gameClock = new GameClock(this);
        gameClock.setSize(new Dimension(200, 100));
        gameClock.setLocation(new Point(500, 0));
        this.add(gameClock);
    }

    private void initFenState(int chessboardWidth)
    {
        this.fenState = new JTextField();
        this.fenState.setEditable(false);
        this.fenState.setSize(new Dimension(chessboardWidth, 20));
        this.fenState.setLocation(new Point(0, 500));
        this.add(fenState);
    }

    private void initTabPane(int chessboardWidth)
    {
        this.tabPane = new JTabbedPane();
        this.tabPane.addTab(Settings.lang("game_chat"), this.chat);
        this.tabPane.addTab(Settings.lang("game_settings"), this.localSettingsView);
        this.tabPane.setSize(new Dimension(380, 100));
        this.tabPane.setLocation(new Point(chessboardWidth, chessboardWidth/2));
        this.tabPane.setMinimumSize(new Dimension(400, 100));
        this.add(tabPane);
    }
    
    public final void updateFenStateText()
    {
        this.fenState.setText(FEN_PREFIX_NAME + this.exportGame(fenExporter));
    }

    /** 
     * Method to save actual state of game
     * @param path address of place where game will be saved
     * @param dataExporter dataExporter object
     * @return game state in PGN string
     */
    public String saveGame(File path, DataExporter dataExporter)
    {
        File file = path;
        FileWriter fileW;
        String str;
        try
        {
            fileW = new FileWriter(file);
            str = exportGame(dataExporter);
            fileW.write(str);
            fileW.flush();
            fileW.close();
            JOptionPane.showMessageDialog(this, Settings.lang("game_saved_properly"));
        }
        catch (IOException exc)
        {
            LOG.error("error writing to file: ", exc);
            JOptionPane.showMessageDialog(this, Settings.lang("error_writing_to_file")+": " + exc);
            return null;
        }
        return str;
    }
    
    public String exportGame(DataExporter de)
    {
        if (null == de)
        {
            return null;
        }
        return de.exportData(this);
    }
    
    public void importGame(String dataInString, DataImporter di) throws ReadGameError
    {
        if (null == di)
        {
            return;
        }
        di.importData(dataInString, this);
    }      
 
    /** 
     * Method to Start new game
     */
    public void newGame()
    {
        getChessboard().setPieces4NewGame(
            getSettings().getPlayerWhite(),
            getSettings().getPlayerBlack()
        );

        activePlayer = getSettings().getPlayerWhite();
        if (activePlayer.getPlayerType() != PlayerType.LOCAL_USER)
        {
            this.setBlockedChessboard(true);
        }
        runRenderingArtifactDirtyFix();
        updateFenStateText();
    }
    
    /**
     * This is helper method to fix rendering artifacts.
     * It mostly for first run issues. 
     * @throws ArrayIndexOutOfBoundsException 
     */
    private void runRenderingArtifactDirtyFix() 
            throws ArrayIndexOutOfBoundsException
    {
        JChessView jChessView = JChessApp.getJavaChessView();
        if (null != jChessView)
        {
            Game activeGame = JChessApp.getJavaChessView().getActiveTabGame();
            if (null != activeGame) {
                Chessboard activeChessboard = activeGame.getChessboard();
                ChessboardView chessboardView = activeChessboard.getChessboardView();
                if (JChessApp.getJavaChessView().getNumberOfOpenedTabs() == 0)
                {
                    chessboardView.resizeChessboard(
                        chessboardView.getChessboardHeight(false)
                    );
                    activeChessboard.repaint();
                    activeGame.repaint();
                }            
            }
            chessboard.repaint();
            this.repaint();      
        }
    }

    /**
     * Method to end game
     * @param message what to show player(s)
     *     at end of the game (for example "draw", "black wins" etc.)
     */
    public void endGame(String message)
    {
        this.setBlockedChessboard(true);
        this.isEndOfGame = true;
        LOG.debug(message);
        JOptionPane.showMessageDialog(null, message);
    }

    /**
     * Method to swich active players after move
     */
    public void switchActive()
    {
        if (activePlayer == getSettings().getPlayerWhite())
        {
            activePlayer = getSettings().getPlayerBlack();
        }
        else
        {
            activePlayer = getSettings().getPlayerWhite();
        }
        this.getGameClock().switchClocks();
    }

    /**
     * Method of getting accualy active player
     *  @return  player The player which have a move
     */
    public Player getActivePlayer()
    {
        return this.activePlayer;
    }
    
    public void setActivePlayer(Player player)
    {
        this.activePlayer = player;
    }

    /**
     * Method to go to next move (checks if game is LOCAL/NETWORK etc.)
     */
    public void nextMove()
    {
        switchActive();
        LOG.debug(String.format(
            "next move, active player: %s, color: %s, type: %s",
            activePlayer.getName(),
            activePlayer.getColor().name(),
            activePlayer.getPlayerType().name()
        ));
        
        if (activePlayer.getPlayerType() == PlayerType.LOCAL_USER)
        {
            this.setBlockedChessboard(false);
        }
        else if (activePlayer.getPlayerType() == PlayerType.NETWORK_USER
                || activePlayer.getPlayerType() == PlayerType.COMPUTER)
        {
            this.setBlockedChessboard(true);
        }
        updateFenStateText();
    }

    /** 
     * Method to simulate Move to check
     *   if it's correct etc. (usable for NETWORK game).
     * @param beginX from which X (on chessboard) move starts
     * @param beginY from which Y (on chessboard) move starts
     * @param endX   to   which X (on chessboard) move go
     * @param endY   to   which Y (on chessboard) move go
     * @param promoted promoted string
     * @return boolean true if move OK, false otherwise.
     * */
    public boolean simulateMove(int beginX, int beginY, int endX,
            int endY, String promoted)
    {
        try 
        {
            Square begin = getChessboard().getSquare(beginX, beginY);
            Square end   = getChessboard().getSquare(endX, endY);
            getChessboard().select(begin);
            Piece activePiece = getChessboard().getActiveSquare().getPiece();
            if (activePiece.getAllMoves().contains(end)) //move
            {
                getChessboard().move(begin, end);
                if (null != promoted && !"".equals(promoted))
                {
                    Piece promotedPiece = PieceFactory.getPiece(
                            getChessboard(), 
                            end.getPiece().getPlayer().getColor(),
                            promoted,
                            activePlayer);
                    end.setPiece(promotedPiece);
                }
            }
            else
            {
                LOG.debug(String.format(
                    "Bad move: beginX: %s beginY: %s endX: %s endY: %s",
                    beginX, beginY, endX, endY
                ));
                return false;
            }
            getChessboard().unselect();
            nextMove();

            return true;
            
        } 
        catch (StringIndexOutOfBoundsException
                | ArrayIndexOutOfBoundsException
                | NullPointerException exc) 
        {
            LOG.error("simulateMove error: ", exc);
            return false;
        }    
    }

    @Override
    public void mouseClicked(MouseEvent arg0)
    {
    }
    
    public boolean undo()
    {
        boolean status = false;
        
        if(this.getSettings().getGameType() != GameTypes.NETWORK)
        {
            status = getChessboard().undo();
            if(status)
            {
                this.switchActive();
            }
            else
            {
                getChessboard().repaint();//repaint for sure
            }
            if(this.getSettings().isGameAgainstComputer())
            {
                if(this.getActivePlayer().getPlayerType() == PlayerType.COMPUTER)
                {
                    this.undo();
                }
            }
        }
        else if(this.getSettings().getGameType() == GameTypes.NETWORK)
        {
            this.getClient().sendUndoAsk();
            status = true;
        }
        updateFenStateText();
        return status;
    }
    
    public boolean rewindToBegin()
    {
        boolean result = false;
        
        if (this.getSettings().getGameType() == GameTypes.LOCAL)
        {
            while (getChessboard().undo())
            {
                result = true;
            }
        }
        else
        {
            throw new UnsupportedOperationException(
                Settings.lang("operation_supported_only_in_local_game")
            );
        }
        updateFenStateText();
        return result;
    }
    
    public boolean rewindToEnd() throws UnsupportedOperationException
    {
        boolean result = false;
        
        if (this.getSettings().getGameType() == GameTypes.LOCAL)
        {
            while(getChessboard().redo())
            {
                result = true;
            }
        }
        else
        {
            throw new UnsupportedOperationException(
                Settings.lang("operation_supported_only_in_local_game")
            );
        }
        updateFenStateText();
        return result;
    }
    
    public boolean redo()
    {
        boolean status = getChessboard().redo();
        if(this.getSettings().getGameType() == GameTypes.LOCAL)
        {
            if (status)
            {
                this.nextMove();
            }
            else
            {
                getChessboard().repaint();//repaint for sure
            }
        }
        else
        {
            throw new UnsupportedOperationException(
                Settings.lang("operation_supported_only_in_local_game")
            );
        }
        updateFenStateText();
        return status;
    }
    
    
    @Override
    public void mousePressed(MouseEvent event)
    {
        if (event.getButton() == MouseEvent.BUTTON3) //right button
        {
            this.undo();
        }
        else if (event.getButton() == MouseEvent.BUTTON2
                && getSettings().getGameType() == GameTypes.LOCAL)
        {
            this.redo();
        }
        else if (event.getButton() == MouseEvent.BUTTON1) //left button
        {
            if (!isChessboardBlocked())
            {
                moveActionInvoked(event);
            }
            else
            {
                LOG.debug("Chessboard is blocked");
            }
        }
        updateFenStateText();
    }

    private void moveActionInvoked(MouseEvent event)
            throws ArrayIndexOutOfBoundsException
    {
        try
        {
            int x = event.getX();//get X position of mouse
            int y = event.getY();//get Y position of mouse
            
            Square sq = getChessboard().getChessboardView().getSquare(x, y);
            if (cannotInvokeMoveAction(sq))
            {
                return;
            }
            if (isSelectAction(sq))
            {
                selectSquare(sq);
            }
            else if (getChessboard().getActiveSquare() == sq) //unselect
            {
                getChessboard().unselect();
            }
            else if (canInvokeMoveAction(sq)) //move
            {
                if (getSettings().getGameType() == GameTypes.LOCAL)
                {
                    getChessboard().move(getChessboard().getActiveSquare(), sq);
                }
                else if (getSettings().getGameType() == GameTypes.NETWORK)
                {
                    moveNetworkActionInvoked(sq);
                }                
                getChessboard().unselect();
                
                //switch player
                this.nextMove();
                
                //checkmate or stalemate
                King king;
                if (this.activePlayer == getSettings().getPlayerWhite())
                {
                    king = getChessboard().getKingWhite();
                }
                else
                {
                    king = getChessboard().getKingBlack();
                }
                
                switch (king.isCheckmatedOrStalemated())
                {
                    case 1:
                        this.endGame(String.format(
                            "Checkmate! %s player lose!",
                            king.getPlayer().getColor().toString()
                        ));
                        break;
                    case 2:
                        this.endGame("Stalemate! Draw!");
                        break;
                }
            }
            if (canDoComputerMove())
            {
                doComputerMove();
                highlighTabIfInactive();
            }
            
        }
        catch(NullPointerException exc)
        {
            LOG.error("NullPointerException: " + exc.getMessage(), exc);
            getChessboard().repaint();
        }
        updateFenStateText();
    }

    private boolean canInvokeMoveAction(Square sq)
    {
        Square activeSq = getChessboard().getActiveSquare();
        return activeSq != null
                && activeSq.piece != null
                && activeSq.getPiece().getAllMoves().contains(sq);
    }

    private void selectSquare(Square sq)
    {
        getChessboard().unselect();
        getChessboard().select(sq);
    }

    private boolean isSelectAction(Square sq)
    {
        return sq.piece != null
                && sq.getPiece().getPlayer() == this.activePlayer
                && sq != getChessboard().getActiveSquare();
    }

    private boolean cannotInvokeMoveAction(Square sq)
    {
        return (sq == null && sq.piece == null
                && getChessboard().getActiveSquare() == null)
                || (this.getChessboard().getActiveSquare() == null
                    && sq.piece != null
                    && sq.getPiece().getPlayer() != this.activePlayer
                );
    }

    private void moveNetworkActionInvoked(Square sq)
    {
        Square from = getChessboard().getActiveSquare();
        boolean canBePromoted = Pawn.class == from.getPiece().getClass()
                && Pawn.canBePromoted(sq);
        getChessboard().move(from, sq);
        Piece promoted = null;
        if (canBePromoted)
        {
            promoted = sq.getPiece();
        }
        getClient().sendMove(
            from.getPozX(),
            from.getPozY(),
            sq.getPozX(),
            sq.getPozY(),
            null == promoted ? "" : promoted.getName()
        );
    }

    private void highlighTabIfInactive() throws ArrayIndexOutOfBoundsException
    {
        JChessView jChessView = JChessApp.getJavaChessView();
        int tabNumber = jChessView.getTabNumber(this);
        if (jChessView.getActiveTabGame() != this)
        {
            jChessView.getGamesPane().setForegroundAt(
                tabNumber,
                JChessTabbedPane.EVENT_COLOR
            );
        }
    }

    private boolean canDoComputerMove() 
    {
        return !this.isEndOfGame 
                && this.getSettings().isGameAgainstComputer()
                && this.getActivePlayer().getPlayerType() == PlayerType.COMPUTER
                && null != this.getAi();
    }

    public void doComputerMove() 
    {
        Move move = this.getAi().getMove(this, this.getMoves().getLastMoveFromHistory());
        getChessboard().move(move.getFrom(), move.getTo());
        if (null != move.getPromotedPiece())
        {
            move.getTo().setPiece(move.getPromotedPiece());
        }
        this.nextMove();
    }
    
    @Override
    public void mouseReleased(MouseEvent arg0)
    {
    }
    
    @Override
    public void mouseEntered(MouseEvent arg0)
    {
    }
    
    @Override
    public void mouseExited(MouseEvent arg0)
    {
    }

    @Override
    public void componentResized(ComponentEvent e)
    {
        resizeGame();
    }

    @Override
    public void componentMoved(ComponentEvent e)
    {
        componentResized(e);
        repaint();
    }

    @Override
    public void componentShown(ComponentEvent e)
    {
        componentResized(e);
    }

    @Override
    public void componentHidden(ComponentEvent e)
    {
    }

    /**
     * @return the chessboard
     */
    public Chessboard getChessboard()
    {
        return chessboard;
    }

    /**
     * @return the settings
     */
    public final Settings getSettings() 
    {
        return settings;
    }

    /**
     * @return the blockedChessboard
     */
    public boolean isChessboardBlocked() 
    {
        return isBlockedChessboard();
    }

    /**
     * @return the gameClock
     */
    public GameClock getGameClock() 
    {
        return gameClock;
    }

    /**
     * @return the client
     */
    public Client getClient() 
    {
        return client;
    }

    /**
     * @return the moves
     */
    public MovesHistory getMoves() 
    {
        return moves;
    }

    /**
     * @return the chat
     */
    public Chat getChat() 
    {
        return chat;
    }

    /**
     * //TODO: refactor, why I've to change settings object in 2 places!?
     * @param settings the settings to set
     */
    public void setSettings(Settings settings)
    {
        this.settings = settings;
        this.chessboard.setSettings(settings);
    }

    /**
     * @param client the client to set
     */
    public void setClient(Client client)
    {
        this.client = client;
    }

    /**
     * @param chat the chat to set
     */
    public void setChat(Chat chat)
    {
        this.chat = chat;
    }

    @Override
    public void repaint()
    {
        super.repaint();
        if (null != this.tabPane)
        {
            this.tabPane.repaint();
        }
        if (null != this.localSettingsView)
        {
            this.localSettingsView.repaint();
        }
        if (null != chessboard)
        {
            this.getChessboard().repaint();
        }
        if (null != historyButtons)
        {
            this.historyButtons.repaint();
        }
    }

    public void resizeGame()
    {
        int height = this.getHeight() >= this.getWidth() ? this.getWidth() : this.getHeight();
        int chessHeight = (int)(countChessHeight(height));
        int chessWidthWithLabels;
        JScrollPane movesScrollPane = this.getMoves().getScrollPane();
        ChessboardView chessboardView = getChessboard().getChessboardView();
        chessboardView.resizeChessboard((int)chessHeight);
        chessHeight = chessboardView.getHeight();
        chessWidthWithLabels  = chessboardView.getChessboardWidht(true);
        movesScrollPane.setLocation(new Point(chessHeight + PADDING, 100));
        movesScrollPane.setSize(
            movesScrollPane.getWidth(),
            chessHeight - 100 - (chessWidthWithLabels/4)
        );
        fenState.setLocation(new Point(0, chessHeight + PADDING));
        fenState.setSize(new Dimension(chessWidthWithLabels, 30));
        
        historyButtons.setLocation(new Point(chessHeight + PADDING, chessHeight + PADDING));

        getGameClock().setLocation(new Point(chessHeight + PADDING, 0));
        if (null !=  tabPane)
        {
            tabPane.setLocation(new Point(chessWidthWithLabels + PADDING, ((int)chessWidthWithLabels/4)*3));
            tabPane.setSize(new Dimension(movesScrollPane.getWidth(), chessWidthWithLabels/4)); 
            tabPane.repaint();       
        }
    }

    private static long countChessHeight(int height)
    {
        return Math.round(
            (height * 0.80)/Chessboard.NUMBER_OF_SQUARES 
        ) * Chessboard.NUMBER_OF_SQUARES;
    }

    /**
     * @return the ai
     */
    public AI getAi() {
        return ai;
    }

    /**
     * @param ai the ai to set
     */
    public void setAi(AI ai) {
        this.ai = ai;
    }

    /**
     * @return the isEndOfGame
     */
    public boolean isIsEndOfGame() {
        return isEndOfGame;
    }

    /**
     * @param isEndOfGame the isEndOfGame to set
     */
    public void setIsEndOfGame(boolean isEndOfGame) {
        this.isEndOfGame = isEndOfGame;
    }

    /**
     * @return the blockedChessboard
     */
    public boolean isBlockedChessboard()
    {
        return blockedChessboard;
    }

    /**
     * @param blockedChessboard the blockedChessboard to set
     */
    public void setBlockedChessboard(boolean blockedChessboard)
    {
        this.blockedChessboard = blockedChessboard;
    }

    /**
     * @return the fenState
     */
    public JTextField getFenState()
    {
        return fenState;
    }

    /**
     * @param fenState the fenState to set
     */
    public void setFenState(JTextField fenState)
    {
        this.fenState = fenState;
    }
}