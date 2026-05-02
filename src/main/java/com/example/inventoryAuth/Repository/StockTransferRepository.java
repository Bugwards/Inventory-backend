package com.example.inventoryAuth.Repository;


import com.example.inventoryAuth.Entity.Location;
import com.example.inventoryAuth.Entity.Status;
import com.example.inventoryAuth.Entity.StockTransfer;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockTransferRepository extends JpaRepository<StockTransfer, String> , JpaSpecificationExecutor<StockTransfer> {
       Optional <StockTransfer> findByTransferNo(String transferNo);

       List<StockTransfer> findByFromLocationAndToLocationAndStatus(Location fromLocation, Location receiptLocation, Status status , PageRequest pageRequest );

}
