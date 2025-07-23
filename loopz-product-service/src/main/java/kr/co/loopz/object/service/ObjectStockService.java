package kr.co.loopz.object.service;

import kr.co.loopz.object.annotation.RedissonLock;
import kr.co.loopz.object.domain.ObjectEntity;
import kr.co.loopz.object.dto.response.PurchasedObjectResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ObjectStockService {

    private final ObjectDetailService objectDetailService;
    private final CartService cartService;

    @Transactional
    public void decreaseStockAndUpdateCart(String userId, List<PurchasedObjectResponse> purchasedObjects) {
        for (PurchasedObjectResponse purchasedObject : purchasedObjects) {
            decreaseStock(purchasedObject.objectId(), purchasedObject.quantity());
            cartService.deleteCartItemAllowNull(userId, purchasedObject.objectId());
        }
    }

    @RedissonLock(key = "#objectId")
    public void decreaseStock(String objectId, int quantity) {
        ObjectEntity object = objectDetailService.findObjectEntity(objectId);
        object.decreaseStock(quantity);
    }

}
