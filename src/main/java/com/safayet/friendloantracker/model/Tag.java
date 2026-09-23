package com.safayet.friendloantracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "tags")
@NoArgsConstructor
@AllArgsConstructor
public class Tag {

    @Id
    private String id;

    @Indexed
    private String name;

    private String description;
}
