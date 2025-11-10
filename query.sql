
# 1. 스키마 생성 
Drop database if exists k5_iot_springboot;

# 2. 스키마 생성 + 문자셋 / 정렬 설정
create database if not exists k5_iot_springboot
	character set utf8mb4
    collate utf8mb4_general_ci;
    
# 3. 스키마 선택
use k5_iot_springboot;


# 0811(A_Test)
Create table if not exists test(
	test_id bigint primary key auto_increment,
    name varchar(50) not null
);

select * from test;

# 0812(B_Student)
create table if not exists students(
	id bigint primary key auto_increment,
    name varchar(100) not null,
    email varchar(100) not null unique,
    unique key uq_name_email (name,email)
    -- : name+email 조합이 유일하도록 설정 
);

select * from students;


#0813(C_Book)
create table if not exists books(
	id bigint primary key auto_increment,
    writer varchar(50) not null,
    title varchar(100) not null,
    content varchar(500) not null,
    category varchar(20) not null,
    # 자바 enum 데이터 처리
    # : DB 에서는 varchar(문자열) 로 관리 + check 제약 조건으로 문자 제한 
    
    constraint chk_book_category CHECK (category IN ('NOVEL', 'ESSAY', 'POEM', 'MAGAZINE')),
    
    # 같은 저자 + 동일 제목 중복 저장 방지 
    constraint uk_book_writer_title unique (writer, title)
);
select * from books;


# 0819(D_Post, D_Comment)
create table if not exists `posts`(
	`id` 		bigint not null auto_increment,
    `title` 	varchar(200) not null comment '게시글 제목',
    `content` 	longtext not null comment '게시글 내용', -- @Lob 매핑 대응 
    `author` 	varchar(100) not null comment '작성자 표시명 또는 ID',
    
    primary key(`id`),
    key `idx_post_author` (`author`) 
) engine=InnoDB
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci
  comment = '게시글';

create table if not exists `comments`(
	`id` 		bigint not null auto_increment,
    `post_id` 	bigint not null comment 'posts.id FK',
    `content` 	varchar(1000) not null comment '댓글 내용',
    `commenter` varchar(100) not null comment '댓글 작성자 표시명 또는 ID',
    
    primary key (`id`),
    key `idx_comment_post_id` (`post_id`),
    key `idx_comment_commenter` (`commenter`),
    
    constraint `fk_comment_post`
		foreign key(`post_id`) references `posts`(`id`) 	
			on delete cascade 
			on update cascade # 무결성을 위한 친구임 
                                                        
)engine=InnoDB
 default charset = utf8mb4
 collate = utf8mb4_unicode_ci
 comment = '댓글';

select * from posts;

select * from comments;

#0821(F_Board)
-- 게시판 테이블(생성/수정 시간 포함)
create table if not exists boards (
	id bigint auto_increment,
    title varchar(150) not null,
    content longtext not null,
    created_at datetime(6) not null, -- insert 문으로 데이터 삽입 안됨
    updated_at datetime(6) not null,
    primary key(id)
) engine=InnoDB
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci
  comment = '게시글';
  
select * from boards;

use k5_iot_springboot;

#0822(G_USER)
CREATE TABLE IF NOT EXISTS `users` (
	id BIGINT NOT NULL AUTO_INCREMENT,
    login_id VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    gender VARCHAR(10),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT `uk_users_login_id` UNIQUE (login_id),
    CONSTRAINT `uk_users_email` UNIQUE (email),
    CONSTRAINT `uk_users_nickname` UNIQUE (nickname),
    CONSTRAINT `chk_users_gender` CHECK(gender IN ('MALE', 'FEMALE'))
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '사용자';

insert into users (login_id, password, email, nickname, gender, created_at, updated_at) values
('qwer123', 'qwer123', 'qwe@asdf.com' ,'qwe', 'MALE', NOW(6), NOW(6)),
('asdf123', 'qwer123', 'asd@asdf.com' ,'asd', 'FEMALE', NOW(6), NOW(6)),
('zxcv123', 'qwer123', 'zxc@asdf.com' ,'zxc', 'MALE', NOW(6), NOW(6));

SELECT * FROM users;

# 0910 (G_Role)
-- 권한 코드 테이블
drop table user_roles;
drop table roles;
CREATE TABLE IF NOT EXISTS `roles`(
   role_name   VARCHAR(30) PRIMARY KEY
)ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '권한 코드(USER, MANAGER, OWNER 등)';
  
# 0910 (G_UserRoleId)
-- 사용자-권한 매핑 (조인 엔티티)
# DROP TABLE IF EXISTS `user_roles`; (기존의 user_roles 제거)
drop table user_roles;
CREATE TABLE IF NOT EXISTS `user_roles`(
   user_id BIGINT NOT NULL,
    role_name VARCHAR(30) NOT NULL,
    PRIMARY KEY (user_id, role_name),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_name) REFERENCES roles(role_name)
)ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '사용자 권한 매핑';

