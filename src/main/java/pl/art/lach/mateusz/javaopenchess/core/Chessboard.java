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
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Knight;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Queen;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Rook;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Pawn;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Bishop;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import java.util.ArrayList;
import java.util.Set;
import pl.art.lach.mateusz.javaopenchess.core.moves.Castling;
import pl.art.lach.mateusz.javaopenchess.core.moves.Move;
import pl.art.lach.mateusz.javaopenchess.core.moves.MovesHistory;
import pl.art.lach.mateusz.javaopenchess.display.views.chessboard.implementation.graphic2D.Chessboard2D;
import pl.art.lach.mateusz.javaopenchess.display.views.chessboard.ChessboardView;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;
import org.apache.log4j.*;
import pl.art.lach.mateusz.javaopenchess.utils.GameTypes;

/** 
 * @author: Mateusz  Lach ( matlak, msl )
 * @author: Damian Marciniak
 * Class to represent chessboard. Chessboard is made from squares.
 * It is setting the squers of chessboard and sets the pieces(pawns)
 * witch the owner is current player on it.
 */
public class Chessboard 
{
    private static final Logger LOG = Logger.getLogger(Chessboard.class);
    
    protected static final int TOP = 0;
    
    protected static final int BOTTOM = 7;
    
    public static final int LAST_SQUARE = 7;
    
    public static final int FIRST_SQUARE = 0;
    
    public static final int NUMBER_OF_SQUARES = 8;
    
    /*
     * squares of chessboard
     */
    protected Square squares[][];

    private Set<Square> moves;
    
    private Settings settings;
    
    protected King kingWhite;
    
    protected King kingBlack;
    
    /** 
     * For En passant:
       |-> Pawn whose in last turn moved two square
     */
    private Pawn twoSquareMovedPawn = null;
    
    private MovesHistory movesObject;
    
    protected Square activeSquare;
    
    protected int activeSquareX;
    
    protected int activeSquareY;      
    
    /**
     * chessboard view data object
     */  
    private ChessboardView chessboardView;
    
    private int halfCounter = 0;
    
    /**
     * For FEN notation. 
     * In case if state has been imported and exported after some move actions.
     */
    private int fullMoveCounterAdd = 0;

    /** 
     * Chessboard class constructor
     * @param settings reference to Settings class object for this chessboard
     * @param moves reference to MovesHistory class object for this chessboard 
     */
    public Chessboard(Settings settings, MovesHistory moves)
    {
        this.settings = settings;
        this.chessboardView = new Chessboard2D(this);

        this.activeSquareX = 0;
        this.activeSquareY = 0;
        
        this.squares = new Square[8][8];//initalization of 8x8 chessboard

        for (int i = 0; i < 8; i++) //create object for each square
        {
            for (int y = 0; y < 8; y++)
            {
                this.squares[i][y] = new Square(i, y, null);
            }
        }
        this.movesObject = moves;
    }/*--endOf-Chessboard--*/
    
    public Chessboard(Settings settings, MovesHistory moves, ChessboardView chessboardView)
    {
        this(settings, moves);
        this.chessboardView = chessboardView;
    }

    /**
     * @return the top
     */
    public static int getTop() 
    {
        return TOP;
    }

    /**
     * @return the bottom
     */
    public static int getBottom() 
    {
        return BOTTOM;
    }

    public void setPieces4NewGame(Player plWhite, Player plBlack)
    {
        Player player = plBlack;
        Player player1 = plWhite;
        this.setFigures4NewGame(0, player);
        this.setPawns4NewGame(1, player);
        this.setFigures4NewGame(7, player1);
        this.setPawns4NewGame(6, player1);
    }

