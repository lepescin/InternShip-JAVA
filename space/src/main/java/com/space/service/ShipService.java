package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;

import java.util.Date;
import java.util.List;

public interface ShipService {
    Ship getById(Long id);
    Ship save(Ship ship);
    void delete(Long id);

    List<Ship> getAll(String name,
                      String planet,
                      ShipType shipType,
                      Long after,
                      Long before,
                      Boolean isUsed,
                      Double minSpeed,
                      Double maxSpeed,
                      Integer minCrewSize,
                      Integer maxCrewSize,
                      Double minRating,
                      Double maxRating);

    Double computeRating(double speed, boolean isUsed, Date prod);

    boolean isStringValid(String string);

    boolean isCrewSizeValid(Integer crewSize);

    boolean isSpeedValid(Double speed);

    boolean isProdDateValid(Date prodDate);


    boolean isShipValid(Ship ship);

    List<Ship> sortShips(List<Ship> ships, ShipOrder order);

    List<Ship> getPage(List<Ship> ships, Integer pageNumber, Integer pageSize);

    public boolean isShipTypeValid(ShipType shipType);
    public Ship updateShip(Ship oldShip, Ship newShip);
}
