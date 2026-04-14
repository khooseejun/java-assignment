package FitnessClubSystem;

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/*
 * FitnessClass
 * ---------------------------------------------------
 * Represents gym classes (Yoga, HIIT, Pilates).
 * 
 * Relationship:
 * - AGGREGATION with Member (class contains members)
 */

/**
 * FitnessClass
 * ---------------------------------------------------
 * 负责人: Khoo See Ze
 * 任务: 完善课程属性，实现时间排程，并为 Admin 提供管理接口。
 */

// (fitnessclass -> 被 member booking / admin管理 )
// booking method 先不用弄 

public class FitnessClass {

    private String classId;
    private String className;
    private LocalDate date;
    private String timeSlot;
    private Trainer instructor;
    private int maxCapacity;
    private int currentEnrollment;
    private ArrayList<Member> bookedMembers;
    private static final int GOLD_RESERVED_SLOTS = 2;
    

    public FitnessClass(String classId, String className, int maxCapacity) {
        this.classId = classId;
        this.className = className;
        this.maxCapacity = maxCapacity;
        this.bookedMembers = new ArrayList<>();
        // 初始状态下，instructor 应该是 null，直到 Admin 分配
    }


    // 方法: 分配教练
    public void setInstructor(Trainer trainer) {
        this.instructor = trainer;
    }

    // 方法: 安排时间
    public void setSchedule(LocalDate date, String timeSlot) {
        // Logic: 更新课程日期和时间
        this.date = date;
        this.timeSlot = timeSlot;
    }
    public void setCurrentEnrollment(int currentEnrollment) {
        this.currentEnrollment = currentEnrollment;
    }

    public String getClassName() { return className; }
    public String getClassId() { return classId; }
    public LocalDate getDate() { return date; }
    public String getTimeSlot() { return timeSlot; }
    public Trainer getInstructor() { return instructor; }
    public int getMaxCapacity() { return maxCapacity; }
    public int getCurrentEnrollment() { return currentEnrollment; }
    public ArrayList<Member> getBookedMembers() {return bookedMembers;}

    // 实现“真实布局”的格式化打印
    public void displayClassInfo() {
        String instructorName = (instructor != null) ? instructor.getName() : "TBA";
        String dateStr = (date != null) ? date.toString() : "TBA";
        String timeStr = (timeSlot != null && !timeSlot.isEmpty()) ? timeSlot : "TBA";
        System.out.printf("%s | %s | %s | %s | Trainer: %s | [%d/%d]%n",
                classId, className, dateStr, timeStr, instructorName,
                bookedMembers.size(), maxCapacity);
    }

    // jy
    // 放在 FitnessClass.java
    public static void showMemberSchedule(ArrayList<FitnessClass> classes) {
        System.out.println("\n==========================================================================");
        System.out.println("                         WEEKLY CLASS SCHEDULE                            ");
        System.out.println("==========================================================================");

        if (classes.isEmpty()) {
            System.out.println("No classes scheduled at the moment. Check back later!");
            return;
        }

        System.out.println("+---------+----------------+--------------------+------------+------------+");
        System.out.printf("| %-7s | %-14s | %-18s | %-10s | %-10s |%n", 
            "ID", "Class Name", "Date & Time", "Instructor", "Available");
        System.out.println("+---------+----------------+--------------------+------------+------------+");

        for (FitnessClass c : classes) {
            String schedule = (c.getDate() != null && c.getTimeSlot() != null) 
                              ? c.getDate().toString() + " " + c.getTimeSlot() 
                              : "TBA";
            
            String trainerName = (c.getInstructor() != null) ? c.getInstructor().getName() : "TBA";
            
            // 显示 [已报/满额]
            String availability = c.getCurrentEnrollment() + "/" + c.getMaxCapacity();

            System.out.printf("| %-7s | %-14s | %-18s | %-10s | %-10s |%n",
                c.getClassId(), 
                c.getClassName(), 
                schedule, 
                trainerName, 
                availability);
        }
        System.out.println("+---------+----------------+--------------------+------------+------------+");
    }
    //jy

