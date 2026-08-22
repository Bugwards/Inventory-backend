package com.example.inventoryAuth.Service;

import com.example.inventoryAuth.DTO.BinCardDTO;
import com.example.inventoryAuth.DTO.BinCardItemDTO;
import com.example.inventoryAuth.DTO.BinCardSummaryDTO;
import com.example.inventoryAuth.DTO.DashboardAnalyticsDTO;
import com.example.inventoryAuth.DTO.StockLevelDTO;
import com.example.inventoryAuth.Entity.BinCard;
import com.example.inventoryAuth.Entity.GRN;
import com.example.inventoryAuth.Entity.GRNItem;
import com.example.inventoryAuth.Entity.Item;
import com.example.inventoryAuth.Entity.MovementType;
import com.example.inventoryAuth.Entity.OpeningStock;
import com.example.inventoryAuth.Entity.OpeningStockItem;
import com.example.inventoryAuth.Entity.Stock;
import com.example.inventoryAuth.Entity.StockAdjustment;
import com.example.inventoryAuth.Entity.StockAdjustmentItem;
import com.example.inventoryAuth.Entity.StockIssue;
import com.example.inventoryAuth.Entity.StockIssueItem;
import com.example.inventoryAuth.Entity.StockTransfer;
import com.example.inventoryAuth.Entity.StockTransferItem;
import com.example.inventoryAuth.Entity.TransactionType;

import com.example.inventoryAuth.Repository.BinCardRepository;
import com.example.inventoryAuth.Repository.GRNItemRepository;
import com.example.inventoryAuth.Repository.ItemRepository;
import com.example.inventoryAuth.Repository.OpeningStockItemRepository;
import com.example.inventoryAuth.Repository.StockAdjustmentItemRepository;
import com.example.inventoryAuth.Repository.StockIssueItemRepository;
import com.example.inventoryAuth.Repository.StockRepository;
import com.example.inventoryAuth.Repository.StockTransferItemRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BinCardService
 */
@ExtendWith(MockitoExtension.class)
class BinCardServiceTest {

    @Mock
    private BinCardRepository binCardRepo;

    @Mock
    private ItemRepository itemRepo;

    @Mock
    private GRNItemRepository grnItemRepository;

    @Mock
    private StockIssueItemRepository stockIssueItemRepo;

    @Mock
    private StockTransferItemRepository stockTransferItemRepo;

    @Mock
    private OpeningStockItemRepository openingStockItemRepo;

    @Mock
    private StockAdjustmentItemRepository stockAdjustmentItemRepo;

    @Mock
    private StockRepository stockRepo;

    @InjectMocks
    private BinCardService binCardService;


    // =========================================================
    // HELPER METHODS
    // =========================================================

    private Item createItem(
            String code,
            String name,
            Integer minimumLevel,
            Integer reorderLevel
    ) {

        Item item = new Item();

        item.setItemCode(code);
        item.setItemName(name);
        item.setMinimumLevel(minimumLevel);
        item.setReorderQuantity(reorderLevel);

        return item;
    }


    private Stock createStock(
            Item item,
            int quantity
    ) {

        Stock stock = new Stock();

        stock.setItem(item);
        stock.setCurrentQty(quantity);

        return stock;
    }


    // =========================================================
    // TEST 1
    // SEARCH ITEMS
    // =========================================================

    @Test
    void searchItems_shouldReturnMatchingItems() {

        Item item =
                createItem(
                        "ITM001",
                        "Laptop",
                        10,
                        20
                );

        when(
                itemRepo.findByItemNameContainingIgnoreCase("Lap")
        )
                .thenReturn(List.of(item));

        List<BinCardItemDTO> result =
                binCardService.searchItems("Lap");

        assertNotNull(result);

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                "ITM001",
                result.get(0).getItemCode()
        );

        assertEquals(
                "Laptop",
                result.get(0).getItemName()
        );

