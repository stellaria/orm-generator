<template>
  <el-container v-loading="loading">
    <el-aside width="400px">
      <h1>{{ schemaName }}</h1>
      <el-table
        :data="tableData"
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-form label-position="left" inline class="demo-table-expand">
              <el-form-item
                v-for="(col, index) in props.row.cols"
                :key="index"
                :label="col.DATA_TYPE"
              >
                <span>{{ col.COLUMN_NAME }}</span>
                <span
                  v-show="col.COLUMN_KEY === 'PRI'"
                  style="margin-left: 30px"
                  >{{ col.COLUMN_KEY }}</span
                >
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column label="表名" prop="name"> </el-table-column>
        <el-table-column label="操作" width="100">
          <template slot-scope="scope">
            <el-button
              @click.prevent="handleClick(scope.row)"
              type="text"
              size="small"
              :disabled="beforeGo"
              >客制化</el-button
            >
          </template>
        </el-table-column>
        <el-table-column type="selection" width="55"> </el-table-column>
      </el-table>
      <el-button
        type="primary"
        plain
        @click="handlePreview"
        style="margin-top: 20px"
        >上一步!</el-button>
      <el-button
        type="primary"
        plain
        @click="generateMapper"
        style="margin-top: 20px"
        >GO!</el-button>
      <el-button
        type="primary"
        plain
        @click="goNext"
        style="margin-top: 20px"
        :disabled="beforeGo"
        >下一步!</el-button>
    </el-aside>
    <el-container>
      <el-header>{{ tableName }}</el-header>
      <el-main>
        <p v-if="!customize">
					欢迎使用本系统,操作流程如下:<br>
					首先用复选框选在左边的表格中选中你想要生成的表,之后点击go按钮<br>
					再来,对你想要自定义的类,点击对应的客制化就能进入自定义环节<br>
					在自定义栏中,你可以选择修改类名,实体名,实体属性,关联对象,甚至可以添加表中不存在的行<br>
					最后点击下一步来进入下一个步骤
				</p>
        <el-form
          v-if="customize"
          :model="dynamicValidateForm"
          ref="dynamicValidateForm"
          label-width="150px"
          class="demo-dynamic1"
        >
          <el-form-item :label="'实体名'">
            <el-col :span="9">
              <el-input v-model="newEntity" disabled></el-input>
            </el-col>
          </el-form-item>
          <el-form-item
            class="demo-dynamic-column1"
            v-for="(domain, index) in dynamicValidateForm.field"
            :label="'域'"
            :key="index"
            :prop="'field.' + index"
          >
            <el-col :span="4">
              <el-input v-model="domain.type" placeholder="类型" disabled />
            </el-col>
            <el-col :span="6">
              <el-input v-model="domain.name" placeholder="名称" disabled />
            </el-col>
            <el-col :span="7">
              <el-cascader
                :options="options"
                :show-all-levels="false"
                clearable
                placeholder="选择关联的属性"
                @change="
                  (val) => {
                    handleChange(val, index);
                  }
                "
              ></el-cascader>
            </el-col>
            <el-col :span="6">
              <el-cascader
                :options="selectOptions"
                :show-all-levels="false"
                clearable
                placeholder="选择关联类型"
                @change="
                  (val) => {
                    handleOptionChange(val, index);
                  }
                "
              ></el-cascader>
            </el-col>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitForm('dynamicValidateForm')"
              >提交</el-button
            >
            <el-button @click="cancle()">取消</el-button>
          </el-form-item>
        </el-form>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import * as api from "@/api/api.js";
import _ from "lodash";

