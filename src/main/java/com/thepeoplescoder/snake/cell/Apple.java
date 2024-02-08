package com.thepeoplescoder.snake.cell;

import java.awt.Color;

import com.thepeoplescoder.snake.math.IntVector2;
import com.thepeoplescoder.snake.Shared;
import com.thepeoplescoder.snake.state.GameState;
import com.thepeoplescoder.snake.view.IoEngine;

/**
 * A class representing an apple.<br>
 * Eating apples causes the {@link Snake} to grow.
 * Eating a set number of apples advances the player to the next level.
 */
public class Apple extends Cell
{
    /** The {@link Apple}'s position on the {@link GameBoard}. */
    private final IntVector2 position;
    /** The amount of points that this {@link Apple} is worth. */
    private final int points;
    /** The number of growth steps a {@link Snake} will go through upon eating this {@link Apple}. */
    private final int growthAmount;

    /**
     * Constructs a new {@link Apple}.  Deliberately private to avoid direct
     * instantiation.
     * @param builder The {@link Apple.Builder} used to build this {@link Apple}.
     * @see Apple.Builder
     */
    private Apple(Apple.Builder builder)
    {
        position     = builder.position;
        points       = builder.points;
        growthAmount = builder.growthAmount;
    }

    /**
     * The position of this {@link Apple} on the {@link GameBoard}.
     */
    public IntVector2 getPosition()
    {
        return position;
    }

    /**
     * @return The number of points this {@link Apple} is worth.
     */
    public int getPoints()
    {
        return points;
    }

    /**
     * @return The number of growth steps a {@link Snake} will go through
     *         as a result of eating this {@link Apple}.
     */
    public int getGrowthAmount()
    {
        return growthAmount;
    }

    /**
     * @return {@code true}, because {@link Apple}s are always safe.
     */
    @Override
    public boolean isSafe()
    {
        return true;
    }

    /**
     * @param gs The {@link GameState} upon the {@link Snake} touching this {@link Apple}.
     * @return
     */
    @Override
    public GameState onTouch(GameState gs)
    {
        return gs.queueGameEvent(_gs -> {
            _gs.getBoard().removeCell(getPosition());
            _gs.getBoard().put(Apple.with()
                    .positionAs(_gs.getRandomEmptyCell())
                    .pointsAs(getPoints())
                    .growthAs(getGrowthAmount())
                .make());
            return GameState.from(_gs)
                    .scoreAs(score -> score.plus(getPoints()))
                    .snakeAs(snake -> snake.growBy(getGrowthAmount()))
                .make();
        });
    }

    /**
     * Draws this {@link Apple} using the given {@link IoEngine}.
     * @param io The {@link IoEngine} used for drawing this {@link Apple}.
     */
    @Override
    public void draw(IoEngine io)
    {
        io.setColor(Apple.color).drawCellAt(getPosition());
    }

    /** The color of each {@link Apple}. */
    private static final Color color = Shared.Colors.apple;

    /**
     * @return An {@link Apple.Builder} to build an {@link Apple}.
     * @see Apple.Builder
     */
    public static Apple.Builder with() { return new Apple.Builder(); }

    /**
     * A class used to create {@link Apple}s.
     * The builder pattern is used for readability over using a constructor directly.
     */
    public static class Builder
    {
        /** The {@link Apple}'s position on the {@link GameBoard}. */
        private IntVector2 position = IntVector2.of(-1, -1).cache();
        /** The amount of points that this {@link Apple} is worth. */
        private int points = 0;
        /** The number of growth steps a {@link Snake} will go through upon eating this {@link Apple}. */
        private int growthAmount = 0;

        /**
         * Default constructor.  Deliberately private to disallow direct instantiation.
         * @see #build()
         * */
        private Builder() {}

        /**
         * @return A new {@link Apple} with the properties of this {@link Builder}.
         */
        public Apple make() { return new Apple(this); }

        /**
         * @param position The position the {@link Apple} will have upon building.
         * @return This {@link Builder}.
         */
        public Builder positionAs(IntVector2 position) { this.position = position; return this; }

        /**
         * @param points The number of points the {@link Apple} will be worth.
         * @return This {@link Builder}.
         */
        public Builder pointsAs(int points) { this.points = points; return this; }

        /**
         * @param growthAmount The number of steps by which the {@link Snake} will grow,
         *                     as a result of eating this {@link Apple}.
         * @return This {@link Builder}.
         */
        public Builder growthAs(int growthAmount) { this.growthAmount = growthAmount; return this; }
    }
}
