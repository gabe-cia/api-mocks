package com.mock.apimocks.repository;

import com.mock.apimocks.models.vo.MockOperation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockOperationRepository extends CrudRepository<MockOperation, String> {
    @Override
    List<MockOperation> findAll();
}
