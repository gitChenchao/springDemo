package com.cc.spring.formework.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class CCHandlerAdapter {
    private Map<String,Integer> paramMapping;

    public CCHandlerAdapter(Map<String,Integer> paramMapping){
        this.paramMapping = paramMapping;
    }


    /**
     *
     * @param req
     * @param resp
     * @param handler
     * @return
     */
    public CCModelAndView handle(HttpServletRequest req, HttpServletResponse resp, CCHandlerMapping handler) {
        //根据用户请求的参数信息,根method中的参数信息进行动态匹配
        //resp传进来的目的只有一个，只是为了将其赋值给方法参数,仅此而已
        //只有当用户传过来的ModleAndView不为空的时候
        return null;
    }
}
