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
package pl.art.lach.mateusz.javaopenchess.display.views.chessboard.implementation.graphic2D;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Set;
import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.Square;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.display.views.chessboard.ChessboardView;
import org.apache.log4j.Logger;

/**
 * @author Mateusz  Lach ( matlak, msl )
 */
public class Chessboard2D extends ChessboardView 
{
    private static final Logger LOG = Logger.getLogger(Chessboard2D.class);
    
    protected Pieces2D pieces2D = Pieces2D.getInstance();
    
    private static final String[] LETTERS = {
        "a", "b", "c", "d", "e", "f", "g", "h"
    };
    
    public Chessboard2D(Chessboard chessboard)
    {
        init(chessboard);
    }

    protected final void init(Chessboard chessboard)
    {
        this.setChessboard(chessboard);
        
        this.setVisible(true);
        this.setSize(Chessboard2D.imgHeight, Chessboard2D.imgWidht);
        this.setLocation(new Point(0, 0));
        this.setDoubleBuffered(true);
        
        this.drawLabels((int) this.squareHeight);
        
        /*
        * this.resizeChessboard(imgHeight); must be called to avoid artifacts
        * during first tab creation. ( dirty hack a little )
        */
        this.resizeChessboard(imgHeight);
    }

    @Override
    public void unselect()
    {
        repaint();
    }/*--endOf-unselect--*/
    
    @Override
    public int getChessboardWidht()
    {
        return this.getChessboardWidht(false);
    }
    
    @Override
    public int getChessboardHeight()
    {
        return this.getChessboardHeight(false);
    }


    @Override
    public int getChessboardWidht(boolean includeLables)
    {
        return getHeight();
    }


    @Override
    public int getChessboardHeight(boolean includeLabels)
    {
        if (getChessboard().getSettings().isRenderLabels())
        {
            return image.getHeight(null) + getUpDownLabel().getHeight(null);
        }
        return image.getHeight(null);
    }
    
    @Override
    public int getSquareHeight()
    {
        int result = (int) this.squareHeight;
        return result;
    }
    
    @Override
    public Square getSquare(int clickedX, int clickedY)
    {
        if ((clickedX > this.getChessboardHeight()) || (clickedY > this.getChessboardWidht()))
        {
            LOG.debug("click out of chessboard.");
            return null;
        }
        if (getChessboard().getSettings().isRenderLabels())
        {
            clickedX -= this.getUpDownLabel().getHeight(null);
            clickedY -= this.getUpDownLabel().getHeight(null);
        }
        double squareX = clickedX / squareHeight;//count which field in X was clicked
        double squareY = clickedY / squareHeight;//count which field in Y was clicked

        if (squareX > (int) squareX) //if X is more than X parsed to Integer
        {
            squareX = (int) squareX + 1;//parse to integer and increment
        }
        if (squareY > (int) squareY) //if X is more than X parsed to Integer
        {
            squareY = (int) squareY + 1;//parse to integer and increment
        }
        
        LOG.debug("square_x: " + squareX + " square_y: " + squareY);
        Square result = null; 
        try
        {
            result = this.getChessboard().getSquare((int)squareX - 1, (int)squareY - 1);
            if (getChessboard().getSettings().isUpsideDown())
            {
                int x = transposePosition(result.getPozX());
                int y = transposePosition(result.getPozY());
                result = getChessboard().getSquare(x, y);
            }
        }
        catch (ArrayIndexOutOfBoundsException exc)
        {
            LOG.error("!!Array out of bounds when getting Square with Chessboard.getSquare(int,int) : " + exc.getMessage());
            return null;
        }
        return result;
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Point topLeftPoint = this.getTopLeftPoint();
        Square[][] squares = getChessboard().getSquares();
        if (getChessboard().getSettings().isRenderLabels())
        {
            this.drawLabels();
            g2d.drawImage(this.getUpDownLabel(), 0, 0, null);
            g2d.drawImage(this.getUpDownLabel(), 0, image.getHeight(null) + topLeftPoint.y, null);
            g2d.drawImage(this.leftRightLabel, 0, 0, null);
            g2d.drawImage(this.leftRightLabel, image.getHeight(null) + topLeftPoint.x, 0, null);
        }
        g2d.drawImage(image, topLeftPoint.x, topLeftPoint.y, null);//draw an Image of chessboard
        drawPieces(squares, g2d);
        
        Square activeSquare = getChessboard().getActiveSquare();

        if (null != activeSquare) //if some square is active
        {
            drawActiveSquare(activeSquare, g2d, topLeftPoint, squares);
            drawLegalMoves(g2d, topLeftPoint);
        }
    }

