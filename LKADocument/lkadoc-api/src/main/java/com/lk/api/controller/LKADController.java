package com.lk.api.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import com.lk.api.annotation.*;
import com.lk.api.constant.*;
import com.lk.api.domain.MethodModel;
import com.lk.api.domain.ModelModel;
import com.lk.api.domain.ParamModel;
import com.lk.api.domain.PropertyModel;
import com.lk.api.domain.ResposeModel;
import com.lk.api.domain.TypeModel;

/**
 * 	文档信息处理类
 * @author liukai
 *
 */
@RestController
@RequestMapping("lkad")
public class LKADController {
	
	@Autowired
	private WebApplicationContext applicationContext;
	
	/* 扫描包路径 */
	@Value("${lkad.basePackages:}")
	private String basePackages;
	/* 项目名称 */
	@Value("${lkad.projectName:lkadoc接口文档}")
	private String projectName;
	/* 项目描述 */
	@Value("${lkad.description:智能、便捷、高效！}")
	private String description;
	/* 文档开关 */
	@Value("${lkad.enabled:true}")
	private Boolean enabled;
	/*服务器名称*/
	@Value("${lkad.serverNames:}")
	private String serverNames;
	/*项目版本号*/
	@Value("${lkad.version:}")
	private String version;
	/* 扫描所有参数 */
	@Value("${lkad.sconAll:false}")
	private Boolean sconAll;
	/*项目接口文档密码*/
	@Value("${lkad.password:}")
	private String password;

	private int reqNum = 0,respNum = 0,proNum = 0;
	/**
	 * 	调试其它项目的接口
	 * @param path 路径
	 * @param contentType 内容
	 * @param headerJson 请求头
	 * @param queryData 请求数据
	 * @param type 类型
	 * @return Object 对象
	 */
	@SuppressWarnings("rawtypes")
	@GetMapping("getServerApi")
	public Object getServerApi(String path,String contentType,String headerJson,String queryData,String type) {
		Map<String, Object> headerMap = JsonUtils.toMap(headerJson);
		Map<String, Object> queryMap = JsonUtils.toMap(queryData);
		RestTemplate restTemplate = new RestTemplate();
		//设置请求头
		HttpHeaders requestHeaders = new HttpHeaders();
		Set<String> hSet = headerMap.keySet();
		for (String key : hSet) {
			requestHeaders.add(key,headerMap.get(key)==null?"":headerMap.get(key).toString());
		}
		
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new ByteArrayHttpMessageConverter());
        /** 解决中文乱码的converter */
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(Charset.forName
                ("UTF-8"));
        messageConverters.add(stringHttpMessageConverter);
        messageConverters.add(new ResourceHttpMessageConverter());
        messageConverters.add(new SourceHttpMessageConverter());
        messageConverters.add(new AllEncompassingFormHttpMessageConverter());
        restTemplate.setMessageConverters(messageConverters);
		
        //HttpEntity
		Object object = null;
		
