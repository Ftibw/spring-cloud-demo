package com.ftibw.demo.utils;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author : Ftibw
 * @date : 2019/7/6 20:56
 */
public class RestUtils {

    public static <T> T getFormUrlEncoded(
            String url,
            Map<String, String> formData,
            Consumer<HttpHeaders> headerSetter,
            ParameterizedTypeReference<T> retTypeRef,
            RestTemplate restTemplate) {
        return requestFormUrlEncoded(url, formData, HttpMethod.GET, headerSetter, retTypeRef, restTemplate);
    }

    public static <T> T postFormUrlEncoded(
            String url,
            Map<String, String> formData,
            Consumer<HttpHeaders> headerSetter,
            ParameterizedTypeReference<T> retTypeRef,
            RestTemplate restTemplate) {
        return requestFormUrlEncoded(url, formData, HttpMethod.POST, headerSetter, retTypeRef, restTemplate);
    }

    public static <T> T requestFormUrlEncoded(
            String url,
            Map<String, String> formData,
            HttpMethod method,
            Consumer<HttpHeaders> headerSetter,
            ParameterizedTypeReference<T> retTypeRef,
            RestTemplate restTemplate
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        if (null != headerSetter) {
            headerSetter.accept(headers);
        }

        MultiValueMap<String, String> params = null;
        if (null != formData && !formData.isEmpty()) {
            params = new LinkedMultiValueMap<>(formData.size());
            params.setAll(formData);
        }

        return restTemplate
                .exchange(url, method, new HttpEntity<>(params, headers), retTypeRef)
                .getBody();
    }
}
