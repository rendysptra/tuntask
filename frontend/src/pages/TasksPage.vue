<template>
  <div class="dashboard-page">
    <!-- Mascot decorations -->
    <!-- Left side mascots -->
    <div class="mascot-decoration mascot-top-left" style="top: 80px; left: 30px;">
      <FriendMascot5 />
    </div>
    <div class="mascot-decoration mascot-mid-left" style="top: 400px; left: 40px;">
      <FriendMascot1 />
    </div>
    <div class="mascot-decoration mascot-bottom-left" style="top: 750px; left: 30px;">
      <FriendMascot2 />
    </div>
    
    <!-- Right side mascots -->
    <div class="mascot-decoration mascot-top-right" style="top: 100px; right: 30px;">
      <FriendMascot3 />
    </div>
    <div class="mascot-decoration mascot-mid-right" style="top: 420px; right: 40px;">
      <FriendMascot4 />
    </div>
    <div class="mascot-decoration mascot-bottom-right" style="top: 770px; right: 30px;">
      <FriendMascot6 />
    </div>

    <!-- Header section -->
    <header class="dashboard-header">
      <div class="header-content">
        <div class="header-left">
          <div class="workspace-icon">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
              <path d="M15 10.4142L7 18.4142L7 2.41421L15 10.4142Z" stroke="#58745C" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="header-info">
            <h1>Ruang tugas</h1>
            <p>Kebun binatang pribadi</p>
          </div>
        </div>
        
        <div class="header-right">
          <div class="task-counter">
            <div class="counter-icon">
              <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                <circle cx="8" cy="8" r="6" fill="#F4B35A"/>
                <circle cx="10" cy="10" r="2" fill="white"/>
                <circle cx="14" cy="10" r="2" fill="white"/>
                <circle cx="10" cy="14" r="2" fill="#F4B35A"/>
              </svg>
            </div>
            <span>{{ totalTasks }} tugas hari ini</span>
          </div>
          <button @click="logout" class="logout-btn">Logout</button>
        </div>
      </div>
    </header>

    <!-- Main content -->
    <main class="dashboard-content">
      <!-- Date and greeting -->
      <section class="greeting-section">
        <div class="date-info">
          <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
            <rect x="3" y="2" width="10" height="12" rx="1" stroke="#737B73" stroke-width="1.5"/>
            <line x1="6" y1="1" x2="6" y2="3" stroke="#737B73" stroke-width="1.5" stroke-linecap="round"/>
            <line x1="10" y1="1" x2="10" y2="3" stroke="#737B73" stroke-width="1.5" stroke-linecap="round"/>
            <line x1="3" y1="5" x2="13" y2="5" stroke="#737B73" stroke-width="1.5"/>
          </svg>
          <span>{{ currentDate }}</span>
        </div>
        <div class="greeting-text">
          <h2>Tugas hari ini</h2>
          <p>Satu tugas, satu langkah. Yuk, mulai dari yang paling penting.</p>
        </div>
      </section>

      <!-- Tasks card -->
      <div class="tasks-card">
        <!-- Add task section -->
        <div class="add-task-section">
          <label class="section-label">Tambah tugas baru</label>
          <div class="add-task-input-group">
            <div class="form-group">
              <label class="input-label">Judul</label>
              <input
                v-model="newTask.title"
                type="text"
                placeholder="Apa yang ingin kamu kerjakan?"
                class="task-input"
              />
            </div>
            <div class="form-group">
              <label class="input-label">Deskripsi</label>
              <textarea
                v-model="newTask.description"
                placeholder="Deskripsi (opsional)"
                class="task-description-input"
                rows="2"
              ></textarea>
            </div>
            <div class="add-task-controls">
              <div class="form-group">
                <label class="input-label">Deadline</label>
                <input
                  v-model="newTask.dueDate"
                  type="date"
                  class="task-date-input"
                  :min="minDate"
                />
              </div>
              <div class="form-group">
                <label class="input-label">Prioritas</label>
                <select v-model="newTask.priority" class="task-priority-select">
                  <option value="LOW">Rendah</option>
                  <option value="MEDIUM">Sedang</option>
                  <option value="HIGH">Tinggi</option>
                </select>
              </div>
              <button @click="addTask" class="add-btn">
                <svg width="18" height="18" viewBox="0 0 18 18" fill="none">
                  <path d="M9 3V15M3 9H15" stroke="white" stroke-width="1.8" stroke-linecap="round"/>
                </svg>
                <span>Tambah</span>
              </button>
            </div>
          </div>
        </div>

        <!-- Filter tabs -->
        <div class="filter-tabs">
          <button
            v-for="filter in filters"
            :key="filter.key"
            :class="['filter-btn', { active: activeFilter === filter.key }]"
            @click="activeFilter = filter.key"
          >
            <span class="filter-label">{{ filter.label }}</span>
            <span class="filter-count">{{ getFilterCount(filter.key) }}</span>
          </button>
          <div class="filter-total">{{ filteredTasks.length }} tugas</div>
        </div>

        <!-- Tasks list -->
        <div class="tasks-list">
          <div v-if="loading" class="state-message">Loading...</div>
          <div v-else-if="filteredTasks.length === 0" class="state-message">Tidak ada tugas</div>
          <template v-else>
            <div
              v-for="task in filteredTasks"
              :key="task.id"
              :class="['task-item', { completed: task.status === 'DONE' }]"
            >
              <input
                type="checkbox"
                :checked="task.status === 'DONE'"
                @change="updateTaskStatus(task.id, $event.target.checked ? 'DONE' : 'TODO')"
                class="task-checkbox"
              />
              <div class="task-content">
                <div class="task-header">
                  <span class="task-text">{{ task.title }}</span>
                  <span :class="['task-priority', `priority-${task.priority}`]">
                    {{ getPriorityLabel(task.priority) }}
                  </span>
                </div>
                <div v-if="task.description" class="task-description">{{ task.description }}</div>
                <div class="task-meta">
                  <span v-if="task.dueDate" class="task-deadline">
                    <svg width="12" height="12" viewBox="0 0 16 16" fill="none">
                      <rect x="3" y="2" width="10" height="12" rx="1" stroke="currentColor" stroke-width="1.5"/>
                      <line x1="6" y1="1" x2="6" y2="3" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                      <line x1="10" y1="1" x2="10" y2="3" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                      <line x1="3" y1="5" x2="13" y2="5" stroke="currentColor" stroke-width="1.5"/>
                    </svg>
                    {{ formatDate(task.dueDate) }}
                  </span>
                  <span :class="['task-status', getStatusClass(task.status)]">
                    {{ getStatusLabel(task.status) }}
                  </span>
                </div>
              </div>
              <button
                @click="openEditModal(task)"
                class="edit-btn"
              >
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                  <path d="M11.5 2.5L13.5 4.5M2 14H4L12 6L10 4L2 12V14Z" stroke="#58745C" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </button>
              <button
                @click="deleteTask(task.id)"
                class="delete-btn"
              >
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                  <path d="M3 4H13M6 7V12M10 7V12M4 4L5 13C5 13.5 5.5 14 6 14H10C10.5 14 11 13.5 11 13L12 4" stroke="#949A93" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </button>
            </div>
          </template>
        </div>

        <!-- Tasks remaining -->
        <div v-if="filteredTasks.length > 0" class="tasks-remaining">
          <div class="remaining-info">
            <span class="dot"></span>
            <span><strong>{{ activeTasks.length }} tugas</strong> tersisa</span>
          </div>
          <button @click="clearCompleted" class="clear-completed-btn">Hapus yang selesai</button>
        </div>
      </div>

      <!-- Info footer -->
      <div class="info-footer">
        <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
          <path d="M7 1C3.68 1 1 3.68 1 7C1 10.32 3.68 13 7 13C10.32 13 13 10.32 13 7" stroke="#737B73" stroke-width="1.5" stroke-linecap="round"/>
          <path d="M7 4V7L9.5 8.5" stroke="#737B73" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <span>Klik kotak di samping tugas untuk menandainya selesai.</span>
      </div>
    </main>

    <!-- Edit Modal -->
    <div v-if="showEditModal" class="modal-overlay" @click="closeEditModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>Edit Tugas</h3>
          <button @click="closeEditModal" class="modal-close">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
              <path d="M15 5L5 15M5 5L15 15" stroke="#737B73" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label class="input-label">Judul</label>
            <input
              v-model="editingTask.title"
              type="text"
              placeholder="Judul tugas"
              class="task-input"
            />
          </div>
          <div class="form-group">
            <label class="input-label">Deskripsi</label>
            <textarea
              v-model="editingTask.description"
              placeholder="Deskripsi (opsional)"
              class="task-description-input"
              rows="2"
            ></textarea>
          </div>
          <div class="form-group">
            <label class="input-label">Deadline</label>
            <input
              v-model="editingTask.dueDate"
              type="date"
              class="task-date-input"
              :min="minDate"
            />
          </div>
          <div class="form-group">
            <label class="input-label">Prioritas</label>
            <select v-model="editingTask.priority" class="task-priority-select">
              <option value="LOW">Rendah</option>
              <option value="MEDIUM">Sedang</option>
              <option value="HIGH">Tinggi</option>
            </select>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeEditModal" class="btn-cancel">Batal</button>
          <button @click="saveEditTask" class="btn-save">Simpan</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/authStore'
