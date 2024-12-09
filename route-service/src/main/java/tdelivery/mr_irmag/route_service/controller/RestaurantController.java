package tdelivery.mr_irmag.route_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tdelivery.mr_irmag.route_service.domain.entity.Restaurant;
import tdelivery.mr_irmag.route_service.service.RestaurantService;

import java.util.List;
import java.util.UUID;


import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    private RestaurantService restaurantService;

    @Autowired
    public void setRestaurantService(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @Operation(
            summary = "Создать новый ресторан",
            description = "Только для пользователей с ролью 'ADMIN'",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ресторан успешно создан",
                            content = @Content(schema = @Schema(implementation = Restaurant.class)))
            }
    )
    @PostMapping
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody Restaurant restaurant) {
        Restaurant createdRestaurant = restaurantService.createRestaurant(restaurant);
        return ResponseEntity.ok(createdRestaurant);
    }

    @Operation(
            summary = "Получить все рестораны",
            description = "Доступно для пользователей с ролью 'ADMIN' и 'USER'",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список ресторанов",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Restaurant.class))))
            }
    )
    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }

    @Operation(
            summary = "Получить ресторан по ID",
            description = "Доступно для пользователей с ролью 'ADMIN' и 'USER'",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ресторан найден",
                            content = @Content(schema = @Schema(implementation = Restaurant.class))),
                    @ApiResponse(responseCode = "404", description = "Ресторан не найден")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@Parameter(description = "ID ресторана") @PathVariable UUID id) {
        return restaurantService.getRestaurantById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Обновить информацию о ресторане",
            description = "Только для пользователей с ролью 'ADMIN'",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ресторан успешно обновлен",
                            content = @Content(schema = @Schema(implementation = Restaurant.class))),
                    @ApiResponse(responseCode = "404", description = "Ресторан не найден")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<Restaurant> updateRestaurant(@Parameter(description = "ID ресторана") @PathVariable UUID id,
                                                       @RequestBody Restaurant restaurantDetails) {
        Restaurant updatedRestaurant = restaurantService.updateRestaurant(id, restaurantDetails);
        return ResponseEntity.ok(updatedRestaurant);
    }

    @Operation(
            summary = "Удалить ресторан",
            description = "Только для пользователей с ролью 'ADMIN'",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Ресторан успешно удален"),
                    @ApiResponse(responseCode = "404", description = "Ресторан не найден")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@Parameter(description = "ID ресторана") @PathVariable UUID id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }
}