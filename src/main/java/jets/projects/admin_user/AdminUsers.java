package jets.projects.admin_user;

import java.util.Arrays;

import jets.projects.beans.UserBean;

public class AdminUsers {

    public static UserBean[] users = {
        new UserBean(1, "John", "Doe", "john.doe@example.com",
        "555-123-4567", "123 Main St, Anytown, CA 12345", "2023-01-15", 3),
        new UserBean(2, "Jane", "Smith", "jane.smith@example.com",
        "555-987-6543", "456 Oak Ave, Somewhere, NY 67890", "2023-01-20", 2),
        new UserBean(3, "Robert", "Johnson", "robert.johnson@example.com",
        "555-456-7890", "789 Pine St, Elsewhere, TX 54321", "2023-02-05", 1),
        new UserBean(4, "Emily", "Davis", "emily.davis@example.com",
        "555-789-0123", "321 Maple Rd, Nowhere, FL 98765", "2023-02-10", 1),
        new UserBean(5, "Michael", "Brown", "michael.brown@example.com",
        "555-321-6547", "654 Cedar Ln, Anywhere, WA 13579", "2023-02-15", 1),
        new UserBean(6, "Leena", "Brown", "leena.brown@example.com",
        "555-321-6547", "654 Cedar Ln, Anywhere, WA 13579", "2023-02-15", 1)
    };

    public static boolean deleteUserById(int id) {
        for (int i = 0; i < users.length; i++) {
            if (users[i].getId() == id) {

                UserBean[] newUsers = new UserBean[users.length - 1];

                System.arraycopy(users, 0, newUsers, 0, i);
                System.arraycopy(users, i + 1, newUsers, i, users.length - i - 1);

                users = newUsers;
                return true;
            }
        }
        return false;
    }

    public static UserBean[] getUsers() {
        return Arrays.copyOf(users, users.length);
    }

    public static void printAllUsers() {
        for (UserBean user : users) {
            System.out.println(user);
        }
    }
}
