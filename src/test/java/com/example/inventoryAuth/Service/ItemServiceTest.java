package com.example.inventoryAuth.Service;

import com.example.inventoryAuth.DTO.ItemDTO;
import com.example.inventoryAuth.DTO.ItemResponse;
import com.example.inventoryAuth.DTO.ItemResponseSearchByKeyword;
import com.example.inventoryAuth.Entity.Item;
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
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepo;

    @Mock
    private ItemGroupRepository groupRepo;

    @InjectMocks
    private ItemService service;


    // =========================================================
    // HELPER
    // =========================================================

    private ItemGroup createGroup() {

        ItemGroup group = new ItemGroup();

        group.setCode("GRP001");
        group.setName("Electronics");

        return group;
    }


    private Item createItem() {

        Item item = new Item();

        item.setItemCode("ITEM001");
        item.setItemName("Laptop");
        item.setItemDescription("Laptop computer");
        item.setActive(true);
        item.setItemGroup(createGroup());

        return item;
    }


    // =========================================================
    // 1. CREATE SUCCESS
    // =========================================================

    @Test
    void create_success() {

        Item item = createItem();

        ItemGroup group = createGroup();

        when(groupRepo.findByCode("GRP001"))
                .thenReturn(Optional.of(group));

        when(itemRepo.save(item))
                .thenReturn(item);

        Item result =
                service.create(item);

        assertNotNull(result);

        assertEquals(
                "ITEM001",
                result.getItemCode()
        );

        assertEquals(
                "Laptop",
                result.getItemName()
        );

        assertEquals(
                "GRP001",
                result.getItemGroup().getCode()
        );

        verify(groupRepo)
                .findByCode("GRP001");

        verify(itemRepo)
                .save(item);
    }


    // =========================================================
    // 2. CREATE GROUP NOT FOUND
    // =========================================================

    @Test
    void create_groupNotFound_shouldFail() {

        Item item = createItem();

        when(groupRepo.findByCode("GRP001"))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.create(item)
                );

        assertEquals(
                "Group not found",
                exception.getMessage()
        );

        verify(itemRepo, never())
                .save(any(Item.class));
    }


    // =========================================================
    // 3. GET ITEM BY CODE SUCCESS
    // =========================================================

    @Test
    void getItemByCode_success() {

        Item item = createItem();

        when(itemRepo.findByItemCode("ITEM001"))
                .thenReturn(Optional.of(item));

        ItemResponse result =
                service.getItemByCode("ITEM001");

        assertNotNull(result);

        assertEquals(
                "ITEM001",
                result.getItemCode()
        );

        assertEquals(
                "Laptop",
                result.getItemName()
        );

        assertEquals(
                "Laptop computer",
                result.getItemDescription()
        );

        assertEquals(
                "Electronics",
                result.getItemGroupName()
        );

        assertEquals(
                true,
                result.getActive()
        );

        verify(itemRepo)
                .findByItemCode("ITEM001");
    }


    // =========================================================
    // 4. GET ITEM BY CODE NOT FOUND
    // =========================================================

    @Test
    void getItemByCode_notFound_shouldFail() {

        when(itemRepo.findByItemCode("ITEM999"))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.getItemByCode("ITEM999")
                );

        assertEquals(
                "Item Not Found",
                exception.getMessage()
        );

        verify(itemRepo)
                .findByItemCode("ITEM999");
    }


    // =========================================================
    // 5. GET SELECTED ITEM
    // =========================================================

    @Test
    void getSelectedItem_success() {

        Item item = createItem();

        item.setMaintainReorder(true);
        item.setMinimumLevel(10);
        item.setReorderQuantity(20);

        when(itemRepo.findByItemCode("ITEM001"))
                .thenReturn(Optional.of(item));

        ItemDTO result =
                service.getSelectedItem("ITEM001");

        assertNotNull(result);

        assertEquals(
                "ITEM001",
                result.getItemCode()
        );

        assertEquals(
                "Laptop",
                result.getItemName()
        );

        assertEquals(
                "Laptop computer",
                result.getItemDescription()
        );

        assertEquals(
                true,
                result.getActive()
        );

        assertEquals(
                true,
                result.getMaintainReorder()
        );

        assertEquals(
                10,
                result.getMinimumLevel()
        );

        assertEquals(
                20,
                result.getReorderQuantity()
        );
    }


    // =========================================================
    // 6. GET SELECTED ITEM NOT FOUND
    // =========================================================

    @Test
    void getSelectedItem_notFound_shouldFail() {

        when(itemRepo.findByItemCode("ITEM999"))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.getSelectedItem("ITEM999")
                );

        assertEquals(
                "Item not found: ITEM999",
                exception.getMessage()
        );
    }


    // =========================================================
    // 7. UPDATE ITEM SUCCESS
    // =========================================================

    @Test
    void updateItem_success() {

        Item existing = createItem();

        ItemDTO dto = new ItemDTO();

        dto.setItemCode("ITEM002");
        dto.setItemName("Updated Laptop");
        dto.setItemDescription("Updated description");
        dto.setActive(false);
        dto.setMaintainReorder(true);
        dto.setMinimumLevel(15);
        dto.setReorderQuantity(30);

        when(itemRepo.findByItemCode("ITEM001"))
                .thenReturn(Optional.of(existing));

        when(itemRepo.save(existing))
                .thenReturn(existing);

        Item result =
                service.updateItem(
                        "ITEM001",
                        dto
                );

        assertNotNull(result);

        assertEquals(
                "ITEM002",
                result.getItemCode()
        );

        assertEquals(
                "Updated Laptop",
                result.getItemName()
        );

        assertEquals(
                "Updated description",
                result.getItemDescription()
        );

        assertEquals(
                false,
                result.getActive()
        );

        assertEquals(
                true,
                result.getMaintainReorder()
        );

        assertEquals(
                15,
                result.getMinimumLevel()
        );

        assertEquals(
                30,
                result.getReorderQuantity()
        );

        verify(itemRepo)
                .findByItemCode("ITEM001");

        verify(itemRepo)
                .save(existing);
    }


    // =========================================================
    // 8. UPDATE ITEM NOT FOUND
    // =========================================================

    @Test
    void updateItem_notFound_shouldFail() {

        ItemDTO dto = new ItemDTO();

        dto.setItemName("New Laptop");

        when(itemRepo.findByItemCode("ITEM999"))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.updateItem(
                                "ITEM999",
                                dto
                        )
                );

        assertEquals(
                "Item not found: ITEM999",
                exception.getMessage()
        );

        verify(itemRepo, never())
                .save(any(Item.class));
    }


    // =========================================================
    // 9. SEARCH ITEMS BY KEYWORD
    // =========================================================

    @Test
    void searchItemsByKeyword_success() {

        Item item = createItem();

        when(
                itemRepo
                        .findByItemNameContainingIgnoreCaseOrItemCodeContainingIgnoreCaseOrItemDescriptionContainingIgnoreCase(
                                "lap",
                                "lap",
                                "lap"
                        )
        ).thenReturn(List.of(item));

        List<ItemResponseSearchByKeyword> result =
                service.searchItemsByKeyword("lap");

        assertNotNull(result);

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                "ITEM001",
                result.get(0).getItemCode()
        );

        assertEquals(
                "Laptop",
                result.get(0).getItemName()
        );

        assertEquals(
                "Laptop computer",
                result.get(0).getDescription()
        );

        verify(itemRepo)
                .findByItemNameContainingIgnoreCaseOrItemCodeContainingIgnoreCaseOrItemDescriptionContainingIgnoreCase(
                        "lap",
                        "lap",
                        "lap"
                );
    }


    // =========================================================
    // 10. GET ALL ITEMS UNPAGINATED
    // =========================================================

    @Test
    void getAllItemsUnpaginated_success() {

        Item item1 = createItem();

        Item item2 = createItem();

        item2.setItemCode("ITEM002");
        item2.setItemName("Mouse");

        when(itemRepo.findAll())
                .thenReturn(List.of(item1, item2));

        List<ItemResponseSearchByKeyword> result =
                service.getAllItemsUnpaginated();

        assertNotNull(result);

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                "ITEM001",
                result.get(0).getItemCode()
        );

        assertEquals(
                "ITEM002",
                result.get(1).getItemCode()
        );

        verify(itemRepo)
                .findAll();
    }


    // =========================================================
    // 11. GET ITEMS BY GROUP
    // =========================================================

    @Test
    void getItemsByGroup_success() {

        Item item = createItem();

        Page<Item> page =
                new PageImpl<>(
                        List.of(item)
                );

        when(
                itemRepo.findByItemGroup_Code(
                        eq("GRP001"),
                        any(PageRequest.class)
                )
        ).thenReturn(page);

        Page<ItemResponse> result =
                service.getItemsByGroup(
                        "GRP001",
                        0,
                        "name",
                        "ASC"
                );

        assertNotNull(result);

        assertEquals(
                1,
                result.getContent().size()
        );

        assertEquals(
                "ITEM001",
                result.getContent()
                        .get(0)
                        .getItemCode()
        );

        assertEquals(
                "Laptop",
                result.getContent()
                        .get(0)
                        .getItemName()
        );

        verify(itemRepo)
                .findByItemGroup_Code(
                        eq("GRP001"),
                        any(PageRequest.class)
                );
    }


    // =========================================================
    // 12. GET ITEMS BY ACTIVE STATUS
    // =========================================================

    @Test
    void getItemsByActiveStatus_success() {

        Item item = createItem();

        Page<Item> page =
                new PageImpl<>(
                        List.of(item)
                );

        when(
                itemRepo.findByActive(
                        eq(true),
                        any(PageRequest.class)
                )
        ).thenReturn(page);

        Page<ItemResponse> result =
                service.getItemsByActiveStatus(
                        true,
                        0,
                        "code",
                        "ASC"
                );

        assertNotNull(result);

        assertEquals(
                1,
                result.getContent().size()
        );

        assertEquals(
                "ITEM001",
                result.getContent()
                        .get(0)
                        .getItemCode()
        );

        verify(itemRepo)
                .findByActive(
                        eq(true),
                        any(PageRequest.class)
                );
    }
}