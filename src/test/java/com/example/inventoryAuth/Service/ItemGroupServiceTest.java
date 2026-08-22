package com.example.inventoryAuth.Service;

import com.example.inventoryAuth.DTO.ItemGroupDTO;
import com.example.inventoryAuth.DTO.ItemGroupResponse;
import com.example.inventoryAuth.Entity.ItemGroup;
import com.example.inventoryAuth.Repository.ItemGroupRepository;
import com.example.inventoryAuth.Repository.ItemRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemGroupServiceTest {

    @Mock
    private ItemGroupRepository itemGroupRepo;

    @Mock
    private ItemRepository itemRepo;

    @InjectMocks
    private ItemGroupService service;


    // =========================================================
    // 1. CREATE SUCCESS
    // =========================================================

    @Test
    void create_success() {

        ItemGroup group = new ItemGroup();
        group.setCode("GRP001");
        group.setName("Electronics");

        when(itemGroupRepo.save(group))
                .thenReturn(group);

        ItemGroup result = service.create(group);

        assertNotNull(result);
        assertEquals("GRP001", result.getCode());
        assertEquals("Electronics", result.getName());

        verify(itemGroupRepo).save(group);
    }


    // =========================================================
    // 2. GET BY CODE SUCCESS
    // =========================================================

    @Test
    void getByItemGroupCode_success() {

        ItemGroup group = new ItemGroup();

        group.setCode("GRP001");
        group.setName("Electronics");
        group.setDescription("Electronic items");
        group.setMaintainReorder(true);
        group.setGlAccount("GL001");

        when(itemGroupRepo.findByCode("GRP001"))
                .thenReturn(Optional.of(group));

        when(itemRepo.countByItemGroup_Code("GRP001"))
                .thenReturn(5L);

        ItemGroupResponse result =
                service.getByItemGroupCode("GRP001");

        assertNotNull(result);

        assertEquals("GRP001", result.getCode());
        assertEquals("Electronics", result.getName());
        assertEquals("Electronic items", result.getDescription());
        assertEquals(5L, result.getItemCount());

        verify(itemGroupRepo)
                .findByCode("GRP001");

        verify(itemRepo)
                .countByItemGroup_Code("GRP001");
    }


    // =========================================================
    // 3. GET BY CODE NOT FOUND
    // =========================================================

    @Test
    void getByItemGroupCode_notFound_shouldFail() {

        when(itemGroupRepo.findByCode("INVALID"))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.getByItemGroupCode("INVALID")
                );

        assertEquals(
                "Item Group is not found",
                exception.getMessage()
        );

        verify(itemGroupRepo)
                .findByCode("INVALID");

        verify(itemRepo, never())
                .countByItemGroup_Code(anyString());
    }


    // =========================================================
    // 4. GET SELECTED GROUP SUCCESS
    // =========================================================

    @Test
    void getSelectedGroup_success() {

        ItemGroup group = new ItemGroup();

        group.setCode("GRP001");
        group.setName("Electronics");
        group.setDescription("Electronic items");
        group.setGlAccount("GL001");
        group.setMaintainReorder(true);

        when(itemGroupRepo.findByCode("GRP001"))
                .thenReturn(Optional.of(group));

        ItemGroupDTO result =
                service.getSelectedGroup("GRP001");

        assertNotNull(result);

        assertEquals(
                "Electronics",
                result.getName()
        );

        assertEquals(
                "Electronic items",
                result.getDescription()
        );

        assertEquals(
                "GL001",
                result.getGlAccount()
        );

        assertEquals(
                true,
                result.getMaintainReorder()
        );

        verify(itemGroupRepo)
                .findByCode("GRP001");
    }


    // =========================================================
    // 5. GET SELECTED GROUP NOT FOUND
    // =========================================================

    @Test
    void getSelectedGroup_notFound_shouldFail() {

        when(itemGroupRepo.findByCode("GRP999"))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.getSelectedGroup("GRP999")
                );

        assertEquals(
                "Group not found: GRP999",
                exception.getMessage()
        );
    }


    // =========================================================
    // 6. UPDATE SUCCESS
    // =========================================================

    @Test
    void updateItemGroup_success() {

        ItemGroup existing = new ItemGroup();

        existing.setCode("GRP001");
        existing.setName("Old Name");

        ItemGroupDTO dto = new ItemGroupDTO();

        dto.setName("New Name");
        dto.setDescription("New Description");
        dto.setGlAccount("GL002");
        dto.setMaintainReorder(true);

        when(itemGroupRepo.findByCode("GRP001"))
                .thenReturn(Optional.of(existing));

        when(itemGroupRepo.save(existing))
                .thenReturn(existing);

        ItemGroup result =
                service.updateItemGroup("GRP001", dto);

        assertNotNull(result);

        assertEquals(
                "New Name",
                result.getName()
        );

        assertEquals(
                "New Description",
                result.getDescription()
        );

        assertEquals(
                "GL002",
                result.getGlAccount()
        );

        assertEquals(
                true,
                result.getMaintainReorder()
        );

        verify(itemGroupRepo)
                .findByCode("GRP001");

        verify(itemGroupRepo)
                .save(existing);
    }


    // =========================================================
    // 7. UPDATE NOT FOUND
    // =========================================================

    @Test
    void updateItemGroup_notFound_shouldFail() {

        ItemGroupDTO dto = new ItemGroupDTO();

        dto.setName("New Name");

        when(itemGroupRepo.findByCode("GRP999"))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.updateItemGroup(
                                "GRP999",
                                dto
                        )
                );

        assertEquals(
                "Item Group not found",
                exception.getMessage()
        );

        verify(itemGroupRepo, never())
                .save(any(ItemGroup.class));
    }


    // =========================================================
    // 8. GET ALL GROUPS
    // =========================================================

    @Test
    void getAllGroup_success() {

        ItemGroup group = new ItemGroup();

        group.setCode("GRP001");
        group.setName("Electronics");

        Page<ItemGroup> page =
                new PageImpl<>(
                        List.of(group)
                );

        when(itemGroupRepo.findAll(any(PageRequest.class)))
                .thenReturn(page);

        when(itemRepo.countByItemGroup_Code("GRP001"))
                .thenReturn(3L);

        Page<ItemGroupResponse> result =
                service.getAllGroup(0);

        assertNotNull(result);

        assertEquals(
                1,
                result.getContent().size()
        );

        assertEquals(
                "GRP001",
                result.getContent()
                        .get(0)
                        .getCode()
        );

        assertEquals(
                3L,
                result.getContent()
                        .get(0)
                        .getItemCount()
        );

        verify(itemGroupRepo)
                .findAll(any(PageRequest.class));

        verify(itemRepo)
                .countByItemGroup_Code("GRP001");
    }
}