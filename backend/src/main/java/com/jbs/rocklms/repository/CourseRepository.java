package com.jbs.rocklms.repository;

import com.jbs.rocklms.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    List<Course> findByStatus(Course.CourseStatus status);
    
    @Query("SELECT c FROM Course c WHERE :status IS NULL OR c.status = :status")
    List<Course> findAllWithOptionalStatus(@Param("status") Course.CourseStatus status);
}
