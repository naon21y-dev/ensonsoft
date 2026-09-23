<script setup>
import { ref } from 'vue'
import api from '../api/api'

const members = ref([])
const message = ref('')

const getMembers = async () => {
  try {
    const response = await api.get('/admin/members')

    members.value = response.data
    message.value = '회원 목록 조회 성공'
  } catch (error) {
    console.error(error)

    if (error.response?.status === 403) {
      message.value = '관리자만 접근할 수 있습니다.'
    } else if (error.response?.status === 401) {
      message.value = '로그인이 필요합니다.'
    } else {
      message.value =
        error.response?.data?.message || '회원 목록 조회 실패'
    }
  }
}
</script>

<template>
  <div>
    <h1>관리자 페이지</h1>

    <button @click="getMembers">
      전체 회원 조회
    </button>

    <p>{{ message }}</p>

    <div v-if="members.length > 0">
      <hr />

      <div
        v-for="member in members"
        :key="member.id"
      >
        <p>ID: {{ member.id }}</p>
        <p>아이디: {{ member.username }}</p>
        <p>이름: {{ member.name }}</p>
        <p>이메일: {{ member.email }}</p>
        <p>권한: {{ member.role }}</p>
        <p>활성화: {{ member.enabled }}</p>

        <hr />
      </div>
    </div>
  </div>
</template>