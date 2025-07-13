import { useEffect, useState } from 'react'
import { CoursesApi } from './api/apis/CoursesApi'
import type { CourseDto } from './api/models/CourseDto'
import './App.css'

function App() {
  const [courses, setCourses] = useState<CourseDto[]>([])

  useEffect(() => {
    new CoursesApi().findAllCourses()
      .then(coursesResult => setCourses(coursesResult))
  }, [])

  return (
    <>
      <div>
        <h1>Rock LMS Courses</h1>
        <ol>
          {courses.map(course => (
            <span key = {course.id}>
              <li >{course.title}</li>
              <ul>
                <li>Description: {course.description}</li>
                <li>Duration: {course.duration}</li>
                <li>Status: {course.status}</li>
              </ul>
            </span>
          ))}
        </ol>
      </div>
    </>
  )
}

export default App
