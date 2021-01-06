package si.fri.rso.albify.imageservice.api.v1;

import si.fri.rso.albify.imageservice.api.v1.resources.ImageResource;
import si.fri.rso.albify.imageservice.services.filters.AuthenticationFilter;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/v1")
public class ImageServiceApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> resources = new HashSet<Class<?>>();
        resources.add(ImageResource.class);
        resources.add(AuthenticationFilter.class);

        return resources;
    }

}
