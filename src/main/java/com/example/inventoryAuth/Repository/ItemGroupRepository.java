package com.example.inventoryAuth.Repository;

import com.example.inventoryAuth.Entity.ItemGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemGroupRepository extends JpaRepository<ItemGroup, Long>{
    Optional<ItemGroup> findByCode(String code);
    Page<ItemGroup> findByNameContainingIgnoreCase(String name, Pageable pageable);
    List<ItemGroup> findByNameContainingIgnoreCase(String name);
}