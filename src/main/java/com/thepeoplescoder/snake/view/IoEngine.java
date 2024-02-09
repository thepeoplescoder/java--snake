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
 * Ideally, I/O engines should be pluggable,
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
     * @param gv The {@link GameView} associated with this {@link IoEngine}.
     */
    protected IoEngine(GameView gv)
    {
        this.gameView = gv;
    }
    
    /**
     * @return The {@link GameView} associated with this {@link IoEngine}.
     */
    public GameView getGameView()
    {
        return gameView;
    }

    /**
     * @return The current {@link GameState}.
     */
    public GameState getGameState()
    {
        return getGameView().getGameState();
    }
    
    /**
     * @return The current {@link GameBoard}.
     */
    public GameBoard getGameBoard()
    {
        return getGameState().getBoard();
    }
    
    /**
     * Sets the current drawing color for this {@link IoEngine}.
     * @param color The new drawing color to use.
     * @return This {@link IoEngine}.
     */
    public abstract IoEngine setColor(Color color);
    
    /**
     * Draws the contents of a cell at the given position in {@link GameBoard} coordinates.
     * @param position The position of the cell in {@link GameBoard} coordinates.  Upon completion of this
     *                 method, the visual representation of this cell should be drawn to the {@link GameView}.
     */
    public abstract void drawCellAt(IntVector2 position);
    
    /**
     * Draws the score to the display.
     * @param score The {@link Score} to display.
     */
    public abstract void drawScore(Score score);

    /**
     * Draws the game over screen.
     */
    public abstract void drawGameOver();
    
    /**
     * Draws a {@link Drawable} object using this {@link IoEngine}.
     * @param d The {@link Drawable} object to draw.
     */
    public void draw(IoEngine.Drawable d)
    {
        d.draw(this);
    }

    /**
     * Draws a grid (optional operation).
     */
    public void drawGrid() {}
}