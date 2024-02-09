package com.thepeoplescoder.snake.state;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.thepeoplescoder.snake.math.IntVector2;
import com.thepeoplescoder.snake.Shared;
import com.thepeoplescoder.snake.cell.Apple;
import com.thepeoplescoder.snake.cell.Cell;
import com.thepeoplescoder.snake.input.GameInputEvent;
import com.thepeoplescoder.snake.view.IoEngine;

/**
 * This class represents a particular state of the game
 * at an arbitrary moment in time.<p>
 * 
 * The game state can only be advanced by creating new {@link GameState} objects.<p>
 * 
 * Any mutable objects that the state holds onto, get mutated by posting events to
 * a game event queue, which then gets processed by {@link #nextState()}.
 */
public class GameState implements IoEngine.Drawable
{
    /**
     * This interface represents a movable object within the context of the game.
     */
    @FunctionalInterface
    public static interface Movable
    {
        Movable move();
    }

    /**
     * The random number generator for our game.
     */
    public static final Random random = Shared.random;

    /** The game board for the current state. */
    private final GameBoard board;
    /** The snake for the current state. */
    private final Snake snake;
    /** The score for the current state. */
    private final Score score;
    /** The level for the currnet state. */
    private final int level;
    /** The number of apples remaining to reach the next level. */
    private final int applesRemaining;

    /** This is {@code true} if the game loop should end, and the application should terminate. */
    private final boolean done;
    /** This is {@code true} if the game is paused, otherwise it is {@code false} */
    private final boolean paused;

    /** The little game over message. */
    private final String littleGameOverMessage;

    /** The input queue for the entire game.  It gets passed from state to state. */
    private final Queue<GameInputEvent> inputQueue;
    /** The queue for other game events that modify mutable state. It gets passed from state to state.*/
    private final Queue<Function<? super GameState, ? extends GameState>> gameEventQueue;

    /**
     * General constructor for a {@code GameState}.
     * Deliberately private to avoid direct instantiation.
     * 
     * @param done This is {@code true} if the application should stop running, otherwise it is {@code false}.
     * @param board The {@link GameBoard} instance represented by this state.  It may be passed from
     *              state object to state object.
     * @param snake The {@link Snake} instance represented by this state.
     * @param score The {@link Score} instance represented by this state.
     * @param inputQueue The input {@link Queue}.  This object gets passed to the next state.
     * @param gameEventQueue The game event {@link Queue} consisting of {@link Runnable}s that should take place when changing state.
     */
    private GameState(GameState.Builder gsb)
    {
        Objects.requireNonNull(gsb.inputQueue, "input queue cannot be null.");
        Objects.requireNonNull(gsb.gameEventQueue, "game event queue cannot be null.");

        if (gsb.paused) { gsb.inputQueue.clear(); }

        this.done                  = gsb.done;
        this.paused                = gsb.paused;
        this.board                 = Objects.requireNonNull(gsb.board, "board cannot be null.");
        this.snake                 = Objects.requireNonNull(gsb.snake, "snake cannot be null.");
        this.score                 = Objects.requireNonNull(gsb.score, "score cannot be null.");
        this.level                 = gsb.level;
        this.applesRemaining       = gsb.applesRemaining;
        this.inputQueue            = gsb.inputQueue;
        this.gameEventQueue        = gsb.gameEventQueue;
        this.littleGameOverMessage = gsb.littleGameOverMessage;
    }
    
    public String getLittleGameOverMessage()
    {
        return littleGameOverMessage;
    }
    
    public static GameState startWith(GameBoard board, Snake snake)
    {
        return GameState.with().boardAs(board).snakeAs(snake).aNewScore()
            .anEmptyInputQueue().anEmptyGameEventQueue()
            .make();
    }
    
    public static GameState startWith(GameBoard board)
    {
        return GameState.startWith(board, board.babySnake());
    }
    
    public static GameState startWith(IntVector2 size, Set<IntVector2> walls)
    {
        return GameState.startWith(new GameBoard(size, walls));
    }
    
    public static List<GameState> stateListFrom(GameBoard board, Stream<Snake> snakes)
    {
        return snakes.map(snake -> GameState.startWith(board, snake)).collect(Collectors.toList());
    }
    
