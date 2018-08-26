package com.cc.spring.formework.webmvc.servlet;

import com.cc.spring.formework.annotation.CCController;
import com.cc.spring.formework.annotation.CCRequestMapping;
import com.cc.spring.formework.annotation.CCRequestParam;
import com.cc.spring.formework.context.CCApplicationContext;
import com.cc.spring.formework.webmvc.CCHandlerAdapter;
import com.cc.spring.formework.webmvc.CCHandlerMapping;
import com.cc.spring.formework.webmvc.CCModelAndView;
import com.cc.spring.formework.webmvc.CCViewResolver;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//servlet作为MVC启动入口
public class CCDispatcherServlet extends HttpServlet {

    private final String LOCATION = "contextConfigLocation";

    private List<CCHandlerMapping> handlerMappings = new ArrayList<CCHandlerMapping>();

    private Map<CCHandlerMapping,CCHandlerAdapter>  handlerAdapters = new HashMap<CCHandlerMapping, CCHandlerAdapter>();

    private List<CCViewResolver> viewResolvers = new ArrayList<CCViewResolver>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatcher(req, resp);

        }catch (Exception e){
            resp.getWriter().write("<font size='25' color='blue'>500 Exception</font><br/>Details:<br/>" + Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]","")
                    .replaceAll("\\s","\r\n") +  "<font color='green'><i>Copyright@GupaoEDU</i></font>");
            e.printStackTrace();
        }
    }

    private void doDispatcher(HttpServletRequest req, HttpServletResponse resp) throws Exception{
        CCHandlerMapping handler = getHandler(req);

        CCHandlerAdapter ha = getHandlerAdapter(handler);

        CCModelAndView mv = ha.handle(req,resp,handler);

        processDispatchResult(resp,mv);
    }

    private void processDispatchResult(HttpServletResponse resp, CCModelAndView mv) {
        //
    }

    private CCHandlerAdapter getHandlerAdapter(CCHandlerMapping handler) {
        return null;
    }

    private CCHandlerMapping getHandler(HttpServletRequest req) {
        if(this.handlerMappings.isEmpty()){ return  null;}
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath,"").replaceAll("/+","/");

        for (CCHandlerMapping handler : this.handlerMappings) {
            Matcher matcher = handler.getPattern().matcher(url);
            if(!matcher.matches()){ continue;}
            return handler;
        }

        return null;
    }


    @Override
    public void init(ServletConfig config) throws ServletException {
        //相当于把ioc初始化了
        CCApplicationContext ccApplicationContext = new CCApplicationContext(config.getInitParameter(LOCATION));
        initStrategies(ccApplicationContext);
    }

    private void initStrategies(CCApplicationContext context) {
        //有九种策略
        // 针对于每个用户请求，都会经过一些处理的策略之后，最终才能有结果输出
        // 每种策略可以自定义干预，但是最终的结果都是一致
        // ModelAndView

        // =============  这里说的就是传说中的九大组件 ================
        initMultipartResolver(context);//文件上传解析，如果请求类型是multipart将通过MultipartResolver进行文件上传解析
        initLocaleResolver(context);//本地化解析
        initThemeResolver(context);//主题解析

        /** 我们自己会实现 */
        //GPHandlerMapping 用来保存Controller中配置的RequestMapping和Method的一个对应关系
        initHandlerMappings(context);//通过HandlerMapping，将请求映射到处理器
        /** 我们自己会实现 */
        //HandlerAdapters 用来动态匹配Method参数，包括类转换，动态赋值
        initHandlerAdapters(context);//通过HandlerAdapter进行多类型的参数动态匹配

        initHandlerExceptionResolvers(context);//如果执行过程中遇到异常，将交给HandlerExceptionResolver来解析
        initRequestToViewNameTranslator(context);//直接解析请求到视图名

        /** 我们自己会实现 */
        //通过ViewResolvers实现动态模板的解析
        //自己解析一套模板语言
        initViewResolvers(context);//通过viewResolver解析逻辑视图到具体视图实现

        initFlashMapManager(context);//flash映射管理器

    }

    private void initFlashMapManager(CCApplicationContext context) {
    }

    private void initViewResolvers(CCApplicationContext context) {
        //解决页面名字和模板文件关联的问题
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootDir = new File(templateRootPath);
        for (File template : templateRootDir.listFiles()) {
            this.viewResolvers.add(new CCViewResolver(template.getName(),template));
        }

    }

    private void initRequestToViewNameTranslator(CCApplicationContext context) {
    }

    private void initHandlerExceptionResolvers(CCApplicationContext context) {
    }

    private void initHandlerAdapters(CCApplicationContext context) {
        //在初始化阶段，将这些参数的名字或类型按一定顺序保存下来
        //后面需要通过反射，传的形参是一个数组
        for (CCHandlerMapping handlerMapping : this.handlerMappings){
            //每一个方法有一个参数列表,这里保存的是参数列表
            Map<String,Integer> paramMapping = new HashMap<String, Integer>();


            //这里处理了命名参数
            Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();
            for(int i = 0;i<pa.length;i++){
                for (Annotation anno: pa[i]) {
                    if(anno instanceof CCRequestParam){
                        String paramName = ((CCRequestParam)anno).value();
                        if(!"".equals(paramName.trim())){
                            paramMapping.put(paramName,i);
                        }
                    }
                }
            }
            //接下来处理非命名参数 Request和Response
            Class<?>[] paramsTypes = handlerMapping.getMethod().getParameterTypes();
            for(int i = 0;i<paramsTypes.length;i++){
                Class<?> type = paramsTypes[i];
                if(type == HttpServletRequest.class||type==HttpServletResponse.class){
                    paramMapping.put(type.getName(),i);
                }
            }
            this.handlerAdapters.put(handlerMapping,new CCHandlerAdapter(paramMapping));
        }
    }

    //将Controller中配置的RequestMapping和Method进行一一对应
    private void initHandlerMappings(CCApplicationContext context) {
        //首先从容器中取到所有的实例
        String[] beanNames = context.getBeanDefinitionNames();
        for (String beanName: beanNames) {
            Object controller = context.getBean(beanName);
            if(controller==null){continue;}
            Class<?> clazz = controller.getClass();
            if(!clazz.isAnnotationPresent(CCController.class)){
                continue;
            }
            String baseUrl = "";
            if(clazz.isAnnotationPresent(CCRequestMapping.class)){
                CCRequestMapping requestMapping = clazz.getAnnotation(CCRequestMapping.class);
                baseUrl = requestMapping.value();
            }
            //扫描所有的public方法
            Method[] methods = clazz.getMethods();
            for (Method method:methods){
                if(!method.isAnnotationPresent(CCRequestMapping.class)){continue;}
                CCRequestMapping requestMapping = method.getAnnotation(CCRequestMapping.class);
                String regex = ("/" + baseUrl + requestMapping.value().replaceAll("/+",""));
                Pattern pattern = Pattern.compile(regex);
                this.handlerMappings.add(new CCHandlerMapping(pattern,controller,method));
                System.out.println("Mapping : "+regex+","+method);
            }
        }
    }

    private void initThemeResolver(CCApplicationContext context) {
    }

    private void initLocaleResolver(CCApplicationContext context) {
    }

    private void initMultipartResolver(CCApplicationContext context) {
    }
}
