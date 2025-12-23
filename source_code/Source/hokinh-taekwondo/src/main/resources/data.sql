INSERT INTO facilities (name, address, phone_number, description, maps_link, image, is_active)
VALUES
    ('Trung tâm cung ứng dịch vụ Văn Hóa - Thể Thao Phường Xuân Hoà', 'Số 185 Cách Mạng Tháng Tám, Quận 3', '0909123456', NULL, '', '/branch2.webp', true),
    ('Nhà văn hóa Phường 15, Quận 10', 'Số 123 Đường ABC, Quận 10', '0909876543', NULL, '', '/branch.webp', true),
    ('Trung tâm thể thao Phường Tân Định, Quận 1', 'Số 456 Lê Thánh Tôn, Quận 1', '0912345678', NULL, '', '/branch2.webp', true),
    ('Nhà văn hóa Phường 12, Quận Tân Bình', 'Số 789 Trường Chinh, Quận Tân Bình', '0933445566', NULL, '', '/branch.webp', true),
    ('Trung tâm TDTT Phường 7, Quận 5', 'Số 321 Nguyễn Trãi, Quận 5', '0988999000', NULL, '', '/branch2.webp', true);

INSERT INTO facility_classes (facility_id, name, days_of_week, start_hour, end_hour, is_active, description)
VALUES
    (1, 'Lớp 1', '2-4-6', '17:15', '18:45', true, ''),
    (1, 'Lớp 2', '2-4-6', '19:00', '20:30', true, ''),
    (1, 'Lớp 3', '3-5-7', '17:15', '18:45', true, ''),
    (1, 'Lớp 4', '7-8', '17:15', '18:45', true, ''),
    (1, 'Lớp 5', '7-8', '19:00', '20:30', false, ''),
    (2, 'Lớp 1', '2-4-6', '18:00', '19:30', true, ''),
    (3, 'Lớp 1', '3-5-7', '17:00', '18:30', true, ''),
    (3, 'Lớp 2', '3-5-7', '19:00', '20:30', true, ''),
    (4, 'Lớp 1', '2-4-6', '18:30', '20:00', true, ''),
    (4, 'Lớp 2', '8', '08:00', '09:30', true, ''),
    (4, 'Lớp 3', '8', '09:45', '11:15', true, ''),
    (5, 'Lớp 1', '3-5', '17:30', '19:00', true, ''),
    (5, 'Lớp 2', '7', '17:30', '19:00', true, ''),
    (5, 'Lớp 3', '7', '19:15', '20:45', true, '');
INSERT INTO award (name, award_rank, description, image, year, is_deleted, deleted_at)
VALUES
    ('Giải Taekwondo Thành phố Hồ Chí Minh', 'Giải Nhất', 'Thành tích xuất sắc của học viên tại giải đấu cấp thành phố', '/award.webp', '2024', true, '2023-05-20T08:30:00'),
    ('Giải Taekwondo Thành phố Hồ Chí Minh', 'Giải Nhì', 'Học viên xuất sắc đạt giải nhì giải đấu thành phố', '/award1.jpg', '2024', false, null),
    ('Giải Taekwondo Thành phố Hồ Chí Minh', 'Giải Ba', 'Thành tích đáng tự hào của học viên CLB', '/award.webp', '2024', false, null),
    ('Giải Taekwondo Cấp Quốc Gia', 'Giải Nhất', 'Vinh dự giải nhất cấp quốc gia', '/award1.jpg', '2023', true, '2023-05-20T08:30:00');

INSERT INTO article_category (category_name)
VALUES
    ('Sự kiện'),
    ('Giải Quốc Gia'),
    ('Giải Quốc Tế'),
    ('Giao Hữu Quốc Tế'),
    ('Giải Thành Phố');

