-- Tạo database
CREATE DATABASE IF NOT EXISTS shift_management;
USE shift_management;

-- Tạo bảng User
CREATE TABLE IF NOT EXISTS User (
    id INTEGER(10) PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NULL,
    email VARCHAR(255) NULL,
    phone VARCHAR(255) NULL,
    hourlyRate DOUBLE(10,2),
    role VARCHAR(255),
    active BIT DEFAULT 1
);

-- Tạo bảng WorkSchedule
CREATE TABLE IF NOT EXISTS WorkSchedule (
    id INTEGER(10) PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NULL,
    createDate DATE,
    tblUserId INTEGER(10),
    FOREIGN KEY (tblUserId) REFERENCES User(id)
);

-- Tạo bảng SlotTemplate
CREATE TABLE IF NOT EXISTS SlotTemplate (
    id INTEGER(10) PRIMARY KEY AUTO_INCREMENT,
    dayOfWeek VARCHAR(255),
    startTime TIME,
    endTime TIME,
    maxEmployee INTEGER(10)
);

-- Tạo bảng WorkScheduleSlot
CREATE TABLE IF NOT EXISTS WorkScheduleSlot (
    id INTEGER(10) PRIMARY KEY AUTO_INCREMENT,
    tblWorkScheduleId INTEGER(10),
    tblSlotTemplateId INTEGER(10),
    FOREIGN KEY (tblWorkScheduleId) REFERENCES WorkSchedule(id),
    FOREIGN KEY (tblSlotTemplateId) REFERENCES SlotTemplate(id)
);

-- Tạo bảng ShiftSlot
CREATE TABLE IF NOT EXISTS ShiftSlot (
    id INTEGER(10) PRIMARY KEY AUTO_INCREMENT,
    dayOfWeek VARCHAR(255),
    startTime TIMESTAMP,
    endTime TIMESTAMP,
    weekStartDate DATE,
    status VARCHAR(255),
    maxEmployee INTEGER(10),
    tblSlotTemplateId INTEGER(10),
    tblCreatedById INTEGER(10),
    FOREIGN KEY (tblSlotTemplateId) REFERENCES SlotTemplate(id),
    FOREIGN KEY (tblCreatedById) REFERENCES User(id)
);

-- Tạo bảng EmployeeShift
CREATE TABLE IF NOT EXISTS EmployeeShift (
    id INTEGER(10) PRIMARY KEY AUTO_INCREMENT,
    registrationDate TIMESTAMP,
    tblShiftSlotId INTEGER(10),
    tblUserId INTEGER(10),
    FOREIGN KEY (tblShiftSlotId) REFERENCES ShiftSlot(id),
    FOREIGN KEY (tblUserId) REFERENCES User(id)
);

-- Tạo bảng Payment
CREATE TABLE IF NOT EXISTS Payment (
    id INTEGER(10) PRIMARY KEY AUTO_INCREMENT,
    weekStartDate DATE,
    paymentDate DATE,
    totalHour DOUBLE(10,2),
    amount DOUBLE(10,2),
    status VARCHAR(255),
    tblEmployeeId INTEGER(10),
    tblProcessedById INTEGER(10),
    FOREIGN KEY (tblEmployeeId) REFERENCES User(id),
    FOREIGN KEY (tblProcessedById) REFERENCES User(id)
);

-- Tạo bảng TimeRecord
CREATE TABLE IF NOT EXISTS TimeRecord (
    id INTEGER(10) PRIMARY KEY AUTO_INCREMENT,
    actualStartTime TIMESTAMP,
    actualEndTime TIMESTAMP,
    tblEmployeeShiftId INTEGER(10),
    tblPaymentId INTEGER(10),
    FOREIGN KEY (tblEmployeeShiftId) REFERENCES EmployeeShift(id),
    FOREIGN KEY (tblPaymentId) REFERENCES Payment(id)
);

-- Tạo trigger để tự động tạo hoặc liên kết Payment khi thêm TimeRecord mới
DELIMITER //

-- Trigger BEFORE INSERT để xác định hoặc tạo Payment
CREATE TRIGGER before_timerecord_insert
BEFORE INSERT ON TimeRecord
FOR EACH ROW
BEGIN
    DECLARE employee_id INT;
    DECLARE week_start_date DATE;
    DECLARE existing_payment_id INT DEFAULT NULL;
    
    -- Lấy employee_id từ EmployeeShift tương ứng
    SELECT es.tblUserId, ss.weekStartDate
    INTO employee_id, week_start_date
    FROM EmployeeShift es
    JOIN ShiftSlot ss ON es.tblShiftSlotId = ss.id
    WHERE es.id = NEW.tblEmployeeShiftId;
    
    -- Kiểm tra xem đã có Payment nào cho employee trong tuần đó chưa
    SELECT id INTO existing_payment_id
    FROM Payment
    WHERE tblEmployeeId = employee_id
      AND weekStartDate = week_start_date
    LIMIT 1;
    
    -- Nếu chưa có Payment, tạo mới và lấy ID
    IF existing_payment_id IS NULL THEN
        INSERT INTO Payment (weekStartDate, paymentDate, totalHour, amount, status, tblEmployeeId, tblProcessedById)
        VALUES (week_start_date, NULL, 0, 0, 'PENDING', employee_id, 
               (SELECT id FROM User WHERE role = 'ADMIN' LIMIT 1));
        
        SET existing_payment_id = LAST_INSERT_ID();
    END IF;
    
    -- Gán payment_id cho TimeRecord mới
    SET NEW.tblPaymentId = existing_payment_id;
