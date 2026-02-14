# lifecycle-lab test results

## Commands run

```powershell
.\mvnw.cmd -q -DskipTests compile
```

```powershell
# successful startup + AOP + actuator check on isolated port
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--server.port=8081"
# while running:
#   GET http://localhost:8081/actuator/beans
```

```powershell
# controlled second run on same port to force context cancellation and capture destroy callbacks
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--server.port=8081"
```

## Verification summary

- Compile succeeded.
- Bean lifecycle ordering for `lifecycleTarget` and `initMethodBean` is visible in `app-run-8081.log`.
- AOP proxy wrapping and aspect invocation are visible in `app-run-8081.log`.
- Actuator `/actuator/beans` returned JSON and contains lifecycle-lab beans (`actuator-beans.json`).
- Destroy callbacks are visible in `app-destroy.log`.

## Key startup lifecycle evidence

From `app-run-8081.log`:

```text
BFPP: postProcessBeanFactory (before beans are instantiated)
HelperBean: constructor
LifecycleTarget: constructor
LifecycleTarget: setter DI (setHelper)
LifecycleTarget: BeanNameAware.setBeanName = lifecycleTarget
LifecycleTarget: ApplicationContextAware.setApplicationContext
BPP: BEFORE init  beanName=lifecycleTarget type=com.example.lifecyclelab.beans.LifecycleTarget
LifecycleTarget: @PostConstruct
LifecycleTarget: InitializingBean.afterPropertiesSet()
BPP: AFTER  init  beanName=lifecycleTarget type=com.example.lifecyclelab.beans.LifecycleTarget$$SpringCGLIB$$0 isAopProxy=true
InitMethodBean: constructor
InitMethodBean: setMessage = set by BeanFactoryPostProcessor
BPP: BEFORE init  beanName=initMethodBean type=com.example.lifecyclelab.beans.InitMethodBean
InitMethodBean: custom init-method (init), message=set by BeanFactoryPostProcessor
BPP: AFTER  init  beanName=initMethodBean type=com.example.lifecyclelab.beans.InitMethodBean isAopProxy=false
Runner: bean class = com.example.lifecyclelab.beans.LifecycleTarget$$SpringCGLIB$$0, isAopProxy=true
ASPECT: before String com.example.lifecyclelab.beans.LifecycleTarget.doWork(String)
LifecycleTarget: doWork(hello)
ASPECT: after  String com.example.lifecyclelab.beans.LifecycleTarget.doWork(String)
```

## Actuator beans evidence

From `actuator-beans.json`:

```text
initMethodBean
demoBeanFactoryPostProcessor
loggingBeanPostProcessor
lifecycleTarget
tracingAspect
```

## Destroy lifecycle evidence

From `app-destroy.log`:

```text
InitMethodBean: custom destroy-method (destroy)
LifecycleTarget: @PreDestroy
LifecycleTarget: DisposableBean.destroy()
```

Note: destroy logs were captured during a controlled context-cancellation run (second app startup on the same port), which still exercises Spring bean destruction callbacks in this project.
