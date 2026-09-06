import java.sql.*;
import java.util.*;

public class FinalProject {
    static final String DB_URL = "jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241";

    public static boolean isStringInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex) {
            System.out.println("Must enter an integer.");
            return false;
        }
    }

    public static boolean isString(String s) {
        
            boolean isString = true;
            char[] chars = s.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (Character.isDigit(chars[i])) {
                    isString = false;
                }
            }
            if (isString == false) {
                System.out.println("Response cannot contain integers.");
            }
            return isString;
    }

    public static int GenerateRandom(int min, int max) {
          int random_int = (int)Math.floor(Math.random()*(max-min+1)+min);
          return random_int;
    }
    public static void main(String[] args) {
        Connection conn = null;
        Scanner in = new Scanner(System.in);
        do {
            try {
                // getting user input for user id and password
                System.out.print("Enter Oracle user id:");
                String user = in.nextLine();
                System.out.print("Enter Oracle user password:");
                String pass = in.nextLine();
                // initialize connection to database
                conn = DriverManager.getConnection(DB_URL, user, pass);
                System.out.println("Super? I'm connected.");
                            
                String optionSelected;
                int optionAsInt;
                do {
                System.out.println("Welcome. Please select your status below by entering 1, 2 or 3.");
                do {
                    System.out.println("1. Property Manager\n2. Tenant\n3. NUMA Manager\n4. Exit");
                    optionSelected = in.nextLine();
                } while (isStringInt(optionSelected) == false);
                optionAsInt = Integer.parseInt(optionSelected);

                if (optionAsInt == 1) {
                    int optionAsInt2;
                do {
                    do {
                        System.out.println("What would you like to do?\n1. Record visit data\n2. Record lease data\n3. Record move-out\n4. Exit\nPlease select your option below by entering 1, 2, 3 or 4.");
                        optionSelected = in.nextLine();
                    } while (isStringInt(optionSelected) == false);
                    optionAsInt2 = Integer.parseInt(optionSelected);

                    if (optionAsInt2 == 1) {

                        String full_name;
                        String phone_number;
                        String email;
                        String date_of_visit;
                        System.out.println("Enter visitor's full name:");
                        full_name = in.nextLine();
                        System.out.println("Enter visitor's phone number in the form ###-###-####:");
                        phone_number = in.nextLine();
                        System.out.println("Enter visitor's email:");
                        email = in.nextLine();
                        System.out.println("Enter visitor's date of visit in the form dd/mm/yyyy:");
                        date_of_visit = in.nextLine();
                
                        PreparedStatement insertPerson = conn.prepareStatement("insert into person "
                        + "(full_name, phone_number, email) values (?, ?, ?)");
                        insertPerson.setString(1, full_name);
                        insertPerson.setString(2, phone_number);
                        insertPerson.setString(3, email);
                        int rowNum = insertPerson.executeUpdate();
                        insertPerson.close();
                        PreparedStatement insertVisitor = conn.prepareStatement("insert into prospective_tenant "
                        + "(full_name, phone_number, email, date_of_visit) values (?, ?, ?, ?)");
                        insertVisitor.setString(1, full_name);
                        insertVisitor.setString(2, phone_number);
                        insertVisitor.setString(3, email);
                        insertVisitor.setString(4, date_of_visit);
                        int rowNum2 = insertVisitor.executeUpdate();
                        insertVisitor.close();
                        PreparedStatement numProperties = conn.prepareStatement("select count(*) "
                        + "from property");
                        ResultSet numPropertiesResult = numProperties.executeQuery();
                        int numPropertiesInt = 0;
                        while (numPropertiesResult.next()) {
                            numPropertiesInt = numPropertiesResult.getInt("count(*)");
                        }
                        PreparedStatement myStmt2 = conn.prepareStatement("SELECT address " +
                        "FROM property");
                        ResultSet myRs2 = myStmt2.executeQuery();
                        String address;
                        String[] addresses = new String[numPropertiesInt];
                        for (int i = 0; i < numPropertiesInt; i++) {
                            myRs2.next();
                            address = myRs2.getString("address");
                            addresses[i] = address;
                            System.out.println((i+1) + ". " + address);
                        }
                        String selection;
                        do {
                            System.out.println("Which property did the visitor visit: ");
                            selection = in.nextLine();
                        } while (isStringInt(selection) == false);
                        int selectionInt = Integer.parseInt(selection);
                        String chosenAddress = addresses[selectionInt - 1];
                        PreparedStatement insertVisit = conn.prepareStatement("insert into visits "
                        + "(full_name, address) values (?, ?)");
                        insertVisit.setString(1, full_name);
                        insertVisit.setString(2, chosenAddress);
                        int rowNum3 = insertVisit.executeUpdate();
                        numProperties.close();
                        numPropertiesResult.close();
                        myRs2.close();
                        myStmt2.close();
                        insertVisit.close();
                        System.out.println("The prospective tenant's information and visit data has successfully been recored.");

                    }

                    else if (optionAsInt2 == 2) {
                        
                        boolean error = false;
                        int min = 11111;
                        int max = 99999;
                        int randomLeaseID;
                        int leaseID;
                        do {
                            PreparedStatement myStmt = conn.prepareStatement("SELECT lease_id " +
                            "FROM lease");
                            ResultSet myRs = myStmt.executeQuery();
                            randomLeaseID = GenerateRandom(min, max);
                            //boolean error = false;
                            while (myRs.next()) {
                                leaseID = myRs.getInt("lease_id");
                                if (leaseID == randomLeaseID) {
                                    error = true;
                                }
                            }
                            myRs.close();
                            myStmt.close();
                        } while (error == true);
                        PreparedStatement insertLease = conn.prepareStatement("insert into lease "
                        + "(lease_id) values (?)");
                        insertLease.setInt(1, randomLeaseID);
                        insertLease.executeUpdate();
                        insertLease.close();
                        int optionAsInt3;
                        do {
                            System.out.println("A new nonexisting lease ID number has been randomly generated.\nWhat kind of lease would you like to add?\n1. Fixed-term lease\n2. Month-by-month lease");
                            optionSelected = in.nextLine();
                        } while (isStringInt(optionSelected) == false);
                        optionAsInt3 = Integer.parseInt(optionSelected);
                        if (optionAsInt3 == 1) {
                            String numMonths;
                            int months;
                            do {
                                System.out.println("How many months will the lease last for:");
                                numMonths = in.nextLine();
                            } while (isStringInt(numMonths) == false);
                            months = Integer.parseInt(numMonths);
                            PreparedStatement insertFixedTermLease = conn.prepareStatement("insert into fixed_term_lease "
                            + "(lease_id, num_months) values (?, ?)");
                            insertFixedTermLease.setInt(1, randomLeaseID);
                            insertFixedTermLease.setInt(2, months);
                            insertFixedTermLease.executeUpdate();
                            insertFixedTermLease.close();

                            String full_name;
                            do {
                                System.out.println("Enter the full name of the individual signing the lease:");
                                full_name = in.nextLine();
                            } while (isString(full_name) == false);
                            String phone;
                            System.out.println("Enter the phone number of the individual signing the lease in the form ###-###-####:");
                            phone = in.nextLine();
                            String email;
                            System.out.println("Enter the email of the individual signing the lease:");
                            email = in.nextLine();
                            String bank;
                            do {
                                System.out.println("Enter the signer's bank name:");
                                bank = in.nextLine();
                            } while (isString(bank) == false);
                            String SSN;
                            System.out.println("Enter the social security number of the individual signing the lease in the form ###-##-####:");
                            SSN = in.nextLine();
                            String DOB;
                            System.out.println("Enter the date of birth of the individual signing the lease in the form mm/dd/yyyy:");
                            DOB = in.nextLine();
                    
                            PreparedStatement insertPerson = conn.prepareStatement("insert into person "
                            + "(full_name, phone_number, email) values (?, ?, ?)");
                            insertPerson.setString(1, full_name);
                            insertPerson.setString(2, phone);
                            insertPerson.setString(3, email);
                            insertPerson.executeUpdate();
                            insertPerson.close();
                            PreparedStatement insertTenant = conn.prepareStatement("insert into tenant "
                            + "(full_name, phone_number, email, bank, social_security_number, date_of_birth) values (?, ?, ?, ?, ?, ?)");
                            insertTenant.setString(1, full_name);
                            insertTenant.setString(2, phone);
                            insertTenant.setString(3, email);
                            insertTenant.setString(4, bank);
                            insertTenant.setString(5, SSN);
                            insertTenant.setString(6, DOB);
                            insertTenant.executeUpdate();
                            insertTenant.close();

                            System.out.println(full_name + "'s lease signature has been recorded.");
                            PreparedStatement insertSigns = conn.prepareStatement("insert into signs "
                            + "(full_name, lease_id) values (?, ?)");
                            insertSigns.setString(1, full_name);
                            insertSigns.setInt(2, randomLeaseID);
                            insertSigns.executeUpdate();
                            insertSigns.close();

                            boolean error2 = false;
                            int min2 = 11111;
                            int max2 = 99999;
                            int randomRentID;
                            int rentID;
                            do {
                                PreparedStatement myStmt = conn.prepareStatement("SELECT rent_id " +
                                "FROM rent");
                                ResultSet myRs = myStmt.executeQuery();
                                randomRentID = GenerateRandom(min2, max2);
                                while (myRs.next()) {
                                    rentID = myRs.getInt("rent_id");
                                    if (rentID == randomRentID) {
                                        error2 = true;
                                    }
                                }
                                myRs.close();
                                myStmt.close();
                            } while (error2 == true);
                            PreparedStatement insertRent = conn.prepareStatement("insert into rent "
                            + "(rent_id) values (?)");
                            insertRent.setInt(1, randomRentID);
                            insertRent.executeUpdate();
                            insertRent.close();

                            System.out.println("A new nonexisting rent ID number has been randomly generated and is now associated with the lease ID for " + full_name + ".");
                            System.out.println("Lease ID: " + randomLeaseID + "\t\tRent ID: " + randomRentID);

                            PreparedStatement insertLeaseRent = conn.prepareStatement("insert into lease_rent "
                            + "(lease_id, rent_id) values (?, ?)");
                            insertLeaseRent.setInt(1, randomLeaseID);
                            insertLeaseRent.setInt(2, randomRentID);
                            insertLeaseRent.executeUpdate();
                            insertLeaseRent.close();

                            PreparedStatement numProperties = conn.prepareStatement("select count(*) "
                            + "from property");
                            ResultSet numPropertiesResult = numProperties.executeQuery();
                            int numPropertiesInt = 0;
                            while (numPropertiesResult.next()) {
                                numPropertiesInt = numPropertiesResult.getInt("count(*)");
                            }
                            numProperties.close();
                            numPropertiesResult.close();
                            PreparedStatement myStmt2 = conn.prepareStatement("SELECT address " +
                            "FROM property");
                            ResultSet myRs2 = myStmt2.executeQuery();
                            String address;
                            String[] addresses = new String[numPropertiesInt];
                            for (int i = 0; i < numPropertiesInt; i++) {
                                myRs2.next();
                                address = myRs2.getString("address");
                                addresses[i] = address;
                                System.out.println((i+1) + ". " + address);
                            }
                            myStmt2.close();
                            myRs2.close();
                            String selection;
                            do {
                                System.out.println("Which property would " + full_name + " like to stay at: ");
                                selection = in.nextLine();
                            } while (isStringInt(selection) == false);
                            int selectionInt = Integer.parseInt(selection);
                            String chosenAddress = addresses[selectionInt - 1];
                            PreparedStatement propertyApartments = conn.prepareStatement("select apt_num from apartment "
                            + "where address = ?");
                            propertyApartments.setString(1, chosenAddress);
                            ResultSet apartments = propertyApartments.executeQuery();
                            int aptNum;
                            int i = 1;
                            System.out.print("\n");
                            while (apartments.next()) {
                                aptNum = apartments.getInt("apt_num");
                                System.out.println(i++ + ". " + aptNum);
                            }
                            propertyApartments.close();
                            apartments.close();
                            int[] apartmentsArray = new int[i-1];
                            int apt;
                            PreparedStatement propertyApartments2 = conn.prepareStatement("select apt_num from apartment "
                            + "where address = ?");
                            propertyApartments2.setString(1, chosenAddress);
                            ResultSet apartments2 = propertyApartments2.executeQuery();
                            for (int j = 0; j < i-1; j++) {
                                apartments2.next();
                                apt = apartments2.getInt("apt_num");
                                apartmentsArray[j] = apt;
                            }
                            propertyApartments2.close();
                            apartments2.close();
                            String selection2;
                            boolean error3 = false;
                            int aptNumber = 0;
                            int desiredAptmt = 0;
                            do {
                                do {
                                    error3 = false;
                                    System.out.println("Which apartment at " + chosenAddress + " would " + full_name + " like to stay at: ");
                                    selection2 = in.nextLine();
                                } while (isStringInt(selection2) == false);
                                int index = Integer.parseInt(selection2);
                                desiredAptmt = apartmentsArray[index - 1];
                                PreparedStatement myStmt = conn.prepareStatement("SELECT apt_num " +
                                "FROM lease_apt");
                                ResultSet myRs = myStmt.executeQuery();
                                while (myRs.next()) {
                                    aptNumber = myRs.getInt("apt_num");
                                    if (aptNumber == desiredAptmt) {
                                        error3 = true;
                                        System.out.println("The apartment number you entered is not vacant.");
                                    }
                                }
                                myRs.close();
                                myStmt.close();
                                
                            } while (error3 == true);

                            PreparedStatement insertLeaseAptmnt = conn.prepareStatement("insert into lease_apt "
                            + "(lease_id, apt_num, address) values (?, ?, ?)");
                            insertLeaseAptmnt.setInt(1, randomLeaseID);
                            insertLeaseAptmnt.setInt(2, desiredAptmt);
                            insertLeaseAptmnt.setString(3, chosenAddress);
                            insertLeaseAptmnt.executeUpdate();
                            insertLeaseAptmnt.close();

                            System.out.println("Apartment number " + desiredAptmt + " at " + chosenAddress + " is now associated with the lease ID for " + full_name + ".");
                            System.out.println("Apartment Number: " + desiredAptmt + "\t\tProperty: " + chosenAddress + "\t\tLease ID: " + randomLeaseID);

                        }
                        else if (optionAsInt3 == 2) {
                            String numMonths;
                            int months;
                            do {
                                System.out.println("What is the notice period in months for the lease:");
                                numMonths = in.nextLine();
                            } while (isStringInt(numMonths) == false);
                            months = Integer.parseInt(numMonths);
                            PreparedStatement insertMonthByMonthLease = conn.prepareStatement("insert into month-by-month_lease "
                            + "(lease_id, notice_period_in_months) values (?, ?)");
                            insertMonthByMonthLease.setInt(1, randomLeaseID);
                            insertMonthByMonthLease.setInt(2, months);
                            insertMonthByMonthLease.executeUpdate();
                            insertMonthByMonthLease.close();

                            String full_name;
                            do {
                                System.out.println("Enter the full name of the individual signing the lease:");
                                full_name = in.nextLine();
                            } while (isString(full_name) == false);
                            String phone;
                            System.out.println("Enter the phone number of the individual signing the lease in the form ###-###-####:");
                            phone = in.nextLine();
                            String email;
                            System.out.println("Enter the email of the individual signing the lease:");
                            email = in.nextLine();
                            String bank;
                            do {
                                System.out.println("Enter the signer's bank name:");
                                bank = in.nextLine();
                            } while (isString(bank) == false);
                            String SSN;
                            System.out.println("Enter the social security number of the individual signing the lease in the form ###-##-####:");
                            SSN = in.nextLine();
                            String DOB;
                            System.out.println("Enter the date of birth of the individual signing the lease in the form mm/dd/yyyy:");
                            DOB = in.nextLine();
                    
                            PreparedStatement insertPerson = conn.prepareStatement("insert into person "
                            + "(full_name, phone_number, email) values (?, ?, ?)");
                            insertPerson.setString(1, full_name);
                            insertPerson.setString(2, phone);
                            insertPerson.setString(3, email);
                            insertPerson.executeUpdate();
                            insertPerson.close();
                            PreparedStatement insertTenant = conn.prepareStatement("insert into tenant "
                            + "(full_name, phone_number, email, bank, social_security_number, date_of_birth) values (?, ?, ?, ?, ?, ?)");
                            insertTenant.setString(1, full_name);
                            insertTenant.setString(2, phone);
                            insertTenant.setString(3, email);
                            insertTenant.setString(4, bank);
                            insertTenant.setString(5, SSN);
                            insertTenant.setString(6, DOB);
                            insertTenant.executeUpdate();
                            insertTenant.close();

                            System.out.println(full_name + "'s lease signature has been recorded.");
                            PreparedStatement insertSigns = conn.prepareStatement("insert into signs "
                            + "(full_name, lease_id) values (?, ?)");
                            insertSigns.setString(1, full_name);
                            insertSigns.setInt(2, randomLeaseID);
                            insertSigns.executeUpdate();
                            insertSigns.close();

                            boolean error2 = false;
                            int min2 = 11111;
                            int max2 = 99999;
                            int randomRentID;
                            int rentID;
                            do {
                                PreparedStatement myStmt = conn.prepareStatement("SELECT rent_id " +
                                "FROM rent");
                                ResultSet myRs = myStmt.executeQuery();
                                randomRentID = GenerateRandom(min2, max2);
                                while (myRs.next()) {
                                    rentID = myRs.getInt("rent_id");
                                    if (rentID == randomRentID) {
                                        error2 = true;
                                    }
                                }
                                myRs.close();
                                myStmt.close();
                            } while (error2 == true);
                            PreparedStatement insertRent = conn.prepareStatement("insert into rent "
                            + "(rent_id) values (?)");
                            insertRent.setInt(1, randomRentID);
                            insertRent.executeUpdate();
                            insertRent.close();

                            System.out.println("A new nonexisting rent ID number has been randomly generated and is now associated with the lease ID for " + full_name + ".");
                            System.out.println("Lease ID: " + randomLeaseID + "\tRent ID: " + randomRentID);

                            PreparedStatement insertLeaseRent = conn.prepareStatement("insert into lease_rent "
                            + "(lease_id, rent_id) values (?, ?)");
                            insertLeaseRent.setInt(1, randomLeaseID);
                            insertLeaseRent.setInt(2, randomRentID);
                            insertLeaseRent.executeUpdate();
                            insertLeaseRent.close();

                            PreparedStatement numProperties = conn.prepareStatement("select count(*) "
                            + "from property");
                            ResultSet numPropertiesResult = numProperties.executeQuery();
                            int numPropertiesInt = 0;
                            while (numPropertiesResult.next()) {
                                numPropertiesInt = numPropertiesResult.getInt("count(*)");
                            }
                            numProperties.close();
                            numPropertiesResult.close();
                            PreparedStatement myStmt2 = conn.prepareStatement("SELECT address " +
                            "FROM property");
                            ResultSet myRs2 = myStmt2.executeQuery();
                            String address;
                            String[] addresses = new String[numPropertiesInt];
                            for (int i = 0; i < numPropertiesInt; i++) {
                                myRs2.next();
                                address = myRs2.getString("address");
                                addresses[i] = address;
                                System.out.println((i+1) + ". " + address);
                            }
                            myStmt2.close();
                            myRs2.close();
                            String selection;
                            do {
                                System.out.println("Which property would " + full_name + " like to stay at: ");
                                selection = in.nextLine();
                            } while (isStringInt(selection) == false);
                            int selectionInt = Integer.parseInt(selection);
                            String chosenAddress = addresses[selectionInt - 1];
                            PreparedStatement propertyApartments = conn.prepareStatement("select apt_num from apartment "
                            + "where address = ?");
                            propertyApartments.setString(1, chosenAddress);
                            ResultSet apartments = propertyApartments.executeQuery();
                            int aptNum;
                            int i = 1;
                            System.out.print("\n");
                            while (apartments.next()) {
                                aptNum = apartments.getInt("apt_num");
                                System.out.println(i++ + ". " + aptNum);
                            }
                            propertyApartments.close();
                            apartments.close();
                            int[] apartmentsArray = new int[i-1];
                            int apt;
                            PreparedStatement propertyApartments2 = conn.prepareStatement("select apt_num from apartment "
                            + "where address = ?");
                            propertyApartments2.setString(1, chosenAddress);
                            ResultSet apartments2 = propertyApartments2.executeQuery();
                            for (int j = 0; j < i-1; j++) {
                                apartments2.next();
                                apt = apartments2.getInt("apt_num");
                                apartmentsArray[j] = apt;
                            }
                            propertyApartments2.close();
                            apartments2.close();
                            String selection2;
                            boolean error3 = false;
                            int aptNumber = 0;
                            int desiredAptmt = 0;
                            do {
                                do {
                                    error3 = false;
                                    System.out.println("Which apartment at " + chosenAddress + " would " + full_name + " like to stay at: ");
                                    selection2 = in.nextLine();
                                } while (isStringInt(selection2) == false);
                                int index = Integer.parseInt(selection2);
                                desiredAptmt = apartmentsArray[index - 1];
                                PreparedStatement myStmt = conn.prepareStatement("SELECT apt_num " +
                                "FROM lease_apt");
                                ResultSet myRs = myStmt.executeQuery();
                                while (myRs.next()) {
                                    aptNumber = myRs.getInt("apt_num");
                                    if (aptNumber == desiredAptmt) {
                                        error3 = true;
                                        System.out.println("The apartment number you entered is not vacant.");
                                    }
                                }
                                myRs.close();
                                myStmt.close();
                                
                            } while (error3 == true);

                            PreparedStatement insertLeaseAptmnt = conn.prepareStatement("insert into lease_apt "
                            + "(lease_id, apt_num, address) values (?, ?, ?)");
                            insertLeaseAptmnt.setInt(1, randomLeaseID);
                            insertLeaseAptmnt.setInt(2, desiredAptmt);
                            insertLeaseAptmnt.setString(3, chosenAddress);
                            insertLeaseAptmnt.executeUpdate();
                            insertLeaseAptmnt.close();

                            System.out.println("Apartment number " + desiredAptmt + " at " + chosenAddress + " is now associated with the lease ID for " + full_name + ".");
                            System.out.println("Apartment Number: " + desiredAptmt + "\t\tProperty: " + chosenAddress + "\t\tLease ID: " + randomLeaseID);

                        }
                    }

                    else if (optionAsInt2 == 3) {

                        String name;
                        do {
                            System.out.println("Enter the full name of the tenant moving out: ");
                            name = in.nextLine();
                        } while (isString(name) == false);
                        String leaseID;
                        int leaseInt;
                        do {
                            System.out.println("Enter the lease ID of the tenant moving out: ");
                            leaseID = in.nextLine();
                        } while (isStringInt(leaseID) == false);
                        leaseInt = Integer.parseInt(leaseID);
                        
                        PreparedStatement deleteLease = conn.prepareStatement("delete from lease "
                        + "where lease_id = ?");
                        deleteLease.setInt(1, leaseInt);
                        deleteLease.executeUpdate();
                        deleteLease.close();
                        PreparedStatement deletePerson = conn.prepareStatement("delete from person "
                        + "where full_name = ?");
                        deletePerson.setString(1, name);
                        deletePerson.executeUpdate();
                        deletePerson.close();
                        System.out.println("The tenant has been successfully moved out and all associated records have been deleted.");
                        
                    }

                } while (optionAsInt2 != 4);
            }

                else if (optionAsInt == 2) {

                    int optionAsInt2;
                    String optionSelected2;
                    do {
                        do {
                            System.out.println("What would you like to do?\n1. Check payment status\n2. Make rental payment\n3. Add pet\n4. Add spouse or roommate\n5. Update personal data\n6. Exit\nPlease select your option below by entering 1, 2, 3, 4, 5 or 6.");
                            optionSelected2 = in.nextLine();
                        } while (isStringInt(optionSelected2) == false);
                        optionAsInt2 = Integer.parseInt(optionSelected2);

                            if (optionAsInt2 == 1) {

                                String full_name;
                                do {
                                    System.out.println("Enter your full name:");
                                    full_name = in.nextLine();
                                } while (isString(full_name) == false);
                                String leaseID;

                                    int leaseIDAsInt;
                                    do {
                                        System.out.println("Enter your lease ID:");
                                        leaseID = in.nextLine();
                                    } while (isStringInt(leaseID) == false);
                                    leaseIDAsInt = Integer.parseInt(leaseID);
                                    PreparedStatement myStmt = conn.prepareStatement("SELECT rent_id " +
                                    "FROM lease_rent WHERE lease_id = ?");
                                    myStmt.setInt(1, leaseIDAsInt);
                                    ResultSet myRs = myStmt.executeQuery();
                                    int rentID = 0;
                                    while (myRs.next()) {
                                        rentID = myRs.getInt("rent_id");
                                    }
                                    System.out.println("Your rent ID associated with your lease ID is: " + rentID);
                                    myRs.close();
                                    myStmt.close();

                                    PreparedStatement numPayments = conn.prepareStatement("select count(*) "
                                    + "from pay_rent where rent_id = ?");
                                    numPayments.setInt(1, rentID);
                                    ResultSet numPaymentsResult = numPayments.executeQuery();
                                    int numPaymentsInt = 0;
                                    while (numPaymentsResult.next()) {
                                        numPaymentsInt = numPaymentsResult.getInt("count(*)");
                                    }
                                    numPayments.close();
                                    numPaymentsResult.close();
                                    PreparedStatement payments = conn.prepareStatement("SELECT payment_id " +
                                    "FROM pay_rent WHERE rent_id = ?");
                                    payments.setInt(1, rentID);
                                    ResultSet paymentsResult = payments.executeQuery();
                                    int payment;
                                    int[] paymentArray = new int[numPaymentsInt];
                                    System.out.println("Your previous rental payments are listed below:");
                                    for (int i = 0; i < numPaymentsInt; i++) {
                                        paymentsResult.next();
                                        payment = paymentsResult.getInt("payment_id");
                                        paymentArray[i] = payment;
                                    }
                                    paymentsResult.close();
                                    payments.close();


                                    for (int i = 0; i < numPaymentsInt; i++) {
                                        PreparedStatement paymentInfo = conn.prepareStatement("select payment_id "
                                        + "from payment where payment_id = ?");
                                        PreparedStatement paymentInfo2 = conn.prepareStatement("select payment_date "
                                        + "from payment where payment_id = ?");
                                        PreparedStatement paymentInfo3 = conn.prepareStatement("select payment_amount "
                                        + "from payment where payment_id = ?");
                                        int paymentElement = paymentArray[i];
                                        paymentInfo.setInt(1, paymentElement);
                                        paymentInfo2.setInt(1, paymentElement);
                                        paymentInfo3.setInt(1, paymentElement);
                                        ResultSet payInfoResult = paymentInfo.executeQuery();
                                        ResultSet payInfoResult2 = paymentInfo2.executeQuery();
                                        ResultSet payInfoResult3 = paymentInfo3.executeQuery();
                                        int payID;
                                        String payDate;
                                        float payAmount;
                                        while (payInfoResult.next() && payInfoResult2.next() && payInfoResult3.next()) {
                                            payID = payInfoResult.getInt("payment_id");
                                            payDate = payInfoResult2.getString("payment_date");
                                            payAmount = payInfoResult3.getFloat("payment_amount");
                                            System.out.println((i+1) + ". Payment ID: " + payID + "\t\tPayment Date: " + payDate + "\t\tPayment Amount: " + payAmount);
                                        }
                                        paymentInfo.close();
                                        payInfoResult.close();
                                        paymentInfo2.close();
                                        payInfoResult2.close();
                                        paymentInfo3.close();
                                        payInfoResult3.close();
                                    }
                            }
                            else if (optionAsInt2 == 2) {
                                
                                String full_name;
                                do {
                                    System.out.println("Enter your full name:");
                                    full_name = in.nextLine();
                                } while (isString(full_name) == false);
                                String leaseID;

                                    int leaseIDAsInt;
                                    do {
                                        System.out.println("Enter your lease ID:");
                                        leaseID = in.nextLine();
                                    } while (isStringInt(leaseID) == false);
                                    leaseIDAsInt = Integer.parseInt(leaseID);
                                    PreparedStatement myStmt = conn.prepareStatement("SELECT rent_id " +
                                    "FROM lease_rent WHERE lease_id = ?");
                                    myStmt.setInt(1, leaseIDAsInt);
                                    ResultSet myRs = myStmt.executeQuery();
                                    int rentID = 0;
                                    while (myRs.next()) {
                                        rentID = myRs.getInt("rent_id");
                                    }
                                    myRs.close();
                                    myStmt.close();
                                    System.out.println("Your rent ID associated with your lease ID is: " + rentID + ".");
                                    int yesNoInt = 0;
                                    String yesNo;

                                    PreparedStatement rentCost2 = conn.prepareStatement("select cost "
                                    + "from rent where rent_id = ?");
                                    rentCost2.setInt(1, rentID);
                                    ResultSet rentCostResult2 = rentCost2.executeQuery();
                                    float cost2 = 0;
                                    while (rentCostResult2.next()) {
                                        cost2 = rentCostResult2.getFloat("cost");
                                    }
                                    rentCost2.close();
                                    rentCostResult2.close();

                                    PreparedStatement dueDate = conn.prepareStatement("select due_date "
                                    + "from rent where rent_id = ?");
                                    dueDate.setInt(1, rentID);
                                    ResultSet dueDateResult = dueDate.executeQuery();
                                    String dueDateString = "";
                                    while (dueDateResult.next()) {
                                        dueDateString = dueDateResult.getString("due_date");
                                    }
                                    dueDateResult.close();
                                    dueDate.close();

                                    System.out.println("You have a rental payment for rent ID " + rentID + " due in the amount of " + cost2 + " due on " + dueDateString + ".");

                                    do {
                                        System.out.println("Would you like to pay your rent now:\n1. Yes\n2. No");
                                        yesNo = in.nextLine();
                                    } while (isStringInt(yesNo) == false);
                                    yesNoInt = Integer.parseInt(yesNo);
                                    if (yesNoInt == 1) {
                                        String payType;
                                        do {
                                            System.out.println("How would you like to pay:\n1. Credit Card\n2. Debit Card\n3. Venmo\n4. Paper Check");
                                            payType = in.nextLine();
                                        } while (isStringInt(payType) == false);
                                        System.out.println("Enter today's date in the form mm/dd/yyyy:");
                                        String date = in.nextLine();

                                        PreparedStatement rentCost = conn.prepareStatement("select cost "
                                        + "from rent where rent_id = ?");
                                        rentCost.setInt(1, rentID);
                                        ResultSet rentCostResult = rentCost.executeQuery();
                                        float cost = 0;
                                        while (rentCostResult.next()) {
                                            cost = rentCostResult.getFloat("cost");
                                        }
                                        rentCost.close();
                                        rentCostResult.close();

                                        System.out.println("The payment for rent ID " + rentID + " in the amount of " + cost + " has been made today on " + date + ". Thank you.");

                                        boolean error2 = false;
                                        int min2 = 11111;
                                        int max2 = 99999;
                                        int randomPaymentID;
                                        int paymentID;
                                        do {
                                            PreparedStatement paymentIDs = conn.prepareStatement("SELECT payment_id " +
                                            "FROM payment");
                                            ResultSet paymIDResultSet = paymentIDs.executeQuery();
                                            randomPaymentID = GenerateRandom(min2, max2);
                                            while (paymIDResultSet.next()) {
                                                paymentID = paymIDResultSet.getInt("payment_id");
                                                if (paymentID == randomPaymentID) {
                                                    error2 = true;
                                                }
                                            }
                                            paymIDResultSet.close();
                                            paymentIDs.close();
                                        } while (error2 == true);
                                        PreparedStatement insertPayment = conn.prepareStatement("insert into payment "
                                        + "(payment_id, payment_date, payment_amount) values (?, ?, ?)");
                                        insertPayment.setInt(1, randomPaymentID);
                                        insertPayment.setString(2, date);
                                        insertPayment.setFloat(3, cost);
                                        insertPayment.executeUpdate();
                                        insertPayment.close();
                                        PreparedStatement insertPayRent = conn.prepareStatement("insert into pay_rent "
                                        + "(payment_id, rent_id) values (?, ?)");
                                        insertPayRent.setInt(1, randomPaymentID);
                                        insertPayRent.setInt(2, rentID);
                                        insertPayRent.executeUpdate();
                                        insertPayRent.close();
                                    }
                            }
                            else if (optionAsInt2 == 3) {
                                
                                String full_name;
                                do {
                                    System.out.println("Enter your full name:");
                                    full_name = in.nextLine();
                                } while (isString(full_name) == false);
                                String species;
                                do {
                                    System.out.println("Enter the species of your pet:");
                                    species = in.nextLine();
                                } while (isString(species) == false);
                                String petName;
                                do {
                                    System.out.println("Enter the name of your pet:");
                                    petName = in.nextLine();
                                } while (isString(petName) == false);
                        
                                PreparedStatement insertPet = conn.prepareStatement("insert into pet "
                                + "(species, pet_name) values (?, ?)");
                                insertPet.setString(1, species);
                                insertPet.setString(2, petName);
                                insertPet.executeUpdate();
                                insertPet.close();

                                PreparedStatement insertPetTen = conn.prepareStatement("insert into ten_pet "
                                + "(full_name, species, pet_name) values (?, ?, ?)");
                                insertPetTen.setString(1, full_name);
                                insertPetTen.setString(2, species);
                                insertPetTen.setString(3, petName);
                                insertPetTen.executeUpdate();
                                insertPetTen.close();

                                System.out.println("Your pet's information has been successfully entered.");

                            }
                            else if (optionAsInt2 == 4) {
                                
                                String full_name;
                                do {
                                    System.out.println("Enter your full name:");
                                    full_name = in.nextLine();
                                } while (isString(full_name) == false);
                                String srName;
                                do {
                                    System.out.println("Enter the name of your spouse or roommate:");
                                    srName = in.nextLine();
                                } while (isString(srName) == false);
                        
                                PreparedStatement insertSR = conn.prepareStatement("insert into spouse_or_roommate "
                                + "(spouse_or_roommate_name) values (?)");
                                insertSR.setString(1, srName);
                                insertSR.executeUpdate();
                                insertSR.close();

                                PreparedStatement insertSRTen = conn.prepareStatement("insert into ten_sr "
                                + "(full_name, spouse_or_roommate_name) values (?, ?)");
                                insertSRTen.setString(1, full_name);
                                insertSRTen.setString(2, srName);
                                insertSRTen.executeUpdate();
                                insertSRTen.close();

                                System.out.println("Your spouse or roommate's information has been successfully entered.");

                            }
                            else if (optionAsInt2 == 5) {

                                String name;
                                do {
                                    System.out.println("Please enter your name:");
                                    name = in.nextLine();
                                } while (isString(name) == false);
                                String selection;
                                int selectionInt;
                                do {
                                    System.out.println("What would you like to update:\n1. Phone Number\n2. Email\n3. Bank\n4. Add prior address");
                                    selection = in.nextLine();
                                } while (isStringInt(selection) == false);
                                selectionInt = Integer.parseInt(selection);
                                if (selectionInt == 1) {
                                    String number;
                                    System.out.println("Enter your new phone number:");
                                    number = in.nextLine();
                                    PreparedStatement updateNum = conn.prepareStatement("UPDATE person "
                                    + "SET phone_number = ? WHERE full_name = ?");
                                    updateNum.setString(1, number);
                                    updateNum.setString(2, name);
                                    updateNum.executeUpdate();
                                    updateNum.close();
                                    PreparedStatement updateNum2 = conn.prepareStatement("UPDATE tenant "
                                    + "SET phone_number = ? WHERE full_name = ?");
                                    updateNum2.setString(1, number);
                                    updateNum2.setString(2, name);
                                    updateNum2.executeUpdate();
                                    updateNum2.close();
                                    System.out.println("Your new phone number has been successfully updated.");
                                }
                                else if (selectionInt == 2) {
                                    String email;
                                    System.out.println("Enter your new email:");
                                    email = in.nextLine();
                                    PreparedStatement updateEmail = conn.prepareStatement("UPDATE person "
                                    + "SET email = ? WHERE full_name = ?");
                                    updateEmail.setString(1, email);
                                    updateEmail.setString(2, name);
                                    updateEmail.executeUpdate();
                                    updateEmail.close();
                                    PreparedStatement updateEmail2 = conn.prepareStatement("UPDATE tenant "
                                    + "SET email = ? WHERE full_name = ?");
                                    updateEmail2.setString(1, email);
                                    updateEmail2.setString(2, name);
                                    updateEmail2.executeUpdate();
                                    updateEmail2.close();
                                    System.out.println("Your new email has been successfully updated.");
                                }
                                else if (selectionInt == 3) {
                                    String bank;
                                    System.out.println("Enter your new bank:");
                                    bank = in.nextLine();
                                    PreparedStatement updateBank = conn.prepareStatement("UPDATE tenant "
                                    + "SET bank = ? WHERE full_name = ?");
                                    updateBank.setString(1, bank);
                                    updateBank.setString(2, name);
                                    updateBank.executeUpdate();
                                    updateBank.close();
                                    System.out.println("Your new bank has been successfully updated.");
                                }
                                else if (selectionInt == 4) {
                                    String address;
                                    System.out.println("Enter your new prior address:");
                                    address = in.nextLine();
                                    PreparedStatement addAddress = conn.prepareStatement("INSERT into prior_address "
                                    + "(prior_address) values (?)");
                                    addAddress.setString(1, address);
                                    addAddress.executeUpdate();
                                    addAddress.close();
                                    PreparedStatement addressTen = conn.prepareStatement("INSERT into ten_address "
                                    + "(full_name, prior_address) values (?, ?)");
                                    addressTen.setString(1, name);
                                    addressTen.setString(2, address);
                                    addressTen.executeUpdate();
                                    addressTen.close();
                                    System.out.println("Your new prior address has been successfully added.");
                                }

                            }

                    } while (optionAsInt2 != 6);
                }

                else if (optionAsInt == 3) {
                    int optionAsInt3;
                    do {
                        String optionSelected3;
                        do {
                            System.out.println("What would you like to do?\n1. Add a new property along with information pertaining to it\n2. Exit\nPlease select your option below by entering 1 or 2.");
                            optionSelected3 = in.nextLine();
                        } while (isStringInt(optionSelected3) == false);
                        optionAsInt3 = Integer.parseInt(optionSelected3);
                        if (optionAsInt3 == 1) {
                            System.out.println("Enter the address of the new property:");
                            String address = in.nextLine();
                            String parkingCharge;
                            int chargeInt;
                            do {
                                System.out.println("Enter the monthly parking charge for the new property:");
                                parkingCharge = in.nextLine();
                            } while (isStringInt(parkingCharge) == false);
                            chargeInt = Integer.parseInt(parkingCharge);
                            PreparedStatement property = conn.prepareStatement("INSERT into property "
                            + "(address, monthly_parking_charge) values (?, ?)");
                            property.setString(1, address);
                            property.setInt(2, chargeInt);
                            property.executeUpdate();
                            property.close();
                            System.out.println("The new property has been successfully added to the system.");
                        }
                    } while (optionAsInt3 != 2);
                }

                } while (optionAsInt != 4); 

                conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
                System.out.println("[Error]: Connect error. Re-enter login data:");
            }
        } while (conn == null);
    }
}