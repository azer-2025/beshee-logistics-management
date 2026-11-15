<template>
  <div class="chat-window" v-if="visible"
       :style="{ right: position.x + 'px', bottom: position.y + 'px', width: chatWidth + 'px', height: chatHeight + 'px' }"
       @mousedown="startDrag">
    <div class="chat-header">
      <div class="header-left">
        <span class="title">在线客服</span>
        <span class="status">
          <span class="status-dot online"></span>
          在线
        </span>
      </div>

      <div class="header-right">
        <el-button type="text" @click="minimize" class="header-btn">
          <el-icon><Minus /></el-icon>
        </el-button>
        <el-button type="text" @click="close" class="header-btn close-btn">
          <el-icon><Close /></el-icon>
        </el-button>
      </div>
    </div>

    <div class="chat-body">
      <div class="messages-container" ref="messagesContainer">
        <div
            v-for="message in messages"
            :key="message.id"
            :class="['message-item', message.type]"
        >
          <div class="message-content">
            <div v-if="message.type === 'user'" class="avatar user-avatar">
              <span>{{ authStore.user?.username?.charAt(0) || '我' }}</span>
            </div>

            <div v-else class="avatar service-avatar">
              <span>客服</span>
            </div>

            <div class="message-bubble">
              <div class="message-text">{{ message.content }}</div>
              <div class="message-time">{{ formatTime(message.timestamp) }}</div>
            </div>
          </div>
        </div>

        <div v-if="isTyping" class="typing-indicator">
          <span>客服正在输入...</span>
        </div>
      </div>
    </div>

    <div class="chat-footer">
      <div class="input-area">
        <el-input
            v-model="inputMessage"
            type="textarea"
            :rows="3"
            placeholder="请输入消息..."
            resize="none"
            @keyup.enter="sendMessage"
            :disabled="isSending"
        />

        <div class="input-actions">
          <el-button
              type="primary"
              @click="sendMessage"
              :loading="isSending"
              :disabled="!inputMessage.trim()"
          >
            发送
          </el-button>
        </div>
      </div>
    </div>
  </div>

  <!-- 最小化状态 -->
  <div v-else-if="minimized" class="chat-minimized" @click="restore">
    <div class="minimized-content">
      <span class="minimized-text">在线客服</span>
      <span v-if="unreadCount > 0" class="unread-badge">{{ unreadCount }}</span>
    </div>
  </div>

  <!-- 悬浮按钮 -->
  <div v-else class="chat-float-btn" @click="open">
    <el-icon><ChatDotRound /></el-icon>
    <span v-if="unreadCount > 0" class="unread-badge">{{ unreadCount }}</span>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted, onUnmounted, watch } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const authStore = useAuthStore()

const visible = ref(false)
const minimized = ref(false)
const messages = ref([])
const inputMessage = ref('')
const isSending = ref(false)
const isTyping = ref(false)
const unreadCount = ref(0)
const messagesContainer = ref(null)
const messageInterval = ref(null)
const pollingTimer = ref(null) // 轮询定时器

// 聊天窗口尺寸
const chatWidth = ref(550)
const chatHeight = ref(700)

// 拖动相关状态
const position = ref({ x: 20, y: 20 })
const isDragging = ref(false)
const dragStart = ref({ x: 0, y: 0 })
const initialPosition = ref({ x: 0, y: 0 })

// 初始化消息
const initMessages = () => {
  messages.value = [
    {
      id: 1,
      type: 'service',
      content: '您好！欢迎使用生鲜冷链物流监控与预警系统，请问有什么可以帮您？',
      timestamp: new Date()
    }
  ]
}

// 获取聊天记录
const fetchChatMessages = async () => {
  try {
    // 在获取聊天记录前先更新用户信息
    await authStore.getUserInfo()

    // 基本验证
    if (!authStore.isLoggedIn || !authStore.user?.id) {
      return
    }

    const response = await request({
      url: '/chat/list',
      method: 'get',
      params: {
        yonghuId: authStore.user.id // 使用明确的用户ID
      }
    })

    if (response.data && response.data.list) {
      // 转换后端数据格式为前端使用格式
      const formattedMessages = response.data.list.map(item => {
        // 用户发送的消息
        if (item.chatIssue) {
          return {
            id: item.id,
            type: 'user',
            content: item.chatIssue,
            timestamp: item.issueTime ? new Date(item.issueTime) : new Date()
          }
        }
        // 客服回复的消息
        else {
          return {
            id: item.id,
            type: 'service',
            content: item.chatReply,
            timestamp: item.replyTime ? new Date(item.replyTime) : new Date()
          }
        }
      })

      // 按时间排序
      formattedMessages.sort((a, b) => a.timestamp - b.timestamp)

      // 保留初始欢迎消息并添加新消息，避免重复
      const welcomeMessage = messages.value.find(m => m.id === 1)
      const existingMessageIds = messages.value.map(m => m.id)

      const newMessages = formattedMessages.filter(m => !existingMessageIds.includes(m.id))

      if (welcomeMessage && messages.value.length === 1) {
        messages.value = [welcomeMessage, ...newMessages]
      } else if (newMessages.length > 0) {
        messages.value = [...messages.value, ...newMessages]
      }

      scrollToBottom()
    }
  } catch (error) {
    console.error('获取聊天记录失败:', error)
  }
}