    public static void printClassDetail(ArrayList<FitnessClass> classes, ArrayList<Trainer> trainers) {
        System.out.println("\n========================================");
        System.out.println("         STAFF ASSIGNMENT MENU          ");
        System.out.println("========================================");
        if (classes.isEmpty()) {
            System.out.println("No fitness classes available.");
            return;
        }

        if (trainers.isEmpty()) {
            System.out.println("No trainers available.");
            return;
        }
        
        System.out.println("--- Available Fitness Classes ---");
        System.out.println("+---------+----------------+--------------------+------------+---------+----------+");
        System.out.printf("| %-7s | %-14s | %-18s | %-10s | %-7s | %-8s |%n", 
            "ClassID", "Class Name", "Schedule", "TrainerID", "Current", "Capacity");
        System.out.println("+---------+----------------+--------------------+------------+---------+----------+");
        for (FitnessClass c : classes) {
            String trainerId = (c.getInstructor() != null) ? c.getInstructor().getUserId() : "pending";
            String schedule = "TBA";
            if (c.getDate() != null && c.getTimeSlot() != null) {
                schedule = c.getDate().toString() + " " + c.getTimeSlot();
            }
            System.out.printf("| %-7s | %-14s | %-18s | %-10s | %-7s | %-8s |%n",
                c.getClassId(), String.format("%-14s", c.getClassName()), 
                String.format("%-18s", schedule), 
                String.format("%-10s", trainerId), 
                String.format("%-7s", c.getCurrentEnrollment()), 
                String.format("%-8s", c.getMaxCapacity()));
        }
        System.out.println("+---------+----------------+--------------------+------------+---------+----------+");

        System.out.println("\n--- Available Trainers ---");
        System.out.println("+-----------+---------------------+");
        System.out.println("| TrainerID | Name                |");
        System.out.println("+-----------+---------------------+");
        for (Trainer t : trainers) {
            System.out.printf("| %-9s | %-19s |%n", t.getUserId(), String.format("%-19s", t.getName()));
        }
        System.out.println("+-----------+---------------------+");
    }

    //jy
    // --- 【修改点 2：增强 enrollMember 方法】 ---
    /**
     * 核心逻辑：
     * 1. 如果完全满了，谁都进不来。
     * 2. 如果进入了“金卡预留区”（即剩余位置 <= 2），只有金卡能进。
     * 3. 其他情况正常进入。
     */
    public boolean enrollMember(Member member) {
        // 1. 先定义预留位数量 (比如最后 2 个位子留给 Gold)
        int currentSize = bookedMembers.size();

        // 2. 检查是否绝对满了
        if (currentSize >= maxCapacity) {
            System.out.println("\n[FAILED] This class is absolutely full!");
            return false;
        }

        // 3. 检查是否进入了“金卡预留区”
        // 逻辑：如果现在报的人数已经达到了 (总容量 - 预留位)，且你不是 Gold
        if (currentSize >= (maxCapacity - GOLD_RESERVED_SLOTS)) {
            if (!member.getMembership().hasPriorityBooking()) {
                System.out.println("\n[RESTRICTED] Only Gold Members can book the last " + GOLD_RESERVED_SLOTS + " priority slots.");
                System.out.println("Please upgrade your membership to Gold for priority access.");
                return false;
            }
        }

        // 4. 通过所有检查，允许加入
        bookedMembers.add(member);
        currentEnrollment = bookedMembers.size(); // 确保这个数字同步更新
        return true;
    }

