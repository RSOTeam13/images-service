package si.fri.rso.albify.imageservice.api.v1.resources;

import org.bson.types.ObjectId;
import si.fri.rso.albify.imageservice.lib.Image;
import si.fri.rso.albify.imageservice.models.converters.ImageConverter;
import si.fri.rso.albify.imageservice.models.entities.ImageEntity;
import si.fri.rso.albify.imageservice.services.beans.ImageBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
@Path("/images")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ImageResource {

    private Logger log = Logger.getLogger(ImageResource.class.getName());

    @Inject
    private ImageBean imageBean;

    @Context
    protected UriInfo uriInfo;

    @GET
    @Path("/{imageId}")
    public Response getImage(@PathParam("imageId") String imageId) {
        if (!ObjectId.isValid(imageId)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        ImageEntity entity = imageBean.getImage(imageId);
        if (entity == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(ImageConverter.toDto(entity)).build();
    }

    @GET
    public Response getImages() {
        List<Image> images = imageBean.getImages(uriInfo);
        long count = imageBean.getImagesCount();

        return Response.status(Response.Status.OK)
                .entity(images)
                .header("X-Total-Count", count)
                .build();
    }

    @DELETE
    @Path("/{imageId}")
    public Response removeAlbum(@PathParam("imageId") String imageId) {
        if (!ObjectId.isValid(imageId)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        ImageEntity entity = imageBean.removeAlbum(imageId);
        if (entity == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(ImageConverter.toDto(entity)).build();
    }

}