<template>
  <el-container>
    <el-aside width="400px">
			<h1>{{schemaName}}</h1>
      <el-table :data="tableData" border style="width: 100%" @selection-change="handleSelectionChange">
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-form label-position="left" inline class="demo-table-expand">
              <el-form-item v-for="(col,index) in props.row.cols"
							:key="index" :label="col.COLUMN_TYPE">
                <span>{{ col.COLUMN_NAME }}</span>
								<span v-show="col.COLUMN_KEY==='PRI'" style="margin-left:30px;">{{ col.COLUMN_KEY }}</span>
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column label="表名" prop="name"> </el-table-column>
				<el-table-column
					label="操作"
					width="100">
					<template slot-scope="scope">
						<el-button @click.prevent="handleClick(scope.row)" type="text" size="small"
						:disabled="beforeGo">客制化</el-button>
					</template>
				</el-table-column>
				<el-table-column
					type="selection"
					width="55">
				</el-table-column>
      </el-table>
			<el-button type="primary" plain @click="generatePOJO()" style="margin-top:20px;">GO!</el-button>
			<el-button type="primary" plain @click="goNext" style="margin-top:20px;" :disabled="beforeGo">下一步!</el-button>
    </el-aside>
		<el-container>
			<el-header>{{tableName}}</el-header>
			<el-main>
				<el-form v-show="customize" :model="dynamicValidateForm" ref="dynamicValidateForm" label-width="150px" class="demo-dynamic">
					<el-form-item
						:label="'实体名'">
						<el-col :span="9">
							<el-input v-model="newEntity" :disabled="mutex"></el-input>
						</el-col>
						<el-col :span="6">
							<el-button type="primary" plain @click="mutex=!mutex" :disabled="mutex">done!</el-button>
						</el-col>
					</el-form-item>
					<el-form-item
						class="demo-dynamic-column"
						v-for="(domain, index) in dynamicValidateForm.field"
						:label="'域'"
						:key="index"
						:prop="'field.' + index"
					>
						<el-col :span="6" style="margin-right:10px">
							<el-input v-model="domain.type" placeholder="类型" 
							:disabled="index==0 || !mutex"/>
						</el-col>
						<el-col :span="10">
							<el-input v-model="domain.name" placeholder="名称" 
							:disabled="index==0 || !mutex"/>
						</el-col>
						<el-col :span="7">
							<el-cascader
							:options="options" 
							:show-all-levels="false" clearable :disabled="index==0 || !mutex"
							@change="val=>{handleChange(val, index)}"></el-cascader>
						</el-col>
					</el-form-item>
					<el-form-item>
						<el-button type="primary" @click="submitForm('dynamicValidateForm')">提交</el-button>
						<el-button @click="addDomain">新增域</el-button>
						<el-button @click="cancle()">取消</el-button>
					</el-form-item>
				</el-form>
			</el-main>
		</el-container>
    
  </el-container>
</template>

<script>
import * as api from '@/api/api.js'
import _ from 'lodash'

