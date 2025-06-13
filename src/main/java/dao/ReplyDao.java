package dao;

import entity.Reply;
import java.util.List;

public interface ReplyDao {
    List<Reply> getRepliesByQNumber(String qNumber);
    boolean addReply(Reply reply);
}