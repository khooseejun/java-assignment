package FitnessClubSystem;

import java.util.ArrayList;
import java.util.Scanner;

/*
 * Equipment Class
 * ---------------------------------------------------
 * Represents gym equipment.
 * 
 * Used by Admin to update status.
 */

/**
 * Equipment Class
 * ---------------------------------------------------
 * 负责人: Low Kar Wai
 * 任务: 提供器材的基础属性，并让 Admin 能够修改其维护状态。
 */

// (equipment -> admin // 这个part是独立的，done了给admin连接 )
// （意思是说弄了那些CRUD，让admin可以直接调用这些method去处理equipment)
public class Equipment {

    private String equipmentId;
    private String name;
    private String status; // 例如: "Available", "Under Maintenance", "Broken"

    public Equipment(String equipmentId, String name, String status) {
        this.equipmentId = equipmentId;
        this.name = name;
        this.status = status;
    }

    // --- 核心方法 ---
    public static String normalizeStatus(String raw) {
        if (raw == null) {
            return null;
        }
        String s = raw.trim();
        if (s.equalsIgnoreCase("Available")) {
            return "Available";
        }
        if (s.equalsIgnoreCase("Under Maintenance")) {
            return "Under Maintenance";
        }
        if (s.equalsIgnoreCase("Broken")) {
            return "Broken";
        }
        return null;
    }

    /**
     * 更新状态
     * 作用: Admin 发现器材坏了，调用此方法改为 "Under Maintenance"
     */
    public void updateStatus(String newStatus) {
        String normalized = normalizeStatus(newStatus);
        if (normalized != null) {
            this.status = normalized;
            System.out.println("Equipment " + equipmentId + " status is now: " + normalized);
        } else {
            System.out.println("Invalid status. Use: Available, Under Maintenance, or Broken");
        }
    }

    /**
     * 格式化展示
     * 作用: 让 Admin 查看器材列表时，能看到整齐的输出
     */
    public void displayEquipment() {
        System.out.printf("  %-8s | %-25s | %-15s\n", equipmentId, name, status);
    }

    public static void displayEquipmentList(ArrayList<Equipment> equipments) {
        System.out.println("--- Equipment Status ---");
        if (equipments == null || equipments.isEmpty()) {
            System.out.println("No equipment is found.");
            return;
        }
        System.out.printf("  %-8s | %-25s | %-15s\n", "ID", "Name", "Status");
        System.out.println("  --------+---------------------------+----------------");
        for (Equipment e : equipments) {
            e.displayEquipment();
        }
    }

    private static String promptStatusChoice(Scanner input) {
        String newStatus = null;
        while (newStatus == null) {
            System.out.println("Set status:");
            System.out.println("  1 - Available");
            System.out.println("  2 - Under Maintenance");
            System.out.println("  3 - Broken");
            System.out.print("Enter choice (1-3): ");
            String statusPick = input.next().trim();
            switch (statusPick) {
                case "1":
                    newStatus = "Available";
                    break;
                case "2":
                    newStatus = "Under Maintenance";
                    break;
                case "3":
                    newStatus = "Broken";
                    break;
                default:
                    System.out.println("Invalid choice. Use 1, 2, or 3.");
            }
        }
        return newStatus;
    }

    public static void updateEquipmentStatusOnly(ArrayList<Equipment> equipments, Scanner input) {
        if (equipments.isEmpty()) {
            System.out.println("No equipment to update.");
            return;
        }
        System.out.print("Enter Equipment ID: ");
        String id = input.next().trim();
        String newStatus = promptStatusChoice(input);

        for (Equipment e : equipments) {
            if (e.getEquipmentId().equalsIgnoreCase(id)) {
                e.updateStatus(newStatus);
                return;
            }
        }
        System.out.println("Equipment ID not found.");
    }

    public static void addNewEquipment(ArrayList<Equipment> equipments, Scanner input) {
        System.out.print("New Equipment ID: ");
        String id = input.next().trim();
        for (Equipment e : equipments) {
            if (e.getEquipmentId().equalsIgnoreCase(id)) {
                System.out.println("That ID already exists.");
                return;
            }
        }
        input.nextLine();
        System.out.print("Equipment name: ");
        String name = input.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }
        String newStatus = promptStatusChoice(input);
        equipments.add(new Equipment(id, name, newStatus));
        System.out.println("Equipment added.");
    }

    public static void deleteEquipment(ArrayList<Equipment> equipments, Scanner input) {
        if (equipments.isEmpty()) {
            System.out.println("No equipment to delete.");
            return;
        }

        System.out.print("Enter Equipment ID to delete: ");
        String id = input.next().trim();

        for (int i = equipments.size() - 1; i >= 0; i--) {
            if (equipments.get(i).getEquipmentId().equalsIgnoreCase(id)) {
                equipments.remove(i);
                System.out.println("Equipment removed.");
                return;
            }
        }
    }


    // --- Getters (For use by Admin and FileManager) ---
    public String getEquipmentId() { return equipmentId; }
    public String getName() {
         return name; }
    public String getStatus() { return status; }
    
    // --- Formatting string used for file saving ---
    public String toFileString() {
        return equipmentId + "|" + name + "|" + status;
    }


}
