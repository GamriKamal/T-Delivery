package tdelivery.mr_irmag.message_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import tdelivery.mr_irmag.message_service.domain.dto.CourierMessageDto;
import tdelivery.mr_irmag.message_service.domain.dto.OrderDTO;
import tdelivery.mr_irmag.message_service.domain.dto.OrderItemDTO;
import tdelivery.mr_irmag.message_service.domain.dto.UserMessageRequestDTO;

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

    public boolean sendPaidStatusMessage(UserMessageRequestDTO request) {
        String subject = "Ваш заказ принят!";
        String htmlContent = loadHtmlTemplate(request.getOrder(), request.getTimeOfCooking());

        try {
            sendHtmlEmail(request.getEmail(), subject, htmlContent);
        } catch (MessagingException e) {
            log.error("Error sending email", e);
            throw new RuntimeException(e);
        }
        return true;
    }

    public void sendShippedStatusMessage(UserMessageRequestDTO request) {
        String subject = "Ваш заказ готов и мы ищем курьера!";
        String htmlContent = loadHtmlTemplateForReadyOrder(request.getOrder());

        try {
            sendHtmlEmail(request.getEmail(), subject, htmlContent);
        } catch (MessagingException e) {
            log.error("Error sending order readiness message", e);
        }
    }

    public void sendCourierPickupMessage(CourierMessageDto request) {
        String subject = "Ваш заказ уже на пути!";
        String htmlContent = loadHtmlTemplateForOrderStatusUpdate(request);

        try {
            sendHtmlEmail(request.getEmail(), subject, htmlContent);
        } catch (MessagingException e) {
            log.error("Error sending courier pickup message", e);
        }
    }

    public boolean sendOrderDeliveredMessage(UserMessageRequestDTO request) {
        String subject = "Ваш заказ доставлен!";
        String htmlContent = loadHtmlTemplateForDelivery();

        try {
            sendHtmlEmail(request.getEmail(), subject, htmlContent);
        } catch (MessagingException e) {
            log.error("Error sending order delivery email", e);
            return false;
        }
        return true;
    }

    public boolean sendCanceledStatusMessage(UserMessageRequestDTO request) {
        String subject = "Ваш заказ отменен";
        String htmlContent = loadHtmlTemplateForCanceledOrder(request.getOrder());

        try {
            sendHtmlEmail(request.getEmail(), subject, htmlContent);
        } catch (MessagingException e) {
            log.error("Error sending canceled order email", e);
            return false;
        }
        return true;
    }


    private String loadHtmlTemplateForOrderStatusUpdate(CourierMessageDto messageRequest) {
        InputStream inputStream = loadResourceAsStream("templates/order_status_update.html");
        StringBuilder htmlBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replace("${timeOfDelivery}", messageRequest.getTimeOfDelivery())
                        .replace("${restaurantAddress}", messageRequest.getRestaurantAddress())
                        .replace("${email}", "tdelivery@gmail.com");

                htmlBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading HTML template for order status update", e);
        }

        return htmlBuilder.toString();
    }

    private String loadHtmlTemplateForDelivery() {
        InputStream inputStream = loadResourceAsStream("templates/order_delivered.html");
        StringBuilder htmlBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                htmlBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading HTML template for order delivery notification", e);
        }

        return htmlBuilder.toString();
    }


    private String loadHtmlTemplateForReadyOrder(OrderDTO order) {
        InputStream inputStream = loadResourceAsStream("templates/order_ready.html");
        StringBuilder htmlBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replace("${name}", order.getName())
                        .replace("${deliveryAddress}", order.getDeliveryAddress())
                        .replace("${totalAmount}", String.valueOf(order.getTotalAmount()));

                htmlBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return htmlBuilder.toString();
    }

    private String loadHtmlTemplateForCanceledOrder(OrderDTO order) {
        InputStream inputStream = loadResourceAsStream("templates/order_canceled.html");
        StringBuilder htmlBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replace("${name}", order.getName())
                        .replace("${deliveryAddress}", order.getDeliveryAddress())
                        .replace("${totalAmount}", String.valueOf(order.getTotalAmount()))
                        .replace("${orderItems}", createOrderItemsHtml(order.getOrderItems()));

                htmlBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading HTML template for canceled order", e);
        }

        return htmlBuilder.toString();
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
            throw new RuntimeException(resourcePath + " not found in the resources!");
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
            throw new RuntimeException("Error loading a resource as a byte array: " + resourcePath, e);
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
