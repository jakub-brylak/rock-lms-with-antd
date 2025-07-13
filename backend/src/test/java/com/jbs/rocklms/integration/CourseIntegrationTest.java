package com.jbs.rocklms.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jbs.rocklms.entity.Course;
import com.jbs.rocklms.model.CourseCreateRequest;
import com.jbs.rocklms.model.CourseUpdateRequest;
import com.jbs.rocklms.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.transaction.annotation.Transactional;

import java.io.Console;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureWebMvc
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CourseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
    }

    @Nested
    @DisplayName("Course Creation Integration Tests")
    class CourseCreationTests {

        @Test
        @DisplayName("Should create course with valid data")
        void shouldCreateCourseWithValidData() throws Exception {
            // Given
            CourseCreateRequest request = new CourseCreateRequest();
            request.setTitle("Rock History 101");
            request.setDescription("Introduction to rock music history");
            request.setDuration(45);

            // When & Then
            mockMvc.perform(post("/api/courses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.title").value("Rock History 101"))
                    .andExpect(jsonPath("$.description").value("Introduction to rock music history"))
                    .andExpect(jsonPath("$.duration").value(45))
                    .andExpect(jsonPath("$.status").value("DRAFT"))
                    .andExpect(jsonPath("$.publishedAt").value(nullValue()))
                    .andExpect(jsonPath("$.id").isNumber());

            // Verify in database
            assertThat(courseRepository.count()).isEqualTo(1);
            Course savedCourse = courseRepository.findAll().get(0);
            assertThat(savedCourse.getTitle()).isEqualTo("Rock History 101");
            assertThat(savedCourse.getStatus()).isEqualTo(Course.CourseStatus.DRAFT);
            assertThat(savedCourse.getPublishedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("Course Publishing Integration Tests")
    class CoursePublishingTests {

        @Test
        @DisplayName("Should publish valid draft course")
        void shouldPublishValidDraftCourse() throws Exception {
            // Given
            Course course = createAndSaveCourse("Publishable Course", "Description", 60, Course.CourseStatus.DRAFT);

            // When & Then
            mockMvc.perform(post("/api/courses/{id}/publish", course.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(course.getId().intValue()))
                    .andExpect(jsonPath("$.title").value("Publishable Course"))
                    .andExpect(jsonPath("$.status").value("PUBLISHED"))
                    .andExpect(jsonPath("$.publishedAt").isNotEmpty());

            // Verify in database
            Course publishedCourse = courseRepository.findById(course.getId()).orElseThrow();
            assertThat(publishedCourse.getStatus()).isEqualTo(Course.CourseStatus.PUBLISHED);
            assertThat(publishedCourse.getPublishedAt()).isNotNull();
            assertThat(publishedCourse.getPublishedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        }

        @Test
        @DisplayName("Should return bad request when publishing course without title")
        void shouldReturnBadRequestWhenPublishingCourseWithoutTitle() throws Exception {
            // Given
            Course course = new Course();
            course.setTitle("");
            course.setDescription("Some description");
            course.setDuration(30);
            course.setStatus(Course.CourseStatus.DRAFT);
            course = courseRepository.save(course);

            // When & Then
            mockMvc.perform(post("/api/courses/{id}/publish", course.getId()))
                    .andExpect(status().isBadRequest());

            // Verify course remains draft
            Course unchangedCourse = courseRepository.findById(course.getId()).orElseThrow();
            assertThat(unchangedCourse.getStatus()).isEqualTo(Course.CourseStatus.DRAFT);
            assertThat(unchangedCourse.getPublishedAt()).isNull();
        }

        @Test
        @DisplayName("Should return bad request when publishing course with invalid duration")
        void shouldReturnBadRequestWhenPublishingCourseWithInvalidDuration() throws Exception {
            // Given
            Course course = createAndSaveCourse("Valid Title", "Description", null, Course.CourseStatus.DRAFT);

            // When & Then
            mockMvc.perform(post("/api/courses/{id}/publish", course.getId()))
                    .andExpect(status().isBadRequest());

            // Verify course remains draft
            Course unchangedCourse = courseRepository.findById(course.getId()).orElseThrow();
            assertThat(unchangedCourse.getStatus()).isEqualTo(Course.CourseStatus.DRAFT);
        }

        @Test
        @DisplayName("Should return bad request when publishing archived course")
        void shouldReturnBadRequestWhenPublishingArchivedCourse() throws Exception {
            // Given
            Course course = createAndSaveCourse("Archived Course", "Description", 40, Course.CourseStatus.ARCHIVED);

            // When & Then
            mockMvc.perform(post("/api/courses/{id}/publish", course.getId()))
                    .andExpect(status().isBadRequest());

            // Verify course remains archived
            Course unchangedCourse = courseRepository.findById(course.getId()).orElseThrow();
            assertThat(unchangedCourse.getStatus()).isEqualTo(Course.CourseStatus.ARCHIVED);
        }

        @Test
        @DisplayName("Should return not found when publishing non-existent course")
        void shouldReturnNotFoundWhenPublishingNonExistentCourse() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/courses/999/publish"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Course Archiving Integration Tests")
    class CourseArchivingTests {

        @Test
        @DisplayName("Should archive draft course")
        void shouldArchiveDraftCourse() throws Exception {
            // Given
            Course course = createAndSaveCourse("Draft Course", "Description", 35, Course.CourseStatus.DRAFT);

            // When & Then
            mockMvc.perform(post("/api/courses/{id}/archive", course.getId()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(jsonPath("$.id").value(course.getId().intValue()))
                    .andExpect(jsonPath("$.title").value("Draft Course"))
                    .andExpect(jsonPath("$.status").value("ARCHIVED"))
                    .andExpect(jsonPath("$.publishedAt").doesNotExist());

            // Verify in database
            Course archivedCourse = courseRepository.findById(course.getId()).orElseThrow();
            assertThat(archivedCourse.getStatus()).isEqualTo(Course.CourseStatus.ARCHIVED);
            assertThat(archivedCourse.getPublishedAt()).isNull();
        }

        @Test
        @DisplayName("Should archive published course and preserve publishedAt")
        void shouldArchivePublishedCourseAndPreservePublishedAt() throws Exception {
            // Given
            Course course = createAndSaveCourse("Published Course", "Description", 50, Course.CourseStatus.PUBLISHED);
            LocalDateTime publishedAt = LocalDateTime.now().minusDays(2);
            course.setPublishedAt(publishedAt);
            courseRepository.save(course);

            // When & Then
            mockMvc.perform(post("/api/courses/{id}/archive", course.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("ARCHIVED"))
                    .andExpect(jsonPath("$.publishedAt").isNotEmpty());

            // Verify published date is preserved
            Course archivedCourse = courseRepository.findById(course.getId()).orElseThrow();
            assertThat(archivedCourse.getStatus()).isEqualTo(Course.CourseStatus.ARCHIVED);
            assertThat(archivedCourse.getPublishedAt()).isEqualTo(publishedAt);
        }

        @Test
        @DisplayName("Should return not found when archiving non-existent course")
        void shouldReturnNotFoundWhenArchivingNonExistentCourse() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/courses/999/archive"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should prevent updating archived course")
        void shouldPreventUpdatingArchivedCourse() throws Exception {
            // Given
            Course course = createAndSaveCourse("Archived Course", "Description", 30, Course.CourseStatus.ARCHIVED);
            
            CourseUpdateRequest updateRequest = new CourseUpdateRequest();
            updateRequest.setTitle("Updated Title");

            // When & Then
            mockMvc.perform(put("/api/courses/{id}", course.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isBadRequest());

            // Verify course was not updated
            Course unchangedCourse = courseRepository.findById(course.getId()).orElseThrow();
            assertThat(unchangedCourse.getTitle()).isEqualTo("Archived Course");
        }
    }

    @Nested
    @DisplayName("Course Workflow Integration Tests")
    class CourseWorkflowTests {

        @Test
        @DisplayName("Should complete full course lifecycle: create -> publish -> archive")
        void shouldCompleteFullCourseLifecycle() throws Exception {
            // Step 1: Create course
            CourseCreateRequest createRequest = new CourseCreateRequest();
            createRequest.setTitle("Lifecycle Test Course");
            createRequest.setDescription("Testing full lifecycle");
            createRequest.setDuration(60);

            String createResponse = mockMvc.perform(post("/api/courses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value("DRAFT"))
                    .andReturn().getResponse().getContentAsString();

            Integer courseId = objectMapper.readTree(createResponse).get("id").asInt();

            // Step 2: Publish course
            mockMvc.perform(post("/api/courses/{id}/publish", courseId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("PUBLISHED"))
                    .andExpect(jsonPath("$.publishedAt").isNotEmpty());

            // Step 3: Archive course
            mockMvc.perform(post("/api/courses/{id}/archive", courseId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("ARCHIVED"))
                    .andExpect(jsonPath("$.publishedAt").isNotEmpty());

            // Verify final state in database
            Course finalCourse = courseRepository.findById(courseId.longValue()).orElseThrow();
            assertThat(finalCourse.getStatus()).isEqualTo(Course.CourseStatus.ARCHIVED);
            assertThat(finalCourse.getPublishedAt()).isNotNull();
            assertThat(finalCourse.getTitle()).isEqualTo("Lifecycle Test Course");
        }

        @Test
        @DisplayName("Should filter courses by status")
        void shouldFilterCoursesByStatus() throws Exception {
            // Given - Create courses with different statuses
            Course draftCourse = createAndSaveCourse("Draft Course", "Description", 30, Course.CourseStatus.DRAFT);
            Course publishedCourse = createAndSaveCourse("Published Course", "Description", 45, Course.CourseStatus.PUBLISHED);
            Course archivedCourse = createAndSaveCourse("Archived Course", "Description", 60, Course.CourseStatus.ARCHIVED);

            // Test filtering by DRAFT
            mockMvc.perform(get("/api/courses?status=DRAFT"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].title").value("Draft Course"));

            // Test filtering by PUBLISHED
            mockMvc.perform(get("/api/courses?status=PUBLISHED"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].title").value("Published Course"));

            // Test filtering by ARCHIVED
            mockMvc.perform(get("/api/courses?status=ARCHIVED"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].title").value("Archived Course"));

            // Test no filter - should return all courses
            mockMvc.perform(get("/api/courses"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)));
        }
    }

    private Course createAndSaveCourse(String title, String description, Integer duration, Course.CourseStatus status) {
        Course course = new Course();
        course.setTitle(title);
        course.setDescription(description);
        course.setDuration(duration);
        course.setStatus(status);
        return courseRepository.save(course);
    }
}
