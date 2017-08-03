package org.openfact.models.es.mapper;

import javax.enterprise.util.AnnotationLiteral;

public class MapperTypeLiteral extends AnnotationLiteral<MapperType> implements MapperType {

    private final String value;

    public MapperTypeLiteral(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }

}