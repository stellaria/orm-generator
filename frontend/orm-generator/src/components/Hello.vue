<template>
  <div>
    <h1>输入信息以开始</h1>
    <el-form  ref="form" :model="form" label-width="100px" class="hello">
      <el-form-item label="项目包名">
        <el-input v-model="form.package"></el-input>
      </el-form-item>
      <el-form-item label="数据库地址">
        <el-col :span="4">
          <span class="customize">jdbc:</span>
        </el-col>
        <el-col :span="20">
          <el-input placeholder="请输入内容" v-model="form.conn_url" class="input-with-select">
            <el-select v-model="database" slot="prepend" placeholder="请选择" class="el-select">
              <el-option label="mysql" value="mysql"></el-option>
              <el-option label="sqlserver" value="sqlserver"></el-option>
            </el-select>
          </el-input>
        </el-col>
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
      <el-form-item label="框架类型">
        <el-select v-model="orm" placeholder="请选择框架类型">
          <el-option label="jpa" value="jpa"></el-option>
          <el-option label="mybatis" value="mybatis"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" style="width:500px;height:100px;" @click="onSubmit">GO!</el-button>
      </el-form-item> 
    </el-form>
  </div>
</template>

<script>
import * as api from '@/api/api.js'
import * as _ from 'lodash'

export default {
  data() {
    return {
      form: {
        package: '',
        conn_url:'',
        conn_username:'',
        conn_passwd:'',
        conn_type:''
      },
      jdbc:'',
      database:'mysql',
      orm:''
    }
  },methods: {
    onSubmit() {
      var urlPart = ''
      if (this.form.conn_url.match(/:\/\//i)) {
        urlPart = this.form.conn_url.substr(this.form.conn_url.indexOf('://')+3)
      } else {
        urlPart = this.form.conn_url
      }
      var finalPart = ''
      if (urlPart.indexOf('?') !== -1) {
        finalPart = urlPart.substring(0, urlPart.indexOf('?'))
      } else {
        finalPart = urlPart
      }
      var paramStr = urlPart.substring(urlPart.indexOf('?')+1)
      var params = []
      if (paramStr.indexOf('&') !== -1) {
        params = _.split(paramStr, '&')
      } 
      if (_.findIndex(params, o=>{return o === 'serverTimezone=UTC'}) === -1) {
        params.push('serverTimezone=UTC')
      }
      var param = _.join(params, '&')
      var finalStr = 'jdbc:'+this.database+'://'+finalPart+'?'+param
      this.form.conn_url=finalStr

      this.$store.commit('setPackage', this.form.package)
      this.$store.commit('setORM', this.orm)
      api.getConnection(this.form).then(res => {
        if (res.code === 200) {
          if (res.data.code === 500) {
            this.$message.error(res.data.msg)
            return 
          }
          this.$store.commit('setTableList', res.data.list)
          this.$store.commit('setSchema', res.data.tableName)
          if (this.orm === 'mybatis') {
            this.$router.push('/mybatis/pojo')
          } else {
            this.$router.push('/jpa/pojo')
          }
        } 
      })
      
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
.el-select {
  width: 130px;
}
.customize {
  background-color: #f5f7fa;
  color: #909399;
  vertical-align: middle;
  display: table-cell;
  position: relative;
  /* border: 1px solid #dcdfe6; */
  border-radius: 4px;
  padding: 0 20px;
  white-space: nowrap;
  width:150px;
  box-sizing:border-box;
}
</style>
