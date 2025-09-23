-- =====================================================
-- DELIVERY MANAGEMENT SYSTEM (SIE)
-- =====================================================

-- Drop and create database
DROP DATABASE IF EXISTS sie_db;
CREATE DATABASE sie_db CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;
USE sie_db;

-- =====================================================
-- LOOKUP TABLES
-- =====================================================

-- TABLE: contract_types
CREATE TABLE contract_types (
    contract_type_id INT AUTO_INCREMENT PRIMARY KEY,
    type_name VARCHAR(50) NOT NULL,
    description TEXT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_active (active)
) ENGINE=InnoDB;

-- TABLE: evidence_types
CREATE TABLE evidence_types (
    evidence_type_id INT AUTO_INCREMENT PRIMARY KEY,
    type_name VARCHAR(50) NOT NULL,
    description TEXT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_active (active)
) ENGINE=InnoDB;

-- TABLE: cancellation_types
CREATE TABLE cancellation_types (
    cancellation_type_id INT AUTO_INCREMENT PRIMARY KEY,
    type_name VARCHAR(50) NOT NULL,
    description TEXT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_active (active)
) ENGINE=InnoDB;

-- TABLE: settlement_statuses
CREATE TABLE settlement_statuses (
    status_id INT AUTO_INCREMENT PRIMARY KEY,
    status_name VARCHAR(50) NOT NULL,
    description TEXT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_active (active)
) ENGINE=InnoDB;

-- TABLE: transaction_types
CREATE TABLE transaction_types (
    transaction_type_id INT AUTO_INCREMENT PRIMARY KEY,
    type_name VARCHAR(50) NOT NULL,
    description TEXT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_active (active)
) ENGINE=InnoDB;

-- TABLE: operation_types
CREATE TABLE operation_types (
    operation_type_id INT AUTO_INCREMENT PRIMARY KEY,
    operation_name VARCHAR(50) NOT NULL,
    description TEXT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_active (active)
) ENGINE=InnoDB;

-- TABLE: incident_types
CREATE TABLE incident_types (
    incident_type_id INT AUTO_INCREMENT PRIMARY KEY,
    type_name VARCHAR(50) NOT NULL,
    description TEXT,
    requires_return BOOLEAN DEFAULT FALSE,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_active (active)
) ENGINE=InnoDB;

-- TABLE: notification_types
CREATE TABLE notification_types (
    notification_type_id INT AUTO_INCREMENT PRIMARY KEY,
    type_name VARCHAR(50) NOT NULL,
    template TEXT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_active (active)
) ENGINE=InnoDB;

-- =====================================================
-- CORE TABLES
-- =====================================================

-- TABLE: roles
CREATE TABLE roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_role_name (role_name),
    INDEX idx_active (active)
) ENGINE=InnoDB;

-- TABLE: users
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    role_id INT NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    national_id VARCHAR(20) UNIQUE,
    active BOOLEAN DEFAULT TRUE,
    two_factor_enabled BOOLEAN DEFAULT FALSE,
    two_factor_code VARCHAR(6),
    two_factor_expiration DATETIME,
    last_login DATETIME,
    failed_login_attempts INT DEFAULT 0,
    locked_until DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(role_id),
    INDEX idx_email (email),
    INDEX idx_role (role_id),
    INDEX idx_active (active),
    INDEX idx_2fa_code (two_factor_code, two_factor_expiration)
) ENGINE=InnoDB;

-- TABLE: refresh_tokens
CREATE TABLE refresh_tokens (
    token_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at DATETIME NOT NULL,
    revoked BOOLEAN DEFAULT FALSE,
    revoked_at DATETIME,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user (user_id),
    INDEX idx_token (token_hash),
    INDEX idx_expires (expires_at),
    INDEX idx_revoked (revoked)
) ENGINE=InnoDB;

-- TABLE: branches
CREATE TABLE branches (
    branch_id INT AUTO_INCREMENT PRIMARY KEY,
    branch_code VARCHAR(20) NOT NULL UNIQUE,
    branch_name VARCHAR(100) NOT NULL,
    address TEXT NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    city VARCHAR(100),
    state VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_code (branch_code),
    INDEX idx_active (active)
) ENGINE=InnoDB;

