##  简介

&emsp;&emsp;Lkadoc是一款开源的接口文档自动生成工具，基于SpringBoot平台，拥有非常强大的接口文档管理功能。为解决Java后台开发人员编写接口文档、调试接口而生。同时提供了简洁、大气、功能丰富的接口文档UI操作界面，方便后端与前端之间的接口对接。

#### 愿景

&emsp;&emsp;我们愿成为java开发人员最好的基友，从手动编写接口文档的痛苦中解救出来，丢弃难用的Postman，工作效率从此翻倍，不再加班，有更多的时间陪伴家人。

## 特性

------

- **无侵入**：引入它不会对现有项目产生影响。悄然降临，寂静无声。

- **整合方便**：一个启动注解就可以完成所有配置。手到擒来，唾手可得。
- **牛逼的注解**：一个注解可描述多个参数，多层参数结构，甚至能做到接口零注解。纵横天地，心随我意。
- **狂拽的调试**：支持在线调试接口，同步、异步压力测试接口。丧心病狂，举世无双。
- **酷炫的界面**：文档界面简洁大气，能同时满足前端和后端开发人员的审美观。参数的展示方式可以在表格和JSON格式间自由切换。
- **强大的辅助**：支持文档聚合、文档下载、对象属性分组、新接口标记显示、动态修改参数状态(高亮、标记、说明)、全局绑定token等。

#### 展示

**自动生成的接口文档UI页面**

