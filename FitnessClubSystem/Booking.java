package FitnessClubSystem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * Booking Class
 * ---------------------------------------------------
 * Represents a class booking made by a Member.
 * 
 * Relationship:
 * - ASSOCIATION with Member
 * - ASSOCIATION with FitnessClass
 * 
 * Composition Concept:
 * Booking cannot exist without Member and FitnessClass.
 */

public class Booking {

    private String bookingId;
    private Member member;
    private FitnessClass fitnessClass;
    private LocalDate sessionDate; // 实际上课的日期
    private String status;         // "Confirmed" 或 "Cancelled"
    private String memberTier;     // 保存那一刻的会员等级，方便直接读取

    // 构造函数：增加 sessionDate 和 memberTier
    public Booking(String bookingId, Member member, FitnessClass fitnessClass, LocalDate sessionDate, String memberTier) {
        this.bookingId = bookingId;
        this.member = member;
        this.fitnessClass = fitnessClass;
        this.sessionDate = sessionDate;
        this.status = "Confirmed"; // 默认新建就是已确认
        this.memberTier = memberTier;
    }

    // --- Getters ---
    public String getBookingId() { return bookingId; }
    public Member getMember() { return member; }
    public FitnessClass getFitnessClass() { return fitnessClass; }
    public LocalDate getSessionDate() { return sessionDate; }
    public String getStatus() { return status; }
    public String getMemberTier() { return memberTier; }
    
    public void setStatus(String status) { this.status = status; }

    // 用于写入 TXT 的格式化字符串
    public String toFileString() {
        return bookingId + "|" + member.getUserId() + "|" + fitnessClass.getClassId() + "|" + sessionDate + "|" + status + "|" + memberTier;
    }
    
    // --- 核心业务方法 (给 Member.java 调用) ---
    /**
     * 这是一个静态工具方法，负责处理整个预约流程。
     * 它会检查资格、调用 FitnessClass 的 enroll 逻辑，并最终生成 Booking 对象。
     */
    public static void performBooking(Member member, ArrayList<FitnessClass> classes, ArrayList<Booking> bookings, Scanner input) {
        System.out.println("\n--- Class Booking Process ---");
        
        // 1. 让用户输入 ID
        System.out.print("Enter Class ID you want to book: ");
        String targetId = input.next();

        // 2. 寻找课程
        FitnessClass selectedClass = null;
        for (FitnessClass c : classes) {
            if (c.getClassId().equalsIgnoreCase(targetId)) {
                selectedClass = c;
                break;
            }
        }

        if (selectedClass == null) {
            System.out.println("[Error] Class ID not found.");
            return;
        }

        // 3. 检查会员权限 (你原本的 hasPriorityBooking 逻辑)
        if (member.getMembership().hasPriorityBooking()) {
            System.out.println(">> Priority booking recognized for " + member.getName() + " (Gold Member)");
        }

        // --- 新增判定：防止重复预约 ---
        for (Booking b : bookings) {
            if (b.getMember().getMemberID().equals(member.getMemberID()) && 
                b.getFitnessClass().getClassId().equalsIgnoreCase(targetId) &&
                b.getStatus().equalsIgnoreCase("Confirmed")) {
                
                System.out.println("\n[ERROR] You have already booked this class!");
                return; // 直接退出，不让继续订
            }
        }

        // 4. 调用 FitnessClass 的核心判断逻辑 (我们在 FitnessClass 里写的金卡预留位逻辑)
        if (selectedClass.enrollMember(member)) {
            // 生成 ID (例如: BK-101)
            String newBookingId = "BK-" + (bookings.size() + 1001);
            
            // 创建新的预约对象
            Booking newBooking = new Booking(
                newBookingId, 
                member, 
                selectedClass, 
                selectedClass.getDate(), 
                member.getMembership().getMembershipName()
            );

            // 加入系统列表
            bookings.add(newBooking);
            
            System.out.println("========================================");
            System.out.println("SUCCESS: " + selectedClass.getClassName() + " booked!");
            System.out.println("Booking ID: " + newBookingId);
            System.out.println("========================================");
        } else {
            // 如果 enrollMember 返回 false，FitnessClass 内部已经打印了失败原因
            System.out.println("Booking failed. Please check class availability.");
        }
    }
}