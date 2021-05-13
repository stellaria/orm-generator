import VueRouter from 'vue-router'
import Vue from 'vue'
import Home from './components/Hello.vue'
import Mapper from './components/MapperGenerator.vue'
import Pojo from './components/POJOGenerator.vue'
import JpaPojo from './components/JpaPojo.vue'
import JpaMapper from './components/JpaMapper.vue'

Vue.use(VueRouter)


const routes = [
	{
		path: '/',
		name: 'home',
		component: Home
	},
	{
		path: '/mybatis/pojo',
		component: Pojo
	},
	{
		path: '/mybatis/mapper',
		component: Mapper
	},
	{
		path: '/jpa/pojo',
		component: JpaPojo
	}, 
	{
		path: '/jpa/mapper',
		component: JpaMapper
	}
]

const router = new VueRouter({
	mode: 'hash',
	routes: routes
})

export default router