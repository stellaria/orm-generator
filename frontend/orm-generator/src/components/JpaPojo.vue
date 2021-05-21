<template>
  <el-container>
    <el-aside width="400px">
			<h3>当前数据库为</h3>
      <h1>{{ this.$store.state.schema }}</h1>
      <el-table
        :data="this.$store.getters.tableList"
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
        @click="generatePOJO()"
        style="margin-top: 20px"
        >GO!</el-button
      >
      <el-button
        type="primary"
        plain
        @click="goNext"
        style="margin-top: 20px"
        :disabled="beforeGo"
        >下一步!</el-button
      >
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
          class="demo-dynamic"
        >
          <el-form-item :label="'实体名'">
            <el-col :span="9">
              <el-input v-model="newEntity" :disabled="mutex"></el-input>
            </el-col>
            <el-col :span="6">
              <el-tooltip class="item" effect="dark" content="先确认实体名，后修改属性" placement="top">
                <el-button
                  type="primary"
                  plain
                  @click="mutex = !mutex"
                  :disabled="mutex"
                  >done!</el-button
                >
              </el-tooltip>
            </el-col>
          </el-form-item>
          <el-form-item
            class="demo-dynamic-column"
            v-for="(domain, index) in dynamicValidateForm.field"
            :label="'域'"
            :key="index"
            :prop="'field.' + index"
          >
            <el-col :span="4" >
              <el-input
                v-model="domain.type"
                placeholder="类型"
                :disabled="index == 0 || !mutex"
              />
            </el-col>
            <el-col :span="6">
              <el-input
                v-model="domain.name"
                placeholder="名称"
                :disabled="index == 0 || !mutex"
              />
            </el-col>
            <el-col :span="7">
              <el-cascader
                :options="options"
                :show-all-levels="false"
                clearable
                :disabled="index == 0 || !mutex"
                placeholder="请选择关联属性"
                @change="
                  (val) => {
                    handleChange(val, index);
                  }
                "
                v-model="cascadeSelect[index]"
              ></el-cascader>
            </el-col>
            <el-col :span="6">
              <el-cascader
                :options="selectOptions"
                :show-all-levels="false"
                clearable
                :disabled="index == 0 || !mutex"
                placeholder="请选择关联类型"
                @change="
                  (val) => {
                    handleOptionChange(val, index);
                  }
                "
                v-model="typeSelect[index]"
              ></el-cascader>
            </el-col>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitForm('dynamicValidateForm')"
              >提交</el-button
            >
            <el-tooltip class="item" effect="dark" content="对一对多一的一方生成管理多的一方的属性" placement="top">
              <el-button @click="addDomain">新增域</el-button>
            </el-tooltip>
            <el-button @click="cancle()">取消</el-button>
          </el-form-item>
        </el-form>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import _ from "lodash";
import * as api from '@/api/api.js'

