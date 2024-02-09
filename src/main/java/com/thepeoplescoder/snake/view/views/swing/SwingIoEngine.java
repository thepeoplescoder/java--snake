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

    /**
     * @param view The {@link SwingView} associated with this {@link SwingIoEngine}.
     */
    public SwingIoEngine(SwingView view)
    {
        super(view);
    }

    /**
     * @return The current {@link GameView}.
     */
    @Override
    public SwingView getGameView()
    {
        return (SwingView)super.getGameView();
    }

    /**
     * @return The current graphics context.
     */
    public Graphics getGraphics()
    {
        return getGameView().getGraphics();
    }

    /**
     * Sets the current drawing color.
     * @param color The new drawing color to use.
     * @return This {@link SwingIoEngine}.
     */
    public SwingIoEngine setColor(Color color)
    {
        getGraphics().setColor(color);
        return this;
    }

    /**
     * Converts the given cell X coordinate to its window pixel coordinate.
     * @param cellX The cell coordinate.
     * @return The window pixel coordinate.
     */
    private static int cellXToPixelX(int cellX)
    {
        return cellX * CELL_TO_PIXEL_SCALE;
    }

    /**
     * Converts the given cell Y coordinate to its window pixel coordinate.
     * @param cellY The cell coordinate.
     * @return The window pixel coordinate.
     */
    private static int cellYToPixelY(int cellY)
    {
        return SCORE_HEIGHT + (cellY * CELL_TO_PIXEL_SCALE);
    }

    /**
     * Sets the given cell in {@link com.thepeoplescoder.snake.state.GameBoard} coordinates to the current color.
     * @param pos The position at which to draw the cell.
     */
    @Override
    public void drawCellAt(IntVector2 pos)
    {
        getGraphics().fillRect(cellXToPixelX(pos.getX()), cellYToPixelY(pos.getY()), CELL_WIDTH, CELL_HEIGHT);
    }

    /**
     * Draws the score to the display.
     * @param score The {@link Score} to display.
     */
    @Override
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

    /**
     * Draws the game over screen.
     */
    @Override
    public void drawGameOver()
    {
        Graphics g = getGraphics();

        g.setFont(Shared.Fonts.LazyLoaded.gameOver.toFont());
        g.setColor(Shared.Colors.gameOverColor);

        final int bigGameOverY = getGameView().getPixelDimensions().height / 2;
        drawStringAtCenter(g, Shared.Messages.gameOver, bigGameOverY);

        g.setFont(Shared.Fonts.LazyLoaded.littleGameOver.toFont());
        g.setColor(Shared.Colors.littleGameOverColor);

        final int littleGameOverY = bigGameOverY + g.getFontMetrics().getHeight();
        drawStringAtCenter(g, getGameState().getLittleGameOverMessage(), littleGameOverY);
    }

    /**
     * Draws a string at the center of the display.
     * @param g Graphics context of the display.
     * @param s The string to draw.
     * @param y The y-coordinate of the string.
     */
    private void drawStringAtCenter(Graphics g, String s, int y)
    {
        g.drawString(s, (getGameView().getPixelDimensions().width - g.getFontMetrics().stringWidth(s)) / 2, y);
    }

    /**
     * Draws the grid.
     */
    @Override
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

    /**
     * Creates a {@link KeyListener} to handle input events. (Swing exclusive)
     * @param v The {@link SwingView} keeping track of the current game state.
     * @return A {@link KeyListener} to be used for handling input events.
     */
    public static final KeyListener newKeyListener(SwingView v)
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