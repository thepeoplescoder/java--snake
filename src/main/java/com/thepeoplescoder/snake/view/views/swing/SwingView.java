package com.thepeoplescoder.snake.view.views.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.thepeoplescoder.snake.Shared;
import com.thepeoplescoder.snake.state.GameState;
import com.thepeoplescoder.snake.view.GameView;
import com.thepeoplescoder.snake.view.IoEngine;

/**
 * This class represents a View where the game is played on a Swing window.
 */
public class SwingView extends GameView
{
    /**
     * Use on code to be ran on the Event Dispatch Thread.
     * @param r The code to run on the Event Dispatch Thread.
     */
    public static void runOnEventDispatchThread(Runnable r)
    {
        SwingIoEngine.runOnEventDispatchThread(r);
    }
    
    private final Dimension pixelDimensions = getGameState().getBoard().getSize().times(CELL_TO_PIXEL_SCALE)
        .plus(0, SCORE_HEIGHT)
        .toDimension();

    private static final int SCORE_HEIGHT = SwingIoEngine.SCORE_HEIGHT;

    public Dimension getPixelDimensions()
    {
        return pixelDimensions;
    }

    /**
     * The current graphics context of this {@link SwingView}.
     */
    private Graphics graphics = null;
    
    /**
     * @return The current graphics context of this {@link SwingView}.
     */
    public Graphics getGraphics()
    {
        return graphics;
    }
    
    /**
     * Constructor for a {@link SwingView}.
     * @param initialState The initial {@link GameState}.
     */
    public SwingView(GameState initialState)
    {
        super(initialState);
        ioEngine = new SwingIoEngine(this);
    }
    private final SwingIoEngine ioEngine;

    /**
     * @return the {@link IoEngine} associated with this {@link SwingView}.
     * @see SwingIoEngine
     */
    public SwingIoEngine getIoEngine()
    {
        return ioEngine;
    }
    
    /**
     * Displays the sequence of {@link GameState}s, starting with a given initial {@link GameState}.
     * @param initialState The initial {@link GameState}.
     */
    public void displayStateSequence()
    {
        runOnEventDispatchThread(GameFrame::new);
    }
    
    /**
     * The JFrame containing the game display.
     */
    @SuppressWarnings("serial")
    public class GameFrame extends JFrame
    {
        public GameFrame()
        {
            this.add(new GamePanel());
            this.setTitle("Snake");
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setResizable(false);
            this.pack();
            this.setLocationRelativeTo(null);
            this.setVisible(true);
        }
    }
    
    /**
     * The JPanel of the game display.
     */
    @SuppressWarnings("serial")
    public class GamePanel extends JPanel
    {
        //private final Timer gameTimer;

        public GamePanel()
        {
            this.setPreferredSize(pixelDimensions);
            this.setBackground(Shared.Colors.background);
            this.setFocusable(true);

            this.addKeyListener(SwingIoEngine.newKeyListener(SwingView.this));

            final Runnable repainter = System.getProperty("os.name").toLowerCase().equals("linux") ? () -> {
                repaint();
                Toolkit.getDefaultToolkit().sync();
            } : this::repaint;

            final Timer gameTimer = new Timer(Shared.Settings.Game.delayMillis, null);
            final Runnable terminateIfDone = () -> {
                if (getGameState().isDone())
                {
                    gameTimer.stop();
                    System.exit(0);
                }
            };

            // This is essentially our game loop.
            gameTimer.addActionListener(e -> {
                terminateIfDone.run();
                setGameState(getGameState().nextState());
                repainter.run();
            });
            repainter.run();        // paint initial state
            gameTimer.start();
        }

        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            SwingView.this.graphics = g;    // Required by the I/O engine.
            drawCurrentState();
        }
    }
    
    private static final int CELL_TO_PIXEL_SCALE = SwingIoEngine.CELL_TO_PIXEL_SCALE;
}
