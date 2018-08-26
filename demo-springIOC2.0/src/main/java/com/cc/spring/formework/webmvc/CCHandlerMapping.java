package com.cc.spring.formework.webmvc;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class CCHandlerMapping {

    private Object controller;

    private Method method;

    private Pattern pattern;

    public CCHandlerMapping(Pattern pattern,Object controller,Method method){
        this.pattern=pattern;
        this.controller=controller;
        this.method=method;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}
