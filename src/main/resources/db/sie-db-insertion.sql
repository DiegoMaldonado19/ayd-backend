-- =====================================================
-- DATA INSERTION SCRIPT
-- =====================================================

USE sie_db;

-- =====================================================
-- INSERT LOOKUP DATA
-- =====================================================

-- Contract Types (IDs: 1=Temporal, 2=Fijo, 3=Por Horas)
INSERT INTO contract_types (type_name, description) VALUES
('Temporal', 'Contrato por tiempo determinado'),
('Fijo', 'Contrato permanente sin fecha de finalizacion'),
('Por Horas', 'Contrato basado en horas trabajadas');

-- Evidence Types (IDs: 1=Fotografia, 2=Firma, 3=Nota)
INSERT INTO evidence_types (type_name, description) VALUES
('Fotografia', 'Evidencia fotografica de la entrega'),
('Firma', 'Firma digital o escaneada del receptor'),
('Nota', 'Nota descriptiva de la entrega');

-- Cancellation Types (IDs: 1=Comercio, 2=Cliente)
INSERT INTO cancellation_types (type_name, description) VALUES
('Comercio', 'Cancelacion realizada por el comercio afiliado'),
('Cliente', 'Rechazo o cancelacion por parte del cliente final');

-- Settlement Statuses (IDs: 1=Pendiente, 2=Aprobada, 3=Pagada)
INSERT INTO settlement_statuses (status_name, description) VALUES
('Pendiente', 'Liquidacion pendiente de revision'),
('Aprobada', 'Liquidacion aprobada para pago'),
('Pagada', 'Liquidacion pagada al repartidor');

-- Transaction Types (IDs: 1=Entrega, 2=Cancelacion)
INSERT INTO transaction_types (type_name, description) VALUES
('Entrega', 'Comision por entrega completada'),
('Cancelacion', 'Penalizacion o comision por cancelacion');

-- Operation Types (IDs: 1=Insercion, 2=Actualizacion, 3=Eliminacion)
INSERT INTO operation_types (operation_name, description) VALUES
('Insercion', 'Creacion de nuevo registro'),
('Actualizacion', 'Modificacion de registro existente'),
('Eliminacion', 'Eliminacion de registro');

-- Incident Types (IDs: 1-6 for different incidents)
INSERT INTO incident_types (type_name, description, requires_return) VALUES
('Cliente Ausente', 'El cliente no se encontraba en la direccion', TRUE),
('Direccion Incorrecta', 'La direccion proporcionada es incorrecta o no existe', TRUE),
('Accidente en Ruta', 'Incidente vehicular o personal durante la entrega', FALSE),
('Paquete Danado', 'El paquete presenta danos visibles', TRUE),
('Clima Adverso', 'Condiciones climaticas impiden la entrega', FALSE),
('Trafico Excesivo', 'Retraso significativo por congestion vehicular', FALSE);

-- Notification Types (IDs: 1-3 for different states)
INSERT INTO notification_types (type_name, template) VALUES
('En Ruta', 'Su paquete {guide_number} esta en camino hacia su direccion'),
('Entrega Proxima', 'El repartidor esta proximo a llegar con su paquete {guide_number}'),
('Entregado', 'Su paquete {guide_number} ha sido entregado exitosamente');

-- =====================================================
-- INSERT CORE DATA
-- =====================================================

-- Roles (IDs: 1=Admin, 2=Coord, 3=Repartidor, 4=Comercio, 5=Cliente)
INSERT INTO roles (role_name, description) VALUES
('Administrador', 'Usuario con acceso total al sistema para gestion y configuracion'),
('Coordinador', 'Supervisa operaciones diarias y asigna entregas a repartidores'),
('Repartidor', 'Personal encargado de realizar entregas fisicas'),
('Comercio', 'Comercio afiliado que genera guias de entrega'),
('Cliente', 'Cliente final receptor de paquetes');

-- Tracking States (IDs: 1-8, including Incidencia)
INSERT INTO tracking_states (state_name, description, is_final, state_order) VALUES
('Creada', 'Guia generada por el comercio afiliado', FALSE, 1),
('Asignada', 'Repartidor designado para la entrega', FALSE, 2),
('Recogida', 'Paquete en poder del repartidor', FALSE, 3),
('En Ruta', 'Paquete en transito hacia el destino', FALSE, 4),
('Entregada', 'Entrega completada exitosamente', TRUE, 5),
('Cancelada', 'Anulada antes de la recoleccion', TRUE, 6),
('Rechazada', 'No aceptada por el cliente final', TRUE, 7),
('Incidencia', 'Problema reportado durante la entrega', FALSE, 8);

