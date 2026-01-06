package util;

import java.util.Objects;

/**
 * A generic triple of three values.
 *
 * @param <T> the type of the first element
 * @param <U> the type of the second element
 * @param <V> the type of the third element
 */
public class Triple<T, U, V> {
    private T first;
    private U second;
    private V third;

	/**
	 * Creates a new triple with the specified values.
	 *
	 * @param first the first element
	 * @param second the second element
	 * @param third the third element
	 */
    public Triple(T first, U second, V third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

	/**
	 * Returns the first element.
	 *
	 * @return the first element
	 */
    public T getFirst() {
        return first;
    }

	/**
	 * Returns the second element.
	 *
	 * @return the second element
	 */
    public U getSecond() {
        return second;
    }

	/**
	 * Returns the third element.
	 *
	 * @return the third element
	 */
    public V getThird() {
        return third;
    }

	/**
	 * Sets the first element.
	 *
	 * @param first the new first element
	 */
    public void setFirst(T first) {
        this.first = first;
    }

	/**
	 * Sets the second element.
	 *
	 * @param second the new second element
	 */
    public void setSecond(U second) {
        this.second = second;
    }

	/**
	 * Sets the third element.
	 *
	 * @param third the new third element
	 */
    public void setThird(V third) {
        this.third = third;
    }

	/**
	 * Creates a shallow copy of this triple.
	 *
	 * @return a new triple with the same elements
	 */
    public Triple<T, U, V> clone() {
        return new Triple<T, U, V>(first, second, third);
    }

	@Override
	public int hashCode() {
		return Objects.hash(first, second, third);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Triple other = (Triple) obj;
		return Objects.equals(first, other.first) && Objects.equals(second, other.second)
				&& Objects.equals(third, other.third);
	}
    
    
}
