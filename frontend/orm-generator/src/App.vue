<template>
  <div id="app">
    <Mapper />
  </div>
</template>

<script>
// import Hello from './components/Hello.vue'
import Mapper from './components/MapperGenerator.vue'

export default {
  name: 'App',
  components: {
    // Hello,
    Mapper
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
