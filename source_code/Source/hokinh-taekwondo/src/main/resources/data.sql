-- Insert facilities
INSERT INTO facilities (name, address, phone, note, maps_link, img)
VALUES
    ('Trung tâm cung ứng dịch vụ Văn Hóa - Thể Thao Phường Xuân Hoà', 'Số 185 Cách Mạng Tháng Tám, Quận 3', '0909123456', NULL, '', '/branch2.webp'),
    ('Nhà văn hóa Phường 15, Quận 10', 'Số 123 Đường ABC, Quận 10', '0909876543', NULL, '', '/branch.webp'),
    ('Trung tâm thể thao Phường Tân Định, Quận 1', 'Số 456 Lê Thánh Tôn, Quận 1', '0912345678', NULL, '', '/branch2.webp'),
    ( 'Nhà văn hóa Phường 12, Quận Tân Bình', 'Số 789 Trường Chinh, Quận Tân Bình', '0933445566', NULL, '', '/branch.webp'),
    ('Trung tâm TDTT Phường 7, Quận 5', 'Số 321 Nguyễn Trãi, Quận 5', '0988999000', NULL, '', '/branch2.webp');

-- Insert facility classes
INSERT INTO facility_class (facility_id, class_name, days, start_hour, end_hour)
VALUES
-- Facility 1
(1, 'Lớp 1', '2-4-6', '17:15', '18:45'),
(1, 'Lớp 2', '2-4-6', '19:00', '20:30'),
(1, 'Lớp 3', '3-5-7', '17:15', '18:45'),
(1, 'Lớp 4', '7-CN', '17:15', '18:45'),
(1, 'Lớp 5', '7-CN', '19:00', '20:30'),

-- Facility 2
(2, 'Lớp 1', '2-4-6', '18:00', '19:30'),

-- Facility 3
(3, 'Lớp 1', '3-5-7', '17:00', '18:30'),
(3, 'Lớp 2', '3-5-7', '19:00', '20:30'),

-- Facility 4
(4, 'Lớp 1', '2-4-6', '18:30', '20:00'),
(4, 'Lớp 2', '8', '08:00', '09:30'),
(4, 'Lớp 3', '8', '09:45', '11:15'),

-- Facility 5
(5, 'Lớp 1', '3-5', '17:30', '19:00'),
(5, 'Lớp 2', '7', '17:30', '19:00'),
(5, 'Lớp 3', '7', '19:15', '20:45');

INSERT INTO award (name, award_rank, description, img, year)
VALUES
('Giải Taekwondo Thành phố Hồ Chí Minh', 'Giải Nhất', 'Thành tích xuất sắc của học viên tại giải đấu cấp thành phố', '/award.webp', '2024'),
('Giải Taekwondo Thành phố Hồ Chí Minh', 'Giải Nhì', 'Học viên xuất sắc đạt giải nhì giải đấu thành phố', '/award1.jpg', '2024'),
('Giải Taekwondo Thành phố Hồ Chí Minh', 'Giải Ba', 'Thành tích đáng tự hào của học viên CLB', '/award.webp', '2024'),
('Giải Taekwondo Cấp Quốc Gia', 'Giải Nhất', 'Vinh dự giải nhất cấp quốc gia', '/award1.jpg', '2023');

