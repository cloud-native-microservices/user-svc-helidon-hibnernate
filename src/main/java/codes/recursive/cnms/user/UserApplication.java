package codes.recursive.cnms.user;

import org.glassfish.jersey.jackson.JacksonFeature;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Set;

/**
 * Simple Application that managers users.
 */
@ApplicationScoped
@ApplicationPath("/")
public class UserApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(
                UserResource.class,
                JacksonFeature.class
        );
    }

}