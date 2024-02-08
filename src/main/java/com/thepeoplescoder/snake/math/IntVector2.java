package com.thepeoplescoder.snake.math;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class IntVector2
{
    /** This vector's x component. */
    private final int x;
    /** This vector's y component. */
    private final int y;

    /** A collection of all cached vectors. */
    private static final Collection<IntVector2> vectorCache = new HashSet<IntVector2>();
    /** A unit vector in the positive X direction. This vector is cached. */
    public static final IntVector2 I = IntVector2.of(1, 0).cache();
    /** A unit vector in the positive Y direction. This vector is cached. */
    public static final IntVector2 J = IntVector2.of(0, 1).cache();
    /** The zero vector.  This vector is cached. */
    public static final IntVector2 ZERO = IntVector2.of(0, 0).cache();
    /** A unit vector in the negative X direction.  This vector is cached. */
    public static final IntVector2 MINUS_I = I.negate().cache();
    /** A unit vector in the negative Y direction.  This vector is cached. */
    public static final IntVector2 MINUS_J = J.negate().cache();
    /** A {@link Collection} of {@link IntVector2}s pointing in the cardinal directions. */
    public static final Collection<IntVector2> DIRECTIONS = Arrays.asList(I, J, MINUS_I, MINUS_J);

    /**
     * Constructor of a two-dimensional vector with {@code int} components.
     * @param x The x component of this vector.
     * @param y The y component of this vector.
     */
    private IntVector2(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    
    /**
     * @return The x component of this vector.
     */
    public int getX()
    {
        return x;
    }
    
    /**
     * @return The y component of this vector.
     */
    public int getY()
    {
        return y;
    }
    
    /**
     * Is this vector the zero vector?
     * @return {@code true} if it is, {@code false} otherwise.
     */
    public boolean isZero()
    {
        return equals(0, 0);
    }
    
    /**
     * Negates this vector.
     * @return A (possibly) new vector of the same magnitude, pointing in the opposite direction.
     */
    public IntVector2 negate()
    {
        return IntVector2.of(-getX(), -getY());
    }
    
    /**
     * Synonym for {@link #negate()}.
     */
    public IntVector2 minus()
    {
        return negate();
    }
    
    /**
     * Adds a vector with the given x and y components to this vector.
     * @param x The x component of the other vector.
     * @param y The y component of the other vector.
     * @return A (possibly) new vector representing the sum of both vectors.
     */
    public IntVector2 plus(int x, int y)
    {
        return IntVector2.of(this.getX() + x, this.getY() + y);
    }
    
    /**
     * Subtracts a vector with the given x and y components to this vector.
     * @param x The x component of the other vector.
     * @param y The y component of the other vector.
     * @return A (possibly) new vector representing the difference between these vectors.
     */
    public IntVector2 minus(int x, int y)
    {
        return IntVector2.of(this.getX() - x, this.getY() - y);
    }
    
    /**
     * Multiplies this vector by an {@code int} scalar.
     * @param n The scalar value to multiply by.
     * @return A (possibly) new vector, scaled by a factor of {@code n}.
     */
    public IntVector2 times(int n)
    {
        return IntVector2.of(n * x, n * y);
    }
    
    /**
     * Takes the dot product of this vector, with a vector of the given x and y components.
     * @param x The x component of the other vector.
     * @param y The y component of the other vector.
     * @return The dot product of the vectors.
     */
    public int dot(int x, int y)
    {
        return this.getX() * x + this.getY() * y;
    }
    
    /**
     * Checks to see if this vector is perpendicular to a vector with the given x and y components.
     * @param x The x component of the other vector.
     * @param y The y component of the other vector.
     * @return {@code true} if these vectors are perpendicular, {@code false} otherwise.
     */
    public boolean isPerpendicularTo(int x, int y)
    {
        return dot(x, y) == 0;
    }
    
    /**
     * Checks to see if this vector is parallel to another vector with the given x and y components.
     * @param x The x component of the other vector.
     * @param y The y component of the other vector.
     * @return {@code true} if these vectors are parallel, {@code false} otherwise.
     */
    public boolean isParallelTo(int x, int y)
    {
        // if this vector and [x, y] are row vectors representing a
        // system of equations and the determinant is zero, then
        // the vectors are parallel.
        return getX() * y - x * getY() == 0;
    }
    
    /**
     * Adds this vector to another.
     * @param other The other vector.
     * @return A (possibly) new vector representing their sum.
     */
    public IntVector2 plus(IntVector2 other)
    {
        return this.plus(other.getX(), other.getY());
    }
    
    /**
     * Subtracts another vector from this one.
     * @param other The other vector.
     * @return A (possibly) new vector representing their difference.
     */
    public IntVector2 minus(IntVector2 other)
    {
        return this.minus(other.getX(), other.getY());
    }
    
    /**
     * Gets the dot product of this vector with another one.
     * @param other The other vector.
     * @return Their dot product.
     */
    public int dot(IntVector2 other)
    {
        return this.dot(other.getX(), other.getY());
    }
    
    /**
     * Is this vector perpendicular to another one?
     * @param other The other vector.
     * @return {@code true} if it is, otherwise {@code false}.
     */
    public boolean isPerpendicularTo(IntVector2 other)
    {
        return isPerpendicularTo(other.getX(), other.getY());
    }
    
    /**
     * Is this vector parallel to another one?
     * @param other The other vector.
     * @return {@code true} if it is, otherwise {@code false}.
     */
    public boolean isParallelTo(IntVector2 other)
    {
        return isParallelTo(other.getX(), other.getY());
    }
    
    /**
     * @return The magnitude of this vector.
     */
    public double magnitude()
    {
        return Math.sqrt(this.dot(this));
    }
    
    /**
     * @return A string representation of this vector.
     */
    @Override
    public String toString()
    {
        return String.format("<%d,%d>", x, y);
    }
    
    /**
     * @return A hash code for this vector.
     */
    @Override
    public int hashCode()
    {
        return x ^ y;
    }
    
    /**
     * Checks to see if this vector is equal to the given object.
     * @param other The other object to examine.
     * @return {@code true} if this vector is equal to the other object, otherwise {@code false}.
     */
    @Override
    public boolean equals(Object other)
    {
        return (other instanceof IntVector2) && equalsWithoutNullCheck((IntVector2)other);
    }
    
    /**
     * Checks to see if this vector is equal to another one, without
     * bothering to check if the other vector is {@code null}.
     * @param other The other vector to check.
     * @return {@code true} if this vector is equal to the other one, otherwise {@code false}
     * @throws NullPointerException {@code other} was {@code null}.
     */
    public boolean equalsWithoutNullCheck(IntVector2 other)
    {
        return this == other || equals(other.x, other.y);
    }
    
    /**
     * Checks to see if this vector is equal to another one.
     * @param other The other vector to check.
     * @return {@code true} if this vector is equal to the other one, otherwise {@code false}.
     */
    public boolean equals(IntVector2 other)
    {
        return other != null && this.equalsWithoutNullCheck(other);
    }
    
    /**
     * Checks to see if this vector is equal to another vector with the given x and y components.
     * @param x The x component of the other vector.
     * @param y The y component of the other vector.
     * @return {@code true} if the vectors are equal, otherise {@code false}.
     */
    public boolean equals(int x, int y)
    {
        return this.x == x && this.y == y;
    }
    
    /**
     * @return A new {@link java.awt.Point} that is representative of this vector.
     */
    public Point toPoint()
    {
        return new Point(x, y);
    }
    
    /**
     * @return A new {@link java.awt.Dimension} that is representative of this vector.
     */
    public Dimension toDimension()
    {
        return new Dimension(x, y);
    }
    
    /**
     * Caches this vector, so that other calculations that result in other vectors of this value
     * use this one, instead of allocating memory for a brand new one.
     * @return This vector.  Subsequent allocations requiring, or calculations resulting in,
     *         a vector of this value, will use this exact object from now on.
     */
    public IntVector2 cache()
    {
        // don't allow duplicates, regardless of whatever collection type we use.
        // this is to allow us to use other collections than HashSet.
        if (!vectorCache.contains(this))
        {
            vectorCache.add(this);
        }
        return this;
    }
    
    /**
     * Creates a (possibly) new vector with the given x and y components.
     * @param x The x component of the vector.
     * @param y The y component of the vector.
     * @return A (possibly) new vector with the given x and y components.  If there was a vector
     *         with these values that has already been cached, that vector will be used instead.
     */
    public static IntVector2 of(int x, int y)
    {
        return vectorCache.stream()
                .filter(vector -> vector.equals(x, y))
                .findFirst()
                .orElseGet(() -> new IntVector2(x, y));
    }
    
    /**
     * @param v The vector to cache.
     * @return {@code v}
     * @see {@link #cache()}
     * @throws NullPointerException {@code v} is {@code null}.
     */
    public static IntVector2 cache(IntVector2 v)
    {
        return v.cache();
    }
}
