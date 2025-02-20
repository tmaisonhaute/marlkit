package util;

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
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof Pair other) {
			return first.equals(other.first) && second.equals(other.second);
		}
		return false;
	}
	

	
}