INSERT INTO articles (title, content, cover_image, date, category_id, author, gallery, type, is_deleted) VALUES
                                                                                                             (
                                                                                                                 'Giải vô địch Taekwondo Đông Nam Á 2025',
                                                                                                                 '{
                                                                                                                   "type": "doc",
                                                                                                                   "content": [
                                                                                                                     { "type": "paragraph", "content": [{ "type": "text", "text": "Là một trong 26 đại diện tham gia giải vô địch Taekwondo Đông Nam Á năm 2025, Câu Lạc Bộ Taekwondo Hổ Kình vinh hạnh cử đi 3 đại diện xuất sắc nhất của câu lạc bộ." }] },
                                                                                                                     { "type": "heading", "attrs": { "level": 2 }, "content": [{ "type": "text", "text": "Đội Tuyển Hổ Kình" }] },
                                                                                                                     { "type": "paragraph", "content": [{ "type": "text", "text": "Ba vận động viên được lựa chọn đại diện cho Hổ Kình tại giải đấu này bao gồm:" }] },
                                                                                                                     {
                                                                                                                       "type": "bulletList",
                                                                                                                       "content": [
                                                                                                                         { "type": "listItem", "content": [{ "type": "paragraph", "content": [{ "type": "text", "text": "Bùi Hữu Trường - Hạng cân 58kg Nam" }] }] },
                                                                                                                         { "type": "listItem", "content": [{ "type": "paragraph", "content": [{ "type": "text", "text": "Đăng Thường Quân - Hạng cân 68kg Nam" }] }] },
                                                                                                                         { "type": "listItem", "content": [{ "type": "paragraph", "content": [{ "type": "text", "text": "Cao Thị Tuyết Nhung - Hạng cân 49kg Nữ" }] }] }
                                                                                                                       ]
                                                                                                                     },
                                                                                                                     { "type": "heading", "attrs": { "level": 2 }, "content": [{ "type": "text", "text": "Quá Trình Chuẩn Bị" }] },
                                                                                                                     { "type": "paragraph", "content": [{ "type": "text", "text": "Đội tuyển đã trải qua 6 tháng tập luyện cường độ cao với sự hướng dẫn trực tiếp từ các huấn luyện viên quốc gia." }] }
                                                                                                                   ]
                                                                                                                 }',
                                                                                                                 'https://placehold.co/1200x600/fef2f2/dc2626?text=SEA+Championship+2025',
                                                                                                                 '2025-09-30T04:30:00',
                                                                                                                 2,
                                                                                                                 'Ban Quản Lý CLB',
                                                                                                                 '["https://placehold.co/400x300/fef2f2/dc2626?text=Training+1","https://placehold.co/400x300/eff6ff/2563eb?text=Training+2","https://placehold.co/400x300/fef2f2/dc2626?text=Team+Photo"]',
                                                                                                                 'event', false
                                                                                                             ),

                                                                                                             (
                                                                                                                 'Giải Taekwondo Quốc Gia 2024',
                                                                                                                 '{
                                                                                                                   "type": "doc",
                                                                                                                   "content": [
                                                                                                                     { "type": "paragraph", "content": [{ "type": "text", "text": "Câu Lạc Bộ Taekwondo Hổ Kình đã có màn thể hiện ấn tượng tại giải Quốc Gia 2024, giành tổng cộng 5 huy chương vàng." }] }
                                                                                                                   ]
                                                                                                                 }',
                                                                                                                 'https://placehold.co/1200x600/eff6ff/2563eb?text=National+Championship+2024',
                                                                                                                 '2024-07-15T09:00:00',
                                                                                                                 1,
                                                                                                                 'Ban Tổ Chức',
                                                                                                                 '["https://placehold.co/400x300/fef2f2/dc2626?text=Award+Ceremony","https://placehold.co/400x300/eff6ff/2563eb?text=Fighting+Scene"]',
                                                                                                                 'event', false
                                                                                                             ),

                                                                                                             (
                                                                                                                 'Giải Taekwondo Trẻ Toàn Thành 2023',
                                                                                                                 '{
                                                                                                                   "type": "doc",
                                                                                                                   "content": [
                                                                                                                     { "type": "paragraph", "content": [{ "type": "text", "text": "Giải đấu thường niên dành cho các vận động viên trẻ đã diễn ra thành công rực rỡ với sự tham gia của hơn 300 thí sinh." }] }
                                                                                                                   ]
                                                                                                                 }',
                                                                                                                 'https://placehold.co/1200x600/fef2f2/dc2626?text=Youth+Championship+2023',
                                                                                                                 '2023-05-20T08:30:00',
                                                                                                                 3,
                                                                                                                 'Huấn Luyện Viên Trưởng',
                                                                                                                 '["https://placehold.co/400x300/fef2f2/dc2626?text=Team+Youth","https://placehold.co/400x300/eff6ff/2563eb?text=Match+Highlights"]',
                                                                                                                 'event', false
                                                                                                             ),

                                                                                                             (
                                                                                                                 'Giải Giao Hữu Taekwondo Việt - Hàn 2022',
                                                                                                                 '{
                                                                                                                   "type": "doc",
                                                                                                                   "content": [
                                                                                                                     { "type": "paragraph", "content": [{ "type": "text", "text": "Giải giao hữu giữa đội tuyển Việt Nam và Hàn Quốc diễn ra trong không khí thân mật, học hỏi và giao lưu văn hóa." }] }
                                                                                                                   ]
                                                                                                                 }',
                                                                                                                 'https://placehold.co/1200x600/fef2f2/dc2626?text=Vietnam-Korea+Friendly+2022',
                                                                                                                 '2022-11-10T14:00:00',
                                                                                                                 4,
                                                                                                                 'Ban Tổ Chức',
                                                                                                                 '["https://placehold.co/400x300/fef2f2/dc2626?text=Opening+Ceremony","https://placehold.co/400x300/eff6ff/2563eb?text=Team+Exchange"]',
                                                                                                                 'article', false
                                                                                                             ),

                                                                                                             (
                                                                                                                 'Lễ Kỷ Niệm Thành Lập Câu Lạc Bộ Hổ Kình',
                                                                                                                 '{
                                                                                                                   "type": "doc",
                                                                                                                   "content": [
                                                                                                                     { "type": "paragraph", "content": [{ "type": "text", "text": "Câu Lạc Bộ Taekwondo Hổ Kình kỷ niệm 10 năm thành lập, đánh dấu hành trình phát triển và cống hiến cho phong trào võ thuật Việt Nam." }] }
                                                                                                                   ]
                                                                                                                 }',
                                                                                                                 'https://placehold.co/1200x600/fef2f2/dc2626?text=Anniversary+2025',
                                                                                                                 '2025-02-10T10:00:00',
                                                                                                                 5,
                                                                                                                 'Ban Lãnh Đạo CLB',
                                                                                                                 '["https://placehold.co/400x300/fef2f2/dc2626?text=Anniversary+Stage","https://placehold.co/400x300/eff6ff/2563eb?text=Members+Together"]',
                                                                                                                 'article', false
                                                                                                             );

