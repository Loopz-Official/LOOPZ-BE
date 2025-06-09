package kr.co.loopz.user.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserTerms {

    // 나이
    private boolean over14;
    // 룹즈 이용 약관
    private boolean agreedTerms;
    // 개인정보 마케팅 동의
    private boolean agreedPrivacyPolicy;
    // SMS 마케팅 동의
    private boolean agreedSMSMarketing;

    private LocalDateTime agreedTermsAt;
    private LocalDateTime agreedPrivacyPolicyAt;
    private LocalDateTime agreedSMSMarketingAt;

    public void updateTerms(boolean over14, boolean agreedTerms, boolean agreedPrivacyPolicy, boolean agreedSMSMarketing) {

        LocalDateTime now = LocalDateTime.now();

        this.over14 = over14;

        if (this.agreedTerms != agreedTerms) {
            this.agreedTerms = agreedTerms;
            this.agreedTermsAt = agreedTerms ? now : null;
        }

        if (this.agreedPrivacyPolicy != agreedPrivacyPolicy) {
            this.agreedPrivacyPolicy = agreedPrivacyPolicy;
            this.agreedPrivacyPolicyAt = agreedPrivacyPolicy ? now : null;
        }

        if (this.agreedSMSMarketing != agreedSMSMarketing) {
            this.agreedSMSMarketing = agreedSMSMarketing;
            this.agreedSMSMarketingAt = agreedSMSMarketing ? now : null;
        }

    }

}
