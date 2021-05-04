import Vuex from 'vuex'
import Vue from 'vue'
import _ from 'lodash'

Vue.use(Vuex)

const store = new Vuex.Store({
  state: {
    basePojoList:[],
    cascadePojoList:[],
    baseMapperList:[],
    cascadeMapperList:[],
    tableList: [],
    package:''
  },
  mutations: {
    setPackage(state, n){
      state.package = n
    },
    appendBasePojo(state, n){
      // state.basePojoList.push(n)
      var index = _.findIndex(state.basePojoList, o=>{return o.table===n.table})
      var thatIndex = _.findIndex(state.cascadePojoList, o=>{return o.table===n.table})
      if (thatIndex === -1) {
        if ( index === -1) {
          state.basePojoList.push(n)
        } else {
          state.basePojoList[index] = n
        }
      }
    },
    appendCascadePojo(state, n){
      state.cascadePojoList.push(n)
    },
    appendBaseMapper(state, n){
      var index = _.findIndex(state.baseMapperList, o=>{return o.table===n.table})
      var thatIndex = _.findIndex(state.cascadeMapperList, o=>{return o.table===n.table})
      if (thatIndex === -1) {
        if ( index === -1) {
          state.baseMapperList.push(n)
        } else {
          state.baseMapperList[index] = n
        }
      }
    },
    appendCascadeMapper(state, n){
      state.cascadeMapperList.push(n)
    },
    setTableList(state, n) {
      state.tableList = n
    }
  }
})

export default store