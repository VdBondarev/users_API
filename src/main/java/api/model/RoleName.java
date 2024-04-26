package api.model;

public enum RoleName {
        USER,
        ADMIN;

    public static RoleName fromString(String value) {
        for (RoleName role : RoleName.values()) {
            if (role.name().equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Can't find a role " + value);
    }

    @Override
    public String toString() {
        return name();
    }
}
