package testClass.basicClass;

public class PublicFieldUser {
	private String firstname;
	private String lastname;
	private int id;
	public String type;
	private boolean isExpired;

	public PublicFieldUser() {
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isExpired() {
		return isExpired;
	}

	public void setExpired(boolean isExpired) {
		this.isExpired = isExpired;
	}

	@Override
	public String toString() {
		return "[" + type + " " + id + "] " + lastname + " " + firstname + " "
				+ (isExpired ? "EXPIRED" : "VERIFIED");
	}
}
