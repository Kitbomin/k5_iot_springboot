
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







