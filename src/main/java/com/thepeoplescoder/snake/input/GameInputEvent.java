package com.thepeoplescoder.snake.input;

import java.util.function.Function;
import java.util.function.Predicate;

import com.thepeoplescoder.snake.math.IntVector2;
import com.thepeoplescoder.snake.state.GameState;

/**
 * A class that represents a handler for a specific input event in the game.
 */
public class GameInputEvent
{
    public static boolean doesNothing(GameInputEvent inputEvent)
    {
        return inputEvent == null || inputEvent == GameInputEvent.noAction;
    }

    public static final GameInputEvent noAction = GameInputEvent.withHandler(Function.identity());

    /**
     * A namespace for input actions and their corresponding handlers.
     * This style of handling events is used in order to decouple the raw
     * input handling (i.e. Swing, SWT, terminal, etc) from the game logic
     * itself.
     */
    public static class Action
    {
        /**
         * The handler for when the player wishes to move up.
         */
        public static final GameInputEvent moveUp = GameInputEvent
            .duringUnpausedGameplay(gs -> gs.withSnake(gs.getSnake().withDirection(IntVector2.MINUS_J)));

        /**
         * The handler for when the player wishes to move down.
         */
        public static final GameInputEvent moveDown = GameInputEvent
            .duringUnpausedGameplay(gs -> gs.withSnake(gs.getSnake().withDirection(IntVector2.J)));

        /**
         * The handler for when the player wishes to move left.
         */
        public static final GameInputEvent moveLeft = GameInputEvent
            .duringUnpausedGameplay(gs -> gs.withSnake(gs.getSnake().withDirection(IntVector2.MINUS_I)));

        /**
         * The handler for when the player wishes to move right.
         */
        public static final GameInputEvent moveRight = GameInputEvent
            .duringUnpausedGameplay(gs -> gs.withSnake(gs.getSnake().withDirection(IntVector2.I)));

        /**
         * The handler for when the player wishes to play again.
         */
        public static final GameInputEvent playAgain = GameInputEvent
            .withConditionalHandler(GameState::isGameOver, gs -> GameState.initial());

        /**
         * The handler for when the player wishes to exit the game.
         */
        public static final GameInputEvent quitGame = GameInputEvent
            .withHandler(GameState::done);

        /**
         * The handler for when the player wishes to pause/unpause the game.
         */
        public static final GameInputEvent togglePaused = GameInputEvent
            .duringGameplay(GameState::togglePaused);
    }

    /**
     * A class that acts as a namespace for predefined handlers.
     */
    public static class Handlers
    {
        public static final Function<? super GameState, ? extends GameState> notImplemented = gs -> {
            throw new UnsupportedOperationException("handler not implemented yet.");
        };
    }

    /**
     * This holds the handler associated with the input action.
     */
    private final Function<? super GameState, ? extends GameState> handler;

    /**
     * Constructs a {@link GameInputEvent}.
     * @param handler A {@link java.util.function.Function} that takes a {@link GameState}
     *                and returns a {@link GameState}.
     */
    private GameInputEvent(Function<? super GameState, ? extends GameState> handler)
    {
        this.handler = handler;
    }
    
    /**
     * Creates a new {@link GameInputEvent} with an associated handler.
     * @param handler A {@link java.util.function.Function} that accepts a {@link GameState} and returns a {@link GameState}, to handle this event.
     * @return A newly created {@link GameInputEvent} with the associated {@code handler}.
     */
    public static GameInputEvent duringUnpausedGameplay(Function<? super GameState, ? extends GameState> handler)
    {
        return duringGameplay(gs -> gs.isPaused() ? gs : handler.apply(gs));
    }
    
    public static GameInputEvent duringGameplay(Function<? super GameState, ? extends GameState> handler)
    {
        return withConditionalHandler(gs -> !gs.isTerminalState(), handler);
    }
    
    public static GameInputEvent withConditionalHandler(Predicate<? super GameState> condition, Function <? super GameState, ? extends GameState> handler)
    {
        return withHandler(gs -> condition.test(gs) ? handler.apply(gs) : gs);
    }
    
    public static GameInputEvent withHandler(Function<? super GameState, ? extends GameState> handler)
    {
        return new GameInputEvent(handler);
    }
    
    /**
     * Applies this {@link GameInputEvent}'s handler to the given {@link GameState}.
     * @param gs The {@link GameState} to apply this {@link GameInputEvent}'s handler to.
     * @return The resulting {@link GameState}.
     */
    public GameState applyHandler(GameState gs)
    {
        return this.handler.apply(gs);
    }
}
