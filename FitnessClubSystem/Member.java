package FitnessClubSystem;

import java.util.ArrayList;
import java.util.Scanner;

/*
 * Member Class
 * -----------------------------------------
 * Inherits from User.
 * HAS-A relationship with Membership.
 */

public class Member extends User {

    private String name;
    private int age;
    private double height;
    private double weight;
    private String status;

    // private ArrayList<Booking> bookingList; Member 应该“知道自己有哪些 Booking”

    private Membership membership;   // HAS-A relationship

    public Member(String userId, String password,
                  String name, int age,
                  double height, double weight,
                  Membership membership, String status) {

        super(userId, password);
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.membership = membership;
        this.status = status;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getStatus(){
        return status;
    }
    public String getName() { 
        return name; 
    }
    public int getAge() { 
        return age; 
    }
    public double getHeight() { 
        return height; 
    }
    public double getWeight() { 
        return weight; 
    }
    public Membership getMembership() { 
        return membership; 
    }
    public String getPassword() {
        return super.getPassword();
    }
    public String getMemberID() {
        return getUserId();
    }
    
    @Override
    public void displayMenu() {
        System.out.println("\n=== Member Menu ===");
        System.out.println("1. View Profile"); // viewProfile()
        System.out.println("2. View Classes & Book"); // viewClasses()
        System.out.println("3. View My Bookings & Cancel"); // viewBooking()
        System.out.println("4. Logout");
    }

    // 建议增加一个独立方法，以后 Trainer 跟踪进度也要用
    public double calculateBMI() {
        double h = this.height;
        h = h / 100.0;

        if (h <= 0) return 0.0;

        return weight / (h * h);
    }

    /*
    * Display member profile information
    * Shows personal details and membership information
    */
    public void viewProfile() {
        
        System.out.println("\n===== Member Profile =====");
        System.out.println("Status: " + status);
        System.out.println("Member ID: " + getMemberID());
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);

        System.out.println("Height: " + height + " m");
        System.out.println("Weight: " + weight + " kg");

        System.out.println("Membership Type: " + membership.getMembershipName());
        System.out.println("Membership Fee: RM " + membership.getPrice());

        System.out.printf("BMI: %.2f", calculateBMI());
        System.out.println("\nSauna Access: " + 
            (membership.hasSaunaAccess() ? "Yes" : "No"));
        System.out.println("Priority Booking: " + 
            (membership.hasPriorityBooking() ? "Yes" : "No"));

        System.out.println("==========================");
    }

    public void checkSaunaAccess() {

        if(membership.hasSaunaAccess()){
            System.out.println("Sauna access granted.");
        }
        else{
            System.out.println("Sauna access is only available for Gold members.");
        }

    }

    // --- 新增的连接方法：Book Class ---
    // 这个方法就是你要求的，参 Booking.java 传过来的 method
    public void bookClass(ArrayList<FitnessClass> classes, ArrayList<Booking> bookings, Scanner input) {
        // 直接调用 Booking 类里面的静态业务逻辑
        Booking.performBooking(this, classes, bookings, input);
    }

    // // --- 新增的连接方法：Cancel Booking (占位逻辑) ---
    // public void cancelMyBooking(ArrayList<Booking> bookings, Scanner input) {
    //     System.out.println("\n--- Cancel Your Booking ---");
    //     // 这里以后可以写：遍历 bookings，找到 memberId 是当前 ID 的，然后执行 remove
    //     System.out.println("Cancel logic will be implemented here.");
    // }

    // --- 逻辑整合：查看并预约 ---
    // Member.java 里的 viewClasses 修改如下
    public void viewClasses(ArrayList<FitnessClass> classes, ArrayList<Booking> bookings, Scanner input) {
        // 1. 调用专门给 Member 看的方法
        FitnessClass.showMemberSchedule(classes);

        // 2. 询问预约
        System.out.print("\nDo you want to book a class? (Y/N): ");
        String choice = input.next();

        if (choice.equalsIgnoreCase("Y")) {
            this.bookClass(classes, bookings, input);
        }
    }

    public void viewBooking(ArrayList<Booking> allBookings, Scanner input) {
        System.out.println("\n===== My Current Bookings =====");
        ArrayList<Booking> myCurrentBookings = new ArrayList<>();
        
        // 1. 只找这个会员的预约
        for (Booking b : allBookings) {
            if (b.getMember().getMemberID().equals(this.getMemberID())) {
                System.out.printf("[%s] Class: %-15s | Date: %s | Status: %s%n",
                    b.getBookingId(), b.getFitnessClass().getClassName(), 
                    b.getSessionDate(), b.getStatus());
                myCurrentBookings.add(b);
            }
        }

        if (myCurrentBookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        // 2. 取消逻辑
        System.out.print("\nEnter Booking ID to cancel (or 'N' to go back): ");
        String bid = input.next();

        if (!bid.equalsIgnoreCase("N")) {
            for (Booking b : myCurrentBookings) {
                if (b.getBookingId().equalsIgnoreCase(bid)) {
                    if (b.getStatus().equals("Cancelled")) {
                        System.out.println("This booking is already cancelled.");
                        return;
                    }
                    
                    // --- 核心同步动作 ---
                    b.setStatus("Cancelled"); // 1. 改变收据状态
                    b.getFitnessClass().cancelEnrollment(this); // 2. 释放 Class 位子
                    
                    System.out.println("Success! Booking " + bid + " cancelled.");
                    return;
                }
            }
            System.out.println("Booking ID not found in your list.");
            
        }
    }

}