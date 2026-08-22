package com.example.inventoryAuth.Service;

import com.example.inventoryAuth.DTO.StockAdjustmentDTO;
import com.example.inventoryAuth.DTO.StockAdjustmentItemDTO;
import com.example.inventoryAuth.DTO.StockAdjustmentResponseDTO;
import com.example.inventoryAuth.Entity.*;
import com.example.inventoryAuth.Repository.StockAdjustmentRepository;
import com.example.inventoryAuth.Repository.StockRepository;
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
class StockAdjustmentServiceTest {

    @Mock
    private StockAdjustmentRepository repository;

    @Mock
    private StockRepository stockRepository;

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
    private Stock stock;

    @Mock
    private Item item;

    @InjectMocks
    private StockAdjustmentService service;


    // =====================================================
    // SECURITY SETUP
    // Only call this in tests that use save()
    // =====================================================

    private void setupUser() {

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(userDetails);

        when(userDetails.getUsername())
                .thenReturn("testuser");

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(currentUser));

        when(currentUser.getLocation())
                .thenReturn(Location.KANDY);

        SecurityContextHolder.setContext(securityContext);
    }


    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }


    // =====================================================
    // DTO
    // =====================================================

    private StockAdjustmentDTO createValidDTO() {

        StockAdjustmentDTO dto = new StockAdjustmentDTO();

        dto.setAdjustmentDate(LocalDate.now());
        dto.setReason("Stock correction");
        dto.setComment("Test comment");

        StockAdjustmentItemDTO itemDTO =
                new StockAdjustmentItemDTO();

        itemDTO.setStockId(1L);
        itemDTO.setQuantity(5);

        dto.setItems(List.of(itemDTO));

        return dto;
    }


    // =====================================================
    // 1. SAVE SUCCESS
    // =====================================================
    @Test
    void save_success() {

        // =========================
        // CURRENT USER
        // =========================

        setupUser();

        // =========================
        // DTO
        // =========================

        StockAdjustmentDTO dto = createValidDTO();

        // =========================
        // STOCK ID
        // =========================

        when(stock.getStockId())
                .thenReturn(1L);

        // =========================
        // IMPORTANT
        // findById() is called twice
        // =========================

        when(stockRepository.findById(1L))
                .thenReturn(Optional.of(stock));

        // =========================
        // SAVE
        // =========================

        when(repository.save(any(StockAdjustment.class)))
                .thenAnswer(invocation -> {

                    StockAdjustment adjustment =
                            invocation.getArgument(0);

                    if (adjustment.getAdjustmentId() == null) {
                        adjustment.setAdjustmentId(1L);
                    }

                    return adjustment;
                });

        // =========================
        // ACT
        // =========================

        StockAdjustment result =
                service.save(dto);

        // =========================
        // ASSERT
        // =========================

        assertNotNull(result);

        assertEquals(
                1L,
                result.getAdjustmentId()
        );

        assertEquals(
                "ADJ-00001",
                result.getAdjustmentNo()
        );

        assertEquals(
                Status.APPROVED,
                result.getStatus()
        );

        assertEquals(
                Location.KANDY,
                result.getLocation()
        );

        assertEquals(
                "Stock correction",
                result.getReason()
        );

        assertEquals(
                "Test comment",
                result.getComment()
        );

        assertEquals(
                1,
                result.getItems().size()
        );

        assertEquals(
                5,
                result.getItems()
                        .get(0)
                        .getQuantity()
        );

        // =========================
        // VERIFY STOCK UPDATE
        // =========================

        verify(stockService)
                .adjustStockDirect(
                        stock,
                        5
                );


        // =========================
        // VERIFY REPOSITORY
        // =========================

        verify(repository, times(2))
                .save(any(StockAdjustment.class));

        verify(stockRepository, times(2))
                .findById(1L);
    }


    // =====================================================
    // 2. ITEMS NULL
    // =====================================================

    @Test
    void save_withoutItems_shouldFail() {

        setupUser();

        StockAdjustmentDTO dto = createValidDTO();

        dto.setItems(null);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.save(dto)
                );

        assertEquals(
                "Items required",
                exception.getMessage()
        );
    }


    // =====================================================
    // 3. ITEMS EMPTY
    // =====================================================

    @Test
    void save_withEmptyItems_shouldFail() {

        setupUser();

        StockAdjustmentDTO dto = createValidDTO();

        dto.setItems(new ArrayList<>());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.save(dto)
                );

        assertEquals(
                "Items required",
                exception.getMessage()
        );
    }


    // =====================================================
    // 4. STOCK NOT FOUND
    // =====================================================

    @Test
    void save_stockNotFound_shouldFail() {

        setupUser();

        StockAdjustmentDTO dto = createValidDTO();

        when(stockRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.save(dto)
                );

        assertEquals(
                "Stock not found",
                exception.getMessage()
        );
    }


    // =====================================================
    // 5. ZERO QUANTITY
    // =====================================================

    @Test
    void save_zeroQuantity_shouldFail() {

        setupUser();

        StockAdjustmentDTO dto = createValidDTO();

        dto.getItems()
                .get(0)
                .setQuantity(0);

        when(stockRepository.findById(1L))
                .thenReturn(Optional.of(stock));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.save(dto)
                );

        assertEquals(
                "Invalid quantity",
                exception.getMessage()
        );
    }


    // =====================================================
    // 6. SEARCH BY LOCATION
    // =====================================================

    @Test
    void search_byLocation_shouldReturnMatchingData() {

        StockAdjustment kandy =
                new StockAdjustment();

        kandy.setAdjustmentId(1L);
        kandy.setAdjustmentNo("ADJ-00001");
        kandy.setLocation(Location.KANDY);
        kandy.setAdjustmentDate(LocalDate.now());


        StockAdjustment colombo =
                new StockAdjustment();

        colombo.setAdjustmentId(2L);
        colombo.setAdjustmentNo("ADJ-00002");
        colombo.setLocation(Location.HEAD_OFFICE);
        colombo.setAdjustmentDate(LocalDate.now());


        when(repository.findAll())
                .thenReturn(
                        List.of(kandy, colombo)
                );


        List<StockAdjustment> result =
                service.search(
                        Location.KANDY,
                        null,
                        null,
                        null,
                        null
                );


        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                Location.KANDY,
                result.get(0).getLocation()
        );
    }


    // =====================================================
    // 7. SEARCH BY KEYWORD
    // =====================================================

    @Test
    void search_byKeyword_shouldReturnMatchingData() {

        StockAdjustment adjustment =
                new StockAdjustment();

        adjustment.setAdjustmentId(1L);
        adjustment.setAdjustmentNo("ADJ-00001");
        adjustment.setLocation(Location.KANDY);
        adjustment.setAdjustmentDate(LocalDate.now());


        when(repository.findAll())
                .thenReturn(
                        List.of(adjustment)
                );


        List<StockAdjustment> result =
                service.search(
                        null,
                        "ADJ-00001",
                        null,
                        null,
                        null
                );


        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                "ADJ-00001",
                result.get(0).getAdjustmentNo()
        );
    }


    // =====================================================
    // 8. GET BY ID
    // =====================================================

    @Test
    void getById_success() {

        StockAdjustment adjustment =
                new StockAdjustment();

        adjustment.setAdjustmentId(1L);
        adjustment.setAdjustmentNo("ADJ-00001");
        adjustment.setAdjustmentDate(LocalDate.now());
        adjustment.setLocation(Location.KANDY);
        adjustment.setStatus(Status.APPROVED);
        adjustment.setReason("Stock correction");
        adjustment.setComment("Test");


        adjustment.setItems(
                new ArrayList<>()
        );


        when(repository.findById(1L))
                .thenReturn(
                        Optional.of(adjustment)
                );


        StockAdjustmentResponseDTO result =
                service.getById(1L);


        assertNotNull(result);

        assertEquals(
                1L,
                result.getAdjustmentId()
        );

        assertEquals(
                "ADJ-00001",
                result.getAdjustmentNo()
        );

        assertEquals(
                Status.APPROVED.name(),
                result.getStatus()
        );

        assertEquals(
                Location.KANDY,
                result.getLocation()
        );

        assertEquals(
                "Stock correction",
                result.getReason()
        );
    }
}