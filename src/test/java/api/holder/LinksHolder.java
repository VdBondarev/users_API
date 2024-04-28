package api.holder;

public interface LinksHolder {
    String DELETE_ALL_USERS_FILE_PATH =
            "classpath:database/delete-all-users-from-database.sql";
    String INSERT_USER_FILE_PATH = "classpath:database/insert-user-to-database.sql";
    String DELETE_ALL_USER_ROLES_FILE_PATH =
            "classpath:database/delete-all-users_roles-from-database.sql";
    String INSERT_ADMIN_ROLES_FILE_PATH =
            "classpath:database/insert-admin-roles-to-user_roles-table.sql";
    String INSERT_FIVE_USERS_FILE_PATH =
            "classpath:database/insert-five-users-to-database.sql";
}