    /**  
     *  Method to set Figures in row (and set Queen and King to right position)
     *  @param i row where to set figures (Rook, Knight etc.)
     *  @param player which is owner of pawns
     *  @param upsideDown if true white pieces will be on top of chessboard
     * */
    private void setFigures4NewGame(int i, Player player)
    {
        if (i != 0 && i != 7)
        {
            LOG.error("error setting figures like rook etc.");
            return;
        }
        else if (i == 0)
        {
            player.setGoDown(true);
        }

        this.getSquare(0, i).setPiece(new Rook(this, player));
        this.getSquare(7, i).setPiece(new Rook(this, player));
        this.getSquare(1, i).setPiece(new Knight(this, player));
        this.getSquare(6, i).setPiece(new Knight(this, player));
        this.getSquare(2, i).setPiece(new Bishop(this, player));
        this.getSquare(5, i).setPiece(new Bishop(this, player));
        

        this.getSquare(3, i).setPiece(new Queen(this, player));
        if (player.getColor() == Colors.WHITE)
        {
            kingWhite = new King(this, player);
            this.getSquare(4, i).setPiece(kingWhite);
        }
        else
        {
            kingBlack = new King(this, player);
            this.getSquare(4, i).setPiece(kingBlack);
        }
    }

    /**  method set Pawns in row
     *  @param i row where to set pawns
     *  @param player player which is owner of pawns
     * */
    private void setPawns4NewGame(int i, Player player)
    {
        if (i != 1 && i != 6)
        {
            LOG.error("error setting pawns etc.");
            return;
        }
        for (int x = 0; x < NUMBER_OF_SQUARES; x++)
        {
            this.getSquare(x, i).setPiece(new Pawn(this, player));
        }
    }
    
    /** Method selecting piece in chessboard
     * @param  sq square to select (when clicked))
     */
    public void select(Square sq)
    {
        this.setActiveSquare(sq);
        this.setActiveSquareX(sq.getPozX() + 1);
        this.setActiveSquareY(sq.getPozY() + 1);

        LOG.debug(String.format("active_x: %s active_y: %s",
            this.getActiveSquareX(), this.getActiveSquareY()
        ));
        this.getChessboardView().repaint();
    }/*--endOf-select--*/

    public void unselect()
    {
        this.setActiveSquareX(0);
        this.setActiveSquareY(0);
        this.setActiveSquare(null);

        this.getChessboardView().unselect();
    }/*--endOf-unselect--*/
        
    public void resetActiveSquare() 
    {
        this.setActiveSquare(null);
    }
 
    public void move(Square begin, Square end)
    {
        move(begin, end, true);
    }

    /** 
     * Method to move piece over chessboard
     * @param xFrom from which x move piece
     * @param yFrom from which y move piece
     * @param xTo to which x move piece
     * @param yTo to which y move piece
     */
    public void move(int xFrom, int yFrom, int xTo, int yTo)
    {
        Square fromSQ;
        Square toSQ;
        try
        {
            fromSQ = this.getSquare(xFrom, yFrom);
            toSQ = this.getSquare(xTo, yTo);
        }
        catch (java.lang.IndexOutOfBoundsException exc)
        {
            LOG.error("error moving piece: " + exc.getMessage());
            return;
        }
        this.move(fromSQ, toSQ, true);
    }

    public void move(Square begin, Square end, boolean refresh)
    {
        this.move(begin, end, refresh, true);
    }

