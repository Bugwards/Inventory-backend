package com.example.inventoryAuth.Controller;


import com.example.inventoryAuth.DTO.ItemDTO;
import com.example.inventoryAuth.DTO.ItemResponse;
import com.example.inventoryAuth.Entity.Item;
import com.example.inventoryAuth.Service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/items")
public class ItemController {
    @Autowired
    ItemService itemService;

    @PreAuthorize("hasRole('STORE_STAFF', 'SYSTEM_ADMIN')")
    @PostMapping("createItem")
    public Item create(@RequestBody Item item) {
        return itemService.create(item);
    }

    @GetMapping("all")
    public Page<ItemResponse> getAllItem(
            @RequestParam(defaultValue = "0") int page
    ){
        return itemService.getAllItem(page);
    }

    @GetMapping("code/{itemCode}")
    public ItemResponse getItemByCode(@PathVariable String itemCode){
        return itemService.getItemByCode(itemCode);
    }

    @GetMapping("name/{itemName}")
    public Object getItemByName(
            @PathVariable String itemName ,
            @RequestParam (defaultValue = "0") int page
    ){
        return itemService.getItemByName(itemName, page);
    }

    @GetMapping("sortByItemCode")
    public Object getItemSortByItemCode(
            @RequestParam (defaultValue = "0") int page,
            @RequestParam (defaultValue = "ASC") String sortBy
    ){
        return itemService.getItemSortByItemCode(page,sortBy);
    }
    @GetMapping("sortByItemName")
    public Object getItemSortByItemName(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam (defaultValue = "ASC") String sortBy
    ){
        return itemService.getItemSortByItemName(page, sortBy);
    }
    @GetMapping("sortByItemGroup")
    public Object getItemSortByItemGroup(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam (defaultValue = "ASC") String sortBy
    ){
        return itemService.getItemSortByItemGroup(page,sortBy);
    }

    @PreAuthorize("hasAnyRole('STORE_STAFF', 'SYSTEM_ADMIN')")
    @PutMapping("{itemCode}")
    public ResponseEntity<Item> updateItem(@PathVariable String itemCode, @RequestBody ItemDTO itemDTO){
        Item updated = itemService.updateItem(itemCode , itemDTO);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasAnyRole('STORE_STAFF', 'SYSTEM_ADMIN')")
    @DeleteMapping("delete/{itemCode}")
    public String deleteItem(@PathVariable String itemCode){
        itemService.deleteItem(itemCode);
        return "Item Deleted successfully";
    }

}
