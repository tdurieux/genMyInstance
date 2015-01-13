package com.github.tdurieux.instanceGenerator;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import testClass.basicClass.RecursiveUser;

import com.github.tdurieux.instanceGenerator.CollectionModelFactory;

public class CollectionModelFactoryTest {

	@Test
	public void testCardinality() throws InstantiationException,
			IllegalAccessException {
		final int cardinality = 10;
		CollectionModelFactory collectionModelFactory = new CollectionModelFactory(
				ArrayList.class, RecursiveUser.class, cardinality);
		Collection<?> collection = collectionModelFactory.getCollection();
		Assert.assertEquals(cardinality, collection.size());
	}

	@Test
	public void testZeroCardinality() throws InstantiationException,
			IllegalAccessException {
		final int cardinality = 0;
		CollectionModelFactory collectionModelFactory = new CollectionModelFactory(
				ArrayList.class, RecursiveUser.class, cardinality);
		Collection<?> collection = collectionModelFactory.getCollection();
		Assert.assertEquals(cardinality, collection.size());
	}

	@Test
	public void testNegativeCardinality() throws InstantiationException,
			IllegalAccessException {
		final int cardinality = -1;
		CollectionModelFactory collectionModelFactory = new CollectionModelFactory(
				ArrayList.class, RecursiveUser.class, cardinality);
		Collection<?> collection = collectionModelFactory.getCollection();
		Assert.assertEquals(0, collection.size());
	}

	@Test
	public void testInt() throws InstantiationException, IllegalAccessException {
		final int cardinality = 10;
		CollectionModelFactory collectionModelFactory = new CollectionModelFactory(
				ArrayList.class, int.class, cardinality);
		Collection<?> collection = collectionModelFactory.getCollection();
		System.out.println(collection);
		Assert.assertTrue(cardinality >= collection.size());
	}

}
