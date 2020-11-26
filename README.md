# Boot
### 描述

1. 适用于Java重新启动实例
2. 适用于Java关闭实例

### 实现方式

1. 通过Java运行时环境调用外部shell
2. 在shell文件中写入执行jar包

### 主要代码

1. 执行shell文件

   ```java
   Runtime.getRuntime().addShutdownHook(new Thread(() -> {
       // sleep 500 ms
       try {
           Thread.sleep(500);
           Runtime.getRuntime().exec(shellCommand.toString());
       } catch (InterruptedException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }
   }));
   ```

2. 配置类

   ```java
   public class ShellConfiguration {
   	
   	// windows shell 文件
       private String windows;
   
   	// linux shell 文件
       private String linux;
   }
   ```

3. 主要方法

   ```java
   /**
        * 使用配置文件带参数重启
        *
        * @param args 传递给脚本的参数
        * @throws FileNotFoundException 文件不存在异常
        */
   void restartByParams(String... args) throws FileNotFoundException;
   
   /**
        * 带参数重启
        *
        * @param shellName 没有后缀的脚本名
        * @param args      传递给脚本的参数
        * @throws FileNotFoundException 文件不存在异常
        */
   void restart(String shellName, String... args) throws FileNotFoundException;
   
   /**
       * 停止
       */
   void stop();
   ```
### 功能新增

1. 支持properties配置脚本

   ```java
   |-- shell.properties 项目路径下的shell.properties文件
      |-- config
      	|-- shell.properties 项目路径config下的shell.properties
   ```

2. 新增关闭钩子接口

   > 将希望在系统关闭时执行的操作以实现 IShutdownHook ，将实现类加入到shutdownHooks就能在系统关闭时执行