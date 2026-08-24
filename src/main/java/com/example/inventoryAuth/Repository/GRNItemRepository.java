package com.example.inventoryAuth.Repository;
import com.example.inventoryAuth.Entity.GRN;
import com.example.inventoryAuth.Entity.GRNItem;
import com.example.inventoryAuth.Entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GRNItemRepository extends JpaRepository<GRNItem, Long> {

    Optional<GRNItem> findByGrnAndItem(GRN grn, Item item);

    List<GRNItem> findByItemAndGrn_GrnDateAfter(Item item, LocalDate localDate);
}