// 检查新消息
const checkNewMessages = async () => {
  try {
    // 获取所有消息
    const response = await request({
      url: '/chat/list',
      method: 'get',
      params: {
        yonghuId: authStore.user.id
      }
    })

    if (response.data && response.data.list) {
      // 转换后端数据格式为前端使用格式
      const formattedMessages = response.data.list.map(item => {
        // 用户发送的消息
        if (item.chatIssue) {
          return {
            id: item.id,
            type: 'user',
            content: item.chatIssue,
            timestamp: item.issueTime ? new Date(item.issueTime) : new Date()
          }
        }
        // 客服回复的消息
        else {
          return {
            id: item.id,
            type: 'service',
            content: item.chatReply,
            timestamp: item.replyTime ? new Date(item.replyTime) : new Date()
          }
        }
      })

      // 查找新增的客服消息
      const existingIds = messages.value.map(m => m.id)
      const newServiceMessages = formattedMessages
          .filter(item =>
              item.type === 'service' &&
              !existingIds.includes(item.id)
          )

      // 添加新消息到列表
      if (newServiceMessages.length > 0) {
        messages.value = [...messages.value, ...newServiceMessages]
        scrollToBottom()

        // 增加未读消息计数（如果窗口最小化）
        if (minimized.value) {
          unreadCount.value += newServiceMessages.length
        }
      }
    }
  } catch (error) {
    console.error('检查新消息失败:', error)
  }
}

// 启动轮询
const startPolling = () => {
  // 先清除已存在的定时器
  if (pollingTimer.value) {
    clearInterval(pollingTimer.value)
  }

  // 每5秒检查一次新消息
  pollingTimer.value = setInterval(async () => {
    if (visible.value && authStore.isLoggedIn) {
      await checkNewMessages()
    }
  }, 5000)
}

// 停止轮询
const stopPolling = () => {
  if (pollingTimer.value) {
    clearInterval(pollingTimer.value)
    pollingTimer.value = null
  }
}

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim() || isSending.value) return

  isSending.value = true

  const userMessage = {
    id: Date.now(),
    type: 'user',
    content: inputMessage.value.trim(),
    timestamp: new Date()
  }

  messages.value.push(userMessage)
  const messageContent = inputMessage.value.trim()
  inputMessage.value = ''
  scrollToBottom()

  try {
    // 发送到后端
    const response = await request({
      url: '/chat/add',
      method: 'post',
      data: {
        yonghuId: authStore.user?.id,
        chatIssue: messageContent,
        chatTypes: 1,
        zhuangtaiTypes: 1,
        issueTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
      }
    })

    // 模拟客服回复延迟
    isTyping.value = true
    await new Promise(resolve => setTimeout(resolve, 1000 + Math.random() * 2000))
    isTyping.value = false

    // 直接添加客服回复消息而不是重新获取所有记录
    if (response.data && response.data.chatReply) {
      const serviceMessage = {
        id: response.data.id || Date.now() + 1,
        type: 'service',
        content: response.data.chatReply,
        timestamp: response.data.replyTime ? new Date(response.data.replyTime) : new Date()
      }
      messages.value.push(serviceMessage)
      scrollToBottom()
    }

  } catch (error) {
    ElMessage.error('发送消息失败')
    // 如果发送失败，从消息列表中移除
    messages.value = messages.value.filter(m => m.id !== userMessage.id)
  } finally {
    isSending.value = false
  }
}

// 滚动到底部
const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// 格式化时间
const formatTime = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  if (isNaN(date.getTime())) return ''

  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 打开聊天窗口
const open = async () => {
  visible.value = true
  minimized.value = false

  // 只有当没有加载过消息时才获取聊天记录
  if (messages.value.length <= 1) {
    await fetchChatMessages()
  }

  unreadCount.value = 0
  scrollToBottom()

  // 启动轮询
  startPolling()
}

// 关闭聊天窗口
const close = () => {
  visible.value = false
  minimized.value = false

  // 停止轮询
  stopPolling()
}

// 最小化聊天窗口
const minimize = () => {
  visible.value = false
  minimized.value = true
  // 保持轮询继续运行，以便接收新消息
}

// 恢复聊天窗口
const restore = async () => {
  visible.value = true
  minimized.value = false

  // 只有当消息数量很少时才重新获取聊天记录
  if (messages.value.length <= 1) {
    await fetchChatMessages()
  }

  unreadCount.value = 0
  scrollToBottom()

  // 启动轮询
  startPolling()
}

// 拖动开始
const startDrag = (e) => {
  if (e.target.closest('.chat-header')) {
    isDragging.value = true
    dragStart.value = {x: e.clientX, y: e.clientY}
    initialPosition.value = {...position.value}

    document.addEventListener('mousemove', onDrag)
    document.addEventListener('mouseup', stopDrag)
    e.preventDefault()
  }
}

