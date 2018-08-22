package com.cc.spring.demo.spring.servlet;

import com.cc.spring.demo.demo.mvc.action.DemoAction;
import com.cc.spring.demo.spring.annotation.Autowired;
import com.cc.spring.demo.spring.annotation.Controller;
import com.cc.spring.demo.spring.annotation.Service;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类名称：DispatchServlet<br>
 * 类描述：<br>
 * 创建时间：2018年08月05日<br>
 *
 * @author 陈超
 * @version 1.0.0
 */
public class DispatchServlet extends HttpServlet {

    private Properties contextConfig = new Properties();

    //ioc
    private Map<String,Object> beanDefinitions = new ConcurrentHashMap<String, Object>();

    private List<String> classNames = new ArrayList<String>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("-----------------doPost方法-------------------");
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println("开始初始化");
        //定位
        doLoadConfig(config.getInitParameter("contextConfigLocation"));
        //加载
        doScanner(contextConfig.getProperty("scanPackage"));
        //注册
        doRegistry();
        //自动依赖注入,在Spring中是通过调用getBean方法才出发依赖注入的
        doAutowired();

        DemoAction action = (DemoAction)beanDefinitions.get("demoAction");
        action.query(null,null,"Tom");

        //如果是SpringMVC会多设计一个HnandlerMapping

        //将@RequestMapping中配置的url和一个Method关联上
        //以便于从浏览器获得用户输入的url以后，能够找到具体执行的Method通过反射去调用
        initHandlerMapping();
    }

    private void initHandlerMapping() {
    }

    private void doAutowired() {
        if(beanDefinitions.isEmpty()){return;}
        for (Map.Entry<String,Object> entry:beanDefinitions.entrySet() ){
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field:fields
                 ) {
                if(!field.isAnnotationPresent(Autowired.class)){continue;}
                Autowired autowired = field.getAnnotation(Autowired.class);
                //采取byName
                String beanName = autowired.value().trim();
                if("".equals(beanName)){
                    //采取byType
                    beanName = field.getType().getSimpleName();
                }
                //设置强制访问
                field.setAccessible(true);
                //自动赋值
                try {
                    System.out.println(beanDefinitions.get(beanName));
                    field.set(entry.getValue(),beanDefinitions.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doRegistry() {
        if(classNames.isEmpty()){return;}
        try {
            for (String className : classNames){
                Class clazz = Class.forName(className);
                //在Spring使用多个子方法来处理的
                if(clazz.isAnnotationPresent(Controller.class)){
                    String beanName = lowerFirstCase(clazz.getSimpleName());
                    //在Spring中在这个阶段不是不会直接put instance，这里put的是BeanDefinition
                    beanDefinitions.put(beanName,clazz.newInstance());
                }else if(clazz.isAnnotationPresent(Service.class)){
                    Service service = (Service) clazz.getAnnotation(Service.class);
                    //默认用类名首字母注入
                    //如果自己定义了beanName,那么优先考虑使用自己定义的beanName
                    //如果是一个接口，使用接口的类型去自动注入
                    //在Spring中同样会分别调用不同方法，autowired ByName autoried byType
                    String beanName = service.value();
                    if("".equals(beanName.trim())){
                        beanName = lowerFirstCase(clazz.getSimpleName());
                    }
                    Object instance = clazz.newInstance();
                    beanDefinitions.put(beanName,instance);

                    Class<?>[] interfaces = clazz.getInterfaces();

                    for (Class<?> i :interfaces){
                        beanDefinitions.put(i.getSimpleName(),instance);
                    }
                }else{
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doScanner(String packageName) {
        URL url = this.getClass().getClassLoader().getResource("/"+packageName.replaceAll("\\.","/"));
        File classDir = new File(url.getFile());
        for(File f : classDir.listFiles()){
            if(f.isDirectory()){
                doScanner(packageName+"."+f.getName());
            }else{
                classNames.add(packageName+"."+f.getName().replace(".class",""));
            }
        }
    }

    private void doLoadConfig(String location) {
        //在Spring中是通过Reader定位的
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(location.replace("classpath:",""));
        try {
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(null!=is){
                    is.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private String lowerFirstCase(String str){
        char [] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
