import { useEffect, useState, type Key } from 'react'
import { CoursesApi } from '../api/apis/CoursesApi'
import type { CourseDto } from '../api/models/CourseDto'
import { Table, Button, Space, message, Tag } from 'antd'
import type { TableColumnsType } from 'antd'
import { useNavigate } from 'react-router-dom'

export function CourseList() {
  const [courses, setCourses] = useState<CourseDto[]>([])
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  const loadCourses = async () => {
    try {
      const coursesResult = await new CoursesApi().findAllCourses()
      setCourses(coursesResult)
    } catch (error) {
      message.error('Failed to load courses')
      console.error('Error loading courses:', error)
    }
  }

  const handleArchive = async (courseId: number) => {
    setLoading(true)
    try {
      await new CoursesApi().archiveCourse({ id: courseId })
      message.success('Course archived successfully!')
      await loadCourses()
    } catch (error) {
      message.error('Failed to archive course')
      console.error('Error archiving course:', error)
    } finally {
      setLoading(false)
    }
  }

  const handlePublish = async (courseId: number, course: CourseDto) => {
    if (!course.title || course.title.trim() === '') {
      message.error('Cannot publish course: title is required')
      return
    }
    if (!course.duration || course.duration < 1) {
      message.error('Cannot publish course: duration must be greater than 0')
      return
    }
    if (course.status === 'ARCHIVED') {
      message.error('Cannot publish archived course')
      return
    }

    setLoading(true)
    try {
      await new CoursesApi().publishCourse({ id: courseId })
      message.success('Course published successfully!')
      await loadCourses()
    } catch (error) {
      message.error('Failed to publish course')
      console.error('Error publishing course:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleEdit = (record: CourseDto) => {
    if (record.status === 'ARCHIVED') {
      message.warning('Cannot edit archived courses')
      return
    }
    navigate(`/edit/${record.id}`)
  }

  const columns: TableColumnsType<CourseDto> = [
    {
      title: 'Title',
      dataIndex: 'title',
      key: 'title',
      sorter: (a: CourseDto, b: CourseDto) => (a.title || '').localeCompare(b.title || ''),
      showSorterTooltip: { target: 'full-header' },
      sortDirections: ['ascend', 'descend'],
    },
    {
      title: 'Description',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true,
    },
    {
      title: 'Duration (min)',
      dataIndex: 'duration',
      key: 'duration',
      sorter: (a: CourseDto, b: CourseDto) => (a.duration || 0) - (b.duration || 0),
      showSorterTooltip: { target: 'full-header' },
      sortDirections: ['ascend', 'descend'],
      width: 130,
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      showSorterTooltip: { target: 'full-header' },
      filters: [
        {
          text: 'PUBLISHED',
          value: 'PUBLISHED',
        },
        {
          text: 'ARCHIVED',
          value: 'ARCHIVED',
        },
        {
          text: 'DRAFT',
          value: 'DRAFT',
        }
      ],
      onFilter: (filterValue: boolean | Key, record: CourseDto) =>  record.status === filterValue,
      sortDirections: ['descend'],
      width: 120,
      render: (status: string) => {
        let color = 'default'
        switch (status) {
          case 'PUBLISHED':
            color = 'green'
            break
          case 'DRAFT':
            color = 'blue'
            break
          case 'ARCHIVED':
            color = 'red'
            break
        }
        return <Tag color={color}>{status}</Tag>
      },
    },
    {
      title: 'Published At',
      dataIndex: 'publishedAt',
      key: 'publishedAt',
      render: (date: Date | null, record: CourseDto) => {
        if (record.status !== 'PUBLISHED') {
          return '-';
        }
        return date ? new Date(date).toLocaleDateString() : '-';
      },
      sorter: (a: CourseDto, b: CourseDto) => {
        const dateA = a.publishedAt ? new Date(a.publishedAt).getTime() : 0;
        const dateB = b.publishedAt ? new Date(b.publishedAt).getTime() : 0;
        return dateA - dateB;
      },
      showSorterTooltip: { target: 'full-header' },
      sortDirections: ['ascend', 'descend'],
      width: 140,
    },
    {
      title: 'Actions',
      key: 'actions',
      width: 250,
      render: (_: any, record: CourseDto) => {
        const canPublish = record.status === 'DRAFT' && 
                          record.title && 
                          record.title.trim() !== '' && 
                          record.duration && 
                          record.duration > 0
        
        return (
          <Space size="small">
            <Button 
              type="link" 
              size="small"
              disabled={record.status === 'ARCHIVED'}
              onClick={() => handleEdit(record)}
            >
              Edit
            </Button>
            <Button 
            type="link" 
            size="small"
            disabled={!canPublish}
            loading={loading}
            onClick={() => handlePublish(record.id!, record)}
            style={{ color: canPublish ? '#52c41a' : undefined }}
            >
            Publish
            </Button>
            <Button 
              type="link" 
              size="small"
              disabled={record.status === 'ARCHIVED'}
              loading={loading}
              onClick={() => handleArchive(record.id!)}
              danger
            >
              Archive
            </Button>
          </Space>
        )
      },
    },
  ]

  useEffect(() => {
    loadCourses()
  }, [])

  return (
    <div>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h1>Rock LMS Courses</h1>
        <Button type="primary" onClick={() => navigate('/add')}>
          Add New Course
        </Button>
      </div>
      <Table<CourseDto>
        columns={columns}
        dataSource={courses}
        rowKey="id"
        showSorterTooltip={{ target: 'sorter-icon' }}
      />
    </div>
  )
}
