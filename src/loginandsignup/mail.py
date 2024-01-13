import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
from email.mime.application import MIMEApplication
import sys
email_add = sys.argv(1)
msg = MIMEMultipart()
msg['From'] = 'hackinghackerhari_bcs26@mepcoeng.ac.in'
msg['To'] = 'email_add'
msg['Subject'] = 'Ticket Booked Successfully'

attachment_path = 'C:\\Users\\Hari\Desktop\\tickets\\ticket_image_20231215_225729.png'
with open(attachment_path, 'rb') as attachment:
    attachment_part = MIMEApplication(attachment.read(), Name=attachment_path)
    attachment_part['Content-Disposition'] = f'attachment; filename={attachment_part["Name"]}'
    msg.attach(attachment_part)

# Connect to the SMTP server
smtp_server = 'smtp.gmail.com'
smtp_port = 587
smtp_username = 'hackinghackerhari_bcs26@mepcoeng.ac.in'

with smtplib.SMTP(smtp_server, smtp_port) as server:
    server.starttls()
    server.login(smtp_username, "Hacker--13052005")
    server.sendmail(msg['From'], msg['To'], msg.as_string())

print('Email sent successfully.')