        verify(itemRepo)
                .findByItemNameContainingIgnoreCase("Lap");
    }


    // =========================================================
    // TEST 2
    // SEARCH ITEMS
    // NO MATCH
    // =========================================================

    @Test
    void searchItems_whenNoItems_shouldReturnEmptyList() {

        when(
                itemRepo.findByItemNameContainingIgnoreCase("Unknown")
        )
                .thenReturn(Collections.emptyList());

        List<BinCardItemDTO> result =
                binCardService.searchItems("Unknown");

        assertNotNull(result);

        assertTrue(
                result.isEmpty()
        );
    }


    // =========================================================
    // TEST 3
    // GET BIN CARD HISTORY
    // =========================================================

    @Test
    void getBinCardByItemCode_shouldReturnHistory() {

        Item item =
                createItem(
                        "ITM001",
                        "Laptop",
                        10,
                        20
                );

        when(
                itemRepo.findByItemCode("ITM001")
        )
                .thenReturn(Optional.of(item));

        BinCard binCard =
                new BinCard();

        binCard.setItem(item);
        binCard.setDate(LocalDate.now());
        binCard.setType(TransactionType.INWARD);
        binCard.setReference("GRN001");
        binCard.setInwardQty(50L);
        binCard.setOutwardQty(0L);
        binCard.setBalance(50L);
        binCard.setRemarks("Received stock");

        when(
                binCardRepo.findByItemOrderByDateAsc(item)
        )
                .thenReturn(List.of(binCard));

        List<BinCardDTO> result =
                binCardService.getBinCardByItemCode("ITM001");

        assertNotNull(result);

        assertEquals(
                1,
                result.size()
        );

        BinCardDTO dto =
                result.get(0);

        assertEquals(
                "ITM001",
                dto.getItemCode()
        );

        assertEquals(
                "Laptop",
                dto.getItemName()
        );

        assertEquals(
                50L,
                dto.getInwardQty()
        );

        assertEquals(
                0L,
                dto.getOutwardQty()
        );

        assertEquals(
                50L,
                dto.getBalance()
        );
    }


    // =========================================================
    // TEST 4
    // ITEM NOT FOUND
    // =========================================================

    @Test
    void getBinCardByItemCode_whenItemNotFound_shouldThrowException() {

        when(
                itemRepo.findByItemCode("INVALID")
        )
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () ->
                                binCardService
                                        .getBinCardByItemCode("INVALID")
                );

        assertEquals(
                "Item not Found",
                exception.getMessage()
        );
    }


    // =========================================================
    // TEST 5
    // CREATE BIN CARD FROM GRN
    // =========================================================

    @Test
    void createBinCardFromGRN_shouldCreateInwardRecord() {

        Item item =
                createItem(
                        "ITM001",
                        "Laptop",
                        10,
                        20
                );

        Stock stock =
                createStock(
                        item,
                        100
                );

        GRN grn =
                mock(GRN.class);

        GRNItem grnItem =
                mock(GRNItem.class);

        when(grnItem.getItem())
                .thenReturn(item);

        when(grnItem.getQuantity())
                .thenReturn(50);

        when(grn.getGrnDate())
                .thenReturn(LocalDate.now());

        when(grn.getGrnNumber())
                .thenReturn("GRN001");

        when(stockRepo.findByItem(item))
                .thenReturn(List.of(stock));

        binCardService.createBinCardFromGRN(
                grn,
                grnItem,
                stock
        );

        verify(binCardRepo)
                .save(any(BinCard.class));
    }


    // =========================================================
    // TEST 6
    // CREATE BIN CARD FROM OPENING STOCK
    // =========================================================

    @Test
    void createBinCardFromOpeningStock_shouldCreateInwardRecord() {

        Item item =
                createItem(
                        "ITM002",
                        "Printer",
                        5,
                        10
                );

        Stock stock =
                createStock(
                        item,
                        80
                );

        OpeningStock openingStock =
                mock(OpeningStock.class);

        OpeningStockItem openingStockItem =
                mock(OpeningStockItem.class);

        when(
                openingStockItem.getItem()
        )
                .thenReturn(item);

        when(
                openingStockItem.getQuantity()
        )
                .thenReturn(80);

        when(
                openingStock.getOpeningDate()
        )
                .thenReturn(LocalDate.now());

        when(
                openingStock.getEntryNo()
        )
                .thenReturn("OPEN001");

        when(
                stockRepo.findByItem(item)
        )
                .thenReturn(List.of(stock));

        binCardService.createBinCardFromOpeningStock(
                openingStock,
                openingStockItem,
                stock
        );

        verify(binCardRepo)
                .save(any(BinCard.class));
    }


    // =========================================================
    // TEST 7
    // CREATE BIN CARD FROM STOCK ADJUSTMENT
    // POSITIVE ADJUSTMENT
    // =========================================================

    @Test
    void createBinCardFromStockAdjustment_positiveQuantity_shouldCreateInward() {

        Item item =
                createItem(
                        "ITM003",
                        "Mouse",
                        10,
                        20
                );

        Stock stock =
                createStock(
                        item,
                        60
                );

        StockAdjustment adjustment =
                mock(StockAdjustment.class);

        StockAdjustmentItem adjustmentItem =
                mock(StockAdjustmentItem.class);

        when(
                adjustmentItem.getQuantity()
        )
                .thenReturn(20);

        when(
                adjustment.getAdjustmentDate()
        )
                .thenReturn(LocalDate.now());

        when(
                adjustment.getAdjustmentNo()
        )
                .thenReturn("ADJ001");

        when(
                stockRepo.findByItem(item)
        )
                .thenReturn(List.of(stock));

        binCardService.createBinCardFromStockAdjustment(
                adjustment,
                adjustmentItem,
                stock
        );

        verify(binCardRepo)
                .save(any(BinCard.class));
    }


    // =========================================================
    // TEST 8
    // CREATE BIN CARD FROM STOCK ADJUSTMENT
    // NEGATIVE QUANTITY
    // =========================================================

    @Test
    void createBinCardFromStockAdjustment_negativeQuantity_shouldCreateOutward() {

        Item item =
                createItem(
                        "ITM004",
                        "Keyboard",
                        10,
                        20
                );

        Stock stock =
                createStock(
                        item,
                        50
                );

        StockAdjustment adjustment =
                mock(StockAdjustment.class);

        StockAdjustmentItem adjustmentItem =
                mock(StockAdjustmentItem.class);

        when(
                adjustmentItem.getQuantity()
        )
                .thenReturn(-15);

        when(
                adjustment.getAdjustmentDate()
        )
                .thenReturn(LocalDate.now());

        when(
                adjustment.getAdjustmentNo()
        )
                .thenReturn("ADJ002");

        when(
                stockRepo.findByItem(item)
        )
                .thenReturn(List.of(stock));

        binCardService.createBinCardFromStockAdjustment(
                adjustment,
                adjustmentItem,
                stock
        );

        verify(binCardRepo)
                .save(any(BinCard.class));
    }


    // =========================================================
    // TEST 9
    // CREATE BIN CARD FROM STOCK ISSUE
    // =========================================================

    @Test
    void createBinCardFromStockIssue_shouldCreateOutwardRecord() {

        Item item =
                createItem(
                        "ITM005",
                        "Monitor",
                        5,
                        10
                );

        Stock stock =
                createStock(
                        item,
                        40
                );

        StockIssue stockIssue =
                mock(StockIssue.class);

        StockIssueItem stockIssueItem =
                mock(StockIssueItem.class);

        when(
                stockIssueItem.getItem()
        )
                .thenReturn(item);

        when(
                stockIssueItem.getIssuedQuantity()
        )
                .thenReturn(10);

        when(
                stockIssue.getDate()
        )
                .thenReturn(LocalDate.now());

        when(
                stockIssue.getIssueNo()
        )
                .thenReturn("ISS001");

        when(
                stockIssue.getComment()
        )
                .thenReturn("Issued to department");

        when(
                stockRepo.findByItem(item)
        )
                .thenReturn(List.of(stock));

        binCardService.createBinCardFromStockIssue(
                stock,
                stockIssue,
                stockIssueItem
        );

        verify(binCardRepo)
                .save(any(BinCard.class));
    }


    // =========================================================
    // TEST 10
    // CREATE BIN CARD FROM STOCK TRANSFER
    // =========================================================

    @Test
    void createBinCardFromStockTransfer_shouldCreateOutwardRecord() {

        Item item =
                createItem(
                        "ITM006",
                        "Chair",
                        10,
                        20
                );

        Stock stock =
                createStock(
                        item,
                        100
                );

        StockTransfer transfer =
                mock(StockTransfer.class);

        StockTransferItem transferItem =
                mock(StockTransferItem.class);

        when(
                transferItem.getItem()
        )
                .thenReturn(item);

        when(
                transferItem.getTransferQty()
        )
                .thenReturn(25);

        when(
                transfer.getTransferDate()
        )
                .thenReturn(LocalDate.now());

        when(
                transfer.getTransferNo()
        )
                .thenReturn("TR001");

        when(
                transfer.getComment()
        )
                .thenReturn("Transfer to branch");

        when(
                stockRepo.findByItem(item)
        )
                .thenReturn(List.of(stock));

        binCardService.createBinCardFromStockTransfer(
                stock,
                transfer,
                transferItem
        );

        verify(binCardRepo)
                .save(any(BinCard.class));
    }


    // =========================================================
    // TEST 11
    // DASHBOARD - NO ITEMS
    // =========================================================

    @Test
    void getDashboardAnalytics_noItems_shouldReturnZero() {

        when(
                itemRepo.findAll()
        )
                .thenReturn(Collections.emptyList());

        when(
                binCardRepo.findAll()
        )
                .thenReturn(Collections.emptyList());

        DashboardAnalyticsDTO result =
                binCardService.getDashboardAnalytics();

        assertNotNull(result);

        assertEquals(
                0,
                result.getTotalItems()
        );

        assertEquals(
                0,
                result.getTotalStock()
        );

        assertEquals(
                0,
                result.getLowStockItems()
        );

        assertEquals(
                0,
                result.getTotalInward()
        );

        assertEquals(
                0,
                result.getTotalOutward()
        );

        assertEquals(
                0.0,
                result.getStockHealthPercentage()
        );

        assertNotNull(
                result.getMovementTrend()
        );

        assertEquals(
                6,
                result.getMovementTrend().size()
        );
    }


    // =========================================================
    // TEST 13
    // DASHBOARD - TOTAL INWARD / OUTWARD
    // =========================================================

    @Test
    void getDashboardAnalytics_shouldCalculateTotalMovement() {

        when(
                itemRepo.findAll()
        )
                .thenReturn(Collections.emptyList());

        BinCard inward =
                new BinCard();

        inward.setDate(LocalDate.now());
        inward.setType(TransactionType.INWARD);
        inward.setInwardQty(100L);
        inward.setOutwardQty(0L);
        inward.setReference("GRN001");
        inward.setRemarks("Received");


        BinCard outward =
                new BinCard();

        outward.setDate(LocalDate.now());
        outward.setType(TransactionType.OUTWARD);
        outward.setInwardQty(0L);
        outward.setOutwardQty(40L);
        outward.setReference("ISS001");
        outward.setRemarks("Issued");


        when(
                binCardRepo.findAll()
        )
                .thenReturn(
                        List.of(
                                inward,
                                outward
                        )
                );

        DashboardAnalyticsDTO result =
                binCardService.getDashboardAnalytics();

        assertEquals(
                100,
                result.getTotalInward()
        );

        assertEquals(
                40,
                result.getTotalOutward()
        );

        assertEquals(
                6,
                result.getMovementTrend().size()
        );
    }


    // =========================================================
    // TEST 14
    // DASHBOARD - RECENT TRANSACTIONS
    // =========================================================

    @Test
    void getDashboardAnalytics_shouldReturnRecentTransactions() {

        when(
                itemRepo.findAll()
        )
                .thenReturn(Collections.emptyList());

        BinCard transaction =
                new BinCard();

        transaction.setDate(
                LocalDate.now()
        );

        transaction.setType(
                TransactionType.INWARD
        );

        transaction.setInwardQty(
                50L
        );

        transaction.setOutwardQty(
                0L
        );

        transaction.setReference(
                "GRN001"
        );

        transaction.setRemarks(
                "Received stock"
        );

        when(
                binCardRepo.findAll()
        )
                .thenReturn(
                        List.of(transaction)
                );

        DashboardAnalyticsDTO result =
                binCardService.getDashboardAnalytics();

        assertNotNull(
                result.getRecentTransactions()
        );

        assertEquals(
                1,
                result.getRecentTransactions().size()
        );

        assertEquals(
                "INWARD",
                result.getRecentTransactions()
                        .get(0)
                        .getType()
        );

        assertEquals(
                50,
                result.getRecentTransactions()
                        .get(0)
                        .getQuantity()
        );

        assertEquals(
                "GRN001",
                result.getRecentTransactions()
                        .get(0)
                        .getReference()
        );
    }


    // =========================================================
    // TEST 15
    // DASHBOARD - SIX MONTH TREND
    // =========================================================

    @Test
    void getDashboardAnalytics_shouldReturnSixMonthTrend() {

        when(
                itemRepo.findAll()
        )
                .thenReturn(Collections.emptyList());

        when(
                binCardRepo.findAll()
        )
                .thenReturn(Collections.emptyList());

        DashboardAnalyticsDTO result =
                binCardService.getDashboardAnalytics();

        assertNotNull(
                result.getMovementTrend()
        );

        assertEquals(
                6,
                result.getMovementTrend().size()
        );

        assertNotNull(
                result.getMovementTrend()
                        .get(0)
                        .getMonth()
        );

        assertNotNull(
                result.getMovementTrend()
                        .get(5)
                        .getMonth()
        );
    }


}