##  简介

&emsp;&emsp;Lkadoc是一款开源的接口文档自动生成工具，基于SpringBoot平台，拥有非常强大的接口文档管理功能。为解决Java后台开发人员编写接口文档、调试接口而生。同时提供了简洁、大气、功能丰富的接口文档UI操作界面，方便后端与前端之间的接口对接。

#### 愿景

&emsp;&emsp;我们愿成为java开发人员最好的基友，从手动编写接口文档的痛苦中解救出来，丢弃难用的Postman，工作效率从此翻倍，不再加班，有更多的时间陪伴家人。

## 特性

------

- **无侵入**：引入它不会对现有项目产生影响。悄然降临，寂静无声。

- **整合方便**：一个启动注解就可以完成所有配置。手到擒来，唾手可得。
- **牛逼的注解**：一个注解可描述多个参数，多层参数结构，甚至能做到接口零注解。登峰造极，纵横天地。
- **狂拽的调试**：支持在线调试接口，同步、异步压力测试接口。丧心病狂，举世无双。
- **酷炫的界面**：文档界面简洁大气，能同时满足前端和后端开发人员的审美观。参数的展示方式可以在表格和JSON格式间自由切换。 如你所愿，心随我意。
- **强大的辅助**：支持文档聚合、文档下载、对象属性分组、新接口标记显示、动态修改参数状态(高亮、标记、说明)、全局绑定token、密码验证等强大的辅助功能加持。锦上添花，如虎添翼。

#### 展示

**自动生成的接口文档UI页面**