-- TABLE: loyalty_levels
CREATE TABLE loyalty_levels (
    level_id INT AUTO_INCREMENT PRIMARY KEY,
    level_name VARCHAR(50) NOT NULL UNIQUE,
    min_deliveries INT NOT NULL,
    max_deliveries INT,
    discount_percentage DECIMAL(5,2) NOT NULL,
    free_cancellations INT DEFAULT 0,
    penalty_percentage DECIMAL(5,2) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CHECK (discount_percentage >= 0 AND discount_percentage <= 100),
    CHECK (penalty_percentage >= 0 AND penalty_percentage <= 100),
    INDEX idx_deliveries (min_deliveries, max_deliveries),
    INDEX idx_active (active)
) ENGINE=InnoDB;

-- TABLE: businesses
CREATE TABLE businesses (
    business_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    current_level_id INT,
    tax_id VARCHAR(20) NOT NULL UNIQUE,
    business_name VARCHAR(100) NOT NULL,
    legal_name VARCHAR(100) NOT NULL,
    tax_address TEXT NOT NULL,
    business_phone VARCHAR(20),
    business_email VARCHAR(100),
    support_contact VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    affiliation_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (current_level_id) REFERENCES loyalty_levels(level_id),
    INDEX idx_tax_id (tax_id),
    INDEX idx_level (current_level_id),
    INDEX idx_active (active)
) ENGINE=InnoDB;

-- TABLE: contracts
CREATE TABLE contracts (
    contract_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    admin_id INT NOT NULL,
    contract_type_id INT NOT NULL,
    base_salary DECIMAL(10,2),
    commission_percentage DECIMAL(5,2) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    active BOOLEAN DEFAULT TRUE,
    observations TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (admin_id) REFERENCES users(user_id),
    FOREIGN KEY (contract_type_id) REFERENCES contract_types(contract_type_id),
    CHECK (commission_percentage >= 0 AND commission_percentage <= 100),
    CHECK (end_date IS NULL OR end_date >= start_date),
    INDEX idx_user (user_id),
    INDEX idx_active_dates (active, start_date, end_date)
) ENGINE=InnoDB;

-- TABLE: courier_availability
CREATE TABLE courier_availability (
    availability_id INT AUTO_INCREMENT PRIMARY KEY,
    courier_id INT NOT NULL,
    day_of_week INT NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (courier_id) REFERENCES users(user_id),
    INDEX idx_courier (courier_id),
    INDEX idx_day (day_of_week),
    CHECK (day_of_week >= 1 AND day_of_week <= 7)
) ENGINE=InnoDB;

-- TABLE: tracking_states
CREATE TABLE tracking_states (
    state_id INT AUTO_INCREMENT PRIMARY KEY,
    state_name VARCHAR(50) NOT NULL,
    description TEXT,
    is_final BOOLEAN DEFAULT FALSE,
    state_order INT NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_order (state_order),
    INDEX idx_final (is_final),
    INDEX idx_active (active)
) ENGINE=InnoDB;

-- TABLE: tracking_guides
CREATE TABLE tracking_guides (
    guide_id INT AUTO_INCREMENT PRIMARY KEY,
    guide_number VARCHAR(50) NOT NULL UNIQUE,
    business_id INT NOT NULL,
    origin_branch_id INT NOT NULL,
    courier_id INT,
    coordinator_id INT,
    current_state_id INT NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    courier_commission DECIMAL(10,2),
    recipient_name VARCHAR(100) NOT NULL,
    recipient_phone VARCHAR(20) NOT NULL,
    recipient_address TEXT NOT NULL,
    recipient_city VARCHAR(100),
    recipient_state VARCHAR(100),
    observations TEXT,
    assignment_accepted BOOLEAN DEFAULT FALSE,
    assignment_accepted_at DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    assignment_date DATETIME,
    pickup_date DATETIME,
    delivery_date DATETIME,
    cancellation_date DATETIME,
    FOREIGN KEY (business_id) REFERENCES businesses(business_id),
    FOREIGN KEY (origin_branch_id) REFERENCES branches(branch_id),
    FOREIGN KEY (courier_id) REFERENCES users(user_id),
    FOREIGN KEY (coordinator_id) REFERENCES users(user_id),
    FOREIGN KEY (current_state_id) REFERENCES tracking_states(state_id),
    INDEX idx_guide_number (guide_number),
    INDEX idx_business (business_id),
    INDEX idx_courier (courier_id),
    INDEX idx_state (current_state_id),
    INDEX idx_dates (created_at, delivery_date)
) ENGINE=InnoDB;

