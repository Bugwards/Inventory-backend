package com.example.inventoryAuth.Service;

import com.example.inventoryAuth.DTO.StockReceiptDto.*;
import com.example.inventoryAuth.DTO.StockTransferDto.StockTransferListResponse;
import com.example.inventoryAuth.DTO.StockTransferDto.TransferredGrnResponse;
import com.example.inventoryAuth.DTO.StockTransferDto.TransferredItemResponse;
import com.example.inventoryAuth.Entity.*;
import com.example.inventoryAuth.Repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockTransferReceiptServiceTest {

    @InjectMocks
    private StockTransferReceiptService service;

    @Mock
    private StockTransferRepository stockTransferRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private GRNItemRepository grnItemRepository;

    @Mock
    private GrnRepository grnRepository;

    @Mock
    private StockReceiptRepository stockReceiptRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StockTransferItemRepository stockTransferItemRepository;

    @Mock
    private LocationRepository locationRepository;

    // ---------------------------------------------------------
    // COMMON TEST DATA
    // ---------------------------------------------------------

    private Item item;
    private ItemGroup itemGroup;
    private GRN grn;
    private GRNItem grnItem;
    private Stock stock;
    private StockTransfer stockTransfer;
    private StockTransferItem stockTransferItem;
    private StockReceipt stockReceipt;
    private StockReceiptItem stockReceiptItem;
    private Location headOfficeLocation;
    private Location kandyLocation;

    private Location createLocation(Long id, String code) {
        Location location = new Location();
        location.setId(id);
        location.setCode(code);
        location.setName(code);
        location.setActive(true);
        return location;
    }

    @BeforeEach
    void setUp() {

        headOfficeLocation = createLocation(2L, "HEAD_OFFICE");
        kandyLocation = createLocation(1L, "KANDY");

        itemGroup = new ItemGroup();
        itemGroup.setName("Electronics");

        item = new Item();
        item.setId(1L);
        item.setItemCode("ITM001");
        item.setItemName("Laptop");
        item.setItemDescription("Dell Laptop");
        item.setItemGroup(itemGroup);

        grn = new GRN();
        grn.setGrnNumber("GRN0001");
        grn.setGrnDate(LocalDate.now());

        grnItem = new GRNItem();
        grnItem.setGrn(grn);
        grnItem.setItem(item);

        stock = new Stock();
        stock.setCurrentQty(50);
        stock.setActualQty(50);
        stock.setGrnItem(grnItem);

        stockTransfer = new StockTransfer();
        stockTransfer.setTransferNo("TRS000001");
        stockTransfer.setTransferDate(LocalDate.now());
        stockTransfer.setFromLocation(headOfficeLocation);
        stockTransfer.setToLocation(kandyLocation);
        stockTransfer.setStatus(Status.APPROVED);

        stockTransferItem = new StockTransferItem();
        stockTransferItem.setItem(item);
        stockTransferItem.setGrnItem(grnItem);
        stockTransferItem.setTransferQty(10);
        stockTransferItem.setStockTransfer(stockTransfer);

        List<StockTransferItem> transferItems = new ArrayList<>();
        transferItems.add(stockTransferItem);
        stockTransfer.setStockTransferItem(transferItems);

        stockReceipt = new StockReceipt();
        stockReceipt.setReceiptNo("RCP000001");
        stockReceipt.setDate(LocalDate.now());
        stockReceipt.setFromLocation(headOfficeLocation);
        stockReceipt.setReceiptLocation(kandyLocation);
        stockReceipt.setComment("Test receipt");
        stockReceipt.setStatus(Status.UNAPPROVED);
        stockReceipt.setStockTransfer(stockTransfer);

        stockReceiptItem = new StockReceiptItem();
        stockReceiptItem.setItem(item);
        stockReceiptItem.setGrnItem(grnItem);
        stockReceiptItem.setReceivedQty(8L);
        stockReceiptItem.setStockReceipt(stockReceipt);

        List<StockReceiptItem> receiptItems = new ArrayList<>();
        receiptItems.add(stockReceiptItem);
        stockReceipt.setStockReceiptItem(receiptItems);
    }

    // =========================================================
    // 1. getCurrentUserLocation()
    // =========================================================

    @Test
    void getCurrentUserLocation_success() {

        User user = new User();
        user.setUsername("testuser");
        user.setLocation(kandyLocation);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "testuser",
                        null
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        Location result = service.getCurrentUserLocation();

        assertEquals(kandyLocation, result);

        verify(userRepository)
                .findByUsername("testuser");
    }

    @Test
    void getCurrentUserLocation_userNotFound_shouldThrowException() {

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "unknown",
                        null
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.getCurrentUserLocation()
                );

        assertEquals(
                "User not found",
                exception.getMessage()
        );
    }

    // =========================================================
    // 2. saveStockReceipt()
    // =========================================================

    @Test
    void saveStockReceipt_success() {

        StockReceiptRequest request =
                new StockReceiptRequest();

        request.setDate(LocalDate.now());
        request.setFromLocationId(headOfficeLocation.getId());
        request.setReceiptLocationId(kandyLocation.getId());
        request.setTransferNo("TRS000001");
        request.setComment("Received stock");

        StockReceiptItemsRequest itemRequest =
                new StockReceiptItemsRequest();

        itemRequest.setItemCode("ITM001");

        StockReceiptItemGrnRequest grnRequest =
                new StockReceiptItemGrnRequest();

        grnRequest.setGrnNo("GRN0001");
        grnRequest.setReceivedQty(8L);

        itemRequest.setStockReceiptItemGrn(
                List.of(grnRequest)
        );

        request.setStockReceiptItems(
                List.of(itemRequest)
        );

        when(stockTransferRepository.findByTransferNo("TRS000001"))
                .thenReturn(Optional.of(stockTransfer));

        when(itemRepository.findByItemCode("ITM001"))
                .thenReturn(Optional.of(item));

        when(grnRepository.findByGrnNumber("GRN0001"))
                .thenReturn(grn);

        when(grnItemRepository.findByGrnAndItem(grn, item))
                .thenReturn(Optional.of(grnItem));

        when(locationRepository.findById(headOfficeLocation.getId()))
                .thenReturn(Optional.of(headOfficeLocation));

        when(locationRepository.findById(kandyLocation.getId()))
                .thenReturn(Optional.of(kandyLocation));

        when(stockReceiptRepository.save(any(StockReceipt.class)))
                .thenAnswer(invocation -> {

                    StockReceipt receipt =
                            invocation.getArgument(0);

                    receipt.setId(1L);

                    return receipt;
                });

        service.saveStockReceipt(request);

        assertEquals(
                Status.TRANSFERRED,
                stockTransfer.getStatus()
        );

        verify(stockTransferRepository)
                .findByTransferNo("TRS000001");

        verify(itemRepository)
                .findByItemCode("ITM001");

        verify(grnRepository)
                .findByGrnNumber("GRN0001");

        verify(grnItemRepository)
                .findByGrnAndItem(grn, item);

        verify(stockReceiptRepository, times(2))
                .save(any(StockReceipt.class));
    }

    @Test
    void saveStockReceipt_transferNotFound_shouldThrowException() {

        StockReceiptRequest request =
                new StockReceiptRequest();

        request.setTransferNo("TRS999999");
        request.setStockReceiptItems(
                Collections.emptyList()
        );

        when(stockTransferRepository.findByTransferNo("TRS999999"))
                .thenReturn(Optional.empty());

        assertThrows(
                NullPointerException.class,
                () -> service.saveStockReceipt(request)
        );
    }

    @Test
    void saveStockReceipt_itemNotFound_shouldThrowException() {

        StockReceiptRequest request =
                new StockReceiptRequest();

        request.setTransferNo("TRS000001");

        StockReceiptItemsRequest itemRequest =
                new StockReceiptItemsRequest();

        itemRequest.setItemCode("INVALID");

        itemRequest.setStockReceiptItemGrn(
                Collections.emptyList()
        );

        request.setStockReceiptItems(
                List.of(itemRequest)
        );

        when(stockTransferRepository.findByTransferNo("TRS000001"))
                .thenReturn(Optional.of(stockTransfer));

        when(itemRepository.findByItemCode("INVALID"))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.saveStockReceipt(request)
                );

        assertEquals(
                "Record Not Found",
                exception.getMessage()
        );
    }

    @Test
    void saveStockReceipt_grnItemNotFound_shouldThrowException() {

        StockReceiptRequest request =
                new StockReceiptRequest();

        request.setTransferNo("TRS000001");

        StockReceiptItemsRequest itemRequest =
                new StockReceiptItemsRequest();

        itemRequest.setItemCode("ITM001");

        StockReceiptItemGrnRequest grnRequest =
                new StockReceiptItemGrnRequest();

        grnRequest.setGrnNo("GRN0001");
        grnRequest.setReceivedQty(5L);

        itemRequest.setStockReceiptItemGrn(
                List.of(grnRequest)
        );

        request.setStockReceiptItems(
                List.of(itemRequest)
        );

        when(stockTransferRepository.findByTransferNo("TRS000001"))
                .thenReturn(Optional.of(stockTransfer));

        when(itemRepository.findByItemCode("ITM001"))
                .thenReturn(Optional.of(item));

        when(grnRepository.findByGrnNumber("GRN0001"))
                .thenReturn(grn);

        when(grnItemRepository.findByGrnAndItem(grn, item))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.saveStockReceipt(request)
                );

        assertEquals(
                "Record Not Found",
                exception.getMessage()
        );
    }


    @Test
    void populateItems_transferNotFound_shouldThrowException() {

        when(stockTransferRepository.findByTransferNo("TRS999999"))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.populateItems("TRS999999")
                );

        assertEquals(
                "Stock transfer not found: TRS999999",
                exception.getMessage()
        );
    }

    // =========================================================
    // 4. getStockTransferList()
    // =========================================================

    @Test
    void getStockTransferList_success() {

        when(locationRepository.findByCode("HEAD_OFFICE"))
                .thenReturn(Optional.of(headOfficeLocation));

        when(locationRepository.findByCode("KANDY"))
                .thenReturn(Optional.of(kandyLocation));

        when(
                stockTransferRepository
                        .findByFromLocationAndToLocationAndStatus(
                                headOfficeLocation,
                                kandyLocation,
                                Status.APPROVED,
                                PageRequest.of(0, 5)
                        )
        ).thenReturn(
                List.of(stockTransfer)
        );

        List<StockTransferListResponse> result =
                service.getStockTransferList(
                        0,
                        "HEAD_OFFICE",
                        "KANDY"
                );

        assertNotNull(result);

        assertEquals(1, result.size());

        assertEquals(
                "TRS000001",
                result.get(0).getTransferNo()
        );

        assertEquals(
                headOfficeLocation,
                result.get(0).getFromLocation()
        );

        assertEquals(
                kandyLocation,
                result.get(0).getToLocation()
        );

        assertEquals(
                Status.APPROVED,
                result.get(0).getStatus()
        );
    }

    @Test
    void getStockTransferList_noData_shouldReturnEmptyList() {

        when(locationRepository.findByCode("HEAD_OFFICE"))
                .thenReturn(Optional.of(headOfficeLocation));

        when(locationRepository.findByCode("KANDY"))
                .thenReturn(Optional.of(kandyLocation));

        when(
                stockTransferRepository
                        .findByFromLocationAndToLocationAndStatus(
                                headOfficeLocation,
                                kandyLocation,
                                Status.APPROVED,
                                PageRequest.of(0, 5)
                        )
        ).thenReturn(
                Collections.emptyList()
        );

        List<StockTransferListResponse> result =
                service.getStockTransferList(
                        0,
                        "HEAD_OFFICE",
                        "KANDY"
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // =========================================================
    // 5. getStockTransferReceiptList()
    // =========================================================

    @Test
    void getStockTransferReceiptList_success() {

        Page<StockReceipt> page =
                mock(Page.class);

        when(page.iterator())
                .thenReturn(
                        List.of(stockReceipt).iterator()
                );

        when(
                stockReceiptRepository
                        .findAll(PageRequest.of(0, 5))
        ).thenReturn(page);

        List<StockReceiptListResponse> result =
                service.getStockTransferReceiptList(0);

        assertNotNull(result);

        assertEquals(1, result.size());

        assertEquals(
                "RCP000001",
                result.get(0).getReceiptNo()
        );

        assertEquals(
                "TRS000001",
                result.get(0).getTransferNo()
        );

        assertEquals(
                headOfficeLocation,
                result.get(0).getFromLocation()
        );

        assertEquals(
                kandyLocation,
                result.get(0).getReceiptLocation()
        );
    }

    @Test
    void getStockTransferReceiptList_noData_shouldReturnEmptyList() {

        Page<StockReceipt> page =
                mock(Page.class);

        when(page.iterator())
                .thenReturn(
                        Collections.<StockReceipt>emptyList()
                                .iterator()
                );

        when(
                stockReceiptRepository
                        .findAll(PageRequest.of(0, 5))
        ).thenReturn(page);

        List<StockReceiptListResponse> result =
                service.getStockTransferReceiptList(0);

        assertNotNull(result);

        assertTrue(result.isEmpty());
    }



    // =========================================================
    // 7. updateStockReceipt()
    // =========================================================

    @Test
    void updateStockReceipt_approved_shouldThrowException() {

        stockReceipt.setStatus(Status.APPROVED);

        when(
                stockReceiptRepository
                        .findByReceiptNo("RCP000001")
        ).thenReturn(stockReceipt);

        StockReceiptRequest request =
                new StockReceiptRequest();

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.updateStockReceipt(
                                "RCP000001",
                                request
                        )
                );

        assertEquals(
                "You Can't Update this stock transfer record",
                exception.getMessage()
        );
    }

    @Test
    void updateStockReceipt_cancelled_shouldThrowException() {

        stockReceipt.setStatus(Status.CANCELLED);

        when(
                stockReceiptRepository
                        .findByReceiptNo("RCP000001")
        ).thenReturn(stockReceipt);

        StockReceiptRequest request =
                new StockReceiptRequest();

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.updateStockReceipt(
                                "RCP000001",
                                request
                        )
                );

        assertEquals(
                "You Can't Update this stock transfer record",
                exception.getMessage()
        );
    }

    @Test
    void updateStockReceipt_success() {

        stockReceipt.setStatus(Status.UNAPPROVED);

        StockReceiptRequest request =
                new StockReceiptRequest();

        request.setDate(LocalDate.now().plusDays(1));
        request.setFromLocationId(kandyLocation.getId());
        request.setTransferNo("TRS000002");
        request.setComment("Updated receipt");

        StockReceiptItemsRequest itemRequest =
                new StockReceiptItemsRequest();

        itemRequest.setItemCode("ITM001");

        StockReceiptItemGrnRequest grnRequest =
                new StockReceiptItemGrnRequest();

        grnRequest.setGrnNo("GRN0001");
        grnRequest.setReceivedQty(12L);

        itemRequest.setStockReceiptItemGrn(
                List.of(grnRequest)
        );

        request.setStockReceiptItems(
                List.of(itemRequest)
        );

        when(
                stockReceiptRepository
                        .findByReceiptNo("RCP000001")
        ).thenReturn(stockReceipt);

        when(
                itemRepository
                        .findByItemCode("ITM001")
        ).thenReturn(Optional.of(item));

        when(
                grnRepository
                        .findByGrnNumber("GRN0001")
        ).thenReturn(grn);

        when(
                grnItemRepository
                        .findByGrnAndItem(grn, item)
        ).thenReturn(Optional.of(grnItem));

        when(locationRepository.findById(kandyLocation.getId()))
                .thenReturn(Optional.of(kandyLocation));

        service.updateStockReceipt(
                "RCP000001",
                request
        );

        assertEquals(
                kandyLocation,
                stockReceipt.getFromLocation()
        );

        assertEquals(
                "TRS000002",
                stockReceipt.getTransferNo()
        );

        assertEquals(
                "Updated receipt",
                stockReceipt.getComment()
        );

        verify(stockReceiptRepository)
                .save(stockReceipt);
    }

    @Test
    void updateStockReceipt_itemNotFound_shouldThrowException() {

        stockReceipt.setStatus(Status.UNAPPROVED);

        StockReceiptRequest request =
                new StockReceiptRequest();

        StockReceiptItemsRequest itemRequest =
                new StockReceiptItemsRequest();

        itemRequest.setItemCode("INVALID");

        itemRequest.setStockReceiptItemGrn(
                Collections.emptyList()
        );

        request.setStockReceiptItems(
                List.of(itemRequest)
        );

        when(
                stockReceiptRepository
                        .findByReceiptNo("RCP000001")
        ).thenReturn(stockReceipt);

        when(
                itemRepository
                        .findByItemCode("INVALID")
        ).thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.updateStockReceipt(
                                "RCP000001",
                                request
                        )
                );

        assertEquals(
                "Record Not Found",
                exception.getMessage()
        );
    }

    // =========================================================
    // 8. approveStockReceipt()
    // =========================================================

    @Test
    void approveStockReceipt_success() {

        stockReceipt.setStatus(Status.UNAPPROVED);

        when(
                stockReceiptRepository
                        .findByReceiptNo("RCP000001")
        ).thenReturn(stockReceipt);

        when(
                stockRepository.findByItemAndGrnItem(
                        item,
                        grnItem
                )
        ).thenReturn(stock);

        service.approveStockReceipt(
                "RCP000001"
        );

        assertEquals(
                Status.APPROVED,
                stockReceipt.getStatus()
        );

        assertEquals(
                Status.TRANSFERRED,
                stockTransfer.getStatus()
        );

        assertEquals(
                58,
                stock.getCurrentQty()
        );

        assertEquals(
                58,
                stock.getActualQty()
        );

        assertNotNull(
                stockReceipt.getApprovedDate()
        );

        verify(stockReceiptRepository)
                .save(stockReceipt);

        verify(stockTransferRepository)
                .save(stockTransfer);
    }

    @Test
    void approveStockReceipt_transferNotFound_shouldThrowException() {

        stockReceipt.setStockTransfer(null);

        when(
                stockReceiptRepository
                        .findByReceiptNo("RCP000001")
        ).thenReturn(stockReceipt);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.approveStockReceipt(
                                "RCP000001"
                        )
                );

        assertEquals(
                "No Stock Transfer found for receipt: RCP000001",
                exception.getMessage()
        );

        verify(stockReceiptRepository, never())
                .save(any());

        verify(stockTransferRepository, never())
                .save(any());
    }

    // =========================================================
    // 9. cancelStockReceipt()
    // =========================================================

    @Test
    void cancelStockReceipt_success() {

        stockReceipt.setStatus(Status.UNAPPROVED);

        when(
                stockReceiptRepository
                        .findByReceiptNo("RCP000001")
        ).thenReturn(stockReceipt);

        service.cancelStockReceipt(
                "RCP000001","damaged"
        );

        assertEquals(
                Status.CANCELLED,
                stockReceipt.getStatus()
        );

        verify(stockReceiptRepository)
                .save(stockReceipt);
    }

    // =========================================================
    // 10. getCancelMsg()
    // =========================================================

    @Test
    void getCancelMsg_success() {

        when(
                stockReceiptRepository
                        .findByReceiptNo("RCP000001")
        ).thenReturn(stockReceipt);

        service.getCancelMsg(
                "RCP000001",
                "Wrong quantity received"
        );

        assertEquals(
                "Wrong quantity received",
                stockReceipt.getCancelReason()
        );

        verify(stockReceiptRepository)
                .save(stockReceipt);
    }
}