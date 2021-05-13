<template>
  <div id="app">
    <!-- <router-view /> -->
    <Download />
  </div>
</template>

<script>
// import POJO from './components/POJOGenerator.vue'
// import Mapper from './components/MapperGenerator.vue'
import Download from './components/Download'

export default {
  name: 'App',
  components: {
    // POJO,
    // Mapper
    Download
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