import { taskApi } from '../services/taskApi'
import { HappyMascot, FriendMascot1, FriendMascot2, FriendMascot3, FriendMascot4, FriendMascot5, FriendMascot6 } from '../components/mascots'

const router = useRouter()
const authStore = useAuthStore()

const tasks = ref([])
const loading = ref(false)
const newTask = ref({ title: '', description: '', dueDate: '', priority: 'MEDIUM' })
const activeFilter = ref('all')
const editingTask = ref(null)
const showEditModal = ref(false)

const minDate = computed(() => {
  const today = new Date()
  return today.toISOString().split('T')[0]
})

const filters = [
  { key: 'all', label: 'Semua' },
  { key: 'active', label: 'Aktif' },
  { key: 'completed', label: 'Selesai' }
]

const currentDate = computed(() => {
  const now = new Date()
  const days = ['Minggu', 'Senin', 'Selasa', 'Rabu', 'Kamis', 'Jumat', 'Sabtu']
  const months = ['Januari', 'Februari', 'Maret', 'April', 'Mei', 'Juni', 'Juli', 'Agustus', 'September', 'Oktober', 'November', 'Desember']
  return `${days[now.getDay()]}, ${now.getDate()} ${months[now.getMonth()]} ${now.getFullYear()}`
})

const totalTasks = computed(() => tasks.value.length)

