package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ShipServiceImpl implements ShipService {
    @Autowired
    private final ShipRepository shipRepository;

    public ShipServiceImpl(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    @Override
    public Ship getById(Long id) {
        return shipRepository.findById(id).orElse(null);
    }

    @Override
    public Ship save(Ship ship) {
        return shipRepository.save(ship);
    }

    @Override
    public void delete(Long id) {
        shipRepository.deleteById(id);
    }

    @Override
    public List<Ship> getAll(
            String name,
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
            Double maxRating
    ) {
        final Date afterDate = after == null ? null : new Date(after);
        final Date beforeDate = before == null ? null : new Date(before);
        final List<Ship> ships = new ArrayList<>();
        shipRepository.findAll().forEach(ship -> {
            if (name != null && !ship.getName().contains(name)) return;
            if (planet != null && !ship.getPlanet().contains(planet)) return;
            if (shipType != null && ship.getShipType() != shipType) return;
            if (beforeDate != null && ship.getProdDate().after(beforeDate)) return;
            if (afterDate != null && ship.getProdDate().before(afterDate)) return;
            if (isUsed != null && ship.getUsed().booleanValue() != isUsed.booleanValue()) return;
            if (minSpeed != null && ship.getSpeed().compareTo(minSpeed) < 0) return;
            if (maxSpeed != null && ship.getSpeed().compareTo(maxSpeed) > 0) return;
            if (minCrewSize != null && ship.getCrewSize().compareTo(minCrewSize) < 0) return;
            if (maxCrewSize != null && ship.getCrewSize().compareTo(maxCrewSize) > 0) return;
            if (minRating != null && ship.getRating().compareTo(minRating) < 0) return;
            if (maxRating != null && ship.getRating().compareTo(maxRating) > 0) return;
            ships.add(ship);
        });
        return ships;
    }

    public Integer getYear(Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    @Override
    public Double computeRating(double speed, boolean isUsed, Date prod) {
        final double result = (80 * speed * (isUsed ? 0.5 : 1)) / (3019 - getYear(prod) + 1);
        return Math.round(result * 100) / 100D;
    }
    @Override
    public boolean isStringValid(String string) {
        return (!string.isEmpty()) && (string!=null) && (string.length()<=50);
    }

    @Override
    public boolean isProdDateValid(Date prodDate) {
        return (prodDate!=null) && (getYear(prodDate)>=2800) && (getYear(prodDate)<=3019);
    }

    @Override
    public boolean isCrewSizeValid(Integer crewSize) {
        return (crewSize!=null) && (crewSize>=1) && (crewSize<=9999);
    }
    @Override
    public boolean isSpeedValid(Double speed) {
        return (speed!=null) && (speed>=0.01d) && (speed<=0.99d);
    }

    @Override
    public boolean isShipValid(Ship ship) {
        return (ship!=null) && isSpeedValid(ship.getSpeed()) && isStringValid(ship.getName()) && isStringValid(ship.getPlanet()) && isProdDateValid(ship.getProdDate()) && isCrewSizeValid(ship.getCrewSize());
    }

    @Override
    public List<Ship> sortShips(List<Ship> ships, ShipOrder order) {
        if (order != null)
            ships.sort((ship1, ship2) -> {
                switch (order) {
                    case ID: return ship1.getId().compareTo(ship2.getId());
                    case SPEED: return ship1.getSpeed().compareTo(ship2.getSpeed());
                    case DATE: return ship1.getProdDate().compareTo(ship2.getProdDate());
                    case RATING: return ship1.getRating().compareTo(ship2.getRating());
                    default: return 0;
                }
            });
        return ships;
    }

    @Override
    public List<Ship> getPage(List<Ship> ships, Integer pageNumber, Integer pageSize) {
        final Integer page = pageNumber == null ? 0 : pageNumber;
        final Integer size = pageSize == null ? 3 : pageSize;
        final int from = page * size;
        int to = from + size;
        if (to > ships.size()) to = ships.size();
        return ships.subList(from, to);
    }

    @Override
    public Ship updateShip(Ship oldShip, Ship newShip) throws IllegalArgumentException {
        boolean needToChangeRating = false;

        final String name = newShip.getName();
        if (name != null) {
            if (isStringValid(name))
                oldShip.setName(name);
            else throw new IllegalArgumentException();
        }

        final String planet = newShip.getPlanet();
        if (planet != null) {
            if (isStringValid(planet))
                oldShip.setPlanet(planet);
            else throw new IllegalArgumentException();
        }

        final ShipType shipType = newShip.getShipType();
        if (shipType != null) {
            if (isShipTypeValid(shipType))
                oldShip.setShipType(shipType);
            else throw new IllegalArgumentException();
        }

        final Date prodDate = newShip.getProdDate();
        if (prodDate != null) {
            if (isProdDateValid(prodDate)) {
                oldShip.setProdDate(prodDate);
                needToChangeRating = true;
            }
            else throw new IllegalArgumentException();
        }

        if (newShip.getUsed() != null) {
            oldShip.setUsed(newShip.getUsed());
            needToChangeRating = true;
        }

        final Double speed = newShip.getSpeed();
        if (speed != null) {
            if (isSpeedValid(speed)) {
                oldShip.setSpeed(speed);
                needToChangeRating = true;
            }
            else throw new IllegalArgumentException();
        }

        final Integer crewSize = newShip.getCrewSize();
        if (crewSize != null) {
            if (isCrewSizeValid(crewSize))
                oldShip.setCrewSize(crewSize);
            else throw new IllegalArgumentException();
        }

        if (needToChangeRating)
            oldShip.setRating(computeRating(oldShip.getSpeed(), oldShip.getUsed(), oldShip.getProdDate()));

        shipRepository.save(oldShip);
        return oldShip;
    }
    @Override
    public boolean isShipTypeValid(ShipType shipType) {
        return shipType != null && Arrays.asList(ShipType.values()).contains(shipType);
    }


}
