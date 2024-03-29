package com.thepeoplescoder.snake.state;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.thepeoplescoder.snake.Shared;
import com.thepeoplescoder.snake.cell.Cell;
import com.thepeoplescoder.snake.cell.Wall;
import com.thepeoplescoder.snake.math.IntVector2;
import com.thepeoplescoder.snake.view.IoEngine;

/**
 * This represents the game board.<p>
 * 
 * This is a mutable object.
 */
public class GameBoard implements IoEngine.Drawable
{
    /** The random number generator for all {@link GameBoard}s.*/
    private static final Random random = Shared.random;

    /** A map of all nonempty {@link Cell}s. */
    private final Map<IntVector2, Cell> cells = new HashMap<>();

    /** The size of this {@link GameBoard} in {@link Cell} dimensions. */
    private final IntVector2 size;

    /**
     * Constructs a {@link GameBoard}.
     * @param size The dimensions of the {@link GameBoard}, in {@link Cell}s, as an {@link IntVector2}.
     * @param wallPositions A {@link Set} of {@link IntVector2}s specifying the positions of all of the 
     *                      {@link Wall}s on the {@link GameBoard}.
     */
    public GameBoard(IntVector2 size, Set<IntVector2> wallPositions)
    {
        this.size = Objects.requireNonNull(size, "size cannot be null.");
        Objects.requireNonNull(wallPositions, "wallPositions cannot be null.")
            .stream().forEach(position -> putCell(new Wall(position)));
    }

    /**
     * 
     * @param size
     * @return
     */
    public static Set<IntVector2> boundingWalls(IntVector2 size)
    {
        Set<IntVector2> walls = new HashSet<>();
        IntStream.range(0, size.getX()).forEach(x -> {
            walls.add(IntVector2.of(x, 0));
            walls.add(IntVector2.of(x, size.getY() - 1));
        });
        IntStream.range(1, size.getY() - 1).forEach(y -> {
            walls.add(IntVector2.of(0, y));
            walls.add(IntVector2.of(size.getX() - 1, y));
        });
        return walls;
    }

    /**
     * @return The size of this {@link GameBoard} in {@link Cell} dimensions.
     */
    public IntVector2 getSize()
    {
        return size;
    }
    
    /**
     * @return The width of this {@link GameBoard} in {@link Cell} dimensions.
     * @see #getSize()
     * @see IntVector2#getX()
     */
    public int getWidth()
    {
        return getSize().getX();
    }
    
    /**
     * @return The height of this {@link GameBoard} in {@link Cell} dimensions.
     * @see #getSize()
     * @see IntVector2#getY()
     */
    public int getHeight()
    {
        return getSize().getY();
    }
    
    /**
     * @return A random x position within the bounds of this {@link GameBoard}.
     * @see java.util.Random#nextInt(int)
     * @see #getWidth()
     */
    public int getRandomX()
    {
        return GameBoard.random.nextInt(getWidth());
    }
    
    /**
     * @return A random y position within the bounds of this {@link GameBoard}.
     * @see java.util.Random#nextInt(int)
     * @see #getHeight()
     */
    public int getRandomY()
    {
        return GameBoard.random.nextInt(getHeight());
    }

    /**
     * @return A random {@link IntVector2} within the bounds of this {@link GameBoard}.
     */
    public IntVector2 getRandomVector()
    {
        return IntVector2.of(getRandomX(), getRandomY());
    }

    /**
     * Is this X value in bounds?
     * @param x The x value to check.
     * @return {@code true} if it is, otherwise {@code false}.
     */
    private boolean _isInBoundsX(int x)
    {
        return 0 <= x && x < getWidth();
    }

    /**
     * Is this Y value in bounds?
     * @param y The y value to check.
     * @return {@code true} if it is, otherwise {@code false}.
     */
    private boolean _isInBoundsY(int y)
    {
        return 0 <= y && y < getHeight();
    }

    /**
     * Is this position in bounds?
     * @param pos The position to check, as an {@link IntVector2}.
     * @return {@code true} if it is, otherwise {@code false}.
     */
    public boolean isInBounds(IntVector2 pos)
    {
        return _isInBoundsX(pos.getX()) && _isInBoundsY(pos.getY());
    }

    /**
     * Throws an exception if the coordinates are out of bounds.
     * @param x The x coordinate to check.
     * @param y The y coordinate to check.
     * @return If the coordinates are in bounds, this {@link GameBoard}.
     * @throws IndexOutOfBoundsException
     */
    private GameBoard _thisOrThrowIfOutOfBounds(int x, int y)
    {
        if (!_isInBoundsX(x))
        {
            throw new IndexOutOfBoundsException(String.format("x is %d - allowed range is 0 to %d", x, getWidth() - 1));
        }
        if (!_isInBoundsY(y))
        {
            throw new IndexOutOfBoundsException(String.format("y is %d - allowed range is 0 to %d", y, getHeight() - 1));
        }
        return this;
    }

