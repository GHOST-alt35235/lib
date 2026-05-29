package cn.edu.wynu.aspect;

import cn.edu.wynu.model.entity.OperationLog;
import cn.edu.wynu.mapper.OperationLogMapper;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class OperationLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(OperationLogAspect.class);

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Around("execution(* cn.edu.wynu.controller.*.*(..))")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        OperationLog log = new OperationLog();
        
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

            try {
                if (StpUtil.isLogin()) {
                    log.setUserId((Integer) StpUtil.getLoginId());
                    log.setUsername(StpUtil.getLoginIdAsString());
                }
            } catch (Exception e) {
                logger.warn("获取登录信息失败: {}", e.getMessage());
            }

            if (request != null) {
                log.setMethod(request.getMethod());
                log.setUrl(request.getRequestURI());
                log.setIp(request.getRemoteAddr());
            }

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            log.setOperation(signature.getName());

            try {
                Object[] args = joinPoint.getArgs();
                String[] paramNames = signature.getParameterNames();
                Map<String, Object> params = new HashMap<>();
                for (int i = 0; i < paramNames.length; i++) {
                    params.put(paramNames[i], args[i]);
                }
                log.setParams(params.toString());
            } catch (Exception e) {
                logger.warn("获取参数失败: {}", e.getMessage());
            }

            Object result = joinPoint.proceed();
            
            try {
                log.setResult(result != null ? result.toString().length() > 500 ? result.toString().substring(0, 500) : result.toString() : "null");
            } catch (Exception e) {
                log.setResult("获取结果失败");
            }
            log.setStatus(1);
            return result;
            
        } catch (Exception e) {
            log.setStatus(0);
            log.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            try {
                log.setCreateTime(LocalDateTime.now());
                operationLogMapper.insert(log);
            } catch (Exception e) {
                logger.error("记录操作日志失败: {}", e.getMessage());
            }
        }
    }
}
