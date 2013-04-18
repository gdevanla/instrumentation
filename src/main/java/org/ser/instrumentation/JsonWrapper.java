package org.ser.instrumentation;

import java.util.ArrayList;
import java.util.List;

abstract class AbstractJsonObject{
	public String encloseQuotes(Object s){
		return "";
	}
	
	public abstract String toJsonString();
}

class JsonPair extends AbstractJsonObject //TODO: this inheritance is wrong, fix it!!
{
	final String name;
	final AbstractJsonObject o;
	
	public JsonPair(String name, AbstractJsonObject o){
		this.name = name;
		this.o = o;
	}
	
	@Override
	public String toJsonString() {
		return "\\\"" + name + "\\\":" + o.toJsonString();
	}
}

class JsonObject extends AbstractJsonObject {
	//final String name;
	final List<JsonPair> value;
	
	public JsonObject(){
		//this.name = name;
		this.value = new ArrayList<JsonPair>();
	}
	
	public String toJsonString(){
	
		StringBuffer sb = new StringBuffer();
		
		for ( JsonPair p: value){
			sb.append(p.toJsonString()).append(",");
		}
		
		if (sb.length()==0) return "{}";		
		return "{" + sb.substring(0, sb.length()-1).toString() + "}";
		
	}
}

class JsonValue extends AbstractJsonObject{
	Object o;
	
	public JsonValue(Object o){
		this.o = o;
	}
	
	public String toJsonString(){
		if ( o instanceof String)
		{
			return "\\\"" + o + "\\\"";
		}
		else if ( o instanceof Integer)
		{
			return o.toString();
		}
		else if ( o instanceof AbstractJsonObject){
			return ((AbstractJsonObject)o).toJsonString();
		}
		else
		{
			return o.toString(); //TODO: take care of nulls, boolean when needed.
		}
	}
}

class JsonArray<T1 extends AbstractJsonObject> extends AbstractJsonObject {
	List<T1> values = new ArrayList<T1>();
	
	public String toJsonString(){
		StringBuffer sb = new StringBuffer("[");
		for(T1 v:values){
			sb.append(v.toJsonString()).append(",");
		}
		if (sb.length()==1) return "[]";
		sb.replace(sb.length()-1, sb.length(), "]");
		return sb.toString();	
	}

}


public class JsonWrapper {

	public static void main(String[] args){
		
		
		JsonPair methodName = new JsonPair("MethodName", new JsonValue("sayHelloWorld"));
		
		JsonPair j1 = new JsonPair("variableName", new JsonValue("x"));
		JsonPair j2 = new JsonPair("variableType", new JsonValue("y"));
		JsonObject variables = new JsonObject();
		variables.value.add(j1);
		variables.value.add(j2);		
		
		
		
		JsonArray<JsonObject> array = new JsonArray<JsonObject>();
		array.values.add(variables);
		array.values.add(variables);
		
		JsonPair localVariables = new JsonPair("LocalVariables", array);
		

		
		JsonPair j3 = new JsonPair("variableName", new JsonValue("x1"));
		JsonPair j4 = new JsonPair("variableType", new JsonValue("y1"));
		JsonObject variablesType = new JsonObject();
		variablesType.value.add(j3);
		variablesType.value.add(j4);
		JsonPair localVariablesType = new JsonPair("LocalVariablesType", variablesType);
		
		
		
		
		JsonObject methodLevel = new JsonObject();
		methodLevel.value.add(localVariablesType);
		methodLevel.value.add(localVariables);
		methodLevel.value.add(methodName);
		
		System.out.println(methodLevel.toJsonString());
		
		
		String s = "test\\\"fsa\\\"fsa";
		System.out.println(s);
		
		s = "test\"fsa\"fsa";
		
		System.out.println(s.replace("\"", "\\\""));
		
		
		
		

		
	}
	
	
}