END //

-- Trigger AFTER INSERT để cập nhật totalHour và amount
CREATE TRIGGER after_timerecord_insert
AFTER INSERT ON TimeRecord
FOR EACH ROW
BEGIN
    DECLARE employee_hourly_rate DOUBLE(10,2);
    
    -- Lấy hourlyRate của employee
    SELECT u.hourlyRate INTO employee_hourly_rate
    FROM EmployeeShift es
    JOIN User u ON es.tblUserId = u.id
    WHERE es.id = NEW.tblEmployeeShiftId;
    
    -- Cập nhật tổng giờ và số tiền cho Payment tương ứng
    UPDATE Payment p
    SET p.totalHour = (
            SELECT COALESCE(SUM(
                TIMESTAMPDIFF(SECOND, tr.actualStartTime, tr.actualEndTime) / 3600.0
            ), 0)
            FROM TimeRecord tr
            WHERE tr.tblPaymentId = NEW.tblPaymentId
        ),
        p.amount = (
            SELECT COALESCE(SUM(
                TIMESTAMPDIFF(SECOND, tr.actualStartTime, tr.actualEndTime) / 3600.0
            ), 0) * employee_hourly_rate
            FROM TimeRecord tr
            WHERE tr.tblPaymentId = NEW.tblPaymentId
        )
    WHERE p.id = NEW.tblPaymentId;
END //

-- Tạo trigger để cập nhật hoặc xóa Payment khi TimeRecord bị xóa
CREATE TRIGGER after_timerecord_update
AFTER UPDATE ON TimeRecord
FOR EACH ROW
BEGIN
    DECLARE employee_hourly_rate DOUBLE(10,2);
    
    -- Lấy hourlyRate của employee
    SELECT u.hourlyRate INTO employee_hourly_rate
    FROM EmployeeShift es
    JOIN User u ON es.tblUserId = u.id
    WHERE es.id = NEW.tblEmployeeShiftId;
    
    -- Cập nhật tổng giờ và số tiền cho Payment tương ứng
    UPDATE Payment p
    SET p.totalHour = (
            SELECT COALESCE(SUM(
                TIMESTAMPDIFF(SECOND, tr.actualStartTime, tr.actualEndTime) / 3600.0
            ), 0)
            FROM TimeRecord tr
            WHERE tr.tblPaymentId = NEW.tblPaymentId
        ),
        p.amount = (
            SELECT COALESCE(SUM(
                TIMESTAMPDIFF(SECOND, tr.actualStartTime, tr.actualEndTime) / 3600.0
            ), 0) * employee_hourly_rate
            FROM TimeRecord tr
            WHERE tr.tblPaymentId = NEW.tblPaymentId
        )
    WHERE p.id = NEW.tblPaymentId;
END //

CREATE TRIGGER after_timerecord_delete
AFTER DELETE ON TimeRecord
FOR EACH ROW
BEGIN
    DECLARE employee_hourly_rate DOUBLE(10,2);
    
    -- Lấy hourlyRate của employee
    SELECT u.hourlyRate INTO employee_hourly_rate
    FROM EmployeeShift es
    JOIN User u ON es.tblUserId = u.id
    WHERE es.id = OLD.tblEmployeeShiftId;
    
    -- Cập nhật tổng giờ và số tiền cho Payment tương ứng
    UPDATE Payment p
    SET p.totalHour = (
            SELECT COALESCE(SUM(
                TIMESTAMPDIFF(SECOND, tr.actualStartTime, tr.actualEndTime) / 3600.0
            ), 0)
            FROM TimeRecord tr
            WHERE tr.tblPaymentId = OLD.tblPaymentId
        ),
        p.amount = (
            SELECT COALESCE(SUM(
                TIMESTAMPDIFF(SECOND, tr.actualStartTime, tr.actualEndTime) / 3600.0
            ), 0) * employee_hourly_rate
            FROM TimeRecord tr
            WHERE tr.tblPaymentId = OLD.tblPaymentId
        )
    WHERE p.id = OLD.tblPaymentId;
END //

DELIMITER ;

-- Thêm dữ liệu mẫu vào bảng User
INSERT INTO User (username, password, name, email, phone, hourlyRate, role, active)
VALUES 
('admin', 'password123', 'Admin User', 'admin@example.com', '0123456789', 25.00, 'ADMIN', 1),
('manager', 'password123', 'Manager User', 'manager@example.com', '0987654321', 20.00, 'MANAGER', 1),
('employee1', 'password123', 'John Doe', 'john@example.com', '0123456788', 15.00, 'EMPLOYEE', 1),
('employee2', 'password123', 'Jane Smith', 'jane@example.com', '0123456787', 15.00, 'EMPLOYEE', 1),
('employee3', 'password123', 'Bob Johnson', 'bob@example.com', '0123456786', 15.00, 'EMPLOYEE', 1);

