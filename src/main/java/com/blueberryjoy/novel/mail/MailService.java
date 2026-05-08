package com.blueberryjoy.novel.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    @Autowired
    Mailtrap mailtrap;

    public void sendWelcomeMail(String email, String username) {
        String body = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                </head>
                <body style="margin:0;padding:0;background-color:#f4f4f4;font-family:Arial,sans-serif;">
                    <table width="100%%" cellpadding="0" cellspacing="0">
                        <tr>
                            <td align="center" style="padding:40px 0;">
                                <table width="600" cellpadding="0" cellspacing="0" style="background:#ffffff;border-radius:8px;padding:40px;">
                                    <tr>
                                        <td align="center">
                                            <h1 style="margin:0;color:#333333;">Welcome to Novels</h1>
                                        </td>
                                    </tr>
                
                                    <tr>
                                        <td style="padding-top:30px;color:#555555;font-size:16px;line-height:1.6;">
                                            Hi %s,
                                            <br><br>
                                            Welcome to Novels! We're excited to have you join our community of readers and writers.
                                            <br><br>
                                            You can now explore novels, save your favorites, and continue reading anytime.
                                        </td>
                                    </tr>
                
                                    <tr>
                                        <td style="padding-top:40px;color:#999999;font-size:14px;">
                                            — The Novels Team
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(username);
        mailtrap.sendEmail(email, "Welcome to Novels", body, "welcome.html");
    }

    public void sendResetPasswordMail(String email, String username, String code) {
        String body = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                </head>
                <body style="margin:0;padding:0;background-color:#f4f4f4;font-family:Arial,sans-serif;">
                    <table width="100%%" cellpadding="0" cellspacing="0">
                        <tr>
                            <td align="center" style="padding:40px 0;">
                                <table width="600" cellpadding="0" cellspacing="0" style="background:#ffffff;border-radius:8px;padding:40px;">
                                    <tr>
                                        <td align="center">
                                            <h1 style="margin:0;color:#333333;">Reset Password</h1>
                                        </td>
                                    </tr>
                
                                    <tr>
                                        <td style="padding-top:30px;color:#555555;font-size:16px;line-height:1.6;">
                                            Hi %s,
                                            <br><br>
                                            We received a request to reset your password for your Novels account.
                                            <br><br>
                                            Your password reset code is:
                                        </td>
                                    </tr>
                
                                    <tr>
                                        <td align="center" style="padding:30px 0;">
                                            <div style="display:inline-block;background:#f3f4f6;padding:16px 32px;border-radius:8px;font-size:32px;font-weight:bold;letter-spacing:6px;color:#111827;">
                                                %s
                                            </div>
                                        </td>
                                    </tr>
                
                                    <tr>
                                        <td style="color:#555555;font-size:16px;line-height:1.6;">
                                            This code will expire in 10 minutes.
                                            <br><br>
                                            If you did not request a password reset, you can safely ignore this email.
                                        </td>
                                    </tr>
                
                                    <tr>
                                        <td style="padding-top:40px;color:#999999;font-size:14px;">
                                            — The Novels Team
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(username, code);
        mailtrap.sendEmail(email, "Reset Your Password", body, "reset_password.html");
    }
}
