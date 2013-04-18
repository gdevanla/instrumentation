package org.ser.instrumentation;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class LoggerAgent implements ClassFileTransformer {
	
	//String loggerDef = "private static java.util.logging.Logger _log;";
	
	//public static String regEx = "*";
	
	public static void premain(String agentArgument,
			Instrumentation instrumentation) {

		ClassLoader cl = ClassLoader.getSystemClassLoader();
		
		System.out.println("Entered PreMain");
		
		URL[] urls = ((URLClassLoader)cl).getURLs();
		for(URL url: urls){
			System.out.println(url.getFile());
		}

		if (agentArgument != null) {
			String[] args = agentArgument.split(",");
			Set<String> argSet = new HashSet<String>(Arrays.asList(args));

			if (argSet.contains("time")) {
				System.out.println("Start at " + new Date());
				Runtime.getRuntime().addShutdownHook(new Thread() {
					public void run() {
						System.out.println("Stop at " + new Date());
					}
				});
			}
			// ... more agent option handling here
		}
		instrumentation.addTransformer(new LoggerAgent());
		
		System.out.println("Leaving PreMain");
	}

	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {		
		
		if (className.contains("edu/ncsu/csc/itrust/")){
			System.out.println(className);
			return doClass(className, classBeingRedefined, classfileBuffer);
		}

		//System.out.println("The classname loaded was" + className);
		return classfileBuffer;
	}
	
	
	private byte[] doClass(String name, Class<?> clazz, byte[] b) {

		
	    CtClass cl = null;
	    try {
	    	System.out.println("outside here===" + name);
		    ClassPool pool = ClassPool.getDefault();
		    System.out.println("jjjjddddd="+name);
	    
	      cl = pool.makeClass(new java.io.ByteArrayInputStream(b));
	      System.out.println("jjjj="+name+cl.isInterface());
	      if (cl.isInterface() == false) {

	        //CtField field = CtField.make("private static org.slf4j.Logger logger;", cl);
	        //String getLogger = " org.slf4j.LoggerFactory.getLogger("
	        //    + name.replace('/', '.') + ".class.getName());";
	        //cl.addField(field, getLogger);

	        CtBehavior[] methods = cl.getDeclaredBehaviors();
	        System.out.println("outside here");
	        for (int i = 0; i < methods.length; i++) {
	        	 System.out.println("outside here=" + methods[i].getName());
	          if (methods[i].isEmpty() == false ){// && (methods[i]).getName().equals("sayHelloWorld")) {
	        	  System.out.println("here=" + methods[i].getName());
	            doMethod(cl, methods[i]);
	          }
	        }
	        b = cl.toBytecode();
	      }
	    } catch (Exception e) {
	    	e.printStackTrace();
	      System.err.println("Could not instrument  " + name
	          + ",  exception : " + e.getMessage());
	    } finally {
	      if (cl != null) {
	        cl.detach();
	      }
	    }
	    return b;
	  }
	
	
	private void doMethod(CtClass cl, CtBehavior method)
		      throws NotFoundException, CannotCompileException {

		    //System.out.println("inside do method for "+ method.getName());
		    String signature = JsonGenerator.getMethodInfo(cl, method);
		    //String returnValue = JavassistHelper.returnValue(method);
		   // signature = signature.replaceAll("\"", "\\\"");

		    String message = "System.out.println(\"" + signature  + "\");"; //".replace(\"X\", \"\"\")"
		   // System.out.println("Inside " + method + "with parameters= " + message);
		   // System.out.println("Over here ==>" + message);		    
		    //System.out.println(message);
		    method.insertBefore(message);
		    //method.insertAfter("logger.info(" + signature + returnValue + ");");
		    //method.insertAfter("_log.info(" + JavassistHelper.getSignature(method) + ")");
		    
		  }
}
	
