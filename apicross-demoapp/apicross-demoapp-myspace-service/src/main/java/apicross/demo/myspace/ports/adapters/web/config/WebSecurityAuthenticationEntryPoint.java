package apicross.demo.myspace.ports.adapters.web.config;

import apicross.demo.common.models.ProblemDescription;
import apicross.demo.myspace.ports.adapters.web.ExceptionsHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class WebSecurityAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Autowired
    public WebSecurityAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx)
            throws IOException {
        response.addHeader("WWW-Authenticate", "Basic realm=\"" + getRealmName() + "\"");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(ExceptionsHandler.APPLICATION_PROBLEM_JSON_CHARSET_UTF_8);
        ProblemDescription problemDescription = new ProblemDescription()
                .withHttpStatus(HttpServletResponse.SC_UNAUTHORIZED)
                .withHttpMethod(request.getMethod())
                .withInstance(request.getRequestURI())
                .withType("https://api.myapp.com/problems/authentication-token-required")
                .withTitle("Authentication token required")
                .withMessage("No authentication information provided with HTTP request. Use HTTP Basic authentication.");
        PrintWriter writer = response.getWriter();
        writer.println(objectMapper.writeValueAsString(problemDescription));
    }

    @Override
    public void afterPropertiesSet() {
        setRealmName("DemoApp");
        super.afterPropertiesSet();
    }
}

