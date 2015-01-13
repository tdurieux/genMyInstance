package com.github.tdurieux.instanceGenerator;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Attr;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
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
	private Map<String, Field> fields = new HashMap<>();
	private A4Solution solution;
	private Sig primSig;
	private Map<String, Sig> sigs = new HashMap<>();
	private Expr constraints;
	private PrimSig alloyBoolean;
	private PrimSig alloyString;
	private boolean isArray = false;

	public <T> AlloyModelFactory(Class<T> type) throws Err {
		this.type = type;
		createAlloyModel();
	}

	private void createAlloyModel() throws Err {
		createAlloyPrimitiveTypes();

		if (type.isArray()) {
			type = (Class<?>) type.getComponentType();
			this.isArray = true;
		}

		if (type.equals(int.class) || type.equals(Integer.class)) {
			primSig = new PrimSig("INSTANCE");
			primSig.addField("value", Sig.SIGINT);
			sigs.put(type.getCanonicalName(), primSig);
		} else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
			primSig = new PrimSig("INSTANCE");
			primSig.addField("value", alloyBoolean);
			sigs.put(type.getCanonicalName(), primSig);
		} else if (type.equals(String.class)) {
			primSig = new PrimSig("INSTANCE");
			primSig.addField("value", alloyString);
			sigs.put(type.getCanonicalName(), primSig);
		} else {
			primSig = createType(type);
		}
		if (!isArray) {
			constraints = primSig.lone();
		} else {
			constraints = primSig.some();
		}
	}

	private Sig createType(Class<?> type) throws Err {
		if (sigs.containsKey(type.getCanonicalName())) {
			return sigs.get(type.getCanonicalName());
		}
		PrimSig sig = new PrimSig(type.getCanonicalName());
		sigs.put(type.getCanonicalName(), sig);
		fields.putAll(extractFields(type));
		for (Field field : fields.values()) {
			addField(sig, field);
		}
		return sig;
	}

	private void addField(Sig sig, Field field) throws Err {
		boolean isArray = false;
		Class<?> fieldType = field.getType();
		if(fieldType.isArray()) {
			isArray = true;
			fieldType = fieldType.getComponentType();
		} 
		if (fieldType.equals(String.class)) {
			if(isArray) {
				sig.addField(field.getName(), alloyString.setOf());
			} else { 
				sig.addField(field.getName(), alloyString.loneOf());
			}
		} else if (fieldType.equals(Integer.class)) {
			if(isArray) {
				sig.addField(field.getName(), Sig.SIGINT.setOf());
			} else { 
				sig.addField(field.getName(), Sig.SIGINT.loneOf());
			}
		} else if (fieldType.equals(int.class)) {
			if(isArray) {
				sig.addField(field.getName(), Sig.SIGINT.setOf());
			} else { 
				sig.addField(field.getName(), Sig.SIGINT.oneOf());
			}
		} else if (fieldType.equals(Float.class)) {
			if(isArray) {
				sig.addField(field.getName(), Sig.SIGINT.setOf());
			} else { 
				sig.addField(field.getName(), Sig.SIGINT.loneOf());
			}
		} else if (fieldType.equals(float.class)) {
			if(isArray) {
				sig.addField(field.getName(), Sig.SIGINT.setOf());
			} else { 
				sig.addField(field.getName(), Sig.SIGINT.oneOf());
			}
		} else if (fieldType.equals(Double.class)) {
			if(isArray) {
				sig.addField(field.getName(), Sig.SIGINT.setOf());
			} else { 
				sig.addField(field.getName(), Sig.SIGINT.loneOf());
			}
		} else if (fieldType.equals(double.class)) {
			if(isArray) {
				sig.addField(field.getName(), Sig.SIGINT.setOf());
			} else { 
				sig.addField(field.getName(), Sig.SIGINT.oneOf());
			}
		} else if (fieldType.equals(boolean.class)) {
			if(isArray) {
				sig.addField(field.getName(), alloyBoolean.setOf());
			} else { 
				sig.addField(field.getName(), alloyBoolean.oneOf());
			}
		} else if (fieldType.equals(Boolean.class)) {
			if(isArray) {
				sig.addField(field.getName(), alloyBoolean.setOf());
			} else { 
				sig.addField(field.getName(), alloyBoolean.loneOf());
			}
		} else {
			Sig fieldSig = createType(field.getType());
			if(isArray) {
				sig.addField(field.getName(), fieldSig.setOf());
			} else { 
				sig.addField(field.getName(), fieldSig.loneOf());
			}
		}
	}

	private void createAlloyPrimitiveTypes() throws Err {
		this.alloyBoolean = new PrimSig("Boolean", Attr.ABSTRACT);
		sigs.put("Boolean", alloyBoolean);
		PrimSig alloyTrue = new PrimSig("True", alloyBoolean, Attr.ONE);
		sigs.put("True", alloyTrue);
		PrimSig alloyFalse = new PrimSig("False", alloyBoolean, Attr.ONE);
		sigs.put("False", alloyFalse);
		this.alloyString = new PrimSig("AlloyString", Attr.ABSTRACT);
		sigs.put("AlloyString", alloyString);
		PrimSig emptyString = new PrimSig("EmptyString", alloyString, Attr.ONE);
		sigs.put("EmptyString", emptyString);
		PrimSig noEmptyString = new PrimSig("NoEmptyString", alloyString,
				Attr.ONE);
		sigs.put("NoEmptyString", noEmptyString);

		/*
		 * PrimSig alloyChar = new PrimSig("AlloyChar", Attr.ONE);
		 * sigs.put("AlloyChar", alloyChar);
		 * 
		 * PrimSig alloyFloat = new PrimSig("AlloyFloat", Attr.ONE);
		 * sigs.put("AlloyFloat", alloyFloat);
		 * 
		 * PrimSig alloyDouble = new PrimSig("AlloyDouble", Attr.ONE);
		 * sigs.put("AlloyDouble", alloyDouble);
		 * 
		 * PrimSig alloyByte = new PrimSig("AlloyByte", Attr.ONE);
		 * sigs.put("AlloyByte", alloyByte);
		 */

		//constraints = alloyString.cardinality().lte(ExprConstant.makeNUMBER(2));
	}

	public Sig getPrimSig() {
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

	public A4Solution getSolution() throws Err {
		if (solution == null) {
			A4Options opt = new A4Options();
			opt.solver = A4Options.SatSolver.SAT4J;
			Command runCommand = new Command(false, 3, 3, 3, constraints);
			solution = TranslateAlloyToKodkod.execute_command(null,
					sigs.values(), runCommand, opt);
		}

		return solution;
	}
}