-- TABLE: state_history
CREATE TABLE state_history (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    guide_id INT NOT NULL,
    state_id INT NOT NULL,
    user_id INT NOT NULL,
    observations TEXT,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (guide_id) REFERENCES tracking_guides(guide_id),
    FOREIGN KEY (state_id) REFERENCES tracking_states(state_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    INDEX idx_guide (guide_id),
    INDEX idx_changed_at (changed_at)
) ENGINE=InnoDB;

-- TABLE: delivery_incidents
CREATE TABLE delivery_incidents (
    incident_id INT AUTO_INCREMENT PRIMARY KEY,
    guide_id INT NOT NULL,
    incident_type_id INT NOT NULL,
    reported_by_user_id INT NOT NULL,
    description TEXT NOT NULL,
    resolution TEXT,
    resolved BOOLEAN DEFAULT FALSE,
    resolved_at DATETIME,
    resolved_by_user_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (guide_id) REFERENCES tracking_guides(guide_id),
    FOREIGN KEY (incident_type_id) REFERENCES incident_types(incident_type_id),
    FOREIGN KEY (reported_by_user_id) REFERENCES users(user_id),
    FOREIGN KEY (resolved_by_user_id) REFERENCES users(user_id),
    INDEX idx_guide (guide_id),
    INDEX idx_type (incident_type_id),
    INDEX idx_resolved (resolved)
) ENGINE=InnoDB;

-- TABLE: delivery_evidence
CREATE TABLE delivery_evidence (
    evidence_id INT AUTO_INCREMENT PRIMARY KEY,
    guide_id INT NOT NULL,
    evidence_type_id INT NOT NULL,
    file_url VARCHAR(500),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (guide_id) REFERENCES tracking_guides(guide_id),
    FOREIGN KEY (evidence_type_id) REFERENCES evidence_types(evidence_type_id),
    INDEX idx_guide (guide_id),
    INDEX idx_type (evidence_type_id)
) ENGINE=InnoDB;

-- TABLE: cancellations
CREATE TABLE cancellations (
    cancellation_id INT AUTO_INCREMENT PRIMARY KEY,
    guide_id INT NOT NULL UNIQUE,
    cancelled_by_user_id INT NOT NULL,
    cancellation_type_id INT NOT NULL,
    reason TEXT NOT NULL,
    penalty_amount DECIMAL(10,2) DEFAULT 0,
    courier_commission DECIMAL(10,2) DEFAULT 0,
    cancelled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (guide_id) REFERENCES tracking_guides(guide_id),
    FOREIGN KEY (cancelled_by_user_id) REFERENCES users(user_id),
    FOREIGN KEY (cancellation_type_id) REFERENCES cancellation_types(cancellation_type_id),
    INDEX idx_guide (guide_id),
    INDEX idx_type (cancellation_type_id),
    INDEX idx_date (cancelled_at)
) ENGINE=InnoDB;

-- TABLE: notifications
CREATE TABLE notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    guide_id INT NOT NULL,
    notification_type_id INT NOT NULL,
    recipient_email VARCHAR(100),
    recipient_phone VARCHAR(20),
    message TEXT NOT NULL,
    sent BOOLEAN DEFAULT FALSE,
    sent_at DATETIME,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (guide_id) REFERENCES tracking_guides(guide_id),
    FOREIGN KEY (notification_type_id) REFERENCES notification_types(notification_type_id),
    INDEX idx_guide (guide_id),
    INDEX idx_sent (sent),
    INDEX idx_created (created_at)
) ENGINE=InnoDB;