    /**
     * @return The initial {@link GameState}.
     */
    public static GameState initial()
    {
        IntVector2 size = IntVector2.of(40, 40).cache();
        IntVector2 center = IntVector2.of(size.getX() / 2, size.getY() / 2);

        Set<IntVector2> walls = GameBoard.boundingWalls(size);

        BiConsumer<IntVector2, Integer> placePlusOnBoard =
            (pos, length) -> {
                walls.add(pos);
                IntStream.range(1, length).forEach(n ->
                    IntVector2.DIRECTIONS.stream()
                        .map(d -> d.times(n).plus(pos))
                        .forEach(walls::add));
            };

        placePlusOnBoard.accept(center, 10);
        placePlusOnBoard.accept(center.plus(-10, -10), 5);
        placePlusOnBoard.accept(center.plus(10, -10), 5);
        placePlusOnBoard.accept(center.plus(-10, 10), 5);
        placePlusOnBoard.accept(center.plus(10, 10), 5);

        return GameState.startWith(size, walls).call(_gs -> {
            _gs.getBoard().putCell(Apple.with().positionAs(_gs.getRandomEmptyCell())
                .pointsAs(100)
                .growthAmountAs(Shared.Settings.Game.growthStepsPerApple)
                .make());
        });
    }
    
    public GameState call(Function<? super GameState, ? extends GameState> f)
    {
        return f.apply(this);
    }
    
    public GameState call(Consumer<? super GameState> f)
    {
        f.accept(this);
        return this;
    }

    /**
     * @return An {@link IntVector2} representing an unoccupied position on the {@link GameBoard}.
     */
    public IntVector2 getRandomEmptyCell()
    {
        return Stream.generate(getBoard()::getRandomVector)
            .filter(pos -> getBoard().getCell(pos) == Cell.EMPTY && !getSnake().contains(pos))
            .findFirst().get();
    }
    
    /**
     * @return The {@link GameBoard} associated with this {@link GameState}.
     */
    public GameBoard getBoard()
    {
        return board;
    }
    
    /**
     * @return The {@link Snake} associated with this {@link GameState}.
     */
    public Snake getSnake()
    {
        return snake;
    }
    
    /**
     * @return The {@link Score} associated with this {@link GameState}.
     */
    public Score getScore()
    {
        return score;
    }

    public int getLevel()
    {
        return level;
    }

    public int getApplesRemaining()
    {
        return applesRemaining;
    }
    
    /**
     * Should the game loop (and application) terminate?
     * @return {@code true} if it should, otherwise {@code false}.
     */
    public boolean isDone()
    {
        return done;
    }
    
    public GameState togglePaused()
    {
        return done ? this : GameState.from(this).togglePaused().make();
    }
    
    public boolean isPaused()
    {
        return paused;
    }
    
    /**
     * @return A {@link GameState} suggesting that the application should terminate.
     */
    public GameState done()
    {
        return done ? this : GameState.from(this).doneAs(true).make();
    }
    
    /**
     * Is this a terminal state?
     * @return {@code true} if it is, otherwise {@code false}.
     * @see #isDone()
     * @see #isGameOver()
     */
    public boolean isTerminalState()
    {
        return isDone() || isGameOver();
    }
    
    /**
     * Is the game over?
     * @return {@code true} if it is, otherwise {@code false}.
     * @see #getSnake()
     * @see #getBoard()
     * @see Snake#isCrashedIntoSelf()
     * @see Snake#getHead()
     * @see GameBoard#isWall(IntVector2)
     */
    public boolean isGameOver()
    {
        return !getBoard().isInBounds(getSnake().getHead()) ||
               getSnake().isCrashedIntoSelf() ||
               getBoard().isWall(getSnake().getHead());
    }
    
    /**
     * Is the {@link GameState} consisting of the given {@link GameBoard}, {@link Snake}, and {@link Score}
     * the same as this {@link GameState} when the game is running and unpaused?
     * 
     * @param board The {@link GameBoard} of the new potential {@link GameState}.
     * @param snake The {@link Snake} of the new potential {@link GameState}.
     * @param score The {@link Score} of the new potential {@link GameState}.
     * @return {@code true} if these parameters already represent this {@link GameState}, otherwise {@code false}.
     */
    private boolean isSameUnpausedState(GameBoard board, Snake snake, Score score)
    {
        return this.board == board && this.snake == snake && this.score == score;
    }
    
