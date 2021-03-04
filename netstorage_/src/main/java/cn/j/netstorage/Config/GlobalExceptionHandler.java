package cn.j.netstorage.Config;

import cn.j.netstorage.tool.ResultBuilder;
import cn.j.netstorage.tool.StatusCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResultBuilder<Boolean> validationErrorHandler(MethodArgumentNotValidException ex) throws JsonProcessingException {
        //1.此处先获取BindingResult
        BindingResult bindingResult = ex.getBindingResult();
        //2.获取错误信息
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        //3.组装异常信息
        Map<String,String> message = new HashMap<>();
        for (FieldError fieldError : fieldErrors) {
            message.put(fieldError.getField(),fieldError.getDefaultMessage());
        }
        //4.将map转换为JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(message);
        //5.返回错误信息
        return new ResultBuilder<Boolean>(StatusCode.REQUEST_PARAM_ERROR);
    }

    @ExceptionHandler(UnauthenticatedException.class)
    public String validationErrorHandler(UnauthenticatedException ex){
        return "redirect:/User/login";
    }
}
