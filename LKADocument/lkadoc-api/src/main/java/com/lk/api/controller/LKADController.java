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
import java.lang.reflect.TypeVariable;
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
import com.lk.api.utils.TypeCls;

/**
 * 	?????????????????????
 * @author liukai
 *
 */
@RestController
@RequestMapping("lkad")
public class LKADController {
	
	@Autowired
	private WebApplicationContext applicationContext;
	
	/* ??????????????? */
	@Value("${lkad.basePackages:}")
	private String basePackages;
	/* ???????????? */
	@Value("${lkad.projectName:lkadoc????????????}")
	private String projectName;
	/* ???????????? */
	@Value("${lkad.description:???????????????????????????}")
	private String description;
	/* ???????????? */
	@Value("${lkad.enabled:true}")
	private Boolean enabled;
	/*???????????????*/
	@Value("${lkad.serverNames:}")
	private String serverNames;
	/*???????????????*/
	@Value("${lkad.version:}")
	private String version;
	/* ?????????????????? */
	@Value("${lkad.sconAll:false}")
	private Boolean sconAll;
	/*????????????????????????*/
	@Value("${lkad.password:}")
	private String password;
	
	/*??????????????????????????????*/
	@Value("${lkad.classNum:5000}")
	private String classNum;

	private int reqNum = 0,respNum = 0,proNum = 0;
	/**
	 * 	???????????????????????????
	 * @param path ??????
	 * @param contentType ??????
	 * @param headerJson ?????????
	 * @param queryData ????????????
	 * @param type ??????
	 * @return Object ??????
	 */
	@SuppressWarnings("rawtypes")
	@GetMapping("getServerApi")
	public Object getServerApi(String path,String contentType,String headerJson,String queryData,String type) {
		Map<String, Object> headerMap = JsonUtils.toMap(headerJson);
		Map<String, Object> queryMap = JsonUtils.toMap(queryData);
		RestTemplate restTemplate = new RestTemplate();
		//???????????????
		HttpHeaders requestHeaders = new HttpHeaders();
		Set<String> hSet = headerMap.keySet();
		for (String key : hSet) {
			requestHeaders.add(key,headerMap.get(key)==null?"":headerMap.get(key).toString());
		}
		
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new ByteArrayHttpMessageConverter());
        /** ?????????????????????converter */
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(Charset.forName
                ("UTF-8"));
        messageConverters.add(stringHttpMessageConverter);
        messageConverters.add(new ResourceHttpMessageConverter());
        messageConverters.add(new SourceHttpMessageConverter());
        messageConverters.add(new AllEncompassingFormHttpMessageConverter());
        restTemplate.setMessageConverters(messageConverters);
		
        //HttpEntity
		Object object = null;
		
