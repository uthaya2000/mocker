package com.us.mocker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "collections")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Collections {
    @Id
    private String id;

    @Column(name = "collection_name")
    private String collectionName;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "apis")
    @OneToMany(mappedBy = "collection")
    private List<Apis> apis;

}
