package si.fri.rso.albify.imageservice.models.entities;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;

public class ImageEntity {

    private ObjectId id;
    private Date createdAt;
    private ObjectId ownerId;
    private String url;
    private ArrayList<String> tags;
    private Boolean visible;

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

    public ObjectId getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(ObjectId ownerId) {
        this.ownerId = ownerId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean id) {
        this.visible = visible;
    }
}