package util;

import java.util.Objects;

/**
 * A generic pair of two values.
 *
 * @param <T> the type of the first element
 * @param <U> the type of the second element
 */
public class Pair<T, U > {
	private T first;
	private U second;
	
	/**
	 * Creates a new pair with the specified values.
	 *
	 * @param first the first element
	 * @param second the second element
	 */
	public Pair(T first, U second) {
		this.first = first;
		this.second = second;
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
	 * Creates a shallow copy of this pair.
	 *
	 * @return a new pair with the same elements
	 */
	public Pair<T, U> clone(){
		return new Pair<T, U>(first, second);
	}
	
	
	
	@Override
	public String toString() {
		return "Pair [first=" + first + ", second=" + second + "]";
	}
	@Override
	public int hashCode() {
		return Objects.hash(first, second);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof Pair other) {
			return first.equals(other.first) && second.equals(other.second);
		}
		return false;
	}
	
	
	
	

	
}
