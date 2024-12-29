# coordi

이 프로젝트는 코디 완성을 위한 API 를 제공하는 스프링부트 애플리케이션입니다.

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
- JUnit5
- Mokito
- MockMvc
- Springdoc-Openapi


### DB 모델링
- 테이블 설계
  - DB 테이블은 상품 데이터를 저장하기 위한 product 단일 테이블로만 구성했습니다.
  - 브랜드, 카테고리 테이블을 별도로 구성해 더 유연하게 만들 수도 있지만 현재 요구사항을 위해선 필요하지 않다고 판단했습니다. 

- 초기화 파일
  - schema.sql : product 테이블 삭제 후 생성
  - data.sql : 초기 데이터 인서트
  
- JPA, Criteria, jdbcTemplate
  - Jpa : 간단한 쿼리는 메소드기반 쿼리와, JPQL 을 사용해 처리했습니다.
  - Criteria : 상품 검색 구현을 위해 사용했습니다. (id, brand, category 조건 검색)
  - JdbcTemplate : Jpa 를 사용해 단일쿼리로 구현하기 어려운 경우 사용했습니다.  

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
