package com.cc.spring.demo.action;

import com.cc.spring.demo.service.IQueryService;
import com.cc.spring.formework.annotation.CCAutowired;
import com.cc.spring.formework.annotation.CCController;
import com.cc.spring.formework.annotation.CCRequestMapping;
import com.cc.spring.formework.annotation.CCRequestParam;
import com.cc.spring.formework.webmvc.GPModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * 公布接口url
 * @author Tom
 *
 */
@CCController
@CCRequestMapping("/")
public class PageAction {

    @CCAutowired
    IQueryService queryService;

    @CCRequestMapping("/first.html")
    public GPModelAndView query(@CCRequestParam("teacher") String teacher){
        String result = queryService.query(teacher);
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("teacher", teacher);
        model.put("data", result);
        model.put("token", "123456");
        return new GPModelAndView("first.html",model);
    }

}