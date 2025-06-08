package entity;

public class Major {
    private String majorId;
    private String majorName;
    
    public Major() {}
    
    public Major(String majorId, String majorName) {
        this.majorId = majorId;
        this.majorName = majorName;
    }
    
    public String getMajorId() {
        return majorId;
    }
    
    public void setMajorId(String majorId) {
        this.majorId = majorId;
    }
    
    public String getMajorName() {
        return majorName;
    }
    
    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }
    
    @Override
    public String toString() {
        return majorName + " (" + majorId + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Major major = (Major) obj;
        return majorId.equals(major.majorId);
    }
    
    @Override
    public int hashCode() {
        return majorId.hashCode();
    }
} 