package com.jbs.rocklms.delegate;

import com.jbs.rocklms.api.CoursesApiDelegate;
import com.jbs.rocklms.entity.Course;
import com.jbs.rocklms.mapper.CourseMapper;
import com.jbs.rocklms.model.CourseCreateRequest;
import com.jbs.rocklms.model.CourseDto;
import com.jbs.rocklms.model.CourseUpdateRequest;
import com.jbs.rocklms.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CoursesApiDelegateImpl implements CoursesApiDelegate {
    
    private final CourseService courseService;
    private final CourseMapper courseMapper;
    
    @Autowired
    public CoursesApiDelegateImpl(CourseService courseService, CourseMapper courseMapper) {
        this.courseService = courseService;
        this.courseMapper = courseMapper;
    }
    
    @Override
    public ResponseEntity<List<CourseDto>> findAllCourses(String status) {
        Course.CourseStatus courseStatus = courseMapper.toEntityStatus(status);
        List<Course> courses = courseService.getAllCourses(courseStatus);
        List<CourseDto> courseDtos = courses.stream()
                .map(courseMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(courseDtos);
    }
    
    @Override
    public ResponseEntity<CourseDto> createCourse(CourseCreateRequest request) {
        Course course = courseService.createCourse(
                request.getTitle(),
                request.getDescription(),
                request.getDuration()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(courseMapper.toDto(course));
    }
    
    @Override
    public ResponseEntity<CourseDto> findCourseById(Integer id) {
        return courseService.getCourseById(id.longValue())
                .map(course -> ResponseEntity.ok(courseMapper.toDto(course)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Override
    public ResponseEntity<CourseDto> updateCourse(Integer id, CourseUpdateRequest request) {
        try {
            Course course = courseService.updateCourse(
                    id.longValue(),
                    request.getTitle(),
                    request.getDescription(),
                    request.getDuration()
            );
            return ResponseEntity.ok(courseMapper.toDto(course));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Override
    public ResponseEntity<Void> removeCourse(Integer id) {
        courseService.deleteCourse(id.longValue());
        return ResponseEntity.noContent().build();
    }
    
    @Override
    public ResponseEntity<CourseDto> publishCourse(Integer id) {
        try {
            Course course = courseService.publishCourse(id.longValue());
            return ResponseEntity.ok(courseMapper.toDto(course));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Override
    public ResponseEntity<CourseDto> archiveCourse(Integer id) {
        try {
            Course course = courseService.archiveCourse(id.longValue());
            return ResponseEntity.ok(courseMapper.toDto(course));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
