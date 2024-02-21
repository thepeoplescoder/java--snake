package com.thepeoplescoder.snake;

/**
 * In case I want to handle command line arguments.
 * For now, this class does nothing.
 */
public class CommandLineConfig
{
    private CommandLineConfig(String[] args)
    {
    }

    public static CommandLineConfig parse(String[] args)
    {
        return new CommandLineConfig(args);
    }
}
