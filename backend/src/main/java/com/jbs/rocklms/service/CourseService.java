package com.jbs.rocklms.service;

import com.jbs.rocklms.entity.Course;
import com.jbs.rocklms.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CourseService {
    
    private final CourseRepository courseRepository;
    
    @Autowired
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }
    
    public List<Course> getAllCourses(Course.CourseStatus status) {
        return courseRepository.findAllWithOptionalStatus(status);
    }
    
    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }
    
    public Course createCourse(String title, String description, Integer duration) {
        Course course = new Course(title, description, duration);
        return courseRepository.save(course);
    }
    
    public Course updateCourse(Long id, String title, String description, Integer duration) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        
        if (!course.canBeEdited()) {
            throw new IllegalStateException("Cannot edit archived course");
        }
        
        if (title != null) course.setTitle(title);
        if (description != null) course.setDescription(description);
        if (duration != null) course.setDuration(duration);
        
        return courseRepository.save(course);
    }
    
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }
    
    public Course publishCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        
        course.publish();
        return courseRepository.save(course);
    }
    
    public Course archiveCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        
        course.archive();
        return courseRepository.save(course);
    }
}
