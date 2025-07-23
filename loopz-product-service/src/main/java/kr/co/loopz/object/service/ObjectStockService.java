package kr.co.loopz.object.service;

import kr.co.loopz.object.annotation.RedissonLock;
import kr.co.loopz.object.domain.ObjectEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ObjectStockService {

    private final ObjectDetailService objectDetailService;

    @RedissonLock(key = "#objectId")
    public void decreaseStock(String objectId, int quantity) {
        ObjectEntity object = objectDetailService.findObjectEntity(objectId);
        object.decreaseStock(quantity);
    }

}