const activeTasks = computed(() => tasks.value.filter(t => t.status !== 'DONE'))

const filteredTasks = computed(() => {
  switch (activeFilter.value) {
    case 'active':
      return tasks.value.filter(t => t.status !== 'DONE')
    case 'completed':
      return tasks.value.filter(t => t.status === 'DONE')
    default:
      return tasks.value
  }
})

const getFilterCount = (filterKey) => {
  switch (filterKey) {
    case 'active':
      return tasks.value.filter(t => t.status !== 'DONE').length
    case 'completed':
      return tasks.value.filter(t => t.status === 'DONE').length
    default:
      return tasks.value.length
  }
}

const getStatusClass = (status) => {
  if (status === 'DONE') return 'completed'
  return 'pending'
}

const getStatusLabel = (status) => {
  return status === 'DONE' ? 'Selesai' : 'Belum selesai'
}

const getPriorityLabel = (priority) => {
  const labels = {
    'LOW': 'Rendah',
    'MEDIUM': 'Sedang',
    'HIGH': 'Tinggi'
  }
  return labels[priority] || priority
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const now = new Date()
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
  const tomorrow = new Date(today)
  tomorrow.setDate(tomorrow.getDate() + 1)
  const taskDate = new Date(date.getFullYear(), date.getMonth(), date.getDate())
  
  if (taskDate.getTime() === today.getTime()) return 'Hari ini'
  if (taskDate.getTime() === tomorrow.getTime()) return 'Besok'
  
  const months = ['Jan', 'Feb', 'Mar', 'Apr', 'Mei', 'Jun', 'Jul', 'Ags', 'Sep', 'Okt', 'Nov', 'Des']
  return `${date.getDate()} ${months[date.getMonth()]}`
}

const fetchTasks = async () => {
  loading.value = true
  try {
    const response = await taskApi.getTasks()
    tasks.value = response.data || []
  } catch (error) {
    console.error('Error fetching tasks:', error)
  } finally {
    loading.value = false
  }
}

const addTask = async () => {
  if (!newTask.value.title.trim()) return

  try {
    await taskApi.createTask({
      title: newTask.value.title,
      description: newTask.value.description || null,
      dueDate: newTask.value.dueDate || null,
      priority: newTask.value.priority,
      status: 'TODO'
    })
    newTask.value = { title: '', description: '', dueDate: '', priority: 'MEDIUM' }
    fetchTasks()
  } catch (error) {
    console.error('Error adding task:', error)
  }
}

