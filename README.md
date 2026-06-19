## 구현 내용

### Level 1-1. @Transactional 이해

클래스 레벨에 `@Transactional(readOnly = true)`가 걸려있어서 `saveTodo()`에서 INSERT 시 `Connection is read-only` 에러가 발생했습니다.

메서드 레벨 `@Transactional`이 클래스 레벨보다 우선순위가 높기 때문에, `saveTodo()` 메서드에 별도로 `@Transactional`을 추가해 해결했습니다.

---

### Level 1-2. JWT 이해

프론트엔드에서 JWT 토큰에서 nickname을 꺼내 사용해야 해서, User 엔티티에 nickname 필드를 추가하고 JWT Claim에 포함시켰습니다.

`JwtUtil.createToken()`에 nickname 파라미터를 추가하고, `JwtFilter`에서 토큰 파싱 시 nickname을 꺼내 `SecurityContextHolder`에 저장합니다.

---

### Level 1-3. JPA 이해

`GET /todos` API에 weather, startDate, endDate 조건 검색 기능을 추가했습니다.

JPQL에서 `:param IS NULL OR 조건` 패턴을 사용해 파라미터가 null이면 해당 조건을 무시하도록 구현했습니다.

```java
"WHERE (:weather IS NULL OR t.weather = :weather) " +
"AND (:startDate IS NULL OR t.modifiedAt >= :startDate) " +
"AND (:endDate IS NULL OR t.modifiedAt <= :endDate) "
```

---

### Level 1-4. 컨트롤러 테스트 이해

`InvalidRequestException` 발생 시 `GlobalExceptionHandler`가 `400 BAD_REQUEST`를 반환하는데, 기존 테스트가 `200 OK`를 기대하고 있어 실패했습니다.

실제 반환값인 `400 BAD_REQUEST`에 맞게 테스트 기대값을 수정했습니다.

---

### Level 1-5. AOP 이해

기존 코드가 `UserController.getUser`를 `@After`로 감시하고 있었는데, 과제 요구사항은 `UserAdminController.changeUserRole` 실행 **전**에 로그를 남기는 것이었습니다.

타겟 메서드를 `changeUserRole`로 바꾸고, `@After` → `@Before`로 변경했습니다.

---

### Level 2-6. JPA Cascade

Todo 생성 시 생성자가 자동으로 담당자로 등록되어야 했는데, managers 필드에 cascade 설정이 없어서 Manager가 DB에 저장되지 않았습니다.

`@OneToMany`에 `cascade = CascadeType.PERSIST`를 추가해 Todo 저장 시 Manager도 함께 저장되도록 했습니다.

---

### Level 2-7. N+1 문제

댓글 조회 시 각 댓글의 user를 N번 추가 조회하는 N+1 문제가 있었습니다.

`JOIN` → `JOIN FETCH`로 변경해 댓글과 user를 한 번의 쿼리로 가져오도록 했습니다.

---

### Level 2-8. QueryDSL

`findByIdWithUser`를 JPQL 문자열 대신 QueryDSL로 전환했습니다.

`TodoRepositoryCustom` 인터페이스에 메서드를 선언하고, `TodoRepositoryCustomImpl`에서 `JPAQueryFactory`로 구현했습니다. `.fetchJoin()`을 사용해 N+1도 방지했습니다.

---

### Level 2-9. Spring Security

기존 Servlet Filter + ArgumentResolver 방식을 Spring Security로 전환했습니다.

- `JwtFilter` → `OncePerRequestFilter` 상속으로 변경
- JWT 파싱 후 유저 정보를 `SecurityContextHolder`에 저장
- URL별 접근 권한을 `SecurityConfig`에서 관리
- `FilterConfig` 삭제 (Spring Security가 필터 등록 담당)
