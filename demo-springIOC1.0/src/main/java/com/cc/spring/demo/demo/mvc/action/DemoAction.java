package com.cc.spring.demo.demo.mvc.action;


import com.cc.spring.demo.demo.service.IDemoService;
import com.cc.spring.demo.spring.annotation.Autowired;
import com.cc.spring.demo.spring.annotation.Controller;
import com.cc.spring.demo.spring.annotation.RequestMapping;
import com.cc.spring.demo.spring.annotation.RequestParam;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/demo")
public class DemoAction {
	
	@Autowired
	private IDemoService demoService;
	
	@RequestMapping("/query.json")
	public void query(HttpServletRequest req,HttpServletResponse resp,
		   @RequestParam("name") String name){
		String result = demoService.get(name);
		System.out.println(result);
//		try {
//			resp.getWriter().write(result);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	@RequestMapping("/edit.json")
	public void edit(HttpServletRequest req,HttpServletResponse resp,Integer id){

	}
	
}
