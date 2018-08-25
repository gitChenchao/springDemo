package com.cc.spring.formework.webmvc.servlet;

import com.cc.spring.formework.context.CCApplicationContext;
import com.cc.spring.formework.webmvc.CCHandlerAdapters;
import com.cc.spring.formework.webmvc.CCHandlerMapping;
import com.cc.spring.formework.webmvc.CCModelAndView;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
//servlet作为MVC启动入口
public class CCDispatcherServlet extends HttpServlet {

    private final String LOCATION = "contextConfigLocation";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatcher(req, resp);
    }

    private void doDispatcher(HttpServletRequest req, HttpServletResponse resp) {
        CCHandlerMapping handler = getHandler(req);

        CCHandlerAdapters ha = getHandlerAdapter(handler);

        CCModelAndView mv = ha.handle(req,resp,handler);

        processDispatchResult(resp,mv);
    }

    private void processDispatchResult(HttpServletResponse resp, CCModelAndView mv) {
    }

    private CCHandlerAdapters getHandlerAdapter(CCHandlerMapping handler) {
        return null;
    }

    private CCHandlerMapping getHandler(HttpServletRequest req) {
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
    }

    private void initRequestToViewNameTranslator(CCApplicationContext context) {
    }

    private void initHandlerExceptionResolvers(CCApplicationContext context) {
    }

    private void initHandlerAdapters(CCApplicationContext context) {
    }

    private void initHandlerMappings(CCApplicationContext context) {
    }

    private void initThemeResolver(CCApplicationContext context) {
    }

    private void initLocaleResolver(CCApplicationContext context) {
    }

    private void initMultipartResolver(CCApplicationContext context) {
    }
}