    /** 
     * Method move piece from square to square
     * @param begin square from which move piece
     * @param end square where we want to move piece         *
     * @param refresh chessboard, default: true
     * @param clearForwardHistory if true, history will be cleared 
     */
    public void move(Square begin, Square end, boolean refresh, boolean clearForwardHistory)
    {
        Castling castling = Castling.NONE;
        Piece promotedPiece = null;
        Piece takenPiece = null;
        boolean wasEnPassant = false;
        if (null != end.piece)
        {
            takenPiece = end.piece;
            end.getPiece().setSquare(null);
        }

        Square tempBegin = new Square(begin);//4 moves history
        Square tempEnd = new Square(end);  //4 moves history

        begin.getPiece().setSquare(end);//set square of piece to ending
        end.piece = begin.piece;//for ending square set piece from beginin square
        begin.piece = null;//make null piece for begining square

        if (King.class == end.getPiece().getClass())
        {
            castling = moveKing(end, castling, begin);
        }
        else if (Rook.class == end.getPiece().getClass())
        {
            moveRook(end);
        }
        else if (Pawn.class == end.getPiece().getClass())
        {
            wasEnPassant = movePawn(end, begin, tempEnd, wasEnPassant);

            if (Pawn.canBePromoted(end)) //promote Pawn
            {
                promotedPiece = promotePawn(clearForwardHistory, end, promotedPiece);
            }
        }
        else if (Pawn.class != end.getPiece().getClass())
        {
            setTwoSquareMovedPawn(null); //erase last saved move (for En passant)
        }

        if (refresh)
        {
            this.unselect();//unselect square
            repaint();
        }
        
        handleHalfMoveCounter(end, takenPiece);
        handleHistory(clearForwardHistory, tempBegin, tempEnd, castling, wasEnPassant, promotedPiece);
        
    }

    private void handleHalfMoveCounter(Square end, Piece takenPiece)
    {
        if (isHalfMove(end, takenPiece))
        {
            halfCounter++;
        }
        else
        {
            halfCounter = 0;
        }
    }

    private static boolean isHalfMove(Square end, Piece takenPiece)
    {
        return !(end.getPiece() instanceof Pawn) && null == takenPiece;
    }

    private void handleHistory(boolean clearForwardHistory, Square tempBegin, Square tempEnd, Castling castling, boolean wasEnPassant, Piece promotedPiece)
    {
        if (clearForwardHistory)
        {
            this.movesObject.clearMoveForwardStack();
            this.movesObject.addMove(tempBegin, tempEnd, true, castling, wasEnPassant, promotedPiece);
        }
        else
        {
            this.movesObject.addMove(tempBegin, tempEnd, false, castling, wasEnPassant, promotedPiece);
        }
        if (this.getSettings().isGameAgainstComputer())
        {
            //TODO: something to implement over here?
        }
    }

    public boolean movePawn(Square end, Square begin, Square tempEnd, boolean wasEnPassant)
    {
        if (getTwoSquareMovedPawn() != null && getSquares()[end.getPozX()][begin.getPozY()] == getTwoSquareMovedPawn().getSquare()) //en passant
        {
            tempEnd.piece = getSquares()[end.getPozX()][begin.getPozY()].piece; //ugly hack - put taken pawn in en passant plasty do end square
            
            squares[end.pozX][begin.pozY].piece = null;
            wasEnPassant = true;
        }
        if (begin.getPozY() - end.getPozY() == 2 || end.getPozY() - begin.getPozY() == 2) //moved two square
        {
            setTwoSquareMovedPawn((Pawn) end.piece);
        }
        else
        {
            setTwoSquareMovedPawn(null); //erase last saved move (for En passant)
        }
        return wasEnPassant;
    }

    public Piece promotePawn(boolean clearForwardHistory, Square end, Piece promotedPiece)
    {
        if (clearForwardHistory)
        {
            Piece piece = end.getPiece().getPlayer().getPromotionPiece(this);
            if (null != piece)
            {
                piece.setChessboard(end.getPiece().getChessboard());
                piece.setPlayer(end.getPiece().getPlayer());
                piece.setSquare(end.getPiece().getSquare());
                end.piece = piece;
                promotedPiece = end.piece;
            }
        }
        return promotedPiece;
    }

    private void moveRook(Square end)
    {
        if (!((Rook) end.piece).getWasMotioned())
        {
            ((Rook) end.piece).setWasMotioned(true);
        }
    }

