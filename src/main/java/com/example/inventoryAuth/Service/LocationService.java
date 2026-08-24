package com.example.inventoryAuth.Service;

import com.example.inventoryAuth.Entity.Location;
import com.example.inventoryAuth.Repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    public Location create(Location location) {
        location.setId(null);
        if (location.getActive() == null) {
            location.setActive(true);
        }
        return locationRepository.save(location);
    }

    public List<Location> getAll() {
        return locationRepository.findAll();
    }

    public List<Location> getActive() {
        return locationRepository.findByActiveTrue();
    }

    public Location update(Long id, Location update) {
        Location existing = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found"));
        existing.setCode(update.getCode());
        existing.setName(update.getName());
        return locationRepository.save(existing);
    }

    public Location setActive(Long id, boolean active) {
        Location existing = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found"));
        existing.setActive(active);
        return locationRepository.save(existing);
    }
}
