package constants;

public enum UserType {
    STUDENT(2),
    TEACHER(1);

    private long position;

    UserType(long position) {
        this.position = position;
    }

    public long value() {
        return position;
    }

    public static UserType getType(Long pos) {
        if (pos == null) return null;
        else if (pos == 1) return TEACHER;
        else if (pos == 2) return STUDENT;
        else return null;
    }
}