    private Castling moveKing(Square end, Castling castling, Square begin)
    {
        if (!((King) end.piece).getWasMotioned())
        {
            ((King) end.piece).setWasMotioned(true);
        }
        //Castling
        castling = King.getCastling(begin, end);
        if (Castling.SHORT_CASTLING == castling)
        {
            move(getSquare(7, begin.getPozY()), getSquare(end.getPozX() - 1, begin.getPozY()), false, false);
        }
        else if (Castling.LONG_CASTLING == castling)
        {
            move(getSquare(0, begin.getPozY()), getSquare(end.getPozX() + 1, begin.getPozY()), false, false);
        }
        //endOf Castling
        return castling;
    }


    public boolean redo()
    {
        return redo(true);
    }

    public boolean redo(boolean refresh)
    {
        if (this.getSettings().getGameType() == GameTypes.LOCAL) //redo only for LOCAL game
        {
            Move first = this.movesObject.redo();

            Square from;
            Square to;

            if (first != null)
            {
                from = first.getFrom();
                to = first.getTo();

                this.move(this.getSquares()[from.getPozX()][from.getPozY()], this.getSquares()[to.getPozX()][to.getPozY()], true, false);
                if (first.getPromotedPiece() != null)
                {
                    Pawn pawn = (Pawn) this.getSquares()[to.getPozX()][to.getPozY()].piece;
                    pawn.setSquare(null);

                    this.squares[to.pozX][to.pozY].piece = first.getPromotedPiece();
                    Piece promoted = this.getSquares()[to.getPozX()][to.getPozY()].piece;
                    promoted.setSquare(this.getSquares()[to.getPozX()][to.getPozY()]);
                }
                return true;
            }
            
        }
        return false;
    }

    public boolean undo()
    {
        return undo(true);
    }

    public synchronized boolean undo(boolean refresh) //undo last move
    {
        Move last = this.movesObject.undo();

        if (canUndo(last))
        {
            return processUndoOperation(last, refresh);
        }
        return false;
    }

    private boolean processUndoOperation(Move last, boolean refresh)
    {
        Square begin = last.getFrom();
        Square end = last.getTo();
        try
        {
            Piece moved = last.getMovedPiece();
            this.squares[begin.pozX][begin.pozY].piece = moved;
            
            moved.setSquare(this.getSquares()[begin.getPozX()][begin.getPozY()]);
            
            Piece taken = last.getTakenPiece();
            if (last.getCastlingMove() != Castling.NONE)
            {
                handleUndoCastling(last, end, begin, moved);
            }
            else if (Rook.class == moved.getClass())
            {
                ((Rook) moved).setWasMotioned(false);
            }
            else if (Pawn.class == moved.getClass() && last.wasEnPassant())
            {
                handleEnPessant(last, end, begin);
            }
            else if (Pawn.class == moved.getClass() && last.getPromotedPiece() != null)
            {
                handlePawnPromotion(end);
            }
            
            //check one more move back for en passant
            Move oneMoveEarlier = this.movesObject.getLastMoveFromHistory();
            if (oneMoveEarlier != null && oneMoveEarlier.wasPawnTwoFieldsMove())
            {
                final int toPozX = oneMoveEarlier.getTo().getPozX();
                final int toPozY = oneMoveEarlier.getTo().getPozY();
                Piece canBeTakenEnPassant = this.getSquare(toPozX, toPozY).getPiece();
                if (Pawn.class == canBeTakenEnPassant.getClass())
                {
                    this.setTwoSquareMovedPawn((Pawn) canBeTakenEnPassant);
                }
            }
            else
            {
                this.setTwoSquareMovedPawn(null);
            }

            if (taken != null && !last.wasEnPassant())
            {
                this.squares[end.pozX][end.pozY].piece = taken;
                taken.setSquare(this.getSquares()[end.getPozX()][end.getPozY()]);
            }
            else
            {
                this.squares[end.pozX][end.pozY].piece = null;
            }
            
            if (refresh)
            {
                this.unselect();//unselect square
                repaint();
            }
            if (0 < halfCounter)
            {
                halfCounter--;
            }
        }
        catch (ArrayIndexOutOfBoundsException | NullPointerException exc)
        {
            LOG.error(
                String.format("error: %s exc object: ", exc.getClass()),
                exc
            );
            return false;
        }
        return true;
    }

