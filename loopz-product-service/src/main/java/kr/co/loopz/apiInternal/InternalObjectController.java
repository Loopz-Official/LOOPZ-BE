package kr.co.loopz.apiInternal;

import kr.co.loopz.repository.ObjectRepository;
import kr.co.loopz.service.ObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
public class InternalObjectController {
    private final ObjectRepository objectRepository;
    private final ObjectService objectService;

    // objectId 존재 여부 확인 API
    @GetMapping("objects/{objectId}/exists")
    public ResponseEntity<Boolean> checkObjectIdExists(@PathVariable String objectId) {
        boolean exists = objectRepository.existsByObjectId(objectId);
        return ResponseEntity.ok(exists);
    }


}