		//????????????
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
	        	return "<span style='color:red'>ContentType??????application/json????????????post???put???delete?????????<span>";	        }
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
	 * ????????????????????????
	 * @return int 1??????0??????
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
	 * 	??????????????????????????????
	 * @param serverName ???????????????
	 * @param pwd ??????
	 * @param type ??????
	 * @return Map ??????
	 * @throws Exception ??????
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
				map.put("error",serverName+"????????????????????????");
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
				map.put("error", "Lkadoc???????????????????????????");
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
					classNum = annotation.classNum();
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
						map.put("error", "Lkadoc???????????????????????????");
						return map;
					}
					bool = true;
					break;
				}
			}
			if(!bool) {
				bpk = "";
				map.put("error", "?????????????????????????????????");
				return map;
			}
		}
		
		//????????????
		if(password != null && !"".equals(password) && type == 1) {
			if(!password.equals(pwd)) {
				bpk = "";
				map.put("error", "???????????????????????????????????????");
				return map;
			}
		}
		
		//????????????
		List<TypeModel> typeModels;
		try {
			typeModels = scanType(bpk.split(","));
		} catch (Exception e) {
			map.put("error",e.getMessage());
			e.printStackTrace();
			return map;
		}
		//?????????
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
		
		//????????????
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
			//????????????
			Collections.sort(typeModel.getMethodModels());
		}
		map.put("apiDoc",typeModels);
		return map;
	}
	
	/**
	 * 	??????????????????
	 * @param rms  ?????????
	 * @param rms2  ?????????
	 * @param n ????????????????????????????????????
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
	 * 	??????????????????????????????
	 * 
	 * @param basePackages ??????
	 * @return list ??????
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
	 *	 ??????????????????
	 * @param baseFile ????????????
	 * @return list ??????
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
	 * 	???????????????????????????????????????????????????????????????????????????
	 * @param basePackages ???????????????
	 * @return list ??????
	 * @throws Exception ??????
	 */
	@SuppressWarnings({"unchecked" })
	public List<TypeModel> scanType(String[] basePackages) throws Exception {
		List<TypeModel> typeModels = new ArrayList<TypeModel>();
		// ?????????????????????
		if (basePackages != null) {
			// ?????????????????????????????????
			List<Map<String, Object>> methodURLs = getMethodURL();
			List<String> sconPackages = new ArrayList<>();
			for (String basePackage : basePackages) {
				SconPackage sconPackage = new SconPackage(basePackage);
				sconPackages.addAll(sconPackage.getFullyQualifiedClassNameList());
			}
			
			for (int n = 0;n<sconPackages.size();n++) {
				if(n>=Integer.parseInt(classNum)) break;
				Class<?> cls = null;
				try {
					cls = Class.forName(sconPackages.get(n));
					if(cls == null) {
						continue;
					}
				} catch (Throwable e) {
					continue;
				}
				// ???????????????LKAType??????Api??????
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
				// ?????????????????????
				typeModel.setValue(cls.getSimpleName());
				// ??????????????????
				Method[] methods = cls.getMethods();
				if (methods != null && methods.length > 0) {
					List<MethodModel> methodModels = new ArrayList<MethodModel>();
					for (Method method : methods) {
						if (method == null)continue;
						MethodModel methodModel = new MethodModel();
						// ???????????????LKAMethod??????
						if (!method.isAnnotationPresent(LKAMethod.class) && !method.isAnnotationPresent(ApiOperation.class)) continue;
						if(method.isAnnotationPresent(LKAMethod.class)) {
							LKAMethod lkaMethod = method.getAnnotation(LKAMethod.class);
							if(lkaMethod.hidden())continue;
							// ????????????????????????
							methodModel.setName(lkaMethod.value());
							methodModel.setDescription(lkaMethod.description());
							methodModel.setValue(method.getName());
							methodModel.setUrl("???API?????????????????????");
							methodModel.setVersion(lkaMethod.version());
							methodModel.setContentType(lkaMethod.contentType());
							methodModel.setRequestType("??????");
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
							// ????????????????????????
							methodModel.setName(lkaMethod.value());
							methodModel.setDescription(lkaMethod.notes());
							methodModel.setValue(method.getName());
							methodModel.setUrl("???API?????????????????????");
							methodModel.setVersion(lkaMethod.version());
							methodModel.setContentType(lkaMethod.contentType());
							methodModel.setRequestType("??????");
							methodModel.setAuthor(lkaMethod.author());
							methodModel.setCreateTime(lkaMethod.createTime());
							methodModel.setUpdateTime(lkaMethod.updateTime());
							methodModel.setDownload(lkaMethod.download());
							methodModel.setToken(lkaMethod.token());
							methodModel.setOrder(lkaMethod.order());
							methodModel.setDirectory(lkaMethod.directory());
						}
						
						for (Map<String, Object> map : methodURLs) {
							if(map.get("className") != null && 
									map.get("methodName") != null && 
									method.getDeclaringClass() != null && 
									method.getDeclaringClass().getName() != null) {
								
								Object params = map.get("params");
								Object className = map.get("className").toString();
								Object methodName = map.get("methodName").toString();
								String classn = method.getDeclaringClass().getName();
								
								if (classn.equals(className) && method.getName().equals(methodName)) {
									//????????????	
									if(params != null && params instanceof Parameter[]) {
										Parameter[] parameters = (Parameter[])params;
										Parameter[] parameters2 = method.getParameters();
										if(parameters2 != null && parameters2.length > 0) {
											if(parameters.length != parameters2.length) {
												continue;
											}
											for(int i = 0;i<parameters.length;i++) {
												if(parameters[i].getType()!= null && parameters2[i].getType() != null) {
													String simpleName = parameters[i].getType().getSimpleName();
													String simpleName2 = parameters2[i].getType().getSimpleName();
													if(!simpleName.equals(simpleName2)) {
														continue;
													}
												}
											}
										}
									}
									
									Object url = map.get("methodURL");
									Object requestType = map.get("requestType");
									if (url == null) {
										url = "???API?????????????????????";
										requestType = "??????";
									}else {
										url = url.toString().substring(1,url.toString().length());
									}
									if (url != null && requestType == null) {
										requestType = "??????";
									}
									methodModel.setUrl(url.toString());
									methodModel.setRequestType(requestType.toString());
								}else {
									Object interfacesNames = map.get("interfacesNames");
									if(interfacesNames == null) {
										continue;
									}
									if(interfacesNames instanceof List) {
										List<String> list = (List<String>)interfacesNames;
											for (String str : list) {
												if(classn.equals(str) && method.getName().equals(methodName)) {
													//????????????													
													if(params != null && params instanceof Parameter[]) {
														Parameter[] parameters = (Parameter[])params;
														Parameter[] parameters2 = method.getParameters();
														if(parameters2 != null && parameters2.length > 0) {
															if(parameters.length != parameters2.length) {
																continue;
															}
															for(int i = 0;i<parameters.length;i++) {
																if(parameters[i].getType()!= null && parameters2[i].getType() != null) {
																	String simpleName = parameters[i].getType().getSimpleName();
																	String simpleName2 = parameters2[i].getType().getSimpleName();
																	if(!simpleName.equals(simpleName2)) {
																		continue;
																	}
																}
															}
														}
													}
													
													Object url = map.get("methodURL");
													Object requestType = map.get("requestType");
													if (url == null) {
														url = "???API?????????????????????";
														requestType = "??????";
													}else {
														url = url.toString().substring(1,url.toString().length());
													}
													if (url != null && requestType == null) {
														requestType = "??????";
													}
													methodModel.setUrl(url.toString());
													methodModel.setRequestType(requestType.toString());
													break;
												}
											}
										}
								}
							}
						}

						List<ParamModel> request = new ArrayList<ParamModel>();
						List<ResposeModel> respose = new ArrayList<ResposeModel>();
						/******/
						//?????????????????????????????????model??????
						Class<?> returnType = method.getReturnType();
						if(returnType != null && !"void".equals(returnType.getName())) {
							boolean bool2 = false;
							boolean isParentArray = false;
							boolean isParentArray2 = false;
							boolean isParentArray3 = false;
							boolean isParentArray4 = false;
							Class<?> proType = null;
							Class<?> proType2 = null;
							Class<?> proType3 = null;
							Class<?> proType4 = null;
							Class<?> parentProType = null;
							if(returnType.isArray()) {//??????
								// ???????????????????????????
								proType = returnType.getComponentType();
								isParentArray = true;
							}else {
								//????????????Type???????????????????????????Method??????????????????????????????????????????
								Type genericReturnType = method.getGenericReturnType();
								if(genericReturnType instanceof ParameterizedType) {
			                        ParameterizedType pt = (ParameterizedType) genericReturnType;
			                        //??????????????????class????????????
									try {
										isParentArray = isParentArray(returnType);
										proType = (Class<?>)pt.getActualTypeArguments()[0];										
									} catch (Exception e) {
										try {
											ParameterizedType pt2 =(ParameterizedType)pt.getActualTypeArguments()[0];
											proType = (Class<?>)pt2.getRawType();
											try {
												isParentArray2 = isParentArray(proType);
												proType2 = (Class<?>)pt2.getActualTypeArguments()[0];
											} catch (Exception e1) {
												ParameterizedType pt3 =(ParameterizedType)pt2.getActualTypeArguments()[0];
												proType2 = (Class<?>)pt3.getRawType();
												try {
													isParentArray3 = isParentArray(proType2);
													proType3 = (Class<?>)pt3.getActualTypeArguments()[0];													
												} catch (Exception e2) {
													ParameterizedType pt4 =(ParameterizedType)pt3.getActualTypeArguments()[0];
													proType3 = (Class<?>)pt4.getRawType();
													try {
														isParentArray4 = isParentArray(proType3);
														proType4 = (Class<?>)pt4.getActualTypeArguments()[0];
													} catch (Exception e3) {
														ParameterizedType pt5 =(ParameterizedType)pt4.getActualTypeArguments()[0];
														proType4 = (Class<?>)pt5.getRawType();
													}
												}
											}
										} catch (Exception e1) {
											//??????????????????,??????????????????
										}
									}
								}
							}
							if(isParentArray || returnType.isAnnotationPresent(LKAModel.class) || returnType.isAnnotationPresent(ApiModel.class)){
								methodModel.setReturnArray(false);
								if(isParentArray) {
									returnType = proType;
									methodModel.setReturnArray(true);
								}
								// ??????model????????????
								ModelModel modelModel = new ModelModel();
								modelModel.setValue(returnType.getSimpleName());
								// ????????????????????????
								Field[] fields = returnType.getDeclaredFields();
								
								//??????????????????????????????
								Field[] declaredField;
								try {
									declaredField = getDeclaredField(returnType.newInstance());
								} catch (Exception e) {
									declaredField = null;
								}
								Object[] arrays = null;
								//????????????
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
										//??????LKARespose????????????????????????????????????
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
											//???????????????????????????????????????????????????
											int proNum = 1;
											if(proType != null) {
												if("Object".equals(type.getSimpleName())) {
													type = proType;
													proNum = 2;
												}
											}
											PropertyModel propertyModel = null;
											TypeCls tcls = getGenericType(type, field);
											Class<?> ptype = tcls.getCls();
											if(ptype == null) {
												//??????????????????ptype????????????????????????T
												if(proType != null) {
													ptype = proType;
													proNum = 2;
												}else {
													ptype = type;
												}
											}
											if("Object".equals(field.getType().getSimpleName()) && tcls.isArray()) {
												//?????????????????????
												if(proType2 != null) {
													ptype = proType2;
													proNum = 3;
												}
											}
											if (field.isAnnotationPresent(LKAProperty.class) || 
													field.isAnnotationPresent(ApiModelProperty.class)) {
												Class<?> propertyType = null;
												String proPertyTypeName = "";
												String proPertyValue = "";
												String proPertyDesc = "";
												if(field.isAnnotationPresent(LKAProperty.class)){
													LKAProperty property = field.getAnnotation(LKAProperty.class);
													if(property.hidden()) { continue;}
													propertyType = property.type();
													proPertyTypeName = propertyType.getName();
													proPertyValue = property.value();
													proPertyDesc = property.description();
												}else{
													ApiModelProperty property = field.getAnnotation(ApiModelProperty.class);
													if(property.hidden()) { continue;}
													propertyType = property.type();
													proPertyTypeName = propertyType.getName();
													proPertyValue = property.value();
													proPertyDesc = property.description();
												}
												if(proPertyTypeName.equals("java.lang.Object") && isObj(ptype) != 3) {
													propertyModel = new PropertyModel();
												}else {
													if(isObj(ptype) == 3) {
														//????????????????????????
														Class<?> p1 = proNum==1?proType:proNum==2?proType2:proNum==3?proType3:proNum==4?proType4:null;
														Class<?> p2 = proNum==1?proType2:proNum==2?proType3:proNum==3?proType4:null;
														Class<?> p3 = proNum==1?proType3:proNum==2?proType4:null;
														Class<?> p4 = proNum==1?proType4:null;
														propertyModel = analysisProModel(methodModel.getUrl(),ptype,null,2,p1,p2,p3,p4);
														if(propertyModel == null) {
															propertyModel = new PropertyModel();
															propertyModel.setDataType("Object");
														}
													}else {
														//??????type??????????????????
														propertyModel = analysisProModel(methodModel.getUrl(),propertyType,null,2,null);
														if(propertyModel == null) {
															propertyModel = new PropertyModel();
															propertyModel.setDataType("Object");
														}
													}
												}
												propertyModel.setArray(false);
												if(tcls.isArray() || isObj(type) == 2 || 
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
												propertyModel.setName(proPertyValue);
												String[] split = proPertyValue.split("\\^");
												if(split.length == 2) {
													propertyModel.setName(split[0]);
													propertyModel.setTestData(split[1]);
												}
												String[] split2 = split[0].split("\\~");
												if(split2.length == 2) {
													propertyModel.setName(split2[1]);
													if(split2[0].contains("n"))propertyModel.setRequired(false);
												}
												propertyModel.setDescription(proPertyDesc);
												propertyModel.setDataType(field.getType().getSimpleName());
												propertyModels.add(propertyModel);
											}else {
												if(sconAll) {
													//??????????????????
													propertyModel.setDataType(type.getSimpleName());
													int flag = isObj(type);
										            if(flag == 3) {
										            	//????????????????????????
										            	Class<?> p1 = proNum==1?proType:proNum==2?proType2:proNum==3?proType3:proNum==4?proType4:null;
														Class<?> p2 = proNum==1?proType2:proNum==2?proType3:proNum==3?proType4:null;
														Class<?> p3 = proNum==1?proType3:proNum==2?proType4:null;
														Class<?> p4 = proNum==1?proType4:null;
										            	propertyModel = analysisProModel(methodModel.getUrl(),type,"",2,p1,p2,p3,p4);
										            	if(propertyModel == null) { 
										            		propertyModel = new PropertyModel();
										            		propertyModel.setDataType("Object");
										            	}
										            }
										            String name = field.getName();
										            propertyModel.setArray(false);
										            if(tcls.isArray() || isObj(type) == 2 || 
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
						
						//?????????????????????????????????model??????
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
						        		type.equals(LinkedHashSet.class)) { //??????
									isArray = true;
									// ???????????????????????????
				                    Type genericType = parameters[i].getParameterizedType();
				                    if (null == genericType) {
				                        continue;
				                    }
				                    if (genericType instanceof ParameterizedType) {
				                        ParameterizedType pt = (ParameterizedType) genericType;
				                        //??????????????????class????????????
				                        try {
											argument = (Class<?>)pt.getActualTypeArguments()[0];
										} catch (Exception e) {
											//continue;
										}
				                    }
								}
								if(type.isArray()) {//??????
									// ???????????????????????????
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
						
						// ??????????????????
						if (method.isAnnotationPresent(LKAParams.class) || method.isAnnotationPresent(ApiImplicitParams.class)) {
							if(method.isAnnotationPresent(LKAParams.class)) {
								LKAParams lkaParams = method.getAnnotation(LKAParams.class);
								LKAParam[] params = lkaParams.value();
								if (params != null && params.length > 0) {
									for (LKAParam param : params) {
										// ????????????????????????
										Class<?> type = param.type();
										if (!type.getName().equals("java.lang.Object")) { // ?????????????????????
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
															paramModel.setParamType(param.paramType());
															if("".equals(param.paramType())) {
																paramModel.setParamType(ParamType.QUERY);
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
													paramModel.setValue("<span style='color:red;'>????????????????????????</span>");
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
										// ????????????????????????
										Class<?> type = param.type();
										if (!type.getName().equals("java.lang.Object")) { // ?????????????????????
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
															paramModel.setParamType(param.paramType());
															if("".equals(param.paramType())) {
																paramModel.setParamType(ParamType.QUERY);
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
													paramModel.setValue("<span style='color:red;'>????????????????????????</span>");
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
								// ????????????????????????
								Class<?> type = param.type();
								if (!type.getName().equals("java.lang.Object")) { // ?????????????????????
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
													paramModel.setParamType(param.paramType());
													if("".equals(param.paramType())) {
														paramModel.setParamType(ParamType.QUERY);
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
											paramModel.setValue("<span style='color:red;'>????????????????????????</span>");
											request.add(paramModel);
										}
									}
								}
							}else {
								ApiImplicitParam param = method.getAnnotation(ApiImplicitParam.class);
								// ????????????????????????
								Class<?> type = param.type();
								if (!type.getName().equals("java.lang.Object")) { // ?????????????????????
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
													paramModel.setParamType(param.paramType());
													if("".equals(param.paramType())) {
														paramModel.setParamType(ParamType.QUERY);
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
											paramModel.setValue("<span style='color:red;'>????????????????????????</span>");
											request.add(paramModel);
										}
									}
								}
							}
						}else { //?????????????????????????????????
							// ????????????????????????
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
							        		type.equals(LinkedHashSet.class)) { //list??????
										isArray = true;
										// ???????????????????????????
					                    Type genericType = parameters[i].getParameterizedType();
					                    if (null == genericType) {
					                        continue;
					                    }
					                    if (genericType instanceof ParameterizedType) {
					                        ParameterizedType pt = (ParameterizedType) genericType;
					                        //??????????????????class????????????
					                        try {
												type = (Class<?>)pt.getActualTypeArguments()[0];
											} catch (Exception e) {
												//continue;
											}
					                    }
									}else if(type.isArray()) {//??????
										isArray = true;
										// ???????????????????????????
										type = type.getComponentType();
									}
									if(type.isAnnotationPresent(LKAModel.class) || type.isAnnotationPresent(ApiModel.class)){
										continue;
									}
									
									//??????????????????
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
						
						// ??????????????????
						if (method.isAnnotationPresent(LKAResposes.class)) {
							LKAResposes lkaResposes = method.getAnnotation(LKAResposes.class);
							LKARespose[] resposes = lkaResposes.value();
							if (resposes != null && resposes.length > 0) {
								for (LKARespose resp : resposes) {
									// ????????????????????????
									Class<?> type = resp.type();
									if (!type.getName().equals("java.lang.Object")) { // ?????????????????????
										ResposeModel resposeModel = analysisResModel(methodModel.getUrl(),type,resp.group());
										if (resposeModel != null) {
											resposeModel.setArray(resp.isArray());
											//###################??????######################
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
											//###################??????######################
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
											//###################??????######################
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
															//###################??????######################
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
															//###################??????######################
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
														//###################??????######################
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
													
													//###################??????######################
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
														//###################??????######################
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
														//###################??????######################
													}

													respose.add(resposeModel);
												} catch (Exception e) {
													ResposeModel resposeModel = new ResposeModel();
													resposeModel.setName(e.getClass().getSimpleName()+":"+e.getMessage());
													resposeModel.setValue("????????????????????????????????????");
													respose.add(resposeModel);
												}
											}
										}else {
											ResposeModel resposeModel = new ResposeModel();
											resposeModel.setDataType(resp.dataType().getSimpleName());
											resposeModel.setDescription(resp.description());
											resposeModel.setName(resp.value());
											resposeModel.setArray(resp.isArray());
											//###################??????######################
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
											//###################??????######################
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
											//###################??????######################
											resposeModel.setParentName(resp.parentName());
											resposeModel.setValue(resp.name());
											respose.add(resposeModel);
										}
									}
								}
							}
						} else if (method.isAnnotationPresent(LKARespose.class)) {
							LKARespose resp = method.getAnnotation(LKARespose.class);
							// ????????????????????????
							Class<?> type = resp.type();
							if (!type.getName().equals("java.lang.Object")) { // ?????????????????????
								ResposeModel resposeModel = analysisResModel(methodModel.getUrl(),type,resp.group());
								if (resposeModel != null) {
									resposeModel.setArray(resp.isArray());
									//###################??????######################
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
									//###################??????######################
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
									//###################??????######################
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
													//###################??????######################
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
													//###################??????######################
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
												//###################??????######################
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
											
											//###################??????######################
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
												//###################??????######################
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
												//###################??????######################
											}

											respose.add(resposeModel);
										} catch (Exception e) {
											ResposeModel resposeModel = new ResposeModel();
											resposeModel.setName(e.getClass().getSimpleName()+":"+e.getMessage());
											resposeModel.setValue("????????????????????????????????????");
											respose.add(resposeModel);
										}
									}
								}else {
									ResposeModel resposeModel = new ResposeModel();
									resposeModel.setDataType(resp.dataType().getSimpleName());
									resposeModel.setDescription(resp.description());
									resposeModel.setName(resp.value());
									resposeModel.setArray(resp.isArray());
									//###################??????######################
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
									//###################??????######################
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
									//###################??????######################
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
					//????????????
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
	 *	 ???????????????????????????????????????????????????????????????
	 * @return list ??????
	 */
	public List<Map<String,Object>> getMethodURL() {
		RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext
				.getBean(RequestMappingHandlerMapping.class);
		// ??????url??????????????????????????????
		Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
		List<Map<String,Object>> resultList = new ArrayList<Map<String, Object>>();
		if(map == null || map.entrySet() == null) {
			return resultList;
		}
		for (Map.Entry<RequestMappingInfo, HandlerMethod> mappingInfoHandlerMethodEntry : map.entrySet()) {
			Map<String, Object> resultMap = new LinkedHashMap<String, Object>();

			RequestMappingInfo requestMappingInfo = mappingInfoHandlerMethodEntry.getKey();
			HandlerMethod handlerMethod = mappingInfoHandlerMethodEntry.getValue();
			
			if(requestMappingInfo == null || 
					handlerMethod == null || 
					handlerMethod.getMethod() ==null || 
					handlerMethod.getMethod().getDeclaringClass() == null) {
				continue;
			}
			
			resultMap.put("className", handlerMethod.getMethod().getDeclaringClass().getName()); // ??????
			Class<?>[] interfaces = handlerMethod.getMethod().getDeclaringClass().getInterfaces();
			List<String> interfacesNames = new ArrayList<String>();
			if (interfaces != null) {
				for (Class<?> c : interfaces) {// ????????????????????????,????????????????????????
					interfacesNames.add(c.getName());
				}
			}
			resultMap.put("interfacesNames",interfacesNames);
			if(handlerMethod.getBeanType() != null) {
				Annotation[] parentAnnotations = handlerMethod.getBeanType().getAnnotations();
				if(parentAnnotations != null && parentAnnotations.length > 0) {
					for (Annotation annotation : parentAnnotations) {
						if (annotation instanceof RequestMapping) {
							RequestMapping requestMapping = (RequestMapping) annotation;
							if (null != requestMapping.value() && requestMapping.value().length > 0) {
								resultMap.put("classURL", requestMapping.value()[0]);// ???URL
							}
						}
					}
				}
			}
			resultMap.put("methodName", handlerMethod.getMethod().getName()); // ?????????
			Parameter[] parameters = handlerMethod.getMethod().getParameters();//????????????
			if(parameters != null && parameters.length > 0) {
				resultMap.put("params", parameters);
			}
			PatternsRequestCondition p = requestMappingInfo.getPatternsCondition();
			if(p !=null && p.getPatterns() != null) {
				for (String url : p.getPatterns()) {
					resultMap.put("methodURL", url);// ??????URL
				}
			}
			RequestMethodsRequestCondition methodsCondition = requestMappingInfo.getMethodsCondition();
			if(methodsCondition != null && methodsCondition.getMethods() != null) {
				for (RequestMethod requestMethod : methodsCondition.getMethods()) {
					if(requestMethod != null) {
						resultMap.put("requestType", requestMethod.toString());// ???????????????POST/PUT/GET/DELETE
					}
				}
			}
			resultList.add(resultMap);
		}
		return resultList;
	}
	
	
	/**
	 * 	?????????????????????LKAModel????????????
	 * @param url ??????
	 * @param typeCls ??????
	 * @param group ??????
	 * @return ParamModel ??????
	 * @throws Exception ??????
	 */
	public ParamModel analysisModel(String url,Class<?> typeCls,String group) throws Exception {
		reqNum++; //?????????????????????
		if(reqNum > 10) {
			reqNum = 0;
			return null;
		}
		if (!typeCls.isAnnotationPresent(LKAModel.class) && !typeCls.isAnnotationPresent(ApiModel.class)) {
			reqNum = 0;
			return null;
		}
		ParamModel pm = new ParamModel();
		// ??????model????????????
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
		
		// ????????????????????????
		Field[] fields = typeCls.getDeclaredFields();
		
		//??????????????????????????????
		Field[] declaredField;
		try {
			declaredField = getDeclaredField(typeCls.newInstance());
		} catch (Exception e) {
			declaredField = null;
		}
		Object[] arrays = null;
		//????????????
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
						type = getGenericType(type,field).getCls();
						if(type == null) type = field.getType();
						//??????????????????
						propertyModel.setDataType(type.getSimpleName());
						int flag = isObj(type);
			            if(flag == 3) {
			            	propertyModel = analysisProModel(url,type,"",1,null);
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
						//??????????????????
						Class<?> gType = getGenericType(pType, field).getCls();
						if(gType == null) gType = pType;
						PropertyModel propertyModel = new PropertyModel();
						if (ctype.getName() != "java.lang.Object" || isObj(gType) == 3) {
							if(ctype.getName() != "java.lang.Object") {
								propertyModel = analysisProModel(url,ctype,group,1,null);
							}else {
								propertyModel = analysisProModel(url,gType,group,1,null);
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
						Class<?> gType = getGenericType(pType, field).getCls();
						if(gType == null) gType = pType;
						PropertyModel propertyModel = new PropertyModel();
						if (ctype.getName() != "java.lang.Object" || isObj(gType) == 3) {
							if(ctype.getName() != "java.lang.Object") {
								propertyModel = analysisProModel(url,ctype,group,1,null);
							}else {
								propertyModel = analysisProModel(url,gType,group,1,null);
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
	 *	?????????????????????PropertyModel????????????
	 * @param url ??????
	 * @param typeCls ??????
	 * @param group ??????
	 * @param proType protype
	 * @param proCls ????????????
	 * @return PropertyModel ??????
	 * @throws Exception ??????
	 */
	public PropertyModel analysisProModel(String url,Class<?> typeCls,String group,Integer proType,Class<?>... proCls) throws Exception {
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
		// ??????model????????????
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
		
		// ????????????????????????
		Field[] fields = typeCls.getDeclaredFields();
		
		//??????????????????????????????
		Field[] declaredField;
		try {
			declaredField = getDeclaredField(typeCls.newInstance());
		} catch (Exception e) {
			declaredField = null;
		}
		Object[] arrays = null;
		//????????????
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
				if (field.isAnnotationPresent(LKAProperty.class) || field.isAnnotationPresent(ApiModelProperty.class)) {
					boolean bool2=false;
					String[] groups = null;
					Class<?> ctype = null;
					boolean paramIsArr = false;
					String paramValue = "";
					String paramDescription = "";
					String paramTestData = "";
					boolean bool = false;
					if(field.isAnnotationPresent(LKAProperty.class)) {
						LKAProperty param = field.getAnnotation(LKAProperty.class);
						if(param.hidden())continue;
						paramIsArr = param.isArray();
						ctype = param.type();
						groups = param.groups();
						paramValue = param.value();
						paramDescription = param.description();
						paramTestData = param.testData();
					}else {
						ApiModelProperty param = field.getAnnotation(ApiModelProperty.class);
						if(param.hidden())continue;
						paramIsArr = param.isArray();
						ctype = param.type();
						groups = param.groups();
						paramValue = param.value();
						paramDescription = param.description();
						paramTestData = param.testData();
					}
					if(group != null && !"".equals(group)) {
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
					String pValue = field.getName();
					Class<?> pType = field.getType();
					//??????????????????
					Class<?> gType = getGenericType(pType, field).getCls();
					PropertyModel propertyModel = new PropertyModel();
					if (ctype.getName() != "java.lang.Object" || isObj(gType) == 3) {
						if(ctype.getName() != "java.lang.Object") {
							//??????type???????????????
							propertyModel = analysisProModel(url,ctype,group,proType,null);
						}else {
							//????????????????????????
							if(gType == null || "Object".equals(gType.getSimpleName())) {
								//??????
								if(proCls != null) {
									if(proCls != null && proCls.length > 0 && proCls[0] != null) {
										gType = proCls[0];
										boolean isArr = false;
										if(isObj(gType) == 2 || isObj(gType) == 4) {
											try {
												gType = proCls[1];
												isArr = true;
												paramIsArr = isArr;
											} catch (Exception e) {
												gType = proCls[0];
											}
										}
										//????????????
										if(!isArr) {
											if(proCls.length>1) {
												Class<?> proCls2[] = new Class<?>[proCls.length-1];
												for(int i = 0;i<proCls.length-1;i++) {
													if(proCls[i+1] != null) {
														proCls2[i] = proCls[i+1];
													}else {
														break;
													}
												}
												proCls = proCls2;
											}
										}else {
											if(proCls.length>2) {
												Class<?> proCls2[] = new Class<?>[proCls.length-2];
												for(int i = 0;i<proCls.length-2;i++) {
													if(proCls[i+2] != null) {
														proCls2[i] = proCls[i+2];
													}else {
														break;
													}
												}
												proCls = proCls2;
											}
										}
									}
								}else {
									gType = pType;
								}
							}
							propertyModel = analysisProModel(url,gType,group,proType,proCls);
						}
						if (propertyModel == null) {
							propertyModel = new PropertyModel();
						}
						propertyModel.setArray(paramIsArr);
						if(!paramIsArr && (pType.getSimpleName().contains("[]") || 
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
						propertyModel.setName(paramValue);
						propertyModel.setDescription(paramDescription);
						propertyModel.setDataType("Object");
						propertyModels.add(propertyModel);
					} else {
						propertyModel.setDataType(pType.getSimpleName());
						propertyModel.setDescription(paramDescription);
						propertyModel.setName(paramValue);
						propertyModel.setTestData(paramTestData);
						String[] split = paramValue.split("\\^");
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
						propertyModel.setArray(paramIsArr);
						if(!paramIsArr && (pType.getSimpleName().contains("[]") || 
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
					if(sconAll) {
						Class<?> type = field.getType();
						PropertyModel propertyModel = new PropertyModel();
						int isArray = isObj(type);
						type = getGenericType(type, field).getCls();
						if(type == null) {
							type = field.getType();
						}
						//??????????????????
						propertyModel.setDataType(type.getSimpleName());
						int flag = isObj(type);
			            if(flag == 3) {
			            	propertyModel = analysisProModel(url,type,"",proType,null);
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
				}
			}
			modelModel.setPropertyModels(propertyModels);
		}
		pm.setModelModel(modelModel);
		proNum = 0;
		return pm;
	}

	/**
	 * 	?????????????????????ResposeModel????????????
	 * @param url ??????
	 * @param typeCls ??????
	 * @param group ??????
	 * @return ResposeModel ??????
	 * @throws Exception ??????
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
		
		// ??????model????????????
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
		
		// ????????????????????????
		Field[] fields = typeCls.getDeclaredFields();
		
		//??????????????????????????????
		Field[] declaredField;
		try {
			declaredField = getDeclaredField(typeCls.newInstance());
		} catch (Exception e) {
			declaredField = null;
		}
		Object[] arrays = null;
		//????????????
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
						type = getGenericType(type, field).getCls();
						if(type == null) type = field.getType();
						//??????????????????
						propertyModel.setDataType(type.getSimpleName());
						int flag = isObj(type);
			            if(flag == 3) {
			            	propertyModel = analysisProModel(url,type,"",2,null);
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
						//??????????????????
						Class<?> gType = getGenericType(pType, field).getCls();
						if(gType == null) gType = pType;
						PropertyModel propertyModel = new PropertyModel();
						if (ctype.getName() != "java.lang.Object" || isObj(gType) == 3) {
							if(ctype.getName() != "java.lang.Object") {
								propertyModel = analysisProModel(url,ctype,group,2,null);
							}else {
								propertyModel = analysisProModel(url,gType,group,2,null);
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
						//??????????????????
						Class<?> gType = getGenericType(pType, field).getCls();
						if(gType == null) gType = pType;
						PropertyModel propertyModel = new PropertyModel();
						if (ctype.getName() != "java.lang.Object" || isObj(gType) == 3) {
							if(ctype.getName() != "java.lang.Object") {
								propertyModel = analysisProModel(url,ctype,group,2,null);
							}else {
								propertyModel = analysisProModel(url,gType,group,2,null);
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
	 * 	????????????????????????
	 * @param value ?????????
	 * @param type ????????????(1.???????????? 2.????????????)
	 * @param url ????????????
	 * @param modaltype ????????????(1.?????? 2.?????? 3.?????? 4.other)
	 * @param content ????????????
	 * @return String ????????????
	 * @param serverName ???????????????
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
		//???????????????
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
			return "????????????";
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
		return "????????????";
	}
	
	/**
	 * 	???????????????????????? 
	 * @param value ?????????
	 * @param type type ????????????(1.???????????? 2.????????????)
	 * @param url ????????????
	 * @param serverName ???????????????
	 * @return String ????????????
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
            return "????????????";
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
		return "????????????";
	}
	
	/**
	 * 	???????????????????????? 
	 * @param serverName ???????????????
	 * @return map ??????
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
            Set<Map.Entry<Object, Object>> entrySet = prop.entrySet();//??????????????????????????????
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
	 * July_wonderful???????????????(??????????????????)
	 * @param object ??????
	 * @return Field ????????????
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
     * 	???????????????????????????
     * @param str ???????????????
     * @return String ???????????????
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
	            entrySet = prop.entrySet();//??????????????????????????????
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
    	
        //???????????????????????????
    	/*if(enToCn) { //??????????????????
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
     * 	??????????????????
     * @param type ??????
     * @return int ????????????
     */
    public int isObj(Class<?> type) {
    	if(type == null) return 3;
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
     * 	???????????????list???set??????????????????
     * @param type ??????
     * @param field ??????
     * @return Class ????????????
     */
    public TypeCls getGenericType(Class<?> type,Field field){
    	TypeCls typeCls = new TypeCls();
    	typeCls.setArray(false);
    	typeCls.setName(type.getSimpleName());
	    try {
			if(type.equals(List.class) || 
	        		type.equals(Set.class) || 
	        		type.equals(ArrayList.class) || 
	        		type.equals(LinkedList.class) ||
	        		type.equals(Vector.class) ||
	        		type.equals(SortedSet.class) ||
	        		type.equals(HashSet.class) ||
	        		type.equals(TreeSet.class) ||
	        		type.equals(LinkedHashSet.class)) { //??????
				typeCls.setArray(true);
				// ???????????????????????????
			    Type genericType = field.getGenericType();
			    if (null == genericType) {
			        type = Object.class;
			    }
			    if (genericType instanceof ParameterizedType) {
			    	ParameterizedType pt = (ParameterizedType) genericType;
			        try {
						//??????????????????class????????????
						type = (Class<?>)pt.getActualTypeArguments()[0];
					} catch (Exception e) {
						try {
							ParameterizedType pt2 =(ParameterizedType)pt.getActualTypeArguments()[0];
							type = (Class<?>)pt2.getRawType();
						} catch (Exception e1) {
							TypeVariable<?> pt3 =(TypeVariable<?>)pt.getActualTypeArguments()[0];
							String name = pt3.getName();
							if("T".equals(name)) {
								type = null;
							}
						}
					}
			    }
			}else if(type.isArray()) {//??????
				// ???????????????????????????
				type = type.getComponentType();
			}
		} catch (Exception e) {
		}
	    typeCls.setCls(type);
	    return typeCls;
    }
    
    public boolean isParentArray(Class<?> returnType) {
    	boolean isParentArray = false;
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
    	return isParentArray;
    }
}
