package si.fri.rso.albify.imageservice.models.converters;

import si.fri.rso.albify.imageservice.lib.Image;
import si.fri.rso.albify.imageservice.models.entities.ImageEntity;

public class ImageConverter {

    public static Image toDto(ImageEntity entity) {

        Image dto = new Image();
        dto.setId(entity.getId().toString());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setOwnerId(entity.getOwnerId().toString());
        dto.setUrl(entity.getUrl());
        dto.setTags(entity.getTags().toArray(new String[0]));
        dto.setVisible(entity.getVisible());

        return dto;

    }

    public static ImageEntity toEntity(Image dto) {

        ImageEntity entity = new ImageEntity();
        return entity;

    }

}