-- Loyalty Levels (IDs: 1=Plata, 2=Oro, 3=Diamante)
INSERT INTO loyalty_levels (level_name, min_deliveries, max_deliveries, discount_percentage, free_cancellations, penalty_percentage) VALUES
('Plata', 0, 99, 5.00, 0, 100.00),
('Oro', 100, 299, 8.00, 0, 50.00),
('Diamante', 300, NULL, 12.00, 5, 50.00);

-- System Configuration
INSERT INTO system_config (config_key, config_value, description) VALUES
('support_email', 'soporte@sie.com.gt', 'Email de contacto para soporte'),
('support_phone', '2360-0000', 'Telefono de contacto para soporte'),
('jwt_expiration_minutes', '60', 'Duracion del access token en minutos'),
('refresh_token_days', '30', 'Duracion del refresh token en dias'),
('max_login_attempts', '5', 'Intentos maximos de login antes de bloqueo'),
('lockout_duration_minutes', '30', 'Duracion del bloqueo de cuenta en minutos');

-- Branches
INSERT INTO branches (branch_code, branch_name, address, phone, email, city, state) VALUES
('SUC001', 'Sucursal Central', 'Avenida Reforma 15-45 Zona 10', '23660000', 'central@sie.com.gt', 'Guatemala', 'Guatemala'),
('SUC002', 'Sucursal Zona 4', 'Septima Avenida 5-20 Zona 4', '23661111', 'zona4@sie.com.gt', 'Guatemala', 'Guatemala'),
('SUC003', 'Sucursal Mixco', 'Boulevard San Cristobal 10-50', '24850000', 'mixco@sie.com.gt', 'Mixco', 'Guatemala'),
('SUC004', 'Sucursal Villa Nueva', 'Calzada Aguilar Batres Kilometro 15', '66300000', 'villanueva@sie.com.gt', 'Villa Nueva', 'Guatemala'),
('SUC005', 'Sucursal Carretera Salvador', 'Kilometro 9 Carretera al Salvador', '66310000', 'salvador@sie.com.gt', 'Santa Catarina Pinula', 'Guatemala');

-- =====================================================
-- INSERT USERS
-- Password for all: Password123$
-- =====================================================

-- Administrators (Role ID = 1)
INSERT INTO users (role_id, email, password_hash, first_name, last_name, phone, address, national_id, two_factor_enabled) VALUES
(1, 'dmaldonado@cari.net', '$2a$12$bJVP7ttQby16rb6CEGlYq.lhAe1XXDxB/ryDHAuuZf9oDjZv4wgKq', 'Carlos Eduardo', 'Martinez Lopez', '55123456', 'Primera Calle 2-34 Zona 1', '2987654321001', TRUE),
(1, 'admin.secundario@sie.com.gt', '$2a$12$bJVP7ttQby16rb6CEGlYq.lhAe1XXDxB/ryDHAuuZf9oDjZv4wgKq', 'Maria Isabel', 'Rodriguez Perez', '55234567', 'Segunda Avenida 3-45 Zona 2', '2987654321002', FALSE);

-- Coordinators (Role ID = 2)
INSERT INTO users (role_id, email, password_hash, first_name, last_name, phone, address, national_id) VALUES
(2, 'coordinador.zona1@sie.com.gt', '$2a$12$bJVP7ttQby16rb6CEGlYq.lhAe1XXDxB/ryDHAuuZf9oDjZv4wgKq', 'Juan Pablo', 'Garcia Mendez', '55345678', 'Tercera Calle 4-56 Zona 3', '2987654321003'),
(2, 'coordinador.zona2@sie.com.gt', '$2a$12$bJVP7ttQby16rb6CEGlYq.lhAe1XXDxB/ryDHAuuZf9oDjZv4wgKq', 'Ana Lucia', 'Hernandez Ruiz', '55456789', 'Cuarta Avenida 5-67 Zona 4', '2987654321004'),
(2, 'coordinador.zona3@sie.com.gt', '$2a$12$bJVP7ttQby16rb6CEGlYq.lhAe1XXDxB/ryDHAuuZf9oDjZv4wgKq', 'Roberto Antonio', 'Flores Castillo', '55567890', 'Quinta Calle 6-78 Zona 5', '2987654321005');