    @Override
    public Point getTopLeftPoint()
    {
        if (getChessboard().getSettings().isRenderLabels())
        {
            return new Point(this.topLeft.x + this.getUpDownLabel().getHeight(null), this.topLeft.y + this.getUpDownLabel().getHeight(null));
        }
        return this.topLeft;
    }   
    
    @Override
    public final void resizeChessboard(int height)
    {
        if (0 != height)
        {
            BufferedImage resized = new BufferedImage(height, height, BufferedImage.TYPE_INT_ARGB_PRE);
            Graphics g = resized.createGraphics();
            g.drawImage(ChessboardView.orgImage, 0, 0, height, height, null);
            g.dispose();
            if (!getChessboard().getSettings().isRenderLabels()) 
            {
                /*if no labels, make chessboard larger
                 * call before fetching scaled instance
                 */
                height += 2 * (this.getUpDownLabel().getHeight(null));
            }        
            image = resized.getScaledInstance(height, height, 0);
            
            resized = new BufferedImage(height, height, BufferedImage.TYPE_INT_ARGB_PRE);
            g = resized.createGraphics();
            g.drawImage(image, 0, 0, height, height, null);
            g.dispose();            
            
            this.squareHeight = (float) (height / 8);
            if (getChessboard().getSettings().isRenderLabels()) 
            {
                /* if labels, make final size larger
                 * call after fetching scaled instance
                 */            
                height += 2 * (this.getUpDownLabel().getHeight(null));
            }        
            setSize(height, height);

            resized = new BufferedImage((int) squareHeight, (int) squareHeight, BufferedImage.TYPE_INT_ARGB_PRE);
            g = resized.createGraphics();
            g.drawImage(ChessboardView.orgAbleSquare, 0, 0, (int) squareHeight, (int) squareHeight, null);
            g.dispose();
            ChessboardView.ableSquare = resized.getScaledInstance((int) squareHeight, (int) squareHeight, 0);

            resized = new BufferedImage((int) squareHeight, (int)squareHeight, BufferedImage.TYPE_INT_ARGB_PRE);
            g = resized.createGraphics();
            g.drawImage(ChessboardView.orgSelSquare, 0, 0, (int) squareHeight, (int) squareHeight, null);
            g.dispose();
            ChessboardView.selSquare = resized.getScaledInstance((int) squareHeight, (int) squareHeight, 0);
            pieces2D.resize(getSquareHeight());
            this.drawLabels();
        }
    }

    protected void drawLabels()
    {
        this.drawLabels((int) this.squareHeight);
    }