![新版风格](https://img-blog.csdnimg.cn/20210701142613279.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

**接口调试页面**

![](C:\Users\liukai\Desktop\lkadoc\教程图片\接口调试界面.png)

## 快速入门

#### 用IDEA创建一个SpringBoot项目，项目名叫LkadocDemo，点击Next

![](C:\Users\liukai\Desktop\lkadoc\教程图片\1.png)

#### 勾选Spring web组件，点击Finish

![](C:\Users\liukai\Desktop\lkadoc\教程图片\2.png)

#### 在LkadocDemo项目的pom.xml文件中引入lkadoc的依赖

```xml
<!--Lkadoc包-->
<dependency>
	<groupId>com.github.liukaitydn</groupId>
	<artifactId>lkadoc-api</artifactId>
	<version>1.3.7</version>
</dependency>
```

#### 在LkadocDemo项目启动类LkadocDemoApplication上加上@LKADocument注解

```java
@LKADocument(basePackages="com.lkad.api")
@SpringBootApplication
public class LkadocDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(LkadocDemoApplication.class, args);
    }
}
```

#### 在com.lkad.api包下面准备一个用户登录注册模块类-LKADemoController

```java
@LKAType(value="用户登录注册模块")
@RestController
@RequestMapping("user")
public class LKADemoController {
 
    @LKAMethod(value="登录")
    @LKAParam(names= {"name","pwd"},values= {"用户名","密码"})
    @LKARespose(names= {"code","msg"},values= {"状态码","消息"})
    @PostMapping("login")
    public Map<String,Object> login(String name, String pwd) {
        Map<String,Object> map = new HashMap<>();
        map.put("code",200);
        map.put("msg","登录成功，欢迎"+name+"光临本系统");
        return map;
    }
}
```

#### 启动项目，打开浏览器，输入地址http://127.0.0.1:8080/lkadoc.html 

![](C:\Users\liukai\Desktop\lkadoc\教程图片\3.png)

**如果能看到如上界面，那么就恭喜客官，Lkadoc能够为您服务了**

## 注解介绍

&emsp;&emsp;Lkadoc 是基于注解来自动生成接口文档的，注解功能非常强大，要想灵活的使用 Lkadoc，那么就必须掌握注解相关的知识。Lkadoc为 swagger 大部分注解做了兼容处理，只需修改引入的包路径为com.lk.api.*即可。

#### @LKADocument

```properties
#@LKADocument是加在SpringBoot启动类上的注解，必须加上，否则Lkadoc接口文档不可使用，用来描述项目信息，该注解下的属性可以代替application配置文件里面的lkad相关配置，非常方便，常用属性：

basePackages:扫描接口的包路径，多个用","号隔开，指定父包路径可以扫描所有父包下的子包路径【必须】
#例如:basePackages="com.lkad.api,com.lkad.admin"

projectName:项目名称【可选】
#例如:projectName="Lkadoc测试项目"

description:项目描述【可选】
#例如:description="该项目用来教学Lkadoc的使用"

serverNames:要聚合的项目地址，"^"前面是项目名称（可省略），后面是项目的地址（也可以用域名），多个用","号隔开，用来聚合其它项目的接口信息，可以在UI界面切换【可选】
#例如:serverNames="物业项目^192.168.0.52:8081,租房系统^192.168.0.77:8001"

version:项目的版本号,配合@LKAMethod注解的version属性可以快速定位新接口【可选】
#例如:version="1.0"

enabled:接口文档启动开关,true是开启，false是禁用,默认为开启【可选】
#例如:enabled=true

sconAll:是否开启对未加注解描述的参数进行自动识别，默认为false【可选】
#例如:sconAll=false

password:设置查看接口文档所需的密码，默认不需要密码【可选】
#例如:password="123456"
```

**注意：@LKADocument注解相关属性也可以在application.properties文件中通过lkad.basePackages="x.x.x"的方式配置，但@LKADocument这个注解不能省略。如果application.properties文件中配置了lkad属性，那么@LKADocument注解中的属性会全部失效，意思就是说两种方式只能二选一**

#### @LKAType

```properties
#用来描述类的信息，常用属性：

value:类的作用【必须】
#例如:value="测试类"

description:类的描述【可选】
#例如:description="该类只是用来测试"

hidden:是否在UI界面隐藏该类的信息，默认为false，如果设置为ture那么该类下在的所有接口也会隐藏【可选】
#例如:hidden=false

order:排序，值越少越靠前【可选】
#例如:order=1
```

#### @LKAMethod

```properties
#用来描述接口的信息，常用属性：

value:接口的作用【必须】
#例如:value="用户登录"

description:接口的描述【可选】
#例如:description="用于APP端登录的接口"

contentType:请求头ContentType类型，默认为application/x-www-form-urlencoded【可选】
#例如:contentType="application/json"

author:作者【可选】
#例如:author="L.K"

createTime:接口创建时间【可选】
#例如:createTime="2021-08-08"

updateTime:接口修改时间【可选】
#例如:updateTime="2021-10-01"

hidden:是否在UI界面隐藏该接口，默认为false【可选】
#例如:hidden=false

version:接口版本号，如果项目版本号相同，在UI界面会标记为新接口【可选】
#例如:version="1.0"

download:是否是下载的方法，如果该接口涉及到下载文件必须设置成true，默认是false【可选】
#例如:download=false

token:是否需要token验证，只标识该接口需要token验证，不会影响正常业务，默认是true【可选】
#例如:token=true

order:排序，数字越少越靠前【可选】
#例如:order=1

directory:指定上级目录（这里目录是指@LKAType的value属性值，上级目录必须存在，不然不会展示在接口文档，默认当前类的目录）【可选】
#例如:directory="用户管理"
```

#### @LKAParam / @LKAParams

```properties
#用来描述请求参数的信息，带s复数属性代表可以设置多个参数,但要注意参数顺序。带s和不带s设置时只能二选一，建议大家不管是多个参数还是单个参数，都用带s复数属性，带s复数属性要更灵活，更智能。常用属性：

name/names:参数名称【必须】（用name设置参数名称时必须;用names设置参数名称时可省略，但JDK版本要1.8以上，编译的时候还要加上–parameters启动参数，这样Lkadoc可自动获取到参数名称,目前测试JDK11不加–parameters参数也可以识别参数名称，否则必须）
#例如:
#单个参数配置:name="name"
#多个参数配置:names={"name","pwd","age"} //这里如果和接口入参顺序一样，可省略不用配置
#或者，
#@LKAParams({
    #@LKAParam(name="name",...),
    #@LKAParam(name="pwd",...),
    #@LKAParam(name="age",...)
#})
#注意，@LKAParams用法的其它属性才name都一样，后面不再举例。

value/values:参数作用【必须】
#例如:
#单个参数配置:value="姓名"
#多个参数配置:values={"姓名","密码","年龄"}

description/descriptions:参数的描述【可选】
#例如:
#单个参数配置:description="姓名不能超过5个汉字"
#多个参数配置:descriptions={"姓名不能超过5个汉字","密码不能少于6位","年龄在18岁和60岁之间"}

dataType/dataTypes:数据类型【可选】（用dataType配置时默认值String.class;用dataTypes配置时可自动获取参数的数据类型，可省略不配置，但要注意参数的顺序。）
#例如:
#单个参数配置:dataType=String.class //这里可省略，因为默认是String
#多个参数配置:dataTypes={String.class,Date.class,Integer.class} //如果和接口入参顺序一致可省略自动获取

required/requireds:是否必传，默认为true【可选】(更简便的用法是在name属性值后面加"-n"或者在value属性值前面加“n~”代表非必传参数)
#例如:
#单个参数配置:required=false 或者 name="name-n" 或者 value="n~用户名" //三种方式都代表非必传（3选1）
#多个参数配置:#requireds={false,true,false} 
#或者 names={"name-n","pwd","age-n"} 
#或者 values={"n~用户名","密码","n~年龄"} //三种方式效果都一样，用户名和年龄非必传，密码必传（3选1）

paramType/paramTypes:参数位置，query、header、path三选一【可选】（用paramType配置时默认为query;用paramTypes配置时Lkadoc可根据参数注解@PathVariable、@RequestHeader自动获取参数位置，可省略不配）
#例如:
#单个参数配置:paramType="query" //默认是query,可以省略不配置
#多个参数配置:paramTypes={"query","header","path"} //参数如果加上了@PathVariable、@RequestHeader注解，可自动获取参数位置，可省略不配置

isArray/isArrays:该参数是否是集合或数组，默认false【可选】
#例如:
#单个参数配置:isArray=false
#多个参数配置:isArrays={false,true,false}

testData/testDatas:测试数据【可选】(更简便的用法是在value后面的"^"符号后面加上测试数据)
#例如:
#单个参数配置:testData="张三" 或者 value="姓名^张三" //两种方式2选1
#多个参数配置:testDatas={"张三","123456","22"} 
#或者 values={"n~姓名^张三","密码^123456","n~年龄^22"} //两种方式2选1 

type:入参对象类型【可选】（当接口请求参数是一个对象时使用，但一般不需要设置，可自动识别）
#例如:type=User.class  //一般不用配置，可自动识别

group:和type配合使用，对象参数分组，可过滤没必要的参数【可选】
#例如:group="add"  //add是一个组名，事先在User对象的参数上面配置好的
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
#用来描述实体类的属性的信息，和@LKAParam注解属性用法差不多，这里不再举例了
#常用属性:
value:属性的作用【必须】
description:属性的描述【可选】
hidden:是否在UI界面隐藏该属性，默认为false【可选】
testData:测试数据【可选】（更简便的用法是在value后面的"^"符号后面加上测试数据）
required:是否必传，默认为true【可选】（更简便的用法是在value前面加"n~"代表非必传）
isArray:是否是数组或集合，不设置也可自动识别【可选】
type:当属性为对象类型时，可以用type来指定，不设置也可自动识别【可选】
groups:用来进行参数分组设置，可设置多个组名【可选】（required在分组时用法是在groups属性里面的组名后面加"-n"代表不是必传，不加默认是必传）
#例如: groups={"add","update-n","info"}
```

#### @LKAGroup

```properties
#@LKAGroup是一个入参注解，作用是指定对象是哪组参数用来作为入参
```

#### @LKARespose/@LKAResposes

```properties
#用来描述响应参数的信息,和@LKAParam注解属性用法差不多，这里不再举例了
#常用属性
name/names:参数名称，和type参数二选一【必须】
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

## 基础应用

&emsp;&emsp;在基础应用中，我会用大量的案例来演示各种场景Lkadoc的使用方法和技巧，能够让大家在案例中快速掌握Lkadoc的基本使用。同时你也能从中体会到Lkadoc的便利与强大。

#### 简单的请求入参示例

**以快速入门的案例为基础，修改@LKADocument注解如下**

```java
//增加项目名称和和项目说明
@LKADocument(basePackages="com.lkad.api",projectName = "演示项目",description = "用于Lkadoc教学项目")
@SpringBootApplication
public class LkadocDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(LkadocDemoApplication.class, args);
    }
}
```

**在LKADemoController类下面我们再增加一个注册的方法**

```java
//注意:JDK8及以上@LKAParam注解的names={"name","pwd","email","age"}配置可省略
@LKAMethod(value="用户注册",description="APP的注册接口",version="1.0",createTime="2021-08-08",author="LK")
@LKAParam(names={"name","pwd","email","age"},values= {"用户名^张凡","密码^123abc","n~邮箱^123@qq.com","n~年龄^21"})
@PostMapping("reg")
public String reg(String name,String pwd,String email,Integer age) {
    if(name == null || "".equals(name) || pwd == null || "".equals(pwd)){
        return "注册失败";
    }
    return "注册成功";
}
```

**启动项目，打开浏览器，输入地址http://127.0.0.1:8080/lkadoc.html ，界面如下：**

![](C:\Users\liukai\Desktop\lkadoc\教程图片\4.png)

#### path和header请求入参示例

**再增加一个path和header入参的测试方法，Lkadoc能够自动识别@RequestHeader、@PathVariable注解**

```java
@LKAMethod(value="path和header入参")
@LKAParam(names={"token","name","email"},values= {"令牌^aaaa","n~姓名^瓜瓜","n~邮箱^123@qq.com"})
@PostMapping("getUser/{name}/{email}")
public String getUser(@RequestHeader("token") String token,
                      @PathVariable("name") String name,
                      @PathVariable("email") String email) {

    return "测试结果：token="+token+",name="+name+",email="+email;
}
```

**重启项目，刷新Lkadoc界面如下:**

![](C:\Users\liukai\Desktop\lkadoc\教程图片\5.png)

#### 数组请求入参示例

**再增加一个数组入参的测试方法**

```java
//isArrays = {true,false}代表第一个参数是数组，第二参数不是数组
@LKAMethod(value="数组入参")
@LKAParam(names={"ids","name"},values= {"用户ID^1001","n~姓名^瓜瓜"},isArrays = {true,false})
@PostMapping("array")
public String array(Integer[] ids,String name) {

    return "测试结果：ids="+ Arrays.toString(ids)+",name="+name;
}
```

**重启项目，刷新Lkadoc界面如下:**

![](C:\Users\liukai\Desktop\lkadoc\教程图片\6.png)

#### 文件上传请求入参示例

**再增加一个文件上传测试方法**

```java
//ContentType.FORMDATA常量的值为multipart/form-data
@LKAMethod(value="文件批量上传",contentType=ContentType.FORMDATA)
@LKAParam(names= "files",values="上传文件",isArrays= true)
@PostMapping("fileUpload")
public String fileUpload(MultipartFile[] files) {
    String fileNames = "";
    if(files != null) {
        for (MultipartFile f : files) {
            if("".equals(fileNames)) {
                fileNames = f.getOriginalFilename();
            }else {
                fileNames = fileNames + ","+f.getOriginalFilename();
            }
        }
    }

    return "上传成功，文件名："+fileNames;
}
```

**重启项目，刷新Lkadoc界面如下:**

![](C:\Users\liukai\Desktop\lkadoc\教程图片\7.png)

#### 文件下载示例

**再增加一个文件下载的测试方法**

```java
//@LKAMethod注解的download=true代表这个方法是一个文件下载的方法，测试这个方法事先要在D盘准备一个test.txt文件。至于文件下载的业务逻辑，这里不做讲解。
@LKAMethod(value="文件下载",download=true)
@PostMapping("fileDownload")
public void fileDownload(HttpServletResponse response) throws Exception {
    String path = "D:\\test.txt";
    File file = new File(path);
    String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1).toUpperCase();
    InputStream fis = new BufferedInputStream(new FileInputStream(path));
    byte[] buffer = new byte[fis.available()];
    fis.read(buffer);
    fis.close();
    response.reset();
    response.addHeader("Content-Disposition", "attachment;filename=" + new String(file.getName().getBytes()));
    response.addHeader("Content-Length", "" + file.length());
    OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
    response.setContentType("application/octet-stream");
    toClient.write(buffer);
    toClient.flush();
    toClient.close();
}
```

**重启项目，刷新Lkadoc界面如下:**

![](C:\Users\liukai\Desktop\lkadoc\教程图片\8.png)

#### 简单的对象请求入参示例

&emsp;&emsp;当我们入参是一个对象时，如果该对象上有@LKAModel注解，并且它的属性上有@LKAProperty注解，那么Lkadoc会去自动扫描这个对象信息，我们无需在接口上加额外的注解去描述对象参数。这样如果我们用对象去操作入参的话，可以大大减少接口上的注解数量，显得更加简洁。

**在com.lkad.model包下面准备一个角色对象**

```java
@LKAModel
public class Role {
    @LKAProperty(value = "角色id^101")
    private Integer id;
    @LKAProperty(value = "n~角色^名称")
    private String name;

