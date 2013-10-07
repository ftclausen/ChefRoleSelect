/*
 *  Copyright 2013 Friedrich Clausen <friedrich.clausen@blackboard.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
*/

/*
 * Author: Friedrich "Fred" Clausen
 *
 * Simple service to allow unpriviged users to add certain
 * restrived roles to nodes in Chef. Basic checks are done for the following
 *  * Is this a valid role?
 *  * Are all the required params present?
 *  * Is the Chef server reachable?
 *
 *  
 *
 */

package com.blackboard.chef.api;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

public class ChefRoleSelectController extends HttpServlet {
    public void init() {
        System.out.println("DEBUG: Loading properties file /etc/ChefRoleSelect.properties ...");
        String config = "/etc/ChefRoleSelect.properties";
        Properties props = new Properties();
        try {
            FileInputStream propsFile = new FileInputStream(config);
            props.load(propsFile);
        } catch (IOException e) {
            System.out.println("WARNING: Unable to open properties file (" + e.getMessage() + ") - using defaults");
            props.setProperty("chef_url", "https://localhost/");
            props.setProperty("user_name", "admin");
            props.setProperty("supported_roles", "");
        }
        System.out.println("INFO: Summary of configuration options follows");
        for (String property : props.stringPropertyNames()) {
            String value = props.getProperty(property);
            System.out.println("INFO: " + property + " => " + value);
        }
        getServletConfig().getServletContext().setAttribute("props", props);
    }

    public void doPost(HttpServletRequest request,
                    HttpServletResponse response) throws IOException, ServletException {

        String node = request.getParameter("node");
        String role = request.getParameter("role");
        boolean nodeOK = false;
        boolean roleOK = false;


        if (node == null) {
            System.out.println("ERROR: Received null node");
            // Make errors enum
            request.setAttribute("result", "Node cannot be null");
        } else {
            nodeOK = true;
        }

        if (role == null) {
            System.out.println("ERROR: Received null role");
            request.setAttribute("result", "Role cannot be null");
        } else {
            // List of "approved" roles to prevent run list breakage
            // TODO: Finish property file work - source approved roles form 
            // there.
            if (role.equals("test") || role.equals ("net") || 
                role.equals("shibboleth-sp") ||
                role.equals("learn-webserver")) {
                roleOK = true;
            } else {
                System.out.println("ERROR: Received unsupported role");
                request.setAttribute("result", "Received unsupported role");
            }
    }
            

        if (roleOK == true && nodeOK == true) {
            ChefRoleSelectModel roleSelect = new ChefRoleSelectModel();
            roleSelect.setProps((Properties) getServletConfig()
                                            .getServletContext()
                                            .getAttribute("props"));
            try {
                roleSelect.addRoleToNode(node, role, (String) request.getParameter("addedBy"));
                request.setAttribute("result", "OK");
            } catch (IOException e) {
                System.out.println("ERROR: Cannot connect to Chef server : " + e.getMessage());
                request.setAttribute("result", "Chef Connection Error");
            } catch (IllegalArgumentException e) {
                System.out.println("ERROR: " + e.getMessage());
                request.setAttribute("result", e.getMessage());
            } catch (NullPointerException e) {
                System.out.println("ERROR: Bug alert! NPE! " + e.getMessage());
                request.setAttribute("result", "general error - invalid node?");
            }
        }

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
