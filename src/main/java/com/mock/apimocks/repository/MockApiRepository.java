package com.mock.apimocks.repository;

import com.mock.apimocks.models.vo.MockApi;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockApiRepository extends CrudRepository<MockApi, String> {
    @Override
    List<MockApi> findAll();
}
