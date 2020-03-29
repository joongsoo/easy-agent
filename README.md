# easy-agent
A library for Java agents that makes it very easy to inject proxies into classes at the bytecode level.

## Table of content
- [Documentation](#documentation)
- [Extend via plugin](#extend-via-plugin)
- [How to use](#how-to-use)
- [Example](#example)


## Documentation
If you want see more info. See [Github wiki documentation](https://github.com/joongsoo/easy-agent/wiki)

## Extend via plugin
You can developing reusable easy-agent plugin. it is very simple. [See more details](https://github.com/joongsoo/easy-agent/wiki/Plugin)

## How to use
Just follow 6 simple steps.

#### (1) Add maven dependency to pom.xml
```xml
<dependency>
    <groupId>software.fitz</groupId>
    <artifactId>easy-agent-core</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

#### (2) Implements AroundInterceptor
```java
public class YourInterceptor implements AroundInterceptor {

    @Override
    public Object[] before(Object target, Object[] args) {
        // your proxy code
        return args; // possible replace to target method arguments
    }

    @Override
    public void after(Object target, Object[] args) {
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

        new EasyAgentBootstrap(agentArgs, instrumentation)
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
java -javaagent:/path/{your-jar-name}.jar -jar target-application.jar
```


## Example
Example of replacing method arguments.

#### AroundInterceptor
```java
public class YourInterceptor implements AroundInterceptor {

    @Override
    public Object[] before(Object target, Object[] args) {
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
`> joongsoo Hi!`
