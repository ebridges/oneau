package com.oneau.core.util;

import java.io.Serializable;
import java.math.BigDecimal;

import static java.lang.String.format;
import static java.lang.System.arraycopy;
import static java.util.Arrays.sort;

/**
 * User: ebridges
 * Date: Jul 18, 2010
 */
public class Range<T extends Number> implements Serializable {
	private static final long serialVersionUID = 4880743930340470793L;
	
	private T left;
    private T right;

    @SuppressWarnings("unchecked")
    public Range(final T ... vals) {
        if(Utility.isEmpty(vals)) {
            throw new IllegalArgumentException("Range requires a value for initialization.");
        }

        Object[] valsCopy = new Object[vals.length];
        arraycopy(vals, 0, valsCopy, 0, vals.length);
        sort(valsCopy);

        this.left = Utility.throwIfNull("leftValue", (T)valsCopy[0]);
        this.right = Utility.throwIfNull("rightValue", (T)valsCopy[valsCopy.length-1]);
    }

    public T getLeft() {
        return left;
    }

    public T getRight() {
        return right;
    }

    public boolean contains(BigDecimal value) {
    	if(left instanceof BigDecimal && right instanceof BigDecimal) {
    		BigDecimal l = BigDecimal.class.cast(left);
    		BigDecimal r = BigDecimal.class.cast(right);
    		return (l.compareTo(value) < 0 && r.compareTo(value) >= 0 );
    	}
    	throw new IllegalArgumentException("left & right must be BigDecimal to compare to another BigDecimal.");
    }
    
    public boolean contains(Double value) {
        return (left.doubleValue() < value) && (value <= right.doubleValue());
    }

    public boolean contains(Float value) {
        return (left.floatValue() < value) && (value <= right.floatValue());
    }

    public boolean contains(Integer value) {
        return (left.intValue() < value) && (value <= right.intValue());
    }

    public boolean contains(Short value) {
        return (left.shortValue() < value) && (value <= right.shortValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        @SuppressWarnings("unchecked")
		Range<T> range = (Range<T>)o;

        if (!left.toString().equals(range.left.toString())) return false;
        if (!right.toString().equals(range.right.toString())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = left.toString().hashCode();
        result = 31 * result + right.toString().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return format("Range{left=%s, right=%s}",left,right);
    }
}
