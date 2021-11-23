package com.lk.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Import;
import com.lk.api.controller.ExportMarkDownController;
import com.lk.api.controller.ExportPDFController;
import com.lk.api.controller.LKADController;

/**
 * 	用在SpringBoot项目的启动类上的注解
 * 	作用：开启全局接口文档注解扫描
 * @author liukai
 */
@Documented
@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Import({LKADController.class,ExportMarkDownController.class,ExportPDFController.class})
@ServletComponentScan
public @interface LKADocument {
	/**基础包路径，多个用英文逗号隔开*/
	String basePackages() default "";
	/**项目名称*/
	String projectName() default "lkadoc接口文档";
	/**项目描述*/
	String description() default "智能、便捷、高效！";
	/**远程服务器地址，
	 * "^"前面是项目名称（可省略），后面是项目的地址（也可以用域名），多个用","号隔开，用来聚合其它项目的接口信息，可以在UI界面切换【可选】
	 #例如:serverNames="物业项目^192.168.0.52:8081,租房系统^192.168.0.77:8001"
	 * */
	String serverNames() default "";
	/**项目版本号*/
	String version() default "";
	/**接口文档开关*/
	boolean enabled() default true;
	/**是否开启全局参数扫描*/
	boolean sconAll() default false;
	/**接口文档查看密码*/
	String password() default "";
	/**最大类扫描数，防止内存溢出*/
	String classNum() default "5000";
}
