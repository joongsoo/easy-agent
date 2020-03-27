# easy-agent
A library for Java agents that makes it very easy to inject proxies into classes at the bytecode level.


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
                        .targetMethodName("aa")
                        .addInterceptor(TestInterceptor.class)
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
`java -javaagent:your/path/{your-jar-name}.jar -jar your-application.jar`