package kr.co.loopz.user.domain;

import jakarta.persistence.Column;
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
    private boolean agreedServiceTerms;
    // 개인정보 마케팅 동의
    private boolean agreedMarketing;
    // SMS 마케팅 동의
    @Column(name = "agreed_event_sms")
    private boolean agreedEventSMS;

    private LocalDateTime agreedServiceTermsAt;
    private LocalDateTime agreedMarketingAt;
    private LocalDateTime agreedEventSMSAt;

    public void updateTerms(boolean over14, boolean agreedServiceTerms, boolean agreedMarketing, boolean agreedEventSMS) {

        LocalDateTime now = LocalDateTime.now();

        this.over14 = over14;

        if (this.agreedServiceTerms != agreedServiceTerms) {
            this.agreedServiceTerms = agreedServiceTerms;
            this.agreedServiceTermsAt = agreedServiceTerms ? now : null;
        }

        if (this.agreedMarketing != agreedMarketing) {
            this.agreedMarketing = agreedMarketing;
            this.agreedMarketingAt = agreedMarketing ? now : null;
        }

        if (this.agreedEventSMS != agreedEventSMS) {
            this.agreedEventSMS = agreedEventSMS;
            this.agreedEventSMSAt = agreedEventSMS ? now : null;
        }

    }

}
