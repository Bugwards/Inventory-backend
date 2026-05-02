package com.example.inventoryAuth.Repository;


import com.example.inventoryAuth.Entity.GRNItem;
import com.example.inventoryAuth.Entity.Item;
import com.example.inventoryAuth.Entity.Stock;
import com.example.inventoryAuth.Entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Long> {


    List<Stock> findByItemAndLocationOrderByCreatedAtAsc(Item item, Location location);

    List<Stock> findByLocation(Location location);

    List<Stock> findByItemId(Long Id);

    Stock findByItemAndGrnItem(Item item, GRNItem grnItem);
}