export default {
  data() {
    return {
			customize:false,
			schemaName:'hello',
			beforeGo:true,
			newEntity:'',
			mutex:false,
			dynamicValidateForm:{
			},
			tableName:'',
      tableData: [
				// {
        //   id: '12987122',
        //   name: '好滋好味鸡蛋仔',
        //   category: '江浙小吃、小吃零食',
        //   desc: '荷兰优质淡奶，奶香浓而不腻',
        //   address: '上海市普陀区真北路',
        //   shop: '王小虎夫妻店',
        //   shopId: '10333'
        // }, {
        //   id: '12987123',
        //   name: '好滋好味鸡蛋仔',
        //   category: '江浙小吃、小吃零食',
        //   desc: '荷兰优质淡奶，奶香浓而不腻',
        //   address: '上海市普陀区真北路',
        //   shop: '王小虎夫妻店',
        //   shopId: '10333'
        // }, {
        //   id: '12987125',
        //   name: '好滋好味鸡蛋仔',
        //   category: '江浙小吃、小吃零食',
        //   desc: '荷兰优质淡奶，奶香浓而不腻',
        //   address: '上海市普陀区真北路',
        //   shop: '王小虎夫妻店',
        //   shopId: '10333'
        // }, {
        //   id: '12987126',
        //   name: '好滋好味鸡蛋仔',
        //   category: '江浙小吃、小吃零食',
        //   desc: '荷兰优质淡奶，奶香浓而不腻',
        //   address: '上海市普陀区真北路',
        //   shop: '王小虎夫妻店',
        //   shopId: '10333'
				// }
				],
			//暂时存放,便于页面显示
			basePojo:[],
			multipleSelection:[],
			options:[{
				value:'basePojo',
				label:'基础POJO',
				children: []
			},{
				value:'cascadePojo',
				label:'级联POJO',
				children: []
			},]
    };
  },methods: {
		//处理客制化点击
		handleClick(n) {
			this.customize = true
			this.tableName = n.name
			this.basePojo.forEach(p=>{
				if (p.table.match(n.name)) {
					this.dynamicValidateForm = _.cloneDeep(p)
					this.newEntity = _.cloneDeep(p.entity)
				}
			})
		},
		goNext() {
			this.basePojo.forEach(p=>{
				// console.log(p)
				var list = p.field
				var field = []
				list.forEach(m=>{
					field.push(m.name+';'+m.type)
				})
				p.field = field
				this.$store.commit('appendBasePojo', p)
			})
		},
		handleSelectionChange(val) {
			this.multipleSelection = val;
		},

		//创建通用pojo
		generatePOJO() {
			if (this.basePojo.length !== 0) {
				this.beforeGo = false
				return
			}
			this.beforeGo=false
			this.multipleSelection.forEach(p=>{
				var data = {}
				data.entity=p.name
				data.table=p.name
				data.packageName=this.$store.state.package
				var option = {}
				option.label=data.entity
				option.value = data.entity
				option.children = []
				var list = []
				p.cols.forEach((col, i)=>{
					var child = {}
					var finalStr = {}
					if (!col.DATA_TYPE.match(/.*int|float|double|bigint|tinyint/i)) {
						finalStr.name = col.COLUMN_NAME
						finalStr.type = 'String'
					} else if(col.COLUMN_NAME.match(/.*id/i) && i === 0){
						var str = col.DATA_TYPE
						if (str.match(/bigint/i)){
							finalStr.name = 'id'
							finalStr.type = 'Long'
						} else {
							finalStr.name='id'
							finalStr.type='Integer'
						}
					} else {
						var type = col.DATA_TYPE
						if (type.match(/bigint/i)){
							finalStr.name=col.COLUMN_NAME
							finalStr.type='Long'
						} else {
							finalStr.name=col.COLUMN_NAME
							finalStr.type='Integer'
						}
					}
					child.label=finalStr.name
					child.value=finalStr.name
					option.children.push(child)
					list.push(finalStr)
				})
				data.field=list
				this.options[0].children.push(option)
				this.basePojo.push(data)
			})
		},
		
		handleChange(n,index) {
			if (this.dynamicValidateForm.refer === undefined) {
				this.dynamicValidateForm.refer = new Map()
			}
			if (n.length === 0) {
				this.dynamicValidateForm.refer.delete(
					this.newEntity+'.'+this.dynamicValidateForm.field[index].name
				)
				if (this.dynamicValidateForm.refer.size === 0) {
					this.dynamicValidateForm.refer = undefined
				}
				return
			}
			this.dynamicValidateForm.refer.set(
				this.newEntity+'.'+this.dynamicValidateForm.field[index].name, n[1]+'.'+n[2])
		},

		//对通用pojo进行客制化
		submitForm() {
			if (this.newEntity==='') {
				this.$message('实体名不可为空')
				return false
			}
			//更新basePojo的实体名
			this.basePojo[_.findIndex(this.basePojo, o=>{return o.table===this.dynamicValidateForm.table})].entity=this.newEntity
			//更新多选器中的实体名
			var index = _.findIndex(this.options[0].children, o=>{return o.label===this.dynamicValidateForm.entity})
			this.options[0].children[index].label=this.newEntity
			this.options[0].children[index].value=this.newEntity
			//更新实体名
			this.dynamicValidateForm.entity=this.newEntity
			var list = this.dynamicValidateForm.field
			var field = []
			list.forEach(m=>{
				field.push(m.name+';'+m.type)
			})
			var data = _.cloneDeep(this.dynamicValidateForm)
			data.field = field
			if (this.dynamicValidateForm.refer === undefined) { //基础pojo
				this.$store.commit('appendBasePojo', data)
			} else { //级联pojo
				this.$store.commit('appendCascadePojo', data)				
			}
			this.customize = false
			this.tableName = ''
			this.mutex=false
		},
      cancle() {
				this.customize=false;
				this.tableName = ''
				this.mutex = false
      },
      removeDomain(item) {
        var index = this.dynamicValidateForm.domains.indexOf(item)
        if (index !== -1) {
          this.dynamicValidateForm.domains.splice(index, 1)
        }
      },
      addDomain() {
        this.dynamicValidateForm.field.push({
          name: '',
          type: '',
        });
      }
	},created(){
			api.getConnection({
				conn_url:'jdbc:mysql://localhost:3306/neet_word?serverTimezone=UTC',
				conn_username:'root',
				conn_passwd:'admin',
				conn_type:'mysql8'
			}).then(res => {
				var list = res.data.list
				this.schemaName='neet_word'
				list.forEach((o)=>{ //每一张表
					var data = {}
					data.name = o[0].TABLE_NAME
					data.cols = o
					this.tableData.push(data)
				})
				this.$store.commit('setPackage', 'com.lunacia.orm')
			})
	}
};
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
.demo-dynamic {
	width: 800px;
}
.demo-dynamic-column {
	width: 500px;
}
</style>