<template>
  <div class="hello">
    <h1>输入包名以开始</h1>
    <el-input v-model="input" placeholder="请输入包名,精确到项目名"></el-input>
  </div>
</template>

<script>
export default {
  data() {
    return {
      input: ''
    }
  }

}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h1{
  height: 50px;
  line-height: 50px;
  margin-bottom: 200px;
}
.hello {
  height: 100%;
  justify-content: center;
  align-items: center;
}
</style>
