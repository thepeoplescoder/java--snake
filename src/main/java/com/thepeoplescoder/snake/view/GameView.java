package com.thepeoplescoder.snake.view;

import com.thepeoplescoder.snake.state.GameState;

public abstract class GameView
{
    public abstract IoEngine getIoEngine();
    public abstract void displayStateSequence();
    
    public GameView(GameState initialState)
    {
        setGameState(initialState);
    }
    
    private GameState currentGameState;
    
    public GameState getGameState()
    {
        return currentGameState;
    }
    
    public void setGameState(GameState state)
    {
        currentGameState = state;
    }
    
    public void drawCurrentState()
    {
        getGameState().draw(getIoEngine());
    }
}
