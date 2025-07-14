import { useState } from 'react'
import { Form, Input, InputNumber, Button, Card, message } from 'antd'
import { useNavigate } from 'react-router-dom'
import { CoursesApi } from '../api/apis/CoursesApi'
import type { CourseCreateRequest } from '../api/models/CourseCreateRequest'

interface CourseFormData {
  title: string
  description?: string
  duration: number
}

export function AddCourse() {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  const onFinish = async (values: CourseFormData) => {
    setLoading(true)
    try {
      const request: CourseCreateRequest = {
        title: values.title,
        description: values.description,
        duration: values.duration,
      }
      
      await new CoursesApi().createCourse({ courseCreateRequest: request })
      message.success('Course created successfully!')
      navigate('/')
    } catch (error) {
      message.error('Failed to create course')
      console.error('Error creating course:', error)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={{ maxWidth: 600, margin: '0 auto', padding: '24px 0' }}>
      <Card title="Add New Course">
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
            rules={[
              { required: true, message: 'Please input the duration!' },
              { type: 'number', min: 1, message: 'Duration must be at least 1 minute' }
            ]}
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
                Create Course
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
