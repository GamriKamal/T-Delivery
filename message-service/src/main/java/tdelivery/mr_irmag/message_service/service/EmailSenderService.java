package tdelivery.mr_irmag.order_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import tdelivery.mr_irmag.message_service.domain.dto.OrderDTO;
import tdelivery.mr_irmag.message_service.domain.dto.OrderItemDTO;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class EmailSenderService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, OrderDTO order, int timeOfCooking) {
        String subject = "Ваш заказ принят!";
        String htmlContent = loadHtmlTemplate(order, timeOfCooking);

        try {
            sendHtmlEmail(to, subject, htmlContent);
        } catch (MessagingException e) {
            log.error("Ошибка при отправке письма", e);
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = createMimeMessageHelper(message, to, subject, htmlContent);

        byte[] svgBytes = loadResourceAsBytes("templates/tbank-logo.svg");
        if (svgBytes != null) {
            ByteArrayResource resource = new ByteArrayResource(svgBytes);
            helper.addInline("imageId", resource, "image/svg+xml");
        } else {
            log.warn("SVG resource not found or couldn't be loaded");
        }


        mailSender.send(message);
    }

    private MimeMessageHelper createMimeMessageHelper(MimeMessage message, String to, String subject, String htmlContent) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        return helper;
    }


    private String loadHtmlTemplate(OrderDTO order, int timeOfCooking) {
        InputStream inputStream = loadResourceAsStream("templates/order_confirmation.html");
        StringBuilder htmlBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replace("${name}", order.getName())
                        .replace("${deliveryAddress}", order.getDeliveryAddress())
                        .replace("${comment}", order.getComment() != null ? order.getComment() : "")
                        .replace("${totalAmount}", String.valueOf(order.getTotalAmount()))
                        .replace("${timeOfCooking}", String.valueOf(timeOfCooking));

                if (line.contains("${orderItems}")) {
                    line = line.replace("${orderItems}", createOrderItemsHtml(order.getOrderItems()));
                }

                htmlBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return htmlBuilder.toString();
    }


    private InputStream loadResourceAsStream(String resourcePath) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new RuntimeException(resourcePath + " не найден в ресурсах!");
        }
        return inputStream;
    }

    private byte[] loadResourceAsBytes(String resourcePath) {
        try (InputStream inputStream = loadResourceAsStream(resourcePath);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке ресурса как байтового массива: " + resourcePath, e);
        }
    }

    private String createOrderItemsHtml(List<OrderItemDTO> orderItems) {
        StringBuilder itemsHtml = new StringBuilder("<ul>");

        for (OrderItemDTO item : orderItems) {
            itemsHtml.append("<li>")
                    .append(item.getName())
                    .append(" - ").append(item.getPrice()).append(" тг.")
                    .append(" (x").append(item.getQuantity()).append(")")
                    .append("</li>");
        }

        itemsHtml.append("</ul>");
        return itemsHtml.toString();
    }
}
