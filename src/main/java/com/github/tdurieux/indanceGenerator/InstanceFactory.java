package com.github.tdurieux.indanceGenerator;

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
		Object instance;
		Class<?> cl = getClass(sig);
		if (cl.equals(int.class)) {
			return new Integer(0);
		} else {
			instance = getClass(sig).newInstance();
		}
		for (edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field f : sig.getFields()) {
			A4TupleSet values = sol.eval(f);
			for (A4Tuple a4Tuple : values) {
				if (!a4Tuple.atom(0).equals(name) && name != null) {
					continue;
				}
				PrimSig fieldSig = a4Tuple.sig(a4Tuple.arity() - 1);
				String fieldInstanceName = a4Tuple.atom(a4Tuple.arity() - 1);
				Object value = getInstance(fieldSig, fieldInstanceName, sol);
				Class<?> clField = value.getClass();
				if (value instanceof Integer) {
					value = (int) Integer
							.parseInt(a4Tuple.atom(a4Tuple.arity() - 1));
					clField = int.class;
				}
				if (value instanceof Boolean) {
					clField = boolean.class;
				}
				try {
					Method m = type.getMethod(AlloyModelFactory.PREFIXSETTER
							+ Character.toUpperCase(f.label.charAt(0))
							+ f.label.substring(1), clField);
					m.invoke(instance, value);
					continue;
				} catch (NoSuchMethodException e) {
					// test other possibilities
				}
				try {
					Field publicField = type.getField(f.label);
					publicField.set(instance, value);
					continue;
				} catch (NoSuchFieldException e) {

				}
			}
		}
		return instance;
	}

	private Class<?> getClass(PrimSig sig) throws ClassNotFoundException {
		if (sig.label.equals("Int")) {
			return int.class;
		}
		if (sig.label.equals("True")) {
			return Boolean.class;
		}
		if (sig.label.equals("seq/Int")) {
			return int.class;
		}
		return this.getClass().getClassLoader().loadClass(sig.label);
	}

	public Iterator<T> iterator() {
		return new InstanceIterator();
	}

	private class InstanceIterator implements Iterator<T> {

		private int index = 0;

		@Override
		public boolean hasNext() {
			return ((limit != 0 && index < limit) || (limit == 0))
					&& solution.satisfiable();
		}

		@Override
		public T next() {
			index++;
			try {
//				System.out.println(solution);
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
