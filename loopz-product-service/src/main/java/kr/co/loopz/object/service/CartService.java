package kr.co.loopz.object.service;

import kr.co.loopz.object.converter.ObjectConverter;
import kr.co.loopz.object.domain.Cart;
import kr.co.loopz.object.domain.CartItem;
import kr.co.loopz.object.domain.ObjectEntity;
import kr.co.loopz.object.domain.ObjectImage;
import kr.co.loopz.object.dto.request.CartUpdateRequest;
import kr.co.loopz.object.dto.response.*;
import kr.co.loopz.object.exception.ObjectException;
import kr.co.loopz.object.repository.CartItemRepository;
import kr.co.loopz.object.repository.CartRepository;
import kr.co.loopz.object.repository.ObjectImageRepository;
import kr.co.loopz.object.repository.ObjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static kr.co.loopz.object.exception.ObjectErrorCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ObjectRepository objectRepository;
    private final CartItemRepository cartItemRepository;
    private final ObjectImageRepository objectImageRepository;
    private final ObjectConverter objectConverter;
    private final ObjectDetailService objectDetailService;

    @Transactional
    public CartResponse updateCart(String userId, CartUpdateRequest request) {

        // 장바구니 정보 없으면 생성
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(new Cart(userId)));

        ObjectEntity object = objectRepository.findByObjectId(request.objectId())
                .orElseThrow(() -> new ObjectException(OBJECT_ID_NOT_FOUND, "Object not found" + request.objectId()));

        CartItem cartItem = cartItemRepository.findByCartIdAndObjectId(cart.getCartId(), request.objectId())
                .orElse(null);

        // 요청 수량 반영
        int updateQuantity = request.quantity();

        // 요청 수량 > 입고 수량
        if (updateQuantity > object.getDetail().getStock()) {
            throw new ObjectException(QUANTITY_EXCEEDS_STOCK, "입고 수량:" + object.getDetail().getStock() + "  요청 수량:" + updateQuantity);
        }

        // 요청 수량 < 0
        if (updateQuantity < 0) {
            throw new ObjectException(INVALID_QUANTITY, "입력 수량:" + updateQuantity);
        }

        if (updateQuantity == 0) {
            // 요청 수량 = 0 불가 (장바구니에는 최소 1개)
            throw new ObjectException(CART_LEAST_ONE);
        } else {
            // CartItem 업데이트
            if (cartItem == null) {
                cartItem = CartItem.builder()
                        .cartId(cart.getCartId())
                        .objectId(request.objectId())
                        .quantity(updateQuantity)
                        .build();
            } else {
                cartItem.updateQuantity(updateQuantity);
            }
            cartItemRepository.save(cartItem);
        }

        // Cart 전체 수량 업데이트
        int total = cartItemRepository.countDistinctObjectByCartId(cart.getCartId());

        if (total > 100) {
            throw new ObjectException(CART_LIMIT_EXCEEDS, "현재: " + total);
        }

        return new CartResponse(total);
    }

    // 장바구니 조회
    public CartListResponse getCart(String userId) {

        Cart cart = cartRepository.findByUserId(userId).orElse(null);

        if (cart == null) {
            // 비어있는 장바구니 응답 반환
            return new CartListResponse(Collections.emptyList(),Collections.emptyList());
        }

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getCartId());

        List<String> objectIds = cartItems.stream()
                .map(CartItem::getObjectId)
                .distinct()
                .collect(Collectors.toList());

        // 상품 리스트 조회
        List<ObjectEntity> objects = objectRepository.findAllByObjectIdIn(objectIds);
        Map<String, ObjectEntity> objectMap = objects.stream()
                .collect(Collectors.toMap(ObjectEntity::getObjectId, Function.identity()));

        List<ObjectImage> images = objectImageRepository.findByObjectIdIn(objectIds);

        Map<String, String> imageMap = images.stream()
                .collect(Collectors.groupingBy(ObjectImage::getObjectId,
                        Collectors.mapping(ObjectImage::getImageUrl, Collectors.toList())))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().isEmpty() ? null : e.getValue().get(0)
                ));


        // 재고 확인: 장바구니 수량 > 재고면 예외 발생
        List<CartItemResponse> availableObject = new ArrayList<>();
        List<String> outOfStock = new ArrayList<>();

        for (CartItem item : cartItems) {
            ObjectEntity object = objectMap.get(item.getObjectId());
            if (item.getQuantity() > object.getStock()) {
                outOfStock.add(object.getObjectId());
            } else {
                // 재고 있는 상품은 응답 반환
                String imageUrl = imageMap.get(item.getObjectId());
                ObjectResponse objectResponse = objectConverter.toObjectResponse(object, imageUrl);
                availableObject.add(new CartItemResponse(objectResponse, item.getQuantity()));
            }
        }

        return new CartListResponse(availableObject, outOfStock);
    }

    // 선택 상품 삭제
    @Transactional
    public void deleteSelected(String userId,List<String> objectIds) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ObjectException(CART_NOT_FOUND, "Cart not found for user: " + userId));

        // 장바구니 상품 가져오기
        List<CartItem> cartItems = cartItemRepository.findByCartIdAndObjectIdIn(cart.getCartId(),objectIds);

        // 선택된 상품만 삭제
        cartItemRepository.deleteAll(cartItems);

    }

    // 개별 상품 삭제
    @Transactional
    public void deleteCart(String userId, String objectId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ObjectException(CART_NOT_FOUND, "Cart not found for user: " + userId));

        CartItem item = cartItemRepository.findByCartIdAndObjectId(cart.getCartId(), objectId)
                .orElseThrow(() -> new ObjectException(CART_ITEM_NOT_FOUND, "상품Id: " + objectId));

            cartItemRepository.delete(item);
        }

    public CartWithQuantityResponse getCartByUserId(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ObjectException(CART_NOT_FOUND, "Cart not found for user: " + userId));

        List<CartItemResponse> items = cartItemRepository.findByCartId(cart.getCartId())
                .stream()
                .map(item -> {
                    ObjectResponse object = objectDetailService.getObjectById(item.getObjectId());
                    return new CartItemResponse(object, item.getQuantity());
                })
                .collect(Collectors.toList());

        return new CartWithQuantityResponse(cart.getCartId(), items);
    }
}