const updateTaskStatus = async (id, status) => {
  try {
    const task = tasks.value.find(t => t.id === id)
    await taskApi.updateTask(id, { ...task, status })
    const taskIndex = tasks.value.findIndex(t => t.id === id)
    if (taskIndex !== -1) {
      tasks.value[taskIndex].status = status
    }
  } catch (error) {
    console.error('Error updating task:', error)
  }
}

const deleteTask = async (id) => {
  if (confirm('Hapus tugas ini?')) {
    try {
      await taskApi.deleteTask(id)
      fetchTasks()
    } catch (error) {
      console.error('Error deleting task:', error)
    }
  }
}

const clearCompleted = async () => {
  const completedTasks = tasks.value.filter(t => t.status === 'DONE')
  for (const task of completedTasks) {
    await deleteTask(task.id)
  }
}

const openEditModal = (task) => {
  editingTask.value = { ...task }
  showEditModal.value = true
}

const closeEditModal = () => {
  editingTask.value = null
  showEditModal.value = false
}

const saveEditTask = async () => {
  if (!editingTask.value.title.trim()) return
  
  try {
    await taskApi.updateTask(editingTask.value.id, {
      title: editingTask.value.title,
      description: editingTask.value.description || null,
      dueDate: editingTask.value.dueDate || null,
      priority: editingTask.value.priority,
      status: editingTask.value.status
    })
    closeEditModal()
    fetchTasks()
  } catch (error) {
    console.error('Error saving task:', error)
  }
}

const logout = () => {
  authStore.logout()
  router.push('/login')
}

onMounted(() => {
  fetchTasks()
})
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.dashboard-page {
  width: 100%;
  min-height: 100dvh;
  background: #FFF7E8;
  position: relative;
  overflow-y: auto;
  overflow-x: hidden;
}

/* Mascot decorations */
.mascot-decoration {
  position: absolute;
  width: 190px;
  height: 190px;
  opacity: 0.6;
  z-index: 0;
}

/* Header */
.dashboard-header {
  width: 100%;
  padding: 0 64px;
  height: 88px;
  background: #FFF1D6;
  border-bottom: 1px solid #E7DCC8;
  display: flex;
  align-items: center;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: clamp(1rem, 3vw, 2rem);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.workspace-icon {
  width: 44px;
  height: 44px;
  background: #D8EEDB;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.header-info h1 {
  font-size: 18px;
  font-weight: 700;
  color: #303A33;
  margin: 0;
}

.header-info p {
  font-size: 12px;
  font-weight: 500;
  color: #C96A2F;
  margin: 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.task-counter {
  display: flex;
  align-items: center;
  gap: 10px;
  background: white;
  padding: 10px 14px;
  border-radius: 16px;
  border: 1px solid #E7DCC8;
  color: #303A33;
  font-size: 13px;
  font-weight: 600;
}

.counter-icon {
  width: 28px;
  height: 28px;
  background: #FFF1E8;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.logout-btn {
  padding: 8px 12px;
  background: #ef4444;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  font-size: 13px;
  transition: all 0.2s ease;
  min-height: 40px;
}

.logout-btn:hover {
  background: #dc2626;
}

/* Main content */
.dashboard-content {
  padding: 56px 64px 80px 64px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
}

/* Greeting section */
.greeting-section {
  width: 100%;
  max-width: 800px;
}

.date-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #737B73;
  font-size: 13px;
  margin-bottom: 12px;
}

.greeting-text h2 {
  font-size: 36px;
  font-weight: 700;
  color: #303A33;
  margin: 0 0 8px 0;
  line-height: 1.2;
}

.greeting-text p {
  font-size: 15px;
  font-weight: 400;
  color: #737B73;
  line-height: 1.5;
  margin: 0;
}

/* Tasks card */
.tasks-card {
  width: 100%;
  max-width: 800px;
  background: white;
  border-radius: 24px;
  border: 1px solid #E7DCC8;
  box-shadow: 0px 10px 24px rgba(0, 0, 0, 0.07);
  overflow: hidden;
}

/* Add task section */
.add-task-section {
  padding: 24px;
  border-bottom: 1px solid #E7DCC8;
}

.add-task-section label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: #303A33;
  margin-bottom: 10px;
}

.section-label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: #303A33;
  margin-bottom: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.input-label {
  display: block;
  font-size: 12px;
  font-weight: 600;
  color: #58745C;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.add-task-input-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
  align-items: stretch;
}

