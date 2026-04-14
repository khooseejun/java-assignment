package FitnessClubSystem;

import java.util.ArrayList;
import java.util.Scanner;

/*
 * FitnessSystem
 * ---------------------------------------------------
 * Main controller of the entire system.
 * 
 * Responsibilities:
 * - Display main menu (Register / Login)
 * - Control workflow
 * - Manage collections of users
 * 
 * Relationship:
 * - HAS-A relationship with Member, Trainer, Admin
 * - Controls system flow
 */

public class FitnessSystem {

    private ArrayList<Member> members;
    private ArrayList<Trainer> trainers;
    private ArrayList<Admin> admins;
    private ArrayList<Booking> bookings;
    private ArrayList<FitnessClass> classes;
    private ArrayList<Equipment> equipments;
    private FileManager fileManager;
    private int memberCounter = 1;

    private Scanner input;

    public FitnessSystem() {
        members = new ArrayList<>();
        trainers = new ArrayList<>();
        admins = new ArrayList<>();
        bookings = new ArrayList<>();
        classes = new ArrayList<>();
        equipments = new ArrayList<>();
        input = new Scanner(System.in);
        fileManager = new FileManager();
        
        //  从 txt 读取 data
        
        // 1. 先加载不依赖别人的
        admins = fileManager.loadAdmins();
        trainers = fileManager.loadTrainers();
        equipments = fileManager.loadEquipments();

        // 2. 加载依赖 Trainer 的
        classes = fileManager.loadFitnessClasses(trainers);

        // 3. 加载依赖最多的
        members = fileManager.loadMembers();
        
        // 4. 最后才是 Booking (依赖 Member 和 Class)
        bookings = fileManager.loadBookings(members, classes);


        updateMemberCounter();   
    }

    public void startSystem() {
        String choice = ""; // 初始化变量

        // 使用 while 循环包裹整个菜单
        while (!choice.equals("3")) {
            System.out.println("\n===== Fitness Club Management System =====");
            System.out.println("1. Register (Member)");
            System.out.println("2. User Login");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");

            choice = input.next(); // 使用 next() 读取字符串，安全且不留换行符

            switch (choice) {
                case "1":
                    registerMember();
                    // 执行完后，循环会自动回到开头，再次显示主菜单
                    break;
                case "2":
                    loginMenu();
                    // 同样的，从登录界面退出后，也会回到这里
                    break;
                case "3":
                    System.out.println("Exiting system...");
                    System.out.println("Saving all data... Goodbye!");
                    fileManager.saveAll(members, trainers, equipments, classes,bookings);
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
                    // 循环会自动重启，不需要手动再次调用 startSystem()
            }
        }
    }

    /*
    * Generate unique Member ID
    * Format: M001, M002...
    */
    private String generateMemberId(int typeChoice) {
        String prefix = "M";
        if (typeChoice == 1) prefix = "MB";
        else if (typeChoice == 2) prefix = "MS";
        else if (typeChoice == 3) prefix = "MG";
        
        return String.format("%s%03d", prefix, memberCounter++);
    }
    
    private void updateMemberCounter() {
        int maxId = 0;
        for (Member m : members) {
            String id = m.getMemberID(); // 例如 M001
            // 逻辑：提取所有数字部分
            String numericPart = id.replaceAll("[^0-9]", ""); 
            if (!numericPart.isEmpty()) {
                int number = Integer.parseInt(numericPart);
                if (number > maxId) maxId = number;
            }
        }
        memberCounter = maxId + 1;
    }

    /*
    * Registration for new Member.
    * Member selects membership level.
    * System auto-generates unique ID.
    * Saves to TXT file after success.
    */
   