-- Couriers/Delivery Personnel (Role ID = 3)
INSERT INTO users (role_id, email, password_hash, first_name, last_name, phone, address, national_id) VALUES
(3, 'repartidor001@sie.com.gt', '$2a$12$bJVP7ttQby16rb6CEGlYq.lhAe1XXDxB/ryDHAuuZf9oDjZv4wgKq', 'Pedro Antonio', 'Gonzalez Ramirez', '56123456', 'Sexta Avenida 7-89 Zona 6', '2987654321006'),
(3, 'repartidor002@sie.com.gt', '$2a$12$bJVP7ttQby16rb6CEGlYq.lhAe1XXDxB/ryDHAuuZf9oDjZv4wgKq', 'Luis Fernando', 'Morales Cruz', '56234567', 'Septima Calle 8-90 Zona 7', '2987654321007'),
(3, 'repartidor003@sie.com.gt', '$2a$12$bJVP7ttQby16rb6CEGlYq.lhAe1XXDxB/ryDHAuuZf9oDjZv4wgKq', 'Jose Manuel', 'Diaz Flores', '56345678', 'Octava Avenida 9-01 Zona 8', '2987654321008'),
(3, 'repartidor004@sie.com.gt', '$2a$12$bJVP7ttQby16rb6CEGlYq.lhAe1XXDxB/ryDHAuuZf9oDjZv4wgKq', 'Roberto Carlos', 'Vasquez Soto', '56456789', 'Novena Calle 10-12 Zona 9', '2987654321009'),
(3, 'repartidor005@sie.com.gt', '$2a$12$bJVP7ttQby16rb6CEGlYq.lhAe1XXDxB/ryDHAuuZf9oDjZv4wgKq', 'Miguel Angel', 'Perez Jimenez', '56567890', 'Decima Avenida 11-23 Zona 10', '2987654321010');

-- Business Users (Role ID = 4)
INSERT INTO users (role_id, email, password_hash, first_name, last_name, phone, address, national_id) VALUES
(4, 'tienda.electronica@gmail.com', '$2a$12$bJVP7ttQby16rb6CEGlYq.lhAe1XXDxB/ryDHAuuZf9oDjZv4wgKq', 'Fernando Jose', 'Castillo Mejia', '57123456', 'Once Calle 12-34 Zona 11', '2987654321011'),
(4, 'boutique.moda@gmail.com', '$2a$12$bJVP7ttQby16rb6CEGlYq.lhAe1XXDxB/ryDHAuuZf9oDjZv4wgKq', 'Patricia Maria', 'Alvarez Torres', '57234567', 'Doce Avenida 13-45 Zona 12', '2987654321012'),
(4, 'libreria.nacional@gmail.com', '$2a$12$bJVP7ttQby16rb6CEGlYq.lhAe1XXDxB/ryDHAuuZf9oDjZv4wgKq', 'Ricardo Alberto', 'Mendez Silva', '57345678', 'Trece Calle 14-56 Zona 13', '2987654321013'),
(4, 'farmacia.salud@gmail.com', '$2a$12$bJVP7ttQby16rb6CEGlYq.lhAe1XXDxB/ryDHAuuZf9oDjZv4wgKq', 'Andrea Sofia', 'Lopez Gutierrez', '57456789', 'Catorce Avenida 15-67 Zona 14', '2987654321014');

-- Customer Users (Role ID = 5)
INSERT INTO users (role_id, email, password_hash, first_name, last_name, phone, address, national_id) VALUES
(5, 'cliente001@gmail.com', '$2a$12$bJVP7ttQby16rb6CEGlYq.lhAe1XXDxB/ryDHAuuZf9oDjZv4wgKq', 'Sofia Alejandra', 'Gutierrez Luna', '58123456', 'Quince Calle 16-78 Zona 15', '2987654321015'),
(5, 'cliente002@gmail.com', '$2a$12$bJVP7ttQby16rb6CEGlYq.lhAe1XXDxB/ryDHAuuZf9oDjZv4wgKq', 'Diego Alejandro', 'Ramirez Soto', '58234567', 'Dieciseis Avenida 17-89 Zona 16', '2987654321016'),
(5, 'cliente003@gmail.com', '$2a$12$bJVP7ttQby16rb6CEGlYq.lhAe1XXDxB/ryDHAuuZf9oDjZv4wgKq', 'Valentina Maria', 'Castro Herrera', '58345678', 'Diecisiete Calle 18-90 Zona 17', '2987654321017');

-- =====================================================
-- INSERT BUSINESSES
-- =====================================================

