package kr.co.loopz.object.service;

import kr.co.loopz.object.Exception.ObjectException;
import kr.co.loopz.object.converter.ObjectConverter;
import kr.co.loopz.object.domain.Cart;
import kr.co.loopz.object.domain.CartItem;
import kr.co.loopz.object.domain.ObjectEntity;
import kr.co.loopz.object.domain.ObjectImage;
import kr.co.loopz.object.dto.request.CartSelectRequest;
import kr.co.loopz.object.dto.response.CartListResponse;
import kr.co.loopz.object.repository.CartItemRepository;
import kr.co.loopz.object.repository.CartRepository;
import kr.co.loopz.object.repository.ObjectImageRepository;
import kr.co.loopz.object.repository.ObjectRepository;
import kr.co.loopz.object.dto.request.CartUpdateRequest;
import kr.co.loopz.object.dto.response.CartResponse;
import kr.co.loopz.object.dto.response.CartItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static kr.co.loopz.object.Exception.ObjectErrorCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ObjectRepository objectRepository;
    private final CartItemRepository cartItemRepository;
    private final ObjectImageRepository objectImageRepository;
    private final ObjectConverter objectConverter;

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
        int current = cartItem != null ? cartItem.getQuantity() : 0;
        int update = current + request.quantity();

        // 요청 수량 > 입고 수량
        if (update > object.getDetail().getStock()) {
            throw new ObjectException(QUANTITY_EXCEEDS_STOCK, "입고 수량:" + object.getDetail().getStock() + "  요청 수량:" + update);
        }

        // 요청 수량 < 0
        if (update < 0){
            throw new ObjectException(INVALID_QUANTITY, "입력 수량:"+update);
        }

        if (update == 0) {
            // 요청 수량 = 0 불가 (장바구니에는 최소 1개)
            throw new ObjectException(CART_LEAST_ONE);
            }
        else {
            // CartItem 업데이트
            if (cartItem == null) {
                cartItem = CartItem.builder()
                        .cartId(cart.getCartId())
                        .objectId(request.objectId())
                        .quantity(update)
                        .build();
            } else {
                cartItem.updateQuantity(update);
            }
            cartItemRepository.save(cartItem);
        }

        // Cart 전체 수량 업데이트
        int total = cartItemRepository.countDistinctObjectByCartId(cart.getCartId());

        if (total>100) {
            throw new ObjectException(CART_LIMIT_EXCEEDS, "현재: " + total);
        }

        return new CartResponse(total);
    }


    public CartListResponse getCart(String userId) {

        Cart cart = cartRepository.findByUserId(userId).orElse(null);

        if (cart == null) {
            // 비어있는 장바구니 응답 반환
            return new CartListResponse(
                    Collections.emptyList(), // cartItems
                    0, // totalQuantity
                    0L, // totalPrice
                    0, // shippingFee
                    0L // finalPrice
            );
        }

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getCartId());

        List<CartItemResponse> responses = new ArrayList<>();
        int totalQuantity = 0;
        long totalPrice = 0;

        for (CartItem item : cartItems) {
            ObjectEntity object = objectRepository.findByObjectId(item.getObjectId())
                    .orElseThrow(() -> new ObjectException(OBJECT_ID_NOT_FOUND, "Object not found: " + item.getObjectId()));

            // 기본 이미지 사용
            List<ObjectImage> images = objectImageRepository.findByObjectId(object.getObjectId());
            String imageUrl = images.isEmpty() ? null : images.get(0).getImageUrl();

            // 상품 선택 여부
            boolean selected = item.isSelected();

            // 선택된 것만 반영
            if (selected) {
                totalQuantity += item.getQuantity();
                totalPrice += item.getQuantity() * object.getObjectPrice();
            }

            CartItemResponse response = objectConverter.toCartItemResponse(item, object, imageUrl, selected);
            responses.add(response);
        }

        int shippingFee = 3000; // 고정 배송비
        long finalPrice = totalPrice + shippingFee;

        return new CartListResponse(responses, totalQuantity, totalPrice, shippingFee,finalPrice);

    }


    // 선택 여부 변경
    @Transactional
    public void updateSelected(String userId, CartSelectRequest request) {

        Cart cart = cartRepository.findByUserId(userId).orElse(null);

        CartItem item = cartItemRepository.findByCartIdAndObjectId(cart.getCartId(), request.objectId())
                .orElseThrow(() -> new ObjectException(CART_ITEM_NOT_FOUND));

        item.updateSelected(request.selected());

        cartItemRepository.save(item);
    }
}
