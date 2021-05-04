import VueRouter from 'vue-router'
import Vue from 'vue'
Vue.use(VueRouter)

// const home = ()=>{import('./components/Hello.vue')}
const mapper = ()=>import('./components/MapperGenerator.vue')
const pojo = ()=>import('./components/POJOGenerator.vue')

const routes = [
	{
		path:'/',
		name:'home',
		component: ()=> import('./components/Hello.vue')
	},
	{
		path:'/pojo',
		component:pojo
	},
	{
		path:'/mapper',
		component:mapper
	}
]

const router = new VueRouter({
	mode:'hash',
	routes:routes
})

export default router