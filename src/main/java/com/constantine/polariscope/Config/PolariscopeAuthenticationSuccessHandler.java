package com.constantine.polariscope.Config;

import com.constantine.polariscope.Comprehension.ReportGenerator;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class PolariscopeAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final ReportGenerator reportGenerator;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws ServletException, IOException {

        reportGenerator.generateReport();

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
