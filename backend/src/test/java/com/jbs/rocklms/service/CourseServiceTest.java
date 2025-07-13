package com.jbs.rocklms.service;

import com.jbs.rocklms.entity.Course;
import com.jbs.rocklms.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    @Nested
    @DisplayName("Publish Course Tests")
    class PublishCourseTests {

        @Test
        @DisplayName("Should successfully publish valid draft course")
        void shouldPublishValidDraftCourse() {
            // Given
            Long courseId = 1L;
            Course course = createValidDraftCourse();
            course.setId(courseId);
            
            when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
            when(courseRepository.save(any(Course.class))).thenReturn(course);

            // When
            Course publishedCourse = courseService.publishCourse(courseId);

            // Then
            assertThat(publishedCourse.getStatus()).isEqualTo(Course.CourseStatus.PUBLISHED);
            assertThat(publishedCourse.getPublishedAt()).isNotNull();
            assertThat(publishedCourse.getPublishedAt()).isBeforeOrEqualTo(LocalDateTime.now());
            
            verify(courseRepository).findById(courseId);
            verify(courseRepository).save(course);
        }

        @Test
        @DisplayName("Should throw exception when course not found")
        void shouldThrowExceptionWhenCourseNotFound() {
            // Given
            Long nonExistentCourseId = 999L;
            when(courseRepository.findById(nonExistentCourseId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> courseService.publishCourse(nonExistentCourseId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Course not found");
            
            verify(courseRepository).findById(nonExistentCourseId);
            verify(courseRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when course title is null")
        void shouldThrowExceptionWhenTitleIsNull() {
            // Given
            Long courseId = 1L;
            Course course = createValidDraftCourse();
            course.setTitle(null);
            
            when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

            // When & Then
            assertThatThrownBy(() -> courseService.publishCourse(courseId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Cannot publish course: title is required");
            
            verify(courseRepository).findById(courseId);
            verify(courseRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when course title is empty")
        void shouldThrowExceptionWhenTitleIsEmpty() {
            // Given
            Long courseId = 1L;
            Course course = createValidDraftCourse();
            course.setTitle("");
            
            when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

            // When & Then
            assertThatThrownBy(() -> courseService.publishCourse(courseId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Cannot publish course: title is required");
        }

        @Test
        @DisplayName("Should throw exception when course title is only whitespace")
        void shouldThrowExceptionWhenTitleIsOnlyWhitespace() {
            // Given
            Long courseId = 1L;
            Course course = createValidDraftCourse();
            course.setTitle("   ");
            
            when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

            // When & Then
            assertThatThrownBy(() -> courseService.publishCourse(courseId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Cannot publish course: title is required");
        }

        @Test
        @DisplayName("Should throw exception when course duration is null")
        void shouldThrowExceptionWhenDurationIsNull() {
            // Given
            Long courseId = 1L;
            Course course = createValidDraftCourse();
            course.setDuration(null);
            
            when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

            // When & Then
            assertThatThrownBy(() -> courseService.publishCourse(courseId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Cannot publish course: duration must be greater than 0");
        }

        @Test
        @DisplayName("Should throw exception when course duration is zero")
        void shouldThrowExceptionWhenDurationIsZero() {
            // Given
            Long courseId = 1L;
            Course course = createValidDraftCourse();
            course.setDuration(0);
            
            when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

            // When & Then
            assertThatThrownBy(() -> courseService.publishCourse(courseId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Cannot publish course: duration must be greater than 0");
        }

        @Test
        @DisplayName("Should throw exception when course duration is negative")
        void shouldThrowExceptionWhenDurationIsNegative() {
            // Given
            Long courseId = 1L;
            Course course = createValidDraftCourse();
            course.setDuration(-5);
            
            when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

            // When & Then
            assertThatThrownBy(() -> courseService.publishCourse(courseId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Cannot publish course: duration must be greater than 0");
        }

        @Test
        @DisplayName("Should throw exception when trying to publish archived course")
        void shouldThrowExceptionWhenPublishingArchivedCourse() {
            // Given
            Long courseId = 1L;
            Course course = createValidDraftCourse();
            course.setStatus(Course.CourseStatus.ARCHIVED);
            
            when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

            // When & Then
            assertThatThrownBy(() -> courseService.publishCourse(courseId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Cannot publish archived course");
        }

        @Test
        @DisplayName("Should successfully republish already published course")
        void shouldRepublishAlreadyPublishedCourse() {
            // Given
            Long courseId = 1L;
            Course course = createValidDraftCourse();
            course.setStatus(Course.CourseStatus.PUBLISHED);
            course.setPublishedAt(LocalDateTime.now().minusDays(1));
            
            when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
            when(courseRepository.save(any(Course.class))).thenReturn(course);

            // When
            Course republishedCourse = courseService.publishCourse(courseId);

            // Then
            assertThat(republishedCourse.getStatus()).isEqualTo(Course.CourseStatus.PUBLISHED);
            assertThat(republishedCourse.getPublishedAt()).isNotNull();
            
            verify(courseRepository).save(course);
        }

        @Test
        @DisplayName("Should validate all fields before publication")
        void shouldValidateAllFieldsBeforePublication() {
            // Given
            Long courseId = 1L;
            Course course = new Course();
            course.setTitle(null);
            course.setDuration(null);
            course.setStatus(Course.CourseStatus.DRAFT);
            
            when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

            // When & Then
            assertThatThrownBy(() -> courseService.publishCourse(courseId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Cannot publish course: title is required");
        }

        @Test
        @DisplayName("Should publish course with minimum valid duration")
        void shouldPublishCourseWithMinimumValidDuration() {
            // Given
            Long courseId = 1L;
            Course course = createValidDraftCourse();
            course.setDuration(1);
            
            when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
            when(courseRepository.save(any(Course.class))).thenReturn(course);

            // When
            Course publishedCourse = courseService.publishCourse(courseId);

            // Then
            assertThat(publishedCourse.getStatus()).isEqualTo(Course.CourseStatus.PUBLISHED);
            assertThat(publishedCourse.getDuration()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should preserve existing publishedAt when republishing")
        void shouldPreservePublishedAtWhenRepublishing() {
            // Given
            Long courseId = 1L;
            Course course = createValidDraftCourse();
            LocalDateTime originalPublishedAt = LocalDateTime.now().minusDays(5);
            course.setStatus(Course.CourseStatus.PUBLISHED);
            course.setPublishedAt(originalPublishedAt);
            
            when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
            when(courseRepository.save(any(Course.class))).thenReturn(course);

            // When
            Course republishedCourse = courseService.publishCourse(courseId);

            // Then
            assertThat(republishedCourse.getStatus()).isEqualTo(Course.CourseStatus.PUBLISHED);
            assertThat(republishedCourse.getPublishedAt()).isAfter(originalPublishedAt);
        }
    }

    @Nested
    @DisplayName("Archive Course Tests")
    class ArchiveCourseTests {

        @Test
        @DisplayName("Should successfully archive draft course")
        void shouldArchiveDraftCourse() {
            // Given
            Long courseId = 1L;
            Course course = createValidDraftCourse();
            course.setId(courseId);
            
            when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
            when(courseRepository.save(any(Course.class))).thenReturn(course);

            // When
            Course archivedCourse = courseService.archiveCourse(courseId);

            // Then
            assertThat(archivedCourse.getStatus()).isEqualTo(Course.CourseStatus.ARCHIVED);
            verify(courseRepository).findById(courseId);
            verify(courseRepository).save(course);
        }

        @Test
        @DisplayName("Should successfully archive published course")
        void shouldArchivePublishedCourse() {
            // Given
            Long courseId = 1L;
            Course course = createValidDraftCourse();
            course.setStatus(Course.CourseStatus.PUBLISHED);
            course.setPublishedAt(LocalDateTime.now().minusDays(1));
            
            when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
            when(courseRepository.save(any(Course.class))).thenReturn(course);

            // When
            Course archivedCourse = courseService.archiveCourse(courseId);

            // Then
            assertThat(archivedCourse.getStatus()).isEqualTo(Course.CourseStatus.ARCHIVED);
            assertThat(archivedCourse.getPublishedAt()).isNotNull(); // Should preserve publishedAt
            verify(courseRepository).save(course);
        }

        @Test
        @DisplayName("Should successfully archive already archived course")
        void shouldArchiveAlreadyArchivedCourse() {
            // Given
            Long courseId = 1L;
            Course course = createValidDraftCourse();
            course.setStatus(Course.CourseStatus.ARCHIVED);
            
            when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
            when(courseRepository.save(any(Course.class))).thenReturn(course);

            // When
            Course archivedCourse = courseService.archiveCourse(courseId);

            // Then
            assertThat(archivedCourse.getStatus()).isEqualTo(Course.CourseStatus.ARCHIVED);
            verify(courseRepository).save(course);
        }

        @Test
        @DisplayName("Should throw exception when course not found for archiving")
        void shouldThrowExceptionWhenCourseNotFoundForArchiving() {
            // Given
            Long nonExistentCourseId = 999L;
            when(courseRepository.findById(nonExistentCourseId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> courseService.archiveCourse(nonExistentCourseId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Course not found");
            
            verify(courseRepository).findById(nonExistentCourseId);
            verify(courseRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should preserve all course data when archiving")
        void shouldPreserveAllCourseDataWhenArchiving() {
            // Given
            Long courseId = 1L;
            Course course = createValidDraftCourse();
            course.setStatus(Course.CourseStatus.PUBLISHED);
            LocalDateTime originalPublishedAt = LocalDateTime.now().minusDays(2);
            course.setPublishedAt(originalPublishedAt);
            
            when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
            when(courseRepository.save(any(Course.class))).thenReturn(course);

            // When
            Course archivedCourse = courseService.archiveCourse(courseId);

            // Then
            assertThat(archivedCourse.getTitle()).isEqualTo("Rock music history");
            assertThat(archivedCourse.getDescription()).isEqualTo("Rock music history from the 1950s to today");
            assertThat(archivedCourse.getDuration()).isEqualTo(40);
            assertThat(archivedCourse.getPublishedAt()).isEqualTo(originalPublishedAt);
            assertThat(archivedCourse.getStatus()).isEqualTo(Course.CourseStatus.ARCHIVED);
        }
    }

    @Nested
    @DisplayName("Update Course Tests")
    class UpdateCourseTests {

        @Test
        @DisplayName("Should successfully update draft course")
        void shouldUpdateDraftCourse() {
            // Given
            Long courseId = 1L;
            Course course = createValidDraftCourse();
            course.setId(courseId);
            
            when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
            when(courseRepository.save(any(Course.class))).thenReturn(course);

            // When
            Course updatedCourse = courseService.updateCourse(courseId, "Updated Title", "Updated Description", 60);

            // Then
            assertThat(updatedCourse.getTitle()).isEqualTo("Updated Title");
            assertThat(updatedCourse.getDescription()).isEqualTo("Updated Description");
            assertThat(updatedCourse.getDuration()).isEqualTo(60);
            verify(courseRepository).save(course);
        }

        @Test
        @DisplayName("Should successfully update published course")
        void shouldUpdatePublishedCourse() {
            // Given
            Long courseId = 1L;
            Course course = createValidDraftCourse();
            course.setStatus(Course.CourseStatus.PUBLISHED);
            course.setPublishedAt(LocalDateTime.now().minusDays(1));
            
            when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
            when(courseRepository.save(any(Course.class))).thenReturn(course);

            // When
            Course updatedCourse = courseService.updateCourse(courseId, "Updated Title", null, null);

            // Then
            assertThat(updatedCourse.getTitle()).isEqualTo("Updated Title");
            assertThat(updatedCourse.getStatus()).isEqualTo(Course.CourseStatus.PUBLISHED);
            verify(courseRepository).save(course);
        }

        @Test
        @DisplayName("Should throw exception when trying to update archived course")
        void shouldThrowExceptionWhenUpdatingArchivedCourse() {
            // Given
            Long courseId = 1L;
            Course course = createValidDraftCourse();
            course.setStatus(Course.CourseStatus.ARCHIVED);
            
            when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

            // When & Then
            assertThatThrownBy(() -> courseService.updateCourse(courseId, "New Title", "New Description", 50))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Cannot edit archived course");
            
            verify(courseRepository).findById(courseId);
            verify(courseRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when course not found for update")
        void shouldThrowExceptionWhenCourseNotFoundForUpdate() {
            // Given
            Long nonExistentCourseId = 999L;
            when(courseRepository.findById(nonExistentCourseId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> courseService.updateCourse(nonExistentCourseId, "Title", "Description", 30))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Course not found");
            
            verify(courseRepository).findById(nonExistentCourseId);
            verify(courseRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should update only provided fields")
        void shouldUpdateOnlyProvidedFields() {
            // Given
            Long courseId = 1L;
            Course course = createValidDraftCourse();
            String originalTitle = course.getTitle();
            String originalDescription = course.getDescription();
            
            when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
            when(courseRepository.save(any(Course.class))).thenReturn(course);

            // When - Update only duration
            Course updatedCourse = courseService.updateCourse(courseId, null, null, 120);

            // Then
            assertThat(updatedCourse.getTitle()).isEqualTo(originalTitle);
            assertThat(updatedCourse.getDescription()).isEqualTo(originalDescription);
            assertThat(updatedCourse.getDuration()).isEqualTo(120);
        }

        @Test
        @DisplayName("Should preserve status and publishedAt when updating")
        void shouldPreserveStatusAndPublishedAtWhenUpdating() {
            // Given
            Long courseId = 1L;
            Course course = createValidDraftCourse();
            course.setStatus(Course.CourseStatus.PUBLISHED);
            LocalDateTime originalPublishedAt = LocalDateTime.now().minusDays(3);
            course.setPublishedAt(originalPublishedAt);
            
            when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
            when(courseRepository.save(any(Course.class))).thenReturn(course);

            // When
            Course updatedCourse = courseService.updateCourse(courseId, "New Title", "New Description", 90);

            // Then
            assertThat(updatedCourse.getStatus()).isEqualTo(Course.CourseStatus.PUBLISHED);
            assertThat(updatedCourse.getPublishedAt()).isEqualTo(originalPublishedAt);
        }
    }

    private Course createValidDraftCourse() {
        Course course = new Course();
        course.setTitle("Rock music history");
        course.setDescription("Rock music history from the 1950s to today");
        course.setDuration(40);
        course.setStatus(Course.CourseStatus.DRAFT);
        return course;
    }
}
