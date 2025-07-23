package com.app.config;

import org.casbin.adapter.JDBCAdapter;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.net.URL;

@Configuration
public class CasbinConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public Enforcer casbinEnforcer() throws Exception {
        URL modelConfig = getClass().getClassLoader().getResource("casbin-model.conf");
        if (modelConfig == null) {
            throw new IllegalStateException("casbin-model.conf not found in classpath!");
        }
        JDBCAdapter adapter = new JDBCAdapter(dataSource);
        Enforcer enforcer = new Enforcer(modelConfig.toURI().getPath(), adapter);
        enforcer.enableAutoSave(true);
        enforcer.loadPolicy();
        enforcer.getGroupingPolicy();
        return enforcer;
    }
}
