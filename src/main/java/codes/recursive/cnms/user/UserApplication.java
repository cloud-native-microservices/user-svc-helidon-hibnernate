package codes.recursive.cnms.user;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import io.helidon.common.CollectionsHelper;
import org.glassfish.jersey.jackson.JacksonFeature;

/**
 * Simple Application that managers users.
 */
@ApplicationScoped
@ApplicationPath("/")
public class UserApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return CollectionsHelper.setOf(
                UserResource.class,
                JacksonFeature.class
        );
    }

}