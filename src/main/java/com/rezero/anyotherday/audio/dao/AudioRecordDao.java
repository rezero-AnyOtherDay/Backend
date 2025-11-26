package com.rezero.anyotherday.audio.dao;

import com.rezero.anyotherday.audio.dto.AudioRecordDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AudioRecordDao {

    int createRecord(AudioRecordDto record);

    AudioRecordDto getRecordById(@Param("recordId") Integer recordId);

    List<AudioRecordDto> getRecordsByWardId(@Param("wardId") Integer wardId);

    AudioRecordDto getLatestRecordByWardId(@Param("wardId") Integer wardId);

    int updateStatus(@Param("recordId") Integer recordId,
            @Param("status") String status,
            @Param("errorMessage") String errorMessage);
}