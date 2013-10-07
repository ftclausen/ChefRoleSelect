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

import org.jclouds.ContextBuilder;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.Apis;
import org.jclouds.chef.ChefApiMetadata;
import org.jclouds.chef.ChefApi;
import org.jclouds.chef.ChefContext;
import org.jclouds.chef.ChefService;
import org.jclouds.chef.config.ChefProperties;
import org.jclouds.chef.util.RunListBuilder;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.RunScriptOnNodesException;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.chef.domain.Node;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.Providers;
import org.jclouds.sshj.config.SshjSshClientModule;

import com.google.common.io.Files;
import static com.google.common.base.Charsets.UTF_8;

import java.io.*;
import java.util.*;

class ChefRoleSelectModel {
    private String client = null;
    private String pemFile = null;
    private String credential = null;
    private ChefContext context = null;
    private Properties props = null;

    public void ChefRoleSelectModel() {
    }

    public void setProps(Properties props) {
        this.props = props;
    }

    private ChefApi getChefServer(String server, String user) throws IOException {
            System.out.println("DEBUG: Entering getChefServer");
            String client = user;
            String homeDir = System.getProperty("user.home");
            String pemFile = homeDir + "/.chef/" + user + ".pem";
            String credential = Files.toString(new File(pemFile), UTF_8);
            // Example below
            // context = ContextBuilder.newBuilder("chef")
            //     .endpoint("https://chef.mhint")
            //     .credentials(client, credential)
            //     .buildView(ChefContext.class);
            ChefContext context = ContextBuilder.newBuilder("chef")
                    .endpoint(server)
                    .credentials(client, credential)
                    .buildView(ChefContext.class);
            ChefApi api = context.getApi(ChefApi.class);
            return(api);
    }

    public void addRoleToNode(String nodeName, String role, String addedBy) throws IllegalArgumentException, IOException {
            String username = addedBy;
            String server = props.getProperty("chef_url"); 
            String user  = props.getProperty("user_name");
            String supportedRoles = props.getProperty("supported_roles");
            ChefApi api = getChefServer(server, user);

            if (addedBy == null) {
                username = "NOT_SENT";
            }

            if (props == null) {
                throw new IllegalArgumentException("Internal error - no Chef server properties specified");
            }

            if (role == null) {
                throw new IllegalArgumentException("Role name cannot be null");
            }

            if (nodeName == null) {
                throw new IllegalArgumentException("Node name cannot be null");
            }

            System.out.println("DEBUG: About to add role " + role + " to node " + nodeName);
            // Update node run list - example from http://goo.gl/EDBP5
            Node node = api.getNode(nodeName);
            List<String> runList = node.getRunList();
            List<String> newRunList = new ArrayList<String>(runList);
            for (String runListItem : runList) {
                System.out.println("DEBUG: Run list item : " + runListItem);
            }

            System.out.println("INFO: User " + username + " added run list item role[" + role + "]");
            newRunList.add("role[" + role + "]");

            // Since nodes are immutable we will create a copy and update with that
            Node updated = new Node(node.getName(), 
                                    node.getNormal(),
                                    node.getOverride(),
                                    node.getDefault(),
                                    node.getAutomatic(),
                                    newRunList,
                                    node.getChefEnvironment());
    
            api.updateNode(updated);
    }

}