    private void registerMember() {

        input.nextLine(); // clear buffer

        System.out.println("\n=== Member Registration ===");

        System.out.print("Enter Name: ");
        String name = input.nextLine();

        System.out.print("Enter Age: ");
        int age = input.nextInt();

        System.out.print("Enter Height (m): ");
        double height = input.nextDouble();

        System.out.print("Enter Weight (kg): ");
        double weight = input.nextDouble();

        System.out.println("\nSelect Membership Type:");
        System.out.println("1. Basic (Price: RM50)");
        System.out.println("2. Silver (Price: RM100)");
        System.out.println("3. Gold (Price: RM200)");
        System.out.print("Enter choice: ");

        int choice = input.nextInt();

        Membership membership = null;

        switch (choice) {
            case 1:
                membership = new BasicMembership();
                break;
            case 2:
                membership = new SilverMembership();
                break;
            case 3:
                membership = new GoldMembership();
                break;
            default:
                System.out.println("Invalid membership type.");
                return;
        }
        
        // 2. 关键点：清理 nextInt() 留下的回车符，防止影响密码输入
        input.nextLine();

        System.out.print("Create Password: ");
        String password = input.next();

        // Generate ID
        String memberId = generateMemberId(choice);

        Member newMember = new Member(
                memberId,
                password,
                name,
                age,
                height,
                weight,
                membership,
                "Active"
                
        );

        members.add(newMember);

        fileManager.saveAllMembers(members);

        System.out.println("\nRegistration Successful!");
        System.out.println("--------------------------------");
        System.out.println("Your Member ID  : " + memberId);
        System.out.println("Name            : " + name);
        System.out.println("Membership      : " + membership.getMembershipName());
        System.out.println("Fee to pay      : RM " + membership.getPrice());
        System.out.println("--------------------------------");

        // --- 新加的部分：等待用户确认 ---
        System.out.println("\nPress Enter to go back to Main Menu...");
        input.nextLine(); // 1. 消耗掉之前可能残余的回车符
        input.nextLine(); // 2. 真正等待用户按下一个 Enter
    }

    /*
     * Second level login menu
     */
    private void loginMenu() {
        boolean backToMain = false;

        while (!backToMain) {
            System.out.println("\n=== User Login ===");
            System.out.println("1. Member Login"); 
            System.out.println("2. Trainer Login");
            System.out.println("3. Admin Login");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter choice: ");

            // 建议统一使用 next()，避免 nextLine() 的换行符 Bug
            String roleChoice = input.next(); 

            switch (roleChoice) {
                case "1":
                    memberLogin();
                    break;
                case "2":
                    trainerLogin();
                    break;
                case "3":
                    adminLogin();
                    break;
                case "4":
                    backToMain = true; // 退出当前循环，自然回到 startSystem()
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1-4.");
                    // 不需要再次调用 loginMenu()，循环会自动重来
            }
        }
    }

    private void memberLogin() {

        Member currentMember = null;

        // login loop
        while(currentMember == null){
            System.out.print("Enter Member ID: ");
            String memberId = input.next();

            System.out.print("Enter Password: ");
            String password = input.next();

            // Search member
            for (Member m : members) {
                // 改去validatePassword(password)
                if (m.getMemberID().equals(memberId) && m.validatePassword(password)) {
                    currentMember = m;
                    break;
                }
            }

            //  Invalid login
            if (currentMember == null) {
                System.out.println("\nInvalid Member ID or Password!");
                System.out.println("Please try again...");

                // 可选：增加一个退出选项，防止用户死循环
                System.out.print("Type 'exit' to go back or any key to retry: ");
                if (input.next().equalsIgnoreCase("exit")){
                    return;
                }
            }
        }

        System.out.println("\nLogin successful!");
        System.out.println("Welcome " + currentMember.getMemberID() + " - " + currentMember.getName());
        String choice; // 修改为 String

        do {
            currentMember.displayMenu();
            System.out.print("Enter choice: ");
            choice = input.next(); // 使用 next() 读取字符串

            switch (choice) {
                case "1":
                    currentMember.viewProfile();
                    System.out.println("Press any key or Enter to go back...");
                    // 1. Consume the leftover newline from input.next()
                    input.nextLine();
                    // 2. This actually waits for the user to hit Enter (or type + Enter)
                    input.nextLine(); 
                    break;
                case "2":
                    currentMember.viewClasses(classes, bookings, input);
                    fileManager.saveBookings(bookings); // 顺手保存
                    fileManager.updateFitnessClasses(classes);
                    break;
                case "3":
                    currentMember.viewBooking(bookings, input);
                    fileManager.saveBookings(bookings); // 顺手保存
                    fileManager.updateFitnessClasses(classes);
                    break;
                case "4":
                    System.out.println("Logging out...");
                    return;
                default:
                    // 此时任何非 "1"-"5" 的输入（包括字母或多位数字）都会走这里
                    System.out.println("Invalid choice. Please enter 1-4.");
            }
        } while (!choice.equals("5")); // 字符串比较使用 .equals()
    }


