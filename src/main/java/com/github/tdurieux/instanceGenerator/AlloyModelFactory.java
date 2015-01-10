package com.github.tdurieux.instanceGenerator;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Attr;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprConstant;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;

public class AlloyModelFactory {
	public static String PREFIXSETTER = "set";
	public static String PREFIXGETTER = "get";
	public static String PREFIXIS = "is";
	public static String PREFIXHAS = "has";

	private Class<?> type;
	private Map<String, Field> fields;
	private A4Solution solution;
	private PrimSig primSig;

	public <T> AlloyModelFactory(Class<T> type) throws Err {
		A4Options opt = new A4Options();
		opt.solver = A4Options.SatSolver.SAT4J;
		this.type = type;

		PrimSig alloyBoolean = new PrimSig("Boolean", Attr.ABSTRACT);
		PrimSig alloyTrue = new PrimSig("True", alloyBoolean, Attr.ONE);
		PrimSig alloyFalse = new PrimSig("False", alloyBoolean, Attr.ONE);

		PrimSig alloyString = new PrimSig("AlloyString", Attr.ABSTRACT);
		PrimSig emptyString = new PrimSig("EmptyString", alloyString, Attr.ONE);
		PrimSig noEmptyString = new PrimSig("NoEmptyString", alloyString,
				Attr.ONE);

		if (type.equals(int.class) || type.equals(Integer.class)) {
			primSig = new PrimSig("INSTANCE");
			primSig.addField("value", Sig.SIGINT);
		} else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
			primSig = new PrimSig("INSTANCE");
			primSig.addField("value", alloyBoolean);
		} else if (type.equals(String.class)) {
			primSig = new PrimSig("INSTANCE");
			primSig.addField("value", alloyString);
		} else {
			primSig = new PrimSig(type.getCanonicalName());
			fields = extractFields(type);
			for (Field field : fields.values()) {
				if (field.getType().equals(String.class)) {
					primSig.addField(field.getName(), alloyString.loneOf());
				} else if (field.getType().equals(Integer.class)) {
					primSig.addField(field.getName(), Sig.SIGINT.loneOf());
				} else if (field.getType().equals(int.class)) {
					primSig.addField(field.getName(), Sig.SIGINT.oneOf());
				} else if (field.getType().equals(boolean.class)) {
					primSig.addField(field.getName(), alloyBoolean.oneOf());
				} else if (field.getType().equals(Boolean.class)) {
					primSig.addField(field.getName(), alloyBoolean.loneOf());
				}
			}
		}

		List<Sig> sigs = Arrays.asList(new Sig[] { alloyBoolean, alloyString,
				emptyString, noEmptyString, alloyTrue, alloyFalse, primSig });
		Expr expr1 = primSig.lone().and(
				alloyString.cardinality().lte(ExprConstant.makeNUMBER(2)));
		Command cmd1 = new Command(false, 3, 3, 3, expr1);
		solution = TranslateAlloyToKodkod
				.execute_command(null, sigs, cmd1, opt);
	}

	public PrimSig getPrimSig() {
		return primSig;
	}

	public Map<String, Field> getFields() {
		return fields;
	}

	private Map<String, Field> extractFields(Class<?> type) {
		Map<String, Field> fields = new HashMap<String, Field>();
		Method[] methods = type.getMethods();
		for (Method method : methods) {
			if (!method.getName().startsWith(PREFIXSETTER)) {
				continue;
			}
			Method getter;
			String fieldName;
			char firstLetter;
			try {
				getter = type.getMethod(PREFIXGETTER
						+ method.getName().substring(PREFIXSETTER.length()));
				fieldName = method.getName().substring(
						PREFIXSETTER.length() + 1);
				firstLetter = method.getName().charAt(PREFIXSETTER.length());
			} catch (NoSuchMethodException | SecurityException e) {
				try {
					getter = type
							.getMethod(PREFIXHAS
									+ method.getName().substring(
											PREFIXSETTER.length()));
					fieldName = method.getName().substring(
							PREFIXSETTER.length() + 1);
					firstLetter = method.getName()
							.charAt(PREFIXSETTER.length());
				} catch (NoSuchMethodException | SecurityException e1) {
					try {
						getter = type.getMethod(PREFIXIS
								+ method.getName().substring(
										PREFIXSETTER.length()));
						fieldName = method.getName().substring(
								PREFIXSETTER.length() + 1);
						firstLetter = method.getName().charAt(
								PREFIXSETTER.length());
					} catch (NoSuchMethodException | SecurityException e2) {
						// ignore the method
						continue;
					}
				}
			}
			firstLetter = Character.toLowerCase(firstLetter);
			fieldName = firstLetter + fieldName;
			fields.put(type.getCanonicalName() + fieldName, new Field(
					fieldName, getter.getReturnType()));
		}
		java.lang.reflect.Field[] f = type.getFields();
		for (int i = 0; i < f.length; i++) {
			if (fields.containsKey(type.getCanonicalName() + f[i].getName())) {
				continue;
			}
			fields.put(type.getCanonicalName() + f[i].getName(),
					new Field(f[i].getName(), f[i].getType()));
		}

		return fields;
	}

	public A4Solution getSolution() {
		return solution;
	}
}