## 권한 데이터 삽입 ##
INSERT INTO roles (role_name) VALUES
   ('USER'),
   ('MANAGER'),
   ('ADMIN')
    # 이미 값이 있는 경우(DUPLICATE, 중복)
    # , 에러 대신 그대로 유지할 것을 설정
    ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);
    
select * from users;
SELECT * FROM roles;
    
select * from roles;

## 사용자 권한 매핑 삽입 ##
INSERT INTO user_roles (user_id, role_name) VALUES
   (1, 'USER'),
   (2, 'USER'),
    (2, 'MANAGER')

    ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);
    
SELECT * FROM `user_roles`;

# 0827 (G_User_role)
-- 사용자 권한 테이블
## 지금와선 이거 안쓸거임...
## : 위의 사용자-권한 다대다 형식 사용 권장 
drop table if exists user_roles;
-- CREATE TABLE IF NOT EXISTS `user_roles` (
-- 	user_id BIGINT NOT NULL,
--     role VARCHAR(30) NOT NULL,

--     CONSTRAINT fk_user_roles_user
-- 		FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
-- 	CONSTRAINT uk_user_roles UNIQUE (user_id, role),
--     
--     CONSTRAINT chk_user_roles_role CHECK (role IN ('USER', 'MANAGER', 'ADMIN'))
-- ) ENGINE=InnoDB
--   DEFAULT CHARSET = utf8mb4
--   COLLATE = utf8mb4_unicode_ci
--   COMMENT = '사용자 권한';

SELECT * FROM `user_roles`;
  
  # 샘플 데이터 #
#   insert into user_roles(user_id, role) values (1, "ADMIN");
#   insert into user_roles(user_id, role) values (2, "USER");


#0827(H_Article)
-- 기사 테이블  
create table if not exists articles (
	id bigint auto_increment,
    title varchar(200) not null,
    content longtext not null,
    author_id bigint not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    
    primary key (id),
    constraint fk_articles_author
		foreign key (author_id) references users(id) on delete cascade
    
) engine=InnoDB
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci
  comment = '기사글';
  
  insert into articles (title, content, author_id, created_at, updated_at)
  values
  ('기사 1', '내ㅔ용', 1, now(6), now(6)),
  ('기사 2', '내ㅔ용', 2, now(6), now(6)),
  ('기사 3', '내ㅔ용', 3, now(6), now(6)),
  ('기사 4', '내ㅔ용', 1, now(6), now(6)),
  ('기사 5', '내ㅔ용', 2, now(6), now(6)),
  ('기사 6', '내ㅔ용', 3, now(6), now(6));
  
  select * from articles;

# 0901(주문 관리 시스템)
-- 트랜잭션, 트리거, 인덱스, 뷰 학습 
# products(상품), stocks(재고), orders(주문 정보), oder_items(주문 상세 정보)
# , order_logs(주문 기록 정보) 

-- 안전 실행: 삭제 순서 기입
# cf) foreign_key_checks: 외래키 제약조건을 활성화(1) 하거나 비활성화(0) 하는 명령어 
set foreign_key_checks = 0;
drop table if exists order_logs;
drop table if exists order_items;
drop table if exists orders;
drop table if exists stocks;
drop table if exists products;
set foreign_key_checks = 1;

