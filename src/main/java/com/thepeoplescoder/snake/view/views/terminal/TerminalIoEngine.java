package com.thepeoplescoder.snake.view.views.terminal;

import java.awt.Color;

import com.thepeoplescoder.snake.math.IntVector2;
import com.thepeoplescoder.snake.state.Score;
import com.thepeoplescoder.snake.view.IoEngine;

public class TerminalIoEngine extends IoEngine
{
    @SuppressWarnings("unused")
    private Color currentColor = new Color(0x7F, 0x7F, 0x7F);
    
    public TerminalIoEngine(TerminalView view)
    {
        super(view);
    }

    public TerminalIoEngine setColor(Color color)
    {
        this.currentColor = color;
        return this;
    }
    
    public void drawCellAt(IntVector2 position)
    {
    }
    
    public void drawScore(Score score)
    {
    }
    
    public void drawGrid()
    {
    }
    
    public void drawGameOver()
    {
    }
}
