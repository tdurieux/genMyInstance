package com.github.tdurieux.instanceGenerator;

import java.util.Collection;
import java.util.Iterator;

/**
 * A class that generates an instance of {@link Collection} filling it with a
 * fixed number of instances randomly generated using Alloy.
 */
public class CollectionModelFactory {
	private Collection<?> collection;

	/**
	 * Sets up the factory and creates an instance of {@link Collection}
	 * 
	 * @param implementation
	 *            Implementation of {@link Collection} that the factory should
	 *            use
	 * @param data
	 *            Type of data that is meant stored in the collection
	 * @param cardinality
	 *            The number of instances of data in the collection
	 * @throws InstantiationException
	 *             If the implementation of the {@link Collection} given as a
	 *             parameter cannot be loaded
	 * @throws IllegalAccessException
	 *             If the class does not have a public nullory constructor
	 */
	public <E> CollectionModelFactory(
			final Class<? extends Collection> implementation, final Class<?> data,
			final int cardinality) throws InstantiationException,
			IllegalAccessException {
		Collection<E> collection = null;
		collection = implementation.newInstance();

		if (cardinality > 0) {
			InstanceFactory<?> factory = new InstanceFactory(data);
			int numberOfInstances = cardinality;
			for (Iterator iterator = factory.iterator(); iterator.hasNext()
					&& numberOfInstances > 0; numberOfInstances--) {
				E e = (E) iterator.next();
				collection.add(e);
			}
		}
		this.collection = collection;
	}

	public Collection<?> getCollection() {
		return collection;
	}
}