INSERT INTO businesses (user_id, current_level_id, tax_id, business_name, legal_name, tax_address, business_phone, business_email, support_contact, affiliation_date) VALUES
(11, 1, '123456-7', 'Electronica Moderna', 'Electronica Moderna Sociedad Anonima', 'Centro Comercial Plaza Mayor Local 45', '23334444', 'ventas@electronicamoderna.gt', 'soporte@electronicamoderna.gt', '2024-01-15'),
(12, 2, '234567-8', 'Boutique Fashion', 'Moda y Estilo Sociedad Anonima', 'Centro Comercial Oakland Mall Local 23', '23335555', 'info@boutiquefashion.gt', 'ayuda@boutiquefashion.gt', '2024-02-20'),
(13, 1, '345678-9', 'Libreria El Saber', 'Libros y Cultura Sociedad Anonima', 'Sexta Avenida 12-45 Zona 1', '23336666', 'ventas@libreriasaber.gt', 'atencion@libreriasaber.gt', '2024-03-10'),
(14, 3, '456789-0', 'Farmacia Salud Total', 'Medicamentos y Salud Sociedad Anonima', 'Torre Medica Zona 10 Nivel 1', '23337777', 'pedidos@farmaciasalud.gt', 'servicio@farmaciasalud.gt', '2024-01-05');

-- =====================================================
-- INSERT CONTRACTS
-- =====================================================

INSERT INTO contracts (user_id, admin_id, contract_type_id, base_salary, commission_percentage, start_date, end_date, active, observations) VALUES
(6, 1, 2, 3000.00, 30.00, '2024-01-01', NULL, TRUE, 'Contrato permanente con comisiones'),
(7, 1, 1, 2500.00, 35.00, '2024-02-01', '2025-01-31', TRUE, 'Contrato temporal un año'),
(8, 1, 2, 3000.00, 30.00, '2024-03-01', NULL, TRUE, 'Contrato permanente'),
(9, 2, 3, NULL, 40.00, '2024-04-01', NULL, TRUE, 'Solo comisiones por hora'),
(10, 2, 1, 2800.00, 32.00, '2024-05-01', '2024-12-31', TRUE, 'Contrato temporal');

-- =====================================================
-- INSERT COURIER AVAILABILITY
-- =====================================================

-- Courier 1 (ID=6): Monday to Friday, 8:00 to 18:00
INSERT INTO courier_availability (courier_id, day_of_week, start_time, end_time) VALUES
(6, 1, '08:00:00', '18:00:00'),
(6, 2, '08:00:00', '18:00:00'),
(6, 3, '08:00:00', '18:00:00'),
(6, 4, '08:00:00', '18:00:00'),
(6, 5, '08:00:00', '18:00:00');

-- Courier 2 (ID=7): Monday to Saturday, 07:00 to 15:00
INSERT INTO courier_availability (courier_id, day_of_week, start_time, end_time) VALUES
(7, 1, '07:00:00', '15:00:00'),
(7, 2, '07:00:00', '15:00:00'),
(7, 3, '07:00:00', '15:00:00'),
(7, 4, '07:00:00', '15:00:00'),
(7, 5, '07:00:00', '15:00:00'),
(7, 6, '07:00:00', '12:00:00');

-- Courier 3 (ID=8): Tuesday to Saturday, 12:00 to 20:00
INSERT INTO courier_availability (courier_id, day_of_week, start_time, end_time) VALUES
(8, 2, '12:00:00', '20:00:00'),
(8, 3, '12:00:00', '20:00:00'),
(8, 4, '12:00:00', '20:00:00'),
(8, 5, '12:00:00', '20:00:00'),
(8, 6, '12:00:00', '20:00:00');

-- =====================================================
-- INSERT SAMPLE TRACKING GUIDES
-- =====================================================

