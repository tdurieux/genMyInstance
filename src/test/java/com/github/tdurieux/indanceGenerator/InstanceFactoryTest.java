package com.github.tdurieux.indanceGenerator;

import java.util.Iterator;

import org.junit.Test;

import basicClass.User;

public class InstanceFactoryTest {
	
	@Test
	public void createUserInstance() {
		InstanceFactory<User> instanceF = new InstanceFactory<User>(User.class);
		Iterator<User> it = instanceF.iterator();
		while(it.hasNext()) {
			User u = it.next();
			System.out.println(u);
		}
	}
}
