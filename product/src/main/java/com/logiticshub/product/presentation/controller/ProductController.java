package com.logiticshub.product.presentation.controller;

import com.logiticshub.product.application.dto.ProductResponse;
import com.logiticshub.product.application.dto.ProductResponseDto;
import com.logiticshub.product.domain.model.Product;
import com.logiticshub.product.domain.service.ProductService;
import com.logiticshub.product.presentation.request.ProductRequestDto;
import com.logiticshub.product.presentation.response.ApiResponse;
import com.logiticshub.product.presentation.response.MessageType;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
//    상품 생성
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDto>> createProduct(@RequestBody ProductRequestDto productRequestDto,
                                                                         @RequestHeader(value = "X-USER-ID") Long userId,
                                                                         @RequestHeader(value = "X-USER-ROLE") String role) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(MessageType.CREATE, productService.createProduct(userId, role, productRequestDto)));

    }

//  상품 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> updateProduct(@RequestBody ProductRequestDto productRequestDto,
                                                                         @PathVariable UUID id,
                                                                         @RequestHeader(value = "X-USER-ID") Long userId,
                                                                         @RequestHeader(value = "X-USER-ROLE") String role) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(MessageType.UPDATE, productService.updateProduct(id, userId, role, productRequestDto)));
    }
//    상품 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> deleteProduct(@PathVariable UUID id,
                                                                         @RequestHeader(value = "X-USER-ID") Long userId,
                                                                         @RequestHeader(value = "X-USER-ROLE") String role) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(MessageType.DELETE, productService.deleteProduct(id, role, userId)));
    }

//    상품 전체 조회
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getProducts(
            @QuerydslPredicate(root = Product.class) Predicate predicate,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable){

        PagedModel<ProductResponseDto> products = productService.getProducts(predicate, pageable);
        if (products.getContent().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(MessageType.NOT_FOUND, "검색 조건을 변경하세요."));
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(MessageType.RETRIEVE, products));

    }

    //    상품 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> findProductById(@PathVariable(value = "id") UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(MessageType.RETRIEVE,productService.getProduct(id)));
    }


    @PostMapping("/batch")
    List<ProductResponse> findProductsByIds(@RequestBody List<UUID> ids) {
        return productService.findProductsByIds(ids);
    }
}
