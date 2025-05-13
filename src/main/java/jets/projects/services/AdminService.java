package jets.projects.services;

import jets.projects.dao.AdminDao;
import jets.projects.entity.Admin;

public class AdminService
{
    private final AdminDao adminDao = new AdminDao();


    public Admin getByUsername(String username)
    {
        return adminDao.findByUsername(username).orElse(null);
    }
}