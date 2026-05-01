package com.example.inventoryAuth.Service;
import com.example.inventoryAuth.DTO.ItemGroupDTO;
import com.example.inventoryAuth.DTO.ItemGroupResponse;
import com.example.inventoryAuth.Entity.ItemGroup;
import com.example.inventoryAuth.Repository.ItemGroupRepository;
import lombok.Getter;
import lombok.Setter;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
@Getter
@Setter
@Service
public class ItemGroupService {
    @Autowired
    ItemGroupRepository itemGroupRepo;

    public ItemGroup create(ItemGroup itemGroup) {

        return itemGroupRepo.save(itemGroup);
    }


    public Page<ItemGroupResponse> getAllGroup(int page) {
        return itemGroupRepo.findAll(PageRequest.of(page,5)).map(this::mapToItemGroupResponse);
    }

    public ItemGroupResponse getByItemGroupCode(String code) {
        ItemGroup itemGroup = itemGroupRepo.findByCode(code).orElseThrow(()->new RuntimeException("Item Group is not found"));

        return mapToItemGroupResponse(itemGroup);
        //return itemGroupRepo.findByCode(code);
    }

    public Object getByItemGroupName(String name, Integer page) {
        if(page != null ){
            Page<ItemGroup> itemGroup = itemGroupRepo.findByNameContainingIgnoreCase(name , PageRequest.of(page, 5));
            return itemGroup.map(this::mapToItemGroupResponse);
        }
        List<ItemGroup> list = itemGroupRepo.findByNameContainingIgnoreCase(name);
        return list.stream().map(this::mapToItemGroupResponse).toList();

    }

    public Object getGroupSortByGroupName(Integer page,String sortBy) {
        if(sortBy.equals("Asc"))
        {   if(page!=null ){
            PageRequest pageable = PageRequest.of(
                    page,
                    5,
                    Sort.by("name").ascending()
            );
            return itemGroupRepo.findAll(pageable).map(this::mapToItemGroupResponse);
        }
            List<ItemGroupResponse> list = itemGroupRepo.findAll(Sort.by("name").ascending())
                    .stream()
                    .map(this::mapToItemGroupResponse)
                    .toList();

            return list;
        }
        else{
            if(page!=null ){
                PageRequest pageable = PageRequest.of(
                        page,
                        5,
                        Sort.by("name").descending()
                );
                return itemGroupRepo.findAll(pageable).map(this::mapToItemGroupResponse);
            }
            List<ItemGroupResponse> list = itemGroupRepo.findAll(Sort.by("name").descending())
                    .stream()
                    .map(this::mapToItemGroupResponse)
                    .toList();

            return list;
        }

    }

    public Object getGroupSortByGroupCode(Integer page,String sortBy) {
        if(sortBy.equals("Asc"))
        {if(page!=null){
            PageRequest pageable = PageRequest.of(
                    page,
                    5,
                    Sort.by("code").ascending()
            );
            return itemGroupRepo.findAll(pageable).map(this::mapToItemGroupResponse);
        }
            List<ItemGroupResponse> list = itemGroupRepo.findAll(Sort.by("code").ascending())
                    .stream()
                    .map(this::mapToItemGroupResponse)
                    .toList();

            return list;
        }
        else{
            if(page!=null){
                PageRequest pageable = PageRequest.of(
                        page,
                        5,
                        Sort.by("code").descending()
                );
                return itemGroupRepo.findAll(pageable).map(this::mapToItemGroupResponse);
            }
            List<ItemGroupResponse> list = itemGroupRepo.findAll(Sort.by("code").descending())
                    .stream()
                    .map(this::mapToItemGroupResponse)
                    .toList();

            return list;
        }

    }


    public ItemGroup updateItemGroup(String code, ItemGroupDTO groupDTO) {
        ItemGroup existingGroup = itemGroupRepo.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Item Group not found"));

        // 2. update allowed fields only
        existingGroup.setName(groupDTO.getName());
        existingGroup.setDescription(groupDTO.getDescription());
        existingGroup.setMaintainReorder(groupDTO.getMaintainReorder());
        existingGroup.setGlAccount(groupDTO.getGlAccount());

        // 3. save
        return itemGroupRepo.save(existingGroup);
    }

    private ItemGroupResponse mapToItemGroupResponse(ItemGroup itemGroup) {

        ItemGroupResponse itemGroupResponse = new ItemGroupResponse();

        itemGroupResponse.setCode(itemGroup.getCode());
        itemGroupResponse.setName(itemGroup.getName());
        if(itemGroup.getDescription() != null){
            itemGroupResponse.setDescription(itemGroup.getDescription());
        }
        itemGroupResponse.setMaintainReorder(itemGroup.getMaintainReorder());
        itemGroupResponse.setGlAccount(itemGroup.getGlAccount());

        return itemGroupResponse;
    }


}
