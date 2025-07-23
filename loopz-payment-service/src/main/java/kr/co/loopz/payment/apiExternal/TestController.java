package kr.co.loopz.payment.apiExternal;

import io.portone.sdk.server.common.Currency;
import kr.co.loopz.payment.dto.response.PurchasedItem;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/test")
    public PurchasedItem test(){
        return new PurchasedItem("eii-wkjn", "shoes", 2, 1000, Currency.Krw.INSTANCE.getValue());
    }

}