--  상품 정보 테이블 
create table if not exists products (
	id 			bigint auto_increment primary key,
    name		varchar(100) 		not null,
    price		int 				not null,
    created_at	datetime(6) 		not null,
	updated_at 	datetime(6) 		not null,
    
    constraint uq_products_name unique (name),	# 제약조건 
    index idx_product_name (name)				#  제품명으로 제품 조회 시 성능향상 목표
    
)	engine=InnoDB								# mySQL 에서 테이블이 데이터를 저장하고 관리하는 방식을 지정함
												# 트랜잭션 지원(ACID 원자성, 일관성, 고립성, 지속성), 외래키 제약조건 지원(참조무결성 보장)
	default charset = utf8mb4					# DB나 테이블의 기본 문자 집합 정의 (4바이트까지 지원 - 이모지 포함 가능)
    collate = utf8mb4_unicode_ci				# 정렬 순서 지정 (대소문자 구분 없이 문자열 비교 정렬)
    comment = '상품정보' ;							# 일종의 주석


-- 재고 정보 테이블 
create table if not exists stocks(
	id 			bigint auto_increment primary key,
    product_id  bigint 				not null,
    quantity	int 				not null,
    created_at	datetime(6) 		not null,
	updated_at 	datetime(6) 		not null,
    
    constraint fk_stocks_product foreign key(product_id) 
			references products(id) on delete cascade,	# foreign key 제약 조건
    constraint chk_stocks_qty check (quantity >= 0), 	# 수량은 음수가 될 수 없다는 제약조건
    index idx_stocks_product_id (product_id)			# 인덱스 제약조건 	
    
)	engine=InnoDB
	default charset = utf8mb4
    collate = utf8mb4_unicode_ci
    comment = '상품 재고 정보' ;


-- 주문 정보 테이블 
create table if not exists orders (
	id 				bigint auto_increment primary key,
	user_id			bigint 			not null,
    order_status	varchar(50) 	not null default 'PENDING',
    created_at		datetime(6) 	not null,
	updated_at 		datetime(6) 	not null,
    
    constraint fk_orders_user foreign key(user_id)
		references users(id) on delete cascade, 	# 외래키 제약조건	
    
    constraint chk_orders_os check 					# enum 씀
    (order_status in ('PENDING', 'APPROVED', 'CANCELLED')),	
	
    index idx_orders_user 		(user_id),
    index idx_orders_status		(order_status),
    index idx_orders_created_at (created_at) -- 얘는 시작과 끝에 대한 값을 넣어야함 
    
)	engine=InnoDB
	default charset = utf8mb4
    collate = utf8mb4_unicode_ci
    comment = '주문 정보' ;


-- 주문 상세 정보 테이블 
create table if not exists order_items (
	id 			bigint auto_increment primary key,
    order_id	bigint 			not null,			# 주문 정보
    product_id	bigint			not null,			# 제품 정보
    quantity	int 			not null,			# 주문에 대한 수량 
    created_at	datetime(6) 	not null,
	updated_at 	datetime(6) 	not null,
    
    constraint fk_order_items_order foreign key (order_id) 
		references orders(id) on delete cascade,
    constraint fk_order_items_product foreign key(product_id)
		references products(id) on delete cascade,
	constraint chk_order_items_qty check (quantity > 0),
    
    index idx_order_items_order (order_id),		
    index idx_order_items_product (product_id),		
    
    unique key uq_order_product (order_id, product_id) # 한 주문 안에 동일한 제품정보가 중복되면 안됨
    
)	engine=InnoDB
	default charset = utf8mb4
    collate = utf8mb4_unicode_ci
    comment = '주문 상세 정보' ;


-- 주문 기록 정보 테이블 
create table if not exists order_logs (
	id 			bigint auto_increment primary key,
    order_id	bigint 			not null,
    message		varchar(255)	not null,
    -- 트리거가 직접 INSERT 하는 로그 테이블은 시간 누락 방지를 위해 DB 기본값 유지 시켜야함 
    
    created_at	datetime(6) 	not null default current_timestamp(6),
	updated_at 	datetime(6) 	not null default current_timestamp(6) 
				on update current_timestamp(6),
    
    constraint fk_order_logs_order foreign key(order_id)
		references orders(id) on delete cascade,
        
    index idx_order_logs_order (order_id),		
    index idx_order_logs_created_at (created_at)

)	engine=InnoDB
	default charset = utf8mb4
    collate = utf8mb4_unicode_ci
    comment = '주문 기록 정보' ;

