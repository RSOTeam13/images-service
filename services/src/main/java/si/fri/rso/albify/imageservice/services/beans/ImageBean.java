package si.fri.rso.albify.imageservice.services.beans;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.mongodb.BasicDBObject;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.glassfish.jersey.server.ContainerRequest;
import si.fri.rso.albify.imageservice.lib.Image;
import si.fri.rso.albify.imageservice.models.converters.ImageConverter;
import si.fri.rso.albify.imageservice.models.entities.ImageEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.*;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@RequestScoped
public class ImageBean {

    @Inject
    ContainerRequest request;

    private Logger log = Logger.getLogger(ImageBean.class.getName());

    private CodecRegistry pojoCodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

    private MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(System.getenv("DB_URL")))
                .codecRegistry(pojoCodecRegistry)
                .build();


    private MongoClient mongoClient = MongoClients.create(settings);
    private MongoDatabase database = mongoClient.getDatabase("albify");
    private MongoCollection<ImageEntity> imagesCollection = database.getCollection("images", ImageEntity.class);

    /**
     * Returns image by its ID.
     * @param imageId Image ID.
     * @return Image entity.
     */
    public ImageEntity getImage(String imageId) {
        try {
            ImageEntity entity = imagesCollection.find(eq("_id", new ObjectId(imageId))).first();
            if (entity != null && entity.getId() != null) {
                return entity;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns list of images.
     * @param uriInfo Filtering parameters.
     * @return List of images.
     */
    public List<Image> getImages(UriInfo uriInfo, List<ObjectId> filterIds) {
        String userId = request.getProperty("userId").toString();
        BasicDBObject query = new BasicDBObject();
        if (!filterIds.isEmpty()) {
            query = new BasicDBObject("_id", new BasicDBObject("$in", filterIds));
        }

        QueryParameters queryParameters = QueryParameters.query(uriInfo
                .getRequestUri()
                .getQuery())
                .defaultOffset(0)
                .defaultLimit(100)
                .maxLimit(100)
                .build();

        try {
            return imagesCollection
                    .find(
                            and(
                                    eq("ownerId", new ObjectId(userId)),
                                    query
                            )
                    )
                    .limit(queryParameters.getLimit().intValue())
                    .skip(queryParameters.getOffset().intValue())
                    .into(new ArrayList<>())
                    .stream()
                    .map(ImageConverter::toDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns list of visible images you don't own.
     * @return List of images.
     */
    public List<Image> getImagesVisible(UriInfo uriInfo) {
        String userId = request.getProperty("userId").toString();
        Bson query = eq("visible", true);

        QueryParameters queryParameters = QueryParameters.query(uriInfo
                .getRequestUri()
                .getQuery())
                .defaultOffset(0)
                .defaultLimit(100)
                .maxLimit(100)
                .build();

        try {
            return imagesCollection
                    .find(
                            and(
                                    ne("ownerId", new ObjectId(userId)),
                                    query
                            )
                    )
                    .limit(queryParameters.getLimit().intValue())
                    .skip(queryParameters.getOffset().intValue())
                    .into(new ArrayList<>())
                    .stream()
                    .map(ImageConverter::toDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Counts visible images in DB.
     * @return Images count.
     */
    public long getImagesCountVisible() {
        String userId = request.getProperty("userId").toString();
        Bson query = and(
                ne("ownerId", new ObjectId(userId)),
                eq("visible", true)
        );

        try {
            return imagesCollection.countDocuments(query);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Counts images in DB.
     * @return Images count.
     */
    public long getImagesCount(List<ObjectId> filterIds) {
        BasicDBObject query = new BasicDBObject();
        if (!filterIds.isEmpty()) {
            query = new BasicDBObject("_id", new BasicDBObject("$in", filterIds));
        }

        try {
            return imagesCollection.countDocuments(query);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Removes image.
     * @param imageId Image ID.
     * @return Deleted image.
     */
    public ImageEntity removeImage(String imageId) {
        try {
            ImageEntity entity = imagesCollection.findOneAndDelete(eq("_id", new ObjectId(imageId)));
            if (entity != null && entity.getId() != null) {
                return entity;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates image is visibility.
     * @param imageId Image ID.
     * @param visible Whether the image is to be made visible or not.
     * @return Updated album.
     */
    public ImageEntity updateVisibility(String imageId, Boolean visible) {
        try {
            ImageEntity entity = imagesCollection.findOneAndUpdate(
                    eq("_id", new ObjectId(imageId)),
                    new BasicDBObject("$set", new BasicDBObject("visible", visible)),
                    new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
            );

            if (entity != null && entity.getId() != null) {
                return entity;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
