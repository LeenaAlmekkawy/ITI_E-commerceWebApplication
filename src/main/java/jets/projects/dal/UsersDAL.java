package jets.projects.dal;

import jets.projects.beans.AdminBean;
import jets.projects.beans.NormalUserBean;
import jets.projects.dao.UsersDAO;

public class UsersDAL {
    UsersDAO usersDAO = new UsersDAO();
    
    public NormalUserBean userLogin(String email, String password) {
        NormalUserBean user = usersDAO.userLogin(email, password);
        if (user == null) {
            return null;
        }
        user.setPassword("Hidden");
        return user;
    }
    
    public Integer register(NormalUserBean user) {
        Integer generatedID = usersDAO.register(user);
        user.setPassword("Hidden");
        return generatedID;
    }
    
    public AdminBean adminLogin(int userID, String password) {
        AdminBean admin = usersDAO.adminLogin(userID, password);
        if (admin == null) {
            return null;
        }
        
        admin.setPassword("Hidden");
        return admin;
    }
}
