package com.munevver.inventory_service.inventory.service;

import com.munevver.inventory_service.inventory.entity.Inventory;
import com.munevver.inventory_service.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryDataSeeder implements CommandLineRunner {

    private final InventoryRepository inventoryRepository;

    @Override
    public void run(String... args) throws Exception {
        if (inventoryRepository.count() == 0) {
            log.info("Inventory table is empty. Seeding initial stock data...");
            
            // Product 101: Developer Laptop (Pro) - Stock: 10
            inventoryRepository.save(Inventory.builder()
                    .productId(101L)
                    .stockQuantity(10)
                    .build());

            // Product 102: Mechanical Keyboard (RGB) - Stock: 5
            inventoryRepository.save(Inventory.builder()
                    .productId(102L)
                    .stockQuantity(5)
                    .build());

            // Product 103: Studio Headset (Hi-Fi) - Stock: 2 (Critical stock level)
            inventoryRepository.save(Inventory.builder()
                    .productId(103L)
                    .stockQuantity(2)
                    .build());

            log.info("Stock seeding completed successfully.");
        } else {
            log.info("Inventory table already has data. Skipping stock seeding.");
        }
    }
}
