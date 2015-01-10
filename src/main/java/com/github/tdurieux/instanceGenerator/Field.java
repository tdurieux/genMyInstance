package com.github.tdurieux.instanceGenerator;

public class Field {
	private String name;
	private Class<?> type;

	public Field(String name, Class<?> type) {
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public Class<?> getType() {
		return type;
	}

	@Override
	public String toString() {
		return type.getName() + " " + name;
	}
}