    /**
     * Throws an exception if the coordinates are out of bounds.
     * @param pos The position to check, as an {@link IntVector2}.
     * @return If the coordinates are in bounds, this {@link GameBoard}.
     * @throws IndexOutOfBoundsException
     */
    private GameBoard _thisOrThrowIfOutOfBounds(IntVector2 pos)
    {
        Objects.requireNonNull(pos, "position vector cannot be null.");
        return _thisOrThrowIfOutOfBounds(pos.getX(), pos.getY());
    }

    /**
     * Puts the given cell on this {@link GameBoard}.
     * @param cell The {@link Cell} to put on the board.
     */
    public void putCell(Cell cell)
    {
        final IntVector2 pos = cell.getPosition();
        if (cell != Cell.EMPTY)
        {
            _thisOrThrowIfOutOfBounds(pos).cells.put(pos, cell);
        }
        else
        {
            removeCell(pos);
        }
    }

    /**
     * Gets the requested cell from the board.
     * @param pos The board position, as an {@link IntVector2}.
     * @return The {@link Cell} at the requested position.
     * @throws IndexOutOfBoundsException
     */
    public Cell getCell(IntVector2 pos)
    {
        return _thisOrThrowIfOutOfBounds(pos).cells.getOrDefault(pos, Cell.EMPTY);
    }

    /**
     * Removes the cell from the board at the requested position.
     * @param pos The board position, as an {@link IntVector2}.
     */
    public void removeCell(IntVector2 pos)
    {
        _thisOrThrowIfOutOfBounds(pos).cells.remove(pos);
    }

    /**
     * Gets the requested cell from the {@link GameBoard}.
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @return The {@link Cell} at the requested position.
     */
    public Cell getCell(int x, int y)
    {
        return getCell(IntVector2.of(x, y));
    }
    
    public void removeCell(int x, int y)
    {
        removeCell(IntVector2.of(x, y));
    }
    
    public boolean isWall(int x, int y)
    {
        return isWall(IntVector2.of(x, y));
    }
    
    public boolean isWall(IntVector2 pos)
    {
        return getCell(pos) instanceof Wall;
    }
    
    public void draw(IoEngine io)
    {
        cells.entrySet().stream().map(e -> e.getValue()).forEach(io::draw);
    }
    
    public boolean isEmptyCell(IntVector2 pos)
    {
        return isInBounds(pos) && getCell(pos) == Cell.EMPTY;
    }

    /**
     * Creates an initial snake for the board.
     * The {@link Snake} returned is guaranteed to be positioned and moving
     * in a direction such that it won't immediately crash into an obstacle.
     * @return The initial {@link Snake}.
     */
    public Snake babySnake()
    {
        // Used to generate snake tails.
        final Supplier<IntVector2> nextTail =
            () -> Stream.generate(this::getRandomVector).filter(this::isEmptyCell).findFirst().get();

        // Converts a snake tail to a stream of snakes facing in all four possible directions.
        final Function<IntVector2, Stream<Snake>> tailToPotentialBabySnakes =
            tail -> IntVector2.DIRECTIONS.stream().map(direction -> Snake.baby(direction, tail.plus(direction), tail));

        // Used to generate a list of states for a snake.
        // These state list consist of elements where element 0 is the initial position of the snake,
        // element 1 is the state after moving the snake forward by one, element 2 is the state after moving
        // the snake forward by two, and so on.
        final Supplier<Collection<List<GameState>>> nextCollectionOfCardinalDirectionStateLists =
            () -> tailToPotentialBabySnakes.apply(nextTail.get())
                .map( snake      -> snake.moves(Shared.Settings.Game.initialSafetySpaces) )
                .map( snakeMoves -> GameState.stateListFrom(this, snakeMoves)             )
                .collect(Collectors.toList());

        // A state list is good if the moved snake in any state doesn't result in a game over.
        final Predicate<List<GameState>> isGoodStateList = sl -> !sl.stream().anyMatch(GameState::isGameOver);
        final Predicate<Collection<List<GameState>>> hasGoodStateList =
            collectionOfStateLists -> collectionOfStateLists.stream().anyMatch(isGoodStateList);

        // Now we have enough information to choose a snake that won't immediately cause a game over.
        List<Snake> possibleSnakes = Stream.generate(nextCollectionOfCardinalDirectionStateLists)
            .filter(hasGoodStateList)
            .findFirst()
            .get().stream()
            .filter(isGoodStateList)
            .map(stateList -> stateList.get(0).getSnake())
            .collect(Collectors.toList());

        // Pick a random one out of the bunch.
        return possibleSnakes.get(GameBoard.random.nextInt(possibleSnakes.size()));
    }
}
