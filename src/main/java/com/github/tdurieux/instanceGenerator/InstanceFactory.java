package com.github.tdurieux.instanceGenerator;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Tuple;
import edu.mit.csail.sdg.alloy4compiler.translator.A4TupleSet;

public class InstanceFactory<T> {

	private Class<?> type;
	private boolean isArray = false;
	// number of instance generated, 0 is for all
	private int limit = 0;

	private A4Solution solution;
	private AlloyModelFactory alloyModelFacotry;
	private Map<String, Object> instances = new HashMap<>();

	public InstanceFactory(Class<?> type) {
		if (type.isInterface()) {
			throw new IllegalArgumentException(
					"The type must be a class not an interface");
		}
		this.type = type;
		if (type.isArray()) {
			type = (Class<?>) type.getComponentType();
			this.isArray = true;
		}
		try {
			alloyModelFacotry = new AlloyModelFactory(this.type);
			solution = alloyModelFacotry.getSolution();
		} catch (Err e) {
			throw new RuntimeException(
					"Unable to create a solution of the class "
							+ this.type.getCanonicalName(), e);
		}
	}

	private Object getInstance(Sig sig, String name, A4Solution sol)
			throws Exception {
		if (sig.label.equals("True")) {
			return true;
		}
		if (sig.label.equals("False")) {
			return false;
		}
		if (sig.label.equals("NoEmptyString")) {
			return "BlaBla";
		}
		if (sig.label.equals("EmptyString")) {
			return "";
		}
		if (sig.label.equals("INSTANCE")) {
			edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field f = sig.getFields()
					.get(0);
			A4TupleSet a4TupleSet = sol.eval(f);
			Iterator<A4Tuple> it = a4TupleSet.iterator();
			if (!it.hasNext())
				return null;
			while(it.hasNext()) {
				A4Tuple a4Tuple = it.next();
				if(!a4Tuple.atom(0).equals(name)) {
					continue;
				}
				PrimSig fieldSig = a4Tuple.sig(a4Tuple.arity() - 1);
				String fieldInstanceName = a4Tuple.atom(a4Tuple.arity() - 1);
				if (instances.containsKey(fieldInstanceName)) {
					return instances.get(fieldInstanceName);
				}
				Object value = getInstance(fieldSig, fieldInstanceName, sol);
				instances.put(fieldInstanceName, value);
				return value;
			}
		}
		Object instance;
		Class<?> cl = getClass(sig);
		if (cl.equals(int.class)) {
			return Integer.parseInt(name);
		} else {
			instance = getClass(sig).newInstance();
		}
		instances.put(name, instance);
		for (edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field f : sig.getFields()) {
			A4TupleSet values = sol.eval(f);
			Object value = null;
			for (A4Tuple a4Tuple : values) {
				if (!a4Tuple.atom(0).equals(name) && name != null) {
					continue;
				}
				PrimSig fieldSig = a4Tuple.sig(a4Tuple.arity() - 1);
				String fieldInstanceName = a4Tuple.atom(a4Tuple.arity() - 1);
				if (instances.containsKey(fieldInstanceName)) {
					value = instances.get(fieldInstanceName);
				} else {
					value = getInstance(fieldSig, fieldInstanceName, sol);
					instances.put(fieldInstanceName, value);
				}
				break;
			}
			try {
				Method m = type
						.getMethod(
								AlloyModelFactory.PREFIXSETTER
										+ Character.toUpperCase(f.label
												.charAt(0))
										+ f.label.substring(1),
								alloyModelFacotry.getFields()
										.get(sig.label + f.label).getType());
				m.invoke(instance, value);
				continue;
			} catch (NoSuchMethodException e) {
				// test other possibilities
			} catch (IllegalArgumentException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			try {
				Field publicField = type.getField(f.label);
				publicField.set(instance, value);
				continue;
			} catch (NoSuchFieldException e) {

			}

		}
		return instance;
	}

	private Class<?> getClass(Sig sig) throws ClassNotFoundException {
		return getClassFromName(sig.label);
	}

	private Class<?> getClassFromName(String name)
			throws ClassNotFoundException {
		if (name.equals("Int") || name.equals("seq/Int")) {
			return int.class;
		}
		if (name.equals("True")) {
			return Boolean.class;
		}
		if (name.equals("EmptyString") || name.equals("NoEmptyString")) {
			return String.class;
		}
		return this.getClass().getClassLoader().loadClass(name);
	}
	
	public void setLimit(int limit) {
		this.limit = limit;
	}

	public Iterator<T> iterator() {
		return new InstanceIterator();
	}

	private class InstanceIterator implements Iterator<T> {

		private int index = 0;

		@Override
		public boolean hasNext() {
			// System.out.println(solution);
			return ((limit != 0 && index < limit) || (limit == 0))
					&& solution.satisfiable();
		}

		@Override
		public T next() {
			index++;
			try {
				A4TupleSet tuples = solution.eval(alloyModelFacotry
						.getPrimSig());
				Iterator<A4Tuple> it = tuples.iterator();
				if (!it.hasNext()) {
					solution = solution.next();
					return null;
				}
				instances = new HashMap<>();
				List<Object> instances = new ArrayList<>();
				while(it.hasNext()) {
					A4Tuple tuple = it.next();
					Object instance = getInstance(alloyModelFacotry.getPrimSig(),
							tuple.atom(0), solution);
					instances.add(instance);
				}
				solution = solution.next();
				if (isArray) {
					int size = instances.size();
					Object[] array = (Object[]) Array.newInstance(type.getComponentType(), size);
					for (int i = 0; i < size; i++) {
						Object instance = instances.get(i);
						array[i] = instance;
					}
					return (T) array;
				} else {
					return (T) instances.get(0);
				}

			} catch (Exception e) {
				throw new RuntimeException("Unable to create the instance", e);
			}
		}

		@Override
		public void remove() {
		}
	}

}
