package com.us.mocker.repo;

import com.us.mocker.model.Collections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionRepo extends JpaRepository<Collections, String> {
    @Query(value = "SELECT * from collections c WHERE c.user_email = ?1", nativeQuery = true)
    List<Collections> getCollectionsByEmail(String email);

    @Query(value = "SELECT * from collections c WHERE c.user_email = ?2 AND LOWER(c.collection_name) = ?1", nativeQuery = true)
    Collections getCollectionByNameAndEmail(String name, String email);
    @Query(value = "SELECT * from collections c WHERE c.user_email = ?2 AND LOWER(c.collection_name) LIKE %?1%", nativeQuery = true)
    List<Collections> searchCollectionByName(String name, String email);

}