-- Guide numbers will be auto-generated: 202400000001, 202400000002, etc.
INSERT INTO tracking_guides (business_id, origin_branch_id, courier_id, coordinator_id, current_state_id, base_price, courier_commission, recipient_name, recipient_phone, recipient_address, recipient_city, recipient_state, observations, assignment_accepted, assignment_accepted_at) VALUES
(1, 1, 6, 3, 5, 45.00, 13.50, 'Maria Fernanda Lopez', '55881122', 'Quinta Calle 23-45 Zona 5', 'Guatemala', 'Guatemala', 'Entregar en horario matutino', TRUE, '2024-09-01 09:15:00'),
(2, 2, 7, 3, 4, 50.00, 17.50, 'Carlos Roberto Mendez', '55882233', 'Sexta Avenida 34-56 Zona 6', 'Guatemala', 'Guatemala', 'Llamar antes de entregar', TRUE, '2024-09-02 10:30:00'),
(3, 1, 8, 4, 3, 40.00, 12.00, 'Ana Lucia Garcia', '55883344', 'Septima Calle 45-67 Zona 7', 'Guatemala', 'Guatemala', 'Paquete fragil', TRUE, '2024-09-02 14:00:00'),
(4, 3, 9, 4, 8, 60.00, 24.00, 'Pedro Jose Martinez', '55884455', 'Boulevard Principal 10-20 Colonia Vista Hermosa', 'Mixco', 'Guatemala', 'Medicamento urgente', TRUE, '2024-09-03 08:45:00'),
(1, 1, 10, 5, 2, 35.00, 11.20, 'Luisa Fernanda Perez', '55885566', 'Calzada Roosevelt 25-30', 'Guatemala', 'Guatemala', 'Oficina segundo nivel', FALSE, NULL),
(2, 2, NULL, 3, 1, 45.00, NULL, 'Jorge Luis Castillo', '55886677', 'Primera Avenida 10-20 Zona 1', 'Guatemala', 'Guatemala', 'Entregar por la tarde', FALSE, NULL),
(3, 3, 6, 4, 6, 55.00, 16.50, 'Carmen Sofia Ruiz', '55887788', 'Decima Calle 5-15 Zona 10', 'Guatemala', 'Guatemala', 'Cliente cancelo pedido', TRUE, '2024-09-03 11:00:00');

-- =====================================================
-- INSERT SAMPLE STATE HISTORY
-- =====================================================

INSERT INTO state_history (guide_id, state_id, user_id, observations) VALUES
-- Guide 1: Complete delivery cycle
(1, 1, 11, 'Guia creada por comercio'),
(1, 2, 3, 'Asignada a repartidor'),
(1, 3, 6, 'Paquete recogido en sucursal'),
(1, 4, 6, 'En camino al destino'),
(1, 5, 6, 'Entregado exitosamente'),
-- Guide 2: In route
(2, 1, 12, 'Guia creada por comercio'),
(2, 2, 3, 'Asignada a repartidor'),
(2, 3, 7, 'Paquete recogido'),
(2, 4, 7, 'En ruta de entrega'),
-- Guide 3: Picked up
(3, 1, 13, 'Guia creada por comercio'),
(3, 2, 4, 'Asignada a repartidor'),
(3, 3, 8, 'Paquete recogido'),
-- Guide 4: Incident reported
(4, 1, 14, 'Guia creada por comercio'),
(4, 2, 4, 'Asignada a repartidor'),
(4, 3, 9, 'Paquete recogido'),
(4, 8, 9, 'Direccion no encontrada'),
-- Guide 5: Assigned
(5, 1, 11, 'Guia creada por comercio'),
(5, 2, 5, 'Asignada a repartidor, pendiente aceptacion'),
-- Guide 7: Cancelled
(7, 1, 13, 'Guia creada por comercio'),
(7, 2, 4, 'Asignada a repartidor'),
(7, 6, 13, 'Cancelado por el comercio');

-- =====================================================
-- INSERT DELIVERY INCIDENTS
-- =====================================================

INSERT INTO delivery_incidents (guide_id, incident_type_id, reported_by_user_id, description, resolution, resolved, resolved_at, resolved_by_user_id) VALUES
(4, 2, 9, 'La direccion Boulevard Principal 10-20 no existe en la colonia indicada', 'Se contacto al cliente y proporciono direccion correcta', TRUE, '2024-09-03 15:30:00', 4);

-- =====================================================
-- INSERT DELIVERY EVIDENCE
-- =====================================================

INSERT INTO delivery_evidence (guide_id, evidence_type_id, file_url, notes) VALUES
(1, 1, '/uploads/evidence/2024/09/guide1_photo.jpg', 'Entregado en puerta principal'),
(1, 2, '/uploads/evidence/2024/09/guide1_signature.png', 'Firmado por Maria F. Lopez'),
(1, 3, NULL, 'Recibido en perfecto estado a las 11:30 AM');

-- =====================================================
-- INSERT CANCELLATIONS
-- =====================================================

