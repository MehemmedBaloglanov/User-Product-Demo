package az.codebridge.task.controller;

import az.codebridge.task.dto.ProductRequestDto;
import az.codebridge.task.dto.ProductResponseDto;
import az.codebridge.task.exception.StockException;
import az.codebridge.task.service.ProductService;
import az.codebridge.task.status.ProductStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    /**
     * 1)Create product(ProductStatus ACTIVE) --> Yeni gelen mehsullarin statusu buna kececek
     * 2)Update product(Statusdan basqa butun fieldlari update elemek ucun istifade edirik)
     * 3)Delete product(@PutMapping isledirik, Id sine gore productu getirib statusunu DELETED cevir)
     * Statusu DELETED olanlar uje istifade edilmir.
     * 4)Get product
     * 5)Get all product(List)
     * 6)BuyProduct(ACTIVE statusunda olan productlari getirib yoxlama aparib sonra almaq islemini tamamlamalisan.
     * Eger ki almaq istediyi miqdar stocda olandan coxdursa o zaman exception atsin. Eger almaq istese o zaman alsin ve
     * Status OUT_OF_STOCK a kecsin; Bazadan miqdar azalsin;
     * 7)ReturnProduct(Aldigi mehsul geri qayitsin ve balance artsin)
     **/

    @PostMapping("/create-product")
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto productRequestDto) {
        ProductResponseDto product = productService.createProduct(productRequestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(product);
    }

    @PutMapping("/update-product/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequestDto productRequestDto) {
        ProductResponseDto productResponseDto = productService.updateProduct(id, productRequestDto);
        return ResponseEntity.
                ok(productResponseDto);
    }

    @PutMapping("/soft-delete-product/{id}")
    public void softDeleteProduct(@PathVariable Long id) {
        productService.softDeleteProduct(id);

    }

    @GetMapping("/get-product/{id}")
    public ProductResponseDto getProduct(@PathVariable Long id) {
        return productService.findById(id);
    }

    @GetMapping("/get-name/{productName}")
    public ProductResponseDto getProductByName(@PathVariable String productName) {
        return productService.findByProductName(productName);
    }


    @GetMapping("/get-all-products")
    public List<ProductResponseDto> getAllProducts() {
        return productService.getAllProducts();
    }


    //BU METODDA DUZELIS ELEMISHEM
    @GetMapping("/buy/{product_id}/{user_id}")
    public ProductResponseDto buyProduct(@PathVariable Long product_id, @PathVariable Long user_id, @RequestParam int count) {
        return productService.buyProduct(product_id, user_id, count);
    }

    @GetMapping("/reverse-product/{product_id}/{user_id}")
    public ProductResponseDto reverseProduct(@PathVariable Long product_id, @PathVariable Long user_id) {
        return productService.reverseProduct(product_id, user_id);
    }


}
