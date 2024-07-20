package com.nek.mysaasapp.rest.binding;

import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TransactionControllerBinding {

    public static final String TRANSACTION_URL = "/transaction";
    public static final String TRANSACTION_SAVE_URL = TRANSACTION_URL + "/save";
    public static final String TRANSACTION_EDIT_URL = TRANSACTION_URL + "/edit";
    public static final String TRANSACTION_EDIT_GET_URL = TRANSACTION_EDIT_URL + "/{transactionId}";
    public static final String TRANSACTION_DELETE_URL = TRANSACTION_URL + "/delete";
}
