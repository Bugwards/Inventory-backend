package com.example.inventoryAuth.Service;

import com.example.inventoryAuth.DTO.ItemResponseSearchByKeyword;
import com.example.inventoryAuth.DTO.StockTransferDto.*;
import com.example.inventoryAuth.Entity.*;
import com.example.inventoryAuth.Repository.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockTransferServiceTest {

    @Mock
    ItemRepository itemRepository;

    @Mock
    GrnRepository grnRepository;

    @Mock
    GRNItemRepository grnItemRepository;

    @Mock
    StockTransferRepository stockTransferRepository;

    @Mock
    StockRepository stockRepository;

    @Mock
    ItemGroupRepository itemGroupRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BinCardServiceTest binCardService;

    @InjectMocks
    StockTransferService stockTransferService;


    // ---------------------------------------------------------
    // Helper method
    // ---------------------------------------------------------

    private UserDetails mockUserDetails() {

        UserDetails userDetails = mock(UserDetails.class);

        when(userDetails.getUsername())
                .thenReturn("testuser");

        return userDetails;
    }


    private User mockUser() {

        User user = mock(User.class);

        when(user.getLocation())
                .thenReturn(Location.HEAD_OFFICE);

        return user;
    }


    private Item mockItem() {

        Item item = mock(Item.class);

        when(item.getId())
                .thenReturn(1L);

        when(item.getItemCode())
                .thenReturn("ITM001");

        when(item.getItemName())
                .thenReturn("Test Item");

        when(item.getItemDescription())
                .thenReturn("Test Description");

        when(item.getUnitOfMeasurement())
                .thenReturn("PCS");

        return item;
    }


    private GRN mockGRN() {

        GRN grn = mock(GRN.class);

        when(grn.getGrnNumber())
                .thenReturn("GRN0001");

        return grn;
    }


    private GRNItem mockGRNItem(GRN grn, Item item) {

        GRNItem grnItem = mock(GRNItem.class);

        when(grnItem.getGrn())
                .thenReturn(grn);

        when(grnItem.getItem())
                .thenReturn(item);

        return grnItem;
    }


    private Stock mockStock(GRNItem grnItem) {

        Stock stock = mock(Stock.class);

        when(stock.getCurrentQty())
                .thenReturn(100);

        when(stock.getActualQty())
                .thenReturn(100);

        when(stock.getUnitPrice())
                .thenReturn(50.0);

        when(stock.getGrnItem())
                .thenReturn(grnItem);

        return stock;
    }


    // =========================================================
    // getCurrentUserLocation()
    // =========================================================



    @Test
    void findItemByName_noResults_shouldReturnEmptyList() {

        when(itemRepository
                .findByItemNameContainingIgnoreCaseOrItemCodeContainingIgnoreCaseOrItemDescriptionContainingIgnoreCase(
                        "abc",
                        "abc",
                        "abc"
                ))
                .thenReturn(new ArrayList<>());

        List<ItemResponseSearchByKeyword> result =
                stockTransferService.findItemByName("abc");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }




    @Test
    void getItemDetails_itemNotFound_shouldFail() {

        when(itemRepository.findByItemCode("INVALID"))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> stockTransferService.getItemDetails("INVALID")
                );

        assertEquals(
                "Item not found",
                exception.getMessage()
        );
    }





    // =========================================================
    // getStockTransferList()
    // =========================================================

    @Test
    void getStockTransferList_success() {

        StockTransfer stockTransfer =
                mock(StockTransfer.class);

        when(stockTransfer.getTransferNo())
                .thenReturn("TRS000001");

        when(stockTransfer.getTransferDate())
                .thenReturn(LocalDate.now());

        when(stockTransfer.getFromLocation())
                .thenReturn(Location.HEAD_OFFICE);

        when(stockTransfer.getToLocation())
                .thenReturn(Location.KANDY);

        when(stockTransfer.getStatus())
                .thenReturn(Status.UNAPPROVED);

        Page<StockTransfer> page =
                new PageImpl<>(
                        List.of(stockTransfer)
                );

        when(stockTransferRepository.findAll(
                PageRequest.of(0, 5)
        )).thenReturn(page);

        List<StockTransferListResponse> result =
                stockTransferService.getStockTransferList(0);

        assertNotNull(result);

        assertEquals(1, result.size());

        assertEquals(
                "TRS000001",
                result.get(0).getTransferNo()
        );

        assertEquals(
                Location.HEAD_OFFICE,
                result.get(0).getFromLocation()
        );

        assertEquals(
                Location.KANDY,
                result.get(0).getToLocation()
        );
    }


    @Test
    void getStockTransferList_empty_shouldReturnEmptyList() {

        Page<StockTransfer> page =
                new PageImpl<>(
                        new ArrayList<>()
                );

        when(stockTransferRepository.findAll(
                PageRequest.of(0, 5)
        )).thenReturn(page);

        List<StockTransferListResponse> result =
                stockTransferService.getStockTransferList(0);

        assertNotNull(result);

        assertTrue(result.isEmpty());
    }


    // =========================================================
    // getSelectedStockTransferRecord()
    // =========================================================

    @Test
    void getSelectedStockTransferRecord_notFound_shouldFail() {

        when(stockTransferRepository
                .findByTransferNo("TRS999999"))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> stockTransferService
                                .getSelectedStockTransferRecord("TRS999999")
                );

        assertEquals(
                "stock transfer not found",
                exception.getMessage()
        );
    }


    @Test
    void getSelectedStockTransferRecord_success() {

        StockTransfer stockTransfer =
                mock(StockTransfer.class);

        when(stockTransfer.getTransferDate())
                .thenReturn(LocalDate.now());

        when(stockTransfer.getToLocation())
                .thenReturn(Location.KANDY);

        when(stockTransfer.getRequestRef())
                .thenReturn("REQ001");

        when(stockTransfer.getComment())
                .thenReturn("Test");

        when(stockTransfer.getStockTransferItem())
                .thenReturn(new ArrayList<>());

        when(stockTransferRepository
                .findByTransferNo("TRS000001"))
                .thenReturn(Optional.of(stockTransfer));

        StockTransferRequest result =
                stockTransferService
                        .getSelectedStockTransferRecord("TRS000001");

        assertNotNull(result);

        assertEquals(
                Location.KANDY,
                result.getToLocation()
        );

        assertEquals(
                "REQ001",
                result.getRequestRef()
        );

        assertEquals(
                "Test",
                result.getComment()
        );

        assertNotNull(result.getItems());

        assertTrue(result.getItems().isEmpty());
    }


    // =========================================================
    // updateStockTransferRecord()
    // =========================================================

    @Test
    void updateStockTransferRecord_notFound_shouldFail() {

        when(stockTransferRepository
                .findByTransferNo("TRS999999"))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> stockTransferService.updateStockTransferRecord(
                                "TRS999999",
                                new StockTransferRequest()
                        )
                );

        assertEquals(
                "stock transfer not found",
                exception.getMessage()
        );
    }


    @Test
    void updateStockTransferRecord_approved_shouldFail() {

        StockTransfer stockTransfer =
                mock(StockTransfer.class);

        when(stockTransfer.getStatus())
                .thenReturn(Status.APPROVED);

        when(stockTransferRepository
                .findByTransferNo("TRS000001"))
                .thenReturn(Optional.of(stockTransfer));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> stockTransferService.updateStockTransferRecord(
                                "TRS000001",
                                new StockTransferRequest()
                        )
                );

        assertEquals(
                "You Can't Update this stock transfer record",
                exception.getMessage()
        );
    }


    @Test
    void updateStockTransferRecord_cancelled_shouldFail() {

        StockTransfer stockTransfer =
                mock(StockTransfer.class);

        when(stockTransfer.getStatus())
                .thenReturn(Status.CANCELLED);

        when(stockTransferRepository
                .findByTransferNo("TRS000001"))
                .thenReturn(Optional.of(stockTransfer));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> stockTransferService.updateStockTransferRecord(
                                "TRS000001",
                                new StockTransferRequest()
                        )
                );

        assertEquals(
                "You Can't Update this stock transfer record",
                exception.getMessage()
        );
    }



    // =========================================================
    // cancelStockTransfer()
    // =========================================================

    @Test
    void cancelStockTransfer_success() {

        StockTransfer stockTransfer =
                mock(StockTransfer.class);

        when(stockTransferRepository
                .findByTransferNo("TRS000001"))
                .thenReturn(Optional.of(stockTransfer));

        when(stockTransfer.getStockTransferItem())
                .thenReturn(new ArrayList<>());

        stockTransferService
                .cancelStockTransfer("TRS000001");

        verify(stockTransfer)
                .setStatus(Status.CANCELLED);

        verify(stockTransferRepository)
                .save(stockTransfer);
    }


    // =========================================================
    // getCancelMsg()
    // =========================================================

    @Test
    void getCancelMsg_success() {

        StockTransfer stockTransfer =
                mock(StockTransfer.class);

        when(stockTransferRepository
                .findByTransferNo("TRS000001"))
                .thenReturn(Optional.of(stockTransfer));

        stockTransferService.getCancelMsg(
                "TRS000001",
                "Wrong destination"
        );

        verify(stockTransfer)
                .setCanselReason("Wrong destination");

        verify(stockTransferRepository)
                .save(stockTransfer);
    }
}