    public Integer getId() {return id;}
    public void setId(Integer id) {this.id = id;}
    public String getName() {return name;}
    public void setName(String name) { this.name = name; }
}
```

**增加一个简单的对象请求入参的测试方法**

```java
@LKAMethod("基本对象入参")
@GetMapping("getRole")
public Role getRole(Role role) {
    return role;
}
```

**重启项目，刷新Lkadoc界面如下:**

![](C:\Users\liukai\Desktop\lkadoc\教程图片\9.png)

#### 复杂的对象请示入参示例

**在com.lkad.model包下再增加两个对象address和User，加上之前Role一共有3个对象**

```java
@LKAModel
public class Address {
    @LKAProperty(value="地址ID^1")
    private Integer id;
    @LKAProperty(value="n~地址信息^深圳市龙华区")
    private String info;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getInfo() { return info; }
    public void setInfo(String info) { this.info = info; }
}
```

```java
@LKAModel
public class User {
    @LKAProperty(value="用户ID^1001")
    private Integer id;
    @LKAProperty(value="n~用户名称^张三")
    private String name;
    @LKAProperty(value="n~年龄^20",description="范围0-120")
    private String age;
    @LKAProperty(value="角色对象")
    private Role role;
    @LKAProperty(value="用户爱好^运动")
    private String[] likes;
    @LKAProperty(value="地址信息")
    private List<Address> addresses;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAge() { return age; }
    public void setAge(String age) { this.age = age; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public String[] getLikes() { return likes; }
    public void setLikes(String[] likes) { this.likes = likes; }
    public List<Address> getAddresses() { return addresses; }
    public void setAddresses(List<Address> addresses) { this.addresses = addresses; }
}
```

**增加一个复杂的对象请求入参的测试方法**

```java
/**
1.复杂的对象需把@LKAMethod注解的contentType属性设置为"application/json"
2.如果contentType="application/json"，需在接收对象参数前面加@RequestBody注解
3.如果contentType="application/json"，那么接口的请求类型不能是get
*/
@LKAMethod(value="复杂的对象传参",contentType=ContentType.JSON)
@PostMapping("addUser")
public User addUser(@RequestBody User user) {
    return user;
}
```

**重启项目，刷新Lkadoc界面如下:**

![](C:\Users\liukai\Desktop\lkadoc\教程图片\10.png)

**通过点击"树状展示请求参数"按钮，可以很直观的显示复杂的入参结构**

![](C:\Users\liukai\Desktop\lkadoc\教程图片\11.png)

#### 对象参数分组示例

&emsp;&emsp;我们感受到了用对象接收请求参数更具便利性，那怎么去过滤对象属性呢？例如：有一个查询接口，只用到user对象的name、age参数和addresses对象的info参数，但对于前端的友好度，我们不需要展示所有对象属性到Lkadoc界面，这时我们就可以用对象参数分组来实现。

- 分组可以用@LKAProperty注解的groups属性来设置组名
- 一个属性可以属于多个组，组名不能重复，多个组名用","号隔开
- 组名没有任何限制，只要不是空白的字符串即可
- 如果用到嵌套对象里面属性，嵌套对象名称和对应属性上都要设置相同的组名
- 入参对象需要用@LKAGroup注解来指定对象是哪组参数用来作为入参
  

```java
//@LKAProperty注解的groups属性可以用来实现参数分组，多个分组可以用英文","隔开。组名"getUser-n"，这个"-n"代表这个参数在这组数据里面是非必传参数
@LKAModel
public class User {
    @LKAProperty(value="用户ID^1001")
    private Integer id;
    @LKAProperty(value="n~用户名称^张三",groups = {"getUser","getUser2"})
    private String name;
    @LKAProperty(value="n~年龄^20",description="范围0-120",groups = {"getUser-n"})
    private String age;
    @LKAProperty(value="角色对象")
    private Role role;
    @LKAProperty(value="用户爱好^运动")
    private String[] likes;
    @LKAProperty(value="地址信息",groups = {"getUser"})//这里的groups = {"getUser"}不能省略
    private List<Address> addresses; 
	...get和set方法（略）...
}
```

```java
@LKAModel
public class Address {
    @LKAProperty(value="地址ID^1")
    private Integer id;
    @LKAProperty(value="n~地址信息^深圳市龙华区",groups= {"getUser-n"})
    private String info;
    .......get和set方法（略）.......
}
```

**新增一个对象参数分组的测试方法**

```java
//在入参对象User前面加一个注解@LKAGroup("getUser")，代表在Lkadoc只需展示组名为getUser的数据
@LKAMethod(value="对象参数分组",contentType=ContentType.JSON)
@PostMapping("getUserArray")
public User getUserArray(@RequestBody @LKAGroup("getUser") User user) {
    return user;
}
```

**重启项目，刷新Lkadoc界面如下:**

![](C:\Users\liukai\Desktop\lkadoc\教程图片\12.png)

#### 简单的响应参数示例

**在com.lkad.api包下面准备一个用户业务模块类-LKADemoController2，并准备一个简单的响应出参测试方法**

```java
@LKAType(value="用户业务模块")
@RestController
@RequestMapping("business")
public class LKADemoController2 {

