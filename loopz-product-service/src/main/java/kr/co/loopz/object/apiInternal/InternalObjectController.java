package kr.co.loopz.object.apiInternal;

import kr.co.loopz.object.dto.response.ObjectResponse;
import kr.co.loopz.object.repository.ObjectRepository;
import kr.co.loopz.object.service.ObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
public class InternalObjectController {
    private final ObjectRepository objectRepository;
    private final ObjectService objectService;

    // objectId 존재 여부 확인 API
    @GetMapping("/objects/{objectId}/exists")
    public ResponseEntity<Boolean> checkObjectIdExists(@PathVariable String objectId) {
        boolean exists = objectRepository.existsByObjectId(objectId);
        return ResponseEntity.ok(exists);
    }


    @GetMapping("/object/{objectId}")
    public ResponseEntity<ObjectResponse> getObjectById(@PathVariable String objectId) {
        ObjectResponse response = objectService.getObjectById(objectId);
        return ResponseEntity.ok(response);
    }


}