##### 초기 데이터 설정 #####
insert into products (name, price, created_at, updated_at)
values 
	('갤럭시 Z플립 7', 50000, now(6), now(6)),
	('아이폰 16', 60000, now(6), now(6)),
	('갤럭시 S25 울트라', 55000, now(6), now(6)),
	('맥북 프로 14', 80000, now(6), now(6));

insert into stocks (product_id, quantity, created_at, updated_at)
values
	(1, 50, now(6), now(6)),
	(2, 30, now(6), now(6)),
	(3, 70, now(6), now(6)),
	(4, 20, now(6), now(6));


### 0902
-- 뷰 (행 단위)
-- : 주문 상세 화면 (API) - 한 주문의 각 상품 라인 아이템 정보를 상세하게 제공할 때
-- EX) GET  /api/v1/orders/{orderId}/items
DROP VIEW order_summary;
CREATE OR REPLACE VIEW order_summary AS 
SELECT 
	o.id 					AS order_id,
    o.user_id 				AS user_id,
    o.order_status			AS order_status,
    p.name					AS product_name,
    oi.quantity				AS quantity,
    p.price					AS price,
    CAST((oi.quantity * p.price) AS SIGNED) AS total_price,
    o.created_at			AS ordered_at
FROM
	orders o
    JOIN order_items oi on o.id = oi.order_id 
    JOIN products p on oi.product_id = p.id;

-- 뷰 (주문 합계)
Drop view order_totals;
CREATE OR REPLACE VIEW order_totals AS 
SELECT
	o.id 						AS order_id,
    o.user_id					AS user_id,
    o.order_status				AS order_status,
    CAST(SUM(oi.quantity * p.price) AS SIGNED)	AS order_total_amount,
    CAST(SUM(oi.quantity)			AS SIGNED)	AS order_total_quantity,
    MIN(o.created_at)			AS ordered_at -- 혹시 분산되면 가장 과거의 값을 들고오게 보장해줌
FROM
	orders o
    JOIN order_items oi on o.id = oi.order_id 
    JOIN products p on oi.product_id = p.id
GROUP BY
	o.id, o.user_id, o.order_status; -- 주문 별 합계: 주문(orders) 정보를 기준으로 그룹화 해줌


-- 트리거 (trigger): 주문 생성 시 로그
# 고객 문의/장애 분석 시 "언제 주문 레코드가 생겼는지" 원인 추적에 사용함 
DELIMITER  //
CREATE TRIGGER trg_after_order_insert
	AFTER INSERT ON orders
	FOR EACH ROW
    BEGIN
		INSERT INTO order_logs(order_id, message)
        VALUES (NEW.id, CONCAT('주문이 생성되었습니다. 주문 ID: ', NEW.id));
	END //
DELIMITER ;


-- 트리거 (trigger): 주문 상태 변경 시 로그
# 상태 전이 추적 시 "누가 언제 어떤 상태로 바꿨는지" 원인 추적에 사용함 
DELIMITER //
CREATE TRIGGER trg_after_order_status_update
	AFTER UPDATE ON orders
    FOR EACH ROW
    BEGIN
		IF NEW.order_status <> OLD.order_status -- <> = !=
			THEN INSERT INTO order_logs(order_id, message)
            VALUES(NEW.id, CONCAT('주문 상태가 ', OLD.order_status, 
									' -> ', NEW.order_status, '로 변경되었습니다.'));
		END IF;
	END //
DELIMITER ;


select * from products;
select * from stocks;
select * from orders;
select * from order_items;
select * from order_logs;
select * from users;

CREATE TABLE notice (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    author VARCHAR(100) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

SELECT * FROM notice;


use k5_iot_springboot;
