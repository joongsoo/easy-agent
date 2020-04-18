# easy-agent
This library was created to easily develop a Java agent that injects proxy code into the class bytecode.

If you write code in Java without needing to know the [bytecode instrumentation](https://docs.oracle.com/javase/8/docs/technotes/guides/instrumentation/index.html), it is injected into bytecode.

## Table of content
- [Summary](#-summary)
- [Feature](#-feature)
- [Full Documentation](#-full-documentation)
- [Extend via plugin](#-extend-via-plugin)
- [How to use](#-how-to-use)
- [Example](#-example)

<br/>

## 🚀 Summary
From an architectural point of view, the Java agent is a good way to inject logic from outside your application.

Many applications are increasingly using cloud and virtualized environments. In that case, there are times when you need to write code that is infrastructure dependent.

For example, if you need to implement [istio distributed tracing](https://istio.io/docs/tasks/observability/distributed-tracing/overview/),
You will write code to propagate HTTP headers inside your application.

However, if you write like this, your application will depend to [istio](https://istio.io/).

If using java agent, infrastructure dependent logic can be managed at the infrastructure level. And independence and reusability increase, because it is injected at the infrastructure layer.

![image](https://user-images.githubusercontent.com/15869525/79363768-4bf4d000-7f83-11ea-9ead-8a95780d413d.png)

[easy-agent](https://github.com/joongsoo/easy-agent) helps you develop java agent easily.

<br/>

## 🚀 Feature

### Fast
`easy-agent` uses [ASM](https://asm.ow2.io/), it is low-level library. [ASM](https://asm.ow2.io/) provide high performance.

### Easy
You don't need to know bytecode transform. You can easily inject a proxy with simple java code.

### Productivity
Productivity is improved by placing infrastructure-dependent code at the infrastructure layer.

You don't have to think about infrastructure when developing and deploying applications. Because the infrastructure-dependent logic is injected by the agent.

### Extension
`easy-agent` provides a `easy-agent-api` that can be easily extended. You can easily develop reusable plugins.

You can see more info in [plugin wiki page](https://github.com/joongsoo/easy-agent/wiki/Make-reusable-plugin).

### Documentation
We try to give you the most detailed and friendly documentation possible. If there is any room for improvement in the document, please make a suggestion.

<br/>

## 🚀 Full documentation
If you want see more info about this module, See [Github wiki documentation](https://github.com/joongsoo/easy-agent/wiki)

<br/>

## 🚀 Extend via plugin
You can make reusable easy-agent plugin. it is very simple. [See more details](https://github.com/joongsoo/easy-agent/wiki/Make-reusable-plugin)

<br/>

## 🚀 How to use
Just follow 6 simple steps.

#### (1) Add maven dependency to pom.xml
```xml
<dependency>
    <groupId>software.fitz</groupId>
    <artifactId>easy-agent-core</artifactId>
    <version>0.4.0-RELEASE</version>
</dependency>
```

#### (2) Implements AroundInterceptor
Override only necessary methods.

```java
public class YourInterceptor implements AroundInterceptor {

    @Override
    public Object[] before(Object target, Method targetMethod, Object[] args) {
        // your proxy code
        if (args[0] instanceof String) {
            args[0] = args[0] + " hi"; // possible replace to target method arguments
        }
        return args;
    }

    @Override
    public Object after(Object target, Method targetMethod, Object returnedValue, Object[] args) {
        // your proxy code
        return returnedValue; // possible replace to return value
    }

    @Override
    public void thrown(Object target, Method targetMethod, Throwable t, Object[] args) {
        // your proxy code
    }
}
```

#### (3) Define your plugin & Register interceptor to your target class
```java
public class YourPlugin implements Plugin {

    @Override
    public void setup(TransformerRegistry transformerRegistry) {

        transformerRegistry.register(
                TransformDefinition.builder()
                        // "+" means applies to all classes that extend this class
                        .transformStrategy(TransformStrategy.className("package.Class+")) 
                        .targetMethodName("methodName")
                        .addInterceptor(YourInterceptor.class)
                        .build()
        );
    }
}
```

#### (4) Define premain class & Register your plugin
```java
public class PremainClass {

    public static void premain(String agentArgs, Instrumentation instrumentation) {

        EasyAgentBootstrap.initialize(agentArgs, instrumentation)
                .addPlugin(new YourPlugin())
                .start();
    }
}
```

#### (5) Add maven plugin to your pom.xml
1. Replace `{your-jar-name}` with the name of the jar you want.
2. Replace `{package.to.premain-class}` with a class that implements the `premain` method.

```xml
<plugin>
    <artifactId>maven-assembly-plugin</artifactId>
    <version>2.2</version>
    <executions>
        <execution>
            <id>make-assembly</id>
            <phase>package</phase>
            <goals>
                <goal>single</goal>
            </goals>
            <configuration>
                <finalName>{your-jar-name}</finalName>
                <appendAssemblyId>false</appendAssemblyId>
                <archive>
                    <manifest>
                        <addDefaultImplementationEntries>true</addDefaultImplementationEntries>
                        <addDefaultSpecificationEntries>true</addDefaultSpecificationEntries>
                    </manifest>
                    <manifestEntries>
                        <Premain-Class>{package.to.premain-class}</Premain-Class>
                        <Can-Retransform-Classes>true</Can-Retransform-Classes>
                        <Can-Redefine-Classes>true</Can-Redefine-Classes>
                        <Boot-Class-Path>{your-jar-name}.jar</Boot-Class-Path>
                    </manifestEntries>
                </archive>
                <descriptorRefs>
                    <descriptorRef>jar-with-dependencies</descriptorRef>
                </descriptorRefs>
            </configuration>
        </execution>
    </executions>
</plugin>
```

#### (6) Run with your application
Specify your agent jar in the `-javaagent` argument.

```
java -javaagent:/path/{your-agent}.jar -jar target-application.jar
```

<br/>

## 🚀 Example
Example of replacing method arguments.

You can see more examples in [wiki page](https://github.com/joongsoo/easy-agent/wiki/Examples).

#### AroundInterceptor
```java
public class YourInterceptor implements AroundInterceptor {

    @Override
    public Object[] before(Object target, Method targetMethod, Object[] args) {
        if (args[0] != null && args[0] instanceof String) {
            args[0] = args[0] + " Hi!";
        }
        return args;
    }
}
```

#### Plugin
The process of registering the module was omitted.

```java
public class YourPlugin implements Plugin {

    @Override
    public void setup(TransformerRegistry transformerRegistry) {

        transformerRegistry.register(
                TransformDefinition.builder()
                        .transformStrategy(TransformStrategy.className("package.TargetClass")) 
                        .targetMethodName("printName")
                        .addInterceptor(YourInterceptor.class)
                        .build()
        );
    }
}
```

#### Target class & Main class
```java
public class TargetClass {
    
    public void printName(String name) {
        System.out.println(name); 
    }

    public static void main(String[] args) {
        new TargetClass().printName("joongsoo");
    }
}
```

#### Execute result
```
joongsoo Hi!
```