.task-input {
  flex: 1;
  height: 50px;
  padding: 0 16px;
  background: #FFF8E8;
  border: 1px solid #E7DCC8;
  border-radius: 14px;
  font-size: 15px;
  color: #303A33;
  transition: all 0.2s ease;
}

.task-input::placeholder {
  color: #949A93;
}

.task-input:focus {
  outline: none;
  border-color: #58745C;
  background: white;
}

.task-description-input {
  width: 100%;
  height: auto;
  min-height: 60px;
  padding: 12px 16px;
  background: #FFF8E8;
  border: 1px solid #E7DCC8;
  border-radius: 14px;
  font-size: 13px;
  color: #303A33;
  font-family: inherit;
  resize: vertical;
  transition: all 0.2s ease;
}

.task-description-input::placeholder {
  color: #949A93;
}

.task-description-input:focus {
  outline: none;
  border-color: #58745C;
  background: white;
}

.add-task-controls {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.add-task-controls .form-group {
  flex: 1;
  min-width: 100px;
}

.task-date-input,
.task-priority-select {
  height: 50px;
  padding: 0 12px;
  background: #FFF8E8;
  border: 1px solid #E7DCC8;
  border-radius: 14px;
  font-size: 13px;
  color: #303A33;
  transition: all 0.2s ease;
  font-family: inherit;
}

.task-date-input:focus,
.task-priority-select:focus {
  outline: none;
  border-color: #58745C;
  background: white;
}

.add-btn {
  height: 50px;
  padding: 0 20px;
  background: #58745C;
  color: white;
  border: none;
  border-radius: 14px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  white-space: nowrap;
  transition: all 0.2s ease;
  box-shadow: 0px 6px 14px rgba(88, 116, 92, 0.20);
}

.add-btn:hover {
  background: #4a6250;
  transform: translateY(-2px);
  box-shadow: 0px 8px 16px rgba(88, 116, 92, 0.25);
}

/* Filter tabs */
.filter-tabs {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 24px 16px;
  border-bottom: 1px solid #E7DCC8;
  overflow-x: auto;
}

.filter-btn {
  padding: 9px 14px;
  background: transparent;
  border: none;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  white-space: nowrap;
  transition: all 0.2s ease;
  color: #58745C;
}

.filter-btn.active {
  background: #D8EEDB;
  color: #58745C;
}

.filter-btn:nth-child(2) {
  color: #C96A2F;
}

.filter-btn:nth-child(2).active {
  background: #FFF1E8;
}

.filter-btn:nth-child(3) {
  color: #2F7BB6;
}

.filter-btn:nth-child(3).active {
  background: #E8F3FF;
}

.filter-count {
  font-weight: 400;
  font-size: 12px;
}

.filter-total {
  margin-left: auto;
  font-size: 13px;
  color: #737B73;
}

/* Tasks list */
.tasks-list {
  display: flex;
  flex-direction: column;
  max-height: 400px;
  overflow-y: auto;
}

.task-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 24px;
  border-bottom: 1px solid #E7DCC8;
  background: #FFF8E8;
  transition: all 0.2s ease;
  min-height: auto;
}

.task-item.completed {
  background: #E8F3FF;
}

.task-checkbox {
  width: 22px;
  height: 22px;
  cursor: pointer;
  flex-shrink: 0;
  appearance: none;
  background: #FFF8E8;
  border: 1.5px solid #C7CEC2;
  border-radius: 6px;
  transition: all 0.2s ease;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 2px;
}

.task-checkbox::before {
  content: '';
  width: 8px;
  height: 8px;
  background: #F4B35A;
  border-radius: 50%;
  display: block;
}

.task-item.completed .task-checkbox {
  background: #2F7BB6;
  border-color: #2F7BB6;
}

.task-item.completed .task-checkbox::before {
  content: '✓';
  width: auto;
  height: auto;
  background: none;
  color: white;
  font-size: 12px;
  font-weight: bold;
  line-height: 1;
}

.task-checkbox:checked {
  background: #2F7BB6;
  border-color: #2F7BB6;
}

.task-checkbox:checked::before {
  content: '✓';
  width: auto;
  height: auto;
  background: none;
  color: white;
  font-size: 12px;
  font-weight: bold;
  line-height: 1;
}

