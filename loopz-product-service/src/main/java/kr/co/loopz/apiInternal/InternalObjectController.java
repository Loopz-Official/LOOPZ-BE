package kr.co.loopz.apiInternal;

import kr.co.loopz.repository.ObjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/objects")
public class InternalObjectController {
    private final ObjectRepository objectRepository;

    // objectId 존재 여부 확인 API
    @GetMapping("/{objectId}/exists")
    public ResponseEntity<Boolean> checkObjectIdExists(@PathVariable String objectId) {
        boolean exists = objectRepository.existsByObjectId(objectId);
        return ResponseEntity.ok(exists);
    }


}
