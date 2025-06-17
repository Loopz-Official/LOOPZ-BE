package kr.co.loopz.object.service;

import kr.co.loopz.object.Exception.ObjectException;
import kr.co.loopz.object.domain.Cart;
import kr.co.loopz.object.domain.CartItem;
import kr.co.loopz.object.domain.ObjectEntity;
import kr.co.loopz.object.repository.CartItemRepository;
import kr.co.loopz.object.repository.CartRepository;
import kr.co.loopz.object.repository.ObjectRepository;
import kr.co.loopz.object.dto.request.CartUpdateRequest;
import kr.co.loopz.object.dto.response.CartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static kr.co.loopz.object.Exception.ObjectErrorCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ObjectRepository objectRepository;
    private final CartItemRepository cartItemRepository;

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
            // 요청 수량 = 0 -> cart에서 삭제
            if (cartItem != null) {
                cartItemRepository.delete(cartItem);
            }
        } else {
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

        return new CartResponse(total);
    }
}
