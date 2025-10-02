DROP DATABASE IF EXISTS hokinh_taekwondo;

CREATE DATABASE hokinh_taekwondo;
USE hokinh_taekwondo;

-- FACILITIES TABLE --
CREATE TABLE facilities (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,          -- tên cơ sở
    address VARCHAR(400) DEFAULT NULL,
    phone CHAR(10),
    note VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_facility (name)
);

-- USERS TABLE --
CREATE TABLE users (
    id VARCHAR(100) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone_number CHAR(10) NOT NULL,
    date_of_birth DATE,
    email VARCHAR(100),
    password VARCHAR(100) NOT NULL CHECK (LENGTH(password) > 5),
    avatar VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    role ENUM('club_head','manager','coach','student') NOT NULL,
	belt_level ENUM(
	    'Không',
	    'Cấp 8 - Trắng',
	    'Cấp 7 - Vàng',
	    'Cấp 6 - Xanh lá cây',
	    'Cấp 5 - Xanh dương',
	    'Cấp 4 - Đỏ',
	    'Cấp 3 - Đỏ',
	    'Cấp 2 - Đỏ',
	    'Cấp 1 - Đỏ',
	    'Một đẳng - Đen 1 vạch vàng',
	    'Hai đẳng - Đen 2 vạch vàng',
	    'Ba đẳng - Đen 3 vạch vàng',
	    'Bốn đẳng - Đen 4 vạch vàng',
	    'Năm đẳng - Đen 5 vạch vàng',
	    'Sáu đẳng - Đen 6 vạch đỏ',
	    'Bảy đẳng - Đen 7 vạch đỏ',
	    'Tám đẳng - Đen 8 vạch trắng',
	    'Chín đẳng - Đen 9 vạch trắng',
	    'Mười đẳng - Đen 10 vạch trắng'
	) DEFAULT 'Không',
    is_active BIT DEFAULT 1,
    facility_id INT DEFAULT NULL, -- học viên/nhân sự gắn cơ sở
    CONSTRAINT fk_user_facility FOREIGN KEY (facility_id) REFERENCES facilities(id) ON DELETE SET NULL
);

-- Thêm cột quản lý cơ sở (manager_user_id) tham chiếu user
ALTER TABLE facilities
    ADD manager_user_id VARCHAR(100) DEFAULT NULL,
    ADD CONSTRAINT fk_facility_manager FOREIGN KEY (manager_user_id) REFERENCES users(id) ON DELETE SET NULL;

-- Test select
SELECT * FROM users;