// 拖动中
const onDrag = (e) => {
  if (!isDragging.value) return

  const deltaX = e.clientX - dragStart.value.x
  const deltaY = e.clientY - dragStart.value.y

  position.value.x = initialPosition.value.x - deltaX
  position.value.y = initialPosition.value.y - deltaY

  const windowWidth = window.innerWidth
  const windowHeight = window.innerHeight

  if (position.value.x < 20) position.value.x = 20
  if (position.value.x > windowWidth - chatWidth.value - 20) position.value.x = windowWidth - chatWidth.value - 20
  if (position.value.y < 20) position.value.y = 20
  if (position.value.y > windowHeight - chatHeight.value - 20) position.value.y = windowHeight - chatHeight.value - 20
}

// 停止拖动
const stopDrag = () => {
  isDragging.value = false
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
}

// 监听键盘事件
const handleKeyPress = (e) => {
  if (e.key === 'Escape') {
    minimize()
  }
}

// 监听打开聊天窗口的事件
const handleOpenChatWindow = () => {
  open()
}

// 监听用户登录状态变化
watch(
    () => authStore.isLoggedIn,
    (newStatus, oldStatus) => {
      // 如果用户从登录状态变为登出状态
      if (oldStatus === true && newStatus === false) {
        // 清空聊天消息
        messages.value = []
        initMessages()
        // 停止轮询
        stopPolling()
      }
      // 如果用户从登出状态变为登录状态（新用户登录）
      else if (oldStatus === false && newStatus === true) {
        // 如果聊天窗口是打开状态，获取新用户的聊天记录
        if (visible.value) {
          fetchChatMessages()
          // 启动轮询
          startPolling()
        }
      }
    }
)

onMounted(() => {
  initMessages()
  document.addEventListener('keydown', handleKeyPress)
  window.addEventListener('openChatWindow', handleOpenChatWindow)

  // 如果聊天窗口默认是打开的，启动轮询
  if (visible.value) {
    startPolling()
  }
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeyPress)
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
  window.removeEventListener('openChatWindow', handleOpenChatWindow)

  // 清除定时器
  stopPolling()
})
</script>

<style scoped>
.chat-window {
  position: fixed;
  background: white;
  border-radius: 10px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  z-index: 1000;
  overflow: hidden;
  cursor: move;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #409eff;
  color: white;
  cursor: move;
  user-select: none;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.title {
  font-weight: bold;
  font-size: 16px;
}

.status {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.status-dot.online {
  background: #67c23a;
}

.header-right {
  display: flex;
  gap: 5px;
}

.header-btn {
  color: rgba(255, 255, 255, 0.8);
  font-size: 18px;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s;
}

.header-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  color: white;
}

.close-btn:hover {
  background: #f56c6c !important;
  color: white !important;
}

.chat-body {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f5f7fa;
}

.message-item {
  margin-bottom: 15px;
}

.message-content {
  display: flex;
  gap: 10px;
  align-items: flex-start;
}

.message-item.user .message-content {
  flex-direction: row-reverse;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: white;
  flex-shrink: 0;
}

.user-avatar {
  background: #409eff;
}

.service-avatar {
  background: #67c23a;
}

.message-bubble {
  max-width: 70%;
  padding: 10px 15px;
  border-radius: 18px;
  position: relative;
  word-wrap: break-word;
}

.message-item.user .message-bubble {
  background: #409eff;
  color: white;
}

.message-item.service .message-bubble {
  background: white;
  color: #333;
  border: 1px solid #e4e7ed;
}

.message-text {
  font-size: 14px;
  line-height: 1.4;
}

.message-time {
  font-size: 11px;
  opacity: 0.7;
  margin-top: 5px;
  text-align: right;
}

.typing-indicator {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #666;
  font-size: 12px;
  padding: 10px 20px;
}

.chat-footer {
  padding: 15px 20px;
  border-top: 1px solid #e4e7ed;
  background: white;
}

.input-area {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.input-actions {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.chat-minimized {
  position: fixed;
  bottom: 100px;
  right: 20px;
  width: 200px;
  height: 50px;
  background: #409eff;
  color: white;
  border-radius: 25px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 1000;
}

.minimized-content {
  display: flex;
  align-items: center;
  gap: 10px;
  position: relative;
}

.minimized-text {
  font-size: 14px;
}

.chat-float-btn {
  position: fixed;
  bottom: 100px;
  right: 20px;
  width: 60px;
  height: 60px;
  background: #409eff;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  transition: transform 0.3s;
}

.chat-float-btn:hover {
  transform: scale(1.1);
}

.unread-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background: #f56c6c;
  color: white;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
}

@media (max-width: 480px) {
  .chat-window {
    width: 100% !important;
    height: 100% !important;
    bottom: 0 !important;
    right: 0 !important;
    border-radius: 0;
  }
}
</style>
