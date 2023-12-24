import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.service.checkout.PaymentsApi;
import com.adyen.model.checkout.*;
import com.adyen.model.checkout.Amount;
import com.adyen.model.checkout.CardDetails;
import com.adyen.model.checkout.CheckoutCard;
import com.adyen.model.checkout.CheckoutPaymentsRequest;
import com.adyen.model.checkout.CheckoutPaymentsResponse;
import com.adyen.model.checkout.RequestOptions;

public class AdyenPaymentExample {
    // Setup the client and service.
    Client client = new Client("YOUR_API_KEY", Environment.TEST);
    PaymentsApi paymentsApi = new PaymentsApi(client);

    // Create a payment request.
    CheckoutPaymentsRequest paymentRequest = new CheckoutPaymentsRequest();
    paymentRequest.setMerchantAccount("YOUR_MERCHANT_ACCOUNT");
    CardDetails cardDetails = new CardDetails();
    cardDetails.encryptedCardNumber("test_4111111111111111")
            .encryptedSecurityCode("test_737")
            .encryptedExpiryMonth("test_03")
            .encryptedExpiryYear("test_2030");
    paymentRequest.setPaymentMethod(new CheckoutCard(cardDetails));
    Amount amount = new Amount().currency("EUR").value(1000L);
    paymentRequest.setAmount(amount);
    paymentRequest.setReference("My first Adyen test payment with an API library/SDK");
    paymentRequest.setReturnUrl("https://your-company.com/checkout?shopperOrder=12xy..");

    // Add your idempotency key.
    RequestOptions requestOptions = new RequestOptions();
    requestOptions.setIdempotencyKey("YOUR_IDEMPOTENCY_KEY");

    // Make a request to the /payments endpoint.
    CheckoutPaymentsResponse checkoutPaymentsResponse = paymentsApi.payments(paymentRequest, requestOptions);

    // Additional payment-related methods (Add more as needed)
    // For example:
    // 1. Capture a payment
    CaptureRequest captureRequest = new CaptureRequest();
    captureRequest.setAmount(amount);
    CaptureResponse captureResponse = paymentsApi.capture(checkoutPaymentsResponse.getPspReference(), captureRequest, requestOptions);

    // 2. Refund a payment
    RefundRequest refundRequest = new RefundRequest();
    refundRequest.setAmount(amount);
    RefundResponse refundResponse = paymentsApi.refund(checkoutPaymentsResponse.getPspReference(), refundRequest, requestOptions);

    // 3. Cancel a payment
    CancelOrRefundResponse cancelResponse = paymentsApi.cancelOrRefund(checkoutPaymentsResponse.getPspReference(), requestOptions);
}
