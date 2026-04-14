package FitnessClubSystem;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * FileManager
 * ------------------------
 * Handles TXT file storage.
 * Demonstrates EXCEPTION HANDLING.
 * ASSOCIATION with all data classes.
 */
public class FileManager {

    // members.txt area
    public void saveAllMembers(ArrayList<Member> members) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("members.txt", false))) {
            for (Member m : members) {
                // 把 Member 的所有 8 个字段都写进去
                String data = m.getMemberID() + "|" +
                            m.getPassword() + "|" +
                            m.getName() + "|" +
                            m.getAge() + "|" +
                            m.getHeight() + "|" +
                            m.getWeight() + "|" +
                            m.getMembership().getMembershipName() + "|" +
                            m.getStatus();
                writer.write(data);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error: Could not save members to file.");
        }
    }

    public ArrayList<Member> loadMembers() {

        ArrayList<Member> members = new ArrayList<>();

        try {
            File file = new File("members.txt");
            Scanner reader = new Scanner(file);

            while (reader.hasNextLine()) {

                String line = reader.nextLine();
                String[] data = line.split("\\|");

                String id = data[0];
                String password = data[1];
                String name = data[2];
                int age = Integer.parseInt(data[3]);
                double height = Double.parseDouble(data[4]);
                double weight = Double.parseDouble(data[5]);
                String membershipType = data[6];
                String status = data[7];

                Membership membership = null;

                if (membershipType.equalsIgnoreCase("Basic")) {
                    membership = new BasicMembership();
                }
                else if (membershipType.equalsIgnoreCase("Silver")) {
                    membership = new SilverMembership();
                }
                else if (membershipType.equalsIgnoreCase("Gold")) {
                    membership = new GoldMembership();
                }

                Member member = new Member(
                        id,
                        password,
                        name,
                        age,
                        height,
                        weight,
                        membership,
                        status
                );
                members.add(member);
            }
            reader.close();

        } catch (Exception e) {
            System.out.println("Error loading members file.");
        }
        return members;
    }
    // members.txt area

    // bookings.txt (Cheng Jun Yu)
    // --- 1. 保存所有预约 ---
    public void saveBookings(ArrayList<Booking> bookings) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("bookings.txt"))) {
            for (Booking b : bookings) {
                writer.println(b.toFileString());
            }
        } catch (IOException e) {
            System.out.println("Error saving bookings: " + e.getMessage());
        }
    }

    // --- 2. 加载所有预约 ---
    public ArrayList<Booking> loadBookings(ArrayList<Member> members, ArrayList<FitnessClass> classes) {
        ArrayList<Booking> bookings = new ArrayList<>();
        File file = new File("bookings.txt");
        if (!file.exists()) return bookings; // 如果文件不存在，返回空列表

        try (Scanner reader = new Scanner(file)) {
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                String[] parts = line.split("\\|"); // 使用 | 分割

                if (parts.length == 6) {
                    String bId = parts[0];
                    String mId = parts[1];
                    String cId = parts[2];
                    LocalDate sDate = LocalDate.parse(parts[3]);
                    String status = parts[4];
                    String tier = parts[5];

                    // --- 核心：对象关联 ---
                    Member foundMember = null;
                    for (Member m : members) {
                        if (m.getMemberID().equals(mId)) {
                            foundMember = m;
                            break;
                        }
                    }

                    FitnessClass foundClass = null;
                    for (FitnessClass c : classes) {
                        if (c.getClassId().equals(cId)) {
                            foundClass = c;
                            break;
                        }
                    }

                    // 如果 Member 和 Class 都还在系统中，才重建这个 Booking
                    if (foundMember != null && foundClass != null) {
                        Booking b = new Booking(bId, foundMember, foundClass, sDate, tier);
                        b.setStatus(status); // 恢复 confirmed/cancelled 状态
                        
                        // 【非常重要】：同步更新 Class 内部的名单
                        if (status.equalsIgnoreCase("Confirmed")) {
                            foundClass.addMember(foundMember); 
                        }
                        
                        bookings.add(b);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading bookings: " + e.getMessage());
        }
        return bookings;
    }
    // bookings.txt


    // (ALL) *follow txtfile format*
    // fitnessclasses.txt area (Khoo See Ze)
    // saddFitnessClasses(), let member/admin to use this method
    public void addFitnessClass(FitnessClass fitnessClass) {
        try (BufferedWriter writer =
                new BufferedWriter(new FileWriter("fitnessclasses.txt", true))) {

            String instructorId = (fitnessClass.getInstructor() != null)
                    ? fitnessClass.getInstructor().getUserId() : "TBA";
            String dateStr = (fitnessClass.getDate() != null)
                    ? fitnessClass.getDate().toString() : "TBA";

            writer.write(fitnessClass.getClassId() + "|" +
                        fitnessClass.getClassName() + "|" +
                        dateStr + "|" +
                        fitnessClass.getTimeSlot() + "|" +
                        instructorId + "|" +
                        fitnessClass.getCurrentEnrollment() + "|" +
                        fitnessClass.getMaxCapacity());

            writer.newLine();
            System.out.println("Fitness class added: " + fitnessClass.getClassName());

        } catch (IOException e) {
            System.out.println("Error saving fitness class data.");
        }
    }
    // updateFitnessClasses(), let member/admin to use this method
    public void updateFitnessClasses(ArrayList<FitnessClass> fitnessClasses) {
        try (BufferedWriter writer =
                new BufferedWriter(new FileWriter("fitnessclasses.txt"))) {

            for (FitnessClass fc : fitnessClasses) {
                String instructorId = (fc.getInstructor() != null)
                        ? fc.getInstructor().getUserId() : "TBA";
                String dateStr = (fc.getDate() != null)
                        ? fc.getDate().toString() : "TBA";

                writer.write(fc.getClassId() + "|" +
                            fc.getClassName() + "|" +
                            dateStr + "|" +
                            fc.getTimeSlot() + "|" +
                            instructorId + "|" +
                            fc.getCurrentEnrollment() + "|" +
                            fc.getMaxCapacity());
                writer.newLine();
            }

            System.out.println("Fitness classes updated successfully.");

        } catch (IOException e) {
            System.out.println("Error updating fitness classes data.");
        }
    }
    // loadFitnessClasses(), let member/admin/trainer to use this method
    public ArrayList<FitnessClass> loadFitnessClasses(ArrayList<Trainer> trainers) {
        ArrayList<FitnessClass> fitnessClasses = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            File file = new File("fitnessclasses.txt");
            if (!file.exists()) {
                System.out.println("Fitness classes file not found: fitnessclasses.txt");
                return fitnessClasses;
            }

            Scanner reader = new Scanner(file);

            while (reader.hasNextLine()) {
                String line = reader.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] data = line.split("\\|");

                if (data.length >= 7) {
                    String classId = data[0];
                    String className = data[1];
                    LocalDate date = data[2].equals("TBA") ? null : LocalDate.parse(data[2], formatter);
                    String timeSlot = data[3];
                    String instructorId = data[4];
                    int currentEnrollment = Integer.parseInt(data[5]);
                    int maxCapacity = Integer.parseInt(data[6]);

                    Trainer instructor = null;
                    if (!instructorId.equals("TBA")) {
                        for (Trainer t : trainers) {
                            if (t.getUserId().equals(instructorId)) {
                                instructor = t;
                                break;
                            }
                        }
                    }

                  
                    FitnessClass fc = new FitnessClass(classId, className, maxCapacity);
                    fc.setSchedule(date, timeSlot);
                    fc.setInstructor(instructor);
                    fc.setCurrentEnrollment(currentEnrollment);
                    fitnessClasses.add(fc);
                } else {
                    System.out.println("Skipping invalid fitness class entry: " + line);
                }
            }
            reader.close();

            System.out.println("Loaded " + fitnessClasses.size() + " fitness classes.");

        } catch (Exception e) {
            System.out.println("Error loading fitness classes file: " + e.getMessage());
        }
        return fitnessClasses;
    }
    // fitnessclasses.txt area

    // trainers.txt area (Low Kar Wai)
    public void saveTrainer(Trainer trainer) {
        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter("trainers.txt", true))) {

            writer.write(trainer.getUserId() + "|" +
                    trainer.getPassword() + "|" +
                    trainer.getName());
            writer.newLine();

        } catch (IOException e) {
            System.out.println("Error saving trainer data.");
        }
    }

    // TODO: 修了trainer.getSpecialty())，原本是没有的
    public void updateTrainers(ArrayList<Trainer> trainers) {
        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter("trainers.txt"))) {

            for (Trainer trainer : trainers) {
                writer.write(trainer.getUserId() + "|" +
                        trainer.getPassword() + "|" +
                        trainer.getName());
                writer.newLine();
            }

            System.out.println("Trainer data updated successfully.");

        } catch (IOException e) {
            System.out.println("Error updating trainer data.");
        }
    }

    public ArrayList<Trainer> loadTrainers() {
        ArrayList<Trainer> trainers = new ArrayList<>();

        try {
            File file = new File("trainers.txt");
            if (!file.exists()) {
                System.out.println("Trainers file not found: trainers.txt");
                return trainers;
            }
            Scanner reader = new Scanner(file);

            while (reader.hasNextLine()) {
                String line = reader.nextLine().trim();

                if (line.isEmpty()) {
                    continue;
                }

                String[] data = line.split("\\|");

                if (data.length >= 3) {
                    String id = data[0];
                    String password = data[1];
                    String name = data[2];
                    
                    Trainer trainer = new Trainer(id, password, name);
                    trainers.add(trainer);
                } else {
                    System.out.println("Skipping invalid trainer entry: " + line);
                }
            }
            reader.close();

            System.out.println("Loaded " + trainers.size() + " trainers.");

        } catch (Exception e) {
            System.out.println("Error loading trainers file.");
        }
        return trainers;
    }
    // trainers.txt area

    //equipments.tst area (Low Kar Wai)
    // loadEquipments() , let admin to use this method
    public ArrayList<Equipment> loadEquipments() {
        ArrayList<Equipment> equipments = new ArrayList<>();
        
        try {
            File file = new File("equipments.txt");
            
            // Check if file exists
            if (!file.exists()) {
                System.out.println("Equipment file not found: equipments.txt");
                return equipments; // Return empty list
            }
            
            Scanner reader = new Scanner(file);
            
            while (reader.hasNextLine()) {
                String line = reader.nextLine().trim();
                
                // Skip empty lines
                if (line.isEmpty()) {
                    continue;
                }
                
                String[] data = line.split("\\|");
                
                if (data.length == 3) {
                    String id = data[0];
                    String name = data[1];
                    String status = data[2];
                    
                    Equipment equipment = new Equipment(id, name, status);
                    equipments.add(equipment);
                } else {
                    System.out.println("Skipping invalid equipment entry: " + line);
                }
            }
            reader.close();
            
            System.out.println("Loaded " + equipments.size() + " equipment items.");
            
        } catch (Exception e) {
            System.out.println("Error loading equipments file: " + e.getMessage());
        }
        return equipments;
    }
    //  updateEquipments() , let admin to use this method
    public void updateEquipments(ArrayList<Equipment> equipments) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("equipments.txt"))) {
            
            for (Equipment e : equipments) {
                writer.write(e.toFileString());
                writer.newLine();
            }
            
            System.out.println("Equipment data saved successfully.");
            
        } catch (IOException e) {
            System.out.println("Error saving equipment data: " + e.getMessage());
        }
    }

    //add 
    public void addEquipment(Equipment equipment) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("equipments.txt", true))) {
            writer.write(equipment.toFileString());
            writer.newLine();
            System.out.println("Equipment added: " + equipment.getName());
            
        } catch (IOException e) {
            System.out.println("Error adding equipment: " + e.getMessage());
        }
    }
    //equipments.tst area

    // admins.txt area (Tang Zhi Hao)
    public ArrayList <Admin> loadAdmins(){
        ArrayList<Admin> admins = new ArrayList<>();

        try{
            File file = new File("admins.txt");
            Scanner reader = new Scanner(file);

            while (reader.hasNextLine()) {
                String line = reader.nextLine();

                if(line.isEmpty()){
                    continue;
                }

                String[] data = line.split("\\|");

                String id = data[0].trim();
                String password =data[1].trim();

                Admin admin = new Admin(id, password);
                admins.add(admin);
                
            }

            reader.close();
        } catch(Exception e){
            System.out.println("Error loading admin file.");
        }

        return admins;
    }
    // admins.txt area 

    public void saveAll(ArrayList<Member> members,
                    ArrayList<Trainer> trainers,
                    ArrayList<Equipment> equipments,
                    ArrayList<FitnessClass> classes,
                    ArrayList<Booking> bookings) {

        try {
            System.out.println("Saving all data to files...");

            saveAllMembers(members);
            updateTrainers(trainers);
            updateEquipments(equipments);
            updateFitnessClasses(classes);
            saveBookings(bookings);

            System.out.println("All data saved successfully!");

        } catch (Exception e) {
            System.out.println("Error saving all data: " + e.getMessage());
        }
    }

}

