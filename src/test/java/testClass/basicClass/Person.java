package testClass.basicClass;

public class Person {
	// Attributs
	private int id;
	private String name;
	private boolean married;
	private boolean voted;

	// Getters - Setters
	public int getId() {
		return this.id;
	}

	public void setId(int n) {
		this.id = n;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String n) {
		this.name = n;
	}

	public boolean isMarried() {
		return this.married;
	}

	public void setMarried(boolean maritalStatus) {
		this.married = maritalStatus;
	}

	public boolean hasVoted() {
		return this.voted;
	}

	public void setVoted(boolean votingStatus) {
		this.voted = votingStatus;
	}
}