import { useState, useEffect } from 'react'
import { Form, Input, InputNumber, Button, Card, message, Spin } from 'antd'
import { useNavigate, useParams } from 'react-router-dom'
import { CoursesApi } from '../api/apis/CoursesApi'
import type { CourseUpdateRequest } from '../api/models/CourseUpdateRequest'
import type { CourseDto } from '../api/models/CourseDto'

interface CourseFormData {
  title: string
  description?: string
  duration?: number
}

export function EditCourse() {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [initialLoading, setInitialLoading] = useState(true)
  const [course, setCourse] = useState<CourseDto | null>(null)
  const navigate = useNavigate()
  const { id } = useParams<{ id: string }>()

  useEffect(() => {
    const loadCourse = async () => {
      if (!id) {
        message.error('Course ID is required')
        navigate('/')
        return
      }

      try {
        const courseData = await new CoursesApi().findCourseById({ id: parseInt(id) })
        setCourse(courseData)
        form.setFieldsValue({
          title: courseData.title,
          description: courseData.description,
          duration: courseData.duration,
        })
      } catch (error) {
        message.error('Failed to load course')
        navigate('/')
      } finally {
        setInitialLoading(false)
      }
    }

    loadCourse()
  }, [id, form, navigate])

  const onFinish = async (values: CourseFormData) => {
    if (!id || !course) return

    setLoading(true)
    try {
      const request: CourseUpdateRequest = {
        title: values.title,
        description: values.description,
        duration: values.duration ?? undefined,
      }
      
      await new CoursesApi().updateCourse({ 
        id: parseInt(id), 
        courseUpdateRequest: request 
      })
      message.success('Course updated successfully!')
      navigate('/')
    } catch (error) {
      message.error('Failed to update course')
      console.error('Error updating course:', error)
    } finally {
      setLoading(false)
    }
  }

  if (initialLoading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', padding: '50px' }}>
        <Spin size="large" />
      </div>
    )
  }

  if (!course) {
    return (
      <div style={{ textAlign: 'center', padding: '50px' }}>
        <h2>Course not found</h2>
        <Button onClick={() => navigate('/')}>Go Back</Button>
      </div>
    )
  }

  return (
    <div style={{ maxWidth: 600, margin: '0 auto', padding: '24px 0' }}>
      <Card title={`Edit Course: ${course.title}`}>
        <Form
          form={form}
          layout="vertical"
          onFinish={onFinish}
          autoComplete="off"
        >
          <Form.Item
            label="Title"
            name="title"
            rules={[{ required: true, message: 'Please input the course title!' }]}
          >
            <Input placeholder="Enter course title" />
          </Form.Item>

          <Form.Item
            label="Description"
            name="description"
          >
            <Input.TextArea 
              rows={4} 
              placeholder="Enter course description" 
            />
          </Form.Item>

          <Form.Item
            label="Duration (minutes)"
            name="duration"
            rules={[{ type: 'number', min: 1, message: 'Duration must be at least 1 minute' }]}
          >
            <InputNumber 
              placeholder="Enter duration in minutes"
              style={{ width: '100%' }}
              min={1}
            />
          </Form.Item>

          <Form.Item style={{ marginBottom: 0 }}>
            <div style={{ display: 'flex', gap: 8 }}>
              <Button type="primary" htmlType="submit" loading={loading}>
                Update Course
              </Button>
              <Button onClick={() => navigate('/')}>
                Cancel
              </Button>
            </div>
          </Form.Item>
        </Form>
      </Card>
    </div>
  )
}
