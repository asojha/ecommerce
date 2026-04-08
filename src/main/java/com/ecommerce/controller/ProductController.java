package com.ecommerce.controller;

import com.ecommerce.model.Product;
import com.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Create, read, update, and soft-delete products in the catalogue")
public class ProductController {

    private final ProductService productService;

    @Operation(
        summary = "List all products",
        description = "Returns every product in the catalogue. By default only active products are included."
    )
    @ApiResponse(responseCode = "200", description = "Product list returned",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = Product.class))))
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(
            @Parameter(description = "When true, inactive (soft-deleted) products are included in the response")
            @RequestParam(defaultValue = "false") boolean includeInactive) {
        return ResponseEntity.ok(productService.getAllProducts(includeInactive));
    }

    @Operation(summary = "Get a product by SKU")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product found",
            content = @Content(schema = @Schema(implementation = Product.class))),
        @ApiResponse(responseCode = "404", description = "No product with the given SKU", content = @Content)
    })
    @GetMapping("/{sku}")
    public ResponseEntity<Product> getProduct(
            @Parameter(description = "Alphanumeric SKU, e.g. ELEC-IP15P", example = "ELEC-IP15P")
            @PathVariable String sku) {
        return ResponseEntity.ok(productService.getProductBySku(sku));
    }

    @Operation(
        summary = "Create a product",
        description = """
            Creates a new product. If `sku` is omitted or blank the server auto-generates one from
            the category prefix and a 6-character random suffix (e.g. `ELEC-A3F7B2`).

            Set `productType` to `SUBSCRIPTION` and include `billingCycle` (and optionally `trialDays`)
            to create a subscription product.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product created",
            content = @Content(schema = @Schema(implementation = Product.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productService.saveProduct(product));
    }

    @Operation(
        summary = "Update a product",
        description = "Replaces all mutable fields on the product identified by SKU. The SKU itself is immutable."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product updated",
            content = @Content(schema = @Schema(implementation = Product.class))),
        @ApiResponse(responseCode = "404", description = "No product with the given SKU", content = @Content)
    })
    @PutMapping("/{sku}")
    public ResponseEntity<Product> updateProduct(
            @Parameter(description = "SKU of the product to update", example = "ELEC-IP15P")
            @PathVariable String sku,
            @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(sku, product));
    }

    @Operation(
        summary = "Soft-delete a product",
        description = "Sets `active = false` on the product. The row is retained in the database and the product " +
                      "stops appearing in recommendations and the default product list."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Product deactivated"),
        @ApiResponse(responseCode = "404", description = "No product with the given SKU", content = @Content)
    })
    @DeleteMapping("/{sku}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "SKU of the product to deactivate", example = "ELEC-IP15P")
            @PathVariable String sku) {
        productService.deleteProduct(sku);
        return ResponseEntity.noContent().build();
    }
}
