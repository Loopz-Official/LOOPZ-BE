package kr.co.loopz.apiInternal;

import kr.co.loopz.repository.ObjectRepository;
import kr.co.loopz.service.ObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/objects")
public class InternalObjectController {
    private final ObjectRepository objectRepository;
    private final ObjectService objectService;

    // objectId 존재 여부 확인 API
    @GetMapping("/{objectId}/exists")
    public ResponseEntity<Boolean> checkObjectIdExists(@PathVariable String objectId) {
        boolean exists = objectRepository.existsByObjectId(objectId);
        return ResponseEntity.ok(exists);
    }

    // like 추가 시 likeCount+1
    @PostMapping("/{objectId}/like/increase")
    public ResponseEntity<Void> increaseLikeCount(@PathVariable String objectId) {
        objectService.updateLikeCount(objectId,true);
        return ResponseEntity.ok().build();
    }

    //like 삭제 시 likeCount -1
    @PostMapping("/{objectId}/like/decrease")
    public ResponseEntity<Void> decreaseLikeCount(@PathVariable String objectId) {
        objectService.updateLikeCount(objectId,false);
        return ResponseEntity.ok().build();
    }


}
