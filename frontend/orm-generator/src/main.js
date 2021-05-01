import Vue from 'vue';
import store from './store.js'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import App from './App.vue';
import * as echarts from 'echarts'

Vue.prototype.$echarts = echarts
Vue.config.productionTip = false
Vue.use(ElementUI)


new Vue({
  render: h => h(App),
  store,
}).$mount('#app')
