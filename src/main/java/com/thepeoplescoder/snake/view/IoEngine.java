package com.thepeoplescoder.snake.view;

import java.awt.Color;

import com.thepeoplescoder.snake.math.IntVector2;
import com.thepeoplescoder.snake.state.GameBoard;
import com.thepeoplescoder.snake.state.GameState;
import com.thepeoplescoder.snake.state.Score;

/**
 * A class representing the concept of an I/O engine.<p>
 * 
 * The I/O engine is responsible for displaying the visual
 * representation of the {@link GameState} to the player, and
 * handling the input from the user.<p>
 * 
 * Theoretically, I/O engines should be pluggable,
 * i.e. the game should run the same (but look different) with
 * an I/O engine used for displaying the game on a terminal,
 * or with an I/O engine used for displaying the game on a GUI
 * window.<p>
 */
public abstract class IoEngine
{
    /**
     * An interface for objects that can be drawn by a {@link IoEngine}.
     */
    @FunctionalInterface
    public static interface Drawable
    {
        void draw(IoEngine io);
    }

    /** The current {@link GameView}. */
    private final GameView gameView;
    
    /**
     * 
     * @param gs
     */
    protected IoEngine(GameView gv)
    {
        this.gameView = gv;
    }
    
    /**
     * @return
     */
    public GameView getGameView()
    {
        return gameView;
    }
    
    public GameState getGameState()
    {
        return getGameView().getGameState();
    }
    
    /**
     * 
     * @return
     */
    public GameBoard getGameBoard()
    {
        return getGameState().getBoard();
    }
    
    /**
     * 
     * @param color
     * @return
     */
    public abstract IoEngine setColor(Color color);
    
    /**
     * Draws the contents of a cell at the given position in {@link GameBoard} coordinates.
     * @param position The position of the cell in {@link GameBoard} coordinates.  Upon completion of this
     *                 method, the visual representation of this cell should be drawn to the {@link GameView}.
     */
    public abstract void drawCellAt(IntVector2 position);
    
    /**
     * 
     * @param score
     */
    public abstract void drawScore(Score score);
    
    public abstract void drawGameOver();
    
    /**
     * Draws a {@link Drawable} object using this {@link IoEngine}.
     * @param d The {@link Drawable} object to draw.
     */
    public void draw(IoEngine.Drawable d)
    {
        d.draw(this);
    }
    
    public abstract void drawGrid();
}