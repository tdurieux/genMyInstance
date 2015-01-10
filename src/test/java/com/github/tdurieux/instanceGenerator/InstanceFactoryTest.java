package com.github.tdurieux.instanceGenerator;

import java.util.Iterator;

import org.junit.Test;

import com.github.tdurieux.instanceGenerator.InstanceFactory;

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