		//设置参数
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        Set<String> qSet = queryMap.keySet();
        String data = "";
        int i = 0;
        for (String key : qSet) {
        	requestBody.add(key,queryMap.get(key)==null?"":queryMap.get(key).toString());
        	if(i == 0) {
        		data = "?"+key+"="+queryMap.get(key);
        	}else {
        		data = data + "&"+key+"="+queryMap.get(key);
        	}
        	i++;
		}
		if("application/json".equals(contentType)) {
	        HttpEntity<String> requestEntity = new HttpEntity<>(queryData, requestHeaders);
	        if("get".equals(type.toLowerCase())) {
	        	return "<span style='color:red'>ContentType值为application/json时只支持post、put、delete请求！<span>";
	        	//object = restTemplate.exchange(path+data,HttpMethod.GET,requestEntity,String.class);
	        }
	        if("post".equals(type.toLowerCase())) {
	        	object = restTemplate.exchange(path,HttpMethod.POST,requestEntity, String.class);
	        }
	        if("put".equals(type.toLowerCase())) {
	        	object = restTemplate.exchange(path,HttpMethod.PUT,requestEntity, String.class);
	        }
	        if("delete".equals(type.toLowerCase())) {
	        	object = restTemplate.exchange(path,HttpMethod.DELETE,requestEntity, String.class);
	        }
		}else {
	        HttpEntity<MultiValueMap> requestEntity = new HttpEntity<>(requestBody, requestHeaders);
	        
	        if("get".equals(type.toLowerCase())) {
	        	object = restTemplate.exchange(path+data,HttpMethod.GET,null,String.class);
	        }
	        if("post".equals(type.toLowerCase())) {
	        	object = restTemplate.exchange(path,HttpMethod.POST,requestEntity, String.class);
	        }
	        if("put".equals(type.toLowerCase())) {
	        	object = restTemplate.exchange(path,HttpMethod.PUT,requestEntity, String.class);
	        }
	        if("delete".equals(type.toLowerCase())) {
	        	object = restTemplate.exchange(path,HttpMethod.DELETE,requestEntity, String.class);
	        }
		}
		return object;
	}
	
	/**
	 * 判断是否需要密码
	 * @return int 1要、0不要
	 */
	@GetMapping("isPwd")
	public int isPwd() {
		if("".equals(basePackages)) {
			Map<String, Object> beans = applicationContext.getBeansWithAnnotation(LKADocument.class);
			if(beans != null && beans.size()>0) {
				Set<String> keySet = beans.keySet();
				for (String key : keySet) {
					Object obj = beans.get(key);
					Class<? extends Object> bootClass = obj.getClass();
					LKADocument annotation = bootClass.getAnnotation(LKADocument.class);
					password = annotation.password();
				}
			}
		}
		if(password == null || "".equals(password)) return 0;
		else return 1;
	}
	
	/**
	 * 	加载接口文档所有信息
	 * @param serverName 服务器名称
	 * @param pwd 密码
	 * @param type 类型
	 * @return Map 集合
	 * @throws Exception 异常
	 */
	@SuppressWarnings("unchecked")
	@GetMapping("doc")
	public Map<String, Object> loadLKADocument(String serverName,String pwd,int type) throws Exception {
		String bpk = "";
		Map<String, Object> map = new HashMap<String, Object>();
		if(serverName != null && !"".equals(serverName)) {
			RestTemplate restTemplate = new RestTemplate();
			try {
				map = restTemplate.getForObject(serverName+"/lkad/doc?pwd="+pwd+"&type="+type, Map.class);
				return map;
			} catch (Exception e) {
				map.put("error",serverName+"服务器连接失败！");
				return map;
			}
		}
		
		if(!"".equals(basePackages)) {
			bpk = basePackages;
			map.put("projectName", projectName);
			map.put("description", description);
			map.put("enabled", enabled?"yes":"no");
			map.put("serverNames",serverNames);
			map.put("version",version);
			if(!enabled) {
				map.put("error", "Lkadoc接口文档功能已关闭");
				return map;
			}
		}else {
			Map<String, Object> beans = applicationContext.getBeansWithAnnotation(LKADocument.class);
			boolean bool = false;
			if(beans != null && beans.size()>0) {
				Set<String> keySet = beans.keySet();
				for (String key : keySet) {
					Object obj = beans.get(key);
					Class<? extends Object> bootClass = obj.getClass();
					LKADocument annotation = bootClass.getAnnotation(LKADocument.class);
					bpk = annotation.basePackages();
					sconAll = annotation.sconAll();
					if("".equals(bpk)) {
						bool = false;
						break;
					}
					map.put("projectName", annotation.projectName());
					map.put("description", annotation.description());
					map.put("serverNames", annotation.serverNames());
					map.put("enabled", annotation.enabled()?"yes":"no");
					map.put("version",annotation.version());
					password = annotation.password();
					if(!annotation.enabled()) {
						bpk = "";
						map.put("error", "Lkadoc接口文档功能已关闭");
						return map;
					}
					bool = true;
					break;
				}
			}
			if(!bool) {
				bpk = "";
				map.put("error", "没有设置要扫描的包路径");
				return map;
			}
		}
		
		//密码判断
		if(password != null && !"".equals(password) && type == 1) {
			if(!password.equals(pwd)) {
				bpk = "";
				map.put("error", "密码不正确，您无权操作文档");
				return map;
			}
		}
		
		//排序算法
		List<TypeModel> typeModels = scanType(bpk.split(","));
		//类排序
		Collections.sort(typeModels);
		List<MethodModel> tempMethod = new ArrayList<>();
		for (TypeModel typeModel : typeModels) {
			List<MethodModel> methods = typeModel.getMethodModels();
			Iterator<MethodModel> iterator = methods.iterator();
			while(iterator.hasNext()) {
				MethodModel method = iterator.next();
				List<ResposeModel> ResposeModels = method.getRespose();
				List<ResposeModel> rms = new ArrayList<ResposeModel>();
				List<ResposeModel> rms2 = new ArrayList<ResposeModel>();
				for (ResposeModel m : ResposeModels) {
					if(m.getParentName()==null || "".equals(m.getParentName())) {
						rms.add(m);
					}else {
						rms2.add(m);
					}
				}
				int n = 0;
				sortResposeModel(rms,rms2,n);
				method.setRespose(rms);
				if(!"".equals(method.getDirectory())) {
					tempMethod.add(method);
					iterator.remove();
				}
			}
		}
		
		//目录转移
		for (TypeModel typeModel : typeModels) {
			Iterator<MethodModel> iterator = tempMethod.iterator();
			while(iterator.hasNext()) {
				MethodModel methodModel = iterator.next();
				if(typeModel.getName().equals(methodModel.getDirectory())) {
					List<MethodModel> methodModels = typeModel.getMethodModels();
					methodModels.add(methodModel);
					iterator.remove();
				}
			}
			//方法排序
			Collections.sort(typeModel.getMethodModels());
		}
		map.put("apiDoc",typeModels);
		return map;
	}
	
	/**
	 * 	字段信息排序
	 * @param rms  排序前
	 * @param rms2  排序后
	 * @param n 记录递归层数，防止死循环
	 */
	public void sortResposeModel(List<ResposeModel> rms,List<ResposeModel> rms2,int n){
		n++;
		if(rms2 == null || rms2.size() < 1 || n == 20){
			return;
		}
		Iterator<ResposeModel> iterator = rms2.iterator();
		while(iterator.hasNext()) {
			ResposeModel next = iterator.next();
			for (ResposeModel m : rms) {
				if(m.getValue().equals(next.getParentName())) {
					rms.add(next);
					iterator.remove();
					break;
				}
			}
		}
		sortResposeModel(rms,rms2,n);
	}

	/**
	 * 	根据包名获取文件对象
	 * 
	 * @param basePackages 包名
	 * @return list 集合
	 */
	public List<File> getFile(String[] basePackages) {
		List<File> packageFiles = new ArrayList<File>();
		for (String basePackage : basePackages) {
			String path = basePackage.replace(".", "/");
			URL url = this.getClass().getClassLoader().getResource(path);
			if (url == null)
				return null;
			path = url.getPath().replace("!","");
			File packageFile = new File(path);
			packageFiles.add(packageFile);
		}
		return packageFiles;
	}

	
	/**
	 *	 扫描所有子包
	 * @param baseFile 基础包名
	 * @return list 集合
	 */
	public List<File> queryFiles(File baseFile) {
		File[] files = baseFile.listFiles();
		List<File> allFiles = new ArrayList<File>();
		if (files == null || files.length < 1) return null;
		for (File file : files) {
			if (file == null || file.getName() == null) continue;
			if (file.isDirectory()) {
				List<File> queryFiles = queryFiles(file);
				if(queryFiles == null) return allFiles;
				allFiles.addAll(queryFiles);
			} else {
				allFiles.add(file);
			}
		}
		return allFiles;
	}
	
	
	/**
	 * 	扫描所有满足条件的接口，获取它们的入参出参相关信息
	 * @param basePackages 要扫描的包
	 * @return list 集合
	 * @throws Exception 异常
	 */
	public List<TypeModel> scanType(String[] basePackages) throws Exception {
		List<TypeModel> typeModels = new ArrayList<TypeModel>();
		// 判断是否是目录
		if (basePackages != null) {
			// 获取所有方法的请求信息
			List<Map<String, Object>> methodURLs = getMethodURL();
			List<String> sconPackages = new ArrayList<>();
			for (String basePackage : basePackages) {
				SconPackage sconPackage = new SconPackage(basePackage);
				sconPackages.addAll(sconPackage.getFullyQualifiedClassNameList());
			}
			
			for (String sconPackage : sconPackages) {
				Class<?> cls = Class.forName(sconPackage);
				// 判断是否有LKAType或者Api注解
				if (!cls.isAnnotationPresent(LKAType.class) && !cls.isAnnotationPresent(Api.class))continue;
				TypeModel typeModel = new TypeModel();
				if(cls.isAnnotationPresent(LKAType.class)) {
					LKAType lkaType = cls.getAnnotation(LKAType.class);
					if(lkaType.hidden())continue;
					typeModel.setName(lkaType.value());
					typeModel.setDescription(lkaType.description());
					typeModel.setOrder(lkaType.order());
				}else {
					Api api = cls.getAnnotation(Api.class);
					if(api.hidden())continue;
					typeModel.setName(api.tags());
					typeModel.setDescription(api.description());
					typeModel.setOrder(api.order());
				}
				// 获取类描述信息
				typeModel.setValue(cls.getSimpleName());
				// 获取所有方法
				Method[] methods = cls.getMethods();
				if (methods != null && methods.length > 0) {
					List<MethodModel> methodModels = new ArrayList<MethodModel>();
					for (Method method : methods) {
						if (method == null)continue;
						MethodModel methodModel = new MethodModel();
						// 判断是否有LKAMethod注解
						if (!method.isAnnotationPresent(LKAMethod.class) && !method.isAnnotationPresent(ApiOperation.class)) continue;
						if(method.isAnnotationPresent(LKAMethod.class)) {
							LKAMethod lkaMethod = method.getAnnotation(LKAMethod.class);
							if(lkaMethod.hidden())continue;
							// 获取方法描述信息
							methodModel.setName(lkaMethod.value());
							methodModel.setDescription(lkaMethod.description());
							methodModel.setValue(method.getName());
							methodModel.setUrl("该API未设置请求路径");
							methodModel.setVersion(lkaMethod.version());
							methodModel.setContentType(lkaMethod.contentType());
							methodModel.setRequestType("未知");
							methodModel.setAuthor(lkaMethod.author());
							methodModel.setCreateTime(lkaMethod.createTime());
							methodModel.setUpdateTime(lkaMethod.updateTime());
							methodModel.setDownload(lkaMethod.download());
							methodModel.setToken(lkaMethod.token());
							methodModel.setOrder(lkaMethod.order());
							methodModel.setDirectory(lkaMethod.directory());
						}else {
							ApiOperation lkaMethod  = method.getAnnotation(ApiOperation.class);
							if(lkaMethod.hidden())continue;
							// 获取方法描述信息
							methodModel.setName(lkaMethod.value());
							methodModel.setDescription(lkaMethod.notes());
							methodModel.setValue(method.getName());
							methodModel.setUrl("该API未设置请求路径");
							methodModel.setVersion(lkaMethod.version());
							methodModel.setContentType(lkaMethod.contentType());
							methodModel.setRequestType("未知");
							methodModel.setAuthor(lkaMethod.author());
							methodModel.setCreateTime(lkaMethod.createTime());
							methodModel.setUpdateTime(lkaMethod.updateTime());
							methodModel.setDownload(lkaMethod.download());
							methodModel.setToken(lkaMethod.token());
							methodModel.setOrder(lkaMethod.order());
							methodModel.setDirectory(lkaMethod.directory());
						}
						
						for (Map<String, Object> map : methodURLs) {
							if(map.get("className") != null) {
								if (method.getDeclaringClass().getName().equals(map.get("className").toString()) && method.getName().equals(map.get("methodName"))) {
									Object url = map.get("methodURL");
									Object requestType = map.get("requestType");
									if (url == null) {
										url = "该API未设置请求路径";
										requestType = "未知";
									}else {
										url = url.toString().substring(1,url.toString().length());
									}
									if (url != null && requestType == null) {
										requestType = "通用";
									}
									methodModel.setUrl(url.toString());
									methodModel.setRequestType(requestType.toString());
								}else {
									@SuppressWarnings("unchecked")
									List<String> list = (List<String>)map.get("interfacesNames");
									for (String str : list) {
										if(method.getDeclaringClass().getName().equals(str) && method.getName().equals(map.get("methodName"))) {
											Object url = map.get("methodURL");
											Object requestType = map.get("requestType");
											if (url == null) {
												url = "该API未设置请求路径";
												requestType = "未知";
											}else {
												url = url.toString().substring(1,url.toString().length());
											}
											if (url != null && requestType == null) {
												requestType = "通用";
											}
											methodModel.setUrl(url.toString());
											methodModel.setRequestType(requestType.toString());
											break;
										}
									}
								}
							}
						}

						List<ParamModel> request = new ArrayList<ParamModel>();
						List<ResposeModel> respose = new ArrayList<ResposeModel>();
						/******/
						//自动判断出参类型是否是model对象
						Class<?> returnType = method.getReturnType();
						if(returnType != null && !"void".equals(returnType.getName())) {
							boolean bool2 = false;
							boolean isParentArray = false;
							Class<?> proType = null;
							Class<?> parentProType = null;
							if(returnType.isArray()) {//数组
								// 获取数组元素的类型
								proType = returnType.getComponentType();
								isParentArray = true;
							}else {
								//当前集合的泛型类型
								Type genericReturnType = method.getGenericReturnType();
								if(genericReturnType instanceof ParameterizedType) {
			                        ParameterizedType pt = (ParameterizedType) genericReturnType;
			                        //得到泛型里的class类型对象
									try {
										proType = (Class<?>)pt.getActualTypeArguments()[0];
										if(returnType.getSimpleName().equals("List") || 
											returnType.getSimpleName().equals("Set") ||
											returnType.getSimpleName().equals("ArrayList") ||
											returnType.getSimpleName().equals("LinkedList") ||
											returnType.getSimpleName().equals("Vector") ||
											returnType.getSimpleName().equals("SortedSet") ||
											returnType.getSimpleName().equals("HashSet") ||
											returnType.getSimpleName().equals("TreeSet") ||
											returnType.getSimpleName().equals("LinkedHashSet")) {
											isParentArray = true;
										}
									} catch (Exception e) {
										try {
											ParameterizedType pt2 =(ParameterizedType)pt.getActualTypeArguments()[0];
											proType = (Class<?>)pt2.getActualTypeArguments()[0];
											parentProType = (Class<?>)pt2.getRawType();
										} catch (Exception e1) {
											//最多判断两层,两层以上待定
										}
									}
								}
							}
							if(isParentArray || returnType.isAnnotationPresent(LKAModel.class) || returnType.isAnnotationPresent(ApiModel.class)){
								if(isParentArray) {
									returnType = proType;
								}
								// 获取model描述信息
								ModelModel modelModel = new ModelModel();
								modelModel.setValue(returnType.getSimpleName());
								// 获取所有属性对象
								Field[] fields = returnType.getDeclaredFields();
								
								//获取父类所有属性对象
								Field[] declaredField;
								try {
									declaredField = getDeclaredField(returnType.newInstance());
								} catch (Exception e) {
									declaredField = null;
								}
								Object[] arrays = null;
								//合并数组
								if(declaredField != null) {
									List<Field> list = new ArrayList<>(Arrays.asList(declaredField));
									arrays = list.toArray();
								}else {
									arrays = fields;
								}
								
								if (arrays != null && arrays.length > 0) {
									List<PropertyModel> propertyModels = new ArrayList<PropertyModel>();
									for (Object obj : arrays) {
										Field field = (Field)obj;
										boolean bool = false;
										String pValue = field.getName();
										//过滤LKARespose注解与对象属性重复的字段
										if (method.isAnnotationPresent(LKAResposes.class)) {
											LKAResposes lKAResposes = method.getAnnotation(LKAResposes.class);
											LKARespose[] resps = lKAResposes.value();
											if(resps != null && resps.length > 0) {
												for (LKARespose resp : resps) {
													if(resp.type() != null && resp.type().getTypeName().equals(returnType.getTypeName())) {
														bool2 = true;
														break;
													}
													if(resp.name().equals(pValue)) {
														bool = true;
														break;
													}
												}
											}
										}
										if (method.isAnnotationPresent(LKARespose.class)) {
											LKARespose resp = method.getAnnotation(LKARespose.class);
											if(resp.type() != null && resp.type().getTypeName().equals(returnType.getTypeName())) {
												bool2 = true;
												break;
											}
											if(resp.name().equals(pValue)) {
												bool = true;
											}
										}
										if(bool2) {
											break;
										}
										if(!bool) {
											Class<?> type = field.getType();
											if(proType != null) {
												if("Object".equals(type.getSimpleName())) {
													type = proType;
												}
											}
											if (!field.isAnnotationPresent(LKAProperty.class) && 
													!field.isAnnotationPresent(ApiModelProperty.class)) {
												if(sconAll) {
													PropertyModel propertyModel = new PropertyModel();
													//int isArray = isObj(type);
													type = getGenericType(type, field);
													
													//判断数据类型
													propertyModel.setDataType(type.getSimpleName());
													int flag = isObj(type);
										            if(flag == 3) {
										            	propertyModel = analysisProModel(methodModel.getUrl(),type,"",2);
										            	if(propertyModel == null) { 
										            		propertyModel = new PropertyModel();
										            		propertyModel.setDataType("Object");
										            	}
										            }
										            String name = field.getName();
										            propertyModel.setArray(false);
										            if(isObj(type) == 2 || 
															isObj(type) == 4 || 
															(field.getType().getSimpleName().equals("Object") && 
																	parentProType != null && 
																	(parentProType.getSimpleName().equals("List") || 
																	 parentProType.getSimpleName().equals("Set") ||
																	 parentProType.getSimpleName().equals("ArrayList") ||
																	 parentProType.getSimpleName().equals("LinkedList") ||
																	 parentProType.getSimpleName().equals("Vector") ||
																	 parentProType.getSimpleName().equals("SortedSet") ||
																	 parentProType.getSimpleName().equals("HashSet") ||
																	 parentProType.getSimpleName().equals("TreeSet") ||
																	 parentProType.getSimpleName().equals("LinkedHashSet")
																	 ))) {
										            	propertyModel.setArray(true);
														propertyModel.setDataType(propertyModel.getDataType()+"[]");
													}
									            	propertyModel.setName(enToCn(methodModel.getUrl(),2,name));
									            	propertyModel.setValue(name);
									            	propertyModel.setRequired(false);
									            	propertyModel.setDescription("");
									            	propertyModel.setTestData("");
													try {
														if(type.isAnnotationPresent(PathVariable.class)) {
															propertyModel.setParamType(ParamType.PATH);
														}else if(type.isAnnotationPresent(RequestHeader.class)) {
															propertyModel.setParamType(ParamType.HEADER);
														}else {
															propertyModel.setParamType(ParamType.QUERY);
														}
													} catch (Exception e) {
														propertyModel.setParamType(ParamType.QUERY);
													}
													propertyModels.add(propertyModel);
												}
											}else {
												PropertyModel propertyModel = null;
												Class<?> ptype = getGenericType(type, field);
												if(field.isAnnotationPresent(LKAProperty.class)){
													LKAProperty property = field.getAnnotation(LKAProperty.class);
													if(property.hidden()) continue;
													if(property.type().getName().equals("java.lang.Object") && isObj(ptype) != 3) {
														propertyModel = new PropertyModel();
													}else {
														if(isObj(ptype) == 3) {
															propertyModel = analysisProModel(methodModel.getUrl(),ptype,null,2);
															if(propertyModel == null) {
																propertyModel = new PropertyModel();
																propertyModel.setDataType("Object");
															}
														}else {
															propertyModel = analysisProModel(methodModel.getUrl(),property.type(),null,2);
															if(propertyModel == null) {
																propertyModel = new PropertyModel();
																propertyModel.setDataType("Object");
															}
														}
													}
													propertyModel.setArray(false);
													if(isObj(type) == 2 || 
															isObj(type) == 4 || 
															(field.getType().getSimpleName().equals("Object") && 
																	parentProType != null && 
																	(parentProType.getSimpleName().equals("List") || 
																	 parentProType.getSimpleName().equals("Set") ||
																	 parentProType.getSimpleName().equals("ArrayList") ||
																	 parentProType.getSimpleName().equals("LinkedList") ||
																	 parentProType.getSimpleName().equals("Vector") ||
																	 parentProType.getSimpleName().equals("SortedSet") ||
																	 parentProType.getSimpleName().equals("HashSet") ||
																	 parentProType.getSimpleName().equals("TreeSet") ||
																	 parentProType.getSimpleName().equals("LinkedHashSet")
																			))) {
														propertyModel.setArray(true);
													}
													propertyModel.setValue(pValue);
													propertyModel.setName(property.value());
													String[] split = property.value().split("\\^");
													if(split.length == 2) {
														propertyModel.setName(split[0]);
														propertyModel.setTestData(split[1]);
													}
													String[] split2 = split[0].split("\\~");
													if(split2.length == 2) {
														propertyModel.setName(split2[1]);
														if(split2[0].contains("n"))propertyModel.setRequired(false);
													}
													propertyModel.setDescription(property.description());
													propertyModel.setDataType(field.getType().getSimpleName());
													propertyModels.add(propertyModel);
												}else {
													ApiModelProperty property = field.getAnnotation(ApiModelProperty.class);
													if(property.hidden()) continue;
													if(property.type().getName().equals("java.lang.Object") && isObj(ptype) != 3) {
														propertyModel = new PropertyModel();
													}else {
														if(isObj(ptype) == 3) {
															propertyModel = analysisProModel(methodModel.getUrl(),ptype,null,2);
															if(propertyModel == null) {
																propertyModel = new PropertyModel();
																propertyModel.setDataType("Object");
															}
														}else {
															propertyModel = analysisProModel(methodModel.getUrl(),property.type(),null,2);
															if(propertyModel == null) {
																propertyModel = new PropertyModel();
																propertyModel.setDataType("Object");
															}
														}
													}
													propertyModel.setArray(false);
													if(isObj(type) == 2 || 
															isObj(type) == 4 || 
															(field.getType().getSimpleName().equals("Object") && 
																	parentProType != null && 
																	(parentProType.getSimpleName().equals("List") || 
																	 parentProType.getSimpleName().equals("Set") ||
																	 parentProType.getSimpleName().equals("ArrayList") ||
																	 parentProType.getSimpleName().equals("LinkedList") ||
																	 parentProType.getSimpleName().equals("Vector") ||
																	 parentProType.getSimpleName().equals("SortedSet") ||
																	 parentProType.getSimpleName().equals("HashSet") ||
																	 parentProType.getSimpleName().equals("TreeSet") ||
																	 parentProType.getSimpleName().equals("LinkedHashSet")
																	 ))) {
														propertyModel.setArray(true);
													}
													propertyModel.setValue(pValue);
													propertyModel.setName(property.value());
													String[] split = property.value().split("\\^");
													if(split.length == 2) {
														propertyModel.setName(split[0]);
														propertyModel.setTestData(split[1]);
													}
													String[] split2 = split[0].split("\\~");
													if(split2.length == 2) {
														propertyModel.setName(split2[1]);
														if(split2[0].contains("n"))propertyModel.setRequired(false);
													}
													propertyModel.setDescription(property.description());
													propertyModel.setDataType(field.getType().getSimpleName());
													propertyModels.add(propertyModel);
				
												}	
											}
										}
									}
										
									modelModel.setPropertyModels(propertyModels);
									ResposeModel resposeModel = new ResposeModel();
									resposeModel.setValue(modelModel.getValue());
									resposeModel.setModelModel(modelModel);
									respose.add(resposeModel);
								}
							}
						}		
						
						//自动判断入参类型是否是model对象
						Class<?>[] parameterTypes = method.getParameterTypes();
						Parameter[] parameters = method.getParameters();
						if(parameters != null && parameters.length > 0) {
							for (int i = 0;i<parameterTypes.length;i++) {
								boolean bool = false,bool2=false;
								Class<?> argument = parameterTypes[i];
								Class<?> type = parameters[i].getType();
								boolean isArray = false;
								if(type.equals(List.class) || 
						        		type.equals(Set.class) || 
						        		type.equals(ArrayList.class) || 
						        		type.equals(LinkedList.class) ||
						        		type.equals(Vector.class) ||
						        		type.equals(SortedSet.class) ||
						        		type.equals(HashSet.class) ||
						        		type.equals(TreeSet.class) ||
						        		type.equals(LinkedHashSet.class)) { //集合
									isArray = true;
									// 当前集合的泛型类型
				                    Type genericType = parameters[i].getParameterizedType();
				                    if (null == genericType) {
				                        continue;
				                    }
				                    if (genericType instanceof ParameterizedType) {
				                        ParameterizedType pt = (ParameterizedType) genericType;
				                        //得到泛型里的class类型对象
				                        try {
											argument = (Class<?>)pt.getActualTypeArguments()[0];
										} catch (Exception e) {
											//continue;
										}
				                    }
								}
								if(type.isArray()) {//数组
									// 获取数组元素的类型
									isArray = true;
									argument = type.getComponentType();					                    
								}

								if(argument.isAnnotationPresent(LKAModel.class) || argument.isAnnotationPresent(ApiModel.class)){
									if (method.isAnnotationPresent(LKAParams.class)) {
										LKAParams lkaParams = method.getAnnotation(LKAParams.class);
										LKAParam[] params = lkaParams.value();
										for (LKAParam pa : params) {
											if(pa.type().getTypeName().equals(argument.getTypeName())) {
												bool2 = true;
												break;
											}
											if(pa.value().equals(parameters[i].getName())) {
												bool = true;
												break;
											}
										}
									}
									if (method.isAnnotationPresent(LKAParam.class)) {
										LKAParam pa = method.getAnnotation(LKAParam.class);
										if(pa.type().getTypeName().equals(argument.getTypeName())) {
											bool2 = true;
										}
										if(pa.value().equals(parameters[i].getName())) {
											bool = true;
										}
									}
									if (method.isAnnotationPresent(ApiImplicitParams.class)) {
										ApiImplicitParams lkaParams = method.getAnnotation(ApiImplicitParams.class);
										ApiImplicitParam[] params = lkaParams.value();
										for (ApiImplicitParam pa : params) {
											if(pa.type().getTypeName().equals(argument.getTypeName())) {
												bool2 = true;
											}
											if(pa.value().equals(parameters[i].getName())) {
												bool = true;
												break;
											}
										}
									}
									if (method.isAnnotationPresent(ApiImplicitParam.class)) {
										ApiImplicitParam pa = method.getAnnotation(ApiImplicitParam.class);
										if(pa.type().getTypeName().equals(argument.getTypeName())) {
											bool2 = true;
										}
										if(pa.value().equals(parameters[i].getName())) {
											bool = true;
										}
									}
									if(bool2) {
										break;
									}
									if(bool) {
										continue;
									}
									
									String  group= "";
									if(parameters[i].isAnnotationPresent(LKAGroup.class)) {
										group = parameters[i].getAnnotation(LKAGroup.class).value();
									}
									ParamModel paramModel = analysisModel(methodModel.getUrl(),argument,group);
									if (paramModel == null) {
										paramModel = new ParamModel();
									}
									if(argument.isAnnotationPresent(LKAModel.class)) {
										paramModel.setArray(isArray);
										paramModel.setValue(argument.getSimpleName());
										paramModel.setName(argument.getAnnotation(LKAModel.class).value());
										paramModel.setDescription(argument.getAnnotation(LKAModel.class).description());
										paramModel.setDataType("Object");
										request.add(paramModel);
									}else {
										paramModel.setArray(isArray);
										paramModel.setValue(argument.getSimpleName());
										paramModel.setName(argument.getAnnotation(ApiModel.class).value());
										paramModel.setDescription(argument.getAnnotation(ApiModel.class).description());
										paramModel.setDataType("Object");
										request.add(paramModel);
									}
								}
							}
						}
						/******/
						
						// 判断入参注解
						if (method.isAnnotationPresent(LKAParams.class) || method.isAnnotationPresent(ApiImplicitParams.class)) {
							if(method.isAnnotationPresent(LKAParams.class)) {
								LKAParams lkaParams = method.getAnnotation(LKAParams.class);
								LKAParam[] params = lkaParams.value();
								if (params != null && params.length > 0) {
									for (LKAParam param : params) {
										// 获取参数描述信息
										Class<?> type = param.type();
										if (!type.getName().equals("java.lang.Object")) { // 说明入参是对象
											ParamModel paramModel = analysisModel(methodModel.getUrl(),type,param.group());
											if (paramModel == null) {
												paramModel = new ParamModel();
											}
											paramModel.setArray(param.isArray());
											paramModel.setValue(param.name());
											paramModel.setName(param.value());
											paramModel.setDescription(param.description());
											paramModel.setDataType("Object");
											request.add(paramModel);
										} else {
											if(param.name() != null && !"".equals(param.name())) {
												ParamModel paramModel = new ParamModel();
												String[] split = param.name().split("-");
												if(split != null && split.length==2) {
													if("n".equals(split[1].toLowerCase())) {
														paramModel.setRequired(false);
													}else {
														paramModel.setRequired(true);
													}
													paramModel.setValue(split[0]);
												}else {
													paramModel.setRequired(param.required());
													paramModel.setValue(param.name());
												}
												paramModel.setDataType(param.dataType().getSimpleName());
												if("Object".equals(param.dataType().getSimpleName())) {
													paramModel.setDataType("String");
													try {
														for(Parameter ps:parameters) {
															if(ps.getName().equals(paramModel.getValue())){
																paramModel.setDataType(ps.getType().getSimpleName());
																break;
															}
														}
													} catch (Exception e) {
													}
												}
												paramModel.setDescription(param.description());
												paramModel.setName(param.value());
												paramModel.setTestData(param.testData());
												String[] split2 = param.value().split("\\^");
												if(split2.length == 2) {
													paramModel.setName(split2[0]);
													paramModel.setTestData(split2[1]);
												}
												String[] split3 = split2[0].split("\\~");
												if(split3.length == 2) {
													paramModel.setName(split3[1]);
													if(split3[0].contains("n"))paramModel.setRequired(false);
												}
												paramModel.setParamType(param.paramType());
												if("".equals(param.paramType())) {
													paramModel.setParamType(ParamType.QUERY);
													try {
														for(Parameter ps:parameters) {
															if(ps.getName().equals(paramModel.getValue())){
																if(ps.isAnnotationPresent(PathVariable.class)) {
																	paramModel.setParamType(ParamType.PATH);
																}else if(ps.isAnnotationPresent(RequestHeader.class)) {
																	paramModel.setParamType(ParamType.HEADER);
																}else {
																	paramModel.setParamType(ParamType.QUERY);
																}
																break;
															}
														}
													} catch (Exception e) {
														//todo
													}
												}
												paramModel.setArray(param.isArray());
												request.add(paramModel);
											}
											if(param.values() != null && param.values().length>0) {
												try {
													String[] names = null;
													if(param.names() != null && param.names().length>0) {
														names = param.names();
													}else {
														names = new String[parameters.length];
														for (int i = 0;i < parameters.length;i++) {
															names[i] = parameters[i].getName();
														}
													}
													for(int i = 0;i<names.length;i++) {
														ParamModel paramModel = new ParamModel();
														String[] split = names[i].split("-");
														if(split != null && split.length==2) {
															if("n".equals(split[1].toLowerCase())) {
																paramModel.setRequired(false);
															}else {
																paramModel.setRequired(true);
															}
															paramModel.setValue(split[0]);
														}else {
															if(param.requireds()!= null && param.requireds().length>0){
																try {
																	paramModel.setRequired(param.requireds()[i]);
																} catch (Exception e) {
																	paramModel.setRequired(param.requireds()[0]);
																}
															}else {
																paramModel.setRequired(param.required());
															}
															paramModel.setValue(names[i]);
														}
														
														if(param.dataTypes()!= null && param.dataTypes().length>0){
															try {
																paramModel.setDataType(param.dataTypes()[i].getSimpleName());
															} catch (Exception e) {
																paramModel.setDataType(param.dataTypes()[0].getSimpleName());
															}
														}else {
															paramModel.setDataType(parameters[i].getType().getSimpleName());
														}
														
														if(param.descriptions()!= null && param.descriptions().length>0){
															try {
																paramModel.setDescription(param.descriptions()[i]);
															} catch (Exception e) {
																paramModel.setDescription(param.descriptions()[0]);
															}
														}else {
															paramModel.setDescription(param.description());
														}
														if(param.testDatas()!= null && param.testDatas().length>0){
															try {
																paramModel.setTestData(param.testDatas()[i]);
															} catch (Exception e) {
																paramModel.setTestData(param.testDatas()[0]);
															}
														}else {
															paramModel.setTestData(param.testData());
														}
														if(param.values()!= null && param.values().length>0){
															try {
																paramModel.setName(param.values()[i]);
																String[] split2 = param.values()[i].split("\\^");
																if(split2.length == 2) {
																	paramModel.setName(split2[0]);
																	paramModel.setTestData(split2[1]);
																}
																String[] split3 = split2[0].split("\\~");
																if(split3.length == 2) {
																	paramModel.setName(split3[1]);
																	if(split3[0].contains("n"))paramModel.setRequired(false);
																}
															} catch (Exception e) {
																paramModel.setName(param.values()[0]);
																String[] split2 = param.values()[0].split("\\^");
																if(split2.length == 2) {
																	paramModel.setName(split2[0]);
																	paramModel.setTestData(split2[1]);
																}
																String[] split3 = split2[0].split("\\~");
																if(split3.length == 2) {
																	paramModel.setName(split3[1]);
																	if(split3[0].contains("n"))paramModel.setRequired(false);
																}
															}
														}else {
															paramModel.setName(param.value());
															String[] split2 = param.value().split("\\^");
															if(split2.length == 2) {
																paramModel.setName(split2[0]);
																paramModel.setTestData(split2[1]);
															}
															String[] split3 = split2[0].split("\\~");
															if(split3.length == 2) {
																paramModel.setName(split3[1]);
																if(split3[0].contains("n"))paramModel.setRequired(false);
															}
														}
														
														if(param.paramTypes()!= null && param.paramTypes().length>0){
															try {
																paramModel.setParamType(param.paramTypes()[i]);
															} catch (Exception e) {
																paramModel.setParamType(param.paramTypes()[0]);
															}
														}else {
															try {
																if(parameters[i].isAnnotationPresent(PathVariable.class)) {
																	paramModel.setParamType(ParamType.PATH);
																}else if(parameters[i].isAnnotationPresent(RequestHeader.class)) {
																	paramModel.setParamType(ParamType.HEADER);
																}else {
																	paramModel.setParamType(param.paramType());
																}
															} catch (Exception e) {
																paramModel.setParamType(param.paramType());
															}
														}
														if(param.isArrays()!= null && param.isArrays().length>0){
															try {
																paramModel.setArray(param.isArrays()[i]);
															} catch (Exception e) {
																paramModel.setArray(param.isArrays()[0]);
															}
														}else {
															paramModel.setArray(param.isArray());
														}
														request.add(paramModel);
													}
												} catch (Exception e) {
													ParamModel paramModel = new ParamModel();
													paramModel.setName("<span style='color:red;'>"+e.getClass().getSimpleName()+":"+e.getMessage()+"</span>");
													paramModel.setValue("<span style='color:red;'>该接口参数异常！</span>");
													request.add(paramModel);
												}
											}
										}
									}
								}
							}else {
								ApiImplicitParams lkaParams = method.getAnnotation(ApiImplicitParams.class);
								ApiImplicitParam[] params = lkaParams.value();
								if (params != null && params.length > 0) {
									for (ApiImplicitParam param : params) {
										// 获取参数描述信息
										Class<?> type = param.type();
										if (!type.getName().equals("java.lang.Object")) { // 说明入参是对象
											ParamModel paramModel = analysisModel(methodModel.getUrl(),type,param.group());
											if (paramModel == null) {
												paramModel = new ParamModel();
											}
											paramModel.setArray(param.isArray());
											paramModel.setValue(param.name());
											paramModel.setName(param.value());
											paramModel.setDescription(param.description());
											paramModel.setDataType("Object");
											request.add(paramModel);
										} else {
											if(param.name() != null && !"".equals(param.name())) {
												ParamModel paramModel = new ParamModel();
												String[] split = param.name().split("-");
												if(split != null && split.length==2) {
													if("n".equals(split[1].toLowerCase())) {
														paramModel.setRequired(false);
													}else {
														paramModel.setRequired(true);
													}
													paramModel.setValue(split[0]);
												}else {
													paramModel.setRequired(param.required());
													paramModel.setValue(param.name());
												}
												paramModel.setDataType(param.dataType().getSimpleName());
												if("Object".equals(param.dataType().getSimpleName())) {
													paramModel.setDataType("String");
													try {
														for(Parameter ps:parameters) {
															if(ps.getName().equals(paramModel.getValue())){
																paramModel.setDataType(ps.getType().getSimpleName());
																break;
															}
														}
													} catch (Exception e) {
													}
												}
												paramModel.setDescription(param.description());
												paramModel.setName(param.value());
												paramModel.setTestData(param.testData());
												String[] split2 = param.value().split("\\^");
												if(split2.length == 2) {
													paramModel.setName(split2[0]);
													paramModel.setTestData(split2[1]);
												}
												String[] split3 = split2[0].split("\\~");
												if(split3.length == 2) {
													paramModel.setName(split3[1]);
													if(split3[0].contains("n"))paramModel.setRequired(false);
												}
												paramModel.setParamType(param.paramType());
												if("".equals(param.paramType())) {
													paramModel.setParamType(ParamType.QUERY);
													try {
														for(Parameter ps:parameters) {
															if(ps.getName().equals(paramModel.getValue())){
																if(ps.isAnnotationPresent(PathVariable.class)) {
																	paramModel.setParamType(ParamType.PATH);
																}else if(ps.isAnnotationPresent(RequestHeader.class)) {
																	paramModel.setParamType(ParamType.HEADER);
																}else {
																	paramModel.setParamType(ParamType.QUERY);
																}
																break;
															}
														}
													} catch (Exception e) {
														//todo
													}
												}
												paramModel.setArray(param.isArray());
												request.add(paramModel);
											}
											if(param.values() != null && param.values().length>0) {
												try {
													String[] names = null;
													if(param.names() != null && param.names().length>0) {
														names = param.names();
													}else {
														names = new String[parameters.length];
														for (int i = 0;i < parameters.length;i++) {
															names[i] = parameters[i].getName();
														}
													}
													for(int i = 0;i<names.length;i++) {
														ParamModel paramModel = new ParamModel();
														String[] split = names[i].split("-");
														if(split != null && split.length==2) {
															if("n".equals(split[1].toLowerCase())) {
																paramModel.setRequired(false);
															}else {
																paramModel.setRequired(true);
															}
															paramModel.setValue(split[0]);
														}else {
															if(param.requireds()!= null && param.requireds().length>0){
																try {
																	paramModel.setRequired(param.requireds()[i]);
																} catch (Exception e) {
																	paramModel.setRequired(param.requireds()[0]);
																}
															}else {
																paramModel.setRequired(param.required());
															}
															paramModel.setValue(names[i]);
														}
														if(param.dataTypes()!= null && param.dataTypes().length>0){
															try {
																paramModel.setDataType(param.dataTypes()[i].getSimpleName());
															} catch (Exception e) {
																paramModel.setDataType(param.dataTypes()[0].getSimpleName());
															}
														}else {
															paramModel.setDataType(parameters[i].getType().getSimpleName());
														}
														if(param.descriptions()!= null && param.descriptions().length>0){
															try {
																paramModel.setDescription(param.descriptions()[i]);
															} catch (Exception e) {
																paramModel.setDescription(param.descriptions()[0]);
															}
														}else {
															paramModel.setDescription(param.description());
														}
														if(param.testDatas()!= null && param.testDatas().length>0){
															try {
																paramModel.setTestData(param.testDatas()[i]);
															} catch (Exception e) {
																paramModel.setTestData(param.testDatas()[0]);
															}
														}else {
															paramModel.setTestData(param.testData());
														}
														if(param.values()!= null && param.values().length>0){
															try {
																paramModel.setName(param.values()[i]);
																String[] split2 = param.values()[i].split("\\^");
																if(split2.length == 2) {
																	paramModel.setName(split2[0]);
																	paramModel.setTestData(split2[1]);
																}
																String[] split3 = split2[0].split("\\~");
																if(split3.length == 2) {
																	paramModel.setName(split3[1]);
																	if(split3[0].contains("n"))paramModel.setRequired(false);
																}
															} catch (Exception e) {
																paramModel.setName(param.values()[0]);
																String[] split2 = param.values()[0].split("\\^");
																if(split2.length == 2) {
																	paramModel.setName(split2[0]);
																	paramModel.setTestData(split2[1]);
																}
																String[] split3 = split2[0].split("\\~");
																if(split3.length == 2) {
																	paramModel.setName(split3[1]);
																	if(split3[0].contains("n"))paramModel.setRequired(false);
																}
															}
														}else {
															paramModel.setName(param.value());
															String[] split2 = param.value().split("\\^");
															if(split2.length == 2) {
																paramModel.setName(split2[0]);
																paramModel.setTestData(split2[1]);
															}
															String[] split3 = split2[0].split("\\~");
															if(split3.length == 2) {
																paramModel.setName(split3[1]);
																if(split3[0].contains("n"))paramModel.setRequired(false);
															}
														}
														
														if(param.paramTypes()!= null && param.paramTypes().length>0){
															try {
																paramModel.setParamType(param.paramTypes()[i]);
															} catch (Exception e) {
																paramModel.setParamType(param.paramTypes()[0]);
															}
														}else {
															try {
																if(parameters[i].isAnnotationPresent(PathVariable.class)) {
																	paramModel.setParamType(ParamType.PATH);
																}else if(parameters[i].isAnnotationPresent(RequestHeader.class)) {
																	paramModel.setParamType(ParamType.HEADER);
																}else {
																	paramModel.setParamType(param.paramType());
																}
															} catch (Exception e) {
																paramModel.setParamType(param.paramType());
															}
														}
														if(param.isArrays()!= null && param.isArrays().length>0){
															try {
																paramModel.setArray(param.isArrays()[i]);
															} catch (Exception e) {
																paramModel.setArray(param.isArrays()[0]);
															}
														}else {
															paramModel.setArray(param.isArray());
														}
														request.add(paramModel);
													}
												} catch (Exception e) {
													ParamModel paramModel = new ParamModel();
													paramModel.setName("<span style='color:red;'>"+e.getClass().getSimpleName()+":"+e.getMessage()+"</span>");
													paramModel.setValue("<span style='color:red;'>该接口参数异常！</span>");
													request.add(paramModel);
												}
											}
										}
									}
								}
							}
							
						} else if(method.isAnnotationPresent(LKAParam.class) || method.isAnnotationPresent(ApiImplicitParam.class)) {
							if(method.isAnnotationPresent(LKAParam.class)) {
								LKAParam param = method.getAnnotation(LKAParam.class);
								// 获取参数描述信息
								Class<?> type = param.type();
								if (!type.getName().equals("java.lang.Object")) { // 说明入参是对象
									ParamModel paramModel = analysisModel(methodModel.getUrl(),type,param.group());
									if (paramModel == null) {
										paramModel = new ParamModel();
									}
									paramModel.setArray(param.isArray());
									paramModel.setValue(param.name());
									paramModel.setName(param.value());
									paramModel.setDescription(param.description());
									paramModel.setDataType("Object");
									request.add(paramModel);
								} else {
									if(param.name() != null && !"".equals(param.name())) {
										ParamModel paramModel = new ParamModel();
										String[] split = param.name().split("-");
										if(split != null && split.length==2) {
											if("n".equals(split[1].toLowerCase())) {
												paramModel.setRequired(false);
											}else {
												paramModel.setRequired(true);
											}
											paramModel.setValue(split[0]);
										}else {
											paramModel.setRequired(param.required());
											paramModel.setValue(param.name());
										}
										paramModel.setDataType(param.dataType().getSimpleName());
										if("Object".equals(param.dataType().getSimpleName())) {
											paramModel.setDataType("String");
											try {
												for(Parameter ps:parameters) {
													if(ps.getName().equals(paramModel.getValue())){
														paramModel.setDataType(ps.getType().getSimpleName());
														break;
													}
												}
											} catch (Exception e) {
											}
										}
										paramModel.setDescription(param.description());
										paramModel.setName(param.value());
										paramModel.setTestData(param.testData());
										String[] split2 = param.value().split("\\^");
										if(split2.length == 2) {
											paramModel.setName(split2[0]);
											paramModel.setTestData(split2[1]);
										}
										String[] split3 = split2[0].split("\\~");
										if(split3.length == 2) {
											paramModel.setName(split3[1]);
											if(split3[0].contains("n"))paramModel.setRequired(false);
										}
										paramModel.setParamType(param.paramType());
										if("".equals(param.paramType())) {
											paramModel.setParamType(ParamType.QUERY);
											try {
												for(Parameter ps:parameters) {
													if(ps.getName().equals(paramModel.getValue())){
														if(ps.isAnnotationPresent(PathVariable.class)) {
															paramModel.setParamType(ParamType.PATH);
														}else if(ps.isAnnotationPresent(RequestHeader.class)) {
															paramModel.setParamType(ParamType.HEADER);
														}else {
															paramModel.setParamType(ParamType.QUERY);
														}
														break;
													}
												}
											} catch (Exception e) {
												//todo
											}
										}
										paramModel.setArray(param.isArray());
										request.add(paramModel);
									}
									if(param.values() != null && param.values().length>0) {
										try {
											String[] names = null;
											if(param.names() != null && param.names().length>0) {
												names = param.names();
											}else {
												names = new String[parameters.length];
												for (int i = 0;i < parameters.length;i++) {
													names[i] = parameters[i].getName();
												}
											}
											for(int i = 0;i<names.length;i++) {
												ParamModel paramModel = new ParamModel();
												String[] split = names[i].split("-");
												if(split != null && split.length==2) {
													if("n".equals(split[1].toLowerCase())) {
														paramModel.setRequired(false);
													}else {
														paramModel.setRequired(true);
													}
													paramModel.setValue(split[0]);
												}else {
													if(param.requireds()!= null && param.requireds().length>0){
														try {
															paramModel.setRequired(param.requireds()[i]);
														} catch (Exception e) {
															paramModel.setRequired(param.requireds()[0]);
														}
													}else {
														paramModel.setRequired(param.required());
													}
													paramModel.setValue(names[i]);
												}
												if(param.dataTypes()!= null && param.dataTypes().length>0){
													try {
														paramModel.setDataType(param.dataTypes()[i].getSimpleName());
													} catch (Exception e) {
														paramModel.setDataType(param.dataTypes()[0].getSimpleName());
													}
												}else {
													paramModel.setDataType(parameters[i].getType().getSimpleName());
												}
												if(param.descriptions()!= null && param.descriptions().length>0){
													try {
														paramModel.setDescription(param.descriptions()[i]);
													} catch (Exception e) {
														paramModel.setDescription(param.descriptions()[0]);
													}
												}else {
													paramModel.setDescription(param.description());
												}
												if(param.testDatas()!= null && param.testDatas().length>0){
													try {
														paramModel.setTestData(param.testDatas()[i]);
													} catch (Exception e) {
														paramModel.setTestData(param.testDatas()[0]);
													}
												}else {
													paramModel.setTestData(param.testData());
												}
												if(param.values()!= null && param.values().length>0){
													try {
														paramModel.setName(param.values()[i]);
														String[] split2 = param.values()[i].split("\\^");
														if(split2.length == 2) {
															paramModel.setName(split2[0]);
															paramModel.setTestData(split2[1]);
														}
														String[] split3 = split2[0].split("\\~");
														if(split3.length == 2) {
															paramModel.setName(split3[1]);
															if(split3[0].contains("n"))paramModel.setRequired(false);
														}
													} catch (Exception e) {
														paramModel.setName(param.values()[0]);
														String[] split2 = param.values()[0].split("\\^");
														if(split2.length == 2) {
															paramModel.setName(split2[0]);
															paramModel.setTestData(split2[1]);
														}
														String[] split3 = split2[0].split("\\~");
														if(split3.length == 2) {
															paramModel.setName(split3[1]);
															if(split3[0].contains("n"))paramModel.setRequired(false);
														}
													}
												}else {
													paramModel.setName(param.value());
													String[] split2 = param.value().split("\\^");
													if(split2.length == 2) {
														paramModel.setName(split2[0]);
														paramModel.setTestData(split2[1]);
													}

													String[] split3 = split2[0].split("\\~");
													if(split3.length == 2) {
														paramModel.setName(split3[1]);
														if(split3[0].contains("n"))paramModel.setRequired(false);
													}
													
												}
												if(param.paramTypes()!= null && param.paramTypes().length>0){
													try {
														paramModel.setParamType(param.paramTypes()[i]);
													} catch (Exception e) {
														paramModel.setParamType(param.paramTypes()[0]);
													}
												}else {
													try {
														if(parameters[i].isAnnotationPresent(PathVariable.class)) {
															paramModel.setParamType(ParamType.PATH);
														}else if(parameters[i].isAnnotationPresent(RequestHeader.class)) {
															paramModel.setParamType(ParamType.HEADER);
														}else {
															paramModel.setParamType(param.paramType());
														}
													} catch (Exception e) {
														paramModel.setParamType(param.paramType());
													}
												}
												if(param.isArrays()!= null && param.isArrays().length>0){
													try {
														paramModel.setArray(param.isArrays()[i]);
													} catch (Exception e) {
														paramModel.setArray(param.isArrays()[0]);
													}
												}else {
													paramModel.setArray(param.isArray());
												}
												request.add(paramModel);
											}
										} catch (Exception e) {
											ParamModel paramModel = new ParamModel();
											paramModel.setName("<span style='color:red;'>"+e.getClass().getSimpleName()+":"+e.getMessage()+"</span>");
											paramModel.setValue("<span style='color:red;'>该接口参数异常！</span>");
											request.add(paramModel);
										}
									}
								}
							}else {
								ApiImplicitParam param = method.getAnnotation(ApiImplicitParam.class);
								// 获取参数描述信息
								Class<?> type = param.type();
								if (!type.getName().equals("java.lang.Object")) { // 说明入参是对象
									ParamModel paramModel = analysisModel(methodModel.getUrl(),type,param.group());
									if (paramModel == null) {
										paramModel = new ParamModel();
									}
									paramModel.setArray(param.isArray());
									paramModel.setValue(param.name());
									paramModel.setName(param.value());
									paramModel.setDescription(param.description());
									paramModel.setDataType("Object");
									request.add(paramModel);
								} else {
									if(param.name() != null && !"".equals(param.name())) {
										ParamModel paramModel = new ParamModel();
										String[] split = param.name().split("-");
										if(split != null && split.length==2) {
											if("n".equals(split[1].toLowerCase())) {
												paramModel.setRequired(false);
											}else {
												paramModel.setRequired(true);
											}
											paramModel.setValue(split[0]);
										}else {
											paramModel.setRequired(param.required());
											paramModel.setValue(param.name());
										}
										paramModel.setDataType(param.dataType().getSimpleName());
										if("Object".equals(param.dataType().getSimpleName())) {
											paramModel.setDataType("String");
											try {
												for(Parameter ps:parameters) {
													if(ps.getName().equals(paramModel.getValue())){
														paramModel.setDataType(ps.getType().getSimpleName());
														break;
													}
												}
											} catch (Exception e) {
											}
										}
										paramModel.setDescription(param.description());
										paramModel.setName(param.value());
										paramModel.setTestData(param.testData());
										String[] split2 = param.value().split("\\^");
										if(split2.length == 2) {
											paramModel.setName(split2[0]);
											paramModel.setTestData(split2[1]);
										}
										String[] split3 = split2[0].split("\\~");
										if(split3.length == 2) {
											paramModel.setName(split3[1]);
											if(split3[0].contains("n"))paramModel.setRequired(false);
										}
										paramModel.setParamType(param.paramType());
										if("".equals(param.paramType())) {
											paramModel.setParamType(ParamType.QUERY);
											try {
												for(Parameter ps:parameters) {
													if(ps.getName().equals(paramModel.getValue())){
														if(ps.isAnnotationPresent(PathVariable.class)) {
															paramModel.setParamType(ParamType.PATH);
														}else if(ps.isAnnotationPresent(RequestHeader.class)) {
															paramModel.setParamType(ParamType.HEADER);
														}else {
															paramModel.setParamType(ParamType.QUERY);
														}
														break;
													}
												}
											} catch (Exception e) {
												//todo
											}
										}
										paramModel.setArray(param.isArray());
										request.add(paramModel);
									}
									if(param.values() != null && param.values().length>0) {
										try {
											String[] names = null;
											if(param.names() != null && param.names().length>0) {
												names = param.names();
											}else {
												names = new String[parameters.length];
												for (int i = 0;i < parameters.length;i++) {
													names[i] = parameters[i].getName();
												}
											}
											for(int i = 0;i<names.length;i++) {
												ParamModel paramModel = new ParamModel();
												String[] split = names[i].split("-");
												if(split != null && split.length==2) {
													if("n".equals(split[1].toLowerCase())) {
														paramModel.setRequired(false);
													}else {
														paramModel.setRequired(true);
													}
													paramModel.setValue(split[0]);
												}else {
													if(param.requireds()!= null && param.requireds().length>0){
														try {
															paramModel.setRequired(param.requireds()[i]);
														} catch (Exception e) {
															paramModel.setRequired(param.requireds()[0]);
														}
													}else {
														paramModel.setRequired(param.required());
													}
													paramModel.setValue(names[i]);
												}
												if(param.dataTypes()!= null && param.dataTypes().length>0){
													try {
														paramModel.setDataType(param.dataTypes()[i].getSimpleName());
													} catch (Exception e) {
														paramModel.setDataType(param.dataTypes()[0].getSimpleName());
													}
												}else {
													paramModel.setDataType(parameters[i].getType().getSimpleName());
												}
												if(param.descriptions()!= null && param.descriptions().length>0){
													try {
														paramModel.setDescription(param.descriptions()[i]);
													} catch (Exception e) {
														paramModel.setDescription(param.descriptions()[0]);
													}
												}else {
													paramModel.setDescription(param.description());
												}
												if(param.testDatas()!= null && param.testDatas().length>0){
													try {
														paramModel.setTestData(param.testDatas()[i]);
													} catch (Exception e) {
														paramModel.setTestData(param.testDatas()[0]);
													}
												}else {
													paramModel.setTestData(param.testData());
												}
												if(param.values()!= null && param.values().length>0){
													try {
														paramModel.setName(param.values()[i]);
														String[] split2 = param.values()[i].split("\\^");
														if(split2.length == 2) {
															paramModel.setName(split2[0]);
															paramModel.setTestData(split2[1]);
														}

														String[] split3 = split2[0].split("\\~");
														if(split3.length == 2) {
															paramModel.setName(split3[1]);
															if(split3[0].contains("n"))paramModel.setRequired(false);
														}
														
													} catch (Exception e) {
														paramModel.setName(param.values()[0]);
														String[] split2 = param.values()[0].split("\\^");
														if(split2.length == 2) {
															paramModel.setName(split2[0]);
															paramModel.setTestData(split2[1]);
														}

														String[] split3 = split2[0].split("\\~");
														if(split3.length == 2) {
															paramModel.setName(split3[1]);
															if(split3[0].contains("n"))paramModel.setRequired(false);
														}
													}
												}else {
													paramModel.setName(param.value());
													String[] split2 = param.value().split("\\^");
													if(split2.length == 2) {
														paramModel.setName(split2[0]);
														paramModel.setTestData(split2[1]);
													}

													String[] split3 = split2[0].split("\\~");
													if(split3.length == 2) {
														paramModel.setName(split3[1]);
														if(split3[0].contains("n"))paramModel.setRequired(false);
													}
												}
												
												if(param.paramTypes()!= null && param.paramTypes().length>0){
													try {
														paramModel.setParamType(param.paramTypes()[i]);
													} catch (Exception e) {
														paramModel.setParamType(param.paramTypes()[0]);
													}
												}else {
													try {
														if(parameters[i].isAnnotationPresent(PathVariable.class)) {
															paramModel.setParamType(ParamType.PATH);
														}else if(parameters[i].isAnnotationPresent(RequestHeader.class)) {
															paramModel.setParamType(ParamType.HEADER);
														}else {
															paramModel.setParamType(param.paramType());
														}
													} catch (Exception e) {
														paramModel.setParamType(param.paramType());
													}
												}
												if(param.isArrays()!= null && param.isArrays().length>0){
													try {
														paramModel.setArray(param.isArrays()[i]);
													} catch (Exception e) {
														paramModel.setArray(param.isArrays()[0]);
													}
												}else {
													paramModel.setArray(param.isArray());
												}
												request.add(paramModel);
											}
										} catch (Exception e) {
											ParamModel paramModel = new ParamModel();
											paramModel.setName("<span style='color:red;'>"+e.getClass().getSimpleName()+":"+e.getMessage()+"</span>");
											paramModel.setValue("<span style='color:red;'>该接口参数异常！</span>");
											request.add(paramModel);
										}
									}
								}
							}
						}else { //没有入参注解，自动获取
							// 获取参数描述信息
							if(parameters != null && parameters.length > 0) {
								for(int i = 0;i<parameters.length;i++) {
									ParamModel paramModel = new ParamModel();
									Class<?> type = parameters[i].getType();
									boolean isArray = false;
									if(type.equals(List.class) || 
							        		type.equals(Set.class) || 
							        		type.equals(ArrayList.class) || 
							        		type.equals(LinkedList.class) ||
							        		type.equals(Vector.class) ||
							        		type.equals(SortedSet.class) ||
							        		type.equals(HashSet.class) ||
							        		type.equals(TreeSet.class) ||
							        		type.equals(LinkedHashSet.class)) { //list集合
										isArray = true;
										// 当前集合的泛型类型
					                    Type genericType = parameters[i].getParameterizedType();
					                    if (null == genericType) {
					                        continue;
					                    }
					                    if (genericType instanceof ParameterizedType) {
					                        ParameterizedType pt = (ParameterizedType) genericType;
					                        //得到泛型里的class类型对象
					                        try {
												type = (Class<?>)pt.getActualTypeArguments()[0];
											} catch (Exception e) {
												//continue;
											}
					                    }
									}else if(type.isArray()) {//数组
										isArray = true;
										// 获取数组元素的类型
										type = type.getComponentType();
									}
									if(type.isAnnotationPresent(LKAModel.class) || type.isAnnotationPresent(ApiModel.class)){
										continue;
									}
									
									//判断数据类型
									paramModel.setDataType(type.getSimpleName());
									int flag = isObj(type);
						            if(flag == 3) {
						            	paramModel = analysisModel(methodModel.getUrl(),type,"");
						            	if(paramModel == null) {
						            		paramModel = new ParamModel();
						            		paramModel.setDataType("Object");
						            	}
						            }

						            String name = parameters[i].getName();
									paramModel.setName(enToCn(methodModel.getUrl(),1,name));
								    paramModel.setValue(name);
								    paramModel.setArray(isArray);
								    if(isArray) {
										paramModel.setDataType(paramModel.getDataType()+"[]");
									}
									paramModel.setRequired(false);
									paramModel.setDescription("");
									paramModel.setTestData("");
									try {
										if(parameters[i].isAnnotationPresent(PathVariable.class)) {
											paramModel.setParamType(ParamType.PATH);
										}else if(parameters[i].isAnnotationPresent(RequestHeader.class)) {
											paramModel.setParamType(ParamType.HEADER);
										}else {
											paramModel.setParamType(ParamType.QUERY);
										}
									} catch (Exception e) {
										paramModel.setParamType(ParamType.QUERY);
									}
									request.add(paramModel);
								}
							}
						}
						
						// 判断出参注解
						if (method.isAnnotationPresent(LKAResposes.class)) {
							LKAResposes lkaResposes = method.getAnnotation(LKAResposes.class);
							LKARespose[] resposes = lkaResposes.value();
							if (resposes != null && resposes.length > 0) {
								for (LKARespose resp : resposes) {
									// 获取参数描述信息
									Class<?> type = resp.type();
									if (!type.getName().equals("java.lang.Object")) { // 说明入参是对象
										ResposeModel resposeModel = analysisResModel(methodModel.getUrl(),type,resp.group());
										if (resposeModel != null) {
											resposeModel.setArray(resp.isArray());
											//###################父参######################
											if(resp.parentName()!=null && !"".equals(resp.parentName())) {
												boolean bl = true;
												for (ResposeModel res: respose) {
													if(res.getValue().equals(resp.parentName())) {
														bl = false;
														break;
													}
													if(res.getModelModel()!=null) {
														List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
														if(propertyModels != null && propertyModels.size()>0) {
															Iterator<PropertyModel> iterator = propertyModels.iterator();
															while(iterator.hasNext()) {
																PropertyModel pm = iterator.next();
																if(pm.getValue() != null && pm.getValue().equals(resp.parentName())) {
																	iterator.remove();
																	break;
																}
															}
														}
													}
												}
												if(bl) {
													ResposeModel pn = new ResposeModel();
													pn.setValue(resp.parentName());
													pn.setName(resp.parentValue());
													pn.setDescription(resp.parentDescription());
													pn.setArray(resp.parentIsArray());
													if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
														pn.setParentName(resp.grandpaName());
													}
													respose.add(pn);
												}
											}
											//###################爷参######################
											if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
												boolean b1 = true;
												for (ResposeModel res: respose) {
													if(res.getValue().equals(resp.grandpaName())) {
														b1 = false;
														break;
													}
													if(res.getModelModel()!=null) {
														List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
														if(propertyModels != null && propertyModels.size()>0) {
															Iterator<PropertyModel> iterator = propertyModels.iterator();
															while(iterator.hasNext()) {
																PropertyModel pm = iterator.next();
																if(pm.getValue() != null && pm.getValue().equals(resp.grandpaName())) {
																	iterator.remove();
																	break;
																}
															}
														}
													}
												}
												if(b1) {
													ResposeModel pn = new ResposeModel();
													pn.setValue(resp.grandpaName());
													pn.setName(resp.grandpaValue());
													pn.setDescription(resp.grandpaDescription());
													pn.setArray(resp.grandpaIsArray());
													respose.add(pn);
												}
											}
											//###################结束######################
											resposeModel.setParentName(resp.parentName());
											resposeModel.setValue(resp.name());
											resposeModel.setName(resp.value());
											resposeModel.setDescription(resp.description());
											resposeModel.setDataType("");
											respose.add(resposeModel);
										}
									} else {
										if(resp.names()!= null && resp.names().length>0) {
											String[] names = resp.names();
											for (int i = 0;i<names.length;i++) {
												try {
													String[] descriptions = resp.descriptions();
													Class<?>[] dataTypes = resp.dataTypes();
													boolean[] arrays = resp.isArrays();
													String[] values = resp.values();
													String[] parentNames = resp.parentNames();
													String[] parentDescriptions = resp.parentDescriptions();
													boolean[] parentIsArrays = resp.parentIsArrays();
													String[] parentValues = resp.parentValues();
													String[] grandpaNames = resp.grandpaNames();
													String[] grandpaDescriptions = resp.grandpaDescriptions();
													boolean[] grandpaIsArrays = resp.grandpaIsArrays();
													String[] grandpaValues = resp.grandpaValues();
													
													
													ResposeModel resposeModel = new ResposeModel();
													resposeModel.setValue(names[i]);
													
													if(descriptions!=null && descriptions.length > 0) {
														try {
															resposeModel.setDescription(descriptions[i]);
														} catch (Exception e) {
															resposeModel.setDescription(descriptions[0]);
														}
													}else {
														resposeModel.setDescription(resp.description());
													}
													
													if(values!=null && values.length > 0) {
														try {
															resposeModel.setName(values[i]);
														} catch (Exception e) {
															resposeModel.setName(values[0]);
														}
													}else {
														resposeModel.setName(resp.value());
													}
													
													if(dataTypes!=null && dataTypes.length > 0) {
														try {
															resposeModel.setDataType(dataTypes[i].getSimpleName());
														} catch (Exception e) {
															resposeModel.setDataType(dataTypes[0].getSimpleName());
														}
													}else {
														resposeModel.setDataType(resp.dataType().getSimpleName());
													}
													
													if(arrays!=null && arrays.length > 0) {
														try {
															resposeModel.setArray(arrays[i]);
														} catch (Exception e) {
															resposeModel.setArray(arrays[0]);
														}
													}else {
														resposeModel.setArray(resp.isArray());
													}
													
													if(parentNames!=null && parentNames.length > 0) {
														try {
															//###################父参######################
															boolean bl = true;
															for (ResposeModel res: respose) {
																if(res.getValue().equals(parentNames[i])) {
																	bl = false;
																	break;
																}
																if(res.getModelModel()!=null) {
																	List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
																	if(propertyModels != null && propertyModels.size()>0) {
																		Iterator<PropertyModel> iterator = propertyModels.iterator();
																		while(iterator.hasNext()) {
																			PropertyModel pm = iterator.next();
																			if(pm.getValue() != null && pm.getValue().equals(parentNames[i])) {
																				iterator.remove();
																				break;
																			}
																		}
																	}
																}
															}
															if(bl) {
																ResposeModel pn = new ResposeModel();
																pn.setValue(parentNames[i]);
																if(parentValues != null && parentValues.length>0) {
																	try {
																		pn.setName(parentValues[i]);
																	} catch (Exception e) {
																		pn.setName(parentValues[0]);
																	}
																}else {
																	pn.setName(resp.parentValue());
																}
																if(parentDescriptions != null && parentDescriptions.length>0) {
																	try {
																		pn.setDescription(parentDescriptions[i]);
																	} catch (Exception e) {
																		pn.setDescription(parentDescriptions[0]);
																	}
																}else {
																	pn.setDescription(resp.parentDescription());
																}
																if(parentIsArrays != null && parentIsArrays.length>0) {
																	try {
																		pn.setArray(parentIsArrays[i]);
																	} catch (Exception e) {
																		pn.setArray(parentIsArrays[0]);
																	}
																}else {
																	pn.setArray(resp.parentIsArray());
																}
																if(grandpaNames!=null && grandpaNames.length>0) {
																	try {
																		pn.setParentName(grandpaNames[i]);
																	} catch (Exception e) {
																		pn.setParentName(grandpaNames[0]);
																	}
																}else {
																	if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
																		pn.setParentName(resp.grandpaName());
																	}
																}
																respose.add(pn);
															}
															resposeModel.setParentName(parentNames[i]);
														} catch (Exception e) {
															//###################父参######################
															boolean bl = true;
															for (ResposeModel res: respose) {
																if(res.getValue().equals(parentNames[0])) {
																	bl = false;
																	break;
																}
																if(res.getModelModel()!=null) {
																	List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
																	if(propertyModels != null && propertyModels.size()>0) {
																		Iterator<PropertyModel> iterator = propertyModels.iterator();
																		while(iterator.hasNext()) {
																			PropertyModel pm = iterator.next();
																			if(pm.getValue() != null && pm.getValue().equals(parentNames[0])) {
																				iterator.remove();
																				break;
																			}
																		}
																	}
																}
															}
															if(bl) {
																ResposeModel pn = new ResposeModel();
																pn.setValue(parentNames[0]);
																if(parentValues != null && parentValues.length>0) {
																	try {
																		pn.setName(parentValues[i]);
																	} catch (Exception e1) {
																		pn.setName(parentValues[0]);
																	}
																}else {
																	pn.setName(resp.parentValue());
																}
																if(parentDescriptions != null && parentDescriptions.length>0) {
																	try {
																		pn.setDescription(parentDescriptions[i]);
																	} catch (Exception e1) {
																		pn.setDescription(parentDescriptions[0]);
																	}
																}else {
																	pn.setDescription(resp.parentDescription());
																}
																if(parentIsArrays != null && parentIsArrays.length>0) {
																	try {
																		pn.setArray(parentIsArrays[i]);
																	} catch (Exception e1) {
																		pn.setArray(parentIsArrays[0]);
																	}
																}else {
																	pn.setArray(resp.parentIsArray());
																}
																if(grandpaNames!=null && grandpaNames.length>0) {
																	try {
																		pn.setParentName(grandpaNames[i]);
																	} catch (Exception e1) {
																		pn.setParentName(grandpaNames[0]);
																	}
																}else {
																	if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
																		pn.setParentName(resp.grandpaName());
																	}
																}
																respose.add(pn);
															}
															resposeModel.setParentName(parentNames[0]);
														}
													}else {
														//###################父参######################
														if(resp.parentName()!=null && !"".equals(resp.parentName())) {
															boolean bl = true;
															for (ResposeModel res: respose) {
																if(res.getValue().equals(resp.parentName())) {
																	bl = false;
																	break;
																}
																if(res.getModelModel()!=null) {
																	List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
																	if(propertyModels != null && propertyModels.size()>0) {
																		Iterator<PropertyModel> iterator = propertyModels.iterator();
																		while(iterator.hasNext()) {
																			PropertyModel pm = iterator.next();
																			if(pm.getValue() != null && pm.getValue().equals(resp.parentName())) {
																				iterator.remove();
																				break;
																			}
																		}
																	}
																}
															}
															if(bl) {
																ResposeModel pn = new ResposeModel();
																pn.setValue(resp.parentName());
																pn.setName(resp.parentValue());
																pn.setDescription(resp.parentDescription());
																pn.setArray(resp.parentIsArray());
																if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
																	pn.setParentName(resp.grandpaName());
																}
																respose.add(pn);
															}
														}
														resposeModel.setParentName(resp.parentName());
													}
													
													//###################爷参######################
													if(grandpaNames != null && grandpaNames.length>0) {
														try {
															boolean b1 = true;
															for (ResposeModel res: respose) {
																if(res.getValue().equals(grandpaNames[i])) {
																	b1 = false;
																	break;
																}
																if(res.getModelModel()!=null) {
																	List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
																	if(propertyModels != null && propertyModels.size()>0) {
																		Iterator<PropertyModel> iterator = propertyModels.iterator();
																		while(iterator.hasNext()) {
																			PropertyModel pm = iterator.next();
																			if(pm.getValue() != null && pm.getValue().equals(grandpaNames[i])) {
																				iterator.remove();
																				break;
																			}
																		}
																	}
																}
															}
															if(b1) {
																ResposeModel pn = new ResposeModel();
																pn.setValue(grandpaNames[i]);
																if(grandpaValues != null && grandpaValues.length>0) {
																	try {
																		pn.setName(grandpaValues[i]);
																	} catch (Exception e1) {
																		pn.setName(grandpaValues[0]);
																	}
																}else {
																	pn.setName(resp.grandpaValue());
																}
																if(grandpaDescriptions != null && grandpaDescriptions.length>0) {
																	try {
																		pn.setDescription(grandpaDescriptions[i]);
																	} catch (Exception e1) {
																		pn.setDescription(grandpaDescriptions[0]);
																	}
																}else {
																	pn.setDescription(resp.grandpaDescription());
																}
																if(grandpaIsArrays != null && grandpaIsArrays.length>0) {
																	try {
																		pn.setArray(grandpaIsArrays[i]);
																	} catch (Exception e1) {
																		pn.setArray(grandpaIsArrays[0]);
																	}
																}else {
																	pn.setArray(resp.grandpaIsArray());
																}
																respose.add(pn);
															}
														} catch (Exception e) {
															boolean b1 = true;
															for (ResposeModel res: respose) {
																if(res.getValue().equals(grandpaNames[0])) {
																	b1 = false;
																	break;
																}
																if(res.getModelModel()!=null) {
																	List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
																	if(propertyModels != null && propertyModels.size()>0) {
																		Iterator<PropertyModel> iterator = propertyModels.iterator();
																		while(iterator.hasNext()) {
																			PropertyModel pm = iterator.next();
																			if(pm.getValue() != null && pm.getValue().equals(grandpaNames[0])) {
																				iterator.remove();
																				break;
																			}
																		}
																	}
																}
															}
															if(b1) {
																ResposeModel pn = new ResposeModel();
																pn.setValue(grandpaNames[0]);
																if(grandpaValues != null && grandpaValues.length>0) {
																	try {
																		pn.setName(grandpaValues[i]);
																	} catch (Exception e1) {
																		pn.setName(grandpaValues[0]);
																	}
																}else {
																	pn.setName(resp.grandpaValue());
																}
																if(grandpaDescriptions != null && grandpaDescriptions.length>0) {
																	try {
																		pn.setDescription(grandpaDescriptions[i]);
																	} catch (Exception e1) {
																		pn.setDescription(grandpaDescriptions[0]);
																	}
																}else {
																	pn.setDescription(resp.grandpaDescription());
																}
																if(grandpaIsArrays != null && grandpaIsArrays.length>0) {
																	try {
																		pn.setArray(grandpaIsArrays[i]);
																	} catch (Exception e1) {
																		pn.setArray(grandpaIsArrays[0]);
																	}
																}else {
																	pn.setArray(resp.grandpaIsArray());
																}
																respose.add(pn);
															}
														}
													}else {
														//###################爷参######################
														if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
															boolean b1 = true;
															for (ResposeModel res: respose) {
																if(res.getValue().equals(resp.grandpaName())) {
																	b1 = false;
																	break;
																}
																if(res.getModelModel()!=null) {
																	List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
																	if(propertyModels != null && propertyModels.size()>0) {
																		Iterator<PropertyModel> iterator = propertyModels.iterator();
																		while(iterator.hasNext()) {
																			PropertyModel pm = iterator.next();
																			if(pm.getValue() != null && pm.getValue().equals(resp.grandpaName())) {
																				iterator.remove();
																				break;
																			}
																		}
																	}
																}
															}
															if(b1) {
																ResposeModel pn = new ResposeModel();
																pn.setValue(resp.grandpaName());
																pn.setName(resp.grandpaValue());
																pn.setDescription(resp.grandpaDescription());
																pn.setArray(resp.grandpaIsArray());
																respose.add(pn);
															}
														}
														//###################结束######################
													}

													respose.add(resposeModel);
												} catch (Exception e) {
													ResposeModel resposeModel = new ResposeModel();
													resposeModel.setName(e.getClass().getSimpleName()+":"+e.getMessage());
													resposeModel.setValue("该接口响应参数描述异常！");
													respose.add(resposeModel);
												}
											}
										}else {
											ResposeModel resposeModel = new ResposeModel();
											resposeModel.setDataType(resp.dataType().getSimpleName());
											resposeModel.setDescription(resp.description());
											resposeModel.setName(resp.value());
											resposeModel.setArray(resp.isArray());
											//###################父参######################
											if(resp.parentName()!=null && !"".equals(resp.parentName())) {
												boolean bl = true;
												for (ResposeModel res: respose) {
													if(res.getValue().equals(resp.parentName())) {
														bl = false;
														break;
													}
													if(res.getModelModel()!=null) {
														List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
														if(propertyModels != null && propertyModels.size()>0) {
															Iterator<PropertyModel> iterator = propertyModels.iterator();
															while(iterator.hasNext()) {
																PropertyModel pm = iterator.next();
																if(pm.getValue() != null && pm.getValue().equals(resp.parentName())) {
																	iterator.remove();
																	break;
																}
															}
														}
													}
												}
												if(bl) {
													ResposeModel pn = new ResposeModel();
													pn.setValue(resp.parentName());
													pn.setName(resp.parentValue());
													pn.setDescription(resp.parentDescription());
													pn.setArray(resp.parentIsArray());
													if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
														pn.setParentName(resp.grandpaName());
													}
													respose.add(pn);
												}
											}
											//###################爷参######################
											if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
												boolean b1 = true;
												for (ResposeModel res: respose) {
													if(res.getValue().equals(resp.grandpaName())) {
														b1 = false;
														break;
													}
													if(res.getModelModel()!=null) {
														List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
														if(propertyModels != null && propertyModels.size()>0) {
															Iterator<PropertyModel> iterator = propertyModels.iterator();
															while(iterator.hasNext()) {
																PropertyModel pm = iterator.next();
																if(pm.getValue() != null && pm.getValue().equals(resp.grandpaName())) {
																	iterator.remove();
																	break;
																}
															}
														}
													}
												}
												if(b1) {
													ResposeModel pn = new ResposeModel();
													pn.setValue(resp.grandpaName());
													pn.setName(resp.grandpaValue());
													pn.setDescription(resp.grandpaDescription());
													pn.setArray(resp.grandpaIsArray());
													respose.add(pn);
												}
											}
											//###################结束######################
											resposeModel.setParentName(resp.parentName());
											resposeModel.setValue(resp.name());
											respose.add(resposeModel);
										}
									}
								}
							}
						} else if (method.isAnnotationPresent(LKARespose.class)) {
							LKARespose resp = method.getAnnotation(LKARespose.class);
							// 获取参数描述信息
							Class<?> type = resp.type();
							if (!type.getName().equals("java.lang.Object")) { // 说明入参是对象
								ResposeModel resposeModel = analysisResModel(methodModel.getUrl(),type,resp.group());
								if (resposeModel != null) {
									resposeModel.setArray(resp.isArray());
									//###################父参######################
									if(resp.parentName()!=null && !"".equals(resp.parentName())) {
										boolean bl = true;
										for (ResposeModel res: respose) {
											if(res.getValue().equals(resp.parentName())) {
												bl = false;
												break;
											}
											if(res.getModelModel()!=null) {
												List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
												if(propertyModels != null && propertyModels.size()>0) {
													Iterator<PropertyModel> iterator = propertyModels.iterator();
													while(iterator.hasNext()) {
														PropertyModel pm = iterator.next();
														if(pm.getValue() != null && pm.getValue().equals(resp.parentName())) {
															iterator.remove();
															break;
														}
													}
												}
											}
										}
										if(bl) {
											ResposeModel pn = new ResposeModel();
											pn.setValue(resp.parentName());
											pn.setName(resp.parentValue());
											pn.setDescription(resp.parentDescription());
											pn.setArray(resp.parentIsArray());
											if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
												pn.setParentName(resp.grandpaName());
											}
											respose.add(pn);
										}
									}
									//###################爷参######################
									if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
										boolean b1 = true;
										for (ResposeModel res: respose) {
											if(res.getValue().equals(resp.grandpaName())) {
												b1 = false;
												break;
											}
											if(res.getModelModel()!=null) {
												List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
												if(propertyModels != null && propertyModels.size()>0) {
													Iterator<PropertyModel> iterator = propertyModels.iterator();
													while(iterator.hasNext()) {
														PropertyModel pm = iterator.next();
														if(pm.getValue() != null && pm.getValue().equals(resp.grandpaName())) {
															iterator.remove();
															break;
														}
													}
												}
											}
										}
										if(b1) {
											ResposeModel pn = new ResposeModel();
											pn.setValue(resp.grandpaName());
											pn.setName(resp.grandpaValue());
											pn.setDescription(resp.grandpaDescription());
											pn.setArray(resp.grandpaIsArray());
											respose.add(pn);
										}
									}
									//###################结束######################
									resposeModel.setParentName(resp.parentName());
									resposeModel.setValue(resp.name());
									resposeModel.setName(resp.value());
									resposeModel.setDescription(resp.description());
									resposeModel.setDataType("");
									respose.add(resposeModel);
								}
							} else {

								if(resp.names()!= null && resp.names().length>0) {
									String[] names = resp.names();
									for (int i = 0;i<names.length;i++) {
										try {
											String[] descriptions = resp.descriptions();
											Class<?>[] dataTypes = resp.dataTypes();
											boolean[] arrays = resp.isArrays();
											String[] values = resp.values();
											String[] parentNames = resp.parentNames();
											String[] parentDescriptions = resp.parentDescriptions();
											boolean[] parentIsArrays = resp.parentIsArrays();
											String[] parentValues = resp.parentValues();
											String[] grandpaNames = resp.grandpaNames();
											String[] grandpaDescriptions = resp.grandpaDescriptions();
											boolean[] grandpaIsArrays = resp.grandpaIsArrays();
											String[] grandpaValues = resp.grandpaValues();
											
											
											ResposeModel resposeModel = new ResposeModel();
											resposeModel.setValue(names[i]);
											
											if(descriptions!=null && descriptions.length > 0) {
												try {
													resposeModel.setDescription(descriptions[i]);
												} catch (Exception e) {
													resposeModel.setDescription(descriptions[0]);
												}
											}else {
												resposeModel.setDescription(resp.description());
											}
											
											if(values!=null && values.length > 0) {
												try {
													resposeModel.setName(values[i]);
												} catch (Exception e) {
													resposeModel.setName(values[0]);
												}
											}else {
												resposeModel.setName(resp.value());
											}
											
											if(dataTypes!=null && dataTypes.length > 0) {
												try {
													resposeModel.setDataType(dataTypes[i].getSimpleName());
												} catch (Exception e) {
													resposeModel.setDataType(dataTypes[0].getSimpleName());
												}
											}else {
												resposeModel.setDataType(resp.dataType().getSimpleName());
											}
											
											if(arrays!=null && arrays.length > 0) {
												try {
													resposeModel.setArray(arrays[i]);
												} catch (Exception e) {
													resposeModel.setArray(arrays[0]);
												}
											}else {
												resposeModel.setArray(resp.isArray());
											}
											
											if(parentNames!=null && parentNames.length > 0) {
												try {
													//###################父参######################
													boolean bl = true;
													for (ResposeModel res: respose) {
														if(res.getValue().equals(parentNames[i])) {
															bl = false;
															break;
														}
														if(res.getModelModel()!=null) {
															List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
															if(propertyModels != null && propertyModels.size()>0) {
																Iterator<PropertyModel> iterator = propertyModels.iterator();
																while(iterator.hasNext()) {
																	PropertyModel pm = iterator.next();
																	if(pm.getValue() != null && pm.getValue().equals(parentNames[i])) {
																		iterator.remove();
																		break;
																	}
																}
															}
														}
													}
													if(bl) {
														ResposeModel pn = new ResposeModel();
														pn.setValue(parentNames[i]);
														if(parentValues != null && parentValues.length>0) {
															try {
																pn.setName(parentValues[i]);
															} catch (Exception e) {
																pn.setName(parentValues[0]);
															}
														}else {
															pn.setName(resp.parentValue());
														}
														if(parentDescriptions != null && parentDescriptions.length>0) {
															try {
																pn.setDescription(parentDescriptions[i]);
															} catch (Exception e) {
																pn.setDescription(parentDescriptions[0]);
															}
														}else {
															pn.setDescription(resp.parentDescription());
														}
														if(parentIsArrays != null && parentIsArrays.length>0) {
															try {
																pn.setArray(parentIsArrays[i]);
															} catch (Exception e) {
																pn.setArray(parentIsArrays[0]);
															}
														}else {
															pn.setArray(resp.parentIsArray());
														}
														if(grandpaNames!=null && grandpaNames.length>0) {
															try {
																pn.setParentName(grandpaNames[i]);
															} catch (Exception e) {
																pn.setParentName(grandpaNames[0]);
															}
														}else {
															if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
																pn.setParentName(resp.grandpaName());
															}
														}
														respose.add(pn);
													}
													resposeModel.setParentName(parentNames[i]);
												} catch (Exception e) {
													//###################父参######################
													boolean bl = true;
													for (ResposeModel res: respose) {
														if(res.getValue().equals(parentNames[0])) {
															bl = false;
															break;
														}
														if(res.getModelModel()!=null) {
															List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
															if(propertyModels != null && propertyModels.size()>0) {
																Iterator<PropertyModel> iterator = propertyModels.iterator();
																while(iterator.hasNext()) {
																	PropertyModel pm = iterator.next();
																	if(pm.getValue() != null && pm.getValue().equals(parentNames[0])) {
																		iterator.remove();
																		break;
																	}
																}
															}
														}
													}
													if(bl) {
														ResposeModel pn = new ResposeModel();
														pn.setValue(parentNames[0]);
														if(parentValues != null && parentValues.length>0) {
															try {
																pn.setName(parentValues[i]);
															} catch (Exception e1) {
																pn.setName(parentValues[0]);
															}
														}else {
															pn.setName(resp.parentValue());
														}
														if(parentDescriptions != null && parentDescriptions.length>0) {
															try {
																pn.setDescription(parentDescriptions[i]);
															} catch (Exception e1) {
																pn.setDescription(parentDescriptions[0]);
															}
														}else {
															pn.setDescription(resp.parentDescription());
														}
														if(parentIsArrays != null && parentIsArrays.length>0) {
															try {
																pn.setArray(parentIsArrays[i]);
															} catch (Exception e1) {
																pn.setArray(parentIsArrays[0]);
															}
														}else {
															pn.setArray(resp.parentIsArray());
														}
														if(grandpaNames!=null && grandpaNames.length>0) {
															try {
																pn.setParentName(grandpaNames[i]);
															} catch (Exception e1) {
																pn.setParentName(grandpaNames[0]);
															}
														}else {
															if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
																pn.setParentName(resp.grandpaName());
															}
														}
														respose.add(pn);
													}
													resposeModel.setParentName(parentNames[0]);
												}
											}else {
												//###################父参######################
												if(resp.parentName()!=null && !"".equals(resp.parentName())) {
													boolean bl = true;
													for (ResposeModel res: respose) {
														if(res.getValue().equals(resp.parentName())) {
															bl = false;
															break;
														}
														if(res.getModelModel()!=null) {
															List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
															if(propertyModels != null && propertyModels.size()>0) {
																Iterator<PropertyModel> iterator = propertyModels.iterator();
																while(iterator.hasNext()) {
																	PropertyModel pm = iterator.next();
																	if(pm.getValue() != null && pm.getValue().equals(resp.parentName())) {
																		iterator.remove();
																		break;
																	}
																}
															}
														}
													}
													if(bl) {
														ResposeModel pn = new ResposeModel();
														pn.setValue(resp.parentName());
														pn.setName(resp.parentValue());
														pn.setDescription(resp.parentDescription());
														pn.setArray(resp.parentIsArray());
														if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
															pn.setParentName(resp.grandpaName());
														}
														respose.add(pn);
													}
												}
												resposeModel.setParentName(resp.parentName());
											}
											
											//###################爷参######################
											if(grandpaNames != null && grandpaNames.length>0) {
												try {
													boolean b1 = true;
													for (ResposeModel res: respose) {
														if(res.getValue().equals(grandpaNames[i])) {
															b1 = false;
															break;
														}
														if(res.getModelModel()!=null) {
															List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
															if(propertyModels != null && propertyModels.size()>0) {
																Iterator<PropertyModel> iterator = propertyModels.iterator();
																while(iterator.hasNext()) {
																	PropertyModel pm = iterator.next();
																	if(pm.getValue() != null && pm.getValue().equals(grandpaNames[i])) {
																		iterator.remove();
																		break;
																	}
																}
															}
														}
													}
													if(b1) {
														ResposeModel pn = new ResposeModel();
														pn.setValue(grandpaNames[i]);
														if(grandpaValues != null && grandpaValues.length>0) {
															try {
																pn.setName(grandpaValues[i]);
															} catch (Exception e1) {
																pn.setName(grandpaValues[0]);
															}
														}else {
															pn.setName(resp.grandpaValue());
														}
														if(grandpaDescriptions != null && grandpaDescriptions.length>0) {
															try {
																pn.setDescription(grandpaDescriptions[i]);
															} catch (Exception e1) {
																pn.setDescription(grandpaDescriptions[0]);
															}
														}else {
															pn.setDescription(resp.grandpaDescription());
														}
														if(grandpaIsArrays != null && grandpaIsArrays.length>0) {
															try {
																pn.setArray(grandpaIsArrays[i]);
															} catch (Exception e1) {
																pn.setArray(grandpaIsArrays[0]);
															}
														}else {
															pn.setArray(resp.grandpaIsArray());
														}
														respose.add(pn);
													}
												} catch (Exception e) {
													boolean b1 = true;
													for (ResposeModel res: respose) {
														if(res.getValue().equals(grandpaNames[0])) {
															b1 = false;
															break;
														}
														if(res.getModelModel()!=null) {
															List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
															if(propertyModels != null && propertyModels.size()>0) {
																Iterator<PropertyModel> iterator = propertyModels.iterator();
																while(iterator.hasNext()) {
																	PropertyModel pm = iterator.next();
																	if(pm.getValue() != null && pm.getValue().equals(grandpaNames[0])) {
																		iterator.remove();
																		break;
																	}
																}
															}
														}
													}
													if(b1) {
														ResposeModel pn = new ResposeModel();
														pn.setValue(grandpaNames[0]);
														if(grandpaValues != null && grandpaValues.length>0) {
															try {
																pn.setName(grandpaValues[i]);
															} catch (Exception e1) {
																pn.setName(grandpaValues[0]);
															}
														}else {
															pn.setName(resp.grandpaValue());
														}
														if(grandpaDescriptions != null && grandpaDescriptions.length>0) {
															try {
																pn.setDescription(grandpaDescriptions[i]);
															} catch (Exception e1) {
																pn.setDescription(grandpaDescriptions[0]);
															}
														}else {
															pn.setDescription(resp.grandpaDescription());
														}
														if(grandpaIsArrays != null && grandpaIsArrays.length>0) {
															try {
																pn.setArray(grandpaIsArrays[i]);
															} catch (Exception e1) {
																pn.setArray(grandpaIsArrays[0]);
															}
														}else {
															pn.setArray(resp.grandpaIsArray());
														}
														respose.add(pn);
													}
												}
											}else {
												//###################爷参######################
												if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
													boolean b1 = true;
													for (ResposeModel res: respose) {
														if(res.getValue().equals(resp.grandpaName())) {
															b1 = false;
															break;
														}
														if(res.getModelModel()!=null) {
															List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
															if(propertyModels != null && propertyModels.size()>0) {
																Iterator<PropertyModel> iterator = propertyModels.iterator();
																while(iterator.hasNext()) {
																	PropertyModel pm = iterator.next();
																	if(pm.getValue() != null && pm.getValue().equals(resp.grandpaName())) {
																		iterator.remove();
																		break;
																	}
																}
															}
														}
													}
													if(b1) {
														ResposeModel pn = new ResposeModel();
														pn.setValue(resp.grandpaName());
														pn.setName(resp.grandpaValue());
														pn.setDescription(resp.grandpaDescription());
														pn.setArray(resp.grandpaIsArray());
														respose.add(pn);
													}
												}
												//###################结束######################
											}

											respose.add(resposeModel);
										} catch (Exception e) {
											ResposeModel resposeModel = new ResposeModel();
											resposeModel.setName(e.getClass().getSimpleName()+":"+e.getMessage());
											resposeModel.setValue("该接口响应参数描述异常！");
											respose.add(resposeModel);
										}
									}
								}else {
									ResposeModel resposeModel = new ResposeModel();
									resposeModel.setDataType(resp.dataType().getSimpleName());
									resposeModel.setDescription(resp.description());
									resposeModel.setName(resp.value());
									resposeModel.setArray(resp.isArray());
									//###################父参######################
									if(resp.parentName()!=null && !"".equals(resp.parentName())) {
										boolean bl = true;
										for (ResposeModel res: respose) {
											if(res.getValue().equals(resp.parentName())) {
												bl = false;
												break;
											}
											if(res.getModelModel()!=null) {
												List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
												if(propertyModels != null && propertyModels.size()>0) {
													Iterator<PropertyModel> iterator = propertyModels.iterator();
													while(iterator.hasNext()) {
														PropertyModel pm = iterator.next();
														if(pm.getValue() != null && pm.getValue().equals(resp.parentName())) {
															iterator.remove();
															break;
														}
													}
												}
											}
										}
										if(bl) {
											ResposeModel pn = new ResposeModel();
											pn.setValue(resp.parentName());
											pn.setName(resp.parentValue());
											pn.setDescription(resp.parentDescription());
											pn.setArray(resp.parentIsArray());
											if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
												pn.setParentName(resp.grandpaName());
											}
											respose.add(pn);
										}
									}
									//###################爷参######################
									if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
										boolean b1 = true;
										for (ResposeModel res: respose) {
											if(res.getValue().equals(resp.grandpaName())) {
												b1 = false;
												break;
											}
											if(res.getModelModel()!=null) {
												List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
												if(propertyModels != null && propertyModels.size()>0) {
													Iterator<PropertyModel> iterator = propertyModels.iterator();
													while(iterator.hasNext()) {
														PropertyModel pm = iterator.next();
														if(pm.getValue() != null && pm.getValue().equals(resp.grandpaName())) {
															iterator.remove();
															break;
														}
													}
												}
											}
										}
										if(b1) {
											ResposeModel pn = new ResposeModel();
											pn.setValue(resp.grandpaName());
											pn.setName(resp.grandpaValue());
											pn.setDescription(resp.grandpaDescription());
											pn.setArray(resp.grandpaIsArray());
											respose.add(pn);
										}
									}
									//###################结束######################
									resposeModel.setParentName(resp.parentName());
									resposeModel.setValue(resp.name());
									respose.add(resposeModel);
								}
							
							}
						}
						
						methodModel.setRequest(request);
						methodModel.setRespose(respose);
						methodModels.add(methodModel);
					}
					typeModel.setMethodModels(methodModels);
					//目录合并
					boolean mergeBool = false;
					for (TypeModel tm : typeModels) {
						if(tm.getName().equals(typeModel.getName())) {
							List<MethodModel> mms = typeModel.getMethodModels();
							tm.getMethodModels().addAll(mms);
							mergeBool = true;
							break;
						}
					}
					if(!mergeBool) typeModels.add(typeModel);
				}
			}
		}
		return typeModels;
	}
	
	/**
	 *	 获取所有方法的请求方式，请求路径等相关信息
	 * @return list 集合
	 */
	public List<Map<String,Object>> getMethodURL() {
		RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext
				.getBean(RequestMappingHandlerMapping.class);
		// 获取url与类和方法的对应信息
		Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
		List<Map<String,Object>> resultList = new ArrayList<Map<String, Object>>();
		for (Map.Entry<RequestMappingInfo, HandlerMethod> mappingInfoHandlerMethodEntry : map.entrySet()) {
			Map<String, Object> resultMap = new LinkedHashMap<String, Object>();

			RequestMappingInfo requestMappingInfo = mappingInfoHandlerMethodEntry.getKey();
			HandlerMethod handlerMethod = mappingInfoHandlerMethodEntry.getValue();

			resultMap.put("className", handlerMethod.getMethod().getDeclaringClass().getName()); // 类名
			Class<?>[] interfaces = handlerMethod.getMethod().getDeclaringClass().getInterfaces();
			List<String> interfacesNames = new ArrayList<String>();
			if (interfaces != null) {
				for (Class<?> c : interfaces) {// 基于接口继承模式,反向获取接口注解
					interfacesNames.add(c.getName());
				}
			}
			resultMap.put("interfacesNames",interfacesNames);
			Annotation[] parentAnnotations = handlerMethod.getBeanType().getAnnotations();
			for (Annotation annotation : parentAnnotations) {
				if (annotation instanceof RequestMapping) {
					RequestMapping requestMapping = (RequestMapping) annotation;
					if (null != requestMapping.value() && requestMapping.value().length > 0) {
						resultMap.put("classURL", requestMapping.value()[0]);// 类URL
					}
				}
			}
			resultMap.put("methodName", handlerMethod.getMethod().getName()); // 方法名
			PatternsRequestCondition p = requestMappingInfo.getPatternsCondition();
			for (String url : p.getPatterns()) {
				resultMap.put("methodURL", url);// 请求URL
			}
			RequestMethodsRequestCondition methodsCondition = requestMappingInfo.getMethodsCondition();
			for (RequestMethod requestMethod : methodsCondition.getMethods()) {
				resultMap.put("requestType", requestMethod.toString());// 请求方式：POST/PUT/GET/DELETE
			}
			resultList.add(resultMap);
		}
		return resultList;
	}
	
	
	/**
	 * 	解析请求参数的LKAModel对象注解
	 * @param url 位置
	 * @param typeCls 类型
	 * @param group 组名
	 * @return ParamModel 对象
	 * @throws Exception 异常
	 */
	public ParamModel analysisModel(String url,Class<?> typeCls,String group) throws Exception {
		reqNum++; //防止递归死循环
		if(reqNum > 10) {
			reqNum = 0;
			return null;
		}
		if (!typeCls.isAnnotationPresent(LKAModel.class) && !typeCls.isAnnotationPresent(ApiModel.class)) {
			reqNum = 0;
			return null;
		}
		ParamModel pm = new ParamModel();
		// 获取model描述信息
		ModelModel modelModel = new ModelModel();
		if(typeCls.isAnnotationPresent(LKAModel.class)) {
			LKAModel lkaModel = typeCls.getAnnotation(LKAModel.class);
			modelModel.setValue(typeCls.getSimpleName());
			modelModel.setName(lkaModel.value());
			modelModel.setDescription(lkaModel.description());
		}else {
			ApiModel lkaModel = typeCls.getAnnotation(ApiModel.class);
			modelModel.setValue(typeCls.getSimpleName());
			modelModel.setName(lkaModel.value());
			modelModel.setDescription(lkaModel.description());
		}
		
		// 获取所有属性对象
		Field[] fields = typeCls.getDeclaredFields();
		
		//获取父类所有属性对象
		Field[] declaredField;
		try {
			declaredField = getDeclaredField(typeCls.newInstance());
		} catch (Exception e) {
			declaredField = null;
		}
		Object[] arrays = null;
		//合并数组
		if(declaredField != null) {
			List<Field> list = new ArrayList<>(Arrays.asList(declaredField));
			arrays = list.toArray();
		}else {
			arrays = fields;
		}
		
		if (arrays != null && arrays.length > 0) {
			List<PropertyModel> propertyModels = new ArrayList<PropertyModel>();
			for (Object obj : arrays) {
				Field field = (Field)obj;
				String name = field.getName();
				Class<?> type = field.getType();
				if (!field.isAnnotationPresent(LKAProperty.class) && !field.isAnnotationPresent(ApiModelProperty.class)) {
					if(sconAll) {
						PropertyModel propertyModel = new PropertyModel();
						int isArray = isObj(type);
						type = getGenericType(type,field);
						//判断数据类型
						propertyModel.setDataType(type.getSimpleName());
						int flag = isObj(type);
			            if(flag == 3) {
			            	propertyModel = analysisProModel(url,type,"",1);
			            	if(propertyModel == null) { 
			            		propertyModel = new PropertyModel();
			            		propertyModel.setDataType("Object");
			            	}
			            }
			            propertyModel.setArray(false);
			            if(isArray == 2 || isArray == 4) {
			            	propertyModel.setArray(true);
							propertyModel.setDataType(propertyModel.getDataType()+"[]");
						}
		            	propertyModel.setName(enToCn(url,1,name));
		            	propertyModel.setValue(name);
		            	propertyModel.setRequired(false);
		            	propertyModel.setDescription("");
		            	propertyModel.setTestData("");
						try {
							if(type.isAnnotationPresent(PathVariable.class)) {
								propertyModel.setParamType(ParamType.PATH);
							}else if(type.isAnnotationPresent(RequestHeader.class)) {
								propertyModel.setParamType(ParamType.HEADER);
							}else {
								propertyModel.setParamType(ParamType.QUERY);
							}
						} catch (Exception e) {
							propertyModel.setParamType(ParamType.QUERY);
						}
						propertyModels.add(propertyModel);
					}
				}else {
					boolean bool2=false;
					if(field.isAnnotationPresent(LKAProperty.class)) {
						LKAProperty param = field.getAnnotation(LKAProperty.class);
						if(param.hidden())continue;
						boolean bool = false;
						if(group != null && !"".equals(group)) {
							String[] groups = param.groups();
							
							if(groups != null && groups.length > 0) {
								for (String gst : groups) {
									if(gst == null) continue;
									String[] gs = gst.split("-");
									if(gs == null || gs[0] == null || "".equals(gs[0]) || !gs[0].equals(group)) {
										continue;
									}else {
										if(gs.length > 1 && gs[1].equals("n")) {
											bool2 = true;
										}
										bool = true;
										break;
									}
								}
							}
							if(!bool) continue;
						}
						Class<?> ctype = param.type();
						String pValue = field.getName();
						Class<?> pType = field.getType();
						//获取泛形类型
						Class<?> gType = getGenericType(pType, field);
						PropertyModel propertyModel = new PropertyModel();
						if (ctype.getName() != "java.lang.Object" || isObj(gType) == 3) {
							if(ctype.getName() != "java.lang.Object") {
								propertyModel = analysisProModel(url,ctype,group,1);
							}else {
								propertyModel = analysisProModel(url,gType,group,1);
							}
							if (propertyModel == null) {
								propertyModel = new PropertyModel();
								propertyModel.setDataType("Object");
							}
							propertyModel.setArray(param.isArray());
							if(!param.isArray() && (pType.getSimpleName().contains("[]") || 
									pType.equals(List.class) || 
									pType.equals(Set.class) || 
									pType.equals(ArrayList.class) || 
									pType.equals(LinkedList.class) ||
									pType.equals(Vector.class) ||
									pType.equals(SortedSet.class) ||
									pType.equals(HashSet.class) ||
									pType.equals(TreeSet.class) ||
									pType.equals(LinkedHashSet.class))) {
								propertyModel.setArray(true);
							}
							propertyModel.setValue(pValue);
							propertyModel.setName(param.value());
							propertyModel.setDescription(param.description());
							propertyModels.add(propertyModel);
						} else {
							propertyModel.setDataType(pType.getSimpleName());
							propertyModel.setDescription(param.description());
							propertyModel.setName(param.value());
							propertyModel.setTestData(param.testData());
							String[] split = param.value().split("\\^");
							if(split.length == 2) {
								propertyModel.setName(split[0]);
								propertyModel.setTestData(split[1]);
							}

							String[] split2 = split[0].split("\\~");
							if(split2.length == 2) {
								propertyModel.setName(split2[1]);
								if(split2[0].contains("n"))propertyModel.setRequired(false);
							}
							
							if(bool) {
								propertyModel.setRequired(true);
							}
							if(bool2) {
								propertyModel.setRequired(false);
							}
							propertyModel.setParamType("query");
							propertyModel.setArray(param.isArray());
							if(!param.isArray() && (pType.getSimpleName().contains("[]") || 
									pType.equals(List.class) || 
									pType.equals(Set.class) || 
									pType.equals(ArrayList.class) || 
									pType.equals(LinkedList.class) ||
									pType.equals(Vector.class) ||
									pType.equals(SortedSet.class) ||
									pType.equals(HashSet.class) ||
									pType.equals(TreeSet.class) ||
									pType.equals(LinkedHashSet.class))) {
								propertyModel.setArray(true);
							}
							propertyModel.setValue(pValue);
							
							propertyModels.add(propertyModel);
						}
					}else {
						ApiModelProperty param = field.getAnnotation(ApiModelProperty.class);
						if(param.hidden())continue;
						boolean bool = false;
						if(group != null && !"".equals(group)) {
							String[] groups = param.groups();
							
							if(groups != null && groups.length > 0) {
								for (String gst : groups) {
									if(gst == null) continue;
									String[] gs = gst.split("-");
									if(gs == null || gs[0] == null || "".equals(gs[0]) || !gs[0].equals(group)) {
										continue;
									}else {
										if(gs.length > 1 && gs[1].equals("n")) {
											bool2 = true;
										}
										bool = true;
										break;
									}
								}
							}
							if(!bool) continue;
						}
						Class<?> ctype = param.type();
						String pValue = field.getName();
						Class<?> pType = field.getType();
						Class<?> gType = getGenericType(pType, field);
						PropertyModel propertyModel = new PropertyModel();
						if (ctype.getName() != "java.lang.Object" || isObj(gType) == 3) {
							if(ctype.getName() != "java.lang.Object") {
								propertyModel = analysisProModel(url,ctype,group,1);
							}else {
								propertyModel = analysisProModel(url,gType,group,1);
							}
							if (propertyModel == null) {
								propertyModel = new PropertyModel();
								propertyModel.setDataType("Object");
							}
							propertyModel.setArray(param.isArray());
							if(!param.isArray() && (pType.getSimpleName().contains("[]") || 
									pType.equals(List.class) || 
									pType.equals(Set.class) || 
									pType.equals(ArrayList.class) || 
									pType.equals(LinkedList.class) ||
									pType.equals(Vector.class) ||
									pType.equals(SortedSet.class) ||
									pType.equals(HashSet.class) ||
									pType.equals(TreeSet.class) ||
									pType.equals(LinkedHashSet.class))) {
								propertyModel.setArray(true);
							}
							propertyModel.setValue(pValue);
							propertyModel.setName(param.value());
							propertyModel.setDescription(param.description());
							propertyModels.add(propertyModel);
						} else {
							propertyModel.setDataType(pType.getSimpleName());
							propertyModel.setDescription(param.description());
							propertyModel.setName(param.value());
							propertyModel.setTestData(param.testData());
							String[] split = param.value().split("\\^");
							if(split.length == 2) {
								propertyModel.setName(split[0]);
								propertyModel.setTestData(split[1]);
							}

							String[] split2 = split[0].split("\\~");
							if(split2.length == 2) {
								propertyModel.setName(split2[1]);
								if(split2[0].contains("n"))propertyModel.setRequired(false);
							}
							if(bool) {
								propertyModel.setRequired(true);
							}
							if(bool2) {
								propertyModel.setRequired(false);
							}
							propertyModel.setParamType("query");
							propertyModel.setArray(param.isArray());
							if(!param.isArray() && (pType.getSimpleName().contains("[]") || 
									pType.equals(List.class) || 
									pType.equals(Set.class) || 
									pType.equals(ArrayList.class) || 
									pType.equals(LinkedList.class) ||
									pType.equals(Vector.class) ||
									pType.equals(SortedSet.class) ||
									pType.equals(HashSet.class) ||
									pType.equals(TreeSet.class) ||
									pType.equals(LinkedHashSet.class))) {
								propertyModel.setArray(true);
							}
							propertyModel.setValue(pValue);
							propertyModels.add(propertyModel);
						}
					}
				}
			}
			modelModel.setPropertyModels(propertyModels);
		}
		pm.setModelModel(modelModel);
		reqNum = 0;
		return pm;
	}

	/**
	 *	解析对象属性的PropertyModel对象注解
	 * @param url 位置
	 * @param typeCls 类型
	 * @param group 组名
	 * @param proType protype
	 * @return PropertyModel 对象
	 * @throws Exception 异常
	 */
	public PropertyModel analysisProModel(String url,Class<?> typeCls,String group,Integer proType) throws Exception {
		proNum++;
		if(proNum > 10) {
			proNum = 0;
			return null;
		}
		if (!typeCls.isAnnotationPresent(LKAModel.class) && !typeCls.isAnnotationPresent(ApiModel.class)) {
			proNum = 0;
			return null;
		}
		PropertyModel pm = new PropertyModel();
		// 获取model描述信息
		ModelModel modelModel = new ModelModel();
		if(typeCls.isAnnotationPresent(LKAModel.class)) {
			LKAModel lkaModel = typeCls.getAnnotation(LKAModel.class);
			modelModel.setValue(typeCls.getSimpleName());
			modelModel.setName(lkaModel.value());
			modelModel.setDescription(lkaModel.description());
		}else {
			ApiModel lkaModel = typeCls.getAnnotation(ApiModel.class);
			modelModel.setValue(typeCls.getSimpleName());
			modelModel.setName(lkaModel.value());
			modelModel.setDescription(lkaModel.description());
		}
		
		// 获取所有属性对象
		Field[] fields = typeCls.getDeclaredFields();
		
		//获取父类所有属性对象
		Field[] declaredField;
		try {
			declaredField = getDeclaredField(typeCls.newInstance());
		} catch (Exception e) {
			declaredField = null;
		}
		Object[] arrays = null;
		//合并数组
		if(declaredField != null) {
			List<Field> list = new ArrayList<>(Arrays.asList(declaredField));
			arrays = list.toArray();
		}else {
			arrays = fields;
		}
		
		if (arrays != null && arrays.length > 0) {
			List<PropertyModel> propertyModels = new ArrayList<PropertyModel>();
			for (Object obj: arrays) {
				Field field = (Field)obj;
				if (!field.isAnnotationPresent(LKAProperty.class) && !field.isAnnotationPresent(ApiModelProperty.class)) {
					if(sconAll) {
						Class<?> type = field.getType();
						PropertyModel propertyModel = new PropertyModel();
						int isArray = isObj(type);
						type = getGenericType(type, field);
						
						//判断数据类型
						propertyModel.setDataType(type.getSimpleName());
						int flag = isObj(type);
			            if(flag == 3) {
			            	propertyModel = analysisProModel(url,type,"",proType);
			            	if(propertyModel == null) { 
			            		propertyModel = new PropertyModel();
			            		propertyModel.setDataType("Object");
			            	}
			            }
			            String name = field.getName();
			            propertyModel.setArray(false);
			            if(isArray == 2 || isArray == 4) {
							propertyModel.setDataType(propertyModel.getDataType()+"[]");
						}
		            	propertyModel.setName(enToCn(url,proType,name));
		            	propertyModel.setValue(name);
		            	propertyModel.setRequired(false);
		            	propertyModel.setDescription("");
		            	propertyModel.setTestData("");
						try {
							if(type.isAnnotationPresent(PathVariable.class)) {
								propertyModel.setParamType(ParamType.PATH);
							}else if(type.isAnnotationPresent(RequestHeader.class)) {
								propertyModel.setParamType(ParamType.HEADER);
							}else {
								propertyModel.setParamType(ParamType.QUERY);
							}
						} catch (Exception e) {
							propertyModel.setParamType(ParamType.QUERY);
						}
						propertyModels.add(propertyModel);
					}
				}else {
					boolean bool2=false;
					if(field.isAnnotationPresent(LKAProperty.class)) {
						LKAProperty param = field.getAnnotation(LKAProperty.class);
						if(param.hidden())continue;
						boolean bool = false;
						if(group != null && !"".equals(group)) {
							String[] groups = param.groups();
							
							if(groups != null && groups.length > 0) {
								for (String gst : groups) {
									if(gst == null) continue;
									String[] gs = gst.split("-");
									if(gs == null || gs[0] == null || "".equals(gs[0]) || !gs[0].equals(group)) {
										continue;
									}else {
										if(gs.length > 1 && gs[1].equals("n")) {
											bool2 = true;
										}
										bool = true;
										break;
									}
								}
							}
							if(!bool) continue;
						}
						Class<?> ctype = param.type();
						String pValue = field.getName();
						Class<?> pType = field.getType();
						//获取泛形类型
						Class<?> gType = getGenericType(pType, field);
						PropertyModel propertyModel = new PropertyModel();
						if (ctype.getName() != "java.lang.Object" || isObj(gType) == 3) {
							if(ctype.getName() != "java.lang.Object") {
								propertyModel = analysisProModel(url,ctype,group,proType);
							}else {
								propertyModel = analysisProModel(url,gType,group,proType);
							}
							if (propertyModel == null) {
								propertyModel = new PropertyModel();
							}
							propertyModel.setArray(param.isArray());
							if(!param.isArray() && (pType.getSimpleName().contains("[]") || 
									pType.equals(List.class) || 
									pType.equals(Set.class) || 
									pType.equals(ArrayList.class) || 
									pType.equals(LinkedList.class) ||
									pType.equals(Vector.class) ||
									pType.equals(SortedSet.class) ||
									pType.equals(HashSet.class) ||
									pType.equals(TreeSet.class) ||
									pType.equals(LinkedHashSet.class))) {
								propertyModel.setArray(true);
							}
							propertyModel.setValue(pValue);
							propertyModel.setName(param.value());
							propertyModel.setDescription(param.description());
							propertyModel.setDataType("Object");
							propertyModels.add(propertyModel);
						} else {
							propertyModel.setDataType(pType.getSimpleName());
							propertyModel.setDescription(param.description());
							propertyModel.setName(param.value());
							propertyModel.setTestData(param.testData());
							String[] split = param.value().split("\\^");
							if(split.length == 2) {
								propertyModel.setName(split[0]);
								propertyModel.setTestData(split[1]);
							}

							String[] split2 = split[0].split("\\~");
							if(split2.length == 2) {
								propertyModel.setName(split2[1]);
								if(split2[0].contains("n"))propertyModel.setRequired(false);
							}
							if(bool) {
								propertyModel.setRequired(true);
							}
							if(bool2) {
								propertyModel.setRequired(false);
							}
							propertyModel.setParamType("query");
							propertyModel.setArray(param.isArray());
							if(!param.isArray() && (pType.getSimpleName().contains("[]") || 
									pType.equals(List.class) || 
									pType.equals(Set.class) || 
									pType.equals(ArrayList.class) || 
									pType.equals(LinkedList.class) ||
									pType.equals(Vector.class) ||
									pType.equals(SortedSet.class) ||
									pType.equals(HashSet.class) ||
									pType.equals(TreeSet.class) ||
									pType.equals(LinkedHashSet.class))) {
								propertyModel.setArray(true);
							}
							propertyModel.setValue(pValue);
							propertyModels.add(propertyModel);
						}
					}else {
						ApiModelProperty param = field.getAnnotation(ApiModelProperty.class);
						if(param.hidden())continue;
						boolean bool = false;
						if(group != null && !"".equals(group)) {
							String[] groups = param.groups();
							
							if(groups != null && groups.length > 0) {
								for (String gst : groups) {
									if(gst == null) continue;
									String[] gs = gst.split("-");
									if(gs == null || gs[0] == null || "".equals(gs[0]) || !gs[0].equals(group)) {
										continue;
									}else {
										if(gs.length > 1 && gs[1].equals("n")) {
											bool2 = true;
										}
										bool = true;
										break;
									}
								}
							}
							if(!bool) continue;
						}
						Class<?> ctype = param.type();
						String pValue = field.getName();
						Class<?> pType = field.getType();
						//获取泛形类型
						Class<?> gType = getGenericType(pType, field);
						PropertyModel propertyModel = new PropertyModel();
						if (ctype.getName() != "java.lang.Object" || isObj(gType) == 3) {
							if(ctype.getName() != "java.lang.Object") {
								propertyModel = analysisProModel(url,ctype,group,proType);
							}else {
								propertyModel = analysisProModel(url,gType,group,proType);
							}
							if (propertyModel == null) {
								propertyModel = new PropertyModel();
								propertyModel.setDataType("Object");
							}
							propertyModel.setArray(param.isArray());
							if(!param.isArray() && (pType.getSimpleName().contains("[]") || 
									pType.equals(List.class) || 
									pType.equals(Set.class) || 
									pType.equals(ArrayList.class) || 
									pType.equals(LinkedList.class) ||
									pType.equals(Vector.class) ||
									pType.equals(SortedSet.class) ||
									pType.equals(HashSet.class) ||
									pType.equals(TreeSet.class) ||
									pType.equals(LinkedHashSet.class))) {
								propertyModel.setArray(true);
							}
							propertyModel.setValue(pValue);
							propertyModel.setName(param.value());
							propertyModel.setDescription(param.description());
							propertyModels.add(propertyModel);
						} else {
							propertyModel.setDataType(pType.getSimpleName());
							propertyModel.setDescription(param.description());
							propertyModel.setName(param.value());
							propertyModel.setTestData(param.testData());
							String[] split = param.value().split("\\^");
							if(split.length == 2) {
								propertyModel.setName(split[0]);
								propertyModel.setTestData(split[1]);
							}

							String[] split2 = split[0].split("\\~");
							if(split2.length == 2) {
								propertyModel.setName(split2[1]);
								if(split2[0].contains("n"))propertyModel.setRequired(false);
							}
							if(bool) {
								propertyModel.setRequired(true);
							}
							if(bool2) {
								propertyModel.setRequired(false);
							}
							propertyModel.setParamType("query");
							propertyModel.setArray(param.isArray());
							if(!param.isArray() && (pType.getSimpleName().contains("[]") || 
									pType.equals(List.class) || 
									pType.equals(Set.class) || 
									pType.equals(ArrayList.class) || 
									pType.equals(LinkedList.class) ||
									pType.equals(Vector.class) ||
									pType.equals(SortedSet.class) ||
									pType.equals(HashSet.class) ||
									pType.equals(TreeSet.class) ||
									pType.equals(LinkedHashSet.class))) {
								propertyModel.setArray(true);
							}
							propertyModel.setValue(pValue);
							propertyModels.add(propertyModel);
						}
					}
				}
				
			}
			modelModel.setPropertyModels(propertyModels);
		}
		pm.setModelModel(modelModel);
		proNum = 0;
		return pm;
	}

	/**
	 * 	解析响应参数的ResposeModel对象注解
	 * @param url 位置
	 * @param typeCls 类型
	 * @param group 组名
	 * @return ResposeModel 对象
	 * @throws Exception 异常
	 */
	public ResposeModel analysisResModel(String url,Class<?> typeCls,String group) throws Exception {
		respNum++;
		if(respNum > 10) {
			respNum = 0;
			return null;
		}
		if (!typeCls.isAnnotationPresent(LKAModel.class) && !typeCls.isAnnotationPresent(ApiModel.class)) {
			respNum = 0;
			return null;
		}
		ResposeModel rm = new ResposeModel();
		
		// 获取model描述信息
		ModelModel modelModel = new ModelModel();
		if(typeCls.isAnnotationPresent(LKAModel.class)) {
			LKAModel lkaModel = typeCls.getAnnotation(LKAModel.class);
			modelModel.setValue(typeCls.getSimpleName());
			modelModel.setName(lkaModel.value());
			modelModel.setDescription(lkaModel.description());
		}else {
			ApiModel lkaModel = typeCls.getAnnotation(ApiModel.class);
			modelModel.setValue(typeCls.getSimpleName());
			modelModel.setName(lkaModel.value());
			modelModel.setDescription(lkaModel.description());
		}
		
		// 获取所有属性对象
		Field[] fields = typeCls.getDeclaredFields();
		
		//获取父类所有属性对象
		Field[] declaredField;
		try {
			declaredField = getDeclaredField(typeCls.newInstance());
		} catch (Exception e) {
			declaredField = null;
		}
		Object[] arrays = null;
		//合并数组
		if(declaredField != null) {
			List<Field> list = new ArrayList<>(Arrays.asList(declaredField));
			arrays = list.toArray();
		}else {
			arrays = fields;
		}
		
		if (arrays != null && arrays.length > 0) {
			List<PropertyModel> propertyModels = new ArrayList<PropertyModel>();
			for (Object obj : arrays) {
				Field field =  (Field)obj;
				if (!field.isAnnotationPresent(LKAProperty.class) && !field.isAnnotationPresent(ApiModelProperty.class)) {
					if(sconAll) {
						Class<?> type = field.getType();
						PropertyModel propertyModel = new PropertyModel();
						int isArray = isObj(type);
						type = getGenericType(type, field);
						
						//判断数据类型
						propertyModel.setDataType(type.getSimpleName());
						int flag = isObj(type);
			            if(flag == 3) {
			            	propertyModel = analysisProModel(url,type,"",2);
			            	if(propertyModel == null) { 
			            		propertyModel = new PropertyModel();
			            		propertyModel.setDataType("Object");
			            	}
			            }
			            String name = field.getName();
			            propertyModel.setArray(false);
			            if(isArray == 2 || isArray == 4) {
							propertyModel.setDataType(propertyModel.getDataType()+"[]");
						}
		            	propertyModel.setName(enToCn(url,2,name));
		            	propertyModel.setValue(name);
		            	propertyModel.setRequired(false);
		            	propertyModel.setDescription("");
		            	propertyModel.setTestData("");
						try {
							if(type.isAnnotationPresent(PathVariable.class)) {
								propertyModel.setParamType(ParamType.PATH);
							}else if(type.isAnnotationPresent(RequestHeader.class)) {
								propertyModel.setParamType(ParamType.HEADER);
							}else {
								propertyModel.setParamType(ParamType.QUERY);
							}
						} catch (Exception e) {
							propertyModel.setParamType(ParamType.QUERY);
						}
						propertyModels.add(propertyModel);
					}
				}else {
					boolean bool2 = false;
					if(field.isAnnotationPresent(LKAProperty.class)) {
						LKAProperty param = field.getAnnotation(LKAProperty.class);
						if(param.hidden())continue;
						boolean bool = false;
						if(group != null && !"".equals(group)) {
							String[] groups = param.groups();
							
							if(groups != null && groups.length > 0) {
								for (String gst : groups) {
									if(gst == null) continue;
									String[] gs = gst.split("-");
									if(gs == null || gs[0] == null || "".equals(gs[0]) || !gs[0].equals(group)) {
										continue;
									}else {
										if(gs.length > 1 && gs[1].equals("n")) {
											bool2 = true;
										}
										bool = true;
										break;
									}
								}
							}
							if(!bool) continue;
						}
						Class<?> ctype = param.type();
						String pValue = field.getName();
						Class<?> pType = field.getType();
						//获取泛形类型
						Class<?> gType = getGenericType(pType, field);
						PropertyModel propertyModel = new PropertyModel();
						if (ctype.getName() != "java.lang.Object" || isObj(gType) == 3) {
							if(ctype.getName() != "java.lang.Object") {
								propertyModel = analysisProModel(url,ctype,group,2);
							}else {
								propertyModel = analysisProModel(url,gType,group,2);
							}
							if (propertyModel == null) {
								propertyModel = new PropertyModel();
								propertyModel.setDataType("Object");
							}
							propertyModel.setArray(param.isArray());
							if(!param.isArray() && (pType.getSimpleName().contains("[]") || 
									pType.equals(List.class) || 
									pType.equals(Set.class) || 
									pType.equals(ArrayList.class) || 
									pType.equals(LinkedList.class) ||
									pType.equals(Vector.class) ||
									pType.equals(SortedSet.class) ||
									pType.equals(HashSet.class) ||
									pType.equals(TreeSet.class) ||
									pType.equals(LinkedHashSet.class))) {
								propertyModel.setArray(true);
							}
							propertyModel.setValue(pValue);
							propertyModel.setName(param.value());
							propertyModel.setDescription(param.description());
							propertyModels.add(propertyModel);
						} else {
							propertyModel.setDataType(pType.getSimpleName());
							propertyModel.setDescription(param.description());
							propertyModel.setName(param.value());
							propertyModel.setTestData(param.testData());
							String[] split = param.value().split("\\^");
							if(split.length == 2) {
								propertyModel.setName(split[0]);
								propertyModel.setTestData(split[1]);
							}

							String[] split2 = split[0].split("\\~");
							if(split2.length == 2) {
								propertyModel.setName(split2[1]);
								if(split2[0].contains("n"))propertyModel.setRequired(false);
							}
							if(bool) {
								propertyModel.setRequired(true);
							}
							if(bool2) {
								propertyModel.setRequired(false);
							}
							propertyModel.setParamType("query");
							propertyModel.setArray(param.isArray());
							if(!param.isArray() && (pType.getSimpleName().contains("[]") || 
									pType.equals(List.class) || 
									pType.equals(Set.class) || 
									pType.equals(ArrayList.class) || 
									pType.equals(LinkedList.class) ||
									pType.equals(Vector.class) ||
									pType.equals(SortedSet.class) ||
									pType.equals(HashSet.class) ||
									pType.equals(TreeSet.class) ||
									pType.equals(LinkedHashSet.class))) {
								propertyModel.setArray(true);
							}
							propertyModel.setValue(pValue);
							propertyModels.add(propertyModel);
						}
					}else {
						ApiModelProperty param = field.getAnnotation(ApiModelProperty.class);
						if(param.hidden())continue;
						boolean bool = false;
						if(group != null && !"".equals(group)) {
							String[] groups = param.groups();
							
							if(groups != null && groups.length > 0) {
								for (String gst : groups) {
									if(gst == null) continue;
									String[] gs = gst.split("-");
									if(gs == null || gs[0] == null || "".equals(gs[0]) || !gs[0].equals(group)) {
										continue;
									}else {
										if(gs.length > 1 && gs[1].equals("n")) {
											bool2 = true;
										}
										bool = true;
										break;
									}
								}
							}
							if(!bool) continue;
						}
						Class<?> ctype = param.type();
						String pValue = field.getName();
						Class<?> pType = field.getType();
						//获取泛形类型
						Class<?> gType = getGenericType(pType, field);
						PropertyModel propertyModel = new PropertyModel();
						if (ctype.getName() != "java.lang.Object" || isObj(gType) == 3) {
							if(ctype.getName() != "java.lang.Object") {
								propertyModel = analysisProModel(url,ctype,group,2);
							}else {
								propertyModel = analysisProModel(url,gType,group,2);
							}
							if (propertyModel == null) {
								propertyModel = new PropertyModel();
								propertyModel.setDataType("Object");
							}
							propertyModel.setArray(param.isArray());
							if(!param.isArray() && (pType.getSimpleName().contains("[]") || 
									pType.equals(List.class) || 
									pType.equals(Set.class) || 
									pType.equals(ArrayList.class) || 
									pType.equals(LinkedList.class) ||
									pType.equals(Vector.class) ||
									pType.equals(SortedSet.class) ||
									pType.equals(HashSet.class) ||
									pType.equals(TreeSet.class) ||
									pType.equals(LinkedHashSet.class))) {
								propertyModel.setArray(true);
							}
							propertyModel.setValue(pValue);
							propertyModel.setName(param.value());
							propertyModel.setDescription(param.description());
							propertyModels.add(propertyModel);
						} else {
							propertyModel.setDataType(pType.getSimpleName());
							propertyModel.setDescription(param.description());
							propertyModel.setName(param.value());
							propertyModel.setTestData(param.testData());
							String[] split = param.value().split("\\^");
							if(split.length == 2) {
								propertyModel.setName(split[0]);
								propertyModel.setTestData(split[1]);
							}

							String[] split2 = split[0].split("\\~");
							if(split2.length == 2) {
								propertyModel.setName(split2[1]);
								if(split2[0].contains("n"))propertyModel.setRequired(false);
							}
							if(bool) {
								propertyModel.setRequired(true);
							}
							if(bool2) {
								propertyModel.setRequired(false);
							}
							propertyModel.setParamType("query");
							propertyModel.setArray(param.isArray());
							if(!param.isArray() && (pType.getSimpleName().contains("[]") || 
									pType.equals(List.class) || 
									pType.equals(Set.class) || 
									pType.equals(ArrayList.class) || 
									pType.equals(LinkedList.class) ||
									pType.equals(Vector.class) ||
									pType.equals(SortedSet.class) ||
									pType.equals(HashSet.class) ||
									pType.equals(TreeSet.class) ||
									pType.equals(LinkedHashSet.class))) {
								propertyModel.setArray(true);
							}
							propertyModel.setValue(pValue);
							propertyModels.add(propertyModel);
						}
					}
				}
			}
			modelModel.setPropertyModels(propertyModels);
		}
		rm.setModelModel(modelModel);
		respNum = 0;
		return rm;
	}
	
	/**
	 * 	添加字段备注信息
	 * @param value 字段值
	 * @param type 字段类型(1.请求参数 2.响应参数)
	 * @param url 请求地址
	 * @param modaltype 修改类型(1.添加 2.修改 3.删除 4.other)
	 * @param content 备注信息
	 * @return String 结果消息
	 * @param serverName 服务器名称
	 */
	@PostMapping("addParamInfo")
	public String addParamInfo(String value,String type,String url,String modaltype,String content,String serverName){
		if(serverName != null && !"".equals(serverName)) {
			RestTemplate restTemplate = new RestTemplate();
			MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
	        requestBody.add("value",value);
	        requestBody.add("type",type);
	        requestBody.add("url",url);
	        requestBody.add("modaltype",modaltype);
	        requestBody.add("content",content);
	        HttpHeaders requestHeaders = new HttpHeaders();
	        requestHeaders.add("Content-Type","application/x-www-form-urlencoded");
	        @SuppressWarnings("rawtypes")
			HttpEntity<MultiValueMap> requestEntity = new HttpEntity<>(requestBody,requestHeaders);
			ResponseEntity<String> exchange = restTemplate.exchange(serverName+"/lkad/addParamInfo",HttpMethod.POST,requestEntity,String.class);
			return exchange.getBody();
		}
		//先删除作用
		if("5".equals(modaltype)) {
			value = value.replace("[]","");
			Map<Object, Object> map = getParamInfo(serverName);
			if(map != null) {
				for (Entry<Object, Object> entry : map.entrySet()) {
					if(entry.getKey().equals(url+"."+type+"."+value)) {
						delParamInfo(value,type,url,serverName);
					}
				}
			}
		}
		
		File file = new File("lkadParamInfo.properties");
		FileOutputStream outStream = null;
        try {
			outStream = new FileOutputStream(file,true); 
			Properties prop= new Properties();
			prop.setProperty(url+"."+type+"."+value,modaltype+"-"+content);
			prop.store(outStream, null);
			return "操作成功";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	if(outStream != null) {
        		try {
					outStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
		return "操作失败";
	}
	
	/**
	 * 	删除字段备注信息 
	 * @param value 字段值
	 * @param type type 字段类型(1.请求参数 2.响应参数)
	 * @param url 请求地址
	 * @param serverName 服务器名称
	 * @return String 结果消息
	 */
	@PostMapping("delParamInfo")
	public String delParamInfo(String value,String type,String url,String serverName){
		
		if(serverName != null && !"".equals(serverName)) {
			RestTemplate restTemplate = new RestTemplate();
			MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
	        requestBody.add("value",value);
	        requestBody.add("type",type);
	        requestBody.add("url",url);
	        HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.add("Content-Type","application/x-www-form-urlencoded");
	        @SuppressWarnings("rawtypes")
			HttpEntity<MultiValueMap> requestEntity = new HttpEntity<>(requestBody, requestHeaders);
			ResponseEntity<String> exchange = restTemplate.exchange(serverName+"/lkad/delParamInfo",HttpMethod.POST,requestEntity, String.class);
			return exchange.getBody();
		}
		
		File file = new File("lkadParamInfo.properties");
		FileOutputStream outStream = null;
		FileInputStream inStream = null;
        try {
        	Properties prop= new Properties();
        	inStream = new FileInputStream(file);
            prop.load(inStream);
            prop.remove(url+"."+type+"."+value);
            outStream = new FileOutputStream(file);
            prop.store(outStream, null);
            return "操作成功";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	if(outStream != null) {
        		try {
					outStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	if(inStream != null) {
        		try {
        			inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
		return "操作失败";
	}
	
	/**
	 * 	获取字段备注信息 
	 * @param serverName 服务器名称
	 * @return map 集合
	 */
	@SuppressWarnings("unchecked")
	@GetMapping("getParamInfo")
	public Map<Object, Object> getParamInfo(String serverName){
		
		if(serverName != null && !"".equals(serverName)) {
			RestTemplate restTemplate = new RestTemplate();
			Map<Object, Object> forObject = restTemplate.getForObject(serverName+"/lkad/getParamInfo", Map.class);
			return forObject;
		}
		
		Map<Object, Object> map = new HashMap<Object, Object>();
		File file = new File("lkadParamInfo.properties");
		FileInputStream inStream = null;
        try {
        	inStream = new FileInputStream(file); 
            Properties prop = new Properties();
            prop.load(inStream);
            Set<Map.Entry<Object, Object>> entrySet = prop.entrySet();//返回的属性键值对实体
            for (Map.Entry<Object, Object> entry : entrySet) {
                map.put(entry.getKey(),entry.getValue());
            }
            return map;
        } catch (Exception e) {
           // e.printStackTrace();
        } finally {
        	if(inStream != null) {
        		try {
        			inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }       
		return null;
	}

	/**
	 * July_wonderful兄优化方案(支持多层继承)
	 * @param object 对象
	 * @return Field 属性对象
	 */
    public  Field[] getDeclaredField(Object object) {
    	Class<?> clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }
    
    /**
     * 	骆峰拆分成多个单词
     * @param str 拆分字符串
     * @return String 拆分后结果
     */
    public String toHump(String str) {
        String rs = "";

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                rs += " " + Character.toLowerCase(c);
            } else {
                rs += c;
            }
        }
        return rs;
    }
    
    
    private Set<Map.Entry<Object, Object>> entrySet = null;
    public String enToCn(String url,Integer type,String name) {
		FileInputStream inStream = null;
        try {
        	if(entrySet == null) {
	        	File file = new File("lkadParamInfo.properties");
	        	inStream = new FileInputStream(file); 
	            Properties prop = new Properties();
	            prop.load(inStream);
	            entrySet = prop.entrySet();//返回的属性键值对实体
        	}
        	if(entrySet != null) {
	            for (Map.Entry<Object, Object> entry : entrySet) {
	            	if(entry.getKey().equals(url+"."+type+"."+name)) {
	            		String value = entry.getValue().toString();
	            		String[] split = value.split("-");
	            		if("5".equals(split[0])) {
	            			String str = "";
	            			for (int i = 1;i<split.length;i++) {
								str+=split[i];
							}
	            			return str;
	            		}
	            	}
	            }
        	}
        } catch (Exception e) {
           // e.printStackTrace();
        } finally {
        	if(inStream != null) {
        		try {
        			inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }       
    	
        //有道翻译入口已关闭
    	/*if(enToCn) { //开启入参翻译
			try {
				RestTemplate restTemplate = new RestTemplate();
				String data = "http://fanyi.youdao.com/translate?&doctype=json&type=EN2ZH_CN&i="+toHump(name);
				ResponseEntity<Map> exchange = restTemplate.exchange(data,HttpMethod.GET,null,Map.class);
				Object object = exchange.getBody().get("translateResult");
				List list = (List)object;
				String txt = "";
				for (Object obj : list) {
					List list2 = (List)obj;
					for (Object obj2 : list2) {
						Map map = (Map)obj2;
						if("".equals(txt)) {
							txt = map.get("tgt").toString();
						}else {
							txt = txt + "//" + map.get("tgt").toString();
						}
					}
				}
				return txt;
			} catch (Exception e1) {
				return name;
			}
		}*/
    	return name;
    }
    
    /**
     * 	判断数据类型
     * @param type 类型
     * @return int 类型标识
     */
    public int isObj(Class<?> type) {
    	if(type.equals(Integer.class) || type.equals(int.class)){
			return 0;
        }else if(type.equals(Byte.class) || type.equals(byte.class)){
        	return 0;
        }else if(type.equals(Short.class) || type.equals(short.class)){
        	return 0;
        }else if(type.equals(Float.class) || type.equals(float.class)){
        	return 0;
        }else if(type.equals(Double.class) || type.equals(double.class)){
        	return 0;
        }else if(type.equals(Character.class) || type.equals(char.class)){
        	return 0;
        }else if(type.equals(Long.class) || type.equals(long.class)){
        	return 0;
        }else if(type.equals(Boolean.class) || type.equals(boolean.class)){
        	return 0;
        }else if(type.equals(String.class) || type.equals(BigDecimal.class)){
        	return 0;
        }else if(type.equals(Date.class) || 
        		type.equals(java.sql.Date.class) || 
        		type.equals(LocalDate.class) ||
        		type.equals(LocalDateTime.class)){
        	return 0;
        }else if(type.equals(Map.class) ||
        		type.equals(HashMap.class) ||
        		type.equals(LinkedHashMap.class) ||
        		type.equals(TreeMap.class) ||
        		type.equals(Hashtable.class) ||
        		type.equals(ConcurrentHashMap.class)
        		) {
        	return 1;
        }else if(type.equals(List.class) || 
        		type.equals(Set.class) || 
        		type.equals(ArrayList.class) || 
        		type.equals(LinkedList.class) ||
        		type.equals(Vector.class) ||
        		type.equals(SortedSet.class) ||
        		type.equals(HashSet.class) ||
        		type.equals(TreeSet.class) ||
        		type.equals(LinkedHashSet.class)) {
        	return 2;
        }else if(type.isArray()) {
        	return 4;
        }
    	return 3;
    }
    
    /**
     * 	获取数组或list、set集合泛形类型
     * @param type 类型
     * @param field 属性
     * @return Class 泛形类型
     */
    public Class<?> getGenericType(Class<?> type,Field field){
	    try {
			if(type.equals(List.class) || 
	        		type.equals(Set.class) || 
	        		type.equals(ArrayList.class) || 
	        		type.equals(LinkedList.class) ||
	        		type.equals(Vector.class) ||
	        		type.equals(SortedSet.class) ||
	        		type.equals(HashSet.class) ||
	        		type.equals(TreeSet.class) ||
	        		type.equals(LinkedHashSet.class)) { //集合
				// 当前集合的泛型类型
			    Type genericType = field.getGenericType();
			    if (null == genericType) {
			        type = Object.class;
			    }
			    if (genericType instanceof ParameterizedType) {
			    	ParameterizedType pt = (ParameterizedType) genericType;
			        try {
						//得到泛型里的class类型对象
						type = (Class<?>)pt.getActualTypeArguments()[0];
					} catch (Exception e) {
						try {
							ParameterizedType pt2 =(ParameterizedType)pt.getActualTypeArguments()[0];
							type = (Class<?>)pt2.getRawType();
						} catch (Exception e1) {
							
						}
					}
			    }
			}else if(type.isArray()) {//数组
				// 获取数组元素的类型
				type = type.getComponentType();
			}
		} catch (Exception e) {
		}
	    return type;
    }
}
