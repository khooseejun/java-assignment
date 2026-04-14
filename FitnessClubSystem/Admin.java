package FitnessClubSystem;

import java.util.ArrayList;
import java.util.Scanner;

/*
 * Admin Class
 * ---------------------------------------------------
 * Inherits from User.
 */

/**
 * Admin Class
 * ---------------------------------------------------
 * 负责人: Tang Zhi Hao
 * 任务: 实现管理端逻辑，包括收入统计、设备维护、教练分配等。
 */

// (admin -> 先要有members, equiment, trainer )
public class Admin extends User {

    Scanner input = new Scanner(System.in); 
    public Admin(String userId, String password) {
        super(userId, password);
    }

    @Override
    public void displayMenu() {
        System.out.println("\n=== Admin Menu ===");
        System.out.println("1. Manage Members (View Active/Expired)");
        System.out.println("2. Manage Equipment (Flag Maintenance)"); 
        System.out.println("3. Manage Staff (Assign Trainer to Classes)");
        System.out.println("4. Generate Revenue Report"); 
        System.out.println("5. Logout");
    }

    //zhi hao
    // TODO: 1. Manage meber / 加判定，expired的话就不能成功login // 
    //zhi hao
    public void manageMembers(ArrayList<Member> members){
        
        System.out.println("\n--- Member List ---");

        if(members.isEmpty()){
            System.out.println("No members found.");
            return;
        }

        // jy 
        System.out.printf("%-8s | %-15s | %-10s | %-10s\n", "ID", "Name", "Type", "Status");
        System.out.println("------------------------------------------------------------");
        for (Member m : members) {
            System.out.printf("%-8s | %-15s | %-10s | %-10s\n", 
                m.getMemberID(), 
                m.getName(), 
                m.getMembership().getMembershipName(),
                m.getStatus());
        }

        // 2. 选择目标
        System.out.print("\nEnter Member ID to update (or '0' to go back): ");
        String targetId = input.next();

        if (targetId.equals("0")) return;

        // 3. 查找逻辑
        Member foundMember = null;
        for (Member m : members) {
            if (m.getMemberID().equalsIgnoreCase(targetId)) {
                foundMember = m;
                break;
            }
        }

        if (foundMember == null) {
            System.out.println("Error: Member with ID " + targetId + " not found.");
            return;
        }

        // 4. 状态更新判定 (用循环强制要求输入正确)
        boolean updated = false;
        while (!updated) {
            System.out.println("\nUpdating Status for: " + foundMember.getName());
            System.out.println("Current Status: " + foundMember.getStatus());
            System.out.println("Select New Status: [1] Active  [2] Expired  [3] Cancel");
            System.out.print("Choice: ");
            
            String choice = input.next();
            switch (choice) {
                case "1":
                    foundMember.setStatus("Active");
                    updated = true;
                    System.out.println("Successfully set to Active.");
                    break;
                case "2":
                    foundMember.setStatus("Expired");
                    updated = true;
                    System.out.println("Successfully set to Expired.");
                    break;
                case "3":
                    System.out.println("Update cancelled.");
                    return;
                default:
                    System.out.println("Invalid choice! Please enter 1, 2, or 3.");
            }
        }

        // 5. 重要：提示数据已更改
        System.out.println("Note: Status updated in memory. Data will be finalized on system exit.");

        // jy
    }
    //zhi hao

    // 实现设备管理逻辑
    // 逻辑: 接收一个 Equipment 的 ArrayList，循环打印并允许 Admin 输入 ID 将其设为 "Under Maintenance"
    public void manageEquipment(ArrayList<Equipment> equipments) {
        Scanner input = new Scanner(System.in);
        if (equipments == null) {
            return;
        }

        String sub;
        do {
            Equipment.displayEquipmentList(equipments);
            System.out.println("--- Actions ---");
            System.out.println("1. Update status (maintenance flag)");
            System.out.println("2. Add new equipment");
            System.out.println("3. Delete equipment");
            System.out.println("0. Back to admin menu");
            System.out.print("Enter choice: ");
            sub = input.next().trim();

            switch (sub) {
                case "1":
                    Equipment.updateEquipmentStatusOnly(equipments, input);
                    break;
                case "2":
                    Equipment.addNewEquipment(equipments, input);
                    break;
                case "3":
                    Equipment.deleteEquipment(equipments, input);
                    break;
                case "0":
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (!sub.equals("0"));
    }

    // 实现教练分配逻辑 (这是你们讨论的核心)
    // 逻辑: 接收课程列表和教练列表，通过 ID 匹配来调用 FitnessClass 的 setInstructor 方法
    public void assignClass(ArrayList<FitnessClass> classes, ArrayList<Trainer> trainers) {
        Scanner input = new Scanner(System.in);
        String choice;

        while (true) {
            FitnessClass.printClassDetail(classes, trainers);
            System.out.println("\n=======================");
            System.out.println("1. Create Classes");
            System.out.println("2. Set Schedule");
            System.out.println("3. Assign Trainer");
            System.out.println("4. Delete Classes");
            System.out.println("5. Return to Admin menu");
            System.out.println("=======================");
            System.out.print("Enter choice: ");
            choice = input.next();

            switch (choice) {
                case "1":
                    FitnessClass.createClass(classes);
                    break;
                case "2":
                    FitnessClass.setSchedule(classes);
                    break;
                case "3":
                    FitnessClass.assignTrainerToClass(classes, trainers);
                    break;
                case "4":
                    FitnessClass.deleteClass(classes);
                    break;
                case "5":
                    return;
                default:
                    System.out.println("Invalid choice. Please enter 1-5.");
            }
        }
    }

    /**
     * 已完成: 收入报表生成
     * 注意: 这里还可以扩展统计不同等级会员的人数 (Basic: X, Gold: Y)
     */
    public void generateMonthlyRevenueReport(ArrayList<Member> members) {
        double totalRevenue = 0;
        int goldCount = 0, silverCount = 0, basicCount = 0;

        for (Member m : members) {
            totalRevenue += m.getMembership().getPrice();
            // 进阶逻辑: 统计各等级人数
            String type = m.getMembership().getMembershipName();
            if (type.equalsIgnoreCase("Gold")) {
                goldCount++;}
            else if (type.equalsIgnoreCase("Silver")) {
                silverCount++;}
            else {
                basicCount++;}
        }

        System.out.println("\n===== Monthly Revenue Report =====");
        System.out.println("Total Active Members: " + members.size());
        System.out.println(" - Gold: " + goldCount + " | Silver: " + silverCount + " | Basic: " + basicCount);
        System.out.println("Total Monthly Revenue: RM " + totalRevenue);
        System.out.println("==================================");
    }
    
}
