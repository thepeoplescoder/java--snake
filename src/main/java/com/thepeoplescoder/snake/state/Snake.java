package com.thepeoplescoder.snake.state;

import static java.util.stream.Collectors.toCollection;

import java.awt.Color;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import com.thepeoplescoder.snake.math.IntVector2;
import com.thepeoplescoder.snake.Shared;
import com.thepeoplescoder.snake.view.IoEngine;

/**
 * This class represents the snake.
 * All methods that change the snake create a new Snake object.
 */
public class Snake implements IoEngine.Drawable, GameState.Movable
{
    /** This {@link Snake}'s current direction.  This is effectively its velocity vector. */
    private final IntVector2 direction;
    
    /** This {@link Snake}'s head position. */
    private final IntVector2 head;
    
    /**
     * The number of growth steps (by one segment) remaining that this {@link Snake} must go through.
     * @see #move(IntVector2)
     * */
    private final int growthStepsRemaining;
    
    /**
     * <p>The positions of all parts of this {@link Snake}'s tail.</p>
     * <p>It is a hard requirement that this be a {@link java.util.LinkedHashSet},
     * as the tail should not consist of duplicate positions, but the order of
     * insertion of segments (defined by {@link #move(IntVector2)}) must be maintained.</p>
     * 
     * @see #move(IntVector2)
     */
    private final LinkedHashSet<IntVector2> tail;

    /**
     * Creates a baby (two-segment) {@link Snake}.
     * @param direction The direction in which it will travel.
     * @param head The head position.
     * @param tail The tail position.
     */
    public static Snake baby(IntVector2 direction, IntVector2 head, IntVector2 tail)
    {
        return new Snake(direction, head, new LinkedHashSet<>(Collections.singleton(tail)));
    }

    /**
     * Constructor for a 2 or more segment {@link Snake}.
     * @param direction The direction it will travel.
     * @param head The head position.
     * @param tail The positions of all parts of the tail.
     */
    private Snake(IntVector2 direction, IntVector2 head, LinkedHashSet<IntVector2> tail)
    {
        this(direction, head, tail, 0);
    }

    /**
     * Constructor for a 2 or more segment {@link Snake}, that may be in the process of growing.
     * @param direction The direction it will travel.
     * @param head The head position.
     * @param tail The positions of all parts of the tail.
     * @param growthStepsRemaining The number of steps in which the {@link Snake} will grow by one segment.
     */
    private Snake(IntVector2 direction, IntVector2 head, LinkedHashSet<IntVector2> tail, int growthStepsRemaining)
    {
        this.direction            = Objects.requireNonNull(direction, "direction cannot be null.");
        this.head                 = Objects.requireNonNull(head, "head cannot be null.");
        this.tail                 = Objects.requireNonNull(tail, "tail cannot be null.");
        this.growthStepsRemaining = growthStepsRemaining;
    }
    
    /**
     * Moves the {@link Snake} forward in the direction which it is traveling.
     * If the {@link Snake} is in the process of growing, it is done so in this method.
     * 
     * @return A {@link Snake} that is moved forward by one cell.<br>
     */
    public Snake move()
    {
        final int tailSegmentsToRemove              = growthStepsRemaining > 0 ? 0 : 1;
        final Stream<IntVector2> endOfNewTailStream = tail.stream().limit(tail.size() - tailSegmentsToRemove);
        final Stream<IntVector2> newTailStream      = Stream.concat(Stream.of(head), endOfNewTailStream);
        final LinkedHashSet<IntVector2> newTail     = newTailStream.collect(toCollection(LinkedHashSet::new));
        final IntVector2 newHead                    = getHead().plus(direction);

        return new Snake(direction, newHead, newTail, Math.max(growthStepsRemaining - 1, 0));
    }

    /**
     * Moves the snake forward n cells.
     * @param n The number of cells forward that the snake should move.
     * @return A new {@link Snake} that has moved forward n cells.
     */
    public Snake move(int n)
    {
        return moves(n).skip(n).findFirst().get();
    }

