package com.ayd.sie.shared.infrastructure.notifications;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class EmailTemplateService {

    public String processTemplate(String templateName, Map<String, Object> variables) {
        return switch (templateName.toLowerCase()) {
            case "two-factor-code" -> processTwoFactorTemplate(variables);
            case "welcome" -> processWelcomeTemplate(variables);
            case "password-reset" -> processPasswordResetTemplate(variables);
            case "account-locked" -> processAccountLockedTemplate(variables);
            default -> {
                log.warn("Unknown template: {}", templateName);
                yield "Template not found: " + templateName;
            }
        };
    }

    private String processTwoFactorTemplate(Map<String, Object> variables) {
        String userName = (String) variables.getOrDefault("userName", "Usuario");
        String code = (String) variables.getOrDefault("code", "000000");

        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Código de Verificación - SIE</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f4f4f4;
                            margin: 0;
                            padding: 0;
                        }
                        .container {
                            max-width: 600px;
                            margin: 0 auto;
                            background-color: white;
                            padding: 20px;
                            border-radius: 10px;
                            box-shadow: 0 0 10px rgba(0,0,0,0.1);
                        }
                        .header {
                            text-align: center;
                            color: #2c3e50;
                            border-bottom: 2px solid #3498db;
                            padding-bottom: 20px;
                            margin-bottom: 30px;
                        }
                        .code-container {
                            background-color: #ecf0f1;
                            border: 2px dashed #3498db;
                            border-radius: 10px;
                            padding: 30px;
                            text-align: center;
                            margin: 30px 0;
                        }
                        .verification-code {
                            font-size: 36px;
                            font-weight: bold;
                            color: #2c3e50;
                            letter-spacing: 8px;
                            font-family: 'Courier New', monospace;
                        }
                        .warning {
                            background-color: #fff3cd;
                            border: 1px solid #ffeaa7;
                            border-radius: 5px;
                            padding: 15px;
                            margin: 20px 0;
                            color: #856404;
                        }
                        .footer {
                            text-align: center;
                            color: #7f8c8d;
                            font-size: 12px;
                            border-top: 1px solid #ecf0f1;
                            padding-top: 20px;
                            margin-top: 30px;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🔐 Sistema Integral de Entregas (SIE)</h1>
                            <h2>Código de Verificación</h2>
                        </div>

                        <p>Hola <strong>%s</strong>,</p>

                        <p>Has solicitado acceder a tu cuenta con autenticación de dos factores. Usa el siguiente código para completar tu inicio de sesión:</p>

                        <div class="code-container">
                            <div class="verification-code">%s</div>
                            <p style="margin-top: 15px; color: #7f8c8d;">
                                <small>Este código expira en 5 minutos</small>
                            </p>
                        </div>

                        <div class="warning">
                            <strong>⚠️ Importante:</strong>
                            <ul>
                                <li>Este código es válido por 5 minutos únicamente</li>
                                <li>No compartas este código con nadie</li>
                                <li>Si no solicitaste este código, ignora este mensaje</li>
                            </ul>
                        </div>

                        <p>Si tienes alguna pregunta o necesitas ayuda, contacta a nuestro equipo de soporte.</p>

                        <div class="footer">
                            <p><strong>Sistema Integral de Entregas (SIE)</strong></p>
                            <p>Este es un mensaje automático, por favor no respondas a este correo.</p>
                            <p>&copy; 2025 SIE. Todos los derechos reservados.</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(userName, code);
    }

    private String processWelcomeTemplate(Map<String, Object> variables) {
        String userName = (String) variables.getOrDefault("userName", "Usuario");
        String email = (String) variables.getOrDefault("email", "");
        String temporaryPassword = (String) variables.getOrDefault("temporaryPassword", "");

        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Bienvenido a SIE</title>
                </head>
                <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px;">
                    <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
                        <div style="text-align: center; color: #2c3e50; border-bottom: 2px solid #27ae60; padding-bottom: 20px; margin-bottom: 30px;">
                            <h1>🚚 Bienvenido a SIE</h1>
                            <h2>Sistema Integral de Entregas</h2>
                        </div>

                        <p>Hola <strong>%s</strong>,</p>

                        <p>¡Bienvenido al Sistema Integral de Entregas! Tu cuenta ha sido creada exitosamente.</p>

                        <div style="background-color: #e8f5e8; border: 2px solid #27ae60; border-radius: 10px; padding: 20px; margin: 20px 0;">
                            <h3 style="color: #27ae60; margin-top: 0;">📧 Datos de Acceso:</h3>
                            <p><strong>Email:</strong> %s</p>
                            <p><strong>Contraseña Temporal:</strong> <code style="background-color: #f8f9fa; padding: 5px; border-radius: 3px;">%s</code></p>
                        </div>

                        <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 5px; padding: 15px; margin: 20px 0; color: #856404;">
                            <strong>🔒 Importante:</strong>
                            <ul>
                                <li>Cambia tu contraseña en el primer inicio de sesión</li>
                                <li>Mantén tus credenciales seguras</li>
                                <li>No compartas tu contraseña con nadie</li>
                            </ul>
                        </div>

                        <div style="text-align: center; margin: 30px 0;">
                            <p>¡Comienza a usar el sistema ahora!</p>
                        </div>

                        <div style="text-align: center; color: #7f8c8d; font-size: 12px; border-top: 1px solid #ecf0f1; padding-top: 20px; margin-top: 30px;">
                            <p><strong>Sistema Integral de Entregas (SIE)</strong></p>
                            <p>&copy; 2025 SIE. Todos los derechos reservados.</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(userName, email, temporaryPassword);
    }

    private String processPasswordResetTemplate(Map<String, Object> variables) {
        String userName = (String) variables.getOrDefault("userName", "Usuario");
        String resetToken = (String) variables.getOrDefault("resetToken", "");

        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <title>Restablecer Contraseña - SIE</title>
                </head>
                <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px;">
                    <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px;">
                        <h1 style="color: #e74c3c; text-align: center;">🔑 Restablecer Contraseña</h1>

                        <p>Hola <strong>%s</strong>,</p>

                        <p>Hemos recibido una solicitud para restablecer la contraseña de tu cuenta.</p>

                        <div style="background-color: #fde2e2; border: 2px solid #e74c3c; border-radius: 10px; padding: 20px; margin: 20px 0; text-align: center;">
                            <p><strong>Token de Restablecimiento:</strong></p>
                            <code style="font-size: 18px; background-color: #f8f9fa; padding: 10px; border-radius: 5px; display: inline-block;">%s</code>
                            <p style="margin-top: 15px; color: #7f8c8d;"><small>Este token expira en 15 minutos</small></p>
                        </div>

                        <p>Si no solicitaste este restablecimiento, ignora este mensaje.</p>
                    </div>
                </body>
                </html>
                """
                .formatted(userName, resetToken);
    }

    private String processAccountLockedTemplate(Map<String, Object> variables) {
        String userName = (String) variables.getOrDefault("userName", "Usuario");

        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <title>Cuenta Bloqueada - SIE</title>
                </head>
                <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px;">
                    <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px;">
                        <h1 style="color: #f39c12; text-align: center;">⚠️ Cuenta Temporalmente Bloqueada</h1>

                        <p>Hola <strong>%s</strong>,</p>

                        <p>Tu cuenta ha sido temporalmente bloqueada debido a múltiples intentos de inicio de sesión fallidos.</p>

                        <div style="background-color: #fef9e7; border: 2px solid #f39c12; border-radius: 10px; padding: 20px; margin: 20px 0;">
                            <h3 style="color: #f39c12; margin-top: 0;">🔒 Información del Bloqueo:</h3>
                            <ul>
                                <li>Duración: 30 minutos</li>
                                <li>Motivo: Exceso de intentos fallidos de login</li>
                                <li>Acción: Esperar o contactar soporte</li>
                            </ul>
                        </div>

                        <p>Si crees que esto es un error o necesitas acceso inmediato, contacta a nuestro equipo de soporte.</p>
                    </div>
                </body>
                </html>
                """
                .formatted(userName);
    }
}