INSERT INTO cancellations (guide_id, cancelled_by_user_id, cancellation_type_id, reason, penalty_amount, courier_commission) VALUES
(7, 13, 1, 'Cliente solicito cancelacion del pedido antes de envio', 0.00, 0.00);

-- =====================================================
-- INSERT NOTIFICATIONS
-- =====================================================

INSERT INTO notifications (guide_id, notification_type_id, recipient_email, recipient_phone, message, sent, sent_at) VALUES
(1, 1, NULL, '55881122', 'Su paquete 202400000001 esta en camino hacia su direccion', TRUE, '2024-09-01 10:45:00'),
(1, 3, NULL, '55881122', 'Su paquete 202400000001 ha sido entregado exitosamente', TRUE, '2024-09-01 11:30:00'),
(2, 1, NULL, '55882233', 'Su paquete 202400000002 esta en camino hacia su direccion', TRUE, '2024-09-02 11:00:00');

-- =====================================================
-- INSERT SAMPLE MONTHLY DISCOUNTS
-- =====================================================

INSERT INTO monthly_discounts (business_id, month, year, total_deliveries, applied_level_id, total_before_discount, discount_percentage, discount_amount, total_after_discount) VALUES
(1, 8, 2024, 45, 1, 2025.00, 5.00, 101.25, 1923.75),
(2, 8, 2024, 150, 2, 7500.00, 8.00, 600.00, 6900.00),
(3, 8, 2024, 80, 1, 3200.00, 5.00, 160.00, 3040.00),
(4, 8, 2024, 350, 3, 21000.00, 12.00, 2520.00, 18480.00);

-- =====================================================
-- INSERT SAMPLE SETTLEMENTS
-- =====================================================

INSERT INTO courier_settlements (courier_id, status_id, period_start, period_end, total_deliveries, total_commissions, total_penalties, net_total, payment_date) VALUES
(6, 3, '2024-08-01', '2024-08-31', 120, 1620.00, 0.00, 1620.00, '2024-09-05 10:00:00'),
(7, 2, '2024-08-01', '2024-08-31', 95, 1662.50, 25.00, 1637.50, NULL),
(8, 1, '2024-09-01', '2024-09-30', 15, 225.00, 0.00, 225.00, NULL);

-- =====================================================
-- INSERT SAMPLE SETTLEMENT DETAILS
-- =====================================================

INSERT INTO settlement_details (settlement_id, guide_id, transaction_type_id, amount, transaction_date) VALUES
(1, 1, 1, 13.50, '2024-08-15 11:30:00'),
(2, 2, 1, 17.50, '2024-08-20 14:45:00'),
(3, 3, 1, 12.00, '2024-09-02 16:00:00');

-- =====================================================
-- INSERT SAMPLE REFRESH TOKENS
-- =====================================================

INSERT INTO refresh_tokens (user_id, token_hash, expires_at, revoked, ip_address, user_agent) VALUES
(1, SHA2('sample_refresh_token_admin_1', 256), DATE_ADD(NOW(), INTERVAL 30 DAY), FALSE, '192.168.1.100', 'Mozilla/5.0 Windows NT 10.0'),
(11, SHA2('sample_refresh_token_business_1', 256), DATE_ADD(NOW(), INTERVAL 30 DAY), FALSE, '192.168.1.101', 'Mozilla/5.0 Macintosh Intel Mac OS X'),
(6, SHA2('sample_refresh_token_courier_1', 256), DATE_ADD(NOW(), INTERVAL 30 DAY), FALSE, '192.168.1.102', 'Mozilla/5.0 X11 Linux x86_64');

-- =====================================================
-- VERIFY DATA INTEGRITY
-- =====================================================

SELECT 'Roles:' as Tabla, COUNT(*) as Total FROM roles
UNION ALL
SELECT 'Usuarios:', COUNT(*) FROM users
UNION ALL
SELECT 'Comercios:', COUNT(*) FROM businesses
UNION ALL
SELECT 'Contratos:', COUNT(*) FROM contracts
UNION ALL
SELECT 'Guias:', COUNT(*) FROM tracking_guides
UNION ALL
SELECT 'Sucursales:', COUNT(*) FROM branches
UNION ALL
SELECT 'Estados:', COUNT(*) FROM tracking_states
UNION ALL
SELECT 'Incidencias:', COUNT(*) FROM delivery_incidents
UNION ALL
SELECT 'Notificaciones:', COUNT(*) FROM notifications
UNION ALL
SELECT 'Tokens Activos:', COUNT(*) FROM refresh_tokens WHERE revoked = FALSE;