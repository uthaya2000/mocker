package com.us.mocker.repo;

import com.us.mocker.model.Apis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiRepo extends JpaRepository<Apis, String> {
    @Query(value = "SELECT * FROM apis WHERE collection_id=?1 AND end_point=?2 AND method=?3", nativeQuery = true)
    Apis getApi(String id, String endPoint, String method);
}
