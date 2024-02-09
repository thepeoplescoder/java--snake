package com.thepeoplescoder.snake.state;

import com.thepeoplescoder.snake.view.IoEngine;

/**
 * A class to represent the player's score.
 */
public class Score implements IoEngine.Drawable
{
    /** The numeric value of this {@link Score}. */
    private final long points;

    /**
     * @param points The numeric value of this {@link Score}.
     */
    private Score(long points)
    {
        this.points = points;
    }
    
    /**
     * Constructs a new initial {@link Score}.
     */
    public Score()
    {
        this(0);
    }
    
    /**
     * Adds (or subtracts) points from the {@link Score}.
     * @param points The amount of points to add.
     * @return This {@link Score} if the {@link Score} doesn't change, otherwise a new, updated {@link Score}.
     */
    public Score plus(int points)
    {
        return points == 0 ? this : new Score(this.points + points);
    }
    
    @Override
    public String toString()
    {
        return String.valueOf(points);
    }

    /**
     * Draws the representation of this {@link Score}.
     * @param io The {@link IoEngine} used to draw the score to the display.
     */
    public void draw(IoEngine io)
    {
        io.drawScore(this);
    }
}
