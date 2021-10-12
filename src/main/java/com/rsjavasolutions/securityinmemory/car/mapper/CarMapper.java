package com.rsjavasolutions.securityinmemory.car.mapper;


import com.rsjavasolutions.securityinmemory.car.CarEntity;
import com.rsjavasolutions.securityinmemory.car.request.CarRequest;
import com.rsjavasolutions.securityinmemory.car.response.CarResponse;

public class CarMapper {

    public static CarEntity mapToEntity(CarRequest request) {
        return new CarEntity(
                request.getBrand(),
                request.getModel(),
                request.getYear(),
                request.getPrice()
        );
    }

    public static CarResponse mapToResponse(CarEntity entity) {
        return new CarResponse(
                entity.getUuid(),
                entity.getId(),
                entity.getBrand(),
                entity.getModel(),
                entity.getYear(),
                entity.getPrice(),
                entity.getCreationDateTime());
    }
}
