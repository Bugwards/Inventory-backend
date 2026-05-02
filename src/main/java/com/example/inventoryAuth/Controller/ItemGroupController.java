package com.example.inventoryAuth.Controller;

import com.example.inventoryAuth.DTO.ItemGroupDTO;
import com.example.inventoryAuth.DTO.ItemGroupResponse;
import com.example.inventoryAuth.Entity.ItemGroup;
import com.example.inventoryAuth.Service.ItemGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/item-groups")
public class ItemGroupController {
    @Autowired
    ItemGroupService itemGroupService;

    @PreAuthorize("hasAnyRole('STORE_STAFF', 'SYSTEM_ADMIN')")
    @PostMapping("createGroup")
    public ItemGroup create(@RequestBody ItemGroup itemGroup) {
        return itemGroupService.create(itemGroup);
    }

    @GetMapping("allGroups")
    public Page<ItemGroupResponse> getAllGroup(
            @RequestParam(defaultValue = "0") int page
    ){
        return itemGroupService.getAllGroup(page);
    }

    @GetMapping("code/{code}")
    public ItemGroupResponse getByItemGroupCode(@PathVariable String code){
        return itemGroupService.getByItemGroupCode(code);
    }

    @GetMapping("name/{name}")
    public Object getByItemGroupName(
            @PathVariable String name,
            @RequestParam(defaultValue = "0") int page
    ){
        return itemGroupService.getByItemGroupName(name,page);
    }

    @GetMapping("sortByGroupName")
    public Object getGroupSortByGroupName(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "Asc") String sortBy
    ){
        return itemGroupService.getGroupSortByGroupName(page,sortBy);
    }

    @GetMapping("sortByGroupCode")
    public Object getGroupSortByGroupCode (
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "Asc") String sortBy
    ){
        return itemGroupService.getGroupSortByGroupCode(page,sortBy);
    }

    @PreAuthorize("hasAnyRole('STORE_STAFF', 'SYSTEM_ADMIN')")
    @PutMapping("{code}")
    public ResponseEntity<ItemGroup> updateItemGroup(@PathVariable String code, @RequestBody ItemGroupDTO groupDTO){
        ItemGroup updated = itemGroupService.updateItemGroup(code, groupDTO);
        return ResponseEntity.ok(updated);
    }

}

