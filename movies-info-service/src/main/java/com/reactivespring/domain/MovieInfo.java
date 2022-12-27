package com.reactivespring.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

import java.time.LocalDate;

@Data // lombok
@NoArgsConstructor // lombok
@AllArgsConstructor // lombok
@Document // indicates MongoDB entry
public class MovieInfo {

    @Id // initiates movieInfoId as primary key
    private String movieInfoId;
    private String name;
    private Integer year;
    private List<String> cast;
    private LocalDate releaseDate;
}