# INSERT INTO users (
#     id,
#     name,
#     phone_number,
#     date_of_birth,
#     email,
#     password,
#     avatar,
#     role,
#     belt_level,
#     is_active,
#     facility_id
# ) VALUES (
#              'U001',                                -- id (custom ID)
#              'Thoi Tuan',                        -- name
#              '0912345678',                          -- phone number
#              '1990-05-15',                          -- date of birth
#              'manager@example.com',                 -- email
#              '$2a$10$abc123xyz456hashedPassword',   -- password (use BCrypt hash in real app)
#              'https://placehold.co/100x100',        -- avatar
#              'manager',                             -- role
#              'KHONG',                               -- belt level (default)
#              true,                                  -- is active
#              NULL                                   -- facility_id (optional, set if belongs to one)
#          );

-- ==========================
-- SAMPLE DATA FOR USERS
-- ==========================

INSERT INTO users (id, name, phone_number, date_of_birth, email, password, avatar, role, belt_level, is_active, facility_id)
VALUES
    ('u001', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA1@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('u010', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA2@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('u011', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA3@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('u012', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA4@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('u013', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA5@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('u014', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA6@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('u015', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA7@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('u016', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA8@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('a001', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA1@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('a010', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA2@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('a011', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA3@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('a012', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA4@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('a013', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA5@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('a014', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA6@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('a015', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA7@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('a016', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA8@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('b001', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA1@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('b010', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA2@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('b011', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA3@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('b012', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA4@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('b013', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA5@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('b014', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA6@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('b015', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA7@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('b016', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA8@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('c001', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA1@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('c010', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA2@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('c011', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA3@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('c012', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA4@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('c013', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA5@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('c014', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA6@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('c015', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA7@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('c016', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA8@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('d001', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA1@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('d010', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA2@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('d011', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA3@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('d012', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA4@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('d013', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA5@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('d014', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA6@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('d015', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA7@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', FALSE, 1),
    ('d016', 'Nguyen Van A', '0912345678', '1990-03-15', 'coachA8@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 2, 'white', TRUE, 1),
    ('u002', 'Tran Thi B', '0987654321', '1995-07-20', 'instructorB@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 3, 'black/1', TRUE, 1),
    ('u003', 'Le Van C', '0978123456', '2000-01-05', 'studentC@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 4, 'red', TRUE, 1),
    ('u004', 'Pham Thi D', '0909090909', '2003-11-10', 'studentD@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 1, 'blue', TRUE, 2),
    ('u005', 'Do Van E', '0923456789', '1998-09-25', 'managerE@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 0, '', TRUE, 3),
    ('u006', 'Hoang Van F', '0934567890', '2005-06-18', 'studentF@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 4, 'yellow', TRUE, 1),
    ('u007', 'Hoang Van FA', '0934567890', '2005-06-18', 'studentFA@example.com', '$2a$10$xEfm2O2psFz9B1U0tMnKEuQIz4WxSaqEOi5Hp2iF0FjMadA3wC/R2', NULL, 4, 'yellow', TRUE, 1);

-- ==========================
-- SAMPLE DATA FOR FACILITY_CLASS_USERS
-- classId = 1
-- ==========================

INSERT INTO facility_class_users (facility_class_id, user_id, role_in_facility_class, is_active, created_at)
VALUES
    (1, 'u001', 'coach', TRUE, NOW()),
    (1, 'u002', 'instructor', TRUE, NOW()),
    (1, 'u003', 'student', false, NOW()),
    (1, 'u006', 'student', TRUE, NOW());

INSERT INTO equipments (
    facility_id, name, unit, damaged_quantity, good_quantity, fixable_quantity,
    damaged_description, good_description, fixable_description,
    created_at, updated_at
) VALUES
-- 1
(1, 'Thảm Taekwondo', 'tấm', 2, 18, 3,
 'Một số thảm bị rách do sử dụng lâu ngày.',
 'Phần lớn thảm vẫn còn tốt và sạch sẽ.',
 'Một vài tấm có thể khâu lại để sử dụng tiếp.',
 '2025-12-02 10:00:00', '2025-12-02 10:00:00'),

-- 2
(2, 'Đệm đá (Kick Pad)', 'bộ', 1, 25, 2,
 'Một đệm bị rách ở mép.',
 'Hầu hết đệm vẫn còn chắc chắn và dùng tốt.',
 'Một số đệm có thể khâu lại để tiết kiệm chi phí.',
 '2025-12-02 10:05:00', '2025-12-02 10:05:00'),

-- 3
(3, 'Giáp ngực', 'cái', 0, 30, 1,
 NULL,
 'Toàn bộ giáp đang trong tình trạng tốt.',
 'Có một giáp bị trầy nhẹ nhưng có thể sửa lại.',
 '2025-12-02 10:10:00', '2025-12-02 10:10:00'),

-- 4
(1, 'Mũ bảo hộ', 'cái', 3, 15, 2,
 'Một số mũ có lớp đệm bị mòn.',
 'Hầu hết mũ bảo hộ vẫn an toàn khi sử dụng.',
 'Hai cái có thể thay lớp đệm bên trong.',
 '2025-12-02 10:15:00', '2025-12-02 10:15:00'),

-- 5
(2, 'Miếng tập đá (Target)', 'cái', 4, 12, 3,
 'Một số miếng bị nứt phần mặt ngoài.',
 'Các miếng còn lại vẫn dùng rất tốt.',
 'Một vài miếng có thể dán lại để sử dụng tiếp.',
 '2025-12-02 10:20:00', '2025-12-02 10:20:00');