    private void handleUndoCastling(Move last, Square end, Square begin, Piece moved)
    {
        Piece rook = null;
        if (last.getCastlingMove() == Castling.SHORT_CASTLING)
        {
            rook = handleShortCastling(rook, end, begin);
        }
        else
        {
            rook = handleLongCastling(rook, end, begin);
        }
        ((King) moved).setWasMotioned(false);
        ((Rook) rook).setWasMotioned(false);
    }

    private static boolean canUndo(Move last)
    {
        return last != null && last.getFrom() != null;
    }

    private void handlePawnPromotion(Square end)
    {
        Piece promoted = this.getSquares()[end.getPozX()][end.getPozY()].piece;
        promoted.setSquare(null);
        this.squares[end.pozX][end.pozY].piece = null;
    }

    private void handleEnPessant(Move last, Square end, Square begin)
    {
        Pawn pawn = (Pawn) last.getTakenPiece();
        this.squares[end.pozX][begin.pozY].piece = pawn;
        pawn.setSquare(this.getSquares()[end.getPozX()][begin.getPozY()]);
    }

    private Piece handleLongCastling(Piece rook, Square end, Square begin)
    {
        rook = this.getSquares()[end.getPozX() + 1][end.getPozY()].piece;
        this.squares[0][begin.pozY].piece = rook;
        rook.setSquare(this.getSquares()[0][begin.getPozY()]);
        this.squares[end.pozX + 1][end.pozY].piece = null;
        return rook;
    }

    private Piece handleShortCastling(Piece rook, Square end, Square begin)
    {
        rook = this.getSquares()[end.getPozX() - 1][end.getPozY()].piece;
        this.squares[7][begin.pozY].piece = rook;
        rook.setSquare(this.getSquares()[7][begin.getPozY()]);
        this.squares[end.pozX - 1][end.pozY].piece = null;
        return rook;
    }

    /**
     * @return the squares
     */
    public Square[][] getSquares() 
    {
        return squares;
    }
    
    public Square getSquare(int x, int y) 
    {
        try 
        {
            return squares[x][y];
        } 
        catch(ArrayIndexOutOfBoundsException exc) 
        {
            return null;
        }
    }
    
    public Square getSquare(Squares squareX, Squares squareY)
    {
        return getSquare(squareX.getValue(), squareY.getValue());
    }
    
    public void clear()
    {
        for (int i=0; i < squares.length; i++)
        {
            for (int j=0; j < squares[i].length; j++)
            {
                Piece piece = squares[i][j].getPiece();
                piece = null;
                squares[i][j].setPiece(null);
            }
        }
    }

    /**
     * @return the activeSquare
     */
    public Square getActiveSquare() 
    {
        return activeSquare;
    }

    public ArrayList<Piece> getAllPieces(Colors color)
    {
        ArrayList<Piece> result = new ArrayList<>();
        for (int i=0; i < squares.length; i++)
        {
            for (int j=0; j < squares[i].length; j++)
            {
                Square sq = squares[i][j];
                if(null != sq.getPiece()
                        && ( sq.getPiece().getPlayer().getColor() == color || color == null) )
                {
                    result.add(sq.getPiece());
                }
            }
        }       
        return result;
    }
    
    public static boolean wasEnPassant(Square sq)
    {
        return sq.getPiece() != null
                && sq.getPiece().getChessboard().getTwoSquareMovedPawn() != null
                && sq == sq.getPiece().getChessboard().getTwoSquareMovedPawn().getSquare();
    }    

    /**
     * @return the kingWhite
     */
    public King getKingWhite()
    {
        return kingWhite;
    }

