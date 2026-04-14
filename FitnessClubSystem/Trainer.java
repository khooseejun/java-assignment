package FitnessClubSystem;

import java.util.ArrayList;
import java.util.Scanner;

/*
 * Trainer Class
 * ---------------------------------------------------
 * Inherits from User.
*/

/**
 * Trainer Class
 * ---------------------------------------------------
 * 负责人: Low Kar Wai
 * 任务: 扩展教练属性，并实现对会员(Member)的管理逻辑。
 */
//(Trainer 是需要有member和class先的，所以创个empty_method())

public class Trainer extends User {

    private String name;

    // private ArrayList<Member> assignedMembers; (存储被分配给该教练的会员)

    public Trainer(String userId, String password, String name) {
        super(userId, password);
        this.name = name;
        // TODO: 2. 在构造函数中初始化 ArrayList
        // this.assignedMembers = new ArrayList<>();
    }

    @Override
    public void displayMenu() {
        System.out.println("\n=== Trainer Menu (ID: " + getUserId() + ") ===");
        System.out.println("1. View My Class Schedule"); // 查看自己被分配了哪些课
        System.out.println("2. View Assigned Members");  // 查看自己的学员名单
        System.out.println("3. Track Member Progress");  // 查看学员的健康数据(Health Stats)
        System.out.println("4. Logout");
    }

    public String getName(){
        return name;
    }

    // 
    public void viewMySchedule(ArrayList<FitnessClass> classes) {
        System.out.println("--- My Class Schedule ---");
        if (classes == null || classes.isEmpty()) {
            System.out.println("No classes scheduled.");
            return;
        }
        boolean any = false;
        for (FitnessClass c : classes) {
            if (c.getInstructor() != null
                    && c.getInstructor().getUserId().equalsIgnoreCase(getUserId())) {
                c.displayClassInfo();
                any = true;
            }
        }
        if (!any) {
            System.out.println("You are not assigned to any class yet.");
        }
    }

    // TODO: 3. 实现查看学员名单的方法 // 连
    // 逻辑: 遍历 assignedMembers 列表并打印每个 Member 的基本信息。
    public void viewAssignedMembers(ArrayList<FitnessClass> classes) {
        System.out.println("--- Your Member List ---");
        if (classes == null || classes.isEmpty()) {
            System.out.println("No classes in the system.");
            return;
        }
        ArrayList<String> seenMemberIds = new ArrayList<>();
        ArrayList<Member> roster = new ArrayList<>();
        for (FitnessClass c : classes) {
            if (c.getInstructor() != null
                    && c.getInstructor().getUserId().equalsIgnoreCase(getUserId())) {
                for (Member m : c.getBookedMembers()) {
                    if (!seenMemberIds.contains(m.getMemberID())) {
                        seenMemberIds.add(m.getMemberID());
                        roster.add(m);
                    }
                }
            }
        }
        if (roster.isEmpty()) {
            System.out.println("No members enrolled in your classes yet.");
            return;
        }
        for (Member m : roster) {
            System.out.println("ID: " + m.getMemberID() + " | Name: " + m.getName());
        }
    }


    // TODO: 4. 实现进度跟踪方法
    // 逻辑: 让教练输入一个 Member ID，然后显示该 Member 的 BMI、体重等数据。
    public void trackMemberProgress(Scanner input, ArrayList<Member> members,
                                    ArrayList<FitnessClass> classes) {
        if (members == null || members.isEmpty()) {
            System.out.println("No members in the system.");
            return;
        }
        System.out.print("Enter Member ID: ");
        String mid = input.next().trim();
        Member found = null;
        for (Member m : members) {
            if (m.getMemberID().equalsIgnoreCase(mid)) {
                found = m;
                break;
            }
        }
        if (found == null) {
            System.out.println("Member not found.");
            return;
        }
        if (!isMemberInMyClasses(found, classes)) {
            System.out.println("This member is not enrolled in any of your classes.");
            return;
        }
        System.out.println("--- Tracking Progress for Member: " + found.getMemberID() + " ---");
        System.out.println("Name: " + found.getName());
        System.out.println("Age: " + found.getAge());
        System.out.printf("Height: %.2f m | Weight: %.2f kg%n", found.getHeight(), found.getWeight());
        double bmi = found.calculateBMI();
        System.out.printf("BMI: %.2f (%s)%n", bmi, getBmiCategory(bmi));
    }

    private boolean isMemberInMyClasses(Member m, ArrayList<FitnessClass> classes) {
        if (classes == null) {
            return false;
        }
        for (FitnessClass c : classes) {
            if (c.getInstructor() != null
                    && c.getInstructor().getUserId().equalsIgnoreCase(getUserId())) {
                for (Member bm : c.getBookedMembers()) {
                    if (bm.getMemberID().equalsIgnoreCase(m.getMemberID())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String getBmiCategory(double bmi) {
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi < 25.0) {
            return "Normal";
        } else if (bmi < 30.0) {
            return "Overweight";
        }
        return "Obese";
    }
    // TODO: 5. 辅助方法: 被分配新会员
    // 当 Admin 分配会员给这个教练时，调用此方法。
    // public void addMember(Member m) { ... }


}