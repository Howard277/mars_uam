package com.mars.mars_uam.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mars.mars_uam.constant.AllConstant;
import com.mars.mars_uam.entity.Resource;
import org.springframework.stereotype.Component;

/**
 * 自定义验证过滤器
 *
 * @author wuketao
 */
@Component
public class AuthenticationFilter implements Filter {

    /**
     * 应用程序上下文路径
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 无需处理
    }

    /**
     * 通过session检查，判断用户是否登录。
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest hsRequest = (HttpServletRequest) request;
        HttpServletResponse hsResponse = (HttpServletResponse) response;

        //获取目标地址
        String requestUrl = hsRequest.getRequestURL().toString();
        String requestUri = hsRequest.getRequestURI();

        HttpSession session = hsRequest.getSession(true);

        //访问控制
        if ("/".equals(requestUri) || requestUri.startsWith("/login")
                || requestUri.contains(".") || requestUri.equals("/swagger-ui.html")
                || requestUri.startsWith("/api/") || requestUri.startsWith("/health")) {
            //不需要权限验证
        } else {
            //页面请求
            if ((null == session)
                    || (null == session.getAttribute(AllConstant.USERKEY))
                    || ((((List<Resource>) session.getAttribute(AllConstant.UAM_RESOURCE_LIST)).stream().filter(r -> r.getResourcePath().startsWith(requestUri)).count() != 0)
                    && (((List<Resource>) session.getAttribute(AllConstant.UAM_COMMISSION_RESOURCE_LIST)).stream().filter(r -> r.getResourcePath().startsWith(requestUri)).count() == 0))) {
                // 三种情况：1、没有session  2、session中没有user信息  3、访问的资源在受控资源列表中但不在授权列表中
                //重定向到登录界面
                hsResponse.sendRedirect(AllConstant.URIROOTPATH + "?target=" + requestUrl);
                return;
            }
        }

        //通过过滤的url，继续执行。
        chain.doFilter(hsRequest, hsResponse);
    }

    @Override
    public void destroy() {
        // 无需处理
    }
}
