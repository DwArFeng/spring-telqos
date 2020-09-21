# spring-terminator

一款基于Spring框架的优雅的程序终止器。

## maven坐标

   ```xml
   <dependency>
       <groupId>com.dwarfeng</groupId>
       <artifactId>spring-terminator</artifactId>
       <version>1.0.0.a</version>
   </dependency>
   ```

## 使用方式

1. 在Spring中添加如下配置。

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xmlns:terminator="http://dwarfeng.com/schema/spring-terminator"
          xmlns="http://www.springframework.org/schema/beans"
          xsi:schemaLocation="http://www.springframework.org/schema/beans
           http://www.springframework.org/schema/beans/spring-beans.xsd
           http://dwarfeng.com/schema/spring-terminator
           http://dwarfeng.com/schema/spring-terminator/spring-terminator.xsd">
   
       <terminator:bean id="terminator" pre-delay="1000" post-delay="1000"/>
   </beans>
   ```
   
2. 使用`ApplicationUtil.launch`方法启动程序。
   
   ```java
       public static void main(String[] args) {
           ApplicationUtil.launch("classpath:spring/application-context*.xml");
       }
   ```
3. 在bean中注入`Terminal`对象，随时随地，优雅的关闭程序。

   ```java
    @Component
    public static class ProgramKiller {

        @Autowired
        private Terminator terminator;

        @Override
        public void exit() {
            terminator.exit(10);
        }
    }
   ```