    // --- 【修改点 3：建议同步修改 isFull】 ---
    // 这个方法可以给 UI 显示用，告诉普通会员这门课“对他而言”是否已满
    public boolean isFullForMember(Member member) {
        boolean isGold = member.getMembership().getMembershipName().equalsIgnoreCase("Gold");
        if (isGold) {
            return bookedMembers.size() >= maxCapacity;
        } else {
            return bookedMembers.size() >= (maxCapacity - GOLD_RESERVED_SLOTS);
        }
    }
    // 确保这个方法在 FileManager 加载时被调用
    public boolean addMember(Member member) {
        if (bookedMembers.size() < maxCapacity) {
            bookedMembers.add(member);
            // 关键：同步更新这个 int 字段，否则打印出来永远是 0
            this.currentEnrollment = bookedMembers.size(); 
            return true;
        }
        return false;
    }
    // 同样，取消时也要同步
    public boolean cancelEnrollment(Member member) {
        if (bookedMembers.remove(member)) {
            this.currentEnrollment = bookedMembers.size(); 
            return true;
        }
        return false;
    }
    //jy

    // let admin use this method
    public static void createClass(ArrayList<FitnessClass> classes) {
        Scanner input = new Scanner(System.in);
        System.out.println("\n--- Create New Class ---");
        System.out.print("Enter Class ID: ");
        String classId = input.next();
        input.nextLine();
        System.out.print("Enter Class Name: ");
        String className = input.nextLine();
        System.out.print("Enter Max Capacity: ");
        int maxCapacity = input.nextInt();

        FitnessClass newClass = new FitnessClass(classId, className, maxCapacity);
        classes.add(newClass);
        System.out.println("Class created successfully.");
    }

    public static void setSchedule(ArrayList<FitnessClass> classes) {
        Scanner input = new Scanner(System.in);
        System.out.println("\n--- Set Schedule ---");
        System.out.print("Enter Class ID: ");
        String classId = input.next();
        input.nextLine();
        System.out.print("Enter Date (yyyy-MM-dd): ");
        String dateStr = input.nextLine();
        System.out.print("Enter Time Slot (e.g., 10:00AM): ");
        String timeSlot = input.nextLine();

        try {
            for (FitnessClass c : classes) {
                if (c.getClassId().equalsIgnoreCase(classId)) {
                    LocalDate date = LocalDate.parse(dateStr);
                    c.setSchedule(date, timeSlot);
                    System.out.println("Schedule set successfully.");
                    return;
                }
            }
            System.out.println("Class ID not found.");
        } catch (Exception e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd (e.g., 2026-03-18).");
        }
    }

    public static void deleteClass(ArrayList<FitnessClass> classes) {
        Scanner input = new Scanner(System.in);
        System.out.println("\n--- Delete Class ---");
        System.out.print("Enter Class ID to delete: ");
        String classId = input.next();

        for (int i = 0; i < classes.size(); i++) {
            if (classes.get(i).getClassId().equalsIgnoreCase(classId)) {
                classes.remove(i);
                System.out.println("Class deleted successfully.");
                return;
            }
        }
        System.out.println("Class ID not found.");
    }

    public static void assignTrainerToClass(ArrayList<FitnessClass> classes, ArrayList<Trainer> trainers) {
        Scanner input = new Scanner(System.in);

        System.out.print("\nEnter Class ID: ");
        String classId = input.next();

        System.out.print("Enter Trainer ID: ");
        String trainerId = input.next();

        FitnessClass selectedClass = null;
        Trainer selectedTrainer = null;

        for (FitnessClass c : classes) {
            if (c.getClassId().equalsIgnoreCase(classId)) {
                selectedClass = c;
                break;
            }
        }

        for (Trainer t : trainers) {
            if (t.getUserId().equalsIgnoreCase(trainerId)) {
                selectedTrainer = t;
                break;
            }
        }

        if (selectedClass != null && selectedTrainer != null) {
            selectedClass.setInstructor(selectedTrainer);
            System.out.println("\n========================================");
            System.out.println("  SUCCESS: " + selectedTrainer.getName() + " assigned to " + selectedClass.getClassName());
            System.out.println("========================================");
        } else {
            System.out.println("\n[ERROR] Invalid class ID or trainer ID. Please try again.");
        }
    }


}