package com.jbs.rocklms.mapper;

import com.jbs.rocklms.entity.Course;
import com.jbs.rocklms.model.CourseDto;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class CourseMapper {
    
    public CourseDto toDto(Course course) {
        CourseDto dto = new CourseDto();
        dto.setId(course.getId().intValue());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setDuration(course.getDuration());
        dto.setStatus(CourseDto.StatusEnum.valueOf(course.getStatus().name()));
        
        if (course.getPublishedAt() != null) {
            dto.setPublishedAt(JsonNullable.of(course.getPublishedAt().atOffset(ZoneOffset.UTC)));
        } else {
            dto.setPublishedAt(JsonNullable.undefined());
        }
        
        return dto;
    }
    
    public Course.CourseStatus toEntityStatus(String status) {
        if (status == null) return null;
        return Course.CourseStatus.valueOf(status);
    }
}
