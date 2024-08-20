package hello.exception.resolver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hello.exception.exception.UserException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;

@Slf4j
public class UserHandlerExceptionResolver implements HandlerExceptionResolver {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        
        log.info("UserExceptionResolver 실행");

        try {

            if (ex instanceof UserException) {
                String acceptHeader = request.getHeader("accept");

                log.info("acceptHeader = {} {}", acceptHeader, MediaType.APPLICATION_JSON_VALUE);

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                if (MediaType.APPLICATION_JSON_VALUE.equals(acceptHeader)) {
                    HashMap<String, Object> errorResult = new HashMap<>();

                    errorResult.put("ex", ex.getClass());
                    errorResult.put("ex message", ex.getMessage());

                    String result = objectMapper.writeValueAsString(errorResult);

                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("utf-8");
                    response.getWriter().println(result);

                    // content-type: text/html
                    return new ModelAndView();
                }

                return new ModelAndView("error/4xx");
            }
        } catch (IOException e) {
            log.error("resolver ex", e);
        }

        return null;
    }
}
