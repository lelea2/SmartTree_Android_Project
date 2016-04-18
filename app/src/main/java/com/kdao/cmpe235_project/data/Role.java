package com.kdao.cmpe235_project.data;

public class Role {

    private int roleId;
    private String roleName;
    private static int ADMIN_ROLE = 1;

    public Role() {}

    public Role(int roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
     * Helper function to get roleId
     * @return
     */
    public boolean isAdmin(int roleId) {
        return (roleId == ADMIN_ROLE);
    }
}
