package com.github.tdurieux.instanceGenerator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Tuple;
import edu.mit.csail.sdg.alloy4compiler.translator.A4TupleSet;

public class InstanceFactory<T> {

	private Class<T> type;
	// number of instance generated, 0 is for all
	private int limit = 0;

	private A4Solution solution;
	private AlloyModelFactory alloyModelFacotry;

	public InstanceFactory(Class<T> type) {
		this.type = type;
		try {
			alloyModelFacotry = new AlloyModelFactory(type);
			solution = alloyModelFacotry.getSolution();
		} catch (Err e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Object getInstance(PrimSig sig, String name, A4Solution sol)
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
		if(sig.label.equals("INSTANCE")) {
			edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field f = sig.getFields().get(0);
			A4TupleSet a4TupleSet = sol.eval(f);
			Iterator<A4Tuple> it = a4TupleSet.iterator();
			if(!it.hasNext()) return null;
			A4Tuple a4Tuple = it.next();
			PrimSig fieldSig = a4Tuple.sig(a4Tuple.arity() - 1);
			String fieldInstanceName = a4Tuple.atom(a4Tuple.arity() - 1);
			return getInstance(fieldSig, fieldInstanceName, sol);
		}
		Object instance;
		Class<?> cl = getClass(sig);
		if (cl.equals(int.class)) {
			return Integer.parseInt(name);
		} else {
			instance = getClass(sig).newInstance();
		}
		for (edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field f : sig.getFields()) {
			A4TupleSet values = sol.eval(f);
			Object value = null;
			for (A4Tuple a4Tuple : values) {
				if (!a4Tuple.atom(0).equals(name) && name != null) {
					continue;
				}
				PrimSig fieldSig = a4Tuple.sig(a4Tuple.arity() - 1);
				String fieldInstanceName = a4Tuple.atom(a4Tuple.arity() - 1);
				value = getInstance(fieldSig, fieldInstanceName, sol);
				break;
			}
			try {
				Method m = type.getMethod(AlloyModelFactory.PREFIXSETTER
						+ Character.toUpperCase(f.label.charAt(0))
						+ f.label.substring(1), alloyModelFacotry.getFields().get(sig.label + f.label).getType());
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
	

	private Class<?> getClass(PrimSig sig) throws ClassNotFoundException {
		return getClassFromName(sig.label);
	}
	
	private Class<?> getClassFromName(String name) throws ClassNotFoundException {
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

	public Iterator<T> iterator() {
		return new InstanceIterator();
	}

	private class InstanceIterator implements Iterator<T> {

		private int index = 0;

		@Override
		public boolean hasNext() {
			//System.out.println(solution);
			return ((limit != 0 && index < limit) || (limit == 0))
					&& solution.satisfiable();
		}

		@Override
		public T next() {
			index++;
			try {
				A4TupleSet tuples = solution.eval(alloyModelFacotry.getPrimSig());
				if(!tuples.iterator().hasNext()){
					solution = solution.next();
					return null;
				}
				Object instance = getInstance(alloyModelFacotry.getPrimSig(),
						null, solution);
				solution = solution.next();
				return (T) instance;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public void remove() {
		}
	}

}
