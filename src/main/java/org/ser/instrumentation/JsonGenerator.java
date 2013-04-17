
package org.ser.instrumentation;

import javassist.CtBehavior;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;


public class JsonGenerator{

	public static String getMethodInfo(CtBehavior method) {

		CodeAttribute codeAttribute = method.getMethodInfo().getCodeAttribute();
		LocalVariableAttribute locals = null;


		JsonPair methodName = new JsonPair("MethodName", new JsonValue(method.getName()));

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
		methodLevel.value.add(new JsonPair("Annotations", allAnnotations));

		return methodLevel.toJsonString();

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
		for ( int i=0; i<locals.tableLength(); i++){
			JsonObject o = new JsonObject();
			o.value.add(new JsonPair("variableType", new JsonValue(locals.descriptor(i))));
			o.value.add(new JsonPair("variableName", new JsonValue(locals.variableName(i))));
			variableList.values.add(o);
		}

		return variableList;
	}
}