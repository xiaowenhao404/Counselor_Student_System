package dao;

import entity.Followup;
import java.util.List;

public interface FollowupDao {
    List<Followup> getFollowupsByQNumber(String qNumber);

    boolean addFollowup(String qNumber, String content, java.time.LocalDateTime time);
}