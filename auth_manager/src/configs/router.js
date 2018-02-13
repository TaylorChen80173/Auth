import Vue from 'vue'
import VueRouter from 'vue-router'
import { menu } from './constants'

Vue.use(VueRouter)

function load (component) {
  // '@' is aliased to src/components
  return () => System.import(`@/${component}.vue`)
}

function menuProcess (menu) {
  var routes = []
  routes.push({ path: '/', name: 'Home', component: load('views/login') })
  menu.forEach(mm1 => {
    if (mm1.menu) {
      mm1.menu.forEach(mm2 => {
        if (mm2.menu) {
          mm2.menu.forEach(mm3 => {
            routes.push({path: mm3.route, name: mm3.alias, component: load(mm3.path)})
          })
        }
        else {
          routes.push({path: mm2.route, name: mm2.alias, component: load(mm2.path)})
        }
      })
    }
  })
  // Always leave this last one
  routes.push({ path: '/Login', name: 'Login', component: load('views/login') })
  routes.push({ path: '*', component: load('Error404') }) // Not found
  return routes
}

export default new VueRouter({
  /*
   * NOTE! VueRouter "history" mode DOESN'T works for Cordova builds,
   * it is only to be used only for websites.
   *
   * If you decide to go with "history" mode, please also open /config/index.js
   * and set "build.publicPath" to something other than an empty string.
   * Example: '/' instead of current ''
   *
   * If switching back to default "hash" mode, don't forget to set the
   * build publicPath back to '' so Cordova builds work again.
   */

  routes: menuProcess(menu)
  // routes: [
  //   { path: '/', component: load('views/login') },
  //
  //   // Always leave this last one
  //   { path: '*', component: load('Error404') } // Not found
  // ]
})
