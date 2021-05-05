import VueRouter from 'vue-router'
import Vue from 'vue'
import Home from './components/Hello.vue'
import Mapper from  './components/MapperGenerator.vue'
import Pojo from './components/POJOGenerator.vue'

Vue.use(VueRouter)


const routes = [
	{
		path:'/',
		name:'home',
		component: Home
	},
	{
		path:'/pojo',
		component:Pojo
	},
	{
		path:'/mapper',
		component:Mapper
	}
]

const router = new VueRouter({
	mode:'hash',
	routes:routes
})

export default router