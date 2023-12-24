/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.squareup.connectexamples.ecommerce;

import com.squareup.square.Environment;
import com.squareup.square.api.PaymentsApi;
import com.squareup.square.models.*;
import com.squareup.square.SquareClient;
import com.squareup.square.exceptions.ApiException;
import com.squareup.square.api.PaymentsApi;
import com.squareup.square.models.CreatePaymentRequest;
import com.squareup.square.models.Money;
import com.squareup.square.models.PaymentResult;
import com.squareup.square.models.RetrieveLocationResponse;
import com.squareup.square.models.TokenWrapper;


import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@SpringBootApplication
public class Main {

    @PostMapping("/process-payment")
    @ResponseBody
    PaymentResult processPayment(@RequestBody TokenWrapper tokenObject)
        throws InterruptedException, ExecutionException {

        // Get currency for location
        RetrieveLocationResponse locationResponse = getLocationInformation(squareClient).get();
        String currency = locationResponse.getLocation().getCurrency();

        Money bodyAmountMoney = new Money.Builder()
            .amount(100L)
            .currency(currency)
            .build();

        CreatePaymentRequest createPaymentRequest = new CreatePaymentRequest.Builder(
            tokenObject.getToken(),
            tokenObject.getIdempotencyKey(),
            bodyAmountMoney)
            .build();

        PaymentsApi paymentsApi = squareClient.getPaymentsApi();
        return paymentsApi.createPaymentAsync(createPaymentRequest).thenApply(result -> {
            return new PaymentResult("SUCCESS", null);
        }).exceptionally(exception -> {
            ApiException e = (ApiException) exception.getCause();
            System.out.println("Failed to make the request");
            System.out.printf("Exception: %s%n", e.getMessage());
            return new PaymentResult("FAILURE", e.getErrors());
        }).join();
    }

    // Other existing methods...

}
