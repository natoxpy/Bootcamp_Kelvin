<script setup lang="ts">
// import TheWelcome from '../components/TheWelcome.vue'
import { onMounted, ref } from 'vue'

const message = ref('ultra mega paimon')

onMounted(() => {
    if ((window as any).api) {
        message.value = (window as any).api.greet('Tanya')
    } else {
        message.value = 'not loaded'
    }
})
</script>

<template>
    <main>
        <span class="bg-red-500">{{ message }} </span>
    </main>
</template>
