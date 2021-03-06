package com.lk.api.domain;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TypeModel implements Comparable<TypeModel>{
	
	private String name;
	private String description;
	List<MethodModel> methodModels = new ArrayList<MethodModel>();
	private String value;
	private String version;
	private int order;
	
	/**
     * 将对象按名称典序升序排序
     * @param o 当前对象
     * @return int
     */
    @Override
    public int compareTo(TypeModel o) {
    	//先按名称排序
    	if(o.getOrder()==1000000 && this.getOrder()==1000000)
    		return Collator.getInstance(Locale.CHINA).compare(this.name, o.getName());
    	//再按order排序
    	return this.getOrder()-o.getOrder();
    }
	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<MethodModel> getMethodModels() {
		return methodModels;
	}
	public void setMethodModels(List<MethodModel> methodModels) {
		this.methodModels = methodModels;
	}
	
	public static void main(String[] args) {
		Class<?> type = TypeModel.class;
		Method[] methods = type.getMethods();
		for (Method method : methods) {
			//Type type2 = method.getAnnotatedReturnType().getType();
			//System.out.println(type2.getTypeName());
			
			//Class<?>[] parameters = method.getParameterTypes();
			Parameter[] parameters = method.getParameters();
			for (Parameter p : parameters) {
				System.out.println(p.getName());
			}
			/*Class<?>[] parameterTypes = method.getParameterTypes();
			Parameter[] parameters = method.getParameters();
			for(int i = 0;i<parameterTypes.length;i++) {
				System.out.println(parameterTypes[i]);
				System.out.println(parameters[i].getAnnotation(LKAGroup.class));
			}
			System.out.println(parameters.length+":"+parameterTypes.length);*/
			/*Class<?> returnType = method.getReturnType();
			System.out.println(returnType.getAnnotation(LKAarg.class));*/
		}
		
		
	}
	
}
