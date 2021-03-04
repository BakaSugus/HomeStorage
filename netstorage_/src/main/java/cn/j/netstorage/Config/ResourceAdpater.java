package cn.j.netstorage.Config;

import cn.j.netstorage.Entity.File.HardDiskDevice;
import cn.j.netstorage.Entity.Type;
import cn.j.netstorage.Mapper.FilesVersionMapper;
import cn.j.netstorage.Mapper.HardDeviceMapper;
import cn.j.netstorage.Service.HardDeviceService;
import cn.j.netstorage.tool.FilesUtil;
import com.alibaba.druid.support.http.util.IPAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Component
public class ResourceAdpater implements WebMvcConfigurer {
    //自定义的拦截器

    @Autowired
    HardDeviceMapper hardDeviceMapper;
    @Autowired
    private HardDeviceService hardDeviceService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //加入播放记录
        InterceptorRegistration builder = registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                return true;
            }

            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

            }

            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

            }
        });

        List<HardDiskDevice> resource = hardDeviceMapper.findAll();
        resource.forEach(value -> {
            builder.addPathPatterns("/" + value.getCustomName() + "/**");
        });

        //检测内外网
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

                return true;
            }

            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

            }

            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

            }
        }).addPathPatterns("/**");
    }

    @Value("${ip}")
    private String ip;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        System.out.println(hardDeviceService.createDevice());

        hardDeviceMapper.findAll().forEach((value) -> {
            registry.addResourceHandler(String.format("%s/**", value.getCustomName())).addResourceLocations(String.format("file:%s/", value.getFolderName()));
        });
    }

}