    @LKAMethod("简单的响应出参")
    @LKARespose(names= {"code","msg","data"},values= {"状态码","消息","数据"})
    @GetMapping("getInfo")
    public Map<String,Object> getInfo() {
        Map<String,Object> map = new HashMap<>();
        map.put("code",200);
        map.put("msg","获取信息成功");
        map.put("data","响应数据");
        return map;
    }
}
```

**重启项目，刷新Lkadoc界面如下:**

![](C:\Users\liukai\Desktop\lkadoc\教程图片\13.png)

#### 对象响应参数示例

**在LKADemoController2类下面再增加一个对象出参的测试方法**

```java
//不管是入参还是出参如果是一个对象，且对象有加@LKAModel及属性有加@LKAProperty注解，那么Lkadoc会自动扫描该出参对象
@LKAMethod(value="对象出参",contentType=ContentType.JSON)
@PostMapping("getUser")
public User getUser(@RequestBody User user) {
    return user;
}
```

**重启项目，刷新Lkadoc界面响应参数展示结果如下:**

![](C:\Users\liukai\Desktop\lkadoc\教程图片\14.png)

#### 复杂的Map结构响应参数示例

**在LKADemoController2类下面再增加一个复杂的Map结构响应参数的测试方法**

```java
@LKAMethod(value="复杂的Map结构响应参数")
@LKAResposes({
    @LKARespose(names= {"code","msg"},values= {"状态码","消息"}),
    @LKARespose(name="total",value="总记录数",parentName="result",parentValue="响应数据"),
    @LKARespose(type=User.class,parentName="users",parentIsArray=true,parentValue="用户对象列表",grandpaName="result")
})
@GetMapping("getMap")
public Map<String,Object> getMap() {
    Map<String,Object> map = new HashMap<>();
    map.put("code",200);
    map.put("msg","操作成功！");
    Map<String,Object> data = new HashMap<>();
    data.put("total",10);
    List<User> users = new ArrayList<>();
    User user1 = new User();
    user1.setName("张三");
    User user2 = new User();
    user2.setName("李四");
    users.add(user1);
    users.add(user2);
    data.put("users",users);
    map.put("result",data);
    return map;
}
```

**重启项目，刷新Lkadoc界面响应参数展示结果如下:**

![](C:\Users\liukai\Desktop\lkadoc\教程图片\15.png)

**通过点击"树状展示响应参数"按钮，可以很直观的显示复杂的响应参数结构**

![](C:\Users\liukai\Desktop\lkadoc\教程图片\16.png)

#### 超过3层嵌套结构响应参数用法示例

&emsp;&emsp;通过parentXXX和grandPaXXX相关属性用一条@LKARespose注解只能一次性描述1到3级节点，如果有5级或者10级节点该怎么办？ 也有解决办法，非常简单，但注解可能会比较多，例如有这么一个结构{a:{b:{c:{d:1}}}},我们可以这么做:
@LKARespose(name=“a”,value=“一级”),
@LKARespose(name=“b”,value=“二级”,parentName=“a”),
@LKARespose(name=“c”,value=“三级”,parentName=“b”),
@LKARespose(name=“d”,value=“四级”,parentName=“c”)

**在LKADemoController2类下面再增加一个超过3层嵌套结构用法技巧的测试方法**

```java
@LKAMethod(value="超过3层嵌套结构用法技巧")
@LKAResposes({
    @LKARespose(name="a",value="一级"),
    @LKARespose(name="b",value="二级",parentName="a"),
    @LKARespose(name="c",value="三级",parentName="b"),
    @LKARespose(name="d",value="四级",parentName="c")
})
@GetMapping("getMoreMap")
public Map<String,Object> getMoreMap(){
    Map<String,Object> mapa= new HashMap<>();
    Map<String,Object> mapb= new HashMap<>();
    Map<String,Object> mapc= new HashMap<>();
    Map<String,Object> mapd= new HashMap<>();
    mapa.put("a",mapb);
    mapb.put("b",mapc);
    mapc.put("c",mapd);
    mapd.put("d",1);
    return mapa;
}
```

**重启项目，刷新Lkadoc界面响应参数展示结果如下:**

![](C:\Users\liukai\Desktop\lkadoc\教程图片\17.png)

**JSON格式化展示**

![](C:\Users\liukai\Desktop\lkadoc\教程图片\18.png)

#### 复杂的对象结构响应参数示例

&emsp;&emsp;在实际工作开发中，我们往往会准备一个封装好的响应参数对象来统一规范化接口的出参，现在我们来演示一下，把上面那复杂的Map结构响应参数示例改造成对象结构。

**准备一个响应封装对象**

```java
@LKAModel
public class ApiResult {
    @LKAProperty(value="状态码")
    private String code;
    @LKAProperty(value="消息")
    private String msg;
    @LKAProperty(value="响应数据")
    private Map<String,Object> result = new HashMap<>();
    
