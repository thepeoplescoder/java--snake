package com.thepeoplescoder.snake.view.views.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import javax.swing.SwingUtilities;

import com.thepeoplescoder.snake.math.IntVector2;
import com.thepeoplescoder.snake.Shared;
import com.thepeoplescoder.snake.input.GameInputEvent;
import com.thepeoplescoder.snake.state.Score;
import com.thepeoplescoder.snake.view.GameView;
import com.thepeoplescoder.snake.view.IoEngine;

public class SwingIoEngine extends IoEngine
{
    /**
     * Use on code to be ran on the Event Dispatch Thread.
     * @param r The code to run on the Event Dispatch Thread.
     */
    public static void runOnEventDispatchThread(Runnable r)
    {
        if (SwingUtilities.isEventDispatchThread())
        {
            r.run();
        }
        else
        {
            SwingUtilities.invokeLater(r);
        }
    }

    public static final int CELL_TO_PIXEL_SCALE = Shared.Settings.View.Swing.cellToPixelScale;
    public static final int CELL_WIDTH = Shared.Settings.View.Swing.cellWidth;
    public static final int CELL_HEIGHT = Shared.Settings.View.Swing.cellHeight;
    public static final int SCORE_HEIGHT = Shared.Settings.View.Swing.scoreHeight;

    public SwingIoEngine(SwingView view)
    {
        super(view);
    }

    public SwingView getGameView()
    {
        return (SwingView)super.getGameView();
    }

    public Graphics getGraphics()
    {
        return getGameView().getGraphics();
    }

    public SwingIoEngine setColor(Color color)
    {
        getGraphics().setColor(color);
        return this;
    }

    private static int cellXToPixelX(int cellX)
    {
        return cellX * CELL_TO_PIXEL_SCALE;
    }

    private static int cellYToPixelY(int cellY)
    {
        return SCORE_HEIGHT + (cellY * CELL_TO_PIXEL_SCALE);
    }

    public void drawCellAt(IntVector2 pos)
    {
        getGraphics().fillRect(cellXToPixelX(pos.getX()), cellYToPixelY(pos.getY()), CELL_WIDTH, CELL_HEIGHT);
    }

    public void drawScore(Score score)
    {
        Graphics g = getGraphics();
        g.setFont(Shared.Fonts.LazyLoaded.score.toFont());

        FontMetrics fm = g.getFontMetrics();
        g.setColor(Shared.Colors.scoreColor);
        g.drawString("Score: ", 0, 20);
        g.setColor(Shared.Colors.scoreColor.darker().darker());
        g.drawString(score.toString(), fm.stringWidth("Score: "), 20);
    }

    public void drawGameOver()
    {
        final int bigGameOverY = getGameView().getPixelDimensions().height / 2;
        Graphics g = getGraphics();
        g.setFont(Shared.Fonts.LazyLoaded.gameOver.toFont());
        g.setColor(Shared.Colors.gameOverColor);
        drawStringAtCenter(g, Shared.Messages.gameOver,
            bigGameOverY);
        g.setFont(Shared.Fonts.LazyLoaded.littleGameOver.toFont());
        g.setColor(Shared.Colors.littleGameOverColor);
        drawStringAtCenter(g, getGameState().getLittleGameOverMessage(),
            bigGameOverY + g.getFontMetrics().getHeight());
    }

    public void drawStringAtCenter(Graphics g, String s, int y)
    {
        g.drawString(s, (getGameView().getPixelDimensions().width - g.getFontMetrics().stringWidth(s)) / 2, y);
    }

    public void drawGrid()
    {
        if (SwingIoEngine.gridColor == null) { return; }

        final IntVector2 boardSize = getGameBoard().getSize();
        final Dimension pixel = getGameView().getPixelDimensions();
        final Graphics g = getGraphics();

        g.setColor(SwingIoEngine.gridColor);

        IntStream.range(0, boardSize.getX()).map(SwingIoEngine::cellXToPixelX)
            .forEach(x -> g.drawLine(x, SCORE_HEIGHT, x, pixel.height));
        IntStream.range(0, boardSize.getY()).map(SwingIoEngine::cellYToPixelY)
            .forEach(y -> g.drawLine(0, y, pixel.width, y));
    }

    public static final KeyListener newKeyListener(GameView v)
    {
        Map<Integer, GameInputEvent> handlers = new HashMap<>();

        handlers.put(KeyEvent.VK_UP, GameInputEvent.Action.moveUp);
        handlers.put(KeyEvent.VK_DOWN, GameInputEvent.Action.moveDown);
        handlers.put(KeyEvent.VK_LEFT, GameInputEvent.Action.moveLeft);
        handlers.put(KeyEvent.VK_RIGHT, GameInputEvent.Action.moveRight);
        handlers.put(KeyEvent.VK_P, GameInputEvent.Action.togglePaused);
        handlers.put(KeyEvent.VK_ENTER, GameInputEvent.Action.playAgain);
        handlers.put(KeyEvent.VK_ESCAPE, GameInputEvent.Action.quitGame);
        handlers.put(KeyEvent.VK_Q, GameInputEvent.Action.quitGame);

        return new KeyListener() {
            @Override public void keyTyped(KeyEvent e) {}
            @Override public void keyReleased(KeyEvent e) {}
            @Override public void keyPressed(KeyEvent e)
            {
                v.getGameState().queueInputEvent(handlers.getOrDefault(e.getKeyCode(), GameInputEvent.noAction));
            }
        };
    }

    private static final Color gridColor = Shared.Colors.grid;
}