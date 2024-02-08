package com.thepeoplescoder.snake.view.views.swing;

import java.awt.Font;

public class LazyFont
{
    private String name = null;
    private int style = Font.PLAIN;
    private int size = -1;
    private volatile Font font = null;

    public LazyFont() {}

    public LazyFont nameAs(String name) { this.name  = name;  return this; }
    public LazyFont styleAs(int style)  { this.style = style; return this; }
    public LazyFont sizeAs(int size)    { this.size  = size;  return this; }

    public Font toFont()
    {
        // temporary variable so that volatile field is only accessed once
        // in the case that it's already initialized
        // for more information -> https://en.wikipedia.org/wiki/Double-checked_locking#Usage_in_Java
        Font nvFont = font; 

        if (nvFont == null)
        {
            synchronized (this)
            {
                nvFont = font;
                if (nvFont == null)
                {
                    font = nvFont = new Font(name, style, size);
                }
            }
        }
        return nvFont;
    }
}