    /**
     * Gets a {@link Stream} of {@link Snake}s in all states of forward movement.
     * @param n The number of spaces to move forward.
     * @return A {@link Stream} of movement states.  The first element of the stream will
     *         be the {@link Snake} in its current state, the next element is the snake
     *         after moving forward 1 cell, then after moving forward 2 cells, up to n
     *         cells total.  The stream will contain {@code Math.max(n + 1, 1)} elements.
     */
    public Stream<Snake> moves(int n)
    {
        return n < 1 ? Stream.of(this) : Stream.iterate(this, Snake::move).limit(n + 1);
    }

    /**
     * Creates a potentially new {@link Snake} who shall move in the given direction.
     * @param direction The new direction to move the {@link Snake} in.
     * @return If the move is valid, a new {@link Snake}, otherwise, the same {@link Snake} (direction not changed).
     *         Note that a {@link Snake} is allowed to make a move that would result in a game over.<br>
     *         In this context, a move that is "not allowed" or "not valid" refers to a move that doesn't
     *         make sense for the {@link Snake}.
     */
    public Snake withDirection(IntVector2 direction)
    {
        return isValidDirection(direction) ? new Snake(direction, head, tail, growthStepsRemaining) : this;
    }

    /**
     * Checks to see if changing the {@link Snake}'s direction to the given direction makes sense.
     * @param newDirection The direction to check.
     * @return {@code true} if changing the direction to {@code newDirection} makes sense, {@code false} otherwise.
     */
    public boolean isValidDirection(IntVector2 newDirection)
    {
        return getDirection().isPerpendicularTo(newDirection);
    }

    /**
     * @param numSteps The number of growth steps that the {@link Snake} must endure.
     * @return A new {@link Snake} instance who will be directed to grow the given number of steps/segments.
     */
    public Snake growBy(int numSteps)
    {
        return numSteps > 0 ? new Snake(direction, head, tail, growthStepsRemaining + numSteps) : this;
    }
    
    /**
     * Checks to see if the given position is one that this {@link Snake} is currently occupying.
     * @param position The position to check.
     * @return {@code true} if this {@link Snake} occupies the position, {@code false} otherwise.
     */
    public boolean contains(IntVector2 position)
    {
        return head.equals(position) || tail.contains(position);
    }

    /**
     * Did the {@link Snake} crash into itself?
     * @return {@code true} if it did, otherwise {@code false}.
     */
    public boolean isCrashedIntoSelf()
    {
        return getTail().contains(getHead());
    }

    /**
     * @return The direction that the {@link Snake} is currently traveling.
     *         This is essentially the {@link Snake}'s velocity vector.
     */
    public IntVector2 getDirection()
    {
        return direction;
    }
    
    /**
     * @return The head position of the {@link Snake}.
     */
    public IntVector2 getHead()
    {
        return head;
    }

    /**
     * A synonym for {@link Snake#getHead()}.
     * @return The head position of the {@link Snake}.
     */
    public IntVector2 getPosition()
    {
        return getHead();
    }
    
    /**
     * @return All current tail positions of the {@link Snake},
     *         as a {@link Set} of {@link IntVector2}s.
     */
    public Set<IntVector2> getTail()
    {
        return tail;
    }

    /**
     * Draws the representation of this {@link Snake}.
     * 
     * @param io The {@link IoEngine} object by which the drawing will be done.
     */
    @Override
    public void draw(IoEngine io)
    {
        tail.stream().forEach(io.setColor(Snake.tailColor)::drawCellAt);
        io.setColor(Snake.headColor).drawCellAt(head);
    }
    
    /** The color of this {@link Snake}'s tail. */
    private static final Color tailColor = Shared.Colors.snakeTail;
    
    /** The color of this {@link Snake}'s head. */
    private static final Color headColor = Shared.Colors.snakeHead;
}