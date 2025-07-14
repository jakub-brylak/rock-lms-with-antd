import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import { CourseList } from './components/CourseList'
import { AddCourse } from './components/AddCourse'
import { EditCourse } from './components/EditCourse'
import './App.css'

function App() {
  return (
    <Router>
      <div style={{ padding: '24px' }}>
        <Routes>
          <Route path="/" element={<CourseList />} />
          <Route path="/add" element={<AddCourse />} />
          <Route path="/edit/:id" element={<EditCourse />} />
        </Routes>
      </div>
    </Router>
  )
}

export default App
