package com.cc.spring.demo.action;

import com.cc.spring.demo.service.IModifyService;
import com.cc.spring.demo.service.IQueryService;
import com.cc.spring.formework.annotation.CCAutowired;
import com.cc.spring.formework.annotation.CCController;
import com.cc.spring.formework.annotation.CCRequestMapping;
import com.cc.spring.formework.annotation.CCRequestParam;
import com.cc.spring.formework.webmvc.CCModelAndView;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 公布接口url
 * @author Tom
 *
 */
@CCController
@CCRequestMapping("/web")
public class MyAction {

    @CCAutowired
    IQueryService queryService;
    @CCAutowired
    IModifyService modifyService;

    @CCRequestMapping("/query.json")
    public CCModelAndView query(HttpServletRequest request, HttpServletResponse response,
                                @CCRequestParam("name") String name){
        String result = queryService.query(name);
        System.out.println(result);
        return out(response,result);
    }

    @CCRequestMapping("/add*.json")
    public CCModelAndView add(HttpServletRequest request, HttpServletResponse response,
                              @CCRequestParam("name") String name, @CCRequestParam("addr") String addr){
        String result = modifyService.add(name,addr);
        return out(response,result);
    }

    @CCRequestMapping("/remove.json")
    public CCModelAndView remove(HttpServletRequest request, HttpServletResponse response,
                                 @CCRequestParam("id") Integer id){
        String result = modifyService.remove(id);
        return out(response,result);
    }

    @CCRequestMapping("/edit.json")
    public CCModelAndView edit(HttpServletRequest request,HttpServletResponse response,
                               @CCRequestParam("id") Integer id,
                               @CCRequestParam("name") String name){
        String result = modifyService.edit(id,name);
        return out(response,result);
    }



    private CCModelAndView out(HttpServletResponse resp,String str){
        try {
            resp.getWriter().write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}