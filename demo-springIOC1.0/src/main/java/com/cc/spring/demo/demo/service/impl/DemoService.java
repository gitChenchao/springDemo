package com.cc.spring.demo.demo.service.impl;


import com.cc.spring.demo.demo.service.IDemoService;
import com.cc.spring.demo.spring.annotation.Service;

@Service
public class DemoService implements IDemoService {

	public String get(String name) {
		return "My name is " + name;
	}

}