    protected final void drawLabels(int squareHeight)
    {
        int minLabelHeight = 20;
        int labelHeight = (int) Math.ceil(squareHeight / 4);
        labelHeight = (labelHeight < minLabelHeight) ? minLabelHeight : labelHeight;
        int labelWidth =  (int) Math.ceil(squareHeight * 8 + (2 * labelHeight)); 
        BufferedImage uDL = new BufferedImage(
            labelWidth + minLabelHeight,
            labelHeight,
            BufferedImage.TYPE_3BYTE_BGR
        );
        Graphics2D graph2D = (Graphics2D) uDL.createGraphics();
        graph2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graph2D.setColor(Color.white);

        graph2D.fillRect(0, 0, labelWidth + minLabelHeight, labelHeight);
        graph2D.setColor(Color.black);
        graph2D.setFont(new Font("Arial", Font.BOLD, 12));
        int addX = (squareHeight / 2);
        
        if (getChessboard().getSettings().isRenderLabels())
        {
            addX += labelHeight;
        }

        if (!getChessboard().getSettings().isUpsideDown())
        {
            for (int i = 1; i <= LETTERS.length; i++)
            {
                graph2D.drawString(LETTERS[i - 1], (squareHeight * (i - 1)) + addX, 10 + (labelHeight / 3));
            }
        }
        else
        {
            int j = 1;
            for (int i = LETTERS.length; i > 0; i--, j++)
            {
                graph2D.drawString(LETTERS[i - 1], (squareHeight * (j - 1)) + addX, 10 + (labelHeight / 3));
            }
        }
        graph2D.dispose();
        setUpDownLabel(uDL);

        uDL = new BufferedImage(labelHeight, labelWidth + minLabelHeight, BufferedImage.TYPE_3BYTE_BGR);
        graph2D = (Graphics2D) uDL.createGraphics();
        graph2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graph2D.setColor(Color.white);
        //uDL2D.fillRect(0, 0, 800, 800);
        graph2D.fillRect(0, 0, labelHeight, labelWidth + minLabelHeight);
        graph2D.setColor(Color.black);
        graph2D.setFont(new Font("Arial", Font.BOLD, 12));

        if (getChessboard().getSettings().isUpsideDown())
        {
            for (int i = 1; i <= 8; i++)
            {
                graph2D.drawString(Integer.toString(i), 3 + (labelHeight / 3), (squareHeight * (i - 1)) + addX);
            }
        }
        else
        {
            int j = 1;
            for (int i = 8; i > 0; i--, j++)
            {
                graph2D.drawString(Integer.toString(i), 3 + (labelHeight / 3), (squareHeight * (j - 1)) + addX);
            }
        }
        graph2D.dispose();
        this.leftRightLabel = uDL;
    }    


    private void drawLegalMoves(Graphics2D g2d, Point topLeftPoint)
    {
        if (getChessboard().getSettings().isDisplayLegalMovesEnabled())
        {
            Set<Square> moves = getChessboard().getMoves();
            if (null != moves)
            {
                for (Iterator it = moves.iterator(); it.hasNext();)
                {
                    Square sq = (Square) it.next();
                    int ableSquarePosX = sq.getPozX();
                    int ableSquarePosY = sq.getPozY();
                    if (getChessboard().getSettings().isUpsideDown())
                    {
                        ableSquarePosX = transposePosition(ableSquarePosX);
                        ableSquarePosY = transposePosition(ableSquarePosY);
                    }
                    g2d.drawImage(ableSquare, 
                        (ableSquarePosX * (int) squareHeight) + topLeftPoint.x,
                        (ableSquarePosY * (int) squareHeight) + topLeftPoint.y, null
                    );
                }
            }
        }
    }

    private void drawActiveSquare(Square activeSquare, Graphics2D g2d, Point topLeftPoint, Square[][] squares)
    {
        int activeSquareX = activeSquare.getPozX();
        int activeSquareY = activeSquare.getPozY();
        if (getChessboard().getSettings().isUpsideDown())
        {
            activeSquareX = transposePosition(activeSquareX);
            activeSquareY = transposePosition(activeSquareY);
        }
        
        g2d.drawImage(
            selSquare, 
            ((activeSquareX) * (int) squareHeight) + topLeftPoint.x,
            ((activeSquareY) * (int) squareHeight) + topLeftPoint.y, null
        ); //draw image of selected square
        
        Square tmpSquare = squares[activeSquare.getPozX()][activeSquare.getPozY()];
        
        if (null != tmpSquare.piece)
        {
            Set<Square> moves = tmpSquare.getPiece().getAllMoves();
            this.getChessboard().setMoves(moves);
        }
    }

    private void drawPieces(Square[][] squares, Graphics2D g2d)
    {
        for (int i = 0; i < 8; i++) //drawPiecesOnSquares
        {
            for (int y = 0; y < 8; y++)
            {
                if (squares[i][y].getPiece() != null)
                {
                    int drawPosI = i;
                    int drawPosY = y;
                    if (getChessboard().getSettings().isUpsideDown())
                    {
                        drawPosI = transposePosition(drawPosI);
                        drawPosY = transposePosition(drawPosY);
                    }                    
                    Piece piece = squares[i][y].getPiece();
                    Image pieceImage = pieces2D.getImage(piece.getPlayer().getColor(), piece);
                    Pieces2D.draw(this, squares[i][y].getPiece(), drawPosI, drawPosY, g2d, pieceImage);
                }
            }
        }
    }

}
