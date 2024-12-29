# coordi

코디 완성을 위한 API 를 제공하는 스프링부트 애플리케이션입니다.

### API 목록

| No  | API                                                           | Method | 설명                                                                         |
|-----|---------------------------------------------------------------|--------|----------------------------------------------------------------------------|
| 1   | http://localhost:8080/api/product/min-price-coordi            | GET    | 구현 1) - 카테고리 별 최저가격 브랜드와 상품 가격, 총액을 조회하는 API                               |
| 2   | http://localhost:8080/api/product/min-price-one-brand-coordi  | GET    | 구현 2) - 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을 조회하는 API |
| 3   | http://localhost:8080/api/product/min-max-price-products/TOP  | GET    | 구현 3) - 카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회하는 API                           |
| 4-0 | http://localhost:8080/api/product/search?id=&brand=&category= | GET    | 구현 4-0) 상품 검색                                                              |
| 4-1 | http://localhost:8080/api/product/add                         | POST   | 구현 4-1) 상품 추가                                                              |
| 4-2 | http://localhost:8080/api/product/update                      | POST   | 구현 4-2) 상품 업데이트                                                            |
| 4-3 | http://localhost:8080/api/product/{id}                        | DELETE | 구현 4-3) 상품 삭제                                                              |


### 기술 스택
- Spring Boot 
- Kotlin 
- H2 DB
- JPA, JdbcTemplate
- Memory Cache 
- JUnit5
- Mokito
- MockMvc
- Springdoc-Openapi


### DB 모델링
- 테이블 설계
  - DB 테이블은 상품 데이터를 저장하기 위한 product 단일 테이블로만 구성했습니다.
  - 브랜드, 카테고리 테이블을 별도로 구성해 더 유연하게 만들 수도 있지만 현재 요구사항을 위해선 필요하지 않다고 판단했습니다. 

- 초기화 파일
  - DB 는 애플리케이션 기동시 항상 초기화 되도록 설정되어 있습니다.
  - schema.sql : product 테이블 삭제 후 생성
  - data.sql : 초기 데이터 인서트
  
- JPA, Criteria, jdbcTemplate
  - Jpa : 간단한 쿼리는 메소드기반 쿼리와, JPQL 을 사용해 처리했습니다.
  - Criteria : 상품 검색 구현을 위해 사용했습니다. (id, brand, category 조건 검색)
  - JdbcTemplate : Jpa 를 사용해 단일쿼리로 구현하기 어려운 경우 사용했습니다. (read only)

- DB 데이터 확인
  - H2 웹 console 을 통해 DB를 확인할 수 있습니다.
  - http://localhost:8080/h2-console

### Swagger 
- 아래의 Swagger 페이지를 통해 API 를 확인하고 테스트해 볼 수 있습니다. 
- url : http://localhost:8080/swagger-ui/index.html


### 실행방법
```bash
# 빌드 
./gradlew clean build

# 실행 
./gradlew bootRun

# 테스트 실행 
./gradlew clean test
```

### 코드 구현 참고 사항 
- ProductService.kt
  - 서비스 메소드에 @Cacheable 로 결과를 캐싱하고 데이터 변경 메소드 호출시 @CacheEvict 로 캐싱 데이터가 제거 되도록 처리했습니다.
  - DB 데이터를 변경하는 메소드의 경우 @Transactional 로 트랜잭션 처리 하였으며 jpa flush 를 직접 호출하여 jdbcTemplate 을 통한 쿼리 시에도 즉시 반영되도록 처리 했습니다. 
- ControllerExceptionHandler.kt 
  - API 요청 처리중 예외 발생시 어떤 오류가 발생했는지 쉽게 알수 있도록 응답 처리했습니다.
  - API 요청 오류 errorCode : 400
  - 시스템 내부 오류 errorCode : 500
- 서버에서 실행되는 쿼리와 바인딩 파라미터 값을 로그로 볼수 있도록 설정했습니다.
- 테스트코드
  - ProductControllerTest : MockMvc 를 통해 API 호출 후 결과 검증 
  - ProductCustomRepositoryTest : DB 쿼리 실행을 통한 결과 검증 
  - ProductRepositoryTest  : DB 쿼리 실행을 통한 결과 검증
  - ProductServiceTest : Mocking 을 통해 service 단 로직만 검증

 