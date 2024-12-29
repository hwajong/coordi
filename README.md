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


### 소스 패키지 구조

### 기술 스택 


### 스웨거 


### 코드빌드

### 테스트

### 실행방법


### 기타 추가 정보 