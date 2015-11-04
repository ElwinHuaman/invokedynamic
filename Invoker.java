package com.elwin;

import java.lang.invoke.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
 
public class Invoker {
 
	public static class Employee {
 
        private String name = " Elwin";
 
        public String getName() {
            return name + " invocado";
        }
        public void setName(String name) {
            this.name = name;
        }
        public String name() {
            return name;
        }
    }
	
    public static void main(String[] arg) throws Throwable {
    	
    	MethodHandles.Lookup lookup = MethodHandles.lookup();
        Class thisClass = lookup.lookupClass();  // (who am I?)

        MethodType methodType;
        MethodHandle methodHandle;
        
        Employee employee = new Employee();
        String name;
        
        Field fieldName = null;
        for (Field field : Employee.class.getDeclaredFields()) {
            if (field.getName().equals("name")) {
                fieldName = field;
                fieldName.setAccessible(true);
                break;
            }
        }
        
        //Lookup invoke dynamic
        methodType = MethodType.methodType(String.class);
        methodHandle = lookup.findVirtual(Employee.class, "getName", methodType);
        name = (String) methodHandle.invokeExact(new Employee());
        System.out.println("invoke dynamic " + name);
        
        //Lookup reflection
        Method method = Employee.class.getMethod("getName", new Class<?>[]{});
        name = (String) method.invoke(new Employee());
        System.out.println("reflection " + name);
        
        //Lookup Handle Field Direct
        MethodHandle methodHandleFieldDirect = lookup.unreflectGetter(fieldName);
        name = (String) methodHandleFieldDirect.invokeExact(new Employee());
        System.out.println("method handle for field direct " + name);
                
        long start = 0;
        long end = 0;
        long times = 10_000;
        long regularTime;
        long invokeDynamicTime;
        long reflectionTime;
        long count=0;
        long invokeDynamicTimeUsingField;
        long fieldDirect;
        
      //warm up
        for (int index =0 ; index < times; index++) {
            employee.getName();
            name = (String) methodHandle.invokeExact(employee);
            name = (String) method.invoke(employee);
            name = (String) methodHandleFieldDirect.invokeExact(employee);
        }
        
        // regular method call time
        start = System.nanoTime();
        for (int index =0 ; index < times; index++) {
            name = employee.getName();
            count += name.hashCode();
        }
        count=0;
        end = System.nanoTime();
        regularTime = end - start;
        System.out.printf("regular method call time        			= %d\n", regularTime/times);
        
        //invoke dynamic method call time
        start = System.nanoTime();
        for (int index =0 ; index < times; index++) {
            name = (String) methodHandle.invokeExact(employee);
            count += name.hashCode();
        }
        count=0;
        end = System.nanoTime();
        invokeDynamicTime = end - start;

        System.out.printf("invoke dynamic method call time 			= %d\n", invokeDynamicTime/times);
        
        //reflection method call time
        start = System.nanoTime();
        for (int index =0 ; index < times; index++) {
            name = (String) method.invoke(employee);
            count += name.hashCode();
        }
        count=0;
        end = System.nanoTime();
        reflectionTime = end - start;
        System.out.printf("reflection method call time     			= %d\n", reflectionTime/times);
    	
        //
        start = System.nanoTime();
        for (int index =0 ; index < times; index++) {
            name = (String) methodHandleFieldDirect.invokeExact(employee);
            count += name.hashCode();
        }
        count=0;
        end = System.nanoTime();
        invokeDynamicTimeUsingField = end - start;
        System.out.printf("field method invoke dynamic call time     		= %d\n", invokeDynamicTimeUsingField/times);

        //
        start = System.nanoTime();
        for (int index =0 ; index < times; index++) {
            name = (String) fieldName.get(employee);
            count += name.hashCode();
        }
        count=0;
        end = System.nanoTime();
        fieldDirect = end - start;
        System.out.printf("field method reflection call time     			= %d\n", fieldDirect/times);
    }
}
