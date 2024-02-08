package com.thepeoplescoder.snake.cell;

import java.awt.Color;

import com.thepeoplescoder.snake.math.IntVector2;
import com.thepeoplescoder.snake.Shared;
import com.thepeoplescoder.snake.state.GameState;
import com.thepeoplescoder.snake.view.IoEngine;

/**
 * A class representing a wall.
 * If the player's {@link Snake} touches a wall, the game is over.
 */
public class Wall extends Cell
{
    /** The position of this {@link Wall}. */
    private final IntVector2 position;
    
    /**
     * Constructs a new {@link Wall}.
     * @param position
     */
    public Wall(IntVector2 position)
    {
        this.position = position;
    }
    
    /**
     * @return The position of this {@link Wall} on the {@link GameBoard}.
     */
    public IntVector2 getPosition()
    {
        return position;
    }

    /**
     * @param io The {@link IoEngine} used to draw this {@link Wall}.
     */
    @Override
    public void draw(IoEngine io)
    {
        io.setColor(Wall.color).drawCellAt(getPosition());
    }
    
    /** The color of every {@link Wall}. */
    private static final Color color = Shared.Colors.wall;

    /**
     * @return {@code false}, because {@link Wall}s are always unsafe.
     */
    @Override
    public boolean isSafe()
    {
        return false;
    }

    /**
     * @param gs The {@link GameState} upon touching this {@link Wall}.
     * @return The {@link GameState} as a result of touching this {@link Wall}.<br>
     *         As of right now, nothing happens, since touching a wall is detected by state variables.
     */
    @Override
    public GameState onTouch(GameState gs)
    {
        return gs;
    }
}
