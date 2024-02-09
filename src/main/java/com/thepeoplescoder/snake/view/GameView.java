package com.thepeoplescoder.snake.view;

import com.thepeoplescoder.snake.state.GameState;

/**
 * 
 */
public abstract class GameView
{
    public abstract IoEngine getIoEngine();
    public abstract void displayStateSequence();

    /**
     * 
     * @param initialState
     */
    public GameView(GameState initialState)
    {
        setGameState(initialState);
    }

    /**
     * The current game state.
     */
    private GameState gameState;

    /**
     * @return The {@link GameState} associated with this {@link GameView}.
     */
    public GameState getGameState()
    {
        return gameState;
    }

    /**
     * Sets the associated {@link GameState} of this {@link GameView}.
     * @param state The {@link GameView}'s new state.
     */
    public void setGameState(GameState state)
    {
        gameState = state;
    }

    /**
     * Draws the associated {@link GameState} to the display.
     */
    public void drawCurrentState()
    {
        getGameState().draw(getIoEngine());
    }
}