    private void trainerLogin() {
        Trainer currentTrainer = null;

        // 1. 登录循环：直到找到匹配的教练为止
        while (currentTrainer == null) {
            System.out.println("\n--- Trainer Login ---");
            System.out.print("Enter Trainer ID: ");
            String trainerId = input.next();

            System.out.print("Enter Password: ");
            String password = input.next();

            // 2. 核心：在 trainers 列表（文件夹）中寻找具体的某个人（简历）
            for (Trainer t : trainers) {
                // 注意：这里调用的是 t 的方法，而不是 trainers 的方法
                if (t.getUserId().equals(trainerId) && t.validatePassword(password)) {
                    currentTrainer = t; // 找到了，把个体提出来
                    break;
                }
            }

            // 3. 验证失败处理
            if (currentTrainer == null) {
                System.out.println("\nInvalid Trainer ID or Password!");
                System.out.println("Please try again...");
                
                // 可选：增加一个退出选项，防止用户死循环
                System.out.print("Type 'exit' to go back or any key to retry: ");
                if (input.next().equalsIgnoreCase("exit")){
                    return;
                }
            }
        }

        // 4. 登录成功后的交互
        System.out.println("\nLogin successful!");
        System.out.println("Welcome, Coach " + currentTrainer.getName());

       boolean isTrainerLoggedIn = true;
        while (isTrainerLoggedIn) {
            // 调用个体对象的 displayMenu()，不会报错了
            currentTrainer.displayMenu(); 
            System.out.print("Enter choice: ");
            String choice = input.next().trim();

            switch (choice) {
                case "1":
                    currentTrainer.viewMySchedule(classes);
                    break;
                case "2":
                    currentTrainer.viewAssignedMembers(classes);
                    break;
                case "3":
                    currentTrainer.trackMemberProgress(input, members, classes);
                    break;
                case "4":
                    System.out.println("Logging out...");
                    isTrainerLoggedIn = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1-4.");
            }
        }
    }

    private void adminLogin() {
        Admin currentAdmin = null;

        // 1. 登录验证循环
        while (currentAdmin == null) {
            System.out.println("\n--- Admin Login ---");
            System.out.print("Enter Admin ID: ");
            String id = input.next();

            System.out.print("Enter Password: ");
            String password = input.next();

            // 2. 在 admins 列表(文件夹)中寻找具体的 Admin(个人)
            for (Admin a : admins) {
                // 这里确保你已经在 User 或 Admin 类里写好了 getUserId() 和 validatePassword()
                if (a.getUserId().equals(id) && a.validatePassword(password)) {
                    currentAdmin = a; 
                    break;
                }
            }

            if (currentAdmin == null) {
                System.out.println("\nInvalid Admin ID or Password!");
                System.out.print("Type 'exit' to go back or any key to retry: ");
                if (input.next().equalsIgnoreCase("exit")){
                    return;
                }
            }
        }

        // 3. 登录成功后的业务循环
        System.out.println("\nLogin successful! Welcome, Administrator.");
        
        // zhi hao
        String choice;
        do {
            currentAdmin.displayMenu(); // 调用具体 Admin 对象的菜单
            System.out.print("Enter choice: ");
            choice = input.next();

            switch (choice) {
                case "1":
                    currentAdmin.manageMembers(members);
                    fileManager.saveAllMembers(members);
                    break;
                case "2":
                    if(equipments != null){
                        currentAdmin.manageEquipment(equipments);
                        fileManager.updateEquipments(equipments);
                    }
                    break;
                case "3":
                    currentAdmin.assignClass(classes, trainers);
                    fileManager.updateFitnessClasses(classes);
                    break;
                case "4":
                    // 调用你之前看过的报表生成逻辑
                    currentAdmin.generateMonthlyRevenueReport(members);
                    break;
                case "5":
                    System.out.println("Logging out...");
                    return; // 返回最开始的登录选择界面
                default:
                    System.out.println("Invalid choice. Please enter 1-5.");
            }
        } while (!choice.equals("5"));
    }
    // zhi hao

    public static void main(String[] args) {
        FitnessSystem system = new FitnessSystem();
        system.startSystem();
    }
}