    /**
     * Given the {@link GameBoard}, {@link Snake}, and {@link Score}, do we even
     * have to create a new {@link GameState} from this one?
     * 
     * @param board The {@link GameBoard} of the new potential {@link GameState}.
     * @param snake The {@link Snake} of the new potential {@link GameState}.
     * @param score The {@link Score} of the new potential {@link GameState}.
     * 
     * @return {@code true} if a new state needs to be created from this one, otherwise {@code false}.
     *         This method will also return {@code false} if this {@link GameState} is in a terminal state
     *         (determined by {@link #isTerminalState}).
     * @see #isTerminalState()
     * @see #isSameUnpausedState(GameBoard, Snake, Score)
     */
    private boolean isNewStateNeeded(GameBoard board, Snake snake, Score score)
    {
        return !isTerminalState() && !paused && !isSameUnpausedState(board, snake, score);
    }
    
    /**
     * A helper method to create the next state from its arguments.
     * 
     * @param board The {@link GameBoard} of the new potential {@link GameState}.
     * @param snake The {@link Snake} of the new potential {@link GameState}.
     * @param score The {@link Score} of the new potential {@link Score}.
     * 
     * @return A new {@link GameState} representative of the given arguments,
     *         if a new state needs to be created.<br>
     *         If a new state does <b>not</b> need to be created, then this
     *         {@link GameState} is returned.
     *         
     * @see #isNewStateNeeded(GameBoard, Snake, Score)
     */
    private GameState with(GameBoard board, Snake snake, Score score)
    {
        return isNewStateNeeded(board, snake, score)
            ? GameState.from(this).boardAs(board).snakeAs(snake).scoreAs(score).make() : this;
    }
    
    /**
     * @return The next {@link GameState}, after processing the game logic for this {@link GameState}.
     */
    public GameState nextState()
    {
        // Handle the current interaction event.
        GameState result = getNewStateFromSnakeTouchingCurrentCell();

        // Process the current game event queue in its entirety.
        result = processEntireCurrentGameEventQueueOn(result);

        if (isLevelPassed())
        {
            return result.nextLevel();
        }
        else
        {
            result = processAtMostOneInputEventOn(result);
            return result.withSnake(result.getSnake().move());
        }
    }

    public boolean isLevelPassed()
    {
        return getApplesRemaining() < 1;
    }

    public GameState nextLevel()
    {
        System.out.println("Levels are not implemented yet.");
        System.exit(0);
        return this;
    }

    public GameState processAtMostOneInputEventOn(GameState state)
    {
        return inputQueue.isEmpty() ? state : inputQueue.remove().applyHandler(state);
    }

    public GameState processEntireCurrentGameEventQueueOn(GameState state)
    {
        state = gameEventQueue.stream().reduce((f1, f2) -> f1.andThen(f2)).orElse(x -> x).apply(state);
        gameEventQueue.clear();
        return state;
    }

    /**
     * Potentially creates a new {@link GameState} with the given {@link Score}.
     * @param newScore The new {@link Score}.
     * @return This {@link GameState} if it can be used to represent the new {@link Score}, otherwise,
     *         a new {@link GameState} where the {@link Score} is updated from this {@link GameState}.
     */
    public GameState withScore(Score newScore)
    {
        return with(getBoard(), getSnake(), newScore);
    }
    
    /**
     * Potentially creates a new {@link GameState} with the given {@link Snake}.
     * @param newSnake The new {@link Snake}
     * @return This {@link GameState} if the {@link Snake} has not changed, otherwise a new {@link GameState}.
     */
    public GameState withSnake(Snake newSnake)
    {
        return with(getBoard(), newSnake, getScore());
    }
    
    /**
     * Calculates a new state by running {@link Cell#onTouch(GameState)} on the {@link Cell} at
     * the {@link Snake}'s current position.
     * @return A potentially new {@link GameState} from running the {@link Cell}'s event handler.
     */
    public GameState getNewStateFromSnakeTouchingCurrentCell()
    {
        return isTerminalState() ? this : getBoard().getCell(getSnake().getHead()).onTouch(this);
    }
    
    /**
     * Draws the visual representation of this {@link GameState}.
     * @param io The {@link IoEngine} used to draw the visual representation of this {@link GameState}.
     */
    @Override
    public void draw(IoEngine io)
    {
        if (isGameOver())
        {
            io.drawGameOver();
        }
        else
        {
            getBoard().draw(io);
            getSnake().draw(io);
            io.drawGrid();
        }
        getScore().draw(io);
    }
    
    /**
     * Sends an input event to the input queue to be handled by the game logic.
     * @param event The {@link GameInputEvent} to queue.
     */
    public void queueInputEvent(GameInputEvent event)
    {
        if (GameInputEvent.doesNothing(event)) { return; }
        inputQueue.add(event);
    }
    
