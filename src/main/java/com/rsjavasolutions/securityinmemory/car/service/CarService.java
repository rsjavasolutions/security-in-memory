package com.rsjavasolutions.securityinmemory.car.service;


import com.rsjavasolutions.securityinmemory.car.CarEntity;
import com.rsjavasolutions.securityinmemory.car.CarRepository;
import com.rsjavasolutions.securityinmemory.car.exception.CarNotFoundException;
import com.rsjavasolutions.securityinmemory.car.mapper.CarMapper;
import com.rsjavasolutions.securityinmemory.car.request.CarRequest;
import com.rsjavasolutions.securityinmemory.car.response.CarResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.rsjavasolutions.securityinmemory.car.mapper.CarMapper.mapToEntity;
import static com.rsjavasolutions.securityinmemory.car.mapper.CarMapper.mapToResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;

    @Transactional
    public List<CarResponse> getCars() {

        return carRepository.findAll()
             .stream()
                .map(CarMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CarResponse getCar(String uuid) {
        CarEntity carEntity = carRepository.findByUuid(uuid).orElseThrow(() -> new CarNotFoundException(uuid));
        return mapToResponse(carEntity);
    }

    @Transactional
    public String saveCar(CarRequest request) {
        log.debug("Save car request with params: {}", request);

        return carRepository.save(mapToEntity(request)).getUuid();
    }

    @Transactional
    public CarResponse updateCar(String uuid, CarRequest request) {
        CarEntity carEntity = carRepository.findByUuid(uuid).orElseThrow(() -> new CarNotFoundException(uuid));

        carEntity.setBrand(request.getBrand());
        carEntity.setModel(request.getModel());

        return mapToResponse(carEntity);
    }

    @Transactional
    public void deleteCar(String uuid) {
        carRepository.deleteByUuid(uuid);
    }
}
