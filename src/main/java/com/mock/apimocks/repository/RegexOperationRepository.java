package com.mock.apimocks.repository;

import com.mock.apimocks.models.vo.RegexOperation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegexOperationRepository extends CrudRepository<RegexOperation, String> {
    @Override
    List<RegexOperation> findAll();
}