    private ApiResult() {}
    
    public static ApiResult success() {
        ApiResult res = new ApiResult();
        return res;
    }
    
    public ApiResult put(String key,Object value) {
        this.result.put(key, value);
        return this;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
    public Map<String, Object> getResult() { return result; }
    public void setResult(Map<String, Object> result) { this.result = result; }
}
```

**在LKADemoController2类下面再增加一个复杂的对象结构响应参数的测试方法**

```java
/**
     这个方法其实和上面复杂的Map结构响应参数示例是一样的，不一样的地方是一个是Map,一个是ApiResut对象。但是我们发现这个方法在响应参数描述是少用一个注解：
     @LKARespose(names= {"code","msg"},values= {"状态码","消息"})
     这是因为ApiResult对象已经通过@LKAProperty注解描述过"code","msg"属性了，Lkadoc会去自动扫描带有@LKAModel注解的响应对象。还有如果@LKARespose注解描述的参数和对象里面的属性一致的话，@LKARespose注解描述的参数会覆盖掉对象里面的属性
     */
    @LKAMethod(value="复杂的对象结构响应参数")
    @LKAResposes({
            @LKARespose(name="total",value="总记录数",parentName="result",parentValue="响应数据"),
            @LKARespose(type=User.class,parentName="users",parentIsArray=true,parentValue="用户对象列表",grandpaName="result")
    })
    @PostMapping("getObj")
    public ApiResult getObj() {
        List<User> users = new ArrayList<User>();
        User user1 = new User();
        user1.setName("张三");
        User user2 = new User();
        user2.setName("李四");
        users.add(user1);
        users.add(user2);
        return ApiResult.success().put("total",10).put("users",users);
    }
```

**重启项目，刷新Lkadoc界面响应参数展示结果如下，实际上和上面复杂的Map结构响应参数示例结果是一样的:**

![](C:\Users\liukai\Desktop\lkadoc\教程图片\15.png)

**通过点击"树状展示响应参数"按钮，可以很直观的显示复杂的响应参数结构**

![](C:\Users\liukai\Desktop\lkadoc\教程图片\16.png)

#### 响应参数对象参数分组

&emsp;&emsp;@LKARespose注解的group属性也可以实现响应参数分组，使用原理和请求参数分组是一样的。

**在LKADemoController2类下面增加一个响应参数对象参数分组测试方法**

```java
//这个方法上面复杂的对象结构响应参数测试方法是一样的，唯一不同的是在type=User.class增加了group = "getUser"属性配置，代表只展示getUser组的数据
@LKAMethod(value="响应参数对象参数分组")
    @LKAResposes({
            @LKARespose(name="total",value="总记录数",parentName="result",parentValue="响应数据"),
            @LKARespose(type=User.class,group = "getUser",parentName="users",parentIsArray=true,
                        parentValue="用户对象列表",grandpaName="result")
    })
    @PostMapping("getObj2")
    public ApiResult getObj2() {
        List<User> users = new ArrayList<User>();
        User user1 = new User();
        user1.setName("张三");
        User user2 = new User();
        user2.setName("李四");
        users.add(user1);
        users.add(user2);
        return ApiResult.success().put("total",10).put("users",users);
    }
```

**重启项目，刷新Lkadoc界面响应参数展示结果如下**：

![](C:\Users\liukai\Desktop\lkadoc\教程图片\19.png)

## 高级特性

&emsp;&emsp;Lkadoc提供了很多非常实用的特色功能，这些特色功能可以给大家在使用Lkadoc工具时带来如虎添翼的效果。有了这些特色功能的加持，能够助你迅速调试接口，快定定位错误，连测试代码都不用写了。还能大大提高与前端对接口的效率，简直堪称接口神器！

#### 调试API功能

&emsp;&emsp;Lkadoc支持对单个接口进入调试，我们只需要准备好请求参数的测试数据，然后点击"执行接口"按钮，就可以在调试窗口看到调试结果信息了。

![](C:\Users\liukai\Desktop\lkadoc\教程图片\20.png)

&emsp;&emsp;Lkadoc还可以通过选择执行方式来决定接口采用"同步"还是"异步"执行，如果选择"同步"测试，可以选择执行次数，和时间间隔，这样可以模拟对接口进行压力测试。如果选择"异步"测试，可以选择执行次数，这样可以模拟对接口进行并发测试。所有测试结果会打印在调试窗口中。

![](C:\Users\liukai\Desktop\lkadoc\教程图片\21.png)

#### 全局请求头令牌(Token)锁定

&emsp;&emsp;我们在调试需要token授权的接口时,需要在每个需要授权接口的请求头带上一个类似token的参数，调试时非常不方便，所以Lkadoc提供了一个可以给全局接口锁定一个请求头参数，这样就不需要在每一个需要授权的接口中去设置这个授权参数了。

&emsp;&emsp;同时我们可以通过@LKAMethod的token属性来控制当前接口是否需要token校验，如果token=true的话，那么在Lkadoc调试接口的时候，会自动带上锁定的请求头参数到后台。

![](C:\Users\liukai\Desktop\lkadoc\教程图片\22.png)

#### 快速定位新接口

&emsp;&emsp;大家应该还有印象，在用@LKADocument注解配置项目信息时有一个version属性用来设置项目的版本号，然后@LKAMethod注解也有一个version属性用来设置接口的版本号，在实际工作中，往往一个项目版本升级并不代表所有接口都需要升级，也可能会增加一些新接口。那么我们怎么才能在众多的接口中定位哪一个接口是新接口或最新修改的接口呢？很简单，我们只需把新接口或最新修改的接口的@LKAMethod注解version属性的版本值设置和@LKADocument注解的version属性的版本值设置成一致就可以了，这样Lkadoc会用星星标记出新接口。那么后端在和前端同事对接口时就可以很快定位哪些是新接口了

**修改@LKADocument注解如下，新增version="1.0"的属性配置**

```java 
@LKADocument(basePackages="com.lkad.api",projectName = "演示项目",description = "用于Lkadoc教学项目",version = "1.0")
@SpringBootApplication
public class LkadocDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(LkadocDemoApplication.class, args);
    }
}
```

**然后我们看一下用户注册方法，里面有配置 version="1.0"的属性配置，这个接口的版本号和项目的版本号是一致的，代表这是一个新接口，同时用户注册接口一般是不需要token校验的，这里我们设置token=false**

```java
//注意:JDK8及以上@LKAParam注解的names={"name","pwd","email","age"}配置可省略
@LKAMethod(value="用户注册",description="APP的注册接口",version="1.0",createTime="2021-08-08",author="LK",token=false)
@LKAParam(names={"name","pwd","email","age"},values= {"用户名^张凡","密码^123abc","n~邮箱^123@qq.com","n~年龄^21"})
@PostMapping("reg")
public String reg(String name,String pwd,String email,Integer age) {
    if(name == null || "".equals(name) || pwd == null || "".equals(pwd)){
        return "注册失败";
    }
    return "注册成功";
}
```

**重启项目，刷新Lkadoc页面如下:**

![](C:\Users\liukai\Desktop\lkadoc\教程图片\23.png)

#### 自动识别未加注解参数

&emsp;&emsp;当 @LKADocument 的 sconAll=true 时，会把接口的所有参数扫描出来，部分没有加@LKAProperty和@LKAParam注解的参数并没有设置作用描述之类的信息，那么就可以通过“自定义作用”动态的添加该参数的作用说明，当然也可以修改已经设置好的作用说明。

**修改@LKADocument注解如下，新增sconAll=true 的属性配置**

```java
@LKADocument(basePackages="com.lkad.api",projectName = "演示项目",description = "用于Lkadoc教学项目",version = "1.0",sconAll = true)
@SpringBootApplication
public class LkadocDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(LkadocDemoApplication.class, args);
    }
}
```

**新增一个实体类， 属性不加任何注解，但@LKAmodel注解必须加上**

```java
@LKAModel
public class Dept {
    private Integer id;
    private String name;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
```

**新增两个测试方法**

```java
@LKAMethod(value="测试SconAll")
@RequestMapping("testSconAll")
public ApiResult testSconAll(Dept dept){
    return ApiResult.success();
}

@LKAMethod(value="测试SconAll2")
@RequestMapping("testSconAll2")
public ApiResult testSconAll2(Integer age,String[] likes){
    return ApiResult.success();
}
```

**重启项目，刷新文档页面如下：**

![](C:\Users\liukai\Desktop\lkadoc\教程图片\24.png)

![](C:\Users\liukai\Desktop\lkadoc\教程图片\25.png)

**我们可以发现入参都可以识别出来，同时我们可以通过在参数名称上点击鼠标右键，修改参数作用说明，还可以设置高亮和删除状态**

![](C:\Users\liukai\Desktop\lkadoc\教程图片\26.png)

![](C:\Users\liukai\Desktop\lkadoc\教程图片\27.png)

#### 项目聚合

&emsp;&emsp;当我们在使用微服务或多个项目时，我们可以把多个项目的接口文档信息聚合到一个UI界面，只需要配置@LKADocument注解的serverNames属性即可，多个项目之间用英文“,”号隔开，“^”符号左边是项目名称，右边是项目地址，也可以是域名，这样我们就可以在Lkadoc界面自由的在当前项目和配置好的其它项目切换接口信息了。

**修改@LKADocument注解如下，新增serverNames 的属性配置**

```java
@LKADocument(basePackages="com.lkad.api",projectName = "演示项目",description = "用于Lkadoc教学项目",
version = "1.0",sconAll = true,serverNames = "物业项目^192.168.0.52:8081,租房项目^192.168.0.77:8001")
@SpringBootApplication
public class LkadocDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(LkadocDemoApplication.class, args);
    }
}

