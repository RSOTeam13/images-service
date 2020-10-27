package si.fri.rso.albify.imageservice.models.converters;

import si.fri.rso.albify.imageservice.lib.Image;
import si.fri.rso.albify.imageservice.models.entities.ImageEntity;

import java.util.ArrayList;
import java.util.List;

public class ImageConverter {

    public static Image toDto(ImageEntity entity) {

        Image dto = new Image();
        dto.setId(entity.getId().toString());
        dto.setCreatedAt(entity.getCreatedAt());

        return dto;

    }

    public static ImageEntity toEntity(Image dto) {

        ImageEntity entity = new ImageEntity();
        return entity;

    }

}
