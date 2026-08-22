package com.example.inventoryAuth.Service;

import com.example.inventoryAuth.DTO.GRNDTO;
import com.example.inventoryAuth.DTO.GRNItemDTO;
import com.example.inventoryAuth.DTO.GRNResponseDTO;
import com.example.inventoryAuth.Entity.*;
import com.example.inventoryAuth.Repository.GrnRepository;
import com.example.inventoryAuth.Repository.ItemRepository;
import com.example.inventoryAuth.Repository.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class GRNServiceTest {

    // =========================================================
    // MOCK DEPENDENCIES
    // =========================================================

    @Mock
    private GrnRepository grnRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private StockService stockService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BinCardServiceTest binCardService;


    // =========================================================
    // MOCK SECURITY
    // =========================================================

    @Mock
    private User currentUser;

    @Mock
    private UserDetails userDetails;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;


    // =========================================================
    // MOCK ENTITIES
    // =========================================================

    @Mock
    private Item item;

    @Mock
    private Supplier supplier;


    // =========================================================
    // SERVICE UNDER TEST
    // =========================================================

    @InjectMocks
    private GRNService grnService;


    // =========================================================
    // CLEAR SECURITY CONTEXT
    // =========================================================

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }


    // =========================================================
    // HELPER - MOCK CURRENT USER
    // =========================================================

    private void mockCurrentUser() {

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(userDetails);

        when(userDetails.getUsername())
                .thenReturn("testuser");

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(currentUser));

        SecurityContextHolder.setContext(securityContext);
    }


    // =========================================================
    // HELPER - VALID GRN DTO
    // =========================================================

    private GRNDTO createValidGRNDTO() {

        GRNDTO dto = new GRNDTO();

        dto.setPoNumber("PO-0001");

        dto.setSupplier(supplier);

        dto.setGrnDate(LocalDate.now());

        GRNItemDTO itemDTO = new GRNItemDTO();

        itemDTO.setItemCode("ITEM-001");
        itemDTO.setQuantity(10);
        itemDTO.setUnitPrice(100.0);

        dto.setItems(List.of(itemDTO));

        return dto;
    }


    // =========================================================
    // HELPER - CREATE GRN
    // =========================================================

    private GRN createGRN() {

        GRN grn = new GRN();

        grn.setGrnId(1L);
        grn.setGrnNumber("GRN-00001");
        grn.setPoNumber("PO-0001");
        grn.setSupplier(supplier);
        grn.setLocation(Location.KANDY);
        grn.setGrnDate(LocalDate.now());
        grn.setStatus(Status.UNAPPROVED);
        grn.setCreatedBy(currentUser);
        grn.setItems(new ArrayList<>());

        return grn;
    }


    // =========================================================
    // HELPER - CREATE GRN ITEM
    // =========================================================

    private GRNItem createGRNItem(GRN grn) {

        GRNItem grnItem = new GRNItem();

        grnItem.setGrnItemId(1L);
        grnItem.setGrn(grn);
        grnItem.setItem(item);
        grnItem.setQuantity(10);
        grnItem.setActualQty(10);
        grnItem.setCurrentQty(10);
        grnItem.setUnitPrice(100.0);

        return grnItem;
    }


    // =========================================================
    // 1. CREATE GRN - SUCCESS
    // =========================================================

    @Test
    void createGRN_success() {

        // Arrange
        mockCurrentUser();

        GRNDTO dto = createValidGRNDTO();

        when(currentUser.getLocation())
                .thenReturn(Location.KANDY);

        when(itemRepository.findByItemCode("ITEM-001"))
                .thenReturn(Optional.of(item));

        when(grnRepository.save(any(GRN.class)))
                .thenAnswer(invocation -> {

                    GRN grn = invocation.getArgument(0);

                    if (grn.getGrnId() == null) {
                        grn.setGrnId(1L);
                    }

                    return grn;
                });


        // Act
        GRN result = grnService.save(dto, null);


        // Assert
        assertNotNull(result);

        assertEquals(
                1L,
                result.getGrnId()
        );

        assertEquals(
                "GRN-00001",
                result.getGrnNumber()
        );

        assertEquals(
                "PO-0001",
                result.getPoNumber()
        );

        assertEquals(
                Status.UNAPPROVED,
                result.getStatus()
        );

        assertEquals(
                Location.KANDY,
                result.getLocation()
        );

        assertEquals(
                1000.0,
                result.getTotalValue()
        );

        assertNotNull(result.getItems());

        assertEquals(
                1,
                result.getItems().size()
        );

        assertEquals(
                10,
                result.getItems().get(0).getQuantity()
        );

        assertEquals(
                100.0,
                result.getItems().get(0).getUnitPrice()
        );

        verify(grnRepository, times(2))
                .save(any(GRN.class));

        verify(itemRepository)
                .findByItemCode("ITEM-001");
    }


    // =========================================================
    // 2. CREATE GRN - PO NUMBER NULL
    // =========================================================

    @Test
    void createGRN_withoutPoNumber_shouldFail() {

        // Arrange
        mockCurrentUser();

        GRNDTO dto = createValidGRNDTO();

        dto.setPoNumber(null);


        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> grnService.save(dto, null)
        );


        assertEquals(
                "PO Number required",
                exception.getMessage()
        );

        verify(grnRepository, never())
                .save(any(GRN.class));
    }


    // =========================================================
    // 3. CREATE GRN - SUPPLIER NULL
    // =========================================================

    @Test
    void createGRN_withoutSupplier_shouldFail() {

        // Arrange
        mockCurrentUser();

        GRNDTO dto = createValidGRNDTO();

        dto.setSupplier(null);


        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> grnService.save(dto, null)
        );


        assertEquals(
                "Supplier required",
                exception.getMessage()
        );

        verify(grnRepository, never())
                .save(any(GRN.class));
    }


    // =========================================================
    // 4. CREATE GRN - ITEMS NULL
    // =========================================================

    @Test
    void createGRN_withoutItems_shouldFail() {

        // Arrange
        mockCurrentUser();

        GRNDTO dto = createValidGRNDTO();

        dto.setItems(null);


        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> grnService.save(dto, null)
        );


        assertEquals(
                "Items required",
                exception.getMessage()
        );

        verify(grnRepository, never())
                .save(any(GRN.class));
    }


    // =========================================================
    // 5. CREATE GRN - INVALID ITEM
    // =========================================================

    @Test
    void createGRN_withInvalidItem_shouldFail() {

        // Arrange
        mockCurrentUser();

        GRNDTO dto = createValidGRNDTO();

        when(itemRepository.findByItemCode("ITEM-001"))
                .thenReturn(Optional.empty());


        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> grnService.save(dto, null)
        );


        assertEquals(
                "Item not found",
                exception.getMessage()
        );

        verify(grnRepository, never())
                .save(any(GRN.class));

        verify(itemRepository)
                .findByItemCode("ITEM-001");
    }


    // =========================================================
    // 6. UPDATE GRN - SUCCESS
    // =========================================================

    @Test
    void updateGRN_success() {

        // Arrange
        mockCurrentUser();

        Long grnId = 1L;

        GRN existingGRN = createGRN();

        GRNDTO dto = createValidGRNDTO();

        when(grnRepository.findById(grnId))
                .thenReturn(Optional.of(existingGRN));

        when(itemRepository.findByItemCode("ITEM-001"))
                .thenReturn(Optional.of(item));

        when(grnRepository.save(any(GRN.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));


        // Act
        GRN result = grnService.save(
                dto,
                grnId
        );


        // Assert
        assertNotNull(result);

        assertEquals(
                "PO-0001",
                result.getPoNumber()
        );

        assertEquals(
                Status.UNAPPROVED,
                result.getStatus()
        );

        assertEquals(
                1,
                result.getItems().size()
        );

        assertEquals(
                10,
                result.getItems().get(0).getQuantity()
        );

        verify(grnRepository, times(2))
                .save(existingGRN);

        verify(itemRepository)
                .findByItemCode("ITEM-001");
    }


    // =========================================================
    // 7. UPDATE APPROVED GRN - FAIL
    // =========================================================

    @Test
    void updateApprovedGRN_shouldFail() {

        // Arrange
        mockCurrentUser();

        Long grnId = 1L;

        GRN grn = createGRN();

        grn.setStatus(Status.APPROVED);

        GRNDTO dto = createValidGRNDTO();

        when(grnRepository.findById(grnId))
                .thenReturn(Optional.of(grn));


        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> grnService.save(dto, grnId)
        );


        assertEquals(
                "Cannot edit processed GRN",
                exception.getMessage()
        );

        verify(grnRepository, never())
                .save(any(GRN.class));
    }


    // =========================================================
    // 8. APPROVE GRN - SUCCESS
    // =========================================================

    @Test
    void approveGRN_success() {

        // Arrange
        mockCurrentUser();

        Long grnId = 1L;

        GRN grn = createGRN();

        GRNItem grnItem = createGRNItem(grn);

        grn.getItems().add(grnItem);

        Stock stock = mock(Stock.class);

        when(grnRepository.findById(grnId))
                .thenReturn(Optional.of(grn));

        when(stockService.addStock(
                any(Item.class),
                any(Location.class),
                anyLong(),
                any(ReferenceType.class),
                any(GRNItem.class),
                anyInt(),
                anyDouble()
        )).thenReturn(stock);

        when(grnRepository.save(any(GRN.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));


        // Act
        GRN result = grnService.approve(grnId);


        // Assert
        assertNotNull(result);

        assertEquals(
                Status.APPROVED,
                result.getStatus()
        );

        assertEquals(
                currentUser,
                result.getApprovedBy()
        );

        assertNotNull(
                result.getApprovedAt()
        );


        verify(stockService)
                .addStock(
                        item,
                        Location.KANDY,
                        grn.getGrnId(),
                        ReferenceType.GRN,
                        grnItem,
                        10,
                        100.0
                );





        verify(grnRepository)
                .save(grn);
    }


    // =========================================================
    // 9. APPROVE CANCELLED GRN - FAIL
    // =========================================================

    @Test
    void approveCancelledGRN_shouldFail() {

        // Arrange
        mockCurrentUser();

        GRN grn = createGRN();

        grn.setStatus(Status.CANCELLED);

        when(grnRepository.findById(1L))
                .thenReturn(Optional.of(grn));


        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> grnService.approve(1L)
        );


        assertEquals(
                "Already processed",
                exception.getMessage()
        );


        verify(stockService, never())
                .addStock(
                        any(),
                        any(),
                        anyLong(),
                        any(),
                        any(),
                        anyInt(),
                        anyDouble()
                );
    }


    // =========================================================
    // 10. CANCEL GRN - SUCCESS
    // =========================================================

    @Test
    void cancelGRN_success() {

        // Arrange
        GRN grn = createGRN();

        when(grnRepository.findById(1L))
                .thenReturn(Optional.of(grn));

        when(grnRepository.save(any(GRN.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        String reason = "Wrong quantity received";


        // Act
        GRN result = grnService.cancel(
                1L,
                reason
        );


        // Assert
        assertNotNull(result);

        assertEquals(
                Status.CANCELLED,
                result.getStatus()
        );

        assertEquals(
                reason,
                result.getCancelReason()
        );

        assertNotNull(
                result.getCancelledAt()
        );

        verify(grnRepository)
                .save(grn);
    }


    // =========================================================
    // 11. CANCEL APPROVED GRN - FAIL
    // =========================================================

    @Test
    void cancelApprovedGRN_shouldFail() {

        // Arrange
        GRN grn = createGRN();

        grn.setStatus(Status.APPROVED);

        when(grnRepository.findById(1L))
                .thenReturn(Optional.of(grn));


        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> grnService.cancel(
                        1L,
                        "Test reason"
                )
        );


        assertEquals(
                "Cannot cancel processed GRN",
                exception.getMessage()
        );

        verify(grnRepository, never())
                .save(any(GRN.class));
    }


    // =========================================================
    // 12. SEARCH BY STATUS
    // =========================================================

    @Test
    void searchByStatus_shouldReturnMatchingGRNs() {

        // Arrange

        GRN approvedGRN = createGRN();

        approvedGRN.setGrnId(1L);

        approvedGRN.setStatus(Status.APPROVED);


        GRN cancelledGRN = createGRN();

        cancelledGRN.setGrnId(2L);

        cancelledGRN.setStatus(Status.CANCELLED);


        when(grnRepository.findAll())
                .thenReturn(
                        List.of(
                                approvedGRN,
                                cancelledGRN
                        )
                );


        // Act

        List<GRNResponseDTO> result =
                grnService.search(
                        null,
                        Status.APPROVED,
                        null,
                        null,
                        null,
                        null
                );


        // Assert

        assertNotNull(result);

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                1L,
                result.get(0).getGrnId()
        );

        assertEquals(
                Status.APPROVED.name(),
                result.get(0).getStatus()
        );


        verify(grnRepository)
                .findAll();
    }


    // =========================================================
    // 13. SEARCH BY DATE RANGE
    // =========================================================

    @Test
    void searchByDateRange_shouldReturnMatchingGRNs() {

        // Arrange

        GRN grn1 = createGRN();

        grn1.setGrnId(1L);

        grn1.setGrnDate(
                LocalDate.of(2026, 8, 10)
        );


        GRN grn2 = createGRN();

        grn2.setGrnId(2L);

        grn2.setGrnDate(
                LocalDate.of(2026, 8, 15)
        );


        GRN grn3 = createGRN();

        grn3.setGrnId(3L);

        grn3.setGrnDate(
                LocalDate.of(2026, 8, 20)
        );


        when(grnRepository.findAll())
                .thenReturn(
                        List.of(
                                grn1,
                                grn2,
                                grn3
                        )
                );


        // Act

        List<GRNResponseDTO> result =
                grnService.search(
                        null,
                        null,
                        null,
                        "DATE_RANGE",
                        "2026-08-10",
                        "2026-08-15"
                );


        // Assert

        assertNotNull(result);

        assertEquals(
                2,
                result.size()
        );
    }


    // =========================================================
    // 14. GET BY ID - SUCCESS
    // =========================================================

    @Test
    void getById_success() {

        // Arrange

        GRN grn = createGRN();

        when(grnRepository.findById(1L))
                .thenReturn(Optional.of(grn));


        // Act

        GRNResponseDTO result =
                grnService.getById(1L);


        // Assert

        assertNotNull(result);

        assertEquals(
                1L,
                result.getGrnId()
        );

        assertEquals(
                "GRN-00001",
                result.getGrnNumber()
        );

        assertEquals(
                "PO-0001",
                result.getPoNumber()
        );

        assertEquals(
                Location.KANDY,
                result.getLocation()
        );

        assertEquals(
                Status.UNAPPROVED.name(),
                result.getStatus()
        );


        verify(grnRepository)
                .findById(1L);
    }
}