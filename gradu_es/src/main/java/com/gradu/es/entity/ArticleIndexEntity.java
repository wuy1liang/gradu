package com.gradu.es.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.io.Serializable;
import java.util.Date;

@Data
@Document(indexName = "gradu_es",type = "article")
public class ArticleIndexEntity implements Serializable {

    @Id
    private String id;

    @Field(index = true,analyzer = "ik_max_work",searchAnalyzer = "ik_max_word")
    private String title;

    @Field(index = true,analyzer = "ik_max_work",searchAnalyzer = "ik_max_word")
    private String content;

    private String state;

    private Date updatetime;


}