-- Thêm dữ liệu mẫu vào bảng WorkSchedule
INSERT INTO WorkSchedule (name, createDate, tblUserId)
VALUES 
('Schedule Spring 2023', '2023-01-15', 1),
('Schedule Summer 2023', '2023-05-20', 1),
('Schedule Fall 2023', '2023-08-25', 2);

-- Thêm dữ liệu mẫu vào bảng SlotTemplate
INSERT INTO SlotTemplate (dayOfWeek, startTime, endTime, maxEmployee)
VALUES 
('MONDAY', '08:00:00', '12:00:00', 3),
('MONDAY', '13:00:00', '17:00:00', 2),
('TUESDAY', '08:00:00', '12:00:00', 3),
('TUESDAY', '13:00:00', '17:00:00', 2),
('WEDNESDAY', '08:00:00', '17:00:00', 4);

-- Thêm dữ liệu mẫu vào bảng WorkScheduleSlot
INSERT INTO WorkScheduleSlot (tblWorkScheduleId, tblSlotTemplateId)
VALUES 
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
(2, 1), (2, 2), (2, 3), (2, 4),
(3, 1), (3, 3), (3, 5);

-- Thêm dữ liệu mẫu vào bảng ShiftSlot
INSERT INTO ShiftSlot (dayOfWeek, startTime, endTime, weekStartDate, status, maxEmployee, tblSlotTemplateId, tblCreatedById)
VALUES 
('MONDAY', '2023-05-01 08:00:00', '2023-05-01 12:00:00', '2023-05-01', 'ACTIVE', 3, 1, 2),
('MONDAY', '2023-05-01 13:00:00', '2023-05-01 17:00:00', '2023-05-01', 'ACTIVE', 2, 2, 2),
('TUESDAY', '2023-05-02 08:00:00', '2023-05-02 12:00:00', '2023-05-01', 'ACTIVE', 3, 3, 2),
('MONDAY', '2023-05-08 08:00:00', '2023-05-08 12:00:00', '2023-05-08', 'ACTIVE', 3, 1, 2),
('TUESDAY', '2023-05-09 08:00:00', '2023-05-09 12:00:00', '2023-05-08', 'ACTIVE', 3, 3, 2);

-- Thêm dữ liệu mẫu vào bảng EmployeeShift
INSERT INTO EmployeeShift (registrationDate, tblShiftSlotId, tblUserId)
VALUES 
('2023-04-25 10:00:00', 1, 3),
('2023-04-25 14:30:00', 1, 4),
('2023-04-26 09:15:00', 2, 3),
('2023-04-26 11:20:00', 3, 5),
('2023-05-03 16:45:00', 4, 3),
('2023-05-04 08:30:00', 5, 4);

-- Thêm dữ liệu mẫu vào bảng Payment (chỉ thêm một số, phần còn lại sẽ được tạo tự động bởi trigger)
INSERT INTO Payment (weekStartDate, paymentDate, totalHour, amount, status, tblEmployeeId, tblProcessedById)
VALUES 
('2023-05-01', '2023-05-14', 16.0, 240.00, 'PAID', 3, 1),
('2023-05-01', '2023-05-14', 8.0, 120.00, 'PAID', 4, 1),
('2023-05-01', '2023-05-14', 4.0, 60.00, 'PAID', 5, 2),
('2023-05-08', '2023-05-21', 8.0, 120.00, 'PENDING', 3, 1);

-- Thêm dữ liệu mẫu vào bảng TimeRecord
-- Lưu ý: Khi thêm TimeRecord, trigger sẽ tự động liên kết với Payment hoặc tạo Payment mới
INSERT INTO TimeRecord (actualStartTime, actualEndTime, tblEmployeeShiftId, tblPaymentId)
VALUES 
('2023-05-01 08:05:00', '2023-05-01 12:10:00', 1, 1),  -- Liên kết với Payment #1
('2023-05-01 13:00:00', '2023-05-01 17:15:00', 3, 1),  -- Liên kết với Payment #1
('2023-05-02 08:00:00', '2023-05-02 12:05:00', 4, 3),  -- Liên kết với Payment #3
('2023-05-01 08:00:00', '2023-05-01 12:00:00', 2, 2),  -- Liên kết với Payment #2
('2023-05-08 07:55:00', '2023-05-08 12:10:00', 5, 4);  -- Liên kết với Payment #4

-- Thêm một TimeRecord mới để kiểm tra trigger (trigger sẽ tự tạo Payment mới hoặc liên kết với Payment hiện có)
INSERT INTO TimeRecord (actualStartTime, actualEndTime, tblEmployeeShiftId)
VALUES ('2023-05-09 08:00:00', '2023-05-09 12:00:00', 6);