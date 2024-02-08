package com.thepeoplescoder.snake;

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
