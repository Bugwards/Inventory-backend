package com.example.inventoryAuth.Service;

import com.example.inventoryAuth.DTO.OpeningStockDTO;
import com.example.inventoryAuth.DTO.OpeningStockItemDTO;
import com.example.inventoryAuth.DTO.OpeningStockResponseDTO;
import com.example.inventoryAuth.Entity.*;
import com.example.inventoryAuth.Repository.ItemRepository;
import com.example.inventoryAuth.Repository.LocationRepository;
import com.example.inventoryAuth.Repository.OpeningStockRepository;
import com.example.inventoryAuth.Repository.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
class OpeningStockServiceTest {

    // =========================================================
    // MOCKS
    // =========================================================

    @Mock
    private OpeningStockRepository repository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private StockService stockService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BinCardServiceTest binCardService;

    @Mock
    private User currentUser;

    @Mock
    private UserDetails userDetails;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Item item;

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private OpeningStockService openingStockService;


    // =========================================================
    // SETUP
    // =========================================================

    @BeforeEach
    void setUp() {
        /*
         * Do NOT put Mockito stubbing here.
         *
         * Some tests don't call getCurrentUser().
         * If we stub the security context here, Mockito will
         * report UnnecessaryStubbingException.
         */
        SecurityContextHolder.clearContext();
    }


    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }


    // =========================================================
    // CURRENT USER HELPER
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
    // VALID DTO
    // =========================================================

    private OpeningStockDTO createValidDTO() {

        OpeningStockDTO dto = new OpeningStockDTO();

        dto.setOpeningDate(LocalDate.now());

        OpeningStockItemDTO itemDTO =
                new OpeningStockItemDTO();

        itemDTO.setItemCode("ITEM-001");
        itemDTO.setQuantity(10);
        itemDTO.setUnitPrice(100.0);

        dto.setItems(List.of(itemDTO));

        return dto;
    }


    // =========================================================
    // HELPER - LOCATION ENTITY
    // =========================================================

    private Location createLocation(Long id, String code) {
        Location location = new Location();
        location.setId(id);
        location.setCode(code);
        location.setName(code);
        location.setActive(true);
        return location;
    }

    private final Location kandyLocation = createLocation(1L, "KANDY");


    // =========================================================
    // CREATE OPENING STOCK ENTITY
    // =========================================================

    private OpeningStock createOpeningStock() {

        OpeningStock os = new OpeningStock();

        os.setOpeningStockId(1L);
        os.setEntryNo("OS-00001");
        os.setOpeningDate(LocalDate.now());
        os.setLocation(kandyLocation);
        os.setStatus(Status.UNAPPROVED);
        os.setTotalValue(1000.0);
        os.setCreatedBy(currentUser);

        os.setItems(new ArrayList<>());

        return os;
    }


    // =========================================================
    // CREATE OPENING STOCK ITEM
    // =========================================================

    private OpeningStockItem createOpeningStockItem(
            OpeningStock os) {

        OpeningStockItem itemEntity =
                new OpeningStockItem();

        itemEntity.setOpeningStockItemId(1L);
        itemEntity.setOpeningStock(os);
        itemEntity.setItem(item);
        itemEntity.setQuantity(10);
        itemEntity.setUnitPrice(100.0);

        return itemEntity;
    }


    // =========================================================
    // 1. CREATE SUCCESS
    // =========================================================

    @Test
    void createOpeningStock_success() {

        // Arrange
        mockCurrentUser();

        OpeningStockDTO dto =
                createValidDTO();

        when(currentUser.getLocation())
                .thenReturn(kandyLocation);

        when(itemRepository.findByItemCode("ITEM-001"))
                .thenReturn(Optional.of(item));

        when(repository.save(any(OpeningStock.class)))
                .thenAnswer(invocation -> {

                    OpeningStock os =
                            invocation.getArgument(0);

                    if (os.getOpeningStockId() == null) {
                        os.setOpeningStockId(1L);
                    }

                    return os;
                });

        // Act
        OpeningStock result =
                openingStockService.save(dto, null);

        // Assert
        assertNotNull(result);

        assertEquals(
                1L,
                result.getOpeningStockId()
        );

        assertEquals(
                "OS-00001",
                result.getEntryNo()
        );

        assertEquals(
                Status.UNAPPROVED,
                result.getStatus()
        );

        assertEquals(
                kandyLocation,
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

        verify(repository, times(2))
                .save(any(OpeningStock.class));

        verify(itemRepository)
                .findByItemCode("ITEM-001");
    }


    // =========================================================
    // 2. CREATE WITHOUT ITEMS
    // =========================================================

    @Test
    void createOpeningStock_withoutItems_shouldFail() {

        // Arrange
        mockCurrentUser();

        OpeningStockDTO dto =
                createValidDTO();

        dto.setItems(null);

        // Act + Assert
        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> openingStockService.save(
                                dto,
                                null
                        )
                );

        assertEquals(
                "Items required",
                exception.getMessage()
        );

        verify(repository, never())
                .save(any(OpeningStock.class));
    }


    // =========================================================
    // 3. CREATE WITH EMPTY ITEMS
    // =========================================================

    @Test
    void createOpeningStock_withEmptyItems_shouldFail() {

        // Arrange
        mockCurrentUser();

        OpeningStockDTO dto =
                createValidDTO();

        dto.setItems(new ArrayList<>());

        // Act + Assert
        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> openingStockService.save(
                                dto,
                                null
                        )
                );

        assertEquals(
                "Items required",
                exception.getMessage()
        );

        verify(repository, never())
                .save(any(OpeningStock.class));
    }


    // =========================================================
    // 4. CREATE INVALID ITEM
    // =========================================================

    @Test
    void createOpeningStock_withInvalidItem_shouldFail() {

        // Arrange
        mockCurrentUser();

        OpeningStockDTO dto =
                createValidDTO();

        when(itemRepository.findByItemCode("ITEM-001"))
                .thenReturn(Optional.empty());

        // Act + Assert
        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> openingStockService.save(
                                dto,
                                null
                        )
                );

        assertEquals(
                "Item not found",
                exception.getMessage()
        );

        verify(repository, never())
                .save(any(OpeningStock.class));
    }


    // =========================================================
    // 5. UPDATE SUCCESS
    // =========================================================

    @Test
    void updateOpeningStock_success() {

        // Arrange
        mockCurrentUser();

        Long id = 1L;

        OpeningStock existing =
                createOpeningStock();

        OpeningStockDTO dto =
                createValidDTO();

        when(repository.findById(id))
                .thenReturn(Optional.of(existing));

        when(itemRepository.findByItemCode("ITEM-001"))
                .thenReturn(Optional.of(item));

        when(repository.save(any(OpeningStock.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        // Act
        OpeningStock result =
                openingStockService.save(dto, id);

        // Assert
        assertNotNull(result);

        assertEquals(
                Status.UNAPPROVED,
                result.getStatus()
        );

        assertEquals(
                1,
                result.getItems().size()
        );

        assertEquals(
                1000.0,
                result.getTotalValue()
        );

        verify(repository, times(2))
                .save(existing);
    }


    // =========================================================
    // 6. UPDATE APPROVED RECORD
    // =========================================================

    @Test
    void updateApprovedOpeningStock_shouldFail() {

        // Arrange
        mockCurrentUser();

        OpeningStock os =
                createOpeningStock();

        os.setStatus(Status.APPROVED);

        OpeningStockDTO dto =
                createValidDTO();

        when(repository.findById(1L))
                .thenReturn(Optional.of(os));

        // Act + Assert
        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> openingStockService.save(
                                dto,
                                1L
                        )
                );

        assertEquals(
                "Cannot edit processed record",
                exception.getMessage()
        );

        verify(repository, never())
                .save(any(OpeningStock.class));
    }


    // =========================================================
    // 7. UPDATE NON EXISTING
    // =========================================================

    @Test
    void updateOpeningStock_notFound_shouldFail() {

        // Arrange
        mockCurrentUser();

        OpeningStockDTO dto =
                createValidDTO();

        when(repository.findById(999L))
                .thenReturn(Optional.empty());

        // Act + Assert
        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> openingStockService.save(
                                dto,
                                999L
                        )
                );

        assertEquals(
                "Opening Stock not found",
                exception.getMessage()
        );
    }





    // =========================================================
    // 9. APPROVE NOT FOUND
    // =========================================================

    @Test
    void approveOpeningStock_notFound_shouldFail() {

        // IMPORTANT:
        // approve() calls getCurrentUser() first
        mockCurrentUser();

        when(repository.findById(999L))
                .thenReturn(Optional.empty());

        // Act + Assert
        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> openingStockService.approve(999L)
                );

        assertEquals(
                "Opening Stock not found",
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
    // 10. APPROVE ALREADY APPROVED
    // =========================================================

    @Test
    void approveAlreadyApproved_shouldFail() {

        // Arrange
        mockCurrentUser();

        OpeningStock os =
                createOpeningStock();

        os.setStatus(Status.APPROVED);

        when(repository.findById(1L))
                .thenReturn(Optional.of(os));

        // Act + Assert
        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> openingStockService.approve(1L)
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
    // 11. CANCEL SUCCESS
    // =========================================================

    @Test
    void cancelOpeningStock_success() {

        // Arrange
        OpeningStock os =
                createOpeningStock();

        when(repository.findById(1L))
                .thenReturn(Optional.of(os));

        when(repository.save(any(OpeningStock.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        // Act
        OpeningStock result =
                openingStockService.cancel(
                        1L,
                        "Opening stock entered incorrectly"
                );

        // Assert
        assertNotNull(result);

        assertEquals(
                Status.CANCELLED,
                result.getStatus()
        );

        assertNotNull(
                result.getCancelledAt()
        );

        verify(repository)
                .save(os);
    }


    // =========================================================
    // 12. CANCEL APPROVED
    // =========================================================

    @Test
    void cancelApprovedOpeningStock_shouldFail() {

        // Arrange
        OpeningStock os =
                createOpeningStock();

        os.setStatus(Status.APPROVED);

        when(repository.findById(1L))
                .thenReturn(Optional.of(os));

        // Act + Assert
        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> openingStockService.cancel(
                                1L,
                                "Test reason"
                        )
                );

        assertEquals(
                "Cannot cancel processed record",
                exception.getMessage()
        );

        verify(repository, never())
                .save(any(OpeningStock.class));
    }


    // =========================================================
    // 13. SEARCH BY STATUS
    // =========================================================

    @Test
    void searchByStatus_shouldReturnMatchingRecords() {

        // Arrange
        OpeningStock approved =
                createOpeningStock();

        approved.setOpeningStockId(1L);
        approved.setStatus(Status.APPROVED);

        OpeningStock cancelled =
                createOpeningStock();

        cancelled.setOpeningStockId(2L);
        cancelled.setStatus(Status.CANCELLED);

        when(repository.findAll())
                .thenReturn(
                        List.of(
                                approved,
                                cancelled
                        )
                );

        // Act
        List<OpeningStock> result =
                openingStockService.search(
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
                Status.APPROVED,
                result.get(0).getStatus()
        );
    }


    // =========================================================
    // 14. SEARCH BY KEYWORD
    // =========================================================

    @Test
    void searchByKeyword_shouldReturnMatchingRecord() {

        // Arrange
        OpeningStock os1 =
                createOpeningStock();

        os1.setOpeningStockId(1L);
        os1.setEntryNo("OS-00001");

        OpeningStock os2 =
                createOpeningStock();

        os2.setOpeningStockId(2L);
        os2.setEntryNo("OS-00002");

        when(repository.findAll())
                .thenReturn(
                        List.of(
                                os1,
                                os2
                        )
                );

        // Act
        List<OpeningStock> result =
                openingStockService.search(
                        null,
                        null,
                        "OS-00001",
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
                "OS-00001",
                result.get(0).getEntryNo()
        );
    }


    // =========================================================
    // 15. GET BY ID SUCCESS
    // =========================================================

    @Test
    void getById_success() {

        // Arrange
        OpeningStock os =
                createOpeningStock();

        OpeningStockItem osItem =
                createOpeningStockItem(os);

        os.getItems().add(osItem);

        when(item.getItemCode())
                .thenReturn("ITEM-001");

        when(item.getItemName())
                .thenReturn("Test Item");

        when(repository.findById(1L))
                .thenReturn(Optional.of(os));

        // Act
        OpeningStockResponseDTO result =
                openingStockService.getById(1L);

        // Assert
        assertNotNull(result);

        assertEquals(
                1L,
                result.getOpeningStockId()
        );

        assertEquals(
                "OS-00001",
                result.getEntryNo()
        );

        assertEquals(
                os.getOpeningDate(),
                result.getOpeningDate()
        );

        assertEquals(
                1000.0,
                result.getTotalValue()
        );

        assertEquals(
                Status.UNAPPROVED.name(),
                result.getStatus()
        );

        assertNotNull(
                result.getItems()
        );

        assertEquals(
                1,
                result.getItems().size()
        );

        assertEquals(
                "ITEM-001",
                result.getItems()
                        .get(0)
                        .getItemCode()
        );

        assertEquals(
                "Test Item",
                result.getItems()
                        .get(0)
                        .getItemName()
        );

        assertEquals(
                10,
                result.getItems()
                        .get(0)
                        .getQuantity()
        );

        assertEquals(
                100.0,
                result.getItems()
                        .get(0)
                        .getUnitPrice()
        );

        verify(repository)
                .findById(1L);
    }
}