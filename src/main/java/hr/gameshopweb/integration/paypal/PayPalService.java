package hr.gameshopweb.integration.paypal;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayPalService {

    private final APIContext apiContext;

    public Payment createPayment(BigDecimal total, String currency,
                                 String description, String cancelUrl,
                                 String successUrl) throws PayPalRESTException {
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(total.setScale(2, RoundingMode.HALF_UP).toString());

        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(List.of(transaction));
        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }

    public Payment executePayment(String paymentId, String payerId)
            throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution execution = new PaymentExecution();
        execution.setPayerId(payerId);
        return payment.execute(apiContext, execution);
    }

    public String getApprovalUrl(Payment payment) {
        return payment.getLinks().stream()
                .filter(l -> "approval_url".equals(l.getRel()))
                .map(Links::getHref)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No PayPal approval URL"));
    }
}