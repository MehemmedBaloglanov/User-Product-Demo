package az.codebridge.task.service.impl;

import az.codebridge.task.dto.ProductRequestDto;
import az.codebridge.task.dto.ProductResponseDto;
import az.codebridge.task.entity.ProductEntity;
import az.codebridge.task.entity.UserEntity;
import az.codebridge.task.exception.BalanceException;
import az.codebridge.task.exception.ProductNotFoundException;
import az.codebridge.task.exception.StockException;
import az.codebridge.task.exception.UserNotFoundException;
import az.codebridge.task.repository.ProductRepository;
import az.codebridge.task.repository.UserRepository;
import az.codebridge.task.service.ProductService;
import az.codebridge.task.status.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ProductResponseDto createProduct(ProductRequestDto productRequestDto) {
        ProductEntity productEntity = modelMapper.map(productRequestDto, ProductEntity.class);
        ProductEntity savedProductEntity = productRepository.save(productEntity);
        return modelMapper.map(savedProductEntity, ProductResponseDto.class);
    }

   /* @Override
    public ProductResponseDto createProduct(ProductRequestDto productRequestDto) {
        ProductEntity productEntity = ProductEntity.builder()
                .name(productRequestDto.getName())
                .price(productRequestDto.getPrice())
                .description(productRequestDto.getDescription())
                .stockQuantity(productRequestDto.getStockQuantity())
                .status(productRequestDto.getStatus())
                .createdAt(productRequestDto.getCreatedAt())
                .build();
        ProductEntity savedProductEntity = productRepository.save(productEntity);
        return ProductResponseDto.builder()
                .name(savedProductEntity.getName())
                .price(savedProductEntity.getPrice())
                .description(savedProductEntity.getDescription())
                .stockQuantity(savedProductEntity.getStockQuantity())
                .status(savedProductEntity.getStatus())
                .build();


    }*/


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ProductResponseDto updateProduct(Long id, ProductRequestDto productRequestDto) {

        ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("BU MEHSUL MOVCUD DEYIL !!!"));

        productEntity.setName(productRequestDto.getName());
        productEntity.setPrice(productRequestDto.getPrice());
        productEntity.setDescription(productRequestDto.getDescription());
        productEntity.setStockQuantity(productRequestDto.getStockQuantity());
        ProductEntity save = productRepository.save(productEntity);

        return ProductResponseDto.builder()
                .name(save.getName())
                .price(save.getPrice())
                .description(save.getDescription())
                .stockQuantity(save.getStockQuantity())
                .status(save.getStatus())
                .build();

    }

    @Override
    public void softDeleteProduct(Long id) {
        ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("BU MEHSUL MOVCUD DEYIL !!!"));

        productEntity.setStatus(ProductStatus.DELETED);
        productRepository.save(productEntity);

    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto findById(Long id) {

        ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("MEHSUL MOVCUD DEYIL"));


        if (productEntity.getStatus().equals(ProductStatus.DELETED)) {
            System.out.println("MEHSUL MOVCUD DEYIL !!!");
            return null;

        } else {
            return ProductResponseDto.builder()
                    .name(productEntity.getName())
                    .price(productEntity.getPrice())
                    .description(productEntity.getDescription())
                    .stockQuantity(productEntity.getStockQuantity())
                    .status(productEntity.getStatus())
                    .build();
        }


    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAllProducts() {

        return productRepository.findAll().stream()
                .map(productEntity -> ProductResponseDto.builder()
                        .name(productEntity.getName())
                        .price(productEntity.getPrice())
                        .description(productEntity.getDescription())
                        .stockQuantity(productEntity.getStockQuantity())
                        .status(productEntity.getStatus())
                        .build()).toList();

    }

    //burda duzelis elemisem
    @Override
    @Transactional(rollbackFor = {UserNotFoundException.class, ProductNotFoundException.class, StockException.class, BalanceException.class})
    public ProductResponseDto buyProduct(Long productId, Long userId, int count) {
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Məhsul mövcud deyil!"));
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("İstifadəçi tapılmadı"));

        if (productEntity.getStatus().equals(ProductStatus.DELETED)) {
            throw new RuntimeException("Bu məhsul silinib");
        }

        if (productEntity.getStockQuantity() < count) {
            throw new StockException("Bazada kifayət qədər məhsul yoxdur");
        }

        if (userEntity.getBalance() < productEntity.getPrice() * count) {
            throw new BalanceException("Kifayət qədər balans yoxdur!");
        }

        productEntity.setStockQuantity(productEntity.getStockQuantity() - count);
        userEntity.setBalance(userEntity.getBalance() - productEntity.getPrice() * count);

        productRepository.save(productEntity);
        userRepository.save(userEntity);

        return ProductResponseDto.builder()
                .name(productEntity.getName())
                .price(productEntity.getPrice())
                .description(productEntity.getDescription())
                .stockQuantity(productEntity.getStockQuantity())
                .status(productEntity.getStatus())
                .build();
    }


    @Override
    public ProductResponseDto reverseProduct(Long productId, Long userId) {
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(" bele mehsul tapilmadi !!"));
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User tapilmadi "));
        productEntity.setStockQuantity(productEntity.getStockQuantity() + 1);
        userEntity.setBalance(userEntity.getBalance() + productEntity.getPrice());
        ProductEntity save = productRepository.save(productEntity);
        userRepository.save(userEntity);
        return ProductResponseDto.builder()
                .name(save.getName())
                .price(save.getPrice())
                .description(save.getDescription())
                .stockQuantity(save.getStockQuantity())
                .status(save.getStatus())
                .build();
    }

    @Override
    public ProductResponseDto findByProductName(String productName) {
        ProductEntity entity = productRepository.findByName(productName)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        return modelMapper.map(entity, ProductResponseDto.class);
    }


}



