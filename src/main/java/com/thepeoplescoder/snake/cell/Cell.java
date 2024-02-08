package com.thepeoplescoder.snake.cell;

import com.thepeoplescoder.snake.math.IntVector2;
import com.thepeoplescoder.snake.state.GameState;
import com.thepeoplescoder.snake.view.IoEngine;

/**
 * Represents the concept of a cell on the {@link GameBoard}.
 * It cannot be instantiated, as it is an abstract class.
 */
public abstract class Cell implements IoEngine.Drawable
{
    /**
     * @return The position of this {@link Cell}.  It is stored
     *         so that {@link IoEngine.Drawable#draw(IoEngine)} can use it.
     */
    public abstract IntVector2 getPosition();
    
    /**
     * Is this {@link Cell} safe?
     * @return {@code true} if it is, otherwise {@code false}.
     */
    public abstract boolean isSafe();
    
    /**
     * Code that gets executed when the {@link Snake} interacts with this {@link Cell}.
     * @param gs The current {@link GameState}.
     * @return The new {@link GameState} as a result of interacting with this {@link Cell}.
     */
    public abstract GameState onTouch(GameState gs);

    /**
     * Represents the concept of the empty {@link Cell}.
     */
    public static final Cell EMPTY = new Cell()
    {
        /** @return A meaningless position. */
        @Override public IntVector2 getPosition() { return IntVector2.of(-1, -1).cache(); }
        
        /** A drawing handler that does nothing, as empty cells do not need to be drawn. */
        @Override public void draw(IoEngine __) {}
        
        /** @return {@code true}, because empty {@link Cell}s are always safe. */
        @Override public boolean isSafe() { return true; }
        
        /** @return The current {@link GameState}, as empty {@link Cell}s don't do anything. */
        @Override public GameState onTouch(GameState gs) { return gs; }
    };
}