-- TABLE: courier_settlements
CREATE TABLE courier_settlements (
    settlement_id INT AUTO_INCREMENT PRIMARY KEY,
    courier_id INT NOT NULL,
    status_id INT NOT NULL,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    total_deliveries INT NOT NULL DEFAULT 0,
    total_commissions DECIMAL(10,2) NOT NULL DEFAULT 0,
    total_penalties DECIMAL(10,2) NOT NULL DEFAULT 0,
    net_total DECIMAL(10,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    payment_date DATETIME,
    FOREIGN KEY (courier_id) REFERENCES users(user_id),
    FOREIGN KEY (status_id) REFERENCES settlement_statuses(status_id),
    INDEX idx_courier (courier_id),
    INDEX idx_period (period_start, period_end),
    INDEX idx_status (status_id)
) ENGINE=InnoDB;

-- TABLE: settlement_details
CREATE TABLE settlement_details (
    detail_id INT AUTO_INCREMENT PRIMARY KEY,
    settlement_id INT NOT NULL,
    guide_id INT NOT NULL,
    transaction_type_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    transaction_date DATETIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (settlement_id) REFERENCES courier_settlements(settlement_id),
    FOREIGN KEY (guide_id) REFERENCES tracking_guides(guide_id),
    FOREIGN KEY (transaction_type_id) REFERENCES transaction_types(transaction_type_id),
    INDEX idx_settlement (settlement_id),
    INDEX idx_guide (guide_id)
) ENGINE=InnoDB;

-- TABLE: monthly_discounts
CREATE TABLE monthly_discounts (
    discount_id INT AUTO_INCREMENT PRIMARY KEY,
    business_id INT NOT NULL,
    month INT NOT NULL,
    year INT NOT NULL,
    total_deliveries INT NOT NULL,
    applied_level_id INT NOT NULL,
    total_before_discount DECIMAL(10,2) NOT NULL,
    discount_percentage DECIMAL(5,2) NOT NULL,
    discount_amount DECIMAL(10,2) NOT NULL,
    total_after_discount DECIMAL(10,2) NOT NULL,
    calculated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (business_id) REFERENCES businesses(business_id),
    FOREIGN KEY (applied_level_id) REFERENCES loyalty_levels(level_id),
    UNIQUE KEY uk_business_period (business_id, month, year),
    INDEX idx_period (month, year),
    CHECK (month >= 1 AND month <= 12),
    CHECK (year >= 2024)
) ENGINE=InnoDB;

-- TABLE: audit_log
CREATE TABLE audit_log (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    table_name VARCHAR(100) NOT NULL,
    operation_type_id INT NOT NULL,
    record_id INT,
    old_data JSON,
    new_data JSON,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (operation_type_id) REFERENCES operation_types(operation_type_id),
    INDEX idx_user (user_id),
    INDEX idx_table (table_name),
    INDEX idx_created (created_at),
    INDEX idx_operation (operation_type_id)
) ENGINE=InnoDB;

-- TABLE: system_config
CREATE TABLE system_config (
    config_id INT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_key (config_key)
) ENGINE=InnoDB;

-- =====================================================
-- VIEWS FOR REPORTS
-- =====================================================

-- View: Completed, cancelled and rejected deliveries
CREATE VIEW v_delivery_summary AS
SELECT 
    DATE(tg.created_at) as delivery_date,
    COUNT(CASE WHEN tg.current_state_id = 5 THEN 1 END) as completed,
    COUNT(CASE WHEN tg.current_state_id = 6 THEN 1 END) as cancelled,
    COUNT(CASE WHEN tg.current_state_id = 7 THEN 1 END) as rejected,
    COUNT(*) as total
FROM tracking_guides tg
GROUP BY DATE(tg.created_at);

-- View: Commissions by courier and period
CREATE VIEW v_courier_commissions AS
SELECT 
    u.user_id,
    CONCAT(u.first_name, ' ', u.last_name) as courier_name,
    YEAR(tg.delivery_date) as year,
    MONTH(tg.delivery_date) as month,
    COUNT(*) as total_deliveries,
    SUM(tg.courier_commission) as total_commission
FROM tracking_guides tg
JOIN users u ON tg.courier_id = u.user_id
WHERE tg.current_state_id = 5
GROUP BY u.user_id, YEAR(tg.delivery_date), MONTH(tg.delivery_date);

-- View: Business ranking by monthly volume
CREATE VIEW v_business_ranking AS
SELECT 
    b.business_id,
    b.business_name,
    YEAR(tg.created_at) as year,
    MONTH(tg.created_at) as month,
    COUNT(*) as total_deliveries,
    ll.level_name as current_level
FROM businesses b
LEFT JOIN tracking_guides tg ON b.business_id = tg.business_id
LEFT JOIN loyalty_levels ll ON b.current_level_id = ll.level_id
GROUP BY b.business_id, YEAR(tg.created_at), MONTH(tg.created_at)
ORDER BY total_deliveries DESC;

-- =====================================================
-- TRIGGERS FOR BUSINESS LOGIC
-- =====================================================

DELIMITER //

-- Trigger: Validate active contract before assignment
CREATE TRIGGER trg_validate_active_contract
BEFORE UPDATE ON tracking_guides
FOR EACH ROW
BEGIN
    DECLARE has_active_contract INT;
    DECLARE commission_rate DECIMAL(5,2);
    
    IF NEW.courier_id IS NOT NULL AND OLD.courier_id IS NULL THEN
        SELECT COUNT(*), MAX(c.commission_percentage)
        INTO has_active_contract, commission_rate
        FROM contracts c
        WHERE c.user_id = NEW.courier_id
        AND c.active = TRUE
        AND CURDATE() BETWEEN c.start_date AND IFNULL(c.end_date, '9999-12-31');
        
        IF has_active_contract = 0 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Repartidor sin contrato activo';
        END IF;
        
        -- Calculate commission automatically
        SET NEW.courier_commission = NEW.base_price * (commission_rate / 100);
        SET NEW.assignment_date = NOW();
        SET NEW.updated_at = NOW();
    END IF;
END//

-- Trigger: Record state changes in history
CREATE TRIGGER trg_record_state_history
AFTER UPDATE ON tracking_guides
FOR EACH ROW
BEGIN
    IF NEW.current_state_id != OLD.current_state_id THEN
        INSERT INTO state_history (guide_id, state_id, user_id, observations)
        VALUES (NEW.guide_id, NEW.current_state_id, 
                IFNULL(NEW.courier_id, NEW.coordinator_id),
                'Cambio de estado registrado');
    END IF;
END//

-- Trigger: Send notifications on state changes
CREATE TRIGGER trg_send_notifications
AFTER UPDATE ON tracking_guides
FOR EACH ROW
BEGIN
    DECLARE notification_type INT;
    DECLARE recipient_phone VARCHAR(20);
    
    -- Determine notification type based on new state
    -- IDs: 4=En Ruta, 5=Entregada
    IF NEW.current_state_id = 4 AND OLD.current_state_id != 4 THEN
        SET notification_type = 1; -- En Ruta notification
        INSERT INTO notifications (guide_id, notification_type_id, recipient_phone, message)
        VALUES (NEW.guide_id, notification_type, NEW.recipient_phone, 
                CONCAT('Su paquete ', NEW.guide_number, ' esta en ruta'));
    ELSEIF NEW.current_state_id = 5 AND OLD.current_state_id != 5 THEN
        SET notification_type = 3; -- Entregado notification
        INSERT INTO notifications (guide_id, notification_type_id, recipient_phone, message)
        VALUES (NEW.guide_id, notification_type, NEW.recipient_phone, 
                CONCAT('Su paquete ', NEW.guide_number, ' ha sido entregado'));
    END IF;
END//

-- Trigger: Apply cancellation penalty based on loyalty level
-- IDs: Cancellation Type 1=Business, 2=Customer
-- IDs: States 6=Cancelled, 7=Rejected
CREATE TRIGGER trg_apply_cancellation_penalty
BEFORE INSERT ON cancellations
FOR EACH ROW
BEGIN
    DECLARE business_level INT;
    DECLARE penalty_rate DECIMAL(5,2);
    DECLARE monthly_cancellations INT;
    DECLARE free_cancellations INT;
    DECLARE base_commission DECIMAL(10,2);
    DECLARE guide_business_id INT;
    DECLARE guide_state INT;
    
    -- Get current guide state
    SELECT current_state_id INTO guide_state
    FROM tracking_guides
    WHERE guide_id = NEW.guide_id;
    
    -- Prevent cancellation after pickup (state > 3)
    IF NEW.cancellation_type_id = 1 AND guide_state > 3 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede cancelar despues de recoleccion';
    END IF;
    
    -- Get business level and commission
    SELECT b.current_level_id, tg.courier_commission, b.business_id
    INTO business_level, base_commission, guide_business_id
    FROM tracking_guides tg
    JOIN businesses b ON tg.business_id = b.business_id
    WHERE tg.guide_id = NEW.guide_id;
    
    -- If cancellation is by business (ID=1)
    IF NEW.cancellation_type_id = 1 THEN
        -- Count monthly cancellations for this business
        SELECT COUNT(*)
        INTO monthly_cancellations
        FROM cancellations c
        JOIN tracking_guides tg ON c.guide_id = tg.guide_id
        WHERE tg.business_id = guide_business_id
        AND MONTH(c.cancelled_at) = MONTH(CURDATE())
        AND YEAR(c.cancelled_at) = YEAR(CURDATE())
        AND c.cancellation_type_id = 1;
        
        -- Get loyalty level configuration
        SELECT ll.penalty_percentage, ll.free_cancellations
        INTO penalty_rate, free_cancellations
        FROM loyalty_levels ll
        WHERE ll.level_id = business_level;
        
        -- Apply penalty logic (CORRECTED: penalty is on courier commission, not base price)
        IF monthly_cancellations < free_cancellations THEN
            SET NEW.penalty_amount = 0;
            SET NEW.courier_commission = 0;
        ELSE
            -- Penalty is percentage of courier commission
            SET NEW.courier_commission = IFNULL(base_commission, 0) * (penalty_rate / 100);
            SET NEW.penalty_amount = NEW.courier_commission;
        END IF;
        
        -- Update to Cancelled state (ID=6)
        UPDATE tracking_guides 
        SET current_state_id = 6,
            cancellation_date = NOW(),
            updated_at = NOW()
        WHERE guide_id = NEW.guide_id;
    ELSE
        -- Customer cancellation (ID=2), update to Rejected state (ID=7)
        SET NEW.penalty_amount = 0;
        SET NEW.courier_commission = IFNULL(base_commission, 0);
        
        UPDATE tracking_guides 
        SET current_state_id = 7,
            cancellation_date = NOW(),
            updated_at = NOW()
        WHERE guide_id = NEW.guide_id;
    END IF;
END//

-- Trigger: Update loyalty level after monthly discount calculation
CREATE TRIGGER trg_update_loyalty_level
AFTER INSERT ON monthly_discounts
FOR EACH ROW
BEGIN
    DECLARE new_level_id INT;
    
    -- Determine new level based on deliveries
    SELECT level_id INTO new_level_id
    FROM loyalty_levels
    WHERE NEW.total_deliveries >= min_deliveries
    AND (max_deliveries IS NULL OR NEW.total_deliveries <= max_deliveries)
    AND active = TRUE
    LIMIT 1;
    
    -- Update business level
    IF new_level_id IS NOT NULL THEN
        UPDATE businesses
        SET current_level_id = new_level_id,
            updated_at = NOW()
        WHERE business_id = NEW.business_id;
    END IF;
END//

-- Trigger: Generate guide number automatically
CREATE TRIGGER trg_generate_guide_number
BEFORE INSERT ON tracking_guides
FOR EACH ROW
BEGIN
    DECLARE next_number INT;
    DECLARE current_year VARCHAR(4);
    
    SET current_year = YEAR(CURDATE());
    
    SELECT IFNULL(MAX(CAST(SUBSTRING(guide_number, 5) AS UNSIGNED)), 0) + 1
    INTO next_number
    FROM tracking_guides
    WHERE guide_number LIKE CONCAT(current_year, '%');
    
    SET NEW.guide_number = CONCAT(current_year, LPAD(next_number, 8, '0'));
    SET NEW.current_state_id = 1; -- Always start with Created state
END//

-- Trigger: Update dates based on state changes
-- IDs: 3=Picked Up, 5=Delivered, 6=Cancelled, 7=Rejected, 8=Incidencia
CREATE TRIGGER trg_update_guide_dates
BEFORE UPDATE ON tracking_guides
FOR EACH ROW
BEGIN
    -- State ID 3: Picked Up
    IF NEW.current_state_id = 3 AND OLD.current_state_id != 3 THEN
        SET NEW.pickup_date = NOW();
    -- State ID 5: Delivered
    ELSEIF NEW.current_state_id = 5 AND OLD.current_state_id != 5 THEN
        SET NEW.delivery_date = NOW();
    -- State ID 6 or 7: Cancelled or Rejected
    ELSEIF (NEW.current_state_id IN (6, 7)) AND (OLD.current_state_id NOT IN (6, 7)) THEN
        SET NEW.cancellation_date = NOW();
    END IF;
    
    SET NEW.updated_at = NOW();
END//

-- Trigger: Handle courier assignment acceptance
CREATE TRIGGER trg_assignment_acceptance
BEFORE UPDATE ON tracking_guides
FOR EACH ROW
BEGIN
    -- When assignment is accepted
    IF NEW.assignment_accepted = TRUE AND OLD.assignment_accepted = FALSE THEN
        SET NEW.assignment_accepted_at = NOW();
        -- Move to Assigned state if still in Created
        IF NEW.current_state_id = 1 THEN
            SET NEW.current_state_id = 2;
        END IF;
    END IF;
END//

-- Trigger: Audit critical operations on contracts
-- Operation Type IDs: 1=INSERT, 2=UPDATE, 3=DELETE
CREATE TRIGGER trg_audit_contracts_insert
AFTER INSERT ON contracts
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (
        user_id, table_name, operation_type_id, 
        record_id, new_data
    )
    VALUES (
        NEW.admin_id, 'contracts', 1, -- 1 = INSERT
        NEW.contract_id, JSON_OBJECT(
            'user_id', NEW.user_id,
            'contract_type_id', NEW.contract_type_id,
            'commission_percentage', NEW.commission_percentage,
            'start_date', NEW.start_date,
            'end_date', NEW.end_date
        )
    );
END//

CREATE TRIGGER trg_audit_contracts_update
AFTER UPDATE ON contracts
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (
        user_id, table_name, operation_type_id, 
        record_id, old_data, new_data
    )
    VALUES (
        NEW.admin_id, 'contracts', 2, -- 2 = UPDATE
        NEW.contract_id,
        JSON_OBJECT(
            'active', OLD.active,
            'commission_percentage', OLD.commission_percentage
        ),
        JSON_OBJECT(
            'active', NEW.active,
            'commission_percentage', NEW.commission_percentage
        )
    );
END//

-- Trigger: Validate courier has role 3 (Repartidor) when creating contract
CREATE TRIGGER trg_validate_courier_role
BEFORE INSERT ON contracts
FOR EACH ROW
BEGIN
    DECLARE user_role INT;
    
    SELECT role_id INTO user_role
    FROM users
    WHERE user_id = NEW.user_id;
    
    IF user_role != 3 THEN -- 3 = Repartidor role
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Solo se pueden crear contratos para usuarios con rol Repartidor';
    END IF;
END//

-- Trigger: Log user login attempts
CREATE TRIGGER trg_log_login_attempt
BEFORE UPDATE ON users
FOR EACH ROW
BEGIN
    -- If login failed (password verification happens in application)
    IF NEW.failed_login_attempts > OLD.failed_login_attempts THEN
        -- Lock account after 5 failed attempts for 30 minutes
        IF NEW.failed_login_attempts >= 5 THEN
            SET NEW.locked_until = DATE_ADD(NOW(), INTERVAL 30 MINUTE);
        END IF;
    END IF;
    
    -- Reset failed attempts on successful login
    IF NEW.last_login > OLD.last_login THEN
        SET NEW.failed_login_attempts = 0;
        SET NEW.locked_until = NULL;
    END IF;
END//

-- Allow edition only before recolection
CREATE TRIGGER trg_validate_guide_edit
BEFORE UPDATE ON tracking_guides
FOR EACH ROW
BEGIN
    IF OLD.current_state_id > 2 AND (
        NEW.recipient_name != OLD.recipient_name OR
        NEW.recipient_address != OLD.recipient_address OR
        NEW.recipient_phone != OLD.recipient_phone
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede editar después de asignación';
    END IF;
END//

DELIMITER ;