package com.example.webshopcosmetics.controller;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.webshopcosmetics.model.Product;
import com.example.webshopcosmetics.repository.ProductRepository;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {
    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/ask")
    public ResponseEntity<String> getAnswer(@RequestParam String question) {
        String lowerCaseQuestion = question.toLowerCase();

        // 1️⃣ Nhận diện lời chào
        if (lowerCaseQuestion.contains("chào") || lowerCaseQuestion.contains("hello") || lowerCaseQuestion.contains("hi")) {
            return ResponseEntity.ok("Xin chào! Bạn cần tư vấn sản phẩm gì ạ? 😊");
        }

        // 2️⃣ Nhận diện câu hỏi về màu sắc
        if (lowerCaseQuestion.contains("màu")) {
            String color = extractColor(lowerCaseQuestion); // Tách màu từ câu hỏi
            if (color != null) {
                List<Product> products = productRepository.findByDescriptionContainingIgnoreCase(color);
                if (!products.isEmpty()) {
                    return ResponseEntity.ok(formatProductList(products));
                } else {
                    return ResponseEntity.ok("Xin lỗi, hiện tại shop chưa có sản phẩm màu " + color + " 😢.");
                }
            }
        }

        // 3️⃣ Nhận diện câu hỏi về giá sản phẩm
        if (lowerCaseQuestion.contains("giá") || lowerCaseQuestion.contains("dưới")) {
            Integer price = extractPrice(lowerCaseQuestion); // Tách giá từ câu hỏi
            if (price != null) {
                List<Product> products = productRepository.findByPriceLessThan(price);
                if (!products.isEmpty()) {
                    return ResponseEntity.ok(formatProductList(products));
                } else {
                    return ResponseEntity.ok("Không tìm thấy sản phẩm nào có giá dưới " + price + "đ.");
                }
            }
        }

        // 4️⃣ Mặc định: Tìm sản phẩm theo tên
        List<Product> products = productRepository.findByNameContainingIgnoreCase(question);
        if (!products.isEmpty()) {
            return ResponseEntity.ok(formatProductList(products));
        }

        return ResponseEntity.ok("Xin lỗi, tôi không hiểu câu hỏi. Bạn có thể hỏi về màu sắc, giá tiền hoặc tên sản phẩm nhé!");
    }

    // Hàm tách màu sắc từ câu hỏi
    private String extractColor(String question) {
        List<String> colors = Arrays.asList("đỏ", "xanh", "vàng", "hồng", "nâu", "đen", "trắng", "tím");
        for (String color : colors) {
            if (question.contains(color)) {
                return color;
            }
        }
        return null;
    }

    // Hàm tách giá từ câu hỏi
    private Integer extractPrice(String question) {
        Pattern pattern = Pattern.compile("(\\d+)[kK]?");
        Matcher matcher = pattern.matcher(question);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1)) * 1000;
        }
        return null;
    }

    // Hàm định dạng danh sách sản phẩm trả về
    private String formatProductList(List<Product> products) {
        StringBuilder response = new StringBuilder("Tôi tìm thấy các sản phẩm sau:\n");
        for (Product p : products) {
            response.append("- ").append(p.getName())
                    .append(" (Giá: ").append(p.getPrice()).append("đ)\n");
        }
        return response.toString();
    }
}

