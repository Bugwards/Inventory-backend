package com.example.inventoryAuth.Repository;


import com.example.inventoryAuth.Entity.BinCard;
import com.example.inventoryAuth.Entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BinCardRepository extends JpaRepository<BinCard, Long> {

    List<BinCard> findByItemOrderByDateAsc(Item item);
    //List<BinCard> findByItem_IdOrderByDateAsc(Long itemId);

    long countByDateBetween(LocalDate startDate, LocalDate endDate);

    List<BinCard> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
