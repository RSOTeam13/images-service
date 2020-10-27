package si.fri.rso.albify.imageservice.models.entities;

import org.bson.types.ObjectId;
import java.util.Date;

public class ImageEntity {

    private ObjectId id;
    private Date createdAt;

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

}