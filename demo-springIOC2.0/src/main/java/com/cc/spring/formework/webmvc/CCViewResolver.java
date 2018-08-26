package com.cc.spring.formework.webmvc;

import java.io.File;

//设计这个类的主要目的:
//1、将一个静态文件变为一个动态文件
//2、根据用户传送参数不同，产生不同的结果
//3、最终输出字符串,交给Response输出
public class CCViewResolver {
    private String viewName;
    private File templateFile;

    public CCViewResolver(String viewName, File templateFile){
        this.viewName=viewName;
        this.templateFile=templateFile;
    }

    public String viewResoler(){
        return null;
    }
}
