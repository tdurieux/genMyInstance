package testClass.basicClass;

public class RecursiveUser {
	private String firstname;
	private String lastname;
	private int id;
	private boolean isExpired;
	private RecursiveUser parent;

	public RecursiveUser() {
	}

	public RecursiveUser(String firstname, String lastname, int id) {
		super();
		this.firstname = firstname;
		this.lastname = lastname;
		this.id = id;
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

	public RecursiveUser getParent() {
		return parent;
	}

	public void setParent(RecursiveUser parent) {
		this.parent = parent;
	}

	private String stringRepresention() {
		return "[" + id + "] " + lastname + " " + firstname + " "
				+ (isExpired ? "EXPIRED" : "VERIFIED");
	}

	@Override
	public String toString() {
		return stringRepresention() + " parent: "
				+ (parent != null ? parent.stringRepresention() : null);
	}
}
