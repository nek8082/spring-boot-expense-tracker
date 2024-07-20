package com.nek.mysaasapp.rest.binding;

import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ExportControllerBinding {

    public static final String EXPORT_URL = "/export";
    public static final String DATEV_EXPORT_URL = EXPORT_URL + "/datev-csv";
    public static final String EXCEL_EXPORT_URL = EXPORT_URL + "/excel";
}