export default {
  data() {
    return {
      customize: false,
      schemaName: "hello",
      beforeGo: true,
      newEntity: "",
      mutex: false,
      dynamicValidateForm: {},
      tableName: "",
      //暂时存放,便于页面显示
      basePojo: [],
      multipleSelection: [],
      options: [
        {
          value: "basePojo",
          label: "基础POJO",
          children: [],
        },
        {
          value: "cascadePojo",
          label: "级联POJO",
          children: [],
        },
      ],
      cascadeSelect:[],
      typeSelect:[],
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
          this.cascadeSelect = new Array(this.dynamicValidateForm.field.length)
          this.typeSelect = new Array(this.dynamicValidateForm.field.length)
        }
      });
    },
    goNext() {
      var tableData = this.$store.state.tableList
      console.log(tableData)
      for (var i = 0; i < this.basePojo.length; i++) {
        var p = _.cloneDeep(this.basePojo[i]);
        var list = p.field;
        // var f = [];
        // list.forEach((m) => {
        //   f.push(m.name + ";" + m.type);
        // });
        // p.field = f;
        var f = new Map();
        // list.forEach((m) => {
        //   f.push(m.name + ";" + m.type);
        // })
        list.forEach((m, index) => {
          if (index < tableData[i].length) {
            var key = tableData[i][index].COLUMN_NAME + ';'
            if (tableData[i][index].DATA_TYPE.match(/.*int|float|double|bigint|tinyint/i)) {
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
        if (_.findIndex(this.$store.state.cascadePojoList, o=>{return o.table === p.table}) === -1) {
          this.$store.commit("appendBasePojo", p);
        }
      }

      this.$store.commit("setBasePojo", this.basePojo);
      this.$store.commit("setOptions", this.options);
      this.handleSubmit()
      // this.$router.push("/mapper");
    },
    handleSubmit() {
      var basePojoList = this.$store.state.basePojoList;
      // if (this.$store.state.orm==='jpa') {
      //   
      // }
      basePojoList.forEach(o=>{
        o.field=JSON.stringify(_strMapToObj(o.field))
      })
      var cascadePojoList = this.$store.state.cascadePojoList;
      cascadePojoList.forEach(o=>{
        o.refer=JSON.stringify(_strMapToObj(o.refer))
        o.field=JSON.stringify(_strMapToObj(o.field))
        o.type=JSON.stringify(_strMapToObj(o.type))
      })
      api.sendJpaLists({
        basePojoList:JSON.stringify(basePojoList),
        baseMapperList:JSON.stringify(basePojoList),
        cascadePojoList:JSON.stringify(cascadePojoList),
        cascadeMapperList:JSON.stringify(cascadePojoList)
      }).then((res) => {
        console.log(res);
      });
     
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },

    //创建通用pojo
    generatePOJO() {
      if (this.basePojo.length !== 0) {
        this.beforeGo = false;
        return;
      }
      this.beforeGo = false;
      this.multipleSelection.forEach((p) => {
        var data = {};
        data.entity = p.name;
        data.table = p.name;
        data.packageName = this.$store.state.package;
        var option = {};
        option.label = data.entity;
        option.value = data.entity;
        option.children = [];
        var list = [];
        var idRealName = ''
        p.cols.forEach((col, i) => {
          var child = {};
          var finalStr = {};
          if (!col.DATA_TYPE.match(/.*int|float|double|bigint|tinyint/i)) {
            finalStr.name = col.COLUMN_NAME;
            finalStr.type = "String";
          } else if (col.COLUMN_NAME.match(/.*id/i) && i === 0) {
            var str = col.DATA_TYPE;
            if (str.match(/bigint/i)) {
              finalStr.name = 'id';
              finalStr.type = "Long";
              idRealName = col.COLUMN_NAME
            } else {
              finalStr.name = 'id';
              finalStr.type = "Integer";
              idRealName = col.COLUMN_NAME
            }
          } else {
            var type = col.DATA_TYPE;
            if (type.match(/bigint/i)) {
              finalStr.name = col.COLUMN_NAME;
              finalStr.type = "Long";
            } else {
              finalStr.name = col.COLUMN_NAME;
              finalStr.type = "Integer";
            }
          }
          child.label = finalStr.name;
          if (idRealName === '') {
            child.value = finalStr.name;
          } else {
            child.value = idRealName;
          }
          option.children.push(child);
          list.push(finalStr);
          idRealName = ''
        });
        data.field = list;
        this.options[0].children.push(option);
        this.basePojo.push(data);
      });
    },

    handleChange(n, index) {
      
      this.cascadeSelect[index] = n
      // if (this.dynamicValidateForm.refer === undefined) {
      //   this.dynamicValidateForm.refer = new Map();
      // }
      // if (n.length === 0) {
      //   this.dynamicValidateForm.refer.delete(
      //     this.newEntity + "." + this.dynamicValidateForm.field[index].name
      //   );
      //   if (this.dynamicValidateForm.refer.size === 0) {
      //     this.dynamicValidateForm.refer = undefined;
      //   }
      //   return;
      // }
      // if (this.$store.state.orm ==='mybatis') {
      //   this.dynamicValidateForm.refer.set(
      //     this.newEntity + "." + this.dynamicValidateForm.field[index].name,
      //     n[1] + "." + n[2]
      //   );
      // } else {
      //   // var tableInfo = this.$store.state.tableList[_.findIndex(this.$store.state.tableList, o=>{return this.tableName===o[0].TABLE_NAME})]
      //   // var thatTableName = this.basePojo[_.findIndex(this.basePojo, o=>{return o.table === n[1]})].table
      //   // console.log(thatTableName)
      //   // console.log(n)
      //   this.dynamicValidateForm.refer.set(
      //      this.newEntity + "." + this.dynamicValidateForm.field[index].name,
      //       n[1] + "." + n[2]
      //   )
      // }
      
    },
    handleOptionChange(n, index) {
      this.typeSelect[index] = n
      // if (this.dynamicValidateForm.type === undefined) {
      //   this.dynamicValidateForm.type = new Map();
      // }
      // if (n.length === 0) {
      //   this.dynamicValidateForm.type.delete(
      //     this.newEntity + "." + this.dynamicValidateForm.field[index].name
      //   );
      //   if (this.dynamicValidateForm.type.size === 0) {
      //     this.dynamicValidateForm.type = undefined;
      //   }
      //   return;
      // }
      // this.dynamicValidateForm.type.set(
      //   this.newEntity + "." + this.dynamicValidateForm.field[index].name,
      //   n[0]
      // );
    },

    //对通用pojo进行客制化
    submitForm() {
      if (this.newEntity === "") {
        this.$message("实体名不可为空");
        return false;
      }
      //更新basePojo的实体名
      this.basePojo[
        _.findIndex(this.basePojo, (o) => {
          return o.table === this.dynamicValidateForm.table;
        })
      ].entity = this.newEntity;
      this.basePojo[
        _.findIndex(this.basePojo, (o) => {
          return o.table === this.dynamicValidateForm.table;
        })
      ].field = this.dynamicValidateForm.field;
      //更新refer和type部分
      var isNull = _.findIndex(this.cascadeSelect, o=>{return o!==undefined})
      if (isNull !== -1) {
        this.dynamicValidateForm.refer = new Map()
        this.dynamicValidateForm.type = new Map()
        this.cascadeSelect.forEach((o,index)=>{
          if (o!==undefined) {
            this.dynamicValidateForm.refer.set(
              this.newEntity + "." + this.dynamicValidateForm.field[index].name,
              this.cascadeSelect[index][1]+'.'+this.cascadeSelect[index][2]
            )
            this.dynamicValidateForm.type.set(
              this.newEntity + "." + this.dynamicValidateForm.field[index].name,
              this.typeSelect[index][0]
            )
          }
        })
      }
      //更新多选器中的实体名
      var index = _.findIndex(this.options[0].children, (o) => {
        return o.label === this.dynamicValidateForm.entity;
      });
      this.options[0].children[index].label = this.newEntity;
      this.options[0].children[index].value = this.newEntity;
      //更新实体名
      this.dynamicValidateForm.entity = this.newEntity;

      var list = this.dynamicValidateForm.field;
      var field = new Map();
      var tableData = this.$store.state.tableList
      var tableIndex = _.findIndex(tableData, (o) => {
        return this.tableName === o[0].TABLE_NAME;
      });
      list.forEach((m, index) => {
        if (index < tableData[tableIndex].length) {
          var key = tableData[tableIndex][index].COLUMN_NAME + ';'
          if (tableData[tableIndex][index].DATA_TYPE.match(/.*int|float|double|bigint|tinyint/i)) {
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
      data.field = field

      if (this.dynamicValidateForm.refer === undefined) {
        //基础pojo
        this.$store.commit("appendBasePojo", data);
      } else {
        //级联pojo
        this.$store.commit("appendCascadePojo", data);
      }
      this.customize = false;
      this.tableName = "";
      this.mutex = false;
      this.cascadeSelect = []
      this.typeSelect = []
    },
    cancle() {
      this.customize = false;
      this.tableName = "";
      this.mutex = false;
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
    console.log("create");
    // // var list = this.$store.state.tableList;
    // // console.log(this.$store.state.tableList)
    // this.schemaName = this.$store.getters.schema;
    // this.tableData =
    // console.log(this.$store.getters.tableList)
    // list.forEach((o) => {
    // 	//每一张表
    // 	var data = {};
    // 	data.name = o[0].TABLE_NAME;
    // 	data.cols = o;
    // 	this.tableData.push(data);
    // });
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
h3 {
	color: gray
}
.demo-table-expand {
  margin-right: 0;
  margin-bottom: 0;
  width: 80%;
}
.demo-table-expand label {
  width: 90px;
  color: #99a9bf;
}
.demo-dynamic {
  width: 800px;
}
.demo-dynamic-column {
  width: 800px;
}
</style>