package com.example.inventoryAuth.Service;

import com.example.inventoryAuth.DTO.ItemDTO;
import com.example.inventoryAuth.DTO.ItemResponse;
import com.example.inventoryAuth.DTO.ItemResponseSearchByKeyword;
import com.example.inventoryAuth.Entity.Item;
import com.example.inventoryAuth.Entity.ItemGroup;
import com.example.inventoryAuth.Repository.ItemGroupRepository;
import com.example.inventoryAuth.Repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class ItemService {

    @Autowired
    ItemRepository itemRepo;

    @Autowired
    ItemGroupRepository groupRepo;

    public Item create(Item item) {
        String code = item.getItemGroup().getCode();

        ItemGroup group = groupRepo.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        item.setItemGroup(group);

        return itemRepo.save(item);
    }

    public Page<ItemResponse> getAllItem(int page) {
        return itemRepo.findAll(PageRequest.of(page, 5)).map(this::mapToResponse);
    }

    public ItemResponse getItemByCode(String itemCode) {
        Item item = itemRepo.findByItemCode(itemCode).orElseThrow(()->new RuntimeException("Item Not Found"));

        return mapToResponse(item);
    }

    public Object getItemByName(String itemName, Integer page){
        if (page != null) {
            Page<Item> items = itemRepo.findByItemNameContainingIgnoreCase(itemName, PageRequest.of(page, 5));
            return items.map(this::mapToResponse);
        }

        List<Item> items = itemRepo.findByItemNameContainingIgnoreCase(itemName);
        return items.stream()
                .map(this::mapToResponse)
                .toList();
    }


    public Object getItemSortByItemCode(Integer page , String sortBy) {
        if (sortBy.equalsIgnoreCase("ASC")){
            if(page != null){
                PageRequest pageable = PageRequest.of(
                        page,
                        5,
                        Sort.by("itemCode").ascending()
                );
                return itemRepo.findAll(pageable).map(this::mapToResponse);
            }
            List<ItemResponse> list = itemRepo.findAll(Sort.by("itemCode").ascending())
                    .stream()
                    .map(this::mapToResponse)
                    .toList();
            return list;
        }
        else{
            if(page != null){
                PageRequest pageable = PageRequest.of(
                        page,
                        5,
                        Sort.by("itemCode").descending()
                );
                return itemRepo.findAll(pageable).map(this::mapToResponse);
            }
            List<ItemResponse> list = itemRepo.findAll(Sort.by("itemCode").descending())
                    .stream()
                    .map(this::mapToResponse)
                    .toList();
            return list;
        }

    }
    public Object getItemSortByItemName(Integer page, String sortBy) {
        if (sortBy.equalsIgnoreCase("ASC")){
            if(page != null){
                PageRequest pageable = PageRequest.of(
                        page,
                        5,
                        Sort.by("itemName").ascending()
                );
                return itemRepo.findAll(pageable).map(this::mapToResponse);
            }
            List<ItemResponse> list = itemRepo.findAll(Sort.by("itemName").ascending())
                    .stream()
                    .map(this::mapToResponse)
                    .toList();
            return list;
        }else {
            if(page != null){
                PageRequest pageable = PageRequest.of(
                        page,
                        5,
                        Sort.by("itemName").descending()
                );
                return itemRepo.findAll(pageable).map(this::mapToResponse);
            }
            List<ItemResponse> list = itemRepo.findAll(Sort.by("itemName").descending())
                    .stream()
                    .map(this::mapToResponse)
                    .toList();
            return list;
        }


    }
    public Object getItemSortByItemGroup(Integer page, String sortBy) {
        if (sortBy.equalsIgnoreCase("ASC")) {
            if (page != null) {
                PageRequest pageable = PageRequest.of(
                        page,
                        5,
                        Sort.by("itemGroup.name").ascending()
                );
                return itemRepo.findAll(pageable).map(this::mapToResponse);

            }
            List<ItemResponse> list = itemRepo.findAll(Sort.by("itemGroup.name").ascending())
                    .stream()
                    .map(this::mapToResponse)
                    .toList();

            return list;

        }else{
            if (page != null) {
                PageRequest pageable = PageRequest.of(
                        page,
                        5,
                        Sort.by("itemGroup.name").descending()
                );
                return itemRepo.findAll(pageable).map(this::mapToResponse);

            }
            List<ItemResponse> list = itemRepo.findAll(Sort.by("itemGroup.name").descending())
                    .stream()
                    .map(this::mapToResponse)
                    .toList();

            return list;


        }

    }

    private ItemResponse mapToResponse(Item item) {

        ItemResponse itemResponse = new ItemResponse();

        itemResponse.setItemCode(item.getItemCode());
        itemResponse.setItemName(item.getItemName());
        itemResponse.setItemDescription(item.getItemDescription());

        itemResponse.setItemGroupName(item.getItemGroup().getName());

        itemResponse.setActive(item.getActive());
        itemResponse.setUnitOfMeasurement(item.getUnitOfMeasurement());

        return itemResponse;
    }

    public ItemDTO getSelectedItem(String itemCode) {

        Item item = itemRepo.findByItemCode(itemCode)
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemCode));

        ItemDTO itemDto = new ItemDTO();

        itemDto.setItemCode(item.getItemCode());
        itemDto.setItemName(item.getItemName());
        itemDto.setItemDescription(item.getItemDescription());
        itemDto.setActive(item.getActive());
        itemDto.setMaintainReorder(item.getMaintainReorder());
        itemDto.setMinimumLevel(item.getMinimumLevel());
        itemDto.setReorderQuantity(item.getReorderQuantity());

        return itemDto;
    }

    public Item updateItem(String itemCode, ItemDTO itemDTO) {

        Item existingItem = itemRepo.findByItemCode(itemCode)
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemCode));

        // update item code only if provided
        if (itemDTO.getItemCode() != null && !itemDTO.getItemCode().isBlank()) {
            existingItem.setItemCode(itemDTO.getItemCode());
        }

        if (itemDTO.getItemName() != null) {
            existingItem.setItemName(itemDTO.getItemName());
        }

        if (itemDTO.getItemDescription() != null) {
            existingItem.setItemDescription(itemDTO.getItemDescription());
        }

        if (itemDTO.getActive() != null) {
            existingItem.setActive(itemDTO.getActive());
        }

        if (itemDTO.getMaintainReorder() != null) {
            existingItem.setMaintainReorder(itemDTO.getMaintainReorder());
        }

        if (itemDTO.getReorderQuantity() != null) {
            existingItem.setReorderQuantity(itemDTO.getReorderQuantity());
        }

        if (itemDTO.getMinimumLevel() != null) {
            existingItem.setMinimumLevel(itemDTO.getMinimumLevel());
        }

        return itemRepo.save(existingItem);
    }

    //for now used frontend filtering
    public Page<ItemResponse> getItemsByGroup(String groupCode, int page, String sortField, String sortOrder) {

        String sortProperty = "itemCode";

        if ("name".equalsIgnoreCase(sortField)) {
            sortProperty = "itemName";
        } else if ("code".equalsIgnoreCase(sortField)) {
            sortProperty = "itemCode";
        } else if ("group".equalsIgnoreCase(sortField)) {
            sortProperty = "itemGroup.name";
        }

        Sort.Direction direction =
                "DESC".equalsIgnoreCase(sortOrder)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        PageRequest pageable = PageRequest.of(
                page,
                5,
                Sort.by(direction, sortProperty)
        );

        return itemRepo.findByItemGroup_Code(groupCode, pageable)
                .map(this::mapToResponse);
    }

    //here too, for now use frontend filtering
    public Page<ItemResponse> getItemsByActiveStatus(Boolean active, int page, String sortField, String sortOrder) {

        String sortProperty = "itemCode";

        if ("name".equalsIgnoreCase(sortField)) {
            sortProperty = "itemName";
        } else if ("code".equalsIgnoreCase(sortField)) {
            sortProperty = "itemCode";
        } else if ("group".equalsIgnoreCase(sortField)) {
            sortProperty = "itemGroup.name";
        }

        Sort.Direction direction =
                "DESC".equalsIgnoreCase(sortOrder)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        PageRequest pageable = PageRequest.of(
                page,
                5,
                Sort.by(direction, sortProperty)
        );

        return itemRepo.findByActive(active, pageable)
                .map(this::mapToResponse);
    }


    // =============================================
    // UNPAGINATED ITEM SEARCH
    // =============================================
    public List<ItemResponseSearchByKeyword> searchItemsByKeyword(String keyword) {
        List<Item> items = itemRepo.findByItemNameContainingIgnoreCaseOrItemCodeContainingIgnoreCaseOrItemDescriptionContainingIgnoreCase(
                keyword, keyword, keyword
        );
        return items.stream().map(this::mapToSearchResponse).toList();
    }

    public List<ItemResponseSearchByKeyword> getAllItemsUnpaginated() {
        List<Item> items = itemRepo.findAll();
        return items.stream().map(this::mapToSearchResponse).toList();
    }

    private ItemResponseSearchByKeyword mapToSearchResponse(Item item) {
        ItemResponseSearchByKeyword dto = new ItemResponseSearchByKeyword();
        dto.setItemCode(item.getItemCode());
        dto.setItemName(item.getItemName());
        dto.setDescription(item.getItemDescription());
        return dto;
    }

}
