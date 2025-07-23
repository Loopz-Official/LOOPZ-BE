package kr.co.loopz.object.service;

import kr.co.loopz.object.dto.response.PurchasedObjectResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ObjectService {

    private final ObjectStockService objectStockService;
    private final CartService cartService;

    @Transactional
    public void decreaseStockAndUpdateCart(String userId, List<PurchasedObjectResponse> purchasedObjects) {
        for (PurchasedObjectResponse purchasedObject : purchasedObjects) {
            objectStockService.decreaseStock(purchasedObject.objectId(), purchasedObject.quantity());
            cartService.deleteCartItemAllowNull(userId, purchasedObject.objectId());
        }
    }

}
