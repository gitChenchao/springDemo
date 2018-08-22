package com.cc.spring.formework.beans;

import com.cc.spring.formework.core.FactoryBean;
import com.sun.corba.se.spi.ior.ObjectKey;

public class BeanWrapper extends FactoryBean {

    private BeanPostProcessor postProcessor;

    private Object wrapperInstance;

    public BeanPostProcessor getPostProcessor() {
        return postProcessor;
    }

    public void setPostProcessor(BeanPostProcessor postProcessor) {
        this.postProcessor = postProcessor;
    }

    private Object originaInstance;

    public BeanWrapper(Object instance){
        this.wrapperInstance = instance;
        this.originaInstance = instance;
    }

    public Object getWrapperInstance(){
        return this.wrapperInstance;
    }

    /**
     * 返回代理以后的Class
     * 可能会是个$Proxy0
     * @return
     */
    public Class<?> getWrappedClass(){
        return this.wrapperInstance.getClass();
    }
}
