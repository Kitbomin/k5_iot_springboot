
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

SELECT * FROM users;

#0827(G_User_role)
-- 사용자 권한 테이블  
create table if not exists user_roles (
	user_id bigint not null,
    role varchar(30) not null,
    
    constraint fk_user_roles_user
		foreign key (user_id) references users(id) on delete cascade,
	constraint uk_user_roles unique (user_id, role),
    
    constraint chk_user_roles_role check(role IN('USER', 'MANAGER', 'ADMIN'))
    
) engine=InnoDB
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci
  comment = '사용자 권한';
  
  select * from user_roles;
  
  # 샘플 데이터 #
  insert into user_roles(user_id, role) values (1, "ADMIN");
  insert into user_roles(user_id, role) values (2, "USER");






