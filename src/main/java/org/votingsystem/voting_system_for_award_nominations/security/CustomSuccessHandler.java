package org.votingsystem.voting_system_for_award_nominations.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        String role = authentication.getAuthorities().iterator().next().getAuthority();

        switch (role) {
            case "ROLE_USER":
                response.sendRedirect("/userdashboard");
                break;
            case "ROLE_COO":
                response.sendRedirect("/admin/dashboard?type=coo");
                break;
            case "ROLE_HR":
                response.sendRedirect("/admin/dashboard?type=hr");
                break;
            case "ROLE_EM":
                response.sendRedirect("/admin/dashboard?type=em");
                break;
            case "ROLE_PRO":
                response.sendRedirect("/admin/dashboard?type=pro");
                break;
            case "ROLE_FM":
                response.sendRedirect("/admin/dashboard?type=fm");
                break;
            default:
                response.sendRedirect("/");
        }
    }
}
