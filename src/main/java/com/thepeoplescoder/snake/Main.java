package com.thepeoplescoder.snake;

import com.thepeoplescoder.snake.state.GameState;
import com.thepeoplescoder.snake.view.GameView;
import com.thepeoplescoder.snake.view.views.swing.SwingView;

public class Main implements Runnable
{
    private final GameView view;

    public static void main(String[] args)
    {
        new Main(CommandLineConfig.parse(args)).run();
    }

    private Main(CommandLineConfig __)
    {
        view = new SwingView(GameState.initial());
    }

    public void run()
    {
        view.displayStateSequence();
    }
}