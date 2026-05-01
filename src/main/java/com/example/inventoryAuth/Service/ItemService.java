package com.example.inventoryAuth.Service;

import com.example.inventoryAuth.DTO.ItemDTO;
import com.example.inventoryAuth.DTO.ItemResponse;
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
        Long groupId = item.getItemGroup().getId();

        ItemGroup group = groupRepo.findById(groupId)
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
        if(sortBy.equals("ASC")){
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
        if(sortBy.equals("ASC")){
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
        if (sortBy.equals("ASC")) {
            if (page != null) {
                PageRequest pageable = PageRequest.of(
                        page,
                        5,
                        Sort.by("itemGroup").ascending()
                );
                return itemRepo.findAll(pageable).map(this::mapToResponse);

            }
            List<ItemResponse> list = itemRepo.findAll(Sort.by("itemGroup").ascending())
                    .stream()
                    .map(this::mapToResponse)
                    .toList();

            return list;

        }else{
            if (page != null) {
                PageRequest pageable = PageRequest.of(
                        page,
                        5,
                        Sort.by("itemGroup").descending()
                );
                return itemRepo.findAll(pageable).map(this::mapToResponse);

            }
            List<ItemResponse> list = itemRepo.findAll(Sort.by("itemGroup").descending())
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

    public Item updateItem(String itemCode, ItemDTO itemDTO) {
        Item existingItem = itemRepo.findByItemCode(itemCode)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        // 2. update allowed fields only
        existingItem.setItemCode(itemDTO.getItemCode());
        existingItem.setItemName(itemDTO.getItemName());
        existingItem.setItemDescription(itemDTO.getItemDescription());
        existingItem.setActive(itemDTO.getActive());
        existingItem.setMaintainReorder(itemDTO.getMaintainReorder());
        existingItem.setReorderQuantity(itemDTO.getReorderQuantity());
        existingItem.setMinimumLevel(itemDTO.getMinimumLevel());

        // 3. save
        return itemRepo.save(existingItem);
    }

    public void deleteItem(String itemCode) {
        Item item = itemRepo.findByItemCode(itemCode)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        itemRepo.delete(item);
    }


}
<<<<<<< HEAD

=======
>>>>>>> aae77b0cf1d385ca1513e1d4cf8901adc6e1ea1b