INSERT INTO article (title, content, cover_image, date, category, author, gallery) VALUES
                                                                                       (
                                                                                           'Giải vô địch Taekwondo Đông Nam Á 2025',
                                                                                           '<p>Là một trong 26 đại diện tham gia giải vô địch Taekwondo Đông Nam Á năm 2025, Câu Lạc Bộ Taekwondo Hổ Kình vinh hạnh cử đi 3 đại diện xuất sắc nhất của câu lạc bộ.</p>
                                                                                           <h2>Đội Tuyển Hổ Kình</h2>
                                                                                           <p>Ba vận động viên được lựa chọn đại diện cho Hổ Kình tại giải đấu này bao gồm:</p>
                                                                                           <ul><li><strong>Bùi Hữu Trường</strong> - Hạng cân 58kg Nam</li><li><strong>Đăng Thường Quân</strong> - Hạng cân 68kg Nam</li><li><strong>Cao Thị Tuyết Nhung</strong> - Hạng cân 49kg Nữ</li></ul>
                                                                                           <h2>Quá Trình Chuẩn Bị</h2>
                                                                                           <p>Đội tuyển đã trải qua 6 tháng tập luyện cường độ cao với sự hướng dẫn trực tiếp từ các huấn luyện viên quốc gia.</p>
                                                                                           <img src="/branch2.webp" alt="Team photo"/>',
                                                                                           'https://placehold.co/1200x600/fef2f2/dc2626?text=SEA+Championship+2025',
                                                                                           '2025-09-30T04:30:00',
                                                                                           'Giải Quốc Tế',
                                                                                           'Ban Quản Lý CLB',
                                                                                           '["https://placehold.co/400x300/fef2f2/dc2626?text=Training+1","https://placehold.co/400x300/eff6ff/2563eb?text=Training+2","https://placehold.co/400x300/fef2f2/dc2626?text=Team+Photo"]'
                                                                                       ),

                                                                                       (
                                                                                           'Giải Taekwondo Quốc Gia 2024',
                                                                                           '<p>Câu Lạc Bộ Taekwondo Hổ Kình đã có màn thể hiện ấn tượng tại giải Quốc Gia 2024, giành tổng cộng 5 huy chương vàng.</p>
                                                                                           <img src="/award.webp" alt="National tournament"/>',
                                                                                           'https://placehold.co/1200x600/eff6ff/2563eb?text=National+Championship+2024',
                                                                                           '2024-07-15T09:00:00',
                                                                                           'Giải Quốc Gia',
                                                                                           'Ban Tổ Chức',
                                                                                           '["https://placehold.co/400x300/fef2f2/dc2626?text=Award+Ceremony","https://placehold.co/400x300/eff6ff/2563eb?text=Fighting+Scene"]'
                                                                                       ),

                                                                                       (
                                                                                           'Giải Taekwondo Trẻ Toàn Thành 2023',
                                                                                           '<p>Giải đấu thường niên dành cho các vận động viên trẻ đã diễn ra thành công rực rỡ với sự tham gia của hơn 300 thí sinh.</p>',
                                                                                           'https://placehold.co/1200x600/fef2f2/dc2626?text=Youth+Championship+2023',
                                                                                           '2023-05-20T08:30:00',
                                                                                           'Giải Thành Phố',
                                                                                           'Huấn Luyện Viên Trưởng',
                                                                                           '["https://placehold.co/400x300/fef2f2/dc2626?text=Team+Youth","https://placehold.co/400x300/eff6ff/2563eb?text=Match+Highlights"]'
                                                                                       ),

                                                                                       (
                                                                                           'Giải Giao Hữu Taekwondo Việt - Hàn 2022',
                                                                                           '<p>Giải giao hữu giữa đội tuyển Việt Nam và Hàn Quốc diễn ra trong không khí thân mật, học hỏi và giao lưu văn hóa.</p>',
                                                                                           'https://placehold.co/1200x600/fef2f2/dc2626?text=Vietnam-Korea+Friendly+2022',
                                                                                           '2022-11-10T14:00:00',
                                                                                           'Giao Hữu Quốc Tế',
                                                                                           'Ban Tổ Chức',
                                                                                           '["https://placehold.co/400x300/fef2f2/dc2626?text=Opening+Ceremony","https://placehold.co/400x300/eff6ff/2563eb?text=Team+Exchange"]'
                                                                                       ),

                                                                                       (
                                                                                           'Lễ Kỷ Niệm Thành Lập Câu Lạc Bộ Hổ Kình',
                                                                                           '<p>Câu Lạc Bộ Taekwondo Hổ Kình kỷ niệm 10 năm thành lập, đánh dấu hành trình phát triển và cống hiến cho phong trào võ thuật Việt Nam.</p>',
                                                                                           'https://placehold.co/1200x600/fef2f2/dc2626?text=Anniversary+2025',
                                                                                           '2025-02-10T10:00:00',
                                                                                           'Sự Kiện',
                                                                                           'Ban Lãnh Đạo CLB',
                                                                                           '["https://placehold.co/400x300/fef2f2/dc2626?text=Anniversary+Stage","https://placehold.co/400x300/eff6ff/2563eb?text=Members+Together"]'
                                                                                       );
