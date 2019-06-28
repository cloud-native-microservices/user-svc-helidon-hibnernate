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

    String getDbUser() { return dbUser.get(); }
    String getDbPassword() { return dbPassword.get(); }
    String getDbUrl() { return dbUrl.get(); }

    void setDbUser(String dbUser) { this.dbUser.set(dbUser); }
    void setDbPassword(String dbPassword) { this.dbPassword.set(dbPassword); }
    void setDbUrl(String dbUrl) { this.dbUrl.set(dbUrl); }
}
