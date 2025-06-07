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

    // objectId 여러개 중 존재하는 ObjectId 반환
    @PostMapping("/exists")
    public ResponseEntity<List<String>> checkExistingObjectIds(@RequestBody List<String> objectIds) {
        List<String> existing = objectRepository.findExistingObjectIds(objectIds);
        return ResponseEntity.ok(existing);
    }

}
