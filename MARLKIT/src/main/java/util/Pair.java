package util;

import java.util.Objects;

public class Pair<T, U > {
	private T first;
	private U second;
	public Pair(T first, U second) {
		this.first = first;
		this.second = second;
	}
	public T getFirst() {
		return first;
	}
	public U getSecond() {
		return second;
	}

	public void setFirst(T first) {
		this.first = first;
	}

	public void setSecond(U second) {
		this.second = second;
	}
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
