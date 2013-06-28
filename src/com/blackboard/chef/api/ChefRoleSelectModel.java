package com.blackboard.chef.api;

import org.jclouds.ContextBuilder;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.Apis;
import org.jclouds.chef.ChefApiMetadata;
import org.jclouds.chef.ChefContext;
import org.jclouds.chef.ChefService;
import org.jclouds.chef.config.ChefProperties;
import org.jclouds.chef.util.RunListBuilder;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.RunScriptOnNodesException;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.Providers;
import org.jclouds.sshj.config.SshjSshClientModule;

import com.google.common.io.Files;
import static com.google.common.base.Charsets.UTF_8;

import java.io.*;

class ChefRoleSelectModel {
    private String client = null;
    private String pemFile = null;
    private String credential = null;
    private ChefContext context = null;

    public void ChefRoleSelectModel() throws IOException {
            String client = "fclausen";
            String pemFile = System.getProperty("user.home") + "/.chef/" + client + ".pem";
            String credential = Files.toString(new File(pemFile), UTF_8);
            context = ContextBuilder.newBuilder("chef")
                .endpoint("https://chef.mhint")
                .credentials(client, credential)
                .buildView(ChefContext.class);
    }

}
