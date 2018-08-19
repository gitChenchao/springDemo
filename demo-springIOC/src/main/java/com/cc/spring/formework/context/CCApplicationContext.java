package com.cc.spring.formework.context;

import com.cc.spring.demo.action.MyAction;
import com.cc.spring.formework.annotation.CCAutowired;
import com.cc.spring.formework.annotation.CCController;
import com.cc.spring.formework.annotation.CCService;
import com.cc.spring.formework.beans.BeanDefinition;
import com.cc.spring.formework.beans.BeanPostProcessor;
import com.cc.spring.formework.beans.BeanWrapper;
import com.cc.spring.formework.context.support.BeanDefinitionReader;
import com.cc.spring.formework.core.BeanFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CCApplicationContext implements BeanFactory {

    private String [] configLications;

    private Map<String,BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();

    //用来保证注册式单例
    private Map<String,Object> beanCacheMap = new HashMap<String, Object>();

    //用来存储所有的被代理过的对象
    private Map<String,BeanWrapper> beanWrapperMap = new ConcurrentHashMap<String, BeanWrapper>();

    private BeanDefinitionReader reader;

    public CCApplicationContext(String ... configLocations){
        this.configLications = configLocations;
        refresh();
    }

    public void refresh(){
        //定位
        this.reader = new BeanDefinitionReader(configLications);
        //加载
        List<String> beanDefinitions = reader.loadBeanDefinitions();
        //注册
        doRegisty(beanDefinitions);
        //依赖注入(lazy-init = false)
        doAutowrited();

        MyAction myAction = (MyAction)this.getBean("myAction");
        myAction.query("任性的Tom老师");

    }

    //开执行自动化依赖注入
    private void doAutowrited() {
        for (Map.Entry<String,BeanDefinition> beanDefinitionEntry : this.beanDefinitionMap.entrySet()){
            String beanName = beanDefinitionEntry.getKey();
            if(!beanDefinitionEntry.getValue().isLazyInit()){
                getBean(beanName);
            }
        }

        for (Map.Entry<String,BeanWrapper> beanWrapperEntry : this.beanWrapperMap.entrySet()){

            populatBean(beanWrapperEntry.getKey(),beanWrapperEntry.getValue().getWrapperInstance());
        }
    }

    public void populatBean(String beanName,Object instance){
        Class clazz = instance.getClass();
        if(!(clazz.isAnnotationPresent(CCController.class)||clazz.isAnnotationPresent(CCService.class))){
            return;
        }
        Field[] fields =  clazz.getDeclaredFields();
        for (Field field : fields){
            if(!field.isAnnotationPresent(CCAutowired.class)){continue;}
            CCAutowired autorited = field.getAnnotation(CCAutowired.class);
            String autowiredBeanName = autorited.value().trim();
            if("".equals(autowiredBeanName)){
                autowiredBeanName = field.getType().getName();
            }
            field.setAccessible(true);
            try {
                field.set(instance,this.beanWrapperMap.get(autowiredBeanName).getWrapperInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    //真正的将Beandefinitios注册到BeanDefinitionsMap中
    private void doRegisty(List<String> beanDefinitions){
        //beanname有三种情况:
        //1、默认是类名首字母小写
        //2、自定义名字
        //3、接口注入
        try {
            for (String className : beanDefinitions) {
                Class<?> beanClass =  Class.forName(className);
                if(beanClass.isInterface()){continue;}
                BeanDefinition beanDefinition = reader.registerBean(className);
                if(beanDefinition != null){
                    //如果是多个实现类，只能覆盖 可以自定义beanName
                    this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
                }
                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> i: interfaces) {
                    //如果是多个实现类，只能覆盖
                    //为什么？因为Spring没那么智能，就是这么傻
                    //这个时候，可以自定义名字
                    this.beanDefinitionMap.put(i.getName(),beanDefinition);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 依赖注入从这个开始
     * 通过读取BeanDefinition中的信息
     * 然后，通过反射机制创建一个实例并返回
     * Spring做法是，不会把最原始的对象放出去，会用一个BeanWrapper来进行一次包装
     * 装饰器模式:
     * 1、保留原来的OOP关系
     * 2、我需要对它进行扩展，增强(为了以后的AOP打基础)
     * @param beanNaame
     * @return
     */
    @Override
    public Object getBean(String beanNaame) {
        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanNaame);
        String className = beanDefinition.getBeanClassName();
        try{
            //生成通知事件
            BeanPostProcessor beanPostProcessor = new BeanPostProcessor();
            Object instance = instantionBean(beanDefinition);
            if(null == instance){return null;}
            //在实例初始化以前调用一次
            beanPostProcessor.postProcessBeforeInitialization(instance,beanNaame);
            BeanWrapper beanWrapper = new BeanWrapper(instance);
            beanWrapper.setPostProcessor(beanPostProcessor);
            this.beanWrapperMap.put(beanNaame,beanWrapper);
            //在实例初始化以后调用一次
            beanPostProcessor.postProcessAfterInitialization(instance,beanNaame);
//            populatBean(beanNaame,instance);
            //相当于给自己留有可操作的空间
            return this.beanWrapperMap.get(beanNaame).getWrapperInstance();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private Object instantionBean(BeanDefinition beanDefinition){
        Object instance = null;
        String className = beanDefinition.getBeanClassName();
        try{

            if(this.beanCacheMap.containsKey(className)){
                instance = this.beanCacheMap.get(className);
            }else{
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                this.beanCacheMap.put(className,instance);
            }
            return instance;
        }catch (Exception e){

        }
        return null;
    }
}
