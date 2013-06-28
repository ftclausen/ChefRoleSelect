package com.blackboard.chef.api;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ChefRoleSelectController extends HttpServlet {
    public void doPost(HttpServletRequest request,
                    HttpServletResponse response) throws IOException, ServletException {

        // Valid environment regex
        Pattern validEnvPattern = Pattern.compile("^(ap|fg)[a-z]+-\\w+-\\w+$");
        String environment = request.getParameter("environment");


        if (environment != null) {
            System.out.println("DEBUG: Received environment : " + environment);
            Matcher matcher = validEnvPattern.matcher(environment);
            if (matcher.find()) {
                System.out.println("DEBUG: Valid environment : " + environment);
                request.setAttribute("result", "OK");
            } else {
                System.out.println("ERROR: Invalid environment format for : " + environment);
                request.setAttribute("result", "Invalid environment specification");
            }

        } else {
            System.out.println("ERROR: Received null environment");
            // Make errors enum
            request.setAttribute("result", "Request cannot be null");
        }

        // Instantiate API model here

        RequestDispatcher view = request.getRequestDispatcher("result.jsp");
        view.forward(request, response);
    }
    
    public void doGet(HttpServletRequest request, 
                    HttpServletResponse response) throws IOException, ServletException {
        System.out.println("DEBUG: GET request received");
        request.setAttribute("result", "This page is not meant to be called directly");
        RequestDispatcher view = request.getRequestDispatcher("result.jsp");
        view.forward(request, response);
    }
}