```

**重启项目，刷新文档如下：**

![](C:\Users\liukai\Desktop\lkadoc\教程图片\28.png)

#### 文档导出功能

&emsp;&emsp;Lkadoc还提供了导出功能，Lkadoc支持导出标准化格式的PDF或MarkDown接口文档，功能非常强大，能满足大部分场景需求。（目前只支持导出本地项目的接口）
  导出PDF文档前需要检查系统是否存在simsun.ttc字体，如果系统没有这个字体的话，导出PDF文档中文不能正确显示。 windows系统字体路径：C:/Windows/fonts/simsun.ttc
linux系统字体路径：/usr/share/fonts/win/simsun.ttc
mac系统字体路径：/System/Library/Fonts/simsun.ttc
![](C:\Users\liukai\Desktop\lkadoc\教程图片\29.png)

**PDF效果：**

![](C:\Users\liukai\Desktop\lkadoc\教程图片\30.png)

**MarkDown效果：**

![](C:\Users\liukai\Desktop\lkadoc\教程图片\31.png)

#### 给Lkadoc设置查看密码

**修改@LKADocument注解如下，新增 password 的属性配置**

```java
@LKADocument(basePackages="com.lkad.api",projectName = "演示项目",description = "用于Lkadoc教学项目",
version = "1.0",sconAll = true,serverNames = "物业项目^192.168.0.52:8081,租房项目^192.168.0.77:8001",password = "123")
@SpringBootApplication
public class LkadocDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(LkadocDemoApplication.class, args);
    }
}
```

**重启项目，刷新文档，这样就需要先输入密码才能进入Lkadoc界面：**

![](C:\Users\liukai\Desktop\lkadoc\教程图片\32.png)

**如果密码错误就进入错误提示页面：**

![](C:\Users\liukai\Desktop\lkadoc\教程图片\33.png)

**注意：除了给文档设置密码之外，在生产环境，还可以通过设置enabled属性值为false来关闭文档功能，这里就不演示了。**

#### 接口排序功能

**@LKAType 和 LKAMethod 都提示了一个order属性，用来设置目录和接口的排序规则，值越少，那么就越靠前。**

![](C:\Users\liukai\Desktop\lkadoc\教程图片\34.png)

#### 目录合并和接口切换目录

&emsp;&emsp;当多个类的@LKAType注解value值相同时，Lkadoc会把它们合并到一个目录管理，同时还可以通过@LKAMethod的directory属性来指定它的上一级目录。达到切换目录的目的，前提是directory的值必须存在。这里就不演示了，大家可以自己去尝试一下。

## 结尾

&emsp;&emsp;感谢您的使用与支持，希望它能够在工作中给您带来便利！如果觉得好用请推荐给您身边需要的朋友或同事。承诺终身免费，免费提供技术支持！如果您在使用过程中遇到问题或者有好的建议，请给我留言，谢谢！

项目开源地址：https://gitee.com/liuk168/lkadoc

**如果大家学得好用，记得给星哦**