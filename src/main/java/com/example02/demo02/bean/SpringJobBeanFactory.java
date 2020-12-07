package com.example02.demo02.bean;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 在监听类容器中无法使用@Autowired注释，
 * 只能通过先获取spring容器再获取相应的Bean对象。
 */
@Component
public class SpringJobBeanFactory implements ApplicationContextAware{

    private static ApplicationContext applicationContext;

    // spring容器的set方法
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringJobBeanFactory.applicationContext = applicationContext;
    }

    // spring容器的get方法
    public static ApplicationContext getApplicationContext(){
        return applicationContext;
    }

    // 下面的两个getBean的方法是两种不同的获取Bean对象的方法，前者为通过变量名获取，后者通过类名获取

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException{
        if (applicationContext == null){
            return null;
        }
        return (T) applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> name) throws BeansException{
        if (applicationContext == null){
            return null;
        }
        return applicationContext.getBean(name);
    }
}
