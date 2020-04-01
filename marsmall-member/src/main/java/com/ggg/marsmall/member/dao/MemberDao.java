package com.ggg.marsmall.member.dao;

import com.ggg.marsmall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author guguanggao
 * @email ggg1169262115@163.com
 * @date 2020-04-01 11:22:37
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
