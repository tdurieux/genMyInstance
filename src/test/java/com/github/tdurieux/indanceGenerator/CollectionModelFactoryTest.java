package com.github.tdurieux.indanceGenerator;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import basicClass.User;

public class CollectionModelFactoryTest {

	@Test
	public void testCardinality() throws InstantiationException, IllegalAccessException {
		final int cardinality = 10;
		CollectionModelFactory collectionModelFactory = new CollectionModelFactory(
				ArrayList.class, User.class, cardinality);
		Collection<?> collection = collectionModelFactory.getCollection();
		Assert.assertEquals(cardinality, collection.size());
	}
	
	@Test
	public void testZeroCardinality() throws InstantiationException, IllegalAccessException {
		final int cardinality = 0;
		CollectionModelFactory collectionModelFactory = new CollectionModelFactory(
				ArrayList.class, User.class, cardinality);
		Collection<?> collection = collectionModelFactory.getCollection();
		Assert.assertEquals(cardinality, collection.size());		
	}
	
	@Test
	public void testNegativeCardinality() throws InstantiationException, IllegalAccessException {
		final int cardinality = -1;
		CollectionModelFactory collectionModelFactory = new CollectionModelFactory(
				ArrayList.class, User.class, cardinality);
		Collection<?> collection = collectionModelFactory.getCollection();
		Assert.assertEquals(0, collection.size());		
	}

}
