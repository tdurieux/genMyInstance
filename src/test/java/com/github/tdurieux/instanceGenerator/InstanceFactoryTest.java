package com.github.tdurieux.instanceGenerator;

import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import testClass.basicClass.Person;
import testClass.basicClass.PublicFieldUser;
import testClass.basicClass.RecursiveUser;
import testClass.basicClass.StaticFieldUser;

public class InstanceFactoryTest {

	/**
	 * Create all instances of a simple class
	 */
	@Test
	public void createInstanceOfAClass() {
		InstanceFactory<Person> instanceF = new InstanceFactory<Person>(
				Person.class);
		Iterator<Person> it = instanceF.iterator();
		int count = 0;
		while (it.hasNext()) {
			it.next();
			count++;
		}
		Assert.assertEquals(97, count);
	}

	/**
	 * Create an instances of an array
	 */
	@Test
	public void createInstanceOfAnArray() {
		InstanceFactory<String[]> instanceF = new InstanceFactory<String[]>(
				String[].class);
		Iterator<String[]> it = instanceF.iterator();
		int count = 0;
		while (it.hasNext()) {
			String[] strings = it.next();
			if (strings != null && strings.length > 0 && strings[0] != null) {
				Assert.assertTrue(strings[0] instanceof String);
			}
			count++;
		}
		// The Alloy model generate maximum 5 different instances
		Assert.assertEquals(10, count);
	}

	/**
	 * the generator instance of classes which have a public field
	 */
	@Test
	public void createInstanceOfAClassWithPublicField() {
		InstanceFactory<PublicFieldUser> instanceF = new InstanceFactory<PublicFieldUser>(
				PublicFieldUser.class);
		Iterator<PublicFieldUser> it = instanceF.iterator();
		int count = 0;
		boolean publicFieldIsInitialised = false;
		while (it.hasNext()) {
			PublicFieldUser publicFieldUser = it.next();
			if (publicFieldUser != null) {
				if (publicFieldUser.type != null) {
					publicFieldIsInitialised = true;
					break;
				}
			}
			count++;
		}
		Assert.assertTrue(publicFieldIsInitialised);
	}

	/**
	 * the generator instance of classes which have a static field
	 */
	@Test
	public void createInstanceOfAClassWithStaticField() {
		InstanceFactory<StaticFieldUser> instanceF = new InstanceFactory<StaticFieldUser>(
				StaticFieldUser.class);
		Iterator<StaticFieldUser> it = instanceF.iterator();
		int count = 0;
		boolean sataticFieldIsInitialised = false;
		while (it.hasNext()) {
			StaticFieldUser staticFieldUser = it.next();
			if (staticFieldUser != null) {
				if (StaticFieldUser.type != null) {
					sataticFieldIsInitialised = true;
					break;
				}
			}
			count++;
		}
		Assert.assertTrue(sataticFieldIsInitialised);
	}

	/**
	 * Create an instance of Interface
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createInstanceOfAnInterface() {
		new InstanceFactory<List>(List.class);
	}

	@Test
	public void createInstanceOfRecurciveClass() {
		// the generator doesn't produce Stack OverFlow
		InstanceFactory<RecursiveUser> instanceF = new InstanceFactory<RecursiveUser>(
				RecursiveUser.class);
		Iterator<RecursiveUser> it = instanceF.iterator();
		int count = 0;
		while (it.hasNext() && count < 10) {
			it.next();
			count++;
		}
	}
}
