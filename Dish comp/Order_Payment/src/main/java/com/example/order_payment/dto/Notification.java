package com.example.order_payment.dto;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Notification {
    private static final Map<Long, List<Map<String, Object>>> notifications = new ConcurrentHashMap<>();
    public static void addNotification(Long customerId, Map<String, Object> notif) {
        notifications.computeIfAbsent(customerId, k -> new CopyOnWriteArrayList<>()).add(notif);
    }
    public static List<Map<String, Object>> getAndClearNotificationsForCustomer(Long customerId) {
        List<Map<String, Object>> list = notifications.getOrDefault(customerId, new CopyOnWriteArrayList<>());
        notifications.remove(customerId);
        return list;
    }
}
