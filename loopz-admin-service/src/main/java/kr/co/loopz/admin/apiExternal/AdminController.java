package kr.co.loopz.admin.apiExternal;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import kr.co.loopz.admin.dto.request.UploadRequest;
import kr.co.loopz.admin.dto.response.UploadResponse;
import kr.co.loopz.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@Slf4j
@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/admin/v1")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/upload")
    @Operation(summary = "상품 등록 API")
    public ResponseEntity<UploadResponse> uploadObject(
            @AuthenticationPrincipal User currentUser,
            @RequestBody @Valid UploadRequest request
    ) {

        String userId = currentUser.getUsername();

        UploadResponse response = adminService.uploadObject(userId, request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 상품 정보 수정
    @PutMapping("/modify/{objectId}")
    @Operation(summary = "상품 수정 API")
    public ResponseEntity<UploadResponse> modifyObject(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String objectId,
            @RequestBody @Valid UploadRequest request
    ) {

        String userId = currentUser.getUsername();

        UploadResponse response = adminService.modifyObject(userId,objectId, request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
