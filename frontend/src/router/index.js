import { createRouter, createWebHistory } from 'vue-router'

import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import SignupView from '../views/SignupView.vue'
import UserView from '../views/UserView.vue'
import AdminView from '../views/AdminView.vue'

import BoardListView from '../views/BoardListView.vue'
import BoardDetailView from '../views/BoardDetailView.vue'
import BoardWriteView from '../views/BoardWriteView.vue'
import BoardEditView from '../views/BoardEditView.vue'

import MonitoringView from '../views/MonitoringView.vue'
import SiteView from '../views/SiteView.vue'
import EquipmentView from '../views/EquipmentView.vue'
import MaintenanceView from '../views/MaintenanceView.vue'
import DashboardView from '../views/DashboardView.vue'


const routes = [
  { path: '/dashboard', name: 'dashboard', component: DashboardView, meta: { requiresAuth: true } },
  { path: '/maintenance', name: 'maintenance', component: MaintenanceView, meta: { requiresAuth: true } },
  { path: '/equipments', name: 'equipments', component: EquipmentView, meta: { requiresAuth: true } },

  // =========================
  // 홈
  // =========================

  {
    path: '/',
    name: 'home',
    component: HomeView
  },


  // =========================
  // 로그인 / 회원가입
  // =========================

  {
    path: '/login',
    name: 'login',
    component: LoginView
  },

  {
    path: '/signup',
    name: 'signup',
    component: SignupView
  },


  // =========================
  // 사용자
  // =========================

  {
    path: '/user',
    name: 'user',
    component: UserView,
    meta: {
      requiresAuth: true
    }
  },


  // =========================
  // 관리자
  // =========================

  {
    path: '/admin',
    name: 'admin',
    component: AdminView,
    meta: {
      requiresAuth: true,
      requiresAdmin: true
    }
  },


  // =========================
  // 게시판
  // =========================

  {
    path: '/boards',
    name: 'boardList',
    component: BoardListView,
    meta: {
      requiresAuth: true
    }
  },

  {
    path: '/boards/write',
    name: 'boardWrite',
    component: BoardWriteView,
    meta: {
      requiresAuth: true
    }
  },

  {
    path: '/boards/:id/edit',
    name: 'boardEdit',
    component: BoardEditView,
    meta: {
      requiresAuth: true
    }
  },

  {
    path: '/boards/:id',
    name: 'boardDetail',
    component: BoardDetailView,
    meta: {
      requiresAuth: true
    }
  },


  // =========================
  // 통합관제
  // =========================

  {
    path: '/monitoring',
    name: 'monitoring',
    component: MonitoringView,
    meta: {
      requiresAuth: true
    }
  },


  // =========================
  // 현장 관리
  // =========================

  {
    path: '/sites',
    name: 'sites',
    component: SiteView,
    meta: {
      requiresAuth: true
    }
  }

]


// =========================
// Router 생성
// =========================

const router = createRouter({

  history: createWebHistory(),

  routes

})


// =========================
// 로그인 / 관리자 권한 체크
// =========================

router.beforeEach((to) => {

  const token =
    localStorage.getItem('accessToken')

  const role =
    localStorage.getItem('role')


  // 로그인이 필요한 페이지
  if (
    to.meta.requiresAuth &&
    !token
  ) {

    return '/login'
  }


  // 관리자 전용 페이지
  if (
    to.meta.requiresAdmin &&
    role !== 'ADMIN'
  ) {

    return '/'
  }

})


export default router