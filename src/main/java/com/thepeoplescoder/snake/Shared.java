package com.thepeoplescoder.snake;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

import com.thepeoplescoder.snake.view.views.swing.LazyFont;

public class Shared
{
    public static final Random random = new Random();

    public static class Settings
    {
        public static class Game
        {
            public static final int initialSafetySpaces = 10;
            public static final int delayMillis = 100;
            public static final int growthStepsPerApple = 5;
        }

        public static class View
        {
            public static class Swing
            {
                public static final int cellToPixelScale = 12;
                public static final int cellWidth = cellToPixelScale;
                public static final int cellHeight = cellToPixelScale;
                public static final int scoreHeight = 20;
            }
            public static class Terminal
            {
            }
        }
    }

    public static class Colors
    {
        public static final Color snakeHead = Color.green;
        public static final Color snakeTail = snakeHead.darker();
        public static final Color background = Color.black;
        public static final Color wall = Color.blue;
        public static final Color apple = Color.red;
        public static final Color grid = background;
        public static final Color scoreColor = Color.yellow.brighter();
        public static final Color gameOverColor = Color.red;
        public static final Color littleGameOverColor = Color.cyan.brighter();
    }

    public static class Fonts
    {
        public static class LazyLoaded
        {
            // Use LazyFont instead of Font so that the fonts
            // aren't loaded if they aren't needed.

            public static final LazyFont little =
                new LazyFont().nameAs("Monospaced").styleAs(Font.PLAIN).sizeAs(20);
            public static final LazyFont big =
                new LazyFont().nameAs("Monospaced").styleAs(Font.BOLD).sizeAs(50);

            public static final LazyFont score = little;
            public static final LazyFont gameOver = big;
            public static final LazyFont littleGameOver = little;
        }
    }

    public static class Messages
    {
        public static final String gameOver = "Game Over!";
        public static final String[] littleGameOver = new String[] {
            "haha u suck",
            "lol get rekt",
            "lol ur ded",
            "and the apples lived peacefully.",
            "well, that's that.",
            "don't you have work to do?",
            "the apples are safe...for now.",
            "wow...you really suck at this!",
            "sucks to suck."
        };
        public static String littleGameOverMessage()
        {
            return littleGameOver[random.nextInt(littleGameOver.length)];
        }
    }
}
