document.addEventListener('DOMContentLoaded', () => {
    const addTaskBtn = document.getElementById('addTaskBtn');
    const taskModal = document.getElementById('taskModal');
    const cancelBtn = document.getElementById('cancelBtn');
    const deleteBtn = document.getElementById('deleteBtn');
    const taskForm = document.getElementById('taskForm');
    const taskList = document.getElementById('taskList');
    const pendingTasksBtn = document.getElementById('pendingTasks');
    const completedTasksBtn = document.getElementById('completedTasks');
    const toast = document.getElementById('toast');

    let currentStatus = false;

    addTaskBtn.addEventListener('click', () => openModal());
    cancelBtn.addEventListener('click', () => closeModal());
    deleteBtn.addEventListener('click', () => {
        const taskId = document.getElementById('taskId').value;
        if (taskId) {
            deleteTask(taskId);
            closeModal();
        }
    });

    taskForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const taskId = document.getElementById('taskId').value;
            const title = document.getElementById('title').value;
            const dueDate = formatDate(document.getElementById('dueDate').value);
            const category = document.getElementById('category').value;
            const user = document.getElementById('user').value;
            const description = document.getElementById('description').value;

            const task = { id: taskId ? parseInt(taskId) : null, title, dueDate, category, user, description, status: currentStatus };

            if (taskId) {
                updateTask(task);
            } else {
                addTask(task);
            }

            closeModal();
        });

    pendingTasksBtn.addEventListener('click', () => {
        currentStatus = false;
        pendingTasksBtn.classList.add('active');
        completedTasksBtn.classList.remove('active');
        fetchTasks();
    });

    completedTasksBtn.addEventListener('click', () => {
        currentStatus = true;
        completedTasksBtn.classList.add('active');
        pendingTasksBtn.classList.remove('active');
        fetchTasks();
    });

    function fetchTasks() {
        fetch('/api/todo')
            .then(response => response.json())
            .then(tasks => {
                const filteredTasks = tasks.filter(task => task.status === currentStatus);
                displayTasks(filteredTasks);
            })
            .catch(error => {
                console.error('Error fetching tasks:', error);
                showToast('Failed to fetch tasks. Please try again.');
            });
    }

    function displayTasks(tasks) {
            taskList.innerHTML = '';
            tasks.forEach(task => {
                const taskElement = document.createElement('div');
                taskElement.classList.add('task-item');
                taskElement.innerHTML = `
                    <div class="status-dot ${task.status ? 'completed' : ''}" data-id="${task.id}"></div>
                    <div class="task-info">
                        <span class="task-title">${task.title}</span>
                        <div class="task-details">
                            <span><i class="far fa-calendar-alt"></i> ${task.dueDate}</span>
                            <span><i class="fas fa-tag"></i> ${task.category}</span>
                            <span><i class="fas fa-user"></i> User ${task.user}</span>
                        </div>
                    </div>
                `;
                taskElement.addEventListener('dblclick', () => openModal(task));
                taskList.appendChild(taskElement);
            });

            document.querySelectorAll('.status-dot').forEach(dot => {
                dot.addEventListener('click', (e) => {
                    e.stopPropagation();
                    toggleTaskStatus(dot.dataset.id);
                });
            });
        }

    function openModal(task = null) {
            document.getElementById('modalTitle').textContent = task ? 'Edit Task' : 'Add Task';
            document.getElementById('taskId').value = task ? task.id : '';
            document.getElementById('title').value = task ? task.title : '';
            document.getElementById('dueDate').value = task ? formatDateForInput(task.dueDate) : '';
            document.getElementById('category').value = task ? task.category : 'Blue';
            document.getElementById('user').value = task ? task.user : '1';
            document.getElementById('description').value = task ? task.description : '';
            deleteBtn.style.display = task ? 'inline-block' : 'none';
            taskModal.style.display = 'block';
            setTimeout(() => taskModal.style.opacity = '1', 10);
        }



    function closeModal() {
        taskModal.style.opacity = '0';
        setTimeout(() => {
            taskModal.style.display = 'none';
            taskForm.reset();
        }, 300);
    }

    function addTask(task) {
        fetch('/api/todo', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(task),
        })
        .then(response => response.json())
        .then(() => {
            fetchTasks();
            showToast('Task added successfully!');
        })
        .catch(error => {
            console.error('Error adding task:', error);
            showToast('Failed to add task. Please try again.');
        });
    }

    function updateTask(task) {
        fetch('/api/todo', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(task),
        })
        .then(response => response.json())
        .then(() => {
            fetchTasks();
            showToast('Task updated successfully!');
        })
        .catch(error => {
            console.error('Error updating task:', error);
            showToast('Failed to update task. Please try again.');
        });
    }

    function deleteTask(id) {
        fetch(`/api/todo/${id}`, { method: 'DELETE' })
        .then(() => {
            fetchTasks();
            showToast('Task deleted successfully!');
        })
        .catch(error => {
            console.error('Error deleting task:', error);
            showToast('Failed to delete task. Please try again.');
        });
    }

    function toggleTaskStatus(id) {
        fetch(`/api/todo/${id}`)
            .then(response => response.json())
            .then(task => {
                task.status = !task.status;
                return fetch('/api/todo', {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(task),
                });
            })
            .then(() => {
                fetchTasks();
                showToast(`Task marked as ${currentStatus ? 'pending' : 'completed'}!`);
            })
            .catch(error => {
                console.error('Error toggling task status:', error);
                showToast('Failed to update task status. Please try again.');
            });
    }

    function formatDate(dateString) {
        const date = new Date(dateString);
        return `${padZero(date.getDate())}-${padZero(date.getMonth() + 1)}-${date.getFullYear()}`;
    }

    function formatDateForInput(dateString) {
        const [day, month, year] = dateString.split('-');
        return `${year}-${padZero(month)}-${padZero(day)}`;
    }

    function padZero(num) {
        return num.toString().padStart(2, '0');
    }

    function showToast(message) {
        toast.textContent = message;
        toast.classList.add('show');
        setTimeout(() => {
            toast.classList.remove('show');
        }, 3000);
    }

    fetchTasks();
});