export default {
  data() {
    return {
      customize: false,
      schemaName: "hello",
      loading: false,
      beforeGo: true,
      newEntity: "",
      dynamicValidateForm: {},
      tableName: "",
      cascadeType: "",
      tableData: [],
      //暂时存放,便于页面显示
      basePojo: [],
      multipleSelection: [],
      selectOptions: [
        {
          value: "1:1",
          label: "1:1",
        },
        {
          value: "1:n",
          label: "1:n",
        },
        {
          value: "n:1",
          label: "n:1",
        },
      ],
      options: [
        {
          value: "basePojo",
          label: "基础POJO",
          children: [],
        },
      ],
      refers: [],
      types: [],
    };
  },
  methods: {
    //处理客制化点击
    handleClick(n) {
      this.customize = true;
      this.tableName = n.name;
      this.basePojo.forEach((p) => {
        if (p.table.match(n.name)) {
          this.dynamicValidateForm = _.cloneDeep(p);
          this.newEntity = _.cloneDeep(p.entity);
        }
      });
    },
    handlePreview() {
      this.$store.state.cascadeMapperList = []
      this.$store.state.baseMapperList = []
      this.$store.state.basePojoList = []
      this.$store.state.cascadePojoList = []
      this.$router.push('/mybatis/pojo')
    },
    goNext() {
      for (var i = 0; i < this.basePojo.length; i++) {
				var p = _.cloneDeep(this.basePojo[i])
				var list = p.field;
        var f = new Map();
        // list.forEach((m) => {
        //   f.push(m.name + ";" + m.type);
        // })
        var tableIndex = _.findIndex(this.tableData, (o) => {
          return this.basePojo[i].table === o.name;
        });
        list.forEach((m, i) => {
          if (i < this.tableData[tableIndex].cols.length) {
            var key = this.tableData[tableIndex].cols[i].COLUMN_NAME + ';'
            if (this.tableData[tableIndex].cols[i].DATA_TYPE.match(/.*int|float|double|bigint|tinyint/i)) {
              key = key + 'Integer'
            } else {
              key = key + 'String'
            }
            f.set(key,m.name + ";" + m.type);
          } else {
            f.set("NULL", m.name + ";" + m.type);
          }
        });
        p.field = f
        var index = _.findIndex(this.$store.state.cascadePojoList, o=>{return o.table === p.table})
        if (index === -1) {
          this.$store.commit('appendBaseMapper', p)
        }
			}
      this.handleSubmit();
    },
    handleSubmit() {
      var baseMapperList = this.$store.state.baseMapperList;
      baseMapperList.forEach(o=>{
        o.field=JSON.stringify(_strMapToObj(o.field))
      })
      var basePojoList = this.$store.state.basePojoList;
      // if (this.$store.state.orm==='jpa') {
      //   basePojoList.forEach(o=>{
      //     o.refer=JSON.stringify(_strMapToObj(o.field))
      //   })
      // }
      var cascadePojoList = this.$store.state.cascadePojoList;
      cascadePojoList.forEach(o=>{
        o.refer=JSON.stringify(_strMapToObj(o.refer))
      })
      var cascadeMapperList = this.$store.state.cascadeMapperList;
      cascadeMapperList.forEach(o=>{
        o.refer = JSON.stringify(_strMapToObj(o.refer))
        o.field = JSON.stringify(_strMapToObj(o.field))
        o.type = JSON.stringify(_strMapToObj(o.type))
      })
      // console.log(list)
      if (this.$store.state.orm === 'mybatis') {
        api.sendLists({
          basePojoList:JSON.stringify(basePojoList),
          baseMapperList:JSON.stringify(baseMapperList),
          cascadePojoList:JSON.stringify(cascadePojoList),
          cascadeMapperList:JSON.stringify(cascadeMapperList)
        }).then((res) => {
          // if(res.code === 200)
          console.log(res)
          this.$router.push('/download')
        });
      } 
     
    },

    handleSelectionChange(val) {
      this.multipleSelection = val;
    },

    //创建通用Mapper
    generateMapper() {
      // if (this.basePojo.length !== 0) {
      //   this.beforeGo = false;
      //   return;
      // }
      // this.beforeGo = false;
      // this.multipleSelection.forEach((p) => {
      //   var data = {};
      //   data.entity = p.name;
      //   data.table = p.name;
      //   data.packageName = this.$store.state.package;
      //   var option = {};
      //   option.label = data.entity;
      //   option.value = data.entity;
      //   option.children = [];
      //   var list = [];
      //   p.cols.forEach((col, i) => {
      //     var child = {};
      //     var finalStr = {};
      //     if (!col.DATA_TYPE.match(/.*int|float|double|bigint|tinyint/i)) {
      //       finalStr.name = col.COLUMN_NAME;
      //       finalStr.type = "String";
      //     } else if (col.COLUMN_NAME.match(/.*id/i) && i === 0) {
      //       var str = col.DATA_TYPE;
      //       if (str.match(/bigint/i)) {
      //         finalStr.name = "id";
      //         finalStr.type = "Long";
      //       } else {
      //         finalStr.name = "id";
      //         finalStr.type = "Integer";
      //       }
      //     } else {
      //       var type = col.DATA_TYPE;
      //       if (type.match(/bigint/i)) {
      //         finalStr.name = col.COLUMN_NAME;
      //         finalStr.type = "Long";
      //       } else {
      //         finalStr.name = col.COLUMN_NAME;
      //         finalStr.type = "Integer";
      //       }
      //     }
      //     child.label = finalStr.name;
      //     child.value = finalStr.name;
      //     option.children.push(child);
      //     list.push(finalStr);
      //   });
      //   data.field = list;
      //   this.options[0].children.push(option);
      //   this.basePojo.push(data);
      // });
      
      this.basePojo = this.$store.getters.basePojo
      this.options = this.$store.state.options
      this.beforeGo = false
    },

    handleChange(n, index) {
      if (this.dynamicValidateForm.refer === undefined) {
        this.dynamicValidateForm.refer = new Map();
      }
      if (n.length === 0) {
        this.dynamicValidateForm.refer.delete(
          this.newEntity + "." + this.dynamicValidateForm.field[index].name
        );
        if (this.dynamicValidateForm.refer.size === 0) {
          this.dynamicValidateForm.refer = undefined;
        }
        return;
      }
      this.dynamicValidateForm.refer.set(
        this.newEntity + "." + this.dynamicValidateForm.field[index].name,
        n[1] + "." + n[2]
      );
    },
    handleOptionChange(n, index) {
      if (this.dynamicValidateForm.type === undefined) {
        this.dynamicValidateForm.type = new Map();
      }
      if (n.length === 0) {
        this.dynamicValidateForm.type.delete(
          this.newEntity + "." + this.dynamicValidateForm.field[index].name
        );
        if (this.dynamicValidateForm.type.size === 0) {
          this.dynamicValidateForm.type = undefined;
        }
        return;
      }
      this.dynamicValidateForm.type.set(
        this.newEntity + "." + this.dynamicValidateForm.field[index].name,
        n[0]
      );
    },

    //对通用Mapper进行客制化
    submitForm() {
      // if (this.newEntity === "") {
      //   this.$message("实体名不可为空");
      //   return false;
      // }
      // //更新basePojo的实体名
      // this.basePojo[
      //   _.findIndex(this.basePojo, (o) => {
      //     return o.table === this.dynamicValidateForm.table;
      //   })
      // ].entity = this.newEntity;
      // //更新多选器中的实体名
      // var index = _.findIndex(this.options[0].children, (o) => {
      //   return o.label === this.dynamicValidateForm.entity;
      // });
      // this.options[0].children[index].label = this.newEntity;
      // this.options[0].children[index].value = this.newEntity;
      // //更新实体名
      // this.dynamicValidateForm.entity = this.newEntity;
      var list = this.dynamicValidateForm.field;
      var field = new Map();
      var tableIndex = _.findIndex(this.tableData, (o) => {
        return this.tableName === o.name;
      });
      list.forEach((m, i) => {
        if (i < this.tableData[tableIndex].cols.length) {
          var key = this.tableData[tableIndex].cols[i].COLUMN_NAME + ';'
          if (this.tableData[tableIndex].cols[i].DATA_TYPE.match(/.*int|float|double|bigint|tinyint/i)) {
            key = key + 'Integer'
          } else {
            key = key + 'String'
          }
          field.set(key,m.name + ";" + m.type);
        } else {
          field.set("NULL", m.name + ";" + m.type);
        }
      });
      var data = _.cloneDeep(this.dynamicValidateForm);
      data.field = field;
      if (this.dynamicValidateForm.refer === undefined) {
        //基础mapper
        this.$store.commit("appendBaseMapper", data);
      } else {
        //级联mapper
        this.$store.commit("appendCascadeMapper", data);
      }
      this.customize = false;
      this.tableName = "";
    },
    cancle() {
      this.customize = false;
      this.tableName = "";
    },
    removeDomain(item) {
      var index = this.dynamicValidateForm.domains.indexOf(item);
      if (index !== -1) {
        this.dynamicValidateForm.domains.splice(index, 1);
      }
    },
    addDomain() {
      this.dynamicValidateForm.field.push({
        name: "",
        type: "",
      });
    },
  },
  mounted() {
		var list = this.$store.state.tableList;
		this.schemaName = this.$store.state.schema;
		list.forEach((o) => {
			//每一张表
			var data = {};
			data.name = o[0].TABLE_NAME;
			data.cols = o;
			this.tableData.push(data);
		});
  },
};
function _strMapToObj(strMap) {
  let obj= Object.create(null);
  for (let[k,v] of strMap) {
    obj[k] = v;
  }
  return obj;
}


</script>

<style>
.demo-table-expand {
  margin-right: 0;
  margin-bottom: 0;
  width: 80%;
}
.demo-table-expand label {
	width: 90px;
	color: #99a9bf;
}
.demo-dynamic1 {
	width: 1000px;
}
.demo-dynamic-column1 {
	width: 800px;
}
</style>