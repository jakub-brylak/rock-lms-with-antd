import { useEffect, useState, type Key } from 'react'
import { CoursesApi } from './api/apis/CoursesApi'
import type { CourseDto } from './api/models/CourseDto'
import { Table } from 'antd'
import type { TableColumnsType } from 'antd';
import './App.css'

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
];

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
        <Table<CourseDto>
          columns={columns}
          dataSource={courses}
          rowKey="id"
          showSorterTooltip={{ target: 'sorter-icon' }}
        />
      </div>
    </>
  )
}

export default App