![新版风格](https://img-blog.csdnimg.cn/20210701142613279.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

**接口调试页面**

![](C:\Users\liukai\Desktop\lkadoc\教程图片\接口调试界面.png)

## 快速入门

#### 在SpringBoot项目中引入jar包

```xml
<!--Lkadoc包-->
<dependency>
	<groupId>com.github.liukaitydn</groupId>
	<artifactId>lkadoc-api</artifactId>
	<version>1.3.6</version>
</dependency>
```

#### 在SpringBoot项目启动类上加上@LKADocument注解

```java
@LKADocument
@SpringBootApplication
public class LKADemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(LKADemoApplication.class, args);
    }
}
```

#### 在application.yml文件中添加如下配置

```yaml
lkad:
#要扫描接口的包路径，多个用","号隔开，指定父包可以扫描所有父包下的子包【必须】
 basePackages: com.lkad.api
#项目名称【可选】
 projectName: Lkadoc测试项目
#项目描述【可选】
 description: 智能、便捷、高效
#要聚合的项目地址，"^"前面是项目名称（可省略），后面是项目的地址（也可以用域名），多个用","号隔开，用来聚合其它项目的接口信息，可以在UI界面切换【可选】
 serverNames: 其它项目1^192.168.0.77:8081/,其它项目2^192.168.0.77:8082/
#项目的版本号【可选】
 version: 1.0
#接口文档启动开关,true是开启，false是禁用,默认为开启【可选】
 enabled: true
#是否开启对未加注解描述的参数进行自动识别，默认为false【可选】
 sconAll: false
 #设置查看接口文档所需的密码，默认不需要密码【可选】
 password: ""
```

**或者在启动类注解@LKADocument上设置如下属性（和上面配置二选一即可,效果一模一样）**

```java
@LKADocument(basePackages="com.lkad.api",projectName="Lkadoc测试项目",description="智能、便捷、高效",version="1.0",serverNames="其它项目1^192.168.0.52:8081/,其它项目2^192.168.0.52:8082/")
@SpringBootApplication
public class LKADemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(LKADemoApplication.class,args);
    }
}
```

**准备测试代码**

```java
package com.lkad.api;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lk.api.annotation.*;

@LKAType(value="第一个测试类")
@RestController
@RequestMapping("lkadocument/demo")
public class LKADemoController {
 
    @LKAMethod(value="登录")
    @LKAParam(names= {"name","pwd"},values= {"用户名","密码"})
    @LKARespose(names= {"code","msg"},values= {"状态码","消息"})
    @PostMapping("login")
    public Map<String,Object> login(String name,String pwd) {
        Map<String,Object> map = new HashMap<>();
        map.put("code",200);
        map.put("msg","登录成功，欢迎"+name+"光临本系统");
        return map;
    }
}
```

**打开浏览器，输入地址http://127.0.0.1:8080/lkadoc.html 查看效果如下**

![](C:\Users\liukai\Desktop\lkadoc\教程图片\快速入门.png)

## 注解介绍

&emsp;&emsp;Lkadoc 是基于注解来自动生成接口文档的，注解功能非常强大，要想灵活的使用 Lkadoc，那么就必须掌握注解相关的知识。Lkadoc为 swagger 大部分注解做了兼容处理，只需修改引入的包路径为com.lk.api.*即可。

#### @LKADocument

```properties
#@LKADocument是加在SpringBoot启动类上的注解，必须加上，否则Lkadoc接口文档不可使用，用来描述项目信息，该注解下的属性可以代替application配置文件里面的lkad相关配置，非常方便
#常用属性：
basePackages:扫描接口的包路径，多个用","号隔开，指定父包路径可以扫描所有父包下的子包路径【必须】
projectName:项目名称【可选】
description:项目描述【可选】
serverNames:要聚合的项目地址，"^"前面是项目名称（可省略），后面是项目的地址（也可以用域名），多个用","号隔开，用来聚合其它项目的接口信息，可以在UI界面切换【可选】
version:项目的版本号,配合@LKAMethod注解的version属性可以快速定位新接口【可选】
enabled:接口文档启动开关,true是开启，false是禁用,默认为开启【可选】
sconAll: 是否开启对未加注解描述的参数进行自动识别，默认为false【可选】
password:设置查看接口文档所需的密码，默认不需要密码【可选】
```

#### @LKAType

```properties
#用来描述类的信息
#常用属性：
value:类的作用【必须】
description:类的描述【可选】
hidden:是否在UI界面隐藏该类的信息，默认为false，如果设置为ture那么该类下在的所有接口也会隐藏【可选】
order:排序，值越少越靠前【可选】
```

#### @LKAMethod

```properties
#用来描述接口的信息
#常用属性：
value:接口的作用【必须】
description:接口的描述【可选】
contentType:请求头ContentType类型，默认为application/x-www-form-urlencoded【可选】
author:作者【可选】
createTime:接口创建时间【可选】
updateTime:接口修改时间【可选】
hidden:是否在UI界面隐藏该接口，默认为false【可选】
version:接口版本号，如果项目版本号相同，在UI界面会标记为新接口【可选】
download:是否是下载的方法，如果该接口涉及到下载文件必须设置成true，默认是false【可选】
token:是否需要token验证，只标识该接口需要token验证，不会影响正常业务，默认是true【可选】
order:排序，数字越少越靠前【可选】
directory:指定上级目录（这里目录是指@LKAType的value属性值，上级目录必须存在，不然不会展示在接口文档，默认当前类的目录）【可选】
```

#### @LKAParam / @LKAParams

```properties
#用来描述请求参数的信息
#常用属性：带s复数属性代表可以设置多个参数,但要注意参数顺序。带s和不带s设置时只能二选一
name/names:参数名称【必须】（用name设置参数名称时必须;用names设置参数名称时可省略，但JDK版本要1.8以上，编译的时候还要加上–parameters参数，这样Lkadoc可自动获取到参数名称,否则必须）
value/values:参数作用【必须】
description/descriptions:参数的描述【可选】
dataType/dataTypes:数据类型【可选】（用dataType配置时默认值String.class;用dataTypes配置时可自动获取参数的数据类型，可省略不配置，但要注意参数的顺序。）
#例如:
#单个参数配置:
#@LKAParam(name="name",value="用户名",dataType=String.class)//这里可省略，因为默认是String
#多个参数配置:
#@LKAParam(...,dataTypes={String.class,String.class,Integer.class},...) //这里如果和接口入参顺序和数量一致的话，也可以省略，可自动获取
    #或者
#@LKAParams({
    #@LKAParam(name="name",value="用户名",dataType=String.class),
    #@LKAParam(name="pwd",value="密码",dataType=String.class),
    #@LKAParam(name="age",value="年龄",dataType=Integer.class)
#})
required/requireds:是否必传，默认为true【可选】(更简便的用法是在参数名后加"-n"代表非必传或者在入参value属性值前面加“n~”代表非必传参数)
#例如:
#@LKAParam(name="name",value="用户名",required=false)
#或者
#@LKAParam(name="name-n",value="用户名") //参数名称后面加"-n"代表非必传
#或者
#@LKAParam(name="name",value="n~用户名") //value属性值前面加“n~”代表非必传参数
paramType/paramTypes:参数位置，query、header、path三选一【可选】（用paramType配置时默认为query;用paramTypes配置时Lkadoc可根据参数注解@PathVariable、@RequestHeader自动获取参数位置，可省略不配）
isArray/isArrays:是否是集合或数组，默认false【可选】
testData/testDatas:测试数据【可选】(更简便的用法是在value后面的"^"符号后面加上测试数据)
#例如:
#@LKAParam(name="name",value="用户名",required=false,testData="张三")
#或者
#@LKAParam(name="name-n",value="用户名^张三") //在value后面的"^"符号后面加上测试数据
type:入参对象类型【可选】（当接口请求参数是一个对象时使用，但一般不需要设置，可自动识别）
group:和type配合使用，对象参数分组，可过滤没必要的参数【可选】
```

#### @LKAModel

```properties
#用来描述实体类的信息
#常用属性：
value:属性的作用【可选】
description:属性的描述【可选】
```

#### @LKAProperty

```properties
#用来描述实体类的属性的信息
#常用属性:
value:属性的作用【必须】
description:属性的描述【可选】
hidden:是否在UI界面隐藏该属性，默认为false【可选】
testData:测试数据【可选】（更简便的用法是在value后面的"^"符号后面加上测试数据）
required:是否必传，默认为true【可选】（更简便的用法是在value前面加"n~"代表非必传）
isArray:是否是数组或集合，不设置也可自动识别【可选】
type:当属性为对象类型时，可以用type来指定，不设置也可自动识别【可选】
groups:用来进行参数分组设置，可设置多个组名【可选】（required在分组时用法是在groups属性里面的组名后面加"-n"代表不是必传，不加默认是必传）
```

#### @LKAGroup

```properties
#@LKAGroup是一个入参注解，作用是指定对象是哪组参数用来作为入参
```

#### @LKARespose/@LKAResposes

```properties
#用来描述响应参数的信息
#常用属性
name/names:参数名称，和type参数二选一【必须】
#例如:
#单个参数配置:
#@LKARespose(name="code",...)
#多个参数配置:
#@LKARespose(names={"code","msg","data"},...)
    #或者
#@LKAResposes({
    #@LKARespose(name="code",...),
    #@LKARespose(name="msg",...),
    #@LKARespose(name="data",...)
#})
value/values:参数作用【必须】
description/descriptions:参数的描述【必须】
dataType/dataTypes:参数数据类型，当使用dataTypes不设置也可以自动识别【可选】
isArray/isArrays:是否是集合或数组，默认false【可选】
type:出参对象类型，和name/names参数二选一，可自动识别【可选】
group:和type配合使用，对象参数分组，可过滤没必要的参数【可选】
#父参数
parentName:父参名称【可选】
parentValue:父参作用【可选】
parentDescription:父参描述【可选】
parentIsArray:父参是否是数组或集合【可选】
#爷参数
grandpaName:爷参名称【可选】
grandpaValue:爷参作用【可选】
grandpaDescription:爷参描述【可选】
grandpaIsArray:爷参是否是数组或集合【可选】
```


项目开源地址：https://gitee.com/liuk168/lkadoc

**如果大家学得好用，记得给星哦**