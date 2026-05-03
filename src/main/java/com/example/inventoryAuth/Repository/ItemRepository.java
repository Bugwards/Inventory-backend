package com.example.inventoryAuth.Repository;

import com.example.inventoryAuth.Entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ItemRepository extends JpaRepository<Item, Long>{

    Optional<Item> findByItemCode(String itemCode);
    List<Item> findByItemNameContainingIgnoreCase(String itemName);
    Page<Item> findByItemNameContainingIgnoreCase(String itemName, Pageable pageable);

    List<Item> findByItemNameContainingIgnoreCaseOrItemCodeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String itemName,
            String itemCode,
            String description
    );
}
