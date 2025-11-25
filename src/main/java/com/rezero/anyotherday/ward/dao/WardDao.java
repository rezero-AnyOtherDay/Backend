package com.rezero.anyotherday.ward.dao;

import com.rezero.anyotherday.ward.dto.WardDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WardDao {
    // 1) Ward 생성
    int insertWard(WardDto wardDto);

}
