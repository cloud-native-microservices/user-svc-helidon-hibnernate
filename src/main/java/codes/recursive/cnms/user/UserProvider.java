/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package codes.recursive.cnms.user;

import java.util.concurrent.atomic.AtomicReference;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Provider for user config.
 */
@ApplicationScoped
public class UserProvider {
    private final AtomicReference<String> dbUser = new AtomicReference<>();
    private final AtomicReference<String> dbPassword = new AtomicReference<>();
    private final AtomicReference<String> dbUrl = new AtomicReference<>();

    /**
     * Create a new user provider, reading the message from configuration.
     *
     * @param dbUser
     * @param dbPassword
     * @param dbUrl
     */
    @Inject
    public UserProvider(
            @ConfigProperty(name = "datasource.username") String dbUser,
            @ConfigProperty(name = "datasource.password") String dbPassword,
            @ConfigProperty(name = "datasource.url") String dbUrl
    ) {
        this.dbUser.set(dbUser);
        this.dbPassword.set(dbPassword);
        this.dbUrl.set(dbUrl);
    }

    String getDbUser() {
        return dbUser.get();
    }
    String getDbPassword() { return dbPassword.get(); }
    String getDbUrl() { return dbUrl.get(); }

    void setDbUser(String dbUser) {
        this.dbUser.set(dbUser);
    }
    void setDbPassword(String dbPassword) { this.dbPassword.set(dbPassword); }
    void setDbUrl(String dbUrl) { this.dbUrl.set(dbUrl); }
}