    /**
     * 
     * @param event
     * @return
     */
    public void queueGameEvent(Function<? super GameState, ? extends GameState> event)
    {
        Function<? super GameState, ? extends GameState>
            onlyHandleTheEventIfTheGameIsNotInATerminalState =
                gs -> gs.isTerminalState() ? gs : event.apply(gs);
        gameEventQueue.add(onlyHandleTheEventIfTheGameIsNotInATerminalState);
    }

    /**
     * @return A {@link GameState.Builder} object.
     */
    public static GameState.Builder with()             { return new GameState.Builder();   }
    public static GameState.Builder from(GameState gs) { return new GameState.Builder(gs); }

    /**
     * Used for creating new {@link GameState}s.
     */
    public static class Builder
    {
        /** The game board for the current state. */
        private GameBoard board;
        /** The snake for the current state. */
        private Snake snake;
        /** The score for the current state. */
        private Score score;
        /** */
        private int level;
        /** */
        private int applesRemaining;
        /** The input queue for the entire game.  It gets passed from state to state. */
        private Queue<GameInputEvent> inputQueue;
        /** The queue for other game events that modify mutable state. It gets passed from state to state.*/
        private Queue<Function<? super GameState, ? extends GameState>> gameEventQueue;
        /** This is {@code true} if the game loop should end, and the application should terminate. */
        private boolean done;
        /** This is {@code true} if the game is paused, otherwise it is {@code false} */
        private boolean paused;
        /** The little game over message ;) */
        private String littleGameOverMessage;

        private Builder(GameBoard board, Snake snake, Score score,
                int level, int applesRemaining,
                Queue<GameInputEvent> inputQueue,
                Queue<Function<? super GameState, ? extends GameState>> gameEventQueue,
                boolean done, boolean paused,
                String littleGameOverMessage)
        {
            this.board = board;
            this.snake = snake;
            this.score = score;
            this.level = level;
            this.applesRemaining = applesRemaining;
            this.inputQueue = inputQueue;
            this.gameEventQueue = gameEventQueue;
            this.done = done;
            this.paused = paused;
            this.littleGameOverMessage = littleGameOverMessage;
        }

        private Builder()
        {
            this(null, null, null,
                1, Shared.Settings.Game.applesPerLevel,
                null,
                null,
                false, false,
                Shared.Messages.littleGameOverMessage());
        }

        private Builder(GameState gs)
        {
            this(gs.board, gs.snake, gs.score,
                gs.level, gs.applesRemaining,
                gs.inputQueue,
                gs.gameEventQueue,
                gs.done, gs.paused,
                gs.littleGameOverMessage);
        }

        public Builder boardAs(GameBoard board)               { this.board           = board;           return this; }
        public Builder snakeAs(Snake snake)                   { this.snake           = snake;           return this; }
        public Builder scoreAs(Score score)                   { this.score           = score;           return this; }
        public Builder levelAs(int level)                     { this.level           = level;           return this; }
        public Builder applesRemainingAs(int applesRemaining) { this.applesRemaining = applesRemaining; return this; }
        public Builder doneAs(boolean done)                   { this.done            = done;            return this; }
        public Builder pausedAs(boolean paused)               { this.paused          = paused;          return this; }
        
        public Builder boardAs(Function<? super GameBoard, ? extends GameBoard> f)
        {
            return boardAs(f.apply(this.board));
        }

        public Builder snakeAs(Function<? super Snake, ? extends Snake> f)
        {
            return snakeAs(f.apply(this.snake));
        }
        public Builder scoreAs(Function<? super Score, ? extends Score> f)
        {
            return scoreAs(f.apply(this.score));
        }
        public Builder levelAs(IntUnaryOperator f)
        {
            return levelAs(f.applyAsInt(this.level));
        }
        public Builder applesRemainingAs(IntUnaryOperator f)
        {
            return applesRemainingAs(f.applyAsInt(this.applesRemaining));
        }
        public Builder doneAs(Predicate<Boolean> f)
        {
            return doneAs(f.test(this.done));
        }
        public Builder pausedAs(Predicate<Boolean> f)
        {
            return pausedAs(f.test(this.paused));
        }

        public Builder anEmptyInputQueue()     { this.inputQueue     = new LinkedList<>(); return this; }
        public Builder anEmptyGameEventQueue() { this.gameEventQueue = new LinkedList<>(); return this; }

        public Builder aNewScore()    { return scoreAs(new Score());   }
        public Builder togglePaused() { return pausedAs(p -> !p); }

        /**
         * @return A new {@link GameState} from this {@link Builder}.
         */
        public GameState make() { return new GameState(this); }
    }
}