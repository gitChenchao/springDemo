package com.cc.spring.demo.demo.mvc.action;


import com.cc.spring.demo.demo.service.IDemoService;
import com.cc.spring.demo.spring.annotation.Autowired;
import com.cc.spring.demo.spring.annotation.Controller;
import com.cc.spring.demo.spring.annotation.RequestMapping;

@Controller
public class MyAction {

		@Autowired
		IDemoService demoService;
	
		@RequestMapping("/index.html")
		public void query(){

		}
	
}
