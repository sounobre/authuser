package com.ead.authuser.clients;

import com.ead.authuser.dtos.CourseRecordDto;

import com.ead.authuser.dtos.ResponsePageDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Log4j2
@Component
public class CourseClient {

    @Value("${ead.api.url.course}")
    String baseUrlCourse;

    final RestClient restClient;

    public CourseClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public Page<CourseRecordDto> getAllCoursesByUser(UUID userId, Pageable pageable) {
        String url = baseUrlCourse + "/courses?userId=" + userId + "&page=" +
                pageable.getPageNumber() + "&size=" + pageable.getPageSize() +
                "&sort=" + pageable.getSort().toString().replaceAll(":", ",").replaceAll(" ", "");

        log.info("URL enviada {}",url);

        try {

            return restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ResponsePageDto<CourseRecordDto>>() {});

        } catch (RestClientException e) {
            log.error("Erro Request Restclient with cause: {}", e.getMessage());
            throw new RestClientException("Erro Request Restclient with cause: " + e);
        }
    }

    public void deleteUserCourseInCourse(UUID userId){
        String url =   baseUrlCourse + "/courses/users/" + userId;
        log.debug("Request URL: {} ", url);

        try{
            restClient.delete()
                    .uri(url)
                    .retrieve()
                    .toBodilessEntity();
        }catch (RestClientException e){
            log.error("Error Request DELETE RestClient with cause: {} ", e.getMessage());
            throw new RuntimeException("Error Request DELETE RestClient", e);
        }
    }
}
