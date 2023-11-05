package edu.doudou.NanqiangTakenout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.doudou.NanqiangTakenout.Entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
