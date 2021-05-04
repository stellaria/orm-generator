<template>
  <div>
    <h1>输入信息以开始</h1>
    <el-form  ref="form" :model="form" label-width="100px" class="hello">
      <el-form-item label="项目包名">
        <el-input v-model="form.package"></el-input>
      </el-form-item>
      <el-form-item label="数据库地址">
        <el-input v-model="form.conn_url"></el-input>
      </el-form-item>
      <el-form-item label="数据库用户名">
        <el-input v-model="form.conn_username"></el-input>
      </el-form-item>
      <el-form-item label="数据库密码">
        <el-input v-model="form.conn_passwd" type="password"></el-input>
      </el-form-item>
      <el-form-item label="数据库类型">
        <el-select v-model="form.conn_type" placeholder="请选择数据库类型">
          <el-option label="mysql8" value="mysql8"></el-option>
          <el-option label="mysql5" value="mysql5"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" style="width:500px;height:100px;" @click="onSubmit">GO!</el-button>
      </el-form-item> 
    </el-form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      form: {
        package: '',
        conn_url:'',
        conn_username:'',
        conn_passwd:'',
        conn_type:''
      }
    }
  },methods: {
    onSubmit() {
      this.$store.commit('setPackage', this.form.package)
      this.$router.push('/pojo')
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

h1{
  height: 50px;
  line-height: 50px;
  margin-bottom: 50px;
}
.hello {
  position: relative;
  left: 50%;
  margin-left: -500px;
  height: 100%;
  width: 1000px;
}
</style>
