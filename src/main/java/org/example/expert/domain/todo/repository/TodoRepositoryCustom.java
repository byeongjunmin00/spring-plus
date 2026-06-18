package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;

import java.util.Optional;

public interface TodoRepositoryCustom {

    // QueryDSL로 구현할 메서드 선언
    Optional<Todo> findByIdWithUser(Long todoId);
}
