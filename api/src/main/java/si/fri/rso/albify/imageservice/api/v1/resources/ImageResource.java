package si.fri.rso.albify.imageservice.api.v1.resources;

import com.kumuluz.ee.cors.annotations.CrossOrigin;
import com.kumuluz.ee.logs.cdi.Log;
import org.bson.types.ObjectId;
import org.glassfish.jersey.server.ContainerRequest;
import si.fri.rso.albify.imageservice.config.RestProperties;
import si.fri.rso.albify.imageservice.lib.Image;
import si.fri.rso.albify.imageservice.models.converters.ImageConverter;
import si.fri.rso.albify.imageservice.models.entities.ImageEntity;
import si.fri.rso.albify.imageservice.services.beans.ImageBean;
import si.fri.rso.albify.imageservice.services.filters.Authenticate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Log
@ApplicationScoped
@Path("/images")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@CrossOrigin(name = "images-resource")
public class ImageResource {

    private Logger log = Logger.getLogger(ImageResource.class.getName());

    @Inject
    private ImageBean imageBean;

    @Context
    protected UriInfo uriInfo;

    @Inject
    private RestProperties properties;

    @GET
    @Authenticate
    public Response getImages(@QueryParam("filterIds") List<String> filterIds, @DefaultValue("false") @QueryParam("forceFail") Boolean forceFail, @Context ContainerRequest request) throws Exception {
        List<ObjectId> parsedIds = new ArrayList<>();
        if (!filterIds.isEmpty()) {
            for (String id : filterIds) {
                if (!ObjectId.isValid(id)) {
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
                parsedIds.add(new ObjectId(id));
            }
        }

        /**

        for (Image img : images) {
            if (!img.getVisible() && !img.getOwnerId().toString().equals(request.getProperty("userId").toString())) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
        }
         **/
        List<Image> images = imageBean.getImages(uriInfo, parsedIds, forceFail);
        long count = imageBean.getImagesCount(parsedIds);

        return Response.status(Response.Status.OK)
                .entity(images)
                .header("X-Total-Count", count)
                .build();
    }

    @GET
    @Path("/visible")
    @Authenticate
    public Response getImagesVisible(@Context ContainerRequest request) {

        /**

         for (Image img : images) {
         if (!img.getVisible() && !img.getOwnerId().toString().equals(request.getProperty("userId").toString())) {
         return Response.status(Response.Status.FORBIDDEN).build();
         }
         }
         **/
        List<Image> images = imageBean.getImagesVisible(uriInfo);
        long count = imageBean.getImagesCountVisible();

        return Response.status(Response.Status.OK)
                .entity(images)
                .header("X-Total-Count", count)
                .build();
    }

    @GET
    @Path("/count")
    @Authenticate
    public Response getImagesCount(@QueryParam("filterIds") List<String> filterIds) {
        List<ObjectId> parsedIds = new ArrayList<>();
        if (!filterIds.isEmpty()) {
            for (String id : filterIds) {
                if (!ObjectId.isValid(id)) {
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
                parsedIds.add(new ObjectId(id));
            }
        }

        long count = imageBean.getImagesCount(parsedIds);
        return Response.status(Response.Status.OK)
                .entity(count)
                .header("X-Total-Count", count)
                .build();
    }

    @DELETE
    @Path("/{imageId}")
    @Authenticate
    public Response removeImage(@PathParam("imageId") String imageId) {
        if (!ObjectId.isValid(imageId)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        ImageEntity entity = imageBean.removeImage(imageId);
        if (entity == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(ImageConverter.toDto(entity)).build();
    }


    @GET
    @Path("/config")
    public Response getConfig() {

        String response =
                "{" +
                        "\"maintenanceMode\": %b" +
                        "}";

        response = String.format(
                response,
                properties.getMaintenanceMode()
        );

        return Response.ok(response).build();
    }

    @PUT
    @Path("/{imageId}/visibility")
    @Authenticate
    public Response addImageToAlbum(@PathParam("imageId") String imageId,  @DefaultValue("true") @QueryParam("visible") Boolean visible, @Context ContainerRequest request) throws Exception {
        if (!ObjectId.isValid(imageId)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        ImageEntity entity = imageBean.getImage(imageId, false, false);
        if (!entity.getOwnerId().toString().equals(request.getProperty("userId").toString())) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        if (entity == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        ImageEntity updatedEntity = imageBean.updateVisibility(imageId, visible);
        if (updatedEntity == null) {
            return Response.status(500, "There was a problem while adding image to album.").build();
        }
        return Response.status(Response.Status.OK).entity(ImageConverter.toDto(updatedEntity)).build();
    }

    @GET
    @Path("/{imageId}")
    @Authenticate
    public Response getImage(@PathParam("imageId") String imageId, @DefaultValue("false") @QueryParam("forceFail") Boolean forceFail,  @DefaultValue("false") @QueryParam("thirdFail") Boolean thirdFail, @Context ContainerRequest request) throws Exception {
        if (!ObjectId.isValid(imageId)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        ImageEntity entity = imageBean.getImage(imageId, forceFail, thirdFail);
        if ((entity.getVisible() == null || !entity.getVisible()) && !entity.getOwnerId().toString().equals(request.getProperty("userId").toString())) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        if (entity == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(ImageConverter.toDto(entity)).build();
    }

}