.task-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.task-header {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.task-text {
  font-size: 15px;
  font-weight: 500;
  color: #303A33;
  line-height: 1.5;
  word-break: break-word;
}

.task-item.completed .task-text {
  color: #949A93;
  text-decoration: line-through;
}

.task-priority {
  padding: 4px 10px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  white-space: nowrap;
}

.task-priority.priority-HIGH {
  background: #FEE2E2;
  color: #DC2626;
}

.task-priority.priority-MEDIUM {
  background: #FEF3C7;
  color: #B45309;
}

.task-priority.priority-LOW {
  background: #DBEAFE;
  color: #0369A1;
}

.task-description {
  font-size: 13px;
  font-weight: 400;
  color: #737B73;
  line-height: 1.4;
}

.task-item.completed .task-description {
  color: #949A93;
}

.task-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  flex-wrap: wrap;
}

.task-deadline {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 8px;
  background: #F0F4F1;
  border-radius: 6px;
  color: #737B73;
}

.task-item.completed .task-deadline {
  color: #949A93;
}

.task-status {
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 600;
  white-space: nowrap;
}

.task-status.pending {
  background: #FFF1E8;
  color: #C96A2F;
}

.task-status.completed {
  background: #D8EEDB;
  color: #58745C;
}

.delete-btn {
  width: 16px;
  height: 16px;
  background: none;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #949A93;
  flex-shrink: 0;
  transition: color 0.2s ease;
}

.delete-btn:hover {
  color: #C96A2F;
}

.edit-btn {
  width: 16px;
  height: 16px;
  background: none;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #58745C;
  flex-shrink: 0;
  transition: color 0.2s ease;
}

.edit-btn:hover {
  color: #4a6250;
}

/* Modal styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 20px;
  box-shadow: 0px 20px 50px rgba(0, 0, 0, 0.15);
  width: 90%;
  max-width: 500px;
  display: flex;
  flex-direction: column;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #E7DCC8;
}

.modal-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #303A33;
}

.modal-close {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #737B73;
  transition: color 0.2s ease;
}

.modal-close:hover {
  color: #303A33;
}

.modal-body {
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.modal-footer {
  display: flex;
  gap: 12px;
  padding: 24px;
  border-top: 1px solid #E7DCC8;
  justify-content: flex-end;
}

.btn-cancel {
  padding: 10px 20px;
  background: #F0F4F1;
  color: #58745C;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-cancel:hover {
  background: #E0E8E1;
}

.btn-save {
  padding: 10px 20px;
  background: #58745C;
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-save:hover {
  background: #4a6250;
}

/* Tasks remaining */
.tasks-remaining {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 24px;
  background: white;
}

.remaining-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #303A33;
}

.dot {
  width: 6px;
  height: 6px;
  background: #58745C;
  border-radius: 50%;
}

.clear-completed-btn {
  background: none;
  border: none;
  color: #58745C;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: color 0.2s ease;
}

.clear-completed-btn:hover {
  color: #4a6250;
}

/* Info footer */
.info-footer {
  width: 800px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  color: #737B73;
  font-size: 13px;
  font-weight: 400;
}

/* State messages */
.state-message {
  padding: 40px 24px;
  text-align: center;
  color: #737B73;
  font-size: 15px;
}

/* Responsive */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    align-items: flex-start;
  }

  .header-right {
    width: 100%;
    justify-content: space-between;
  }

  .greeting-text h2 {
    font-size: 28px;
  }

  .task-counter {
    font-size: 12px;
  }

  .add-task-input-group {
    flex-direction: column;
  }

  .add-task-controls {
    flex-direction: column;
  }

  .add-btn {
    width: 100%;
    justify-content: center;
  }

  .task-description-input {
    min-height: 60px;
  }

  .filter-tabs {
    justify-content: flex-start;
    padding-right: 0;
  }

  .filter-total {
    display: none;
  }

  .tasks-list {
    max-height: none;
  }
}

@media (max-width: 480px) {
  .dashboard-content {
    padding: 1.5rem 1rem;
  }

  .greeting-text h2 {
    font-size: 24px;
  }

  .greeting-text p {
    font-size: 13px;
  }

  .task-item {
    padding: 16px;
    min-height: 60px;
  }

  .add-task-section {
    padding: 16px;
  }

  .add-task-input-group {
    gap: 8px;
  }

  .task-input {
    font-size: 14px;
    height: 44px;
  }

  .add-btn {
    height: 44px;
    padding: 0 16px;
    font-size: 13px;
  }

  .logout-btn {
    font-size: 12px;
    padding: 0.375rem 0.75rem;
  }

  .mascot-decoration {
    opacity: 0.3;
  }
}
</style>
