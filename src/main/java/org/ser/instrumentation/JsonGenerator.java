
package org.ser.instrumentation;

import javassist.CtBehavior;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;


public class JsonGenerator{

	public static String getMethodInfo(CtClass cl, CtBehavior method) {

		CodeAttribute codeAttribute = method.getMethodInfo().getCodeAttribute();
		LocalVariableAttribute locals = null;

		if ( codeAttribute == null){
			System.out.println("Code attribute null for method=" + method.getName());
		}
		JsonPair className = new JsonPair("ClassName", new JsonValue(cl.getName()));
		JsonPair methodName = new JsonPair("MethodName", new JsonValue(method.getName()));
		JsonPair superClass;
		try {
			superClass = new JsonPair("SuperClassName", 
					new JsonValue(cl.getSuperclass().getName()));
		} catch (NotFoundException e) {
			superClass = new JsonPair("SuperClassName", new JsonValue("java.lang.Object"));
		}

		AttributeInfo attribute;
		attribute = codeAttribute.getAttribute("LocalVariableTable");
		locals = (LocalVariableAttribute) attribute;
		JsonArray<JsonObject> localVariableInfo = getLocalVariableInfo(locals);
		JsonPair localVariable = new JsonPair("LocalVariables", localVariableInfo);


		attribute = codeAttribute.getAttribute("LocalVariableTypeTable");
		locals = (LocalVariableAttribute) attribute;
		JsonArray<JsonObject> localVariableTypeInfo = getLocalVariableInfo(locals);
		JsonPair localVariableType = new JsonPair("LocalVariableTypes", localVariableTypeInfo);
		
		JsonArray<JsonValue> allAnnotations = getAvailableAnnotations(method);

		JsonObject methodLevel = new JsonObject();
		methodLevel.value.add(localVariableType);
		methodLevel.value.add(localVariable);
		methodLevel.value.add(methodName);
		methodLevel.value.add(className);
		methodLevel.value.add(superClass);
		
		methodLevel.value.add(new JsonPair("Annotations", allAnnotations));

		JsonObject loggerLevel = new JsonObject();
		loggerLevel.value.add(new JsonPair("MethodInstrumentation", methodLevel));
		
		return loggerLevel.toJsonString();

	}

	private static JsonArray<JsonValue> getAvailableAnnotations(CtBehavior method){
		JsonArray<JsonValue> allAnnotations = new JsonArray<JsonValue>();
		try
		{
			Object[] annotations = method.getAvailableAnnotations();
			for ( Object o: annotations){
				JsonValue v = new JsonValue(o);
				allAnnotations.values.add(v);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
			System.out.println("Instrumentation Error:Exception occurred" +
					" while parsing annotations:" + ex.getMessage());
		}

		return allAnnotations;
	}

	private static JsonArray<JsonObject> getLocalVariableInfo(LocalVariableAttribute locals) {

		JsonArray<JsonObject> variableList = new JsonArray<JsonObject>();
		if ( locals == null) return variableList;
		for ( int i=0; i<locals.tableLength(); i++){
			JsonObject o = new JsonObject();
			o.value.add(new JsonPair("variableType", new JsonValue(locals.descriptor(i))));
			o.value.add(new JsonPair("variableName", new JsonValue(locals.variableName(i))));
			variableList.values.add(o);
		}

		return variableList;
	}
}