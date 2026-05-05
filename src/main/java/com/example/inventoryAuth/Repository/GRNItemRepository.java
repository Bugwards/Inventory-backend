package com.example.inventoryAuth.Repository;
import com.example.inventoryAuth.Entity.GRN;
import com.example.inventoryAuth.Entity.GRNItem;
import com.example.inventoryAuth.Entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GRNItemRepository extends JpaRepository<GRNItem, Long> {

    Optional<GRNItem> findByGrnAndItem(GRN grn, Item item);
}
