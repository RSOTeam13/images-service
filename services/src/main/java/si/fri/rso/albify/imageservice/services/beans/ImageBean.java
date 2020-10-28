package si.fri.rso.albify.imageservice.services.beans;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.mongodb.BasicDBObject;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import si.fri.rso.albify.imageservice.lib.Image;
import si.fri.rso.albify.imageservice.models.converters.ImageConverter;
import si.fri.rso.albify.imageservice.models.entities.ImageEntity;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@RequestScoped
public class ImageBean {

    private Logger log = Logger.getLogger(ImageBean.class.getName());

    private CodecRegistry pojoCodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

    private MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString("mongodb://localhost:27017/rso"))
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
                    .find(query)
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
    public ImageEntity removeAlbum(String imageId) {
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

}
