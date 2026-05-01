package util;

import java.util.ArrayList;
import java.util.List;
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
	
	public Pair<T, U> clone() {
		return new Pair<>(first, second);
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
	
	/**
	 * Extract the first element of a list of PAir and return a list of the first elements.
	 */
	public static <T, U> List<T> extractFirstsFromList(List<Pair<T, U>> pairs){
		List<T> firsts = new ArrayList<>();
		for (Pair<T, U> p : pairs) {
			firsts.add(p.getFirst());
		}
		return firsts;
	}
	
	/**
	 * Extract the second element of a list of PAir and return a list of the second
	 * elements.
	 */
	public static <T, U> List<U> extractSecondsFromList(List<Pair<T, U>> pairs){
		List<U> seconds = new ArrayList<>();
		for (Pair<T, U> p : pairs) {
			seconds.add(p.getSecond());
		}
		return seconds;
	}

	
}
