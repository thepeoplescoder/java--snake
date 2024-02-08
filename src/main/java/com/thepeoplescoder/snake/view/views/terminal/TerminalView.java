package com.thepeoplescoder.snake.view.views.terminal;

import com.thepeoplescoder.snake.state.GameState;
import com.thepeoplescoder.snake.view.GameView;

public class TerminalView extends GameView
{
    public TerminalIoEngine getIoEngine()
    {
        throw new UnsupportedOperationException("This method has not been implemented yet.");
    }
    
    public void displayStateSequence()
    {
        throw new UnsupportedOperationException("not implemented yet");
    }
    
    public TerminalView(GameState initialState)
    {
        super(initialState);
    }
}