    /**
     * @return the kingBlack
     */
    public King getKingBlack()
    {
        return kingBlack;
    }
    
    public void setKingWhite(King kingWhite, Square sq)
    {
        this.kingWhite = kingWhite;
        this.getSquare(sq.getPozX(), sq.getPozY()).setPiece(this.kingWhite);
    }
    
    public void setKingBlack(King kingBlack, Square sq)
    {
        this.kingBlack = kingBlack;
        this.getSquare(sq.getPozX(), sq.getPozY()).setPiece(this.kingBlack);
    }
    /**
     * @return the twoSquareMovedPawn
     */
    public Pawn getTwoSquareMovedPawn()
    {
        return twoSquareMovedPawn;
    }

    /**
     * @return the chessboardView
     */
    public ChessboardView getChessboardView()
    {
        return chessboardView;
    }

    /**
     * @param chessboardView the chessboardView to set
     */
    public void setChessboardView(ChessboardView chessboardView)
    {
        this.chessboardView = chessboardView;
    }
    
    public void repaint()
    {
        getChessboardView().repaint();
    }

    /**
     * @return the settings
     */
    public Settings getSettings()
    {
        return settings;
    }

    /**
     * @param settings the settings to set
     */
    public void setSettings(Settings settings)
    {
        this.settings = settings;
        // TODO: refactor. Create a GameFactory class instead such stupid methods.
        for (int i = 0; i < Chessboard.LAST_SQUARE; i++)
        {
            for (int j = 0; j < Chessboard.LAST_SQUARE; j++)
            {
                Square sq = this.getSquare(i, j);
                if (null != sq.getPiece())
                {
                    if (Colors.WHITE == sq.getPiece().getPlayer().getColor())
                    {
                        sq.getPiece().setPlayer(settings.getPlayerWhite());
                    }
                    else
                    {
                        sq.getPiece().setPlayer(settings.getPlayerBlack());
                    }
                }
            }
        }
    }

    /**
     * @return the moves
     */
    public Set<Square> getMoves()
    {
        return moves;
    }

    /**
     * @param moves the moves to set
     */
    public void setMoves(Set<Square> moves)
    {
        this.moves = moves;
    }

    /**
     * @param activeSquare the activeSquare to set
     */
    public void setActiveSquare(Square activeSquare)
    {
        this.activeSquare = activeSquare;
    }

    /**
     * @return the activeSquareX
     */
    public int getActiveSquareX()
    {
        return activeSquareX;
    }

    /**
     * @param activeSquareX the activeSquareX to set
     */
    public void setActiveSquareX(int activeSquareX)
    {
        this.activeSquareX = activeSquareX;
    }

    /**
     * @return the activeSquareY
     */
    public int getActiveSquareY()
    {
        return activeSquareY;
    }

    /**
     * @param activeSquareY the activeSquareY to set
     */
    public void setActiveSquareY(int activeSquareY)
    {
        this.activeSquareY = activeSquareY;
    }

    /**
     * @param twoSquareMovedPawn the twoSquareMovedPawn to set
     */
    public void setTwoSquareMovedPawn(Pawn twoSquareMovedPawn)
    {
        this.twoSquareMovedPawn = twoSquareMovedPawn;
    }

    /**
     * @return the halfCounter
     */
    public int getHalfCounter()
    {
        return halfCounter;
    }

    /**
     * @param halfCounter the halfCounter to set
     */
    public void setHalfCounter(int halfCounter)
    {
        this.halfCounter = halfCounter;
    }

    /**
     * @return the fullMoveCounterAdd
     */
    public int getFullMoveCounterAdd()
    {
        return fullMoveCounterAdd;
    }

    /**
     * @param fullMoveCounterAdd the fullMoveCounterAdd to set
     */
    public void setFullMoveCounterAdd(int fullMoveCounterAdd)
    {
        this.fullMoveCounterAdd = fullMoveCounterAdd;
    }
}
