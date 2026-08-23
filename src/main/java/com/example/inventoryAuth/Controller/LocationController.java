package com.example.inventoryAuth.Controller;

import com.example.inventoryAuth.Entity.Location;
import com.example.inventoryAuth.Service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {

    @Autowired
    LocationService locationService;

    @PreAuthorize("hasAuthority('SYSTEM_MANAGEMENT_MANAGE')")
    @GetMapping
    public List<Location> getAll() {
        return locationService.getAll();
    }

    @GetMapping("/active")
    public List<Location> getActive() {
        return locationService.getActive();
    }

    @PreAuthorize("hasAuthority('SYSTEM_MANAGEMENT_MANAGE')")
    @PostMapping
    public Location create(@RequestBody Location location) {
        return locationService.create(location);
    }

    @PreAuthorize("hasAuthority('SYSTEM_MANAGEMENT_MANAGE')")
    @PutMapping("/{id}")
    public Location update(@PathVariable Long id, @RequestBody Location location) {
        return locationService.update(id, location);
    }

    @PreAuthorize("hasAuthority('SYSTEM_MANAGEMENT_MANAGE')")
    @PatchMapping("/{id}/deactivate")
    public Location deactivate(@PathVariable Long id) {
        return locationService.setActive(id, false);
    }

    @PreAuthorize("hasAuthority('SYSTEM_MANAGEMENT_MANAGE')")
    @PatchMapping("/{id}/activate")
    public Location activate(@PathVariable Long id) {
        return locationService.setActive(id, true);
    }
}
