package com.blueberryjoy.novel.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    @Autowired
    Mailtrap mailtrap;
    @Autowired
    MailCount mailCount;

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
        mailCount.increment();
    }

    public void sendResetPasswordMail(String email, String username, String link) {
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
                                                   Click the button below to reset your password:
                                               </td>
                                           </tr>
                
                                           <tr>
                                               <td align="center" style="padding:30px 0;">
                                                   <a href="%s"
                                                      style="background:#4f46e5;color:#ffffff;text-decoration:none;padding:14px 28px;border-radius:6px;display:inline-block;font-size:16px;">
                                                       Reset Password
                                                   </a>
                                               </td>
                                           </tr>
                
                                           <tr>
                                               <td style="color:#555555;font-size:14px;line-height:1.6;">
                                                   If the button doesn’t work, copy and paste this link:
                                                   <br>
                                                   <a href="%s" style="color:#4f46e5;word-break:break-all;">
                                                       %s
                                                   </a>
                                                   <br><br>
                                                   This link will expire in 10 minutes.
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
                """.formatted(username, link, link, link);
        mailtrap.sendEmail(email, "Reset Your Password", body, "reset_password.html");
        mailCount.increment();
    }

    public void sendAccountDeletedMail(String email, String username, String reason) {
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
                                            <h1 style="margin:0;color:#333333;">Account Deleted</h1>
                                        </td>
                                    </tr>
                
                                    <tr>
                                        <td style="padding-top:30px;color:#555555;font-size:16px;line-height:1.6;">
                                            Hi %s,
                                            <br><br>
                                            Your Novels account has been deleted.
                                            <br><br>
                                            <strong>Reason:</strong>
                                            <br>
                                            %s
                                            <br><br>
                                            Your account and associated content are no longer available.
                                            If you believe this action was taken in error, please contact the Novels administration team.
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
                """.formatted(username, reason);
        mailtrap.sendEmail(email, "Your Novels Account Has Been Deleted", body, "account-deleted.html");
        mailCount.increment